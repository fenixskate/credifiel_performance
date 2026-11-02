package automation.utils;

import java.time.Duration;
import org.openqa.selenium.WebDriver;

/** Polling corto, manteniendo el refresco periodico para trabajos de Odoo. */
public final class AsyncRefresh {
    private final WebDriver driver;
    private long lastRefresh = System.nanoTime();
    public AsyncRefresh(WebDriver driver) { this.driver = driver; }
    public void poll() {
        try { Thread.sleep(500); }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Espera interrumpida", e);
        }
        if (System.nanoTime() - lastRefresh >= Duration.ofSeconds(5).toNanos()) {
            driver.navigate().refresh();
            lastRefresh = System.nanoTime();
            System.out.println("[REFRESH] Reconsultando resultado | " + Thread.currentThread().getName());
        }
    }
}
