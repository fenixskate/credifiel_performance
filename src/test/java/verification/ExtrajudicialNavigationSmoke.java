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
                        String layout = System.getProperty("verification.layout.id");
                        if (layout != null) {
                            if (!layout.matches("[0-9]+")) throw new IllegalArgumentException("ID de layout invalido");
                            var driver = DriverManager.getDriver();
                            driver.get(java.net.URI.create(config.ConfigManager.get("base.url"))
                                    .resolve("/web#id=" + layout + "&model=vor.importacion.layout.bancario&view_type=form").toString());
                            new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(60))
                                    .until(d -> !d.findElements(org.openqa.selenium.By.cssSelector(".o_form_view .o_form_sheet")).isEmpty());
                            try {
                                java.nio.file.Files.writeString(java.nio.file.Path.of("target", "azteca-form-inspect.html"), driver.getPageSource());
                            } catch (java.io.IOException e) { throw new java.io.UncheckedIOException(e); }
                            for (var field : driver.findElements(org.openqa.selenium.By.cssSelector(".o_form_view [name]"))) {
                                String name = field.getDomAttribute("name");
                                if (name.contains("total") || name.contains("count") || name.equals("estatus"))
                                    System.out.println("CAMPO " + name + " = " + field.getText());
                            }
                            System.out.println("PASS: inspeccion del layout existente, sin modificarlo");
                            return;
                        }
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
