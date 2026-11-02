package automation.utils;

import java.time.Duration;
import java.util.function.LongSupplier;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import automation.selectors.LoginSelectors;
import automation.selectors.PagosSelectors;

/** Consulta el DOM frecuentemente; recarga solo si el estado no se actualiza. */
public final class PaymentStateWait {
    private final WebDriver driver;
    public PaymentStateWait(WebDriver driver) { this.driver = driver; }

    public WebElement until(By target, String label, Duration timeout) {
        long started = System.nanoTime();
        long[] lastRefresh = {started};
        long[] lastLog = {started};
        String[] lastState = {"sin estado visible"};
        LongSupplier elapsed = () -> Duration.ofNanos(System.nanoTime() - started).toSeconds();
        Duration refreshInterval = Duration.ofSeconds(positive("payments.refresh.seconds", 15));
        Duration pageLoadTimeout = driver.manage().timeouts().getPageLoadTimeout();
        try {
            return new WebDriverWait(driver, timeout)
                    .pollingEvery(Duration.ofMillis(500))
                    .ignoring(StaleElementReferenceException.class)
                    .withMessage(() -> label + ": no se alcanzo en " + elapsed.getAsLong()
                            + " s. Ultimo estado: " + lastState[0])
                    .until(d -> {
                        for (WebElement error : d.findElements(PagosSelectors.ERROR_ODOO)) {
                            if (error.isDisplayed() && !error.getText().isBlank())
                                throw new IllegalStateException("Odoo: " + error.getText().trim());
                        }
                        for (WebElement login : d.findElements(LoginSelectors.USERNAME)) {
                            if (login.isDisplayed()) throw new IllegalStateException("La sesion expiro esperando " + label);
                        }
                        for (WebElement element : d.findElements(target)) {
                            if (element.isDisplayed() && element.isEnabled()) {
                                log("[OK] " + label + " detectado en " + elapsed.getAsLong() + " s");
                                return element;
                            }
                        }
                        String states = d.findElements(By.cssSelector(".o_form_view .badge, .o_statusbar_status .o_arrow_button_current"))
                                .stream().filter(WebElement::isDisplayed).map(WebElement::getText)
                                .filter(text -> !text.isBlank()).distinct().collect(java.util.stream.Collectors.joining(", "));
                        if (!states.isBlank()) lastState[0] = states;
                        long now = System.nanoTime();
                        if (now - lastLog[0] >= Duration.ofSeconds(10).toNanos()) {
                            log("[ESPERANDO] " + label + " | " + elapsed.getAsLong() + " s | " + lastState[0]);
                            lastLog[0] = now;
                        }
                        // Dejar terminar la navegacion anterior y dar tiempo a las actualizaciones asincronas.
                        if (now - lastRefresh[0] >= refreshInterval.toNanos()
                                && "complete".equals(((JavascriptExecutor) d).executeScript("return document.readyState"))) {
                            long remaining = timeout.toNanos() - (now - started);
                            if (remaining > Duration.ofSeconds(1).toNanos()) {
                                try {
                                    d.manage().timeouts().pageLoadTimeout(Duration.ofNanos(Math.min(
                                            Duration.ofSeconds(5).toNanos(), remaining)));
                                    log("[REFRESCO] " + label + " | " + elapsed.getAsLong() + " s");
                                    d.navigate().refresh();
                                } catch (TimeoutException slowPage) {
                                    log("[ESPERANDO] Recarga lenta; se continuara observando el DOM: " + label);
                                } finally {
                                    d.manage().timeouts().pageLoadTimeout(pageLoadTimeout);
                                    lastRefresh[0] = System.nanoTime();
                                }
                            }
                        }
                        return null;
                    });
        } finally {
            log("[TIEMPO] Espera de " + label + ": " + elapsed.getAsLong() + " s");
        }
    }

    private static void log(String message) {
        System.out.println(message + " | " + Thread.currentThread().getName());
    }

    public static int positive(String key, int fallback) {
        int value = Integer.parseInt(System.getProperty(key, Integer.toString(fallback)));
        if (value < 1) throw new IllegalArgumentException(key + " debe ser mayor que cero");
        return value;
    }
}
