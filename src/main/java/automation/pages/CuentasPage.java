
package automation.pages;

import automation.selectors.CuentasSelectors;
import automation.utils.ElementActions;
import automation.utils.ExcelUtils;
import automation.utils.WaitUtils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public final class CuentasPage {

    private final WebDriver driver;
    private final WaitUtils wait;
    private final ElementActions elements;

    private long inicioConciliacion;
    private long finConciliacion;
    private String procesoConciliacion;

    private String referenciaCapturada;

    private static final Duration TIMEOUT =
            Duration.ofSeconds(30);

    private static final By INDICADOR_CARGA =
            By.cssSelector(".o_loading_indicator");

    private static final By TARJETAS_CUENTAS =
            By.cssSelector(
                    ".o_kanban_record:not(.o_kanban_ghost)");

    private static final By MODAL_CONFIRMACION =
            By.cssSelector(".modal-content");

    private static final By FILAS_CONCILIACION =
            By.cssSelector(".o_list_view tbody tr.o_data_row");

    public CuentasPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WaitUtils(driver);
        this.elements = new ElementActions(driver);
    }

    // =====================================================
    // UTILIDADES PRIVADAS
    // =====================================================

    private WebDriverWait crearEspera(Duration duracion) {

        WebDriverWait espera = new WebDriverWait(
                driver, duracion);

        espera.pollingEvery(Duration.ofMillis(500));
        espera.ignoring(StaleElementReferenceException.class);

        return espera;
    }

    private WebDriverWait crearEspera() {
        return crearEspera(TIMEOUT);
    }

    private String obtenerNombreBusqueda(String nombreCuenta) {

        return nombreCuenta.contains("|")
                ? nombreCuenta.substring(
                nombreCuenta.indexOf("|") + 1).trim()
                : nombreCuenta.trim();
    }

    private void esperarModalCerrado() {

        crearEspera(Duration.ofSeconds(60)).until(
                ExpectedConditions.invisibilityOfElementLocated(
                        CuentasSelectors.MODAL_ABIERTO));
    }

    private void esperarCargaOdoo() {

        crearEspera().until(
                ExpectedConditions.invisibilityOfElementLocated(
                        INDICADOR_CARGA));
    }

    private boolean estadoVisible(By selector) {

        return driver.findElements(selector)
                .stream()
                .anyMatch(WebElement::isDisplayed);
    }


    private void buscarCuenta(String nombreCuenta) {

        WebDriverWait espera = crearEspera(Duration.ofSeconds(60));

        espera.pollingEvery(Duration.ofMillis(500));

        String cuentaBusqueda =
                obtenerNombreBusqueda(nombreCuenta);

        System.out.println(
                "[BUSCANDO CUENTA] " + cuentaBusqueda);

        // 1. Esperar buscador y escribir con reintentos ante cambios del DOM
        espera.until(d -> {
            try {
                WebElement buscador = d.findElement(
                        CuentasSelectors.BUSCADOR_CUENTAS);

                if (!buscador.isDisplayed() || !buscador.isEnabled()) {
                    return false;
                }

                buscador.click();
                buscador.sendKeys(Keys.CONTROL, "a");
                buscador.sendKeys(Keys.BACK_SPACE);
                buscador.sendKeys(cuentaBusqueda);

                System.out.println(
                        "[OK] Búsqueda ingresada: " + cuentaBusqueda);

                return true;

            } catch (StaleElementReferenceException |
                     NoSuchElementException |
                     ElementClickInterceptedException e) {

                return false;
            }
        });

        // 2. Aplicar filtro de búsqueda
        espera.until(d -> {
            try {
                WebElement buscador = d.findElement(
                        CuentasSelectors.BUSCADOR_CUENTAS);

                if (!buscador.isDisplayed()) {
                    return false;
                }

                buscador.sendKeys(Keys.ARROW_DOWN);
                buscador.sendKeys(Keys.ENTER);

                return true;

            } catch (StaleElementReferenceException |
                     NoSuchElementException e) {

                return false;
            }
        });

        // 3. Esperar que Odoo aplique el filtro
        espera.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".o_searchview_facet")));

        System.out.println("[OK] Filtro aplicado");

        // 4. Buscar tarjeta por nombre exacto
        espera.until(d -> {

            for (WebElement tarjeta :
                    d.findElements(TARJETAS_CUENTAS)) {

                try {
                    WebElement titulo = tarjeta.findElement(
                            By.cssSelector(
                                    ".o_kanban_details strong span"));

                    if (!titulo.isDisplayed()
                            || !titulo.getText().trim()
                            .equals(nombreCuenta.trim())) {
                        continue;
                    }

                    tarjeta.click();

                    return true;

                } catch (StaleElementReferenceException |
                         ElementClickInterceptedException e) {

                    // Se actualizó la tarjeta, volver a localizarla
                    return false;
                }
            }

            return false;
        });

        System.out.println(
                "[OK] Cuenta seleccionada: " + nombreCuenta);
    }

    // =====================================================
    // MÓDULO CUENTAS
    // =====================================================



    public void ingresarModuloCuentas() {
        new automation.utils.OdooNavigation(driver).open(
                automation.utils.OdooMenu.CUENTAS);
        System.out.println("[OK] Módulo Cuentas cargado");
    }
    public void seleccionarCuenta(String nombreCuenta) {

        buscarCuenta(nombreCuenta);
    }

    public void validarDetalleCuenta(String nombreCuenta) {

        WebDriverWait espera = crearEspera();

        espera.until(d -> {

            for (WebElement elemento : d.findElements(
                    CuentasSelectors.DETALLE_CUENTA)) {

                if (elemento.isDisplayed()
                        && elemento.getText().trim()
                        .equals(nombreCuenta.trim())) {

                    System.out.println(
                            "[OK] Detalle de cuenta: " + nombreCuenta);

                    return true;
                }
            }

            return false;
        });
    }

    // =====================================================
    // IMPORTACIÓN DE LAYOUT BANCARIO
    // =====================================================


    public void importarLayoutBancario(String usuario)
            throws IOException {

        // 1. Generar primero el Excel
        Path carpetaBase = Path.of(
                System.getProperty("user.dir"),
                "Documentos",
                "Layout Deposito");

        Path rutaOrigen = carpetaBase.resolve("Invex 2595.xlsx");

        Path carpetaDestino = carpetaBase.resolve("layouts_generados");

        String archivoActualizado =
                ExcelUtils.generarExcelPorUsuario(
                        rutaOrigen.toString(),
                        carpetaDestino.toString(),
                        usuario);

        System.out.println("[OK] Excel generado: " + archivoActualizado);

        // 2. Abrir formulario de importación
        elements.click(CuentasSelectors.IMPORTAR_LAYOUT);

        // 3. Seleccionar banco
        elements.type(
                CuentasSelectors.LAYOUT_BANCO,
                "INVEX 2595");

        elements.click(CuentasSelectors.OPCION_INVEX);

        // 4. Esperar que Odoo muestre el input del archivo
        WebDriverWait espera = crearEspera(Duration.ofSeconds(60));

        espera.until(ExpectedConditions.presenceOfElementLocated(
                CuentasSelectors.SUBIR_LAYOUT));

        // 5. Subir Excel
        elements.upload(
                CuentasSelectors.SUBIR_LAYOUT,
                Path.of(archivoActualizado));

        System.out.println("[OK] Usuario: " + usuario);
        System.out.println("[OK] Archivo: " + archivoActualizado);
    }


    public void cargarTransacciones() {

        elements.click(
                CuentasSelectors.CARGAR_TRANSACCIONES);

        System.out.println(
                "[OK] Clic en Cargar transacciones");

        crearEspera().until(
                ExpectedConditions.invisibilityOfElementLocated(
                        CuentasSelectors.CARGAR_TRANSACCIONES));
    }

    public void validarEstadoCargado() {

        WebDriverWait espera = crearEspera(
                Duration.ofMinutes(5));

        espera.pollingEvery(Duration.ofSeconds(5));

        espera.until(d -> {

            esperarCargaOdoo();

            if (estadoVisible(
                    CuentasSelectors.ESTADO_CARGADO)) {

                System.out.println("[OK] Estado Cargado");
                return true;
            }

            System.out.println(
                    "[ESPERANDO] Estado Cargado...");

            d.navigate().refresh();

            return false;
        });
    }

    public void importarTransacciones() {

        WebDriverWait espera = crearEspera(
                Duration.ofSeconds(60));

        // Confirmar el estado Cargado
        espera.until(
                ExpectedConditions.visibilityOfElementLocated(
                        CuentasSelectors.ESTADO_CARGADO));

        System.out.println(
                "[OK] Estado Cargado confirmado");

        // Refresh necesario para actualizar botones de Odoo
        System.out.println(
                "[INFO] Actualizando pantalla...");

        driver.navigate().refresh();

        esperarCargaOdoo();

        espera.until(
                ExpectedConditions.elementToBeClickable(
                        CuentasSelectors.IMPORTAR_TRANSACCIONES));

        elements.click(
                CuentasSelectors.IMPORTAR_TRANSACCIONES);

        System.out.println(
                "[OK] Clic en Importar transacciones");
    }

    public void validarEstadoProcesando() {

        WebDriverWait espera = crearEspera(
                Duration.ofMinutes(3));

        espera.pollingEvery(Duration.ofSeconds(2));

        espera.until(d -> {

            if (estadoVisible(
                    CuentasSelectors.ESTADO_PROCESANDO)) {

                System.out.println(
                        "[OK] Estado Procesando detectado");

                return true;
            }

            // Odoo puede terminar antes de observar Procesando
            if (estadoVisible(
                    CuentasSelectors.ESTADO_SIN_IDENTIFICAR)) {

                System.out.println(
                        "[OK] Odoo avanzó a Sin identificar");

                return true;
            }

            System.out.println(
                    "[ESPERANDO] Procesando transacciones...");

            d.navigate().refresh();

            return false;
        });
    }

    public void validarEstadoSinIdentificar() {

        WebDriverWait espera = crearEspera(
                Duration.ofMinutes(10));

        espera.pollingEvery(Duration.ofSeconds(10));

        espera.until(d -> {

            if (estadoVisible(
                    CuentasSelectors.ESTADO_SIN_IDENTIFICAR)) {

                System.out.println(
                        "[OK] Estado Sin identificar");

                return true;
            }

            System.out.println(
                    "[ESPERANDO] Estado Sin identificar. Refrescando...");

            d.navigate().refresh();

            return false;
        });

        referenciaCapturada = espera.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                CuentasSelectors.REFERENCIA_TRANSACCION))
                .getText().trim();

        System.out.println(
                "[OK] Referencia capturada: " +
                        referenciaCapturada);
    }

    // =====================================================
    // GENERAL OPERACIONES
    // =====================================================

    public void ingresarGeneralOperaciones() {

        new automation.utils.OdooNavigation(driver).open(automation.utils.OdooMenu.LISTADO_CUENTAS);

        elements.click(
                CuentasSelectors.CUENTA_GENERAL_OPERACIONES);

        System.out.println(
                "[OK] Cuenta General Operaciones seleccionada");
    }

    public void abrirTransacciones() {

        elements.click(
                CuentasSelectors.BOTON_TRANSACCIONES);

        System.out.println(
                "[OK] Transacciones abiertas");
    }

    public void abrirFiltroPersonalizado() {

        elements.click(
                CuentasSelectors.BOTON_FILTROS);

        elements.click(
                CuentasSelectors.AGREGAR_FILTRO_PERSONALIZADO);

        System.out.println(
                "[OK] Filtro personalizado abierto");
    }

    public void filtrarPorReferencia(String referencia) {

        WebDriverWait espera = crearEspera();

        WebElement campo = espera.until(
                ExpectedConditions.visibilityOfElementLocated(
                        CuentasSelectors.CAMPO_FILTRO));

        new Select(campo).selectByValue("reference");

        elements.type(
                CuentasSelectors.VALOR_FILTRO,
                referencia);

        System.out.println(
                "[OK] Referencia ingresada: " + referencia);
    }

    public String obtenerReferenciaCapturada() {

        return referenciaCapturada;
    }

    public void aplicarFiltro() {

        elements.click(
                CuentasSelectors.APLICAR_FILTRO);

        System.out.println("[OK] Filtro aplicado");
    }

    // =====================================================
    // SELECCIÓN Y TRANSFERENCIA DE TRANSACCIONES
    // =====================================================


    public void seleccionarTransaccion() {

        WebDriverWait espera = crearEspera(Duration.ofSeconds(60));

        espera.pollingEvery(Duration.ofMillis(500));

        if (referenciaCapturada == null ||
                referenciaCapturada.isBlank()) {
            throw new IllegalStateException(
                    "No existe una referencia capturada");
        }

        By filasReferencia =
                CuentasSelectors.filasPorReferencia(referenciaCapturada);

        // 1. Identificar el número de recibo mayor
        long mayorRecibo = espera.until(d -> {

            long mayor = -1;

            for (WebElement fila : d.findElements(filasReferencia)) {

                try {
                    String texto = fila.findElement(
                                    CuentasSelectors.NO_RECIBO)
                            .getText().trim();

                    long numero = Long.parseLong(
                            texto.replaceAll("[^0-9]", ""));

                    if (numero > mayor) {
                        mayor = numero;
                    }

                } catch (StaleElementReferenceException |
                         NoSuchElementException |
                         NumberFormatException e) {
                    return null;
                }
            }

            return mayor >= 0 ? mayor : null;
        });

        System.out.println(
                "[OK] Referencia encontrada: " + referenciaCapturada);

        System.out.println(
                "[OK] No. Recibo mayor: " + mayorRecibo);

        // 2. Seleccionar únicamente la fila con el recibo mayor
        By checkbox = CuentasSelectors.checkboxPorReferenciaYRecibo(
                referenciaCapturada,
                String.valueOf(mayorRecibo));

        elements.check(checkbox);

        // 3. Verificar selección
        espera.until(d -> {
            try {
                return d.findElement(checkbox).isSelected();
            } catch (StaleElementReferenceException |
                     NoSuchElementException e) {
                return false;
            }
        });

        System.out.println(
                "[OK] Transacción seleccionada | Referencia: "
                        + referenciaCapturada
                        + " | No. Recibo: " + mayorRecibo);

        // 4. Esperar botón de transferencia
        espera.until(ExpectedConditions.elementToBeClickable(
                CuentasSelectors.TRANSFERIR_SELECCIONADOS));

        System.out.println(
                "[OK] Botón Transferir Seleccionados disponible");
    }


    public void transferirTransacciones(String nombreCuenta) {

        WebDriverWait espera = crearEspera();

        String cuentaBusqueda =
                obtenerNombreBusqueda(nombreCuenta);

        elements.click(
                CuentasSelectors.TRANSFERIR_SELECCIONADOS);

        elements.type(
                CuentasSelectors.CUENTA_DESTINO,
                cuentaBusqueda);

        // Seleccionar opción del autocomplete de Odoo
        By opcionDestino = By.xpath(
                "//*[contains(@class,'o-autocomplete--dropdown-item')]" +
                        "[contains(normalize-space(.),'" +
                        cuentaBusqueda + "')]");

        elements.click(opcionDestino);

        // Confirmar transferencia una sola vez
        espera.until(
                ExpectedConditions.elementToBeClickable(
                        CuentasSelectors.CONFIRMAR_TRANSFERENCIA));

        elements.click(
                CuentasSelectors.CONFIRMAR_TRANSFERENCIA);

        System.out.println(
                "[OK] Transferencia realizada a: " +
                        nombreCuenta);
    }

    public void ingresarCuentaDestino(String nombreCuenta) {

        esperarModalCerrado();

        System.out.println(
                "[OK] Ventana de transferencia cerrada");

        new automation.utils.OdooNavigation(driver).open(automation.utils.OdooMenu.LISTADO_CUENTAS);

        crearEspera().until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".o_kanban_renderer")));

        buscarCuenta(nombreCuenta);

        System.out.println(
                "[OK] Cuenta destino seleccionada: " +
                        nombreCuenta);
    }

    // =====================================================
    // ASIGNACIÓN DE PERIODO OPERATIVO
    // =====================================================

    public void asignarPeriodoOperativo(String periodo) {

        elements.click(
                CuentasSelectors.ASIGNAR_PERIODO);

        elements.type(
                CuentasSelectors.CAMPO_PERIODO,
                periodo);

        elements.click(
                CuentasSelectors.opcionPeriodo(periodo));

        System.out.println(
                "[OK] Periodo operativo seleccionado: " +
                        periodo);
    }

    public void agregarLineaPeriodo(String referencia) {

        elements.click(
                CuentasSelectors.AGREGAR_LINEA);

        System.out.println(
                "[BUSCANDO] Referencia: " + referencia);

        elements.check(
                CuentasSelectors.checkboxPorReferencia(
                        referencia));

        System.out.println(
                "[OK] Referencia seleccionada: " + referencia);

        elements.click(
                CuentasSelectors.SELECCIONAR_LINEA);

        System.out.println(
                "[OK] Línea agregada correctamente");
    }

    public void confirmarAsignacionPeriodo() {

        WebDriverWait espera = crearEspera(
                Duration.ofSeconds(60));

        By botonConfirmar = By.cssSelector(
                "button[name='action_asignar_periodo']");

        By ventanaSeleccion = By.cssSelector(
                ".modal.show .o_select_button");

        System.out.println(
                "[ESPERANDO] Cierre de selección de líneas...");

        espera.until(
                ExpectedConditions.invisibilityOfElementLocated(
                        ventanaSeleccion));

        System.out.println(
                "[OK] Selección de líneas finalizada");

        // No repetir la confirmación si Odoo ya cerró el formulario
        if (driver.findElements(botonConfirmar).isEmpty()) {

            if (estadoVisible(
                    CuentasSelectors.ASIGNAR_PAGADURIA)) {

                System.out.println(
                        "[INFO] Formulario de asignación ya cerrado");

                return;
            }

            throw new IllegalStateException(
                    "No se encontró el botón Confirmar Periodo");
        }

        elements.click(botonConfirmar);

        System.out.println(
                "[OK] Clic en Confirmar Periodo");

        esperarModalCerrado();

        espera.until(
                ExpectedConditions.elementToBeClickable(
                        CuentasSelectors.ASIGNAR_PAGADURIA));

        System.out.println(
                "[OK] Asignación del periodo confirmada");
    }

    // =====================================================
    // ASIGNACIÓN DE PAGADURÍA
    // =====================================================

    public void asignarPagaduria(String pagaduria) {

        esperarModalCerrado();

        elements.click(
                CuentasSelectors.ASIGNAR_PAGADURIA);

        // En Odoo este campo se abre sin escribir
        elements.click(
                CuentasSelectors.CAMPO_PAGADURIA);

        System.out.println(
                "[OK] Opciones de pagaduría desplegadas");

        elements.click(
                CuentasSelectors.opcionPagaduria(pagaduria));

        System.out.println(
                "[OK] Pagaduría seleccionada: " + pagaduria);
    }

    public void agregarLineaPagaduria(String referencia) {

        elements.click(
                CuentasSelectors.AGREGAR_LINEA_PAGADURIA);

        System.out.println(
                "[BUSCANDO] Referencia: " + referencia);

        elements.check(
                CuentasSelectors.checkboxPagaduriaPorReferencia(
                        referencia));

        System.out.println(
                "[OK] Línea de pagaduría seleccionada: " +
                        referencia);

        elements.click(
                CuentasSelectors.SELECCIONAR_LINEA_PAGADURIA);

        System.out.println(
                "[OK] Línea de pagaduría agregada");
    }

    public void confirmarPagaduria() {

        WebDriverWait espera = crearEspera(
                Duration.ofSeconds(60));

        espera.until(d -> {

            for (WebElement boton : d.findElements(
                    CuentasSelectors.CONFIRMAR_PAGADURIA)) {

                try {

                    if (!boton.isDisplayed()
                            || !boton.isEnabled()) {
                        continue;
                    }

                    boton.click();

                    System.out.println(
                            "[OK] Clic en Asignar pagaduría");

                    return true;

                } catch (ElementClickInterceptedException |
                         StaleElementReferenceException e) {

                    System.out.println(
                            "[ESPERANDO] Odoo actualizando formulario...");
                }
            }

            return false;
        });
    }

    // =====================================================
    // CONCILIACIÓN POR PERIODO OPERATIVO
    // =====================================================

    public void abrirConciliacionPeriodo() {

        esperarModalCerrado();

        elements.click(
                CuentasSelectors.CONCILIAR_PERIODO);

        System.out.println(
                "[OK] Conciliación por periodo operativo abierta");
    }

    public void configurarConciliacion(
            String periodo,
            String pagaduria) {

        elements.type(
                CuentasSelectors.PERIODO_CONCILIACION,
                periodo);

        elements.click(
                CuentasSelectors.opcionPeriodo(periodo));

        System.out.println(
                "[OK] Periodo seleccionado: " + periodo);

        // Abrir las opciones sin escribir
        elements.click(
                CuentasSelectors.PAGADURIA_CONCILIACION);

        elements.click(
                CuentasSelectors.opcionPagaduria(pagaduria));

        System.out.println(
                "[OK] Pagaduría seleccionada: " + pagaduria);
    }





    public void ejecutarConciliacion() {

        WebDriverWait espera = crearEspera(Duration.ofSeconds(60));

        inicioConciliacion = 0;

        // 1. Clic en Conciliar
        elements.click(CuentasSelectors.BOTON_CONCILIAR);
        System.out.println("[OK] Clic en Conciliar");

        // 2. Esperar el modal Confirmación
        WebElement modal = espera.until(d -> {

            for (WebElement ventana : d.findElements(
                    CuentasSelectors.MODAL_CONFIRMACION)) {

                try {
                    if (!ventana.isDisplayed()) {
                        continue;
                    }

                    String titulo = ventana.findElement(
                                    CuentasSelectors.TITULO_MODAL)
                            .getText().trim();

                    if (!titulo.equalsIgnoreCase("Confirmación")) {
                        continue;
                    }

                    String mensaje = ventana.findElement(
                                    CuentasSelectors.MENSAJE_CONFIRMACION)
                            .getText().trim();

                    if (mensaje.contains(
                            "Esta operación no se puede deshacer.")) {
                        return ventana;
                    }

                } catch (NoSuchElementException |
                         StaleElementReferenceException e) {
                    // Odoo actualizando modal
                }
            }

            return null;
        });

        System.out.println("[OK] Modal Confirmación detectado");

        // 3. Buscar ACEPTAR o De acuerdo
        WebElement aceptar = espera.until(d -> {

            try {
                for (WebElement boton : modal.findElements(
                        CuentasSelectors.ACEPTAR_CONFIRMACION)) {

                    String texto = boton.getText().trim();

                    if (boton.isDisplayed()
                            && boton.isEnabled()
                            && (texto.equalsIgnoreCase("Aceptar")
                            || texto.equalsIgnoreCase("De acuerdo"))) {

                        return boton;
                    }
                }

            } catch (NoSuchElementException |
                     StaleElementReferenceException e) {
                return null;
            }

            return null;
        });

        // 4. Iniciar medición desde la primera confirmación
        inicioConciliacion = System.nanoTime();

        String textoAceptar = aceptar.getText().trim();
        aceptar.click();

        System.out.println("[OK] Clic en " + textoAceptar);

        // 5. Esperar que desaparezca la confirmación original.
        // No exigir que desaparezca todo el contenedor modal.
        espera.until(d -> {
            try {
                return d.findElements(
                                CuentasSelectors.MODAL_CONFIRMACION)
                        .stream()
                        .filter(WebElement::isDisplayed)
                        .noneMatch(ventana -> {
                            try {
                                return ventana.findElement(
                                                CuentasSelectors.TITULO_MODAL)
                                        .getText().trim()
                                        .equalsIgnoreCase("Confirmación");
                            } catch (NoSuchElementException |
                                     StaleElementReferenceException e) {
                                return false;
                            }
                        });
            } catch (StaleElementReferenceException e) {
                return false;
            }
        });

        System.out.println("[OK] Confirmación cerrada");

        // 6. Verificar si aparece la advertencia opcional de Odoo
        WebElement advertencia = null;

        try {
            advertencia = crearEspera(Duration.ofSeconds(5)).until(d -> {

                for (WebElement ventana : d.findElements(
                        CuentasSelectors.MODAL_CONFIRMACION)) {

                    try {
                        if (!ventana.isDisplayed()) {
                            continue;
                        }

                        String titulo = ventana.findElement(
                                        CuentasSelectors.TITULO_MODAL)
                                .getText().trim();

                        if (!titulo.equalsIgnoreCase("Odoo")) {
                            continue;
                        }

                        for (WebElement mensaje : ventana.findElements(
                                CuentasSelectors.MENSAJE_ADVERTENCIA_CONCILIACION)) {

                            if (mensaje.isDisplayed()
                                    && mensaje.getText().contains(
                                    "Pagos Registrados")) {
                                return ventana;
                            }
                        }

                    } catch (NoSuchElementException |
                             StaleElementReferenceException e) {
                        // Odoo actualizando advertencia
                    }
                }

                return null;
            });

        } catch (TimeoutException e) {
            // La advertencia es opcional
            System.out.println(
                    "[OK] No apareció advertencia adicional");
        }

        // 7. Si apareció, confirmar Continuar de todas formas
        if (advertencia != null) {

            System.out.println(
                    "[ADVERTENCIA] Archivo sin estado Pagos Registrados");

            WebElement continuar = espera.until(
                    ExpectedConditions.elementToBeClickable(
                            CuentasSelectors.CONTINUAR_CONCILIACION));

            String textoContinuar = continuar.getText().trim();

            if (!textoContinuar.equalsIgnoreCase(
                    "Continuar de todas formas")) {

                throw new IllegalStateException(
                        "Botón inesperado: " + textoContinuar);
            }

            // Clic una sola vez
            continuar.click();

            System.out.println(
                    "[OK] Clic en Continuar de todas formas");

            // Esperar que desaparezca el mensaje específico
            espera.until(d -> {

                for (WebElement mensaje : d.findElements(
                        CuentasSelectors.MENSAJE_ADVERTENCIA_CONCILIACION)) {

                    try {
                        if (mensaje.isDisplayed()) {
                            return false;
                        }

                    } catch (StaleElementReferenceException e) {
                        return false;
                    }
                }

                return true;
            });

            System.out.println("[OK] Advertencia cerrada");
        }

        System.out.println("[OK] Conciliación iniciada");
        System.out.println("[TIEMPO] Cronómetro activo");
    }


    // =====================================================
    // PROCESOS DE CONCILIACIÓN
    // =====================================================

    public void abrirProcesosConciliacion() {

        WebDriverWait espera = crearEspera(
                Duration.ofSeconds(60));

        esperarModalCerrado();

        System.out.println(
                "[OK] Ventana de conciliación cerrada");

        // Abrir pantalla mediante el botón del formulario
        elements.click(
                CuentasSelectors.PROCESOS_CONCILIACION);

        System.out.println(
                "[OK] Clic en Procesos de Conciliación");

        // Verificar navegación
        espera.until(d -> {

            for (WebElement titulo : d.findElements(
                    CuentasSelectors.PANTALLA_PROCESOS_CONCILIACION)) {

                if (titulo.isDisplayed()
                        && titulo.getText().trim()
                        .equalsIgnoreCase(
                                "Procesos de conciliación")) {

                    return true;
                }
            }

            return false;
        });

        System.out.println(
                "[OK] Pantalla Procesos de conciliación abierta");
    }



    public void validarConciliacionCompleta() {

        // Iniciar cronómetro al entrar al Step
        long inicio = System.nanoTime();

        WebDriverWait espera = crearEspera(
                Duration.ofMinutes(60));

        espera.pollingEvery(Duration.ofSeconds(4));
        espera.ignoring(StaleElementReferenceException.class);

        // 1. Identificar el proceso más reciente por ID
        String idProceso = crearEspera(
                Duration.ofSeconds(60)).until(d -> {

            String reciente = null;
            long mayorId = -1;

            for (WebElement fila : d.findElements(
                    CuentasSelectors.FILAS_CONCILIACION)) {

                try {
                    String nombre = fila.findElement(
                                    CuentasSelectors.ID_PROCESO)
                            .getText().trim();

                    if (!nombre.matches("PC\\d+")) {
                        continue;
                    }

                    long numero = Long.parseLong(
                            nombre.substring(2));

                    if (numero > mayorId) {
                        mayorId = numero;
                        reciente = nombre;
                    }

                } catch (NoSuchElementException |
                         StaleElementReferenceException e) {
                    // Odoo está actualizando la tabla
                }
            }

            return reciente;
        });

        System.out.println(
                "[OK] Proceso más reciente: " + idProceso);

        System.out.println(
                "[TIEMPO] Iniciando medición: " + idProceso);

        try {

            // 2. Validar únicamente el proceso identificado
            espera.until(d -> {

                try {
                    WebElement proceso = null;

                    for (WebElement fila : d.findElements(
                            CuentasSelectors.FILAS_CONCILIACION)) {

                        String nombre = fila.findElement(
                                        CuentasSelectors.ID_PROCESO)
                                .getText().trim();

                        if (nombre.equals(idProceso)) {
                            proceso = fila;
                            break;
                        }
                    }

                    if (proceso != null) {

                        long actual = Long.parseLong(
                                proceso.findElement(
                                                CuentasSelectors.TX_PROCESADAS)
                                        .getText().trim()
                                        .replace(",", ""));

                        long total = Long.parseLong(
                                proceso.findElement(
                                                CuentasSelectors.TX_TOTALES)
                                        .getText().trim()
                                        .replace(",", ""));

                        double porcentaje = Double.parseDouble(
                                proceso.findElement(
                                                CuentasSelectors.AVANCE_PROCESO)
                                        .getText().trim()
                                        .replace("%", "")
                                        .replaceAll("\\s+", ""));

                        String estatus = proceso.findElement(
                                        CuentasSelectors.ESTATUS_PROCESO)
                                .getText().trim();

                        double duracionSegundos =
                                (System.nanoTime() - inicio)
                                        / 1_000_000_000.0;

                        System.out.printf(
                                "[CONCILIACION] %s | %.0f%% | %d/%d | %s | Tiempo: %.3f s%n",
                                idProceso,
                                porcentaje,
                                actual,
                                total,
                                estatus,
                                duracionSegundos);

                        // 3. Validación exitosa
                        if (porcentaje == 100
                                && actual == total
                                && total > 0
                                && estatus.equalsIgnoreCase("Conciliado")) {

                            long fin = System.nanoTime();

                            duracionSegundos =
                                    (fin - inicio) / 1_000_000_000.0;

                            System.out.printf(
                                    "[OK] Proceso %s conciliado al 100%%%n",
                                    idProceso);

                            System.out.printf(
                                    "[TIEMPO] Duración: %.3f segundos%n",
                                    duracionSegundos);

                            // 4. Guardar métricas en CSV
                            guardarMetricasConciliacion(
                                    idProceso,
                                    actual,
                                    total,
                                    duracionSegundos,
                                    "EXITO");

                            return true;
                        }

                        if (estatus.equalsIgnoreCase("Fallido")) {
                            throw new IllegalStateException(
                                    "Conciliación fallida: " + idProceso);
                        }
                    }

                } catch (NoSuchElementException |
                         NumberFormatException |
                         StaleElementReferenceException e) {

                    System.out.println(
                            "[ESPERANDO] Actualizando proceso " + idProceso);
                }

                // 5. Refresh mientras no alcance el 100%
                System.out.println(
                        "[ESPERANDO] Refrescando " + idProceso);

                d.navigate().refresh();

                return false;
            });

        } finally {

            double duracionSegundos =
                    (System.nanoTime() - inicio)
                            / 1_000_000_000.0;

            System.out.printf(
                    "[TIEMPO TOTAL] %s: %.3f segundos%n",
                    idProceso,
                    duracionSegundos);
        }
    }



    private void guardarMetricasConciliacion(
            String idProceso,
            long procesadas,
            long total,
            double duracionSegundos,
            String estado) {

        // Fecha de ejecución
        String fecha = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Identificar el hilo de ejecución
        String hilo = Thread.currentThread().getName();

        String encabezado =
                "Fecha,Hilo,Proceso,Procesadas,Total,Avance,DuracionSegundos,Estado\n";

        String fila = String.format(
                Locale.ROOT,
                "%s,%s,%s,%d,%d,100,%.3f,%s%n",
                fecha,
                hilo,
                idProceso,
                procesadas,
                total,
                duracionSegundos,
                estado);

        synchronized (CuentasPage.class) {

            try {
                // Obtener la carpeta de evidencias de la ejecución actual
                Path archivoRuta = Path.of(
                        System.getProperty("user.dir"),
                        "target",
                        "current-run.txt");

                if (Files.notExists(archivoRuta)) {
                    throw new IllegalStateException(
                            "No se encontró la ruta de evidencias: "
                                    + archivoRuta);
                }

                String rutaEvidencias = Files.readString(
                        archivoRuta).trim();

                if (rutaEvidencias.isBlank()) {
                    throw new IllegalStateException(
                            "La ruta de evidencias está vacía");
                }

                // Misma carpeta que utiliza ScenarioPdfReporter
                Path carpetaEvidencias = Path.of(rutaEvidencias);

                if (!carpetaEvidencias.isAbsolute()) {
                    carpetaEvidencias = Path.of(
                                    System.getProperty("user.dir"))
                            .resolve(carpetaEvidencias);
                }

                Path reporte = carpetaEvidencias.resolve(
                        "metricas_cp02.csv");

                Files.createDirectories(carpetaEvidencias);

                // Crear archivo con encabezados si no existe
                if (Files.notExists(reporte)) {
                    Files.writeString(
                            reporte,
                            encabezado,
                            StandardOpenOption.CREATE_NEW);
                }

                // Agregar los resultados sin sobrescribir
                Files.writeString(
                        reporte,
                        fila,
                        StandardOpenOption.APPEND);

                System.out.println(
                        "[CSV] Métricas CP02 guardadas en: "
                                + reporte.toAbsolutePath());

            } catch (IOException e) {
                throw new IllegalStateException(
                        "No se pudieron guardar las métricas CP02",
                        e);
            }
        }
    }


}
