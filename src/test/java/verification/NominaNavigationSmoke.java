package verification;

import automation.driver.DriverManager;
import automation.driver.UserSessions;
import automation.pages.CuentasPage;
import automation.actions.LoginActions;
import steps.LoginSteps;

/** Verifica solo la transición de sesión a Cuentas; no carga archivos ni registra pagos. */
public final class NominaNavigationSmoke {
    public static void main(String[] args) throws Exception {
        System.setProperty("execution.mode", "porUsuario");
        var pool = java.util.concurrent.Executors.newFixedThreadPool(2);
        try {
            var tasks = new java.util.ArrayList<java.util.concurrent.Future<?>>();
            for (String user : new String[]{"polet.valdes@credifiel.com.mx", "fernanda.jimenez@credifiel.com.mx"}) {
                tasks.add(pool.submit(() -> {
                    try {
                        new LoginSteps().iniciarSesionConUsuario(user);
                        CuentasPage cuentas = new CuentasPage(DriverManager.getDriver());
                        for (int intento = 1; intento <= 3; intento++) {
                            new LoginActions(DriverManager.getDriver()).resumeOrLogin(user);
                            cuentas.ingresarModuloCuentas();
                            System.out.println("PASS CUENTAS: " + user + " | transición=" + intento);
                        }
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
