package verification;

import automation.driver.DriverManager;
import automation.pages.PagosPage;
import io.cucumber.java.es.Entonces;
import java.util.concurrent.ConcurrentHashMap;
import org.openqa.selenium.remote.RemoteWebDriver;

public final class SessionSuiteSteps {
    static final ConcurrentHashMap<String, String> IDS = new ConcurrentHashMap<>();
    @io.cucumber.java.Before("@verificacionSesion")
    public void requireLocalServer() {
        if (!"127.0.0.1".equals(java.net.URI.create(config.ConfigManager.get("base.url")).getHost()))
            throw new IllegalStateException("La suite de verificacion solo admite el servidor local");
    }
    @Entonces("la sesion local de {string} conserva su navegador")
    public void verify(String user) {
        var driver = DriverManager.getDriver();
        if (!driver.getCurrentUrl().startsWith("http://127.0.0.1:"))
            throw new IllegalStateException("Esta prueba solo admite el servidor local");
        String id = ((RemoteWebDriver) driver).getSessionId().toString();
        String previous = IDS.putIfAbsent(user, id);
        if (previous != null && !previous.equals(id)) throw new AssertionError("Se cambio el navegador de " + user);
        new PagosPage(driver).validarArchivoTerminado();
    }
}
