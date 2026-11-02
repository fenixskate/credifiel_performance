
package automation.actions;

import automation.pages.LoginPage;
import config.ConfigManager;
import org.openqa.selenium.WebDriver;

public final class LoginActions {

    private final LoginPage loginPage;

    public LoginActions(WebDriver driver) {
        this.loginPage = new LoginPage(driver);
    }

    public void openLogin() {
        loginPage.open();
    }

    public void resumeOrLogin(String usuario) {
        if (loginPage.openSession()) iniciarSesion(usuario);
    }

    public void iniciarSesion(String usuario) {
        loginPage.signIn(
                usuario,
                ConfigManager.get("login.password")
        );
    }

    public void iniciarSesion() {
        loginPage.signIn(
                ConfigManager.get("login.user"),
                ConfigManager.get("login.password")
        );
    }
}
