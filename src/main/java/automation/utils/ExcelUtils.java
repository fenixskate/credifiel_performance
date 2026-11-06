
package automation.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

public class ExcelUtils {

    private static final int MAX_INTENTOS = 5;
    private static final long ESPERA_REINTENTO_MS = 1000;

    private ExcelUtils() {
    }

    // Incrementar referencias conservando su tipo original
    public static void incrementarReferencias(
            String rutaOrigen,
            String rutaDestino) throws IOException {

        Path origen = Path.of(rutaOrigen).toAbsolutePath();
        Path destino = Path.of(rutaDestino).toAbsolutePath();

        try (InputStream entrada = Files.newInputStream(origen);
             Workbook workbook = new XSSFWorkbook(entrada)) {

            Sheet hoja = workbook.getSheetAt(0);
            Row encabezado = hoja.getRow(0);

            if (encabezado == null) {
                throw new IOException(
                        "El Excel no contiene encabezados");
            }

            int columnaReferencia = -1;

            for (Cell celda : encabezado) {

                if (celda.getCellType() == CellType.STRING
                        && celda.getStringCellValue()
                        .trim()
                        .equalsIgnoreCase("Referencia")) {

                    columnaReferencia = celda.getColumnIndex();
                    break;
                }
            }

            if (columnaReferencia == -1) {
                throw new IOException(
                        "No existe la columna Referencia");
            }

            // A2 debe contener la fecha de cada ejecución, incluso al reutilizar el maestro.
            Row filaFecha = hoja.getRow(1);
            if (filaFecha == null) {
                filaFecha = hoja.createRow(1);
            }
            Cell celdaFecha = filaFecha.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            celdaFecha.setCellValue(LocalDate.now(ZoneId.of("America/Mexico_City")));
            CellStyle estiloFecha = workbook.createCellStyle();
            estiloFecha.cloneStyleFrom(celdaFecha.getCellStyle());
            estiloFecha.setDataFormat(workbook.createDataFormat().getFormat("dd/MM/yyyy"));
            celdaFecha.setCellStyle(estiloFecha);

            for (int i = 1; i <= hoja.getLastRowNum(); i++) {

                Row fila = hoja.getRow(i);

                if (fila == null) {
                    continue;
                }

                Cell celda = fila.getCell(columnaReferencia);

                if (celda == null
                        || celda.getCellType() == CellType.BLANK) {
                    continue;
                }

                long referencia;

                if (celda.getCellType() == CellType.NUMERIC) {

                    referencia =
                            (long) celda.getNumericCellValue();

                } else if (celda.getCellType() == CellType.STRING) {

                    referencia = Long.parseLong(
                            celda.getStringCellValue().trim());

                } else {
                    throw new IOException(
                            "Tipo de referencia no compatible: "
                                    + celda.getAddress());
                }

                long nuevaReferencia = Math.addExact(
                        referencia, 1);

                if (celda.getCellType() == CellType.STRING) {

                    celda.setCellValue(
                            String.valueOf(nuevaReferencia));

                } else {

                    celda.setCellValue(
                            (double) nuevaReferencia);
                }

                System.out.println(
                        "[EXCEL] " + referencia
                                + " -> " + nuevaReferencia);
            }

            Path directorio = destino.getParent();

            if (directorio != null) {
                Files.createDirectories(directorio);
            }

            try (OutputStream salida =
                         Files.newOutputStream(
                                 destino,
                                 StandardOpenOption.CREATE,
                                 StandardOpenOption.TRUNCATE_EXISTING)) {

                workbook.write(salida);
            }
        }

        System.out.println(
                "[OK] Excel modificado: " + destino);
    }


    public static synchronized String generarSiguienteExcel(
            String rutaOriginal,
            String rutaModificada) throws IOException {

        Path original = Path.of(rutaOriginal)
                .toAbsolutePath().normalize();

        Path maestro = Path.of(rutaModificada)
                .toAbsolutePath().normalize();

        if (!Files.isRegularFile(original)) {
            throw new IOException(
                    "No existe el archivo original: " + original);
        }

        Files.createDirectories(maestro.getParent());

        // Siempre utilizar la última versión disponible
        Path origen = Files.isRegularFile(maestro)
                ? maestro : original;

        // Crear la siguiente versión con referencias +1
        Path siguiente = Files.createTempFile(
                maestro.getParent(), "invex_", ".xlsx");

        incrementarReferencias(
                origen.toString(),
                siguiente.toString());

        // Validar que se generó correctamente
        if (!Files.isRegularFile(siguiente)
                || Files.size(siguiente) == 0) {
            throw new IOException(
                    "No se generó el Excel actualizado");
        }

        // Actualizar maestro mediante copia
        // La actualización debe completarse antes de continuar
        Files.copy(
                siguiente,
                maestro,
                StandardCopyOption.REPLACE_EXISTING);

        System.out.println(
                "[OK] Maestro actualizado: " + maestro);

        return siguiente.toString();
    }


    public static synchronized String generarExcelPorUsuario(
            String rutaOriginal,
            String carpetaDestino,
            String usuario) throws IOException {

        Path original = Path.of(rutaOriginal)
                .toAbsolutePath()
                .normalize();

        Path carpeta = Path.of(carpetaDestino)
                .toAbsolutePath()
                .normalize();

        Files.createDirectories(carpeta);

        Path maestro = original.getParent()
                .resolve("archivo_modificado.xlsx");

        String archivoActualizado = generarSiguienteExcel(
                original.toString(),
                maestro.toString());

        Path temporal = Path.of(archivoActualizado);

        String usuarioSeguro = usuario
                .replaceAll("[^a-zA-Z0-9._-]", "_");

        String identificador = java.util.UUID.randomUUID()
                .toString().substring(0, 8);

        Path archivoUsuario = carpeta.resolve(
                "INVEX_" + usuarioSeguro
                        + "_" + identificador + ".xlsx");

        try {

            // Crear Excel exclusivo para el usuario
            Files.copy(temporal, archivoUsuario);

            System.out.println(
                    "[OK] Excel generado: " + archivoUsuario);

            return archivoUsuario.toString();

        } finally {

            // Limpiar el temporal, no el maestro ni el Excel del usuario
            try {
                Files.deleteIfExists(temporal);

                System.out.println(
                        "[LIMPIEZA] Temporal eliminado: "
                                + temporal.getFileName());

            } catch (IOException e) {

                // Un bloqueo temporal no debe invalidar un Excel
                // que ya se copió correctamente
                System.out.println(
                        "[WARNING] No se pudo eliminar temporal: "
                                + temporal.getFileName()
                                + " | " + e.getMessage());
            }
        }
    }


    // Prueba manual opcional
    public static void main(String[] args) {

        Path carpetaBase = Path.of(
                System.getProperty("user.dir"),
                "Documentos",
                "Layout Deposito");

        String rutaOrigen = carpetaBase
                .resolve("Invex 2595.xlsx")
                .toString();

        String carpetaDestino = carpetaBase
                .resolve("layouts_generados")
                .toString();

        try {

            String archivo = generarExcelPorUsuario(
                    rutaOrigen,
                    carpetaDestino,
                    "usuario.prueba");

            System.out.println(
                    "[OK] Archivo generado: " + archivo);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}
