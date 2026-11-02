package verification;

import automation.driver.DriverManager;
import automation.driver.UserSessions;
import automation.utils.PreparacionPromesas;
import steps.LoginSteps;

/** Ejecuta únicamente el nuevo step de descarga y actualización; no carga promesas en Odoo. */
public final class PrepararPromesas {
    public static void main(String[] args) throws Exception {
        System.setProperty("execution.mode", "porUsuario");
        try {
            new LoginSteps().iniciarSesionConUsuario(System.getProperty("verification.user", "polet.valdes@credifiel.com.mx"));
            System.out.println("PREPARACION COMPLETA: " + PreparacionPromesas.actualizarUnaVez(DriverManager.getDriver()));
        } finally {
            if (DriverManager.isStarted()) UserSessions.release();
            UserSessions.closeAll();
        }
    }
}
