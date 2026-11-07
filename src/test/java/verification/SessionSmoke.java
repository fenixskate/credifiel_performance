package verification;

import automation.actions.LoginActions;
import automation.driver.DriverManager;
import automation.driver.UserSessions;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.remote.RemoteWebDriver;

/** Prueba explicita y local: no accede al sistema de pagos. */
public final class SessionSmoke {
    public static void main(String[] args) throws Exception {
        AtomicInteger logins = new AtomicInteger();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            String cookie = exchange.getRequestHeaders().getFirst("Cookie");
            boolean authenticated = cookie != null && cookie.contains("session=");
            // Como Odoo: /web/login muestra login incluso con cookie valida.
            if (exchange.getRequestURI().getPath().equals("/web/login")) authenticated = false;
            if (exchange.getRequestURI().getPath().equals("/signin")) {
                exchange.getResponseHeaders().add("Set-Cookie", "session=user" + logins.incrementAndGet() + "; Path=/; HttpOnly");
                authenticated = true;
            }
            String html = authenticated ? "<nav class='o_main_navbar'><div class='o_navbar_apps_menu'><button class='dropdown-toggle' title='Menú de Inicio'>Inicio</button></div></nav>"
                    : "<div id='wrapwrap'><main><div><form action='/signin'><div><input id='login'></div>"
                    + "<div><input id='password' type='password'></div><div><button>Entrar</button></div></form></div></main></div>";
            byte[] body = ("<!doctype html><meta charset='utf-8'>" + html).getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
            exchange.sendResponseHeaders(200, body.length);
            try (var output = exchange.getResponseBody()) { output.write(body); }
        });
        server.start();
        System.setProperty("base.url", "http://127.0.0.1:" + server.getAddress().getPort() + "/web/login");
        System.setProperty("headless", "true");
        System.setProperty("browser", "chrome");
        System.setProperty("login.password", "local-test-only");
        System.setProperty("execution.mode", "porUsuario");
        try {
            UserSessions.acquire("usuario-a");
            new LoginActions(DriverManager.getDriver()).resumeOrLogin("usuario-a");
            String firstId = id();
            Cookie firstCookie = DriverManager.getDriver().manage().getCookieNamed("session");
            UserSessions.release();
            UserSessions.acquire("usuario-b");
            new LoginActions(DriverManager.getDriver()).resumeOrLogin("usuario-b");
            check(!firstId.equals(id()), "Los usuarios deben tener navegadores distintos");
            check(!firstCookie.equals(DriverManager.getDriver().manage().getCookieNamed("session")), "Cookies independientes");
            UserSessions.release();
            var executor = java.util.concurrent.Executors.newSingleThreadExecutor();
            try {
                executor.submit(() -> {
                    UserSessions.acquire("usuario-a");
                    try {
                        new LoginActions(DriverManager.getDriver()).resumeOrLogin("usuario-a");
                        check(firstId.equals(id()), "Debe conservar el WebDriver al cambiar de hilo");
                        check(firstCookie.equals(DriverManager.getDriver().manage().getCookieNamed("session")), "Debe conservar la cookie");
                        check(logins.get() == 2, "No debe repetir el login");
                        DriverManager.getDriver().manage().deleteAllCookies();
                        new LoginActions(DriverManager.getDriver()).resumeOrLogin("usuario-a");
                        check(logins.get() == 3, "Debe autenticar de nuevo una sesion vencida");
                    } finally { UserSessions.release(); }
                }).get();
            } finally { executor.shutdownNow(); }
            UserSessions.acquire("usuario-a");
            String deadBrowser = id();
            DriverManager.getDriver().quit(); // Simula perdida del navegador entre filas.
            UserSessions.release();
            UserSessions.acquire("usuario-a");
            try {
                check(!deadBrowser.equals(id()), "No debe reutilizar una conexion de navegador muerta");
                new LoginActions(DriverManager.getDriver()).resumeOrLogin("usuario-a");
                check(logins.get() == 4, "El navegador recuperado necesita autenticar una vez");
            } finally { UserSessions.release(); }
            UserSessions.acquire("usuario-a");
            try {
                String ownerId = id();
                UserSessions.describeCurrent("paso local en curso");
                System.setProperty("session.acquire.timeout.seconds", "1");
                java.util.concurrent.CompletableFuture.runAsync(() -> {
                    try {
                        UserSessions.acquire("usuario-a");
                        try { throw new AssertionError("Dos escenarios adquirieron el mismo navegador"); }
                        finally { UserSessions.release(); }
                    } catch (IllegalStateException expected) {
                        check(expected.getMessage().contains("paso local en curso"), "El timeout debe identificar el paso que bloquea");
                    }
                }).get();
                check(ownerId.equals(id()), "El timeout de otra fila no debe quitar el navegador al propietario");
            } finally {
                System.clearProperty("session.acquire.timeout.seconds");
                UserSessions.release();
            }
            UserSessions.closeAll();
            System.setProperty("execution.mode", "aislado");
            check(!UserSessions.enabled(), "Seleccion de modo aislado");
            DriverManager.start();
            String isolated = id();
            DriverManager.quit();
            DriverManager.start();
            check(!isolated.equals(id()), "Modo aislado debe crear otro navegador");
            System.out.println("PASS: navegador y cookie persistentes entre hilos, usuarios separados, reautenticacion, recuperacion de navegador perdido, cola acotada y modo aislado.");
        } finally {
            DriverManager.quit();
            UserSessions.closeAll();
            server.stop(0);
        }
    }
    private static String id() { return ((RemoteWebDriver) DriverManager.getDriver()).getSessionId().toString(); }
    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
