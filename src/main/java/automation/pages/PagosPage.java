
package automation.pages;

import automation.selectors.PagosSelectors;
import automation.utils.ElementActions;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Path;
import java.time.Duration;

public final class PagosPage {

    private final WebDriver driver;
    private final ElementActions elements;

    public PagosPage(WebDriver driver) {
        this.driver = driver;
        this.elements = new ElementActions(driver);
    }

    // =========================================================
    // 1. NAVEGACIÓN Y CONFIGURACIÓN DE PAGOS
    // =========================================================


    public void seleccionarMeNU(
            String fechaRecepcion,
            String periodo,
            String nombreDependencia,
            String pagaduria) {

        automation.utils.BrowserConciliationReport.set("Dependencia", nombreDependencia);
        automation.utils.BrowserConciliationReport.set("Pagaduria", pagaduria);
        automation.utils.BrowserConciliationReport.set("Periodo", periodo);

        new automation.utils.OdooNavigation(driver).open(automation.utils.OdooMenu.PAGOS);

        // Crear nuevo registro
        elements.click(PagosSelectors.NUEVO);

        // Buscar dependencia
        elements.type(
                PagosSelectors.DEPENDENCIA_25,
                nombreDependencia);

        // Seleccionar dependencia
        By opcionDependencia = By.xpath(
                "//*[contains(@class,'o-autocomplete--dropdown-item')]" +
                        "[normalize-space()='" + nombreDependencia + "']");

        elements.click(opcionDependencia);

        System.out.println(
                "[OK] Dependencia seleccionada: " + nombreDependencia);


        WebDriverWait espera = new WebDriverWait(
                driver, Duration.ofSeconds(30));

// Esperar a que el campo de pagaduría esté disponible
        WebElement campoPagaduria = espera.until(
                ExpectedConditions.elementToBeClickable(
                        PagosSelectors.PAGADURIA));

// Abrir el desplegable
        campoPagaduria.click();

// Esperar a que Odoo cargue la opción parametrizada
        espera.until(d -> {

            WebElement select = d.findElement(
                    PagosSelectors.PAGADURIA);

            Select opciones = new Select(select);

            return opciones.getOptions().stream()
                    .anyMatch(opcion ->
                            opcion.getText().trim().equals(pagaduria));
        });

// Seleccionar la opción exacta
        WebElement selectActualizado = espera.until(
                ExpectedConditions.elementToBeClickable(
                        PagosSelectors.PAGADURIA));

        new Select(selectActualizado)
                .selectByVisibleText(pagaduria);

        System.out.println(
                "[OK] Pagaduría seleccionada: " + pagaduria);


        System.out.println(
                "[OK] Pagaduría seleccionada: " + pagaduria);

        // Fecha de recepción
        elements.type(
                PagosSelectors.FECHA_RECEPCION,
                fechaRecepcion);

        driver.findElement(
                PagosSelectors.FECHA_RECEPCION).sendKeys(Keys.TAB);

        // Periodicidad
        elements.select(
                PagosSelectors.PERIODICIDAD,
                "Quincenal");

        // Periodo operativo
        elements.type(
                PagosSelectors.PERIODO_OPERATIVO,
                periodo);

        elements.click(
                PagosSelectors.opcionPeriodo(periodo));

        System.out.println(
                "[OK] Fecha Recepción: " + fechaRecepcion);

        System.out.println(
                "[OK] Periodicidad: Quincenal");

        System.out.println(
                "[OK] Periodo Operativo: " + periodo);
    }


    // =========================================================
    // 2. SUBIR ARCHIVO ARR
    // =========================================================

    public void subirArchivo(String archivo) {

        Path rutaArchivo = Path.of(
                System.getProperty("user.dir"),
                "Documentos",
                "ARR",
                archivo
        ).toAbsolutePath().normalize();

        automation.utils.BrowserConciliationReport.set("ArchivoARR", archivo);
        try (var input = java.nio.file.Files.newInputStream(rutaArchivo);
             var workbook = org.apache.poi.ss.usermodel.WorkbookFactory.create(input)) {
            long records = 0;
            var formatter = new org.apache.poi.ss.usermodel.DataFormatter();
            for (var row : workbook.getSheetAt(0)) {
                if (row.getRowNum() == 0) continue;
                for (var cell : row) {
                    if (!formatter.formatCellValue(cell).isBlank()) { records++; break; }
                }
            }
            automation.utils.BrowserConciliationReport.set("RegistrosArchivo", records);
        } catch (java.io.IOException e) {
            throw new IllegalStateException("No se pudo contar el ARR para el reporte web: " + archivo, e);
        }

        elements.upload(
                PagosSelectors.SUBIR_ARCHIVO,
                rutaArchivo);

        System.out.println(
                "[OK] Archivo cargado: " +
                        rutaArchivo.getFileName());
    }

