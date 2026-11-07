
package automation.pages;

import automation.selectors.PromesasSelectors;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;


public class PromesasPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final automation.utils.ElementActions elements;

    private long inicioCarga;
    private long inicioLectura;
    private long finLectura;
    private long inicioProcesamiento;

    private String archivoCargado;
    private int registrosProcesados;
    private String usuarioEjecucion;



    // Cantidad de registros del HTML compartido.
    // Ajustar si cambia el template.
    private static final int REGISTROS_ESPERADOS = 500;

    private static final Duration INTERVALO =
            Duration.ofSeconds(5);

    public PromesasPage(WebDriver driver) {
        this.driver = driver;
        this.elements = new automation.utils.ElementActions(driver);
        this.wait = new WebDriverWait(
                driver, Duration.ofSeconds(30));
    }



    public void ingresarModulo() {

        WebDriverWait espera = new WebDriverWait(
                driver, Duration.ofSeconds(45));

        espera.pollingEvery(Duration.ofMillis(500));

        new automation.utils.OdooNavigation(driver).open(
                automation.utils.OdooMenu.PROMESAS);

        // Esperar la vista de listado
        elements.click(PromesasSelectors.NUEVO);

        // Esperar formulario de carga
        espera.until(ExpectedConditions.presenceOfElementLocated(
                PromesasSelectors.SUBIR_ARCHIVO));

        System.out.println("[OK] Formulario de promesas abierto");
    }


    public void subirArchivo(String nombreArchivo) {

        Path ruta = Path.of(
                System.getProperty("user.dir"),
                "Documentos",
                "Insumos Flujo 4",
                "Template",
                "Generados",
                nombreArchivo
        ).toAbsolutePath().normalize();

        if (!Files.isRegularFile(ruta)) {
            throw new IllegalArgumentException(
                    "Archivo no encontrado: " + ruta);
        }

        WebElement input = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        PromesasSelectors.SUBIR_ARCHIVO));

        archivoCargado = nombreArchivo;
        inicioCarga = System.nanoTime();

        input.sendKeys(ruta.toString());

        System.out.println(
                "[OK] Archivo cargado: " + archivoCargado);

        System.out.println(
                "[RUTA] " + ruta);
    }

    public void leerArchivo() {

        inicioLectura = System.nanoTime();

        elements.click(PromesasSelectors.LEER_ARCHIVO);

        esperarFilas();

        System.out.println("[OK] Tabla generada");
    }


    private List<WebElement> esperarFilas() {

        WebDriverWait espera = new WebDriverWait(
                driver, Duration.ofSeconds(90));

        espera.pollingEvery(Duration.ofMillis(500));

        final String[] ultimoEstado = {""};

        try {
            return espera.until(d -> {

                try {
                    List<WebElement> totales = d.findElements(
                            PromesasSelectors.TOTAL_PROMESAS);

                    List<WebElement> rangos = d.findElements(
                            PromesasSelectors.RANGO_PROMESAS);

                    List<WebElement> filas = d.findElements(
                            PromesasSelectors.FILAS_PROMESAS);

                    String totalTexto = totales.isEmpty()
                            ? "NO ENCONTRADO"
                            : totales.get(0).getText().trim();

                    String rangoTexto = rangos.isEmpty()
                            ? "NO ENCONTRADO"
                            : rangos.get(0).getText().trim();

                    String estado = String.format(
                            "Total=%s | Rango=%s | FilasDOM=%d",
                            totalTexto,
                            rangoTexto,
                            filas.size());

                    if (!estado.equals(ultimoEstado[0])) {
                        System.out.println("[DIAGNOSTICO] " + estado);
                        ultimoEstado[0] = estado;
                    }

                    if (totales.isEmpty() || rangos.isEmpty()) {
                        return null;
                    }

                    int total = Integer.parseInt(
                            totalTexto.replaceAll("[^0-9]", ""));

                    if (total != REGISTROS_ESPERADOS) {
                        return null;
                    }

                    String[] limites = rangoTexto.split("\\s*-\\s*");

                    if (limites.length != 2) {
                        return null;
                    }

                    int desde = Integer.parseInt(limites[0].trim());
                    int hasta = Integer.parseInt(limites[1].trim());

                    int cantidadEsperada = hasta - desde + 1;

                    if (filas.size() != cantidadEsperada) {
                        return null;
                    }

                    for (WebElement fila : filas) {

                        if (fila.findElements(
                                PromesasSelectors.ESTATUS_FILA).isEmpty()) {
                            return null;
                        }
                    }

                    return filas;

                } catch (StaleElementReferenceException |
                         NoSuchElementException |
                         NumberFormatException e) {

                    return null;
                }
            });

        } catch (TimeoutException e) {

            throw new AssertionError(
                    "No se pudo cargar la tabla de promesas. "
                            + "Último estado: " + ultimoEstado[0],
                    e);
        }
    }


    private void validarEstados(
            String estadoEsperado,
            Duration tiempoMaximo,
            boolean refrescar) {

        long limite = System.nanoTime()
                + tiempoMaximo.toNanos();

        automation.utils.AsyncRefresh updates = new automation.utils.AsyncRefresh(driver);
        while (System.nanoTime() < limite) {

            try {

                List<WebElement> filas = esperarFilas();

                int correctos = 0;
                int pendientes = 0;
                int procesando = 0;
                int fallidos = 0;

                StringBuilder errores = new StringBuilder();

                for (WebElement fila : filas) {

                    String nombre = fila.findElement(
                                    PromesasSelectors.NOMBRE_FILA)
                            .getText().trim();

                    String estado = fila.findElement(
                                    PromesasSelectors.ESTATUS_FILA)
                            .getText().trim();

                    String mensaje = fila.findElement(
                                    PromesasSelectors.MENSAJE_FILA)
                            .getText().trim();

                    String normalizado = estado.toLowerCase(
                            Locale.ROOT);

                    if (estado.equalsIgnoreCase(
                            estadoEsperado)) {
                        correctos++;
                    }

                    switch (normalizado) {
                        case "pendiente" -> pendientes++;
                        case "procesando" -> procesando++;
                        case "fallido" -> {
                            fallidos++;
                            errores.append("\n")
                                    .append(nombre)
                                    .append(": ")
                                    .append(mensaje);
                        }
                    }
                }

                System.out.printf(
                        "[PROMESAS] Total=%d | %s=%d | Pendiente=%d"
                                + " | Procesando=%d | Fallido=%d%n",
                        filas.size(),
                        estadoEsperado,
                        correctos,
                        pendientes,
                        procesando,
                        fallidos);

                if (fallidos > 0) {
                    throw new AssertionError(
                            "Existen promesas fallidas:"
                                    + errores);
                }

                if (correctos == REGISTROS_ESPERADOS) {
                    System.out.println(
                            "[OK] Todas las promesas están en "
                                    + estadoEsperado);
                    return;
                }

                // Si buscamos Pendiente, un estado Exitoso
                // o Procesando es inesperado antes de procesar.
                if (estadoEsperado.equalsIgnoreCase(
                        "Pendiente")) {

                    if (procesando > 0) {
                        throw new AssertionError(
                                "Hay promesas procesándose antes "
                                        + "de presionar Procesar Promesas");
                    }

                    if (correctos < REGISTROS_ESPERADOS
                            && !refrescar) {
                        throw new AssertionError(
                                "No todas las promesas están pendientes");
                    }
                }

            } catch (StaleElementReferenceException e) {
                System.out.println(
                        "[ESPERANDO] Odoo actualizó la tabla");
            }

            if (refrescar) {
                dormir(INTERVALO);
                driver.navigate().refresh();
                System.out.println("[REFRESH] Actualizando promesas...");
            } else {
                break;
            }
        }

        throw new AssertionError(
                "Tiempo agotado esperando el estado: "
                        + estadoEsperado);
    }

    public void validarPendientes() {

        By totalRegistros = By.cssSelector(
                "div[name='line_count'] span");

        WebDriverWait espera = new WebDriverWait(
                driver, Duration.ofSeconds(60));

        espera.until(d -> {
            try {
                String texto = d.findElement(totalRegistros)
                        .getText().trim();

                return Integer.parseInt(texto) == REGISTROS_ESPERADOS;

            } catch (StaleElementReferenceException |
                     NoSuchElementException |
                     NumberFormatException e) {
                return false;
            }
        });

        // Validar solamente las filas visibles, sin paginar
        List<WebElement> filas = esperarFilas();

        int pendientes = 0;

        for (WebElement fila : filas) {

            String estado = fila.findElement(
                            PromesasSelectors.ESTATUS_FILA)
                    .getText().trim();

            if (estado.equalsIgnoreCase("Pendiente")) {
                pendientes++;
            }
        }

        if (pendientes != filas.size()) {
            throw new AssertionError(
                    "No todas las promesas visibles están Pendientes");
        }

        finLectura = System.nanoTime();

        System.out.println(
                "[OK] Total de promesas cargadas: "
                        + REGISTROS_ESPERADOS);

        System.out.println(
                "[OK] Promesas visibles pendientes: "
                        + pendientes);

        System.out.println(
                "[TIEMPO] Lectura y preparación: "
                        + segundos(inicioLectura, finLectura) + " s");
    }



    public void procesarPromesas() {

        WebElement boton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        PromesasSelectors.PROCESAR_PROMESAS));

        // Iniciar medición justo antes de hacer clic
        inicioProcesamiento = System.nanoTime();

        elements.click(boton);

        System.out.println(
                "[TIEMPO] Iniciando medición de procesamiento CP03");
    }





    public void validarExitosas() {

        if (inicioProcesamiento == 0) {
            throw new IllegalStateException(
                    "No se inició el cronómetro de procesamiento");
        }

        WebDriverWait espera = new WebDriverWait(
                driver, Duration.ofSeconds(30));

        long limite = System.nanoTime()
                + Duration.ofMinutes(15).toNanos();

        int total = 0;
        int exitosos = 0;
        int fallidos = 0;

        automation.utils.AsyncRefresh updates = new automation.utils.AsyncRefresh(driver, Duration.ofSeconds(2));
        while (System.nanoTime() < limite) {

            try {
                espera.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                PromesasSelectors.TOTAL_REGISTROS));

                total = Integer.parseInt(
                        driver.findElement(
                                        PromesasSelectors.TOTAL_REGISTROS)
                                .getText().trim());

                exitosos = Integer.parseInt(
                        driver.findElement(
                                        PromesasSelectors.REGISTROS_EXITOSOS)
                                .getText().trim());

                fallidos = Integer.parseInt(
                        driver.findElement(
                                        PromesasSelectors.REGISTROS_FALLIDOS)
                                .getText().trim());

                int procesados = exitosos + fallidos;

                double transcurrido = segundos(
                        inicioProcesamiento,
                        System.nanoTime());

                System.out.printf(
                        Locale.ROOT,
                        "[PROMESAS] Usuario=%s | Total=%d"
                                + " | Exitosos=%d | Fallidos=%d"
                                + " | Procesados=%d/%d | Tiempo=%.3f s%n",
                        usuarioEjecucion,
                        total,
                        exitosos,
                        fallidos,
                        procesados,
                        total,
                        transcurrido);

                if (total != REGISTROS_ESPERADOS) {
                    throw new AssertionError(
                            "Se esperaban " + REGISTROS_ESPERADOS
                                    + " promesas, pero Odoo reportó "
                                    + total);
                }

                if (exitosos < 0 || fallidos < 0 || procesados > total) {
                    throw new AssertionError(
                            "Los registros procesados superan el total");
                }

                if (procesados == total) {

                    long finProcesamiento = System.nanoTime();

                    registrosProcesados = procesados;

                    double duracion = segundos(
                            inicioProcesamiento,
                            finProcesamiento);

                    System.out.printf(
                            Locale.ROOT,
                            "[OK] Procesamiento finalizado"
                                    + " | Usuario=%s"
                                    + " | Total=%d"
                                    + " | Exitosos=%d"
                                    + " | Fallidos=%d"
                                    + " | Duración=%.3f s%n",
                            usuarioEjecucion,
                            total,
                            exitosos,
                            fallidos,
                            duracion);

                    guardarMetricasCSV(
                            total,
                            exitosos,
                            fallidos,
                            duracion);

                    // Regla de negocio: se acepta la mezcla de exitosas y fallidas.
                    // Las 500 filas deben haber terminado de procesarse.
                    return;
                }

            } catch (StaleElementReferenceException |
                     NoSuchElementException e) {

                System.out.println(
                        "[ESPERANDO] Odoo actualizó los contadores");
            }

            // Reconsultar cada 2 segundos hasta completar las 500 filas.
            updates.poll();


        }

        throw new AssertionError(
                "Tiempo agotado esperando el procesamiento."
                        + " Total=" + total
                        + " | Exitosos=" + exitosos
                        + " | Fallidos=" + fallidos);
    }



    private void dormir(Duration duracion) {
        try {
            Thread.sleep(duracion.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Espera interrumpida", e);
        }
    }



    private double segundos(long inicio, long fin) {
        return (fin - inicio) / 1_000_000_000.0;
    }


    private static synchronized void escribirCSV(String fila)
            throws IOException {

        Path archivoRun = Path.of(
                "target", "current-run.txt");

        String verificationDirectory = System.getProperty("verification.metrics.directory");
        if (verificationDirectory == null && !Files.isRegularFile(archivoRun)) {
            throw new IOException(
                    "No existe el archivo de la ejecución: "
                            + archivoRun);
        }

        Path carpeta = verificationDirectory == null ? Path.of(Files.readString(archivoRun).trim())
                : Path.of(verificationDirectory);

        if (!Files.isDirectory(carpeta)) {
            throw new IOException(
                    "No existe la carpeta de reportes: "
                            + carpeta);
        }

        Path ruta = carpeta.resolve("metricas_cp03.csv");

        if (Files.notExists(ruta)) {
            Files.writeString(
                    ruta,
                    "Fecha,Usuario,Archivo,TotalRegistros,"
                            + "Exitosos,Fallidos,"
                            + "DuracionSegundos,Estado"
                            + System.lineSeparator(),
                    StandardOpenOption.CREATE_NEW);
        }

        Files.writeString(
                ruta,
                fila,
                StandardOpenOption.APPEND);

        System.out.println(
                "[CSV] Métricas CP03 guardadas en: "
                        + ruta.toAbsolutePath());
    }

    private void guardarMetricasCSV(
            int total,
            int exitosos,
            int fallidos,
            double duracion) {

        try {

            String usuario = usuarioEjecucion == null
                    ? "NO_DEFINIDO"
                    : usuarioEjecucion;

            String archivo = archivoCargado == null
                    ? "NO_DEFINIDO"
                    : archivoCargado;

            String fecha = java.time.LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern(
                            "yyyy-MM-dd HH:mm:ss"));

            String fila = String.format(
                    Locale.ROOT,
                    "\"%s\",\"%s\",\"%s\",%d,%d,%d,%.3f,COMPLETADO%n",
                    fecha,
                    usuario.replace("\"", "\"\""),
                    archivo.replace("\"", "\"\""),
                    total,
                    exitosos,
                    fallidos,
                    duracion);

            escribirCSV(fila);

        } catch (IOException e) {

            throw new IllegalStateException(
                    "No se pudo guardar el CSV CP03", e);
        }
    }


    public void establecerUsuario(String usuario) {
        this.usuarioEjecucion = usuario;
    }

}
