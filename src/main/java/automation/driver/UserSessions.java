package automation.driver;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.NoSuchWindowException;

/** Un propietario a la vez por navegador, incluso si Cucumber cambia de hilo. */
public final class UserSessions {
    private static final ConcurrentHashMap<String, Session> SESSIONS = new ConcurrentHashMap<>();
    private static final ThreadLocal<Session> CURRENT = new ThreadLocal<>();
    private static final class Session {
        final ReentrantLock lock = new ReentrantLock(true);
        WebDriver driver;
        volatile String operation = "inicio/autenticacion";
    }
    private UserSessions() {}

    public static boolean enabled() {
        String mode = System.getProperty("execution.mode", "porUsuario");
        if (!mode.equals("aislado") && !mode.equals("porUsuario")) {
            throw new IllegalArgumentException("execution.mode debe ser aislado o porUsuario");
        }
        return mode.equals("porUsuario");
    }

    public static void acquire(String user) {
        if (CURRENT.get() != null) throw new IllegalStateException("Ya hay una sesion asignada al escenario");
        Session session = SESSIONS.computeIfAbsent(user.trim().toLowerCase(java.util.Locale.ROOT), key -> new Session());
        long requested = System.nanoTime();
        if (session.lock.isLocked()) {
            System.out.println("[COLA] " + user + ": esperando que termine su escenario anterior");
        }
        int waitSeconds = automation.utils.PaymentStateWait.positive("session.acquire.timeout.seconds", 1800);
        long deadline = requested + java.time.Duration.ofSeconds(waitSeconds).toNanos();
        try {
            while (!session.lock.tryLock(Math.min(java.time.Duration.ofSeconds(15).toNanos(),
                    Math.max(1, deadline - System.nanoTime())), java.util.concurrent.TimeUnit.NANOSECONDS)) {
                if (System.nanoTime() >= deadline) throw new IllegalStateException(
                        "Se agoto la espera de turno de " + user + ". Su escenario anterior sigue en: " + session.operation);
                System.out.println("[COLA] " + user + " | " + java.time.Duration.ofNanos(System.nanoTime() - requested).toSeconds()
                        + " s | escenario anterior: " + session.operation);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Espera de sesion interrumpida: " + user, e);
        }
        System.out.println("[SESION] " + user + ": navegador asignado tras "
                + java.time.Duration.ofNanos(System.nanoTime() - requested).toSeconds() + " s | " + Thread.currentThread().getName());
        CURRENT.set(session);
        session.operation = "inicio/autenticacion";
        try {
            if (session.driver != null) {
                try {
                    if (session.driver.getWindowHandles().isEmpty()) discard(session);
                } catch (NoSuchSessionException | NoSuchWindowException disconnected) {
                    System.out.println("[RECUPERACION] El navegador de " + user + " ya no existe; se crea otro para esta nueva fila");
                    discard(session);
                }
            }
            if (session.driver == null) {
                DriverManager.start();
                session.driver = DriverManager.getDriver();
            } else {
                DriverManager.bind(session.driver);
                System.out.println("[SESION] Se reutiliza el navegador autenticado de " + user);
            }
        } catch (RuntimeException | Error e) {
            release();
            throw e;
        }
    }

    private static void discard(Session session) {
        try { session.driver.quit(); } catch (RuntimeException ignored) { /* Conexion ya perdida. */ }
        session.driver = null;
    }

    public static void describeCurrent(String operation) {
        Session session = CURRENT.get();
        if (session != null) session.operation = operation;
    }

    public static void release() {
        Session session = CURRENT.get();
        DriverManager.unbind();
        CURRENT.remove();
        if (session != null) session.lock.unlock();
    }

    public static void closeAll() {
        RuntimeException failure = null;
        for (Session session : SESSIONS.values()) {
            try {
                if (session.driver != null) session.driver.quit();
            } catch (NoSuchSessionException | NoSuchWindowException alreadyClosed) {
                // Ya no hay navegador que cerrar.
            } catch (RuntimeException e) {
                if (failure == null) failure = e;
                else failure.addSuppressed(e);
            }
        }
        SESSIONS.clear();
        if (failure != null) throw failure;
    }
}
