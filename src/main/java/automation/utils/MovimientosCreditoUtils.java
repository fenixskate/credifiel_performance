
package automation.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MovimientosCreditoUtils {


    public static int agregarCreditos(
            Path archivoDescargado,
            Path archivoPromesas) throws IOException {

        if (!Files.isRegularFile(archivoDescargado)
                || !Files.isRegularFile(archivoPromesas)) {
            throw new IOException(
                    "No se encontró el archivo descargado o el template");
        }

        Path temporal = Files.createTempFile(
                archivoDescargado.getParent(),
                "azteca_creditos_", ".xlsx");

        int totalCreditos = 0;

        try {

            try (InputStream entradaPromesas =
                         Files.newInputStream(archivoPromesas);
                 InputStream entradaDescarga =
                         Files.newInputStream(archivoDescargado);
                 Workbook promesas = new XSSFWorkbook(entradaPromesas);
                 Workbook descargado = new XSSFWorkbook(entradaDescarga)) {

                Sheet hojaPromesas = promesas.getSheetAt(0);
                Sheet hojaDescargada = descargado.getSheetAt(0);

                DataFormatter formatter = new DataFormatter();

                int filaEncabezadoPromesas =
                        buscarFilaEncabezado(
                                hojaPromesas, "num_credito");

                int filaEncabezadoDescarga =
                        buscarFilaEncabezado(
                                hojaDescargada, "Crédito");

                int columnaOrigen = buscarColumna(
                        hojaPromesas.getRow(filaEncabezadoPromesas),
                        "num_credito");

                int columnaDestino = buscarColumna(
                        hojaDescargada.getRow(filaEncabezadoDescarga),
                        "Crédito");

                List<String> creditos = new ArrayList<>();

                // Obtener créditos del template
                for (int i = filaEncabezadoPromesas + 1;
                     i <= hojaPromesas.getLastRowNum(); i++) {

                    Row fila = hojaPromesas.getRow(i);

                    if (fila == null) {
                        continue;
                    }

                    String credito = formatter.formatCellValue(
                            fila.getCell(columnaOrigen)).trim();

                    if (!credito.isEmpty()) {
                        creditos.add(credito);
                    }
                }

                List<Row> filasDestino = new ArrayList<>();

                // Obtener filas del Excel descargado
                for (int i = filaEncabezadoDescarga + 1;
                     i <= hojaDescargada.getLastRowNum(); i++) {

                    Row fila = hojaDescargada.getRow(i);

                    if (fila == null) {
                        continue;
                    }

                    boolean tieneDatos = false;

                    for (Cell celda : fila) {

                        if (!formatter.formatCellValue(celda)
                                .trim().isEmpty()) {
                            tieneDatos = true;
                            break;
                        }
                    }

                    if (tieneDatos) {
                        filasDestino.add(fila);
                    }
                }

                // Validar cantidad de registros

// Obtener la cantidad de registros del archivo AZTECA
                int totalFilasDestino = filasDestino.size();
                int totalCreditosDisponibles = creditos.size();

                System.out.println(
                        "[EXCEL] Registros archivo AZTECA: "
                                + totalFilasDestino);

                System.out.println(
                        "[EXCEL] Créditos disponibles en template: "
                                + totalCreditosDisponibles);

// Validar que existan créditos suficientes
                if (totalCreditosDisponibles < totalFilasDestino) {
                    throw new IOException(
                            "Créditos insuficientes. AZTECA requiere "
                                    + totalFilasDestino
                                    + " y el template contiene "
                                    + totalCreditosDisponibles);
                }

// Copiar únicamente los créditos necesarios
                for (int i = 0; i < totalFilasDestino; i++) {

                    Row fila = filasDestino.get(i);

                    Cell celda = fila.getCell(columnaDestino);

                    if (celda == null) {
                        celda = fila.createCell(columnaDestino);
                    }

                    celda.setCellValue(creditos.get(i));
                }

                totalCreditos = totalFilasDestino;

                System.out.println(
                        "[OK] Créditos copiados: " + totalCreditos);

                // Guardar primero en el archivo temporal
                try (OutputStream salida =
                             Files.newOutputStream(temporal)) {

                    descargado.write(salida);
                }

            } // AQUÍ SE CIERRAN LOS WORKBOOK Y LOS INPUTSTREAM

            // Reemplazar el Excel cuando ya está cerrado

            boolean actualizado = false;
            IOException ultimoError = null;

            for (int intento = 1; intento <= 10; intento++) {

                try {
                    Files.move(
                            temporal,
                            archivoDescargado,
                            StandardCopyOption.REPLACE_EXISTING);

                    actualizado = true;

                    System.out.println(
                            "[OK] Archivo actualizado en intento " + intento);

                    break;

                } catch (FileSystemException e) {

                    ultimoError = e;

                    System.out.println(
                            "[ESPERANDO] Archivo bloqueado. Intento "
                                    + intento + "/10");

                    if (intento < 10) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();

                            throw new IOException(
                                    "Actualización del Excel interrumpida", ex);
                        }
                    }
                }
            }

            if (!actualizado) {
                throw new IOException(
                        "No se pudo actualizar el archivo después de 10 intentos: "
                                + archivoDescargado,
                        ultimoError);
            }


            System.out.println(
                    "[OK] Créditos agregados: " + totalCreditos);

            System.out.println(
                    "[OK] Excel actualizado: " +
                            archivoDescargado.toAbsolutePath());

            return totalCreditos;

        } finally {

            // Limpiar temporal solamente si todavía existe
            try {
                Files.deleteIfExists(temporal);
            } catch (IOException e) {
                System.err.println(
                        "[WARN] No se pudo eliminar temporal: " + temporal);
            }
        }
    }


    private static int buscarFilaEncabezado(
            Sheet hoja, String nombre) throws IOException {

        int limite = Math.min(hoja.getLastRowNum(), 30);

        for (int i = 0; i <= limite; i++) {
            Row fila = hoja.getRow(i);

            if (fila != null
                    && buscarColumna(fila, nombre) >= 0) {
                return i;
            }
        }

        throw new IOException(
                "No se encontró el encabezado: " + nombre);
    }

    private static int buscarColumna(
            Row fila, String nombre) {

        DataFormatter formatter = new DataFormatter();

        for (Cell celda : fila) {

            String encabezado = normalizar(
                    formatter.formatCellValue(celda));

            if (encabezado.equals(normalizar(nombre))) {
                return celda.getColumnIndex();
            }
        }

        return -1;
    }

    private static String normalizar(String valor) {
        return Normalizer.normalize(
                        valor.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }
}
