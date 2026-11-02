package verification;

import automation.pages.MovimientosPage;
import automation.pages.PromesasPage;
import automation.driver.DriverManager;
import automation.driver.UserSessions;
import steps.LoginSteps;

/** Navegacion real sin subir archivos ni ejecutar importaciones/conciliaciones. */
public final class ExtrajudicialNavigationSmoke {
    public static void main(String[] args) throws Exception {
        System.setProperty("execution.mode", "porUsuario");
        var pool = java.util.concurrent.Executors.newFixedThreadPool(2);
        try {
            var tasks = new java.util.ArrayList<java.util.concurrent.Future<?>>();
            String selected = System.getProperty("verification.user");
            for (String user : selected == null ? new String[]{"polet.valdes@credifiel.com.mx", "fernanda.jimenez@credifiel.com.mx"} : new String[]{selected}) {
                tasks.add(pool.submit(() -> {
                    try {
                        new LoginSteps().iniciarSesionConUsuario(user);
                        var page = new MovimientosPage(DriverManager.getDriver());
                        page.ingresarCuentas();
                        page.seleccionarCuenta("Extrajudicial | Azteca 8646");
                        page.ingresarExportarMovimientos();
                        page.ingresarImportarMovimientos();
                        page.ingresarModuloCuentas();
                        page.seleccionarGeneralOperaciones();
                        new automation.actions.LoginActions(DriverManager.getDriver()).resumeOrLogin(user);
                        new PromesasPage(DriverManager.getDriver()).ingresarModulo();
                        System.out.println("PASS NAVEGACION: " + user);
                    } catch (RuntimeException | Error failure) {
                        try {
                            java.nio.file.Files.writeString(java.nio.file.Path.of("target", "navigation-" + user.split("@")[0] + ".html"), DriverManager.getDriver().getPageSource());
                            java.nio.file.Files.write(java.nio.file.Path.of("target", "navigation-" + user.split("@")[0] + ".png"),
                                    ((org.openqa.selenium.TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(org.openqa.selenium.OutputType.BYTES));
                        } catch (Exception ignored) { }
                        throw failure;
                    } finally { UserSessions.release(); }
                }));
            }
            for (var task : tasks) task.get();
        } finally {
            pool.shutdownNow();
            UserSessions.closeAll();
        }
    }
}
