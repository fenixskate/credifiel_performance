package automation.utils;

import java.time.Duration;
import org.openqa.selenium.WebDriver;

/** Polling corto, manteniendo el refresco periodico para trabajos de Odoo. */
public final class AsyncRefresh {
    private final WebDriver driver;
    private final long refreshIntervalNanos;
    private long lastRefresh = System.nanoTime();
    public AsyncRefresh(WebDriver driver) { this(driver, Duration.ofSeconds(5)); }
    public AsyncRefresh(WebDriver driver, Duration interval) {
        if (interval.isZero() || interval.isNegative())
            throw new IllegalArgumentException("El intervalo de refresco debe ser positivo");
        this.driver = driver;
        this.refreshIntervalNanos = interval.toNanos();
    }
    public void poll() {
        try { Thread.sleep(500); }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Espera interrumpida", e);
        }
        if (System.nanoTime() - lastRefresh >= refreshIntervalNanos) {
            driver.navigate().refresh();
            lastRefresh = System.nanoTime();
            System.out.println("[REFRESH] Reconsultando resultado | " + Thread.currentThread().getName());
        }
    }
}
