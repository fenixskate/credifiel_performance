package verification;

import automation.driver.DriverManager;
import automation.utils.MovimientosCreditoUtils;
import automation.pages.MovimientosPage;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;

/** Validaciones locales de exportacion, contadores y descargas; no accede a Odoo. */
public final class ExtrajudicialArchivosSmoke {
    public static void main(String[] args) throws Exception {
        System.setProperty("browser", "chrome");
        System.setProperty("headless", "true");
        Path carpeta = Files.createTempDirectory(Path.of("target"), "extrajudicial-archivos-").toAbsolutePath();
        Path real = Path.of("Documentos/Insumos Flujo 4/Azteca_Descargados/Movimientos_Conciliados_20261107_005439.xlsx");
        byte[] original = Files.readAllBytes(real);
        Path copia = carpeta.resolve("exportacion-mixta.xlsx");
        Files.copy(real, copia);
        LocalDate fecha = null;
        LocalDateTime importacion = LocalDateTime.MIN;
        try (InputStream in = Files.newInputStream(real); Workbook libro = WorkbookFactory.create(in)) {
            Sheet hoja = libro.getSheetAt(0);
            for (Row fila : hoja) {
                if (fila.getRowNum() == 0 || !new DataFormatter().formatCellValue(fila.getCell(1)).equalsIgnoreCase("Extrajudicial | Azteca 8646")) continue;
                fecha = fila.getCell(3).getLocalDateTimeCellValue().toLocalDate();
                LocalDateTime actual = fila.getCell(4).getLocalDateTimeCellValue();
                if (actual.isAfter(importacion)) importacion = actual;
            }
        }
        int excluidos = MovimientosCreditoUtils.filtrarExportacion(copia, "Extrajudicial | Azteca 8646", fecha, importacion.minusSeconds(1), 500);
        if (excluidos != 43) throw new AssertionError("Se esperaban 43 movimientos ajenos/anteriores, se excluyeron " + excluidos);
        int asignados = MovimientosCreditoUtils.agregarCreditos(copia, Path.of("Documentos/Insumos Flujo 4/Template/Generados/Template de Carga de Promesas v2_3.xlsx"));
        if (asignados != 500 || !Arrays.equals(original, Files.readAllBytes(real))) throw new AssertionError("Cantidad incorrecta o evidencia original alterada");
        byte[] antes = Files.readAllBytes(copia);
        try {
            MovimientosCreditoUtils.filtrarExportacion(copia, "Extrajudicial | Azteca 8646", fecha, importacion.plusDays(1), 500);
            throw new AssertionError("Se acepto una exportacion incompleta");
        } catch (IOException esperado) {
            if (!esperado.getMessage().contains("0 movimientos")) throw esperado;
        }
        if (!Arrays.equals(antes, Files.readAllBytes(copia))) throw new AssertionError("Se modifico una exportacion rechazada");
        System.out.println("PASS: exportacion real de 543 filtrada a los 500 de esta carga; 43 excluidos; 500 creditos asignados");

        var lecturas = new java.util.concurrent.atomic.AtomicInteger();
        var servidor = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        servidor.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            byte[] body;
            if (path.equals("/descarga")) {
                body = exchange.getRequestURI().getQuery().getBytes(java.nio.charset.StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/octet-stream");
                exchange.getResponseHeaders().set("Content-Disposition", "attachment; filename=igual.xlsx");
            } else if (path.equals("/contadores")) {
                int procesadas = lecturas.getAndIncrement() == 0 ? 40 : 500;
                String html = "<div class='o_form_view'><span>Procesando</span>"
                        + "<div name='total_transacciones_archivo'>500</div><div name='total_transacciones_cargadas'>500</div>"
                        + "<div name='total_transacciones_importadas'>" + procesadas + "</div>"
                        + "<div name='total_transacciones_duplicadas'>0</div><div name='total_transacciones_fallidas'>0</div>"
                        + "<table><tbody>" + "<tr class='o_data_row'><td name='estatus'>Importado</td></tr>".repeat(40) + "</tbody></table></div>";
                body = html.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
            } else {
                body = ("<a id='descargar' href='/descarga?" + exchange.getRequestURI().getQuery() + "'>Descargar</a>").getBytes(java.nio.charset.StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
            }
            exchange.sendResponseHeaders(200, body.length);
            try (var out = exchange.getResponseBody()) { out.write(body); }
        });
        servidor.start();
        String base = "http://127.0.0.1:" + servidor.getAddress().getPort();
        var pool = Executors.newFixedThreadPool(2);
        try {
            var gate = new CyclicBarrier(2);
            List<Future<Path>> trabajos = new ArrayList<>();
            for (String usuario : List.of("usuario1", "usuario2")) trabajos.add(pool.submit(() -> {
                try {
                    DriverManager.start();
                    var driver = DriverManager.getDriver();
                    Path downloads = DriverManager.downloadDirectory(driver);
                    driver.get(base + "/inicio?" + usuario);
                    gate.await(30, TimeUnit.SECONDS);
                    driver.findElement(By.id("descargar")).click();
                    new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(15)).until(d -> Files.isRegularFile(downloads.resolve("igual.xlsx")));
                    if (!Files.readString(downloads.resolve("igual.xlsx")).equals(usuario)) throw new AssertionError("Se mezclaron las descargas");
                    return downloads;
                } finally { DriverManager.quit(); }
            }));
            Path primera = trabajos.get(0).get(), segunda = trabajos.get(1).get();
            if (primera.equals(segunda)) throw new AssertionError("Los navegadores comparten descargas");
            System.out.println("PASS: dos navegadores descargan igual.xlsx simultaneamente sin mezclar archivos");
            DriverManager.start();
            var driver = DriverManager.getDriver();
            driver.get(base + "/contadores");
            new MovimientosPage(driver).validarTransaccionesImportadas();
            if (lecturas.get() != 2) throw new AssertionError("Se aceptaron las 40 filas visibles sin esperar los 500 importados");
            System.out.println("PASS: espera los 500 importados aunque solo haya 40 filas visibles y el layout siga Procesando");
        } finally { DriverManager.quit(); pool.shutdownNow(); servidor.stop(0); }
    }
}
