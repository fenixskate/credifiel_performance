package verification;

import automation.utils.PromesasExcelUtils;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/** Pruebas locales: no abre navegador ni publica archivos en Documentos. */
public final class PromesasJavaSmoke {
    public static void main(String[] args) throws Exception {
        Path raiz = Files.createTempDirectory(Path.of("target"), "promesas-java-smoke-").toAbsolutePath();
        if (Boolean.getBoolean("promesas.smoke.real")) {
            Path templates = Path.of("Documentos/Insumos Flujo 4/Template/Generados");
            PromesasExcelUtils.preparar(Path.of("Documentos/Reporte Universo Prestamos/ReporteUniversoCartera.xlsx"),
                    raiz.resolve("real"), templates, raiz.resolve("respaldos"), raiz.resolve("publicados"), 40);
            System.out.println("PASS: reporte real, 40 templates, 20000 activos unicos; salida aislada: " + raiz);
            return;
        }
        Path templates = raiz.resolve("templates");
        Files.createDirectories(templates);
        for (int i = 1; i <= 2; i++) template(templates.resolve(nombre(i)));
        Path reporte = raiz.resolve("universo.xlsx");
        reporte(reporte, 0, 1000);
        Path trabajo = raiz.resolve("xlsx"), destino = raiz.resolve("publicados-xlsx");
        PromesasExcelUtils.preparar(reporte, trabajo, templates, raiz.resolve("respaldos"), destino, 2);
        comprobar(destino, 2);
        System.out.println("PASS: XLSX, 2 hojas, activos unicos, duplicados/inactivos excluidos, formatos/formulas conservados");

        Path zip = raiz.resolve("universo.zip");
        try (ZipOutputStream salida = new ZipOutputStream(Files.newOutputStream(zip))) {
            for (int i = 0; i < 2; i++) {
                Path parte = raiz.resolve("parte" + i + ".xlsx");
                reporte(parte, i * 500, 500);
                salida.putNextEntry(new ZipEntry("reportes/parte" + i + ".xlsx"));
                Files.copy(parte, salida);
                salida.closeEntry();
            }
        }
        PromesasExcelUtils.preparar(zip, raiz.resolve("zip"), templates, raiz.resolve("respaldos"), raiz.resolve("publicados-zip"), 2);
        comprobar(raiz.resolve("publicados-zip"), 2);
        System.out.println("PASS: ZIP con multiples Excel");

        Path insuficiente = raiz.resolve("insuficiente.xlsx"), sinPublicar = raiz.resolve("sin-publicar");
        reporte(insuficiente, 0, 499);
        try {
            PromesasExcelUtils.preparar(insuficiente, raiz.resolve("insuficiente"), templates, raiz.resolve("respaldos"), sinPublicar, 1);
            throw new AssertionError("Se aceptaron activos insuficientes");
        } catch (IllegalStateException esperado) {
            if (!esperado.getMessage().contains("insuficientes") || Files.exists(sinPublicar)) throw esperado;
        }
        System.out.println("PASS: activos insuficientes impiden publicar");

        Path segundo = trabajo.resolve("generados").resolve(nombre(2));
        try (InputStream entrada = Files.newInputStream(segundo); Workbook libro = WorkbookFactory.create(entrada)) {
            libro.getSheetAt(0).getRow(1).getCell(1).setCellValue("ALTERADO");
            try (OutputStream salida = Files.newOutputStream(raiz.resolve("alterado.xlsx"))) { libro.write(salida); }
        }
        Files.move(raiz.resolve("alterado.xlsx"), segundo, StandardCopyOption.REPLACE_EXISTING);
        byte[] antes = Files.readAllBytes(destino.resolve(nombre(1)));
        try {
            PromesasExcelUtils.verificarYPublicar(trabajo, destino);
            throw new AssertionError("Se acepto una columna alterada");
        } catch (IllegalStateException esperado) {
            if (!esperado.getMessage().contains("Cambio otra celda")) throw esperado;
        }
        if (!Arrays.equals(antes, Files.readAllBytes(destino.resolve(nombre(1))))) throw new AssertionError("Se publico antes de validar toda la tanda");
        System.out.println("PASS: alteracion en ultimo template impide reemplazar cualquier archivo");
        System.out.println("Evidencia: " + raiz);
    }

