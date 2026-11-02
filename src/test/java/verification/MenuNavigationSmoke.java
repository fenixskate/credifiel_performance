package verification;

import automation.actions.LoginActions;
import automation.driver.DriverManager;
import automation.driver.UserSessions;
import automation.utils.OdooMenu;
import automation.utils.OdooNavigation;
import java.nio.file.*;
import org.openqa.selenium.*;
import steps.LoginSteps;

/** Solo abre listados y menús de CP01-CP06. No crea registros ni realiza operaciones. */
public final class MenuNavigationSmoke {
    public static void main(String[] args) throws Exception {
        System.setProperty("execution.mode", "porUsuario");
        var pool = java.util.concurrent.Executors.newFixedThreadPool(2);
        try {
            var tasks = new java.util.ArrayList<java.util.concurrent.Future<?>>();
            for (String user : new String[]{"polet.valdes@credifiel.com.mx", "fernanda.jimenez@credifiel.com.mx"}) {
                tasks.add(pool.submit(() -> {
                    OdooMenu current = null;
                    try {
                        new LoginSteps().iniciarSesionConUsuario(user);
                        var navigation = new OdooNavigation(DriverManager.getDriver());
                        for (OdooMenu menu : new OdooMenu[]{OdooMenu.PAGOS, OdooMenu.PROMESAS, OdooMenu.CUENTAS,
                                OdooMenu.LISTADO_CUENTAS, OdooMenu.EXPORTAR_MOVIMIENTOS, OdooMenu.IMPORTAR_MOVIMIENTOS,
                                OdooMenu.DOMICILIACION, OdooMenu.SOLICITUD_FACTURACION, OdooMenu.REEMBOLSOS}) {
                            current = menu;
                            if (menu == OdooMenu.PAGOS || menu == OdooMenu.PROMESAS)
                                new LoginActions(DriverManager.getDriver()).resumeOrLogin(user);
                            navigation.open(menu);
                            System.out.println("PASS MENU: " + user + " | " + menu);
                        }
                    } catch (RuntimeException | Error failure) {
                        try {
                            Path dir = Path.of("target", "menu-navigation");
                            Files.createDirectories(dir);
                            String name = user.split("@")[0] + "-" + current;
                            Files.writeString(dir.resolve(name + ".html"), DriverManager.getDriver().getPageSource());
                            Files.write(dir.resolve(name + ".png"), ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BYTES));
                        } catch (Exception evidenceError) { failure.addSuppressed(evidenceError); }
                        throw failure;
                    } finally { UserSessions.release(); }
                }));
            }
            Exception failure = null;
            for (var task : tasks) {
                try { task.get(); }
                catch (Exception e) { if (failure == null) failure = e; else failure.addSuppressed(e); }
            }
            if (failure != null) throw failure;
        } finally {
            pool.shutdownNow();
            UserSessions.closeAll();
        }
    }
}
