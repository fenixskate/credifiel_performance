
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class MovimientosCreditoUtils {

    public static int filtrarExportacion(Path archivo, String cuenta, LocalDate fecha,
                                         LocalDateTime importadaDesde, int esperados) throws IOException {
        if (esperados < 1) throw new IllegalArgumentException("Layout sin movimientos importados");
        Path temporal = Files.createTempFile(archivo.getParent(), "movimientos_filtrados_", ".xlsx");
        int eliminados = 0;
        try {
            try (InputStream entrada = Files.newInputStream(archivo); Workbook libro = new XSSFWorkbook(entrada)) {
                Sheet hoja = libro.getSheetAt(0);
                int encabezado = buscarFilaEncabezado(hoja, "ID Movimiento");
                int id = buscarColumna(hoja.getRow(encabezado), "ID Movimiento");
                int propietario = buscarColumna(hoja.getRow(encabezado), "Cuenta");
                int dia = buscarColumna(hoja.getRow(encabezado), "Fecha");
                int importacion = buscarColumna(hoja.getRow(encabezado), "Fecha Importación");
                if (propietario < 0 || dia < 0 || (importadaDesde != null && importacion < 0))
                    throw new IOException("La exportacion no contiene Cuenta, Fecha o Fecha Importacion");
                DataFormatter formato = new DataFormatter();
                Set<String> ids = new HashSet<>();
                List<Integer> excluir = new ArrayList<>();
                for (int i = encabezado + 1; i <= hoja.getLastRowNum(); i++) {
                    Row fila = hoja.getRow(i);
                    if (fila == null) continue;
                    String movimiento = formato.formatCellValue(fila.getCell(id)).trim();
                    if (movimiento.isEmpty()) continue;
                    boolean propia = normalizar(formato.formatCellValue(fila.getCell(propietario))).equals(normalizar(cuenta));
                    if (propia) propia = fechaExcel(fila.getCell(dia)).toLocalDate().equals(fecha);
                    if (propia && importadaDesde != null)
                        propia = !fechaExcel(fila.getCell(importacion)).isBefore(importadaDesde);
                    if (propia) {
                        if (!ids.add(movimiento)) throw new IOException("Movimiento duplicado en exportacion: " + movimiento);
                    } else excluir.add(i);
                }
                if (ids.size() != esperados)
                    throw new IOException("La exportacion de " + cuenta + " para " + fecha + " contiene "
                            + ids.size() + " movimientos de esta carga; se esperaban " + esperados);
                for (int i = excluir.size() - 1; i >= 0; i--) {
                    int fila = excluir.get(i), ultima = hoja.getLastRowNum();
                    hoja.removeRow(hoja.getRow(fila));
                    if (fila < ultima) hoja.shiftRows(fila + 1, ultima, -1);
                }
                eliminados = excluir.size();
                if (eliminados > 0) try (OutputStream salida = Files.newOutputStream(temporal)) { libro.write(salida); }
            }
            if (eliminados > 0) Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING);
            return eliminados;
        } finally { Files.deleteIfExists(temporal); }
    }

    private static LocalDateTime fechaExcel(Cell celda) throws IOException {
        if (celda == null || celda.getCellType() == CellType.BLANK) throw new IOException("Movimiento sin fecha");
        if (celda.getCellType() == CellType.NUMERIC) return celda.getLocalDateTimeCellValue();
        String texto = new DataFormatter().formatCellValue(celda).trim();
        try {
            if (texto.matches("\\d{4}-\\d{2}-\\d{2}")) return LocalDate.parse(texto).atStartOfDay();
            if (texto.matches("\\d{2}/\\d{2}/\\d{4}"))
                return LocalDate.parse(texto, DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay();
            return LocalDateTime.parse(texto.replace(' ', 'T'), DateTimeFormatter.ISO_DATE_TIME);
        } catch (java.time.format.DateTimeParseException e) { throw new IOException("Fecha invalida en exportacion: " + celda.getAddress(), e); }
    }


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