    private static String nombre(int numero) {
        return "Template de Carga de Promesas v2" + (numero == 1 ? "" : "_" + numero) + ".xlsx";
    }

    private static void template(Path destino) throws Exception {
        try (Workbook libro = new XSSFWorkbook()) {
            Sheet hoja = libro.createSheet("Promesas");
            hoja.createFreezePane(1, 1);
            Row encabezado = hoja.createRow(0);
            for (int c = 0; c < 8; c++) encabezado.createCell(c).setCellValue(c == 0 ? "num_credito" : "columna" + c);
            CellStyle fecha = libro.createCellStyle();
            fecha.setDataFormat(libro.createDataFormat().getFormat("dd/MM/yyyy"));
            for (int r = 1; r <= 500; r++) {
                Row fila = hoja.createRow(r);
                for (int c = 0; c < 8; c++) fila.createCell(c).setCellValue("dato" + c);
                fila.getCell(2).setCellValue(java.time.LocalDate.of(2026, 11, 6));
                fila.getCell(2).setCellStyle(fecha);
                fila.getCell(3).setCellFormula("1+2");
            }
            libro.createSheet("Auxiliar").createRow(0).createCell(0).setCellValue("Conservar");
            try (OutputStream salida = Files.newOutputStream(destino)) { libro.write(salida); }
        }
    }

    private static void reporte(Path destino, int inicio, int cantidad) throws Exception {
        try (Workbook libro = new XSSFWorkbook()) {
            for (int s = 0; s < 2; s++) {
                Sheet hoja = libro.createSheet("Universo" + s);
                Row header = hoja.createRow(0);
                header.createCell(0).setCellValue("Credito");
                header.createCell(20).setCellValue("Estado");
                int fila = 1;
                Row inactivo = hoja.createRow(fila++);
                inactivo.createCell(0).setCellValue("INACTIVO");
                inactivo.createCell(20).setCellValue("Liquidado");
                int desde = s == 0 ? 0 : cantidad / 2;
                int hasta = s == 0 ? cantidad / 2 : cantidad;
                for (int i = desde; i < hasta; i++) {
                    Row row = hoja.createRow(fila++);
                    // Texto con ceros iniciales y creditos numericos deben conservarse correctamente.
                    if (inicio + i == 0) row.createCell(0).setCellValue("00001");
                    else row.createCell(0).setCellValue(100000 + inicio + i);
                    row.createCell(20).setCellValue(" Activo ");
                    Row duplicado = hoja.createRow(fila++);
                    if (inicio + i == 0) duplicado.createCell(0).setCellValue("00001");
                    else duplicado.createCell(0).setCellValue(100000 + inicio + i);
                    duplicado.createCell(20).setCellValue("Activo");
                }
            }
            try (OutputStream salida = Files.newOutputStream(destino)) { libro.write(salida); }
        }
    }

    private static void comprobar(Path destino, int cantidad) throws Exception {
        Set<String> ids = new HashSet<>();
        for (int i = 1; i <= cantidad; i++) {
            try (InputStream entrada = Files.newInputStream(destino.resolve(nombre(i))); Workbook libro = WorkbookFactory.create(entrada)) {
                Sheet hoja = libro.getSheetAt(0);
                for (int r = 1; r <= 500; r++) {
                    String id = hoja.getRow(r).getCell(0).getStringCellValue();
                    if (id.equals("INACTIVO") || !ids.add(id)) throw new AssertionError("Credito incorrecto: " + id);
                }
                if (!"1+2".equals(hoja.getRow(1).getCell(3).getCellFormula())) throw new AssertionError("Formula alterada");
            }
        }
        if (!ids.contains("00001") || ids.size() != cantidad * 500) throw new AssertionError("Seleccion incorrecta");
    }
}
