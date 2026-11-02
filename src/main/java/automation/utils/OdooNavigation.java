package automation.utils;

import java.time.Duration;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Menus de escritorio y menus trasladados al desbordamiento (+). */
public final class OdooNavigation {
    private final WebDriver driver;
    private final ElementActions elements;
    public OdooNavigation(WebDriver driver) {
        this.driver = driver;
        this.elements = new ElementActions(driver);
    }
    private boolean visible(By selector) {
        return driver.findElements(selector).stream().anyMatch(WebElement::isDisplayed);
    }
    public void open(OdooMenu menu) {
        System.out.println("[MENU] Abriendo " + menu + " | " + Thread.currentThread().getName());
        open(menu.parent, menu.option, menu.destination);
    }
    public void open(By parent, By child, By destination) {
        long start = System.nanoTime();
        long[] lastRefresh = {start};
        long[] lastAction = {0};
        boolean[] clickedParent = {false};
        boolean[] clickedOverflow = {false};
        By overflow = By.cssSelector("header .o_menu_sections_more, nav button:has(.fa-plus), nav button:has(.fa-ellipsis-h), nav button:has(.oi-ellipsis-h)");
        new WebDriverWait(driver, Duration.ofSeconds(positive("navigation.timeout.seconds", 120)))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class)
                .ignoring(ElementNotInteractableException.class)
                .until(d -> {
                    if (visible(By.cssSelector(".o_loading_indicator, .o_blockUI, .blockUI.blockOverlay"))) return false;
                    WebElement option = clickable(child);
                    if (option != null) {
                        elements.click(option);
                        return true;
                    }
                    WebElement parentElement = clickable(parent);
                    if (parentElement != null && (!clickedParent[0]
                            || System.nanoTime() - lastAction[0] >= Duration.ofSeconds(positive("navigation.retry.seconds", 2)).toNanos())) {
                        elements.click(parentElement);
                        clickedParent[0] = true;
                        lastAction[0] = System.nanoTime();
                        lastRefresh[0] = lastAction[0];
                    } else if (!visible(parent) && clickable(overflow) != null && !clickedOverflow[0]) {
                        elements.click(clickable(overflow));
                        clickedOverflow[0] = true;
                        lastRefresh[0] = System.nanoTime();
                    }
                    if (System.nanoTime() - lastRefresh[0] >= Duration.ofSeconds(positive("navigation.refresh.seconds", 5)).toNanos()
                            && "complete".equals(((JavascriptExecutor) d).executeScript("return document.readyState"))) {
                        System.out.println("[MENU] Opcion pendiente; refrescando | " + Thread.currentThread().getName());
                        d.navigate().refresh();
                        lastRefresh[0] = System.nanoTime();
                        clickedParent[0] = false;
                        clickedOverflow[0] = false;
                    }
                    return false;
                });
        new WebDriverWait(driver, Duration.ofSeconds(positive("navigation.view.timeout.seconds", 30)))
                .ignoring(StaleElementReferenceException.class)
                .until(d -> !visible(By.cssSelector(".o_loading_indicator, .o_blockUI, .blockUI.blockOverlay")) && visible(destination));
        System.out.println("[MENU] Vista cargada en " + Duration.ofNanos(System.nanoTime() - start).toMillis() + " ms");
    }

    private WebElement clickable(By selector) {
        return driver.findElements(selector).stream().filter(WebElement::isDisplayed)
                .filter(WebElement::isEnabled).findFirst().orElse(null);
    }

    private static int positive(String property, int fallback) {
        int value = Integer.parseInt(System.getProperty(property, Integer.toString(fallback)));
        if (value <= 0) throw new IllegalArgumentException(property + " debe ser mayor que cero");
        return value;
    }
}
