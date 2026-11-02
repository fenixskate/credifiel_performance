
package automation.utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

public final class ElementActions {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public ElementActions(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(
                driver, Duration.ofSeconds(30));
    }


    public void click(By selector) {

        WebDriverWait espera = new WebDriverWait(
                driver, Duration.ofSeconds(30));

        espera.pollingEvery(Duration.ofMillis(500));
        espera.ignoring(StaleElementReferenceException.class);

        espera.until(d -> {

            WebElement elemento = null;
            String estiloOriginal = null;

            try {
                elemento = d.findElements(selector).stream()
                        .filter(WebElement::isDisplayed).filter(WebElement::isEnabled)
                        .findFirst().orElse(null);

                if (elemento == null) return false;

                if (!elemento.isDisplayed() ||
                        !elemento.isEnabled()) {
                    return false;
                }

                scrollToElement(elemento);
                estiloOriginal = aplicarHighlight(elemento);

                elemento.click();

                System.out.println(
                        "[OK] Clic realizado: " + selector);

                return true;

            } catch (ElementClickInterceptedException |
                     NoSuchElementException e) {

                return false;

            } finally {
                if (elemento != null) {
                    quitarHighlight(elemento, estiloOriginal);
                }
            }
        });
    }
    // 2. Escribir texto
    public void click(WebElement element) {
        scrollToElement(element);
        String original = aplicarHighlight(element);
        try { element.click(); }
        finally { quitarHighlight(element, original); }
    }

    public void type(By selector, String texto) {

        WebDriverWait espera = new WebDriverWait(
                driver, Duration.ofSeconds(30));

        espera.pollingEvery(Duration.ofMillis(500));

        espera.until(d -> {

            try {
                // Obtener una referencia nueva del elemento
                WebElement elemento = d.findElement(selector);

                if (!elemento.isDisplayed() || !elemento.isEnabled()) {
                    return false;
                }

                scrollToElement(elemento);

                elemento.click();
                elemento.clear();
                elemento.sendKeys(texto);

                System.out.println(
                        "[OK] Texto ingresado: " + texto);

                return true;

            } catch (StaleElementReferenceException |
                     NoSuchElementException |
                     ElementClickInterceptedException e) {

                System.out.println(
                        "[ESPERANDO] Odoo actualizó el campo. Reintentando...");

                return false;
            }
        });
    }


    // 3. Seleccionar opción en un select HTML
    public void select(By selector, String texto) {

        WebElement elemento = wait.until(
                ExpectedConditions.elementToBeClickable(selector));

        new Select(elemento).selectByVisibleText(texto);
    }

    // 4. Marcar checkbox solo si es necesario
    public void check(By selector) {

        WebElement elemento = wait.until(
                ExpectedConditions.elementToBeClickable(selector));

        if (!elemento.isSelected()) {
            click(selector);
        }

        if (!wait.until(d ->
                d.findElement(selector).isSelected())) {

            throw new AssertionError(
                    "No se pudo marcar el checkbox: " + selector);
        }
    }

    // 5. Adjuntar archivo
    public void upload(By selector, Path archivo) {

        Path ruta = archivo.toAbsolutePath().normalize();

        if (!Files.isRegularFile(ruta)) {
            throw new IllegalStateException(
                    "No existe el archivo: " + ruta);
        }

        WebElement input = wait.until(
                ExpectedConditions.presenceOfElementLocated(selector));

        input.sendKeys(ruta.toString());
    }


    private void scrollToElement(WebElement elemento) {

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView(" +
                        "{block:'center', inline:'nearest'});",
                elemento);
    }

    private String aplicarHighlight(WebElement elemento) {

        JavascriptExecutor js = (JavascriptExecutor) driver;

        String estiloOriginal = elemento.getDomAttribute("style");

        js.executeScript(
                "arguments[0].style.setProperty(" +
                        "'outline', '3px solid #ff0000', 'important');" +
                        "arguments[0].style.setProperty(" +
                        "'outline-offset', '3px', 'important');",
                elemento);

        return estiloOriginal;
    }

    private void quitarHighlight(
            WebElement elemento,
            String estiloOriginal) {

        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            if (estiloOriginal == null) {
                js.executeScript(
                        "arguments[0].removeAttribute('style');",
                        elemento);
            } else {
                js.executeScript(
                        "arguments[0].setAttribute('style', arguments[1]);",
                        elemento,
                        estiloOriginal);
            }
        } catch (StaleElementReferenceException ignored) {
            // Odoo pudo actualizar el DOM después del clic.
        }
    }
    public void evidenciarEstado(
            By selector,
            Runnable capturarEvidencia) {

        highlightForScreenshot(selector, capturarEvidencia);
    }

    public void highlightForScreenshot(
            By selector,
            Runnable capturarEvidencia) {

        WebDriverWait espera = new WebDriverWait(
                driver, Duration.ofSeconds(30));

        WebElement elemento = espera.until(
                ExpectedConditions.visibilityOfElementLocated(selector));

        scrollToElement(elemento);

        String estiloOriginal = aplicarHighlight(elemento);

        try {
            if (capturarEvidencia != null) {
                capturarEvidencia.run();
            }
        } finally {
            quitarHighlight(elemento, estiloOriginal);
        }
    }


    public void mostrarEstado(By selector) {

        WebElement elemento = new WebDriverWait(
                driver, Duration.ofSeconds(30))
                .until(ExpectedConditions.visibilityOfElementLocated(selector));

        scrollToElement(elemento);
        aplicarHighlight(elemento);

        System.out.println(
                "[EVIDENCIA] Estado visible y resaltado: " + selector);
    }

    public void highlight(By selector) {

        WebElement elemento = wait.until(
                ExpectedConditions.visibilityOfElementLocated(selector));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView(" +
                        "{block:'center', inline:'nearest'});",
                elemento);

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].style.setProperty(" +
                        "'outline', '4px solid #ff0000', 'important');" +
                        "arguments[0].style.setProperty(" +
                        "'outline-offset', '3px', 'important');",
                elemento);

        System.out.println("[HIGHLIGHT] Elemento resaltado: " + selector);
    }

    public void removeHighlight(By selector) {

        for (WebElement elemento : driver.findElements(selector)) {

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].style.removeProperty('outline');" +
                            "arguments[0].style.removeProperty('outline-offset');",
                    elemento);
        }
    }

}
