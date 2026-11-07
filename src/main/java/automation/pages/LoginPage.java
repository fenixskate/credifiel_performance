
package automation.pages;

import config.ConfigManager;
import automation.selectors.LoginSelectors;
import automation.utils.WaitUtils;
import org.openqa.selenium.WebDriver;

public final class LoginPage {

    private final WebDriver driver;
    private final WaitUtils wait;
    private final org.openqa.selenium.support.ui.WebDriverWait esperaInicio;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WaitUtils(driver);
        int segundos = Integer.getInteger("login.timeout.seconds",
                Integer.getInteger("navigation.timeout.seconds", 120));
        if (segundos <= 0) throw new IllegalArgumentException("login.timeout.seconds debe ser mayor que cero");
        this.esperaInicio = new org.openqa.selenium.support.ui.WebDriverWait(driver,
                java.time.Duration.ofSeconds(segundos));
        this.esperaInicio.ignoring(org.openqa.selenium.StaleElementReferenceException.class);
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
        try {
            esperaInicio.until(d -> d.findElements(LoginSelectors.USERNAME).stream().anyMatch(org.openqa.selenium.WebElement::isDisplayed)
                        || d.findElements(LoginSelectors.MENU_INICIO).stream().anyMatch(org.openqa.selenium.WebElement::isDisplayed));
        } catch (org.openqa.selenium.TimeoutException failure) {
            guardarErrorMenu(failure);
            throw failure;
        }
        return driver.findElements(LoginSelectors.USERNAME).stream().anyMatch(org.openqa.selenium.WebElement::isDisplayed);
    }

    public void signIn(String user, String secret) {
        wait.visible(LoginSelectors.USERNAME).clear();
        wait.visible(LoginSelectors.USERNAME).sendKeys(user);

        wait.visible(LoginSelectors.PASSWORD).clear();
        wait.visible(LoginSelectors.PASSWORD).sendKeys(secret);

        wait.clickable(LoginSelectors.SUBMIT).click();
        try {
            esperaInicio.until(d -> {
                        for (var error : d.findElements(LoginSelectors.ERROR_LOGIN)) {
                            if (error.isDisplayed() && !error.getText().isBlank())
                                throw new IllegalStateException("Odoo rechazo el acceso de " + user + ": " + error.getText().trim());
                        }
                        return d.findElements(LoginSelectors.MENU_INICIO).stream()
                                .anyMatch(org.openqa.selenium.WebElement::isDisplayed);
                    });
        } catch (RuntimeException failure) {
            guardarErrorMenu(failure);
            throw failure;
        }
    }

    private void guardarErrorMenu(RuntimeException failure) {
        try {
            java.nio.file.Path carpeta = java.nio.file.Path.of("target", "login-errors", java.util.UUID.randomUUID().toString());
            java.nio.file.Files.createDirectories(carpeta);
            java.nio.file.Files.writeString(carpeta.resolve("pagina.html"), driver.getPageSource());
            java.nio.file.Files.writeString(carpeta.resolve("url.txt"), driver.getCurrentUrl());
            java.nio.file.Files.write(carpeta.resolve("pantalla.png"),
                    ((org.openqa.selenium.TakesScreenshot) driver).getScreenshotAs(org.openqa.selenium.OutputType.BYTES));
            System.out.println("[LOGIN] No se reconocio el menu. Evidencia: " + carpeta.toAbsolutePath());
        } catch (Exception evidencia) { failure.addSuppressed(evidencia); }
    }

}
