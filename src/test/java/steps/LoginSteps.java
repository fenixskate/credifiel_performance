
package steps;

import automation.actions.LoginActions;
import automation.driver.DriverManager;
import automation.driver.UserSessions;
import config.ConfigManager;
import io.cucumber.java.es.Dado;

public class LoginSteps {

    private LoginActions actions() {
        return new LoginActions(DriverManager.getDriver());
    }

    @Dado("que el usuario está en la página de login")
    public void openLogin() {
        if (!UserSessions.enabled()) actions().openLogin();
    }

    @Dado("El usuario inicia sesion")
    public void iniciarSesion() {
        iniciarSesionConUsuario(ConfigManager.get("login.user"));
    }

    @Dado("El usuario inicia sesion con {string}")
    public void iniciarSesionConUsuario(String usuario) {
        if (UserSessions.enabled()) {
            UserSessions.acquire(usuario);
            actions().resumeOrLogin(usuario);
        } else {
            actions().iniciarSesion(usuario);
        }
    }

    @io.cucumber.java.es.Cuando("el usuario vuelve al inicio con su sesion de {string}")
    public void volverAlInicioConSesion(String usuario) {
        // El escenario ya posee el navegador: no adquirir de nuevo el bloqueo.
        actions().resumeOrLogin(usuario);
    }
}
