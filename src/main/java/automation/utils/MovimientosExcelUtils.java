
package automation.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.text.Normalizer;
import java.util.*;

public final class MovimientosExcelUtils {

    private MovimientosExcelUtils() {}

    private static final DateTimeFormatter ISO =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter LATINA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static synchronized String generarExcelPorUsuario(
            String rutaOriginal,
            String carpetaDestino,
            String usuario) throws IOException {

        Path original = Path.of(rutaOriginal).toAbsolutePath();
        Path carpeta = Path.of(carpetaDestino).toAbsolutePath();
        Files.createDirectories(carpeta);

        Path maestro = carpeta.resolve("AZTECA_ultimo.xlsx");
        Path origen = Files.exists(maestro) ? maestro : original;

        String usuarioSeguro =
                usuario.replaceAll("[^a-zA-Z0-9._-]", "_");

        Path archivoUsuario = carpeta.resolve(
                "AZTECA_" + usuarioSeguro + "_" +
                        UUID.randomUUID().toString().substring(0, 8) +
                        ".xlsx");

        try (InputStream entrada = Files.newInputStream(origen);
             Workbook libro = new XSSFWorkbook(entrada)) {

            Sheet hoja = libro.getSheetAt(0);
            Row encabezado = hoja.getRow(0);

            if (encabezado == null) {
                throw new IOException("El Excel no tiene encabezados");
            }

            int operacion = buscarColumna(
                    encabezado, "Fecha de Operación");

            int aplicacion = buscarColumna(
                    encabezado, "Fecha de Aplicación");

            if (operacion < 0 || aplicacion < 0) {
                throw new IOException(
                        "No se encontraron las columnas de fechas");
            }

            int modificadas = 0;

            for (int i = 1; i <= hoja.getLastRowNum(); i++) {
                Row fila = hoja.getRow(i);

                if (fila == null) continue;

                boolean cambio = false;

                cambio |= incrementarFecha(
                        fila.getCell(operacion));

                cambio |= incrementarFecha(
                        fila.getCell(aplicacion));

                if (cambio) modificadas++;
            }

            if (modificadas == 0) {
                throw new IOException(
                        "No se modificó ninguna fecha del archivo");
            }

            try (OutputStream salida =
                         Files.newOutputStream(
                                 archivoUsuario,
                                 StandardOpenOption.CREATE_NEW)) {

                libro.write(salida);
            }


            Path temporal = Files.createTempFile(
                    carpeta, "azteca_master_", ".xlsx");

            try {
                Files.copy(
                        archivoUsuario,
                        temporal,
                        StandardCopyOption.REPLACE_EXISTING);

                IOException ultimoError = null;
                boolean actualizado = false;

                for (int intento = 1; intento <= 5; intento++) {
                    try {
                        Files.move(
                                temporal,
                                maestro,
                                StandardCopyOption.REPLACE_EXISTING);

                        actualizado = true;
                        break;

                    } catch (AccessDeniedException e) {
                        ultimoError = e;
                    } catch (FileSystemException e) {
                        ultimoError = e;
                    }

                    System.out.println(
                            "[EXCEL] Maestro bloqueado. Reintento "
                                    + intento + "/5");

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new IOException(
                                "Actualización del maestro interrumpida", e);
                    }
                }

                if (!actualizado) {
                    throw new IOException(
                            "No se pudo actualizar AZTECA_ultimo.xlsx. "
                                    + "Verifica que no esté abierto en Excel "
                                    + "ni bloqueado por otro proceso.",
                            ultimoError);
                }

            } finally {
                Files.deleteIfExists(temporal);
            }


            System.out.println(
                    "[EXCEL] Usuario: " + usuario);
            System.out.println(
                    "[EXCEL] Filas modificadas: " + modificadas);
            System.out.println(
                    "[EXCEL] Archivo: " + archivoUsuario);

            return archivoUsuario.toString();
        }
    }

    private static int buscarColumna(
            Row encabezado, String nombre) {

        String buscado = normalizar(nombre);

        for (Cell celda : encabezado) {
            if (normalizar(celda.toString()).equals(buscado)) {
                return celda.getColumnIndex();
            }
        }

        return -1;
    }

    private static String normalizar(String texto) {

        String valor = Normalizer.normalize(
                texto.trim(), Normalizer.Form.NFD);

        return valor.replaceAll("\\p{M}", "")
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }

    private static boolean incrementarFecha(Cell celda)
            throws IOException {

        if (celda == null ||
                celda.getCellType() == CellType.BLANK) {
            return false;
        }

        if (celda.getCellType() == CellType.NUMERIC) {

            if (!DateUtil.isCellDateFormatted(celda)) {
                throw new IOException(
                        "La celda " + celda.getAddress() +
                                " no tiene formato de fecha");
            }

            LocalDate fecha = celda.getDateCellValue()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            Date nueva = Date.from(
                    fecha.plusDays(1)
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant());

            celda.setCellValue(nueva);
            return true;
        }

        if (celda.getCellType() == CellType.STRING) {

            String texto = celda.getStringCellValue().trim();

            if (texto.isEmpty()) return false;

            try {
                if (texto.matches("\\d{4}-\\d{2}-\\d{2}")) {

                    LocalDate fecha = LocalDate.parse(texto, ISO);
                    celda.setCellValue(
                            fecha.plusDays(1).format(ISO));

                } else {

                    LocalDate fecha = LocalDate.parse(
                            texto, LATINA);

                    celda.setCellValue(
                            fecha.plusDays(1).format(LATINA));
                }

                return true;

            } catch (DateTimeParseException e) {
                throw new IOException(
                        "Fecha inválida en " +
                                celda.getAddress() + ": " + texto, e);
            }
        }

        throw new IOException(
                "Tipo de celda no compatible: " +
                        celda.getAddress());
    }


    public static String obtenerFechaExportacion(String rutaExcel)
            throws IOException {

        try (InputStream entrada = Files.newInputStream(Path.of(rutaExcel));
             Workbook libro = new XSSFWorkbook(entrada)) {

            Sheet hoja = libro.getSheetAt(0);
            Row encabezado = hoja.getRow(0);

            if (encabezado == null) {
                throw new IOException("El Excel no tiene encabezados");
            }

            int columna = buscarColumna(
                    encabezado, "Fecha de Operación");

            int columnaAplicacion = buscarColumna(
                    encabezado, "Fecha de Aplicación");

            if (columna < 0 || columnaAplicacion < 0) {
                throw new IOException(
                        "No se encontraron las columnas de fechas");
            }

            java.time.LocalDate fechaEncontrada = null;

            for (int i = 1; i <= hoja.getLastRowNum(); i++) {

                Row fila = hoja.getRow(i);
                if (fila == null) continue;

                java.time.LocalDate operacion =
                        leerFecha(fila.getCell(columna));

                java.time.LocalDate aplicacion =
                        leerFecha(fila.getCell(columnaAplicacion));

                if (operacion == null && aplicacion == null) continue;

                if (operacion == null || !operacion.equals(aplicacion)) {
                    throw new IOException(
                            "Las fechas no coinciden en la fila " + (i + 1));
                }

                if (fechaEncontrada != null
                        && !fechaEncontrada.equals(operacion)) {
                    throw new IOException(
                            "El Excel contiene varias fechas de operación");
                }

                fechaEncontrada = operacion;
            }

            if (fechaEncontrada == null) {
                throw new IOException(
                        "No se encontraron fechas en el Excel");
            }

            return fechaEncontrada.format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
    }

    private static java.time.LocalDate leerFecha(Cell celda)
            throws IOException {

        if (celda == null || celda.getCellType() == CellType.BLANK) {
            return null;
        }

        if (celda.getCellType() == CellType.NUMERIC) {
            if (!DateUtil.isCellDateFormatted(celda)) {
                throw new IOException(
                        "Fecha sin formato válido: " + celda.getAddress());
            }

            return celda.getLocalDateTimeCellValue().toLocalDate();
        }

        if (celda.getCellType() == CellType.STRING) {
            String valor = celda.getStringCellValue().trim();

            try {
                if (valor.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    return java.time.LocalDate.parse(valor);
                }

                return java.time.LocalDate.parse(
                        valor,
                        DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            } catch (java.time.format.DateTimeParseException e) {
                throw new IOException("Fecha inválida: " + valor, e);
            }
        }

        throw new IOException(
                "Tipo de celda no válido: " + celda.getAddress());
    }

}
