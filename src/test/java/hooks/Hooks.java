package hooks;

import automation.driver.DriverManager;
import automation.driver.UserSessions;
import io.cucumber.java.AfterAll;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import automation.utils.ScreenshotUtils;

public class Hooks {
    @Before("@web")
    public void startBrowser(Scenario scenario) {
        scenario.log("Inicio: " + scenario.getName());
        scenario.log("Modo de navegador: " + (UserSessions.enabled() ? "porUsuario (conserva sesion entre filas)" : "aislado (cierra por fila)"));
        if (!UserSessions.enabled()) DriverManager.start();
    }

    @AfterStep("@web")
    public void attachScreenshot(Scenario scenario) {
        if (!DriverManager.isStarted()) return;
        if (scenario.isFailed()) {
            try {
                scenario.log("Consola del navegador: " + DriverManager.getDriver().manage().logs().get("browser").getAll());
            } catch (RuntimeException unavailable) {
                scenario.log("Consola no disponible: " + unavailable.getMessage());
            }
        }
        try {
            long started = System.nanoTime();
            System.out.println("[EVIDENCIA] Capturando paso | " + Thread.currentThread().getName());
            scenario.attach(ScreenshotUtils.capture(DriverManager.getDriver()),
                    "image/png", scenario.isFailed() ? "Paso fallido" : "Paso ejecutado");
            System.out.println("[EVIDENCIA] Captura adjunta en "
                    + java.time.Duration.ofNanos(System.nanoTime() - started).toMillis() + " ms | " + Thread.currentThread().getName());
        } catch (RuntimeException e) {
            scenario.log("No se pudo capturar evidencia: " + e.getMessage());
        }
    }

    @After("@web")
    public void closeBrowser(Scenario scenario) {
        boolean pausaCp05 = scenario.getSourceTagNames().contains("@CP05") && Boolean.getBoolean("debug.cp05.pause");
        boolean pausaCp06 = scenario.getSourceTagNames().contains("@CP06") && Boolean.getBoolean("debug.cp06.pause");
        if ((pausaCp05 || pausaCp06) && DriverManager.isStarted()) {
            String caso = pausaCp06 ? "cp06" : "cp05";
            String suffix = Thread.currentThread().getName();
            java.nio.file.Path marker = java.nio.file.Path.of("target", caso + "-" + suffix + ".pause");
            try {
                java.nio.file.Files.writeString(java.nio.file.Path.of("target", caso + "-final-" + suffix + ".html"),
                        DriverManager.getDriver().getPageSource());
                java.nio.file.Files.write(java.nio.file.Path.of("target", caso + "-final-" + suffix + ".png"),
                        ScreenshotUtils.capture(DriverManager.getDriver()));
                java.nio.file.Files.writeString(marker, scenario.getStatus().toString());
                System.out.println("[BREAKPOINT] " + caso.toUpperCase(java.util.Locale.ROOT) + ": " + scenario.getStatus()
                        + ". Navegador abierto. Elimina " + marker + " para continuar.");
                while (java.nio.file.Files.exists(marker)) Thread.sleep(500);
            } catch (java.io.IOException e) {
                throw new IllegalStateException("No se pudo guardar la evidencia de " + caso, e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Pausa de " + caso + " interrumpida", e);
            }
        }
        closeBrowser();
    }

    public void closeBrowser() {
        if (UserSessions.enabled()) UserSessions.release();
        else DriverManager.quit();
        System.out.println("[FLUJO] Escenario liberado; navegador disponible para otra fila | " + Thread.currentThread().getName());
    }

    @AfterAll
    public static void closeUserBrowsers() {
        UserSessions.closeAll();
    }
}