    // =========================================================
    // 3. LEER ARCHIVO
    // =========================================================

    public void leerArchivo() {

        WebDriverWait espera = new WebDriverWait(
                driver, Duration.ofMinutes(3));

        espera.pollingEvery(Duration.ofSeconds(3));

        // Activar complemento solamente si está desmarcado
        elements.check(PagosSelectors.ES_COMPLEMENTO);

        // Ejecutar lectura del archivo
        elements.click(PagosSelectors.LEER_ARCHIVO);

        System.out.println("[OK] Clic en Leer Archivo");

        // Esperar hasta que Odoo termine la lectura
        espera.until(d -> {

            // Detectar errores de Odoo
            for (WebElement modal : d.findElements(
                    PagosSelectors.ERROR_ODOO)) {

                if (modal.isDisplayed()) {

                    String mensaje = modal.getText().trim();

                    if (!mensaje.isEmpty()) {
                        throw new IllegalStateException(
                                "Odoo mostró un mensaje: " + mensaje);
                    }
                }
            }

            // Detectar disponibilidad de Procesar Archivo
            for (WebElement boton : d.findElements(
                    PagosSelectors.PROCESAR_ARCHIVO)) {

                if (boton.isDisplayed() &&
                        boton.isEnabled()) {

                    System.out.println(
                            "[OK] Procesar Archivo disponible");

                    return true;
                }
            }

            System.out.println(
                    "[ESPERANDO] Odoo está leyendo el archivo...");

            return false;
        });
    }

    // =========================================================
    // 4. SELECCIONAR COMPLEMENTO
    // =========================================================

    public void seleccionarComplemento() {

        elements.check(PagosSelectors.ES_COMPLEMENTO);

        System.out.println(
                "[OK] Complemento seleccionado");
    }

    // =========================================================
    // 5. PROCESAR ARCHIVO
    // =========================================================

    public void procesarArchivo() {

        WebDriverWait espera = new WebDriverWait(
                driver, Duration.ofSeconds(60));

        espera.pollingEvery(Duration.ofSeconds(1));
        espera.ignoring(StaleElementReferenceException.class);

        // Esperar hasta que el botón esté disponible
        espera.until(d -> {

            for (WebElement boton : d.findElements(
                    PagosSelectors.PROCESAR_ARCHIVO)) {

                if (boton.isDisplayed() &&
                        boton.isEnabled()) {

                    return true;
                }
            }

            System.out.println(
                    "[ESPERANDO] Procesar Archivo no disponible...");

            return false;
        });

        // Scroll y clic encapsulados
        elements.click(PagosSelectors.PROCESAR_ARCHIVO);

        System.out.println(
                "[OK] Clic en Procesar Archivo");
    }

    // =========================================================
    // 6. REGISTRAR PAGOS
    // =========================================================

    public void registrarPagos() {

        new automation.utils.PaymentStateWait(driver).until(
                PagosSelectors.REGISTRAR_PAGOS, "Registrar Pagos",
                Duration.ofSeconds(automation.utils.PaymentStateWait.positive("payments.register.timeout.seconds", 300)));

        // Scroll y clic encapsulados
        elements.click(PagosSelectors.REGISTRAR_PAGOS);

        System.out.println(
                "[OK] Clic en Registrar Pagos");
    }

    // =========================================================
    // 7. VALIDAR ESTADO TERMINADO
    // =========================================================



    public void validarArchivoTerminado() {

        new automation.utils.PaymentStateWait(driver).until(
                PagosSelectors.ESTADO_TERMINADO, "Estado Terminado",
                Duration.ofSeconds(automation.utils.PaymentStateWait.positive("payments.finished.timeout.seconds", 180)));
        elements.highlight(PagosSelectors.ESTADO_TERMINADO);
        System.out.println("[FLUJO] Validacion de Terminado completa; sigue captura y cierre del escenario.");
    }

}
