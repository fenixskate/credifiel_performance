package verification;

import automation.utils.MovimientosExcelUtils;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/** Valida el volumen en carpetas aisladas; no inicia navegadores ni carga movimientos. */
public final class AztecaVolumenSmoke {
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static void main(String[] args) throws Exception {
        Path original = Path.of("Documentos/AZTECA_QA.xlsx");
        byte[] originalAntes = Files.readAllBytes(original);
        Path trabajo = Files.createTempDirectory(Path.of("target"), "azteca-volumen-").toAbsolutePath();
        Path salida = trabajo.resolve("sin-maestro");
        LocalDate anterior = LocalDate.parse(MovimientosExcelUtils.obtenerFechaExportacion(original.toString()), FECHA);
        Set<String> movimientosTanda = new HashSet<>();
        for (int i = 1; i <= 5; i++) {
            Path generado = Path.of(MovimientosExcelUtils.generarExcelPorUsuario(
                    original.toString(), salida.toString(), "control" + i));
            LocalDate fecha = LocalDate.parse(MovimientosExcelUtils.obtenerFechaExportacion(generado.toString()), FECHA);
            if (!fecha.equals(anterior.plusDays(1))) throw new AssertionError("La fecha no incremento un dia");
            anterior = fecha;
            comprobar(original, generado, fecha, movimientosTanda);
        }
        if (movimientosTanda.size() != 2500) throw new AssertionError("La tanda no contiene 2500 movimientos distintos");
        System.out.println("PASS: primera ejecucion desde el original de 80 y cinco archivos de 500; 2500 movimientos por fecha/numero");

        Path legado = trabajo.resolve("maestro-80");
        Files.createDirectories(legado);
        Files.copy(original, legado.resolve("AZTECA_ultimo.xlsx"));
        Path convertido = Path.of(MovimientosExcelUtils.generarExcelPorUsuario(original.toString(), legado.toString(), "maestro-legado"));
        comprobar(original, convertido, LocalDate.parse(MovimientosExcelUtils.obtenerFechaExportacion(convertido.toString()), FECHA), new HashSet<>());
        System.out.println("PASS: maestro existente de 80 convertido a 500");

        Path excesivo = trabajo.resolve("mas-de-500");
        Files.createDirectories(excesivo);
        Path maestro = excesivo.resolve("AZTECA_ultimo.xlsx");
        try (InputStream entrada = Files.newInputStream(convertido); XSSFWorkbook libro = new XSSFWorkbook(entrada)) {
            var hoja = libro.getSheetAt(0);
            hoja.copyRows(1, 1, 501, new CellCopyPolicy());
            hoja.getRow(501).getCell(columna(hoja, "Numero de Movimiento")).setCellValue(501);
            try (OutputStream out = Files.newOutputStream(maestro)) { libro.write(out); }
        }
        byte[] antes = Files.readAllBytes(maestro);
        try {
            MovimientosExcelUtils.generarExcelPorUsuario(original.toString(), excesivo.toString(), "no-publicar");
            throw new AssertionError("Se acepto un maestro de 501 movimientos");
        } catch (IOException esperado) {
            if (!esperado.getMessage().contains("501 movimientos")) throw esperado;
        }
        if (!Arrays.equals(antes, Files.readAllBytes(maestro))) throw new AssertionError("Se altero un maestro rechazado");
        if (!Arrays.equals(originalAntes, Files.readAllBytes(original))) throw new AssertionError("Se modifico el original");
        System.out.println("PASS: volumen excesivo rechazado y originales conservados");
        System.out.println("Evidencia: " + trabajo);
    }

    private static int columna(Sheet hoja, String nombre) {
        for (Cell celda : hoja.getRow(0)) if (celda.getStringCellValue().equals(nombre)) return celda.getColumnIndex();
        throw new AssertionError("Falta columna " + nombre);
    }

    private static void comprobar(Path original, Path generado, LocalDate fecha, Set<String> tanda) throws Exception {
        try (InputStream in = Files.newInputStream(generado); Workbook libro = WorkbookFactory.create(in);
             InputStream anterior = Files.newInputStream(original); Workbook base = WorkbookFactory.create(anterior)) {
            Sheet hoja = libro.getSheetAt(0), modelo = base.getSheetAt(0);
            int cuenta = columna(hoja, "Número de Cuenta Bancaria");
            int numero = columna(hoja, "Numero de Movimiento");
            int monto = columna(hoja, "Importe de Movimiento");
            int codigo = columna(hoja, "Código de Operación");
            int operacion = columna(hoja, "Fecha de Operación");
            int aplicacion = columna(hoja, "Fecha de Aplicación");
            DataFormatter formato = new DataFormatter();
            int total = 0;
            Set<String> ids = new HashSet<>();
            for (Row fila : hoja) {
                if (fila.getRowNum() == 0 || formato.formatCellValue(fila.getCell(cuenta)).isBlank()) continue;
                total++;
                String id = formato.formatCellValue(fila.getCell(numero));
                if (id.isBlank() || !ids.add(id) || !tanda.add(fecha + ":" + id)) throw new AssertionError("Numero repetido: " + id);
                for (int columna : new int[]{cuenta, monto, codigo, operacion, aplicacion}) {
                    Cell celda = fila.getCell(columna);
                    if (formato.formatCellValue(celda).isBlank()) throw new AssertionError("Movimiento incompleto");
                    if (!celda.getCellStyle().getDataFormatString().equals(modelo.getRow(1).getCell(columna).getCellStyle().getDataFormatString()))
                        throw new AssertionError("Formato alterado");
                }
            }
            if (total != 500) throw new AssertionError("Se esperaban 500, hay " + total);
        }
    }
}
