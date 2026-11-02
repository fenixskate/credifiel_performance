
package automation.pages;

import config.ConfigManager;
import automation.selectors.LoginSelectors;
import automation.utils.WaitUtils;
import org.openqa.selenium.WebDriver;

public final class LoginPage {

    private final WebDriver driver;
    private final WaitUtils wait;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WaitUtils(driver);
    }

    public void open() {
        driver.get(ConfigManager.get("base.url"));
        wait.visible(LoginSelectors.USERNAME);
    }

    /** En Odoo /web/login muestra el formulario aun con sesion: volver a /web. */
    public boolean openSession() {
        String base = ConfigManager.get("base.url");
        java.net.URI uri = java.net.URI.create(base);
        String path = uri.getPath();
        String home = base;
        if (path != null && path.matches(".*/web/login/?")) {
            home = uri.resolve(path.replaceFirst("/login/?$", "")).toString();
        }
        try {
            driver.get(System.getProperty("session.start.url", home));
        } catch (org.openqa.selenium.TimeoutException slowResources) {
            // Una imagen/recurso pendiente no debe invalidar un login ya renderizado.
            System.out.println("[LOGIN] Carga lenta; comprobando formulario o sesion antes de fallar");
        }
        new org.openqa.selenium.support.ui.WebDriverWait(driver,
                java.time.Duration.ofSeconds(Math.max(30, ConfigManager.positiveInt("timeout.explicit.seconds"))))
                .until(d -> d.findElements(LoginSelectors.USERNAME).stream().anyMatch(org.openqa.selenium.WebElement::isDisplayed)
                        || d.findElements(LoginSelectors.MENU_INICIO).stream().anyMatch(org.openqa.selenium.WebElement::isDisplayed));
        return driver.findElements(LoginSelectors.USERNAME).stream().anyMatch(org.openqa.selenium.WebElement::isDisplayed);
    }

    public void signIn(String user, String secret) {
        wait.visible(LoginSelectors.USERNAME).clear();
        wait.visible(LoginSelectors.USERNAME).sendKeys(user);

        wait.visible(LoginSelectors.PASSWORD).clear();
        wait.visible(LoginSelectors.PASSWORD).sendKeys(secret);

        wait.clickable(LoginSelectors.SUBMIT).click();
        wait.visible(LoginSelectors.MENU_INICIO);
    }


}
