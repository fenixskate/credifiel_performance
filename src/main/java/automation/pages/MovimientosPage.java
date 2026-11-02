
package automation.pages;

import automation.selectors.MovimientosSelectors;
import automation.utils.MovimientosCreditoUtils;
import automation.utils.MovimientosExcelUtils;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.openqa.selenium.NoSuchElementException;


public class MovimientosPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final automation.utils.ElementActions elements;
    private final automation.utils.OdooNavigation navigation;

    private String usuario;
    private String archivoGenerado;

    private int registrosEsperados;

    private long inicioCarga;
    private long finCarga;
    private long inicioImportacion;

    private String fechaExportacion;
    private String nombreArchivoExportado;
    private java.nio.file.Path descargaExportada;

    private static final Path CARPETA_AZTECA = Path.of(
            "C:\\Users\\Cuevita\\Documents\\GIT\\PruebaTecnica\\Documentos\\Azteca_Descargados");

    private long inicioConciliacion;
    private long finConciliacion;
    private String procesoConciliacion;

    private static final Object CSV_LOCK = new Object();

    public MovimientosPage(WebDriver driver) {
        this.driver = driver;
        this.elements = new automation.utils.ElementActions(driver);
        this.navigation = new automation.utils.OdooNavigation(driver);
        this.wait = new WebDriverWait(
                driver, Duration.ofSeconds(30));
    }

    public void ingresarCuentas() {
        navigation.open(automation.utils.OdooMenu.CUENTAS);
    }

    public void seleccionarCuenta(String nombre) {
        new CuentasPage(driver).seleccionarCuenta(nombre);
        wait.until(ExpectedConditions.elementToBeClickable(MovimientosSelectors.IMPORTAR_LAYOUT));
    }

    public void abrirImportacion() {

        elements.click(MovimientosSelectors.IMPORTAR_LAYOUT);
    }

    public void seleccionarBanco(String banco) {

        WebElement campo = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        MovimientosSelectors.LAYOUT_BANCO));

        campo.sendKeys(banco);

        wait.until(ExpectedConditions.elementToBeClickable(
                MovimientosSelectors.opcionBanco(banco))).click();

        System.out.println("[OK] Banco: " + banco);
    }


    public void subirArchivo(String usuario) throws IOException {

        this.usuario = usuario;

        Path carpetaBase = Path.of(
                System.getProperty("user.dir"),
                "Documentos",
                "Insumos Flujo 4"
        ).toAbsolutePath().normalize();

        Path rutaOriginal = carpetaBase.resolve("AZTECA_ QA.xlsx");

        Path carpetaGenerados = carpetaBase.resolve("AZTECA_generados");

        if (!Files.isRegularFile(rutaOriginal)) {
            throw new IOException(
                    "No existe el archivo original: " + rutaOriginal);
        }

        Files.createDirectories(carpetaGenerados);

        archivoGenerado =
                MovimientosExcelUtils.generarExcelPorUsuario(
                        rutaOriginal.toString(),
                        carpetaGenerados.toString(),
                        usuario);

        Path archivo = Path.of(archivoGenerado);

        if (!Files.isRegularFile(archivo)) {
            throw new IOException(
                    "No se generó el archivo: " + archivo);
        }

        WebElement input = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        MovimientosSelectors.SUBIR_ARCHIVO));

        input.sendKeys(archivo.toAbsolutePath().toString());

        System.out.println(
                "[OK] Archivo subido: " + archivo.getFileName());

        System.out.println(
                "[RUTA] " + archivo.toAbsolutePath());
    }



    public void cargarTransacciones() {

        WebDriverWait espera = new WebDriverWait(
                driver, Duration.ofSeconds(60));

        // Esperar el botón Cargar transacciones
        WebElement boton = espera.until(
                ExpectedConditions.elementToBeClickable(
                        MovimientosSelectors.CARGAR_TRANSACCIONES));

        // Iniciar medición del tiempo
        inicioCarga = System.nanoTime();

        // Hacer clic ANTES del refresh
        elements.click(boton);

        System.out.println("[OK] Clic en Cargar transacciones");

        // Esperar que Odoo termine la acción del botón
        espera.until(ExpectedConditions.or(
                ExpectedConditions.stalenessOf(boton),
                ExpectedConditions.presenceOfElementLocated(
                        MovimientosSelectors.FILAS)
        ));

        // Refrescar después del clic
        driver.navigate().refresh();

        System.out.println("[REFRESH] Página actualizada después de cargar");

        espera.until(ExpectedConditions.presenceOfElementLocated(
                MovimientosSelectors.FILAS));

        System.out.println("[OK] Transacciones disponibles para validar");
    }


    public void validarCargado() {

        esperarEstado("Cargado", Duration.ofMinutes(10));

        finCarga = System.nanoTime();

        System.out.println("[TIEMPO] Carga: " +
                segundos(inicioCarga, finCarga) + " s");
    }

    public void importarTransacciones() {

        inicioImportacion = System.nanoTime();

        elements.click(MovimientosSelectors.IMPORTAR_TRANSACCIONES);

        System.out.println("[TIEMPO] Inicio de importación");
    }

    public void validarImportado() {

        esperarEstado("Importado", Duration.ofMinutes(15));

        long fin = System.nanoTime();

        if (inicioCarga == 0 || finCarga == 0
                || inicioImportacion == 0) {
            throw new IllegalStateException(
                    "Los cronómetros no fueron inicializados");
        }

        double carga = segundos(inicioCarga, finCarga);
        double importacion = segundos(inicioImportacion, fin);
        double total = segundos(inicioCarga, fin);

        guardarCsv(carga, importacion, total);

        System.out.println("[OK] Todos los movimientos importados");
        System.out.println("[METRICA] Total: " + total + " s");
    }

    public void validarTransaccionesImportadas() {
        esperarEstado("Importado", Duration.ofMinutes(15));
    }

    private void esperarEstado(
            String esperado, Duration tiempoMaximo) {

        long limite = System.nanoTime() + tiempoMaximo.toNanos();

        automation.utils.AsyncRefresh updates = new automation.utils.AsyncRefresh(driver);
        while (System.nanoTime() < limite) {

            try {
                List<WebElement> filas = wait.until(d -> {
                    List<WebElement> encontradas =
                            d.findElements(MovimientosSelectors.FILAS);

                    return encontradas.isEmpty()
                            ? null : encontradas;
                });

                int total = filas.size();
                int correctos = 0;
                int duplicados = 0;
                int fallidos = 0;

                Map<String, Integer> estados = new LinkedHashMap<>();

                for (WebElement fila : filas) {

                    String estado = fila.findElement(
                                    MovimientosSelectors.ESTATUS)
                            .getText().trim();

                    estados.merge(estado, 1, Integer::sum);

                    if (estado.equalsIgnoreCase(esperado)) {
                        correctos++;
                    }

                    if (estado.equalsIgnoreCase("Duplicado")) {
                        duplicados++;
                    }

                    if (estado.equalsIgnoreCase("Fallido")
                            || estado.equalsIgnoreCase("Error")) {
                        fallidos++;
                    }
                }

                if (registrosEsperados == 0) {
                    registrosEsperados = total;
                }

                System.out.println(
                        "[AZTECA] Esperado: " + esperado +
                                " | Registros=" + total +
                                " | Estados=" + estados);

                if (duplicados > 0 || fallidos > 0) {
                    throw new AssertionError(
                            "Existen registros no importables. " +
                                    "Duplicados=" + duplicados +
                                    ", fallidos=" + fallidos +
                                    ", estados=" + estados);
                }

                if (total == registrosEsperados
                        && correctos == registrosEsperados) {

                    System.out.println(
                            "[OK] " + correctos +
                                    " movimientos en estado " + esperado);
                    return;
                }

            } catch (StaleElementReferenceException e) {
                System.out.println(
                        "[ESPERANDO] Se actualizó la tabla de Odoo");
            }

            updates.poll();
        }

        throw new AssertionError(
                "Tiempo agotado esperando el estado " + esperado);
    }

    private static double segundos(long inicio, long fin) {
        return (fin - inicio) / 1_000_000_000.0;
    }

    private void guardarCsv(
            double carga,
            double importacion,
            double total) {

        if (archivoGenerado == null || usuario == null) {
            throw new IllegalStateException(
                    "Falta usuario o archivo en el reporte");
        }

        Path reporte = Path.of("output", "metricas_cp04.csv");

        String encabezado =
                "Hora,Usuario,Archivo,Registros," +
                        "CargaSeg,ImportacionSeg,TotalSeg,Estado\n";

        String hora = LocalTime.now().format(
                DateTimeFormatter.ofPattern("HH:mm:ss"));

        String fila = String.format(
                Locale.ROOT,
                "%s,\"%s\",\"%s\",%d,%.3f,%.3f,%.3f,EXITO%n",
                hora,
                usuario.replace("\"", "\"\""),
                Path.of(archivoGenerado).getFileName(),
                registrosEsperados,
                carga,
                importacion,
                total);

        synchronized (CSV_LOCK) {
            try {
                Files.createDirectories(reporte.getParent());

                if (Files.notExists(reporte)) {
                    Files.writeString(reporte, encabezado,
                            StandardOpenOption.CREATE_NEW);
                }

                Files.writeString(reporte, fila,
                        StandardOpenOption.APPEND);

            } catch (IOException e) {
                throw new IllegalStateException(
                        "No se pudo escribir métricas CP04", e);
            }
        }

        System.out.println("[CSV] " + reporte.toAbsolutePath());
    }





    public void subirArchivo(String usuario, String archivo)
            throws IOException {

        this.usuario = usuario;

        // 1. Directorio principal
        Path carpetaDocumentos = Path.of(
                System.getProperty("user.dir"),
                "Documentos"
        ).toAbsolutePath().normalize();

        // 2. Archivo original recibido del Feature
        Path rutaOriginal = carpetaDocumentos.resolve(archivo);

        // 3. Carpeta donde se generan los archivos del Flujo 4
        Path carpetaDestino = Path.of(
                System.getProperty("user.dir"),
                "Documentos",
                "Insumos Flujo 4",
                "AZTECA_generados"
        ).toAbsolutePath().normalize();

        // 4. Validar archivo original
        if (!Files.isRegularFile(rutaOriginal)) {
            throw new IOException(
                    "No existe el archivo original: "
                            + rutaOriginal);
        }

        Files.createDirectories(carpetaDestino);

        System.out.println(
                "[EXCEL] Archivo original: " + rutaOriginal);

        // 5. Generar Excel independiente por usuario
        archivoGenerado =
                MovimientosExcelUtils.generarExcelPorUsuario(
                        rutaOriginal.toString(),
                        carpetaDestino.toString(),
                        usuario);

        Path rutaGenerada = Path.of(archivoGenerado)
                .toAbsolutePath().normalize();

        if (!Files.isRegularFile(rutaGenerada)) {
            throw new IOException(
                    "No se generó el archivo: " + rutaGenerada);
        }

        System.out.println(
                "[EXCEL] Archivo generado: "
                        + rutaGenerada.getFileName());

        // 6. Obtener fecha de exportación del Excel generado
        fechaExportacion =
                MovimientosExcelUtils.obtenerFechaExportacion(
                        rutaGenerada.toString());

        System.out.println(
                "[EXCEL] Fecha para exportación: "
                        + fechaExportacion);

        // 7. Esperar campo de carga en Odoo
        WebElement input = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        MovimientosSelectors.SUBIR_ARCHIVO));

        // 8. Subir Excel sin refrescar la página
        input.sendKeys(rutaGenerada.toString());

        System.out.println("[OK] Usuario: " + usuario);
        System.out.println(
                "[OK] Archivo subido: "
                        + rutaGenerada.getFileName());

        // 9. Esperar el botón Cargar transacciones
        wait.until(ExpectedConditions.elementToBeClickable(
                MovimientosSelectors.CARGAR_TRANSACCIONES));

        System.out.println(
                "[OK] Formulario listo para cargar transacciones");
    }



    public void ingresarExportarMovimientos() {
        navigation.open(automation.utils.OdooMenu.EXPORTAR_MOVIMIENTOS);
    }

    public void crearNuevaExportacion() {

        elements.click(MovimientosSelectors.NUEVA_EXPORTACION);

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                MovimientosSelectors.FECHA_INICIAL_EXPORTACION));

        System.out.println("[OK] Nueva exportación");
    }

    public void establecerFechasExportacion() {

        if (fechaExportacion == null) {
            throw new IllegalStateException(
                    "No se obtuvo la fecha del Excel generado");
        }

        WebElement fechaInicial = wait.until(
                ExpectedConditions.elementToBeClickable(
                        MovimientosSelectors.FECHA_INICIAL_EXPORTACION));

        fechaInicial.clear();
        fechaInicial.sendKeys(fechaExportacion);
        fechaInicial.sendKeys(Keys.TAB);

        WebElement fechaFinal = wait.until(
                ExpectedConditions.elementToBeClickable(
                        MovimientosSelectors.FECHA_FINAL_EXPORTACION));

        fechaFinal.clear();
        fechaFinal.sendKeys(fechaExportacion);
        fechaFinal.sendKeys(Keys.TAB);

        System.out.println(
                "[OK] Fecha inicial y final: " + fechaExportacion);
    }


    public void generarExportacion() {

        WebDriverWait espera = new WebDriverWait(
                driver, Duration.ofSeconds(60));

        // Clic en Generar Exportación
        espera.until(ExpectedConditions.elementToBeClickable(
                MovimientosSelectors.GENERAR_EXPORTACION)).click();

        System.out.println("[OK] Clic en Generar Exportación");

        // Esperar la ventana de confirmación de Odoo
        By mensajeConfirmacion = By.xpath(
                "//div[@role='dialog' " +
                        "and contains(@class,'o_technical_modal') " +
                        "and contains(@class,'d-block')]" +
                        "//p[contains(.,'El proceso corre en segundo plano')]"
        );

        espera.until(ExpectedConditions.visibilityOfElementLocated(
                mensajeConfirmacion));

        System.out.println("[OK] Ventana de confirmación visible");
    }


    public void confirmarExportacion() {

        WebDriverWait espera = new WebDriverWait(
                driver, Duration.ofSeconds(60));

        By modal = By.xpath(
                "//div[contains(@class,'modal-content')]" +
                        "[.//p[contains(.,'El proceso corre en segundo plano')]]"
        );

        espera.until(
                ExpectedConditions.visibilityOfElementLocated(modal));

        System.out.println("[OK] Ventana de confirmación visible");

        espera.until(d -> {
            try {
                WebElement ventana = d.findElement(modal);

                WebElement boton = ventana.findElement(
                        MovimientosSelectors.CONFIRMAR_EXPORTACION);

                String texto = boton.getText().trim();

                if (!boton.isDisplayed() || !boton.isEnabled()) {
                    return false;
                }

                if (!texto.equalsIgnoreCase("Aceptar")
                        && !texto.equalsIgnoreCase("De acuerdo")) {
                    return false;
                }

                elements.click(boton);

                System.out.println("[OK] Clic en " + texto);

                return true;

            } catch (StaleElementReferenceException |
                     NoSuchElementException e) {
                return false;
            }
        });

        espera.until(
                ExpectedConditions.invisibilityOfElementLocated(modal));

        System.out.println("[OK] Exportación confirmada");
    }

    public void validarArchivoExportado() {

        WebDriverWait espera = new WebDriverWait(
                driver, Duration.ofSeconds(30));

        long limite = System.nanoTime()
                + Duration.ofMinutes(15).toNanos();

        By campoArchivo = By.cssSelector(
                "div[name='cf_file']");

        By enlaceArchivo = By.cssSelector(
                "div[name='cf_file'] a.o_form_uri");

        automation.utils.AsyncRefresh updates = new automation.utils.AsyncRefresh(driver);
        while (System.nanoTime() < limite) {

            try {
                driver.switchTo().defaultContent();

                // Esperar a que Odoo renderice el formulario
                espera.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(".o_form_view .o_form_sheet")));

                List<WebElement> campos =
                        driver.findElements(campoArchivo);

                System.out.println(
                        "[DEBUG] Campos cf_file: " + campos.size());

                // Revisar si Odoo generó el archivo
                List<WebElement> enlaces =
                        driver.findElements(enlaceArchivo);

                if (!enlaces.isEmpty()) {

                    String nombre = enlaces.get(0)
                            .getDomProperty("textContent")
                            .trim();

                    if (nombre.startsWith("Movimientos_Conciliados_")
                            && nombre.endsWith(".xlsx")) {

                        nombreArchivoExportado = nombre;

                        System.out.println(
                                "[OK] Archivo generado: "
                                        + nombreArchivoExportado);

                        return;
                    }
                }

                // Comprobar si terminó, pero sin archivo
                boolean completado = !driver.findElements(
                                By.cssSelector(
                                        "div[name='cf_state'] " +
                                                "button[data-value='done'][aria-checked='true']"))
                        .isEmpty();

                System.out.println(
                        "[DEBUG] Exportación completada: " + completado);

                System.out.println(
                        "[ESPERANDO] Archivo aún no disponible");

            } catch (TimeoutException e) {
                System.out.println(
                        "[ESPERANDO] Odoo todavía no renderiza el formulario");

            } catch (StaleElementReferenceException e) {
                System.out.println(
                        "[ESPERANDO] Odoo actualizó el formulario");
            }

            updates.poll();
        }

        throw new AssertionError(
                "No apareció el archivo exportado después de 15 minutos");
    }



    public void descargarArchivoExportado() {

        if (nombreArchivoExportado == null) {
            throw new IllegalStateException(
                    "No se encontró el nombre del archivo exportado");
        }

        WebDriverWait espera = new WebDriverWait(
                driver, Duration.ofSeconds(60));

        WebElement enlace = espera.until(
                ExpectedConditions.elementToBeClickable(
                        MovimientosSelectors.LINK_ARCHIVO_EXPORTADO));

        String nombre = enlace.getDomProperty("textContent").trim();

        System.out.println("[DESCARGA] Archivo detectado: " + nombre);

        if (!nombre.equals(nombreArchivoExportado)) {
            throw new IllegalStateException(
                    "El archivo mostrado no coincide con el esperado: "
                            + nombreArchivoExportado);
        }

        // Carpeta de descargas del navegador
        Path carpetaDescargas = Path.of(
                System.getProperty("user.home"),
                "Downloads"
        );

        Path ruta = carpetaDescargas.resolve(nombreArchivoExportado);

        // Carpeta destino del Flujo 4
        Path carpetaDestino = Path.of(
                System.getProperty("user.dir"),
                "Documentos",
                "Insumos Flujo 4",
                "Azteca_Descargados"
        ).toAbsolutePath().normalize();

        if (Files.exists(ruta)) {
            throw new IllegalStateException(
                    "Ya existe un archivo con ese nombre: " + ruta);
        }

        // Iniciar descarga
        elements.click(enlace);

        System.out.println("[OK] Clic en enlace de descarga");

        long limite = System.nanoTime()
                + Duration.ofMinutes(3).toNanos();

        long tamanoAnterior = -1;
        int estable = 0;

        automation.utils.AsyncRefresh updates = new automation.utils.AsyncRefresh(driver);
        while (System.nanoTime() < limite) {

            try {
                if (Files.isRegularFile(ruta)) {

                    long tamanoActual = Files.size(ruta);

                    if (tamanoActual > 0
                            && tamanoActual == tamanoAnterior) {
                        estable++;
                    } else {
                        estable = 0;
                    }

                    tamanoAnterior = tamanoActual;

                    if (estable >= 2) {

                        // Validar que el Excel esté completo
                        try (InputStream entrada =
                                     Files.newInputStream(ruta);
                             Workbook libro =
                                     new XSSFWorkbook(entrada)) {

                            if (libro.getNumberOfSheets() == 0) {
                                throw new IOException(
                                        "El archivo no contiene hojas");
                            }
                        }

                        Files.createDirectories(carpetaDestino);

                        Path destino = carpetaDestino.resolve(
                                nombreArchivoExportado);

                        if (Files.exists(destino)) {
                            throw new IOException(
                                    "Ya existe el archivo de destino: "
                                            + destino);
                        }

                        // Mover archivo al Flujo 4
                        descargaExportada = Files.move(
                                ruta,
                                destino);

                        System.out.println(
                                "[OK] Archivo guardado en: "
                                        + descargaExportada.toAbsolutePath());

                        return;
                    }
                }

                Thread.sleep(1000);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

                throw new IllegalStateException(
                        "Descarga interrumpida", e);

            } catch (IOException e) {

                throw new IllegalStateException(
                        "Error al validar o mover el Excel descargado", e);
            }
        }

        throw new AssertionError(
                "No se descargó el archivo: "
                        + nombreArchivoExportado);
    }




    public void agregarCreditosArchivoDescargado(
            String nombreTemplate) throws IOException {

        // Validar que el archivo AZTECA ya fue descargado
        if (descargaExportada == null
                || !Files.isRegularFile(descargaExportada)) {

            throw new IllegalStateException(
                    "Primero debe descargarse el archivo AZTECA");
        }

        // Ruta de los templates generados del Flujo 4
        Path archivoPromesas = Path.of(
                System.getProperty("user.dir"),
                "Documentos",
                "Insumos Flujo 4",
                "Template",
                "Generados",
                nombreTemplate
        ).toAbsolutePath().normalize();

        // Validar que exista el template
        if (!Files.isRegularFile(archivoPromesas)) {
            throw new IOException(
                    "No existe el template generado: "
                            + archivoPromesas);
        }

        System.out.println(
                "[EXCEL] Template seleccionado: "
                        + archivoPromesas.getFileName());

        // Agregar créditos al archivo AZTECA descargado
        int total = MovimientosCreditoUtils.agregarCreditos(
                descargaExportada,
                archivoPromesas);

        System.out.println(
                "[OK] Archivo AZTECA actualizado: "
                        + descargaExportada.getFileName());

        System.out.println(
                "[OK] Total de créditos copiados: " + total);
    }



    public void ingresarImportarMovimientos() {
        navigation.open(automation.utils.OdooMenu.IMPORTAR_MOVIMIENTOS);
    }

    public void crearNuevaImportacionMovimientos() {

        elements.click(MovimientosSelectors.NUEVA_IMPORTACION_MOVIMIENTOS);

        wait.until(ExpectedConditions.presenceOfElementLocated(
                MovimientosSelectors.ARCHIVO_IMPORTACION_MOVIMIENTOS));

        System.out.println("[OK] Nueva importación creada");
    }

    public void adjuntarArchivoMovimientosModificado() {

        if (descargaExportada == null ||
                !Files.isRegularFile(descargaExportada)) {

            throw new IllegalStateException(
                    "No existe el Excel modificado para importar");
        }

        WebElement input = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        MovimientosSelectors.ARCHIVO_IMPORTACION_MOVIMIENTOS));

        input.sendKeys(descargaExportada.toAbsolutePath().toString());

        System.out.println(
                "[OK] Archivo adjuntado: " +
                        descargaExportada.getFileName());
    }

    public void cargarMovimientosImportados() {

        elements.click(MovimientosSelectors.CARGAR_MOVIMIENTOS_IMPORTACION);

        System.out.println("[OK] Clic en Cargar movimientos");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                MovimientosSelectors.MODAL_CONFIRMACION_IMPORTACION));

        System.out.println("[OK] Confirmación de carga visible");
    }


    public void confirmarCargaMovimientos() {

        // Identificar el modal de carga de movimientos
        WebElement modal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        MovimientosSelectors.MODAL_CONFIRMACION_IMPORTACION));

        // Buscar el botón principal dentro del modal
        WebElement aceptar = wait.until(driver -> {
            WebElement ventana = driver.findElement(
                    MovimientosSelectors.MODAL_CONFIRMACION_IMPORTACION);

            WebElement boton = ventana.findElement(
                    MovimientosSelectors.ACEPTAR_IMPORTACION_MOVIMIENTOS);

            String texto = boton.getText().trim();

            if (boton.isDisplayed()
                    && boton.isEnabled()
                    && (texto.equalsIgnoreCase("Aceptar")
                    || texto.equalsIgnoreCase("De acuerdo"))) {
                return boton;
            }

            return null;
        });

        System.out.println(
                "[MODAL] Botón detectado: " + aceptar.getText().trim());

        elements.click(aceptar);

        System.out.println("[OK] Confirmación de carga aceptada");

        // Esperar a que desaparezca el modal
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                MovimientosSelectors.MODAL_CONFIRMACION_IMPORTACION));

        // Refrescar después de confirmar
        driver.navigate().refresh();

        System.out.println("[REFRESH] Página actualizada");
    }



    public void validarEstadoImportacionMovimientos() {

        long limite = System.nanoTime()
                + Duration.ofMinutes(15).toNanos();

        WebDriverWait espera = new WebDriverWait(
                driver, Duration.ofSeconds(30));

        automation.utils.AsyncRefresh updates = new automation.utils.AsyncRefresh(driver);
        while (System.nanoTime() < limite) {

            try {
                espera.until(
                        ExpectedConditions.presenceOfElementLocated(
                                MovimientosSelectors.TOTAL_FILAS));

                int total = Integer.parseInt(
                        driver.findElement(
                                        MovimientosSelectors.TOTAL_FILAS)
                                .getText().trim());

                int validas = Integer.parseInt(
                        driver.findElement(
                                        MovimientosSelectors.FILAS_VALIDAS)
                                .getText().trim());

                int rechazadas = Integer.parseInt(
                        driver.findElement(
                                        MovimientosSelectors.FILAS_RECHAZADAS)
                                .getText().trim());

                int procesadas = validas + rechazadas;

                System.out.println(
                        "[IMPORTACIÓN] Total: " + total
                                + " | Válidas: " + validas
                                + " | Rechazadas: " + rechazadas
                                + " | Analizadas: " + procesadas);

                if (total > 0 && procesadas == total) {

                    System.out.println(
                            "[OK] Análisis de importación finalizado");

                    System.out.println(
                            "[OK] Total de filas analizadas: " + total);

                    System.out.println(
                            "[OK] Movimientos válidos: " + validas);

                    System.out.println(
                            "[OK] Movimientos rechazados: " + rechazadas);

                    return;
                }

            } catch (TimeoutException e) {

                System.out.println(
                        "[ESPERANDO] Cargando análisis del archivo");

            } catch (StaleElementReferenceException e) {

                System.out.println(
                        "[ESPERANDO] Odoo actualizando los registros");

            }

            System.out.println(
                    "[ESPERANDO] Analizando movimientos...");

            updates.poll();
        }

        throw new AssertionError(
                "Tiempo agotado analizando la importacion de movimientos");
    }



    public void ingresarModuloCuentas() {
        ingresarCuentas();
    }

    public void seleccionarGeneralOperaciones() {
        new CuentasPage(driver).seleccionarCuenta("General Operaciones");
        wait.until(ExpectedConditions.elementToBeClickable(MovimientosSelectors.CONCILIAR_IMPORTADOS));
    }

    public void abrirConciliacionImportados() {
        elements.click(MovimientosSelectors.CONCILIAR_IMPORTADOS);
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div[role='dialog'] table th[data-name='file_name']")));
    }

    public void seleccionarArchivoConciliacion() {
        if (nombreArchivoExportado == null) throw new IllegalStateException("No hay archivo de esta ejecucion");
        By rows = MovimientosSelectors.FILAS_ARCHIVOS_CONCILIACION;
        wait.until(d -> d.findElements(rows).stream().anyMatch(row ->
                row.findElement(By.cssSelector("td[name='file_name']")).getText().trim().equals(nombreArchivoExportado)));
        for (WebElement row : driver.findElements(rows)) {
            String name = row.findElement(By.cssSelector("td[name='file_name']")).getText().trim();
            boolean required = name.equals(nombreArchivoExportado);
            setSelected(name, required);
        }
        wait.until(d -> {
            int selected = 0;
            for (WebElement row : d.findElements(rows)) {
                String name = row.findElement(By.cssSelector("td[name='file_name']")).getText().trim();
                var boxes = row.findElements(By.cssSelector("td[name='selected'] input[type='checkbox']"));
                if (!boxes.isEmpty() && boxes.get(0).isSelected()) {
                    if (!name.equals(nombreArchivoExportado)) return false;
                    selected++;
                }
            }
            return selected == 1;
        });
        System.out.println("[OK] Unicamente seleccionado: " + nombreArchivoExportado);
    }

    private void setSelected(String name, boolean required) {
        By rowSelector = By.xpath("//div[@role='dialog']//tr[contains(@class,'o_data_row')][td[@name='file_name' and normalize-space()=" + xpathLiteral(name) + "]]");
        wait.ignoring(StaleElementReferenceException.class).until(d -> {
            WebElement row = d.findElement(rowSelector);
            var boxes = row.findElements(By.cssSelector("td[name='selected'] input[type='checkbox']"));
            if (!boxes.isEmpty() && boxes.get(0).isSelected() == required) return true;
            WebElement cell = row.findElement(By.cssSelector("td[name='selected']"));
            if (boxes.isEmpty() || !boxes.get(0).isEnabled()) cell.click();
            else boxes.get(0).click();
            return false;
        });
    }

    private static String xpathLiteral(String value) {
        if (!value.contains("'")) return "'" + value + "'";
        if (!value.contains("\"")) return "\"" + value + "\"";
        return "concat('" + value.replace("'", "',\"'\",'") + "')";
    }

    public void confirmarConciliacion() {
        inicioConciliacion = System.nanoTime();
        elements.click(MovimientosSelectors.CONFIRMAR_CONCILIACION);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table th[data-name='progreso_pct']")));
    }

    public void validarConciliacionCompleta() {
        if (inicioConciliacion == 0) throw new IllegalStateException("No se inicio conciliacion");
        long deadline = System.nanoTime() + Duration.ofMinutes(60).toNanos();
        automation.utils.AsyncRefresh updates = new automation.utils.AsyncRefresh(driver);
        while (System.nanoTime() < deadline) {
            try {
                for (WebElement row : driver.findElements(MovimientosSelectors.FILAS_PROCESO_CONCILIACION)) {
                    String name = row.findElement(By.cssSelector("td[name='name']")).getText().trim();
                    if (procesoConciliacion == null) procesoConciliacion = name;
                    if (!procesoConciliacion.equals(name)) continue;
                    WebElement progress = row.findElement(MovimientosSelectors.COLUMNA_AVANCE);
                    String percent = progress.getText().replace("%", "").replace(",", ".").trim();
                    var bars = progress.findElements(By.cssSelector("[role='progressbar'], .progress-bar"));
                    if (!bars.isEmpty() && bars.get(0).getDomAttribute("aria-valuenow") != null)
                        percent = bars.get(0).getDomAttribute("aria-valuenow").trim();
                    String state = row.findElement(MovimientosSelectors.COLUMNA_ESTADO_PROCESO).getText().trim();
                    System.out.println("[CONCILIACION] " + name + " | " + percent + "% | " + state);
                    if (state.equalsIgnoreCase("Fallido")) throw new AssertionError("Conciliacion fallida: " + name);
                    if (percent.matches("100(?:\\.0+)?") && state.equalsIgnoreCase("Conciliado")) {
                        finConciliacion = System.nanoTime();
                        elements.highlight(MovimientosSelectors.COLUMNA_AVANCE);
                        guardarCsvConciliacion(name, segundos(inicioConciliacion, finConciliacion), "EXITO");
                        return;
                    }
                    break;
                }
            } catch (StaleElementReferenceException | NoSuchElementException updating) {
                System.out.println("[ESPERANDO] Actualizando proceso de conciliacion");
            }
            updates.poll();
        }
        throw new AssertionError("La conciliacion no alcanzo 100% y Conciliado en 60 minutos");
    }

    private void guardarCsvConciliacion(
            String proceso,
            double duracionSegundos,
            String estado) {

        Path reporte = Path.of(
                "output", "metricas_conciliacion_cp04.csv");

        String encabezado =
                "Fecha,Proceso,DuracionSeg,Estado\n";

        String fecha = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern(
                        "yyyy-MM-dd HH:mm:ss"));

        String fila = String.format(
                Locale.ROOT,
                "%s,%s,%.3f,%s%n",
                fecha,
                proceso,
                duracionSegundos,
                estado);

        synchronized (CSV_LOCK) {

            try {
                Files.createDirectories(reporte.getParent());

                if (Files.notExists(reporte)) {
                    Files.writeString(
                            reporte,
                            encabezado,
                            StandardOpenOption.CREATE_NEW);
                }

                Files.writeString(
                        reporte,
                        fila,
                        StandardOpenOption.APPEND);

            } catch (IOException e) {
                throw new IllegalStateException(
                        "No se pudo guardar la conciliación CP04",
                        e);
            }
        }

        System.out.println(
                "[CSV] Conciliación guardada en: "
                        + reporte.toAbsolutePath());
    }



}
