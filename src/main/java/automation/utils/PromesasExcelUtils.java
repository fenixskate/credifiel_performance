package automation.utils;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;
import java.util.zip.ZipFile;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.openqa.selenium.json.Json;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/** Selecciona activos del Universo y prepara los templates sin procesos externos. */
public final class PromesasExcelUtils {
    public static final int REGISTROS = 500;
    private PromesasExcelUtils() {}

    public static Path preparar(Path reporte, Path trabajo, Path templates, Path respaldos,
                                Path destino, int cantidad) throws Exception {
        if (cantidad < 1) throw new IllegalArgumentException("Cantidad de archivos invalida");
        Files.createDirectories(trabajo);
        List<String> creditos = seleccionar(reporte, trabajo, Math.multiplyExact(cantidad, REGISTROS));
        List<String> nombres = new ArrayList<>();
        Map<String, String> fuentes = new LinkedHashMap<>();
        // Validar todos los insumos antes de crear o publicar la nueva tanda.
        for (int i = 1; i <= cantidad; i++) {
            String nombre = nombre(i);
            Path fuente = templates.resolve(nombre);
            if (!Files.isRegularFile(fuente)) fuente = templates.resolve(nombre((i - 1) % 20 + 1));
            try (Workbook libro = abrir(fuente)) { validarTemplate(libro, fuente); }
            nombres.add(nombre);
            fuentes.put(nombre, fuente.toAbsolutePath().toString());
        }
        Path respaldo = respaldos.resolve(UUID.randomUUID().toString()).toAbsolutePath();
        Files.createDirectories(respaldo);
        Path generados = trabajo.resolve("generados");
        Files.createDirectories(generados);
        for (String nombre : nombres) Files.copy(Path.of(fuentes.get(nombre)), respaldo.resolve(nombre));
        Map<String, Object> manifest = new LinkedHashMap<>();
        manifest.put("source", reporte.toAbsolutePath().toString());
        manifest.put("download", reporte.toAbsolutePath().toString());
        manifest.put("backup", respaldo.toString());
        manifest.put("directory", destino.toAbsolutePath().toString());
        manifest.put("names", nombres);
        manifest.put("credits", creditos);
        manifest.put("file_count", cantidad);
        manifest.put("records_per_file", REGISTROS);
        manifest.put("template_sources", fuentes);
        MessageDigest hash = MessageDigest.getInstance("SHA-256");
        try (InputStream entrada = Files.newInputStream(reporte)) {
            byte[] buffer = new byte[65536];
            int leidos;
            while ((leidos = entrada.read(buffer)) != -1) hash.update(buffer, 0, leidos);
        }
        manifest.put("source_sha256", HexFormat.of().formatHex(hash.digest()));
        Files.writeString(trabajo.resolve("manifest.json"), new Json().toJson(manifest));
        for (int i = 0; i < nombres.size(); i++) {
            String nombre = nombres.get(i);
            try (Workbook libro = abrir(respaldo.resolve(nombre))) {
                Sheet hoja = libro.getSheetAt(0);
                for (int fila = 1; fila <= REGISTROS; fila++) {
                    Cell celda = hoja.getRow(fila).getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    celda.setCellValue(creditos.get(i * REGISTROS + fila - 1));
                }
                libro.getCreationHelper().createFormulaEvaluator().evaluateAll();
                try (OutputStream salida = Files.newOutputStream(generados.resolve(nombre))) { libro.write(salida); }
            }
            System.out.println("[PROMESAS JAVA] Preparado " + (i + 1) + "/" + cantidad + " | " + nombre);
        }
        return verificarYPublicar(trabajo, destino);
    }

    /** Tambien admite tandas previamente preparadas con el mismo manifest. */
    @SuppressWarnings("unchecked")
    public static Path verificarYPublicar(Path trabajo, Path destino) throws Exception {
        Map<String, Object> manifest = new Json().toType(Files.readString(trabajo.resolve("manifest.json")), Map.class);
        List<String> nombres = (List<String>) manifest.get("names");
        List<String> creditos = (List<String>) manifest.get("credits");
        if (nombres.isEmpty() || creditos.size() != Math.multiplyExact(nombres.size(), REGISTROS)
                || new HashSet<>(creditos).size() != creditos.size()
                || creditos.stream().anyMatch(id -> id == null || id.isBlank()))
            throw new IllegalStateException("Tanda incompleta o con creditos duplicados");
        Path respaldo = Path.of((String) manifest.get("backup"));
        Set<String> unicos = new HashSet<>();
        for (int i = 0; i < nombres.size(); i++) {
            String nombre = nombres.get(i);
            if (!nombre.equals(nombre(i + 1))) throw new IllegalStateException("Nombre de template inesperado: " + nombre);
            try (Workbook anterior = abrir(respaldo.resolve(nombre));
                 Workbook nuevo = abrir(trabajo.resolve("generados").resolve(nombre))) {
                validarTemplate(nuevo, Path.of(nombre));
                if (anterior.getNumberOfSheets() != nuevo.getNumberOfSheets())
                    throw new IllegalStateException("Cambiaron las hojas: " + nombre);
                for (int s = 0; s < anterior.getNumberOfSheets(); s++) {
                    Sheet a = anterior.getSheetAt(s), b = nuevo.getSheetAt(s);
                    if (!a.getSheetName().equals(b.getSheetName()) || a.getLastRowNum() != b.getLastRowNum()
                            || !a.getMergedRegions().equals(b.getMergedRegions())
                            || !panel(a).equals(panel(b)))
                        throw new IllegalStateException("Cambio la estructura: " + nombre);
                    for (int r = 0; r <= a.getLastRowNum(); r++) {
                        Row vieja = a.getRow(r), nueva = b.getRow(r);
                        if ((vieja == null) != (nueva == null)
                                || (vieja != null && vieja.getLastCellNum() != nueva.getLastCellNum()))
                            throw new IllegalStateException("Cambio una fila: " + nombre + " / " + (r + 1));
                        if (vieja == null) continue;
                        for (int c = 0; c < vieja.getLastCellNum(); c++) {
                            Cell antes = vieja.getCell(c), despues = nueva.getCell(c);
                            boolean credito = s == 0 && r > 0 && c == 0;
                            if (credito) {
                                String esperado = creditos.get(i * REGISTROS + r - 1);
                                if (despues == null || despues.getCellType() != CellType.STRING
                                        || !esperado.equals(despues.getStringCellValue()) || !unicos.add(esperado))
                                    throw new IllegalStateException("Credito incorrecto: " + nombre + " / " + (r + 1));
                            } else if (!Objects.equals(valor(antes), valor(despues))) {
                                throw new IllegalStateException("Cambio otra celda: " + nombre + " / " + (r + 1) + "," + (c + 1));
                            }
                            if (antes != null && despues != null
                                    && !antes.getCellStyle().getDataFormatString().equals(despues.getCellStyle().getDataFormatString()))
                                throw new IllegalStateException("Cambio un formato: " + nombre);
                            if (despues != null && (despues.getCellType() == CellType.ERROR
                                    || (despues.getCellType() == CellType.FORMULA && despues.getCachedFormulaResultType() == CellType.ERROR)))
                                throw new IllegalStateException("Error Excel: " + nombre + " / " + despues.getAddress());
                        }
                    }
                }
            }
        }
        if (unicos.size() != creditos.size()) throw new IllegalStateException("Faltan creditos en los templates");
        // Ningun archivo se publica si falla la validacion de cualquier template.
        Files.createDirectories(destino);
        for (String nombre : nombres) {
            Path temporal = Files.createTempFile(destino, "promesas_actualizando_", ".xlsx");
            try {
                Files.copy(trabajo.resolve("generados").resolve(nombre), temporal, StandardCopyOption.REPLACE_EXISTING);
                Files.move(temporal, destino.resolve(nombre), StandardCopyOption.REPLACE_EXISTING);
            } finally { Files.deleteIfExists(temporal); }
        }
        manifest.put("directory", destino.toAbsolutePath().toString());
        Files.writeString(trabajo.resolve("manifest.json"), new Json().toJson(manifest));
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("resultado", "PASS");
        resultado.put("archivos", nombres.size());
        resultado.put("registros_por_archivo", REGISTROS);
        resultado.put("activos_unicos", unicos.size());
        resultado.put("sin_duplicados", true);
        resultado.put("otras_columnas_conservadas", true);
        resultado.put("respaldo", respaldo.toString());
        resultado.put("reporte", manifest.get("source"));
        Files.writeString(trabajo.resolve("verificacion.json"), new Json().toJson(resultado));
        return destino;
    }

    private static Workbook abrir(Path archivo) throws IOException {
        try (InputStream entrada = Files.newInputStream(archivo)) { return WorkbookFactory.create(entrada); }
    }

    private static String nombre(int numero) {
        return "Template de Carga de Promesas v2" + (numero == 1 ? "" : "_" + numero) + ".xlsx";
    }

    private static void validarTemplate(Workbook libro, Path archivo) {
        Sheet hoja = libro.getSheetAt(0);
        if (hoja.getLastRowNum() != REGISTROS || hoja.getRow(0) == null
                || hoja.getRow(0).getLastCellNum() != 8
                || !"num_credito".equals(new DataFormatter().formatCellValue(hoja.getRow(0).getCell(0))))
            throw new IllegalStateException("Template incompatible: " + archivo);
        for (int i = 1; i <= REGISTROS; i++)
            if (hoja.getRow(i) == null || hoja.getRow(i).getLastCellNum() > 8)
                throw new IllegalStateException("Template debe contener 500 filas y 8 columnas: " + archivo);
    }

    private static String panel(Sheet hoja) {
        var panel = hoja.getPaneInformation();
        return panel == null ? "" : panel.isFreezePane() + ":" + panel.getHorizontalSplitPosition()
                + ":" + panel.getVerticalSplitPosition() + ":" + panel.getHorizontalSplitTopRow()
                + ":" + panel.getVerticalSplitLeftColumn();
    }

    private static Object valor(Cell celda) {
        if (celda == null) return null;
        return celda.getCellType() + ":" + switch (celda.getCellType()) {
            case STRING -> celda.getStringCellValue();
            case NUMERIC -> Double.toString(celda.getNumericCellValue());
            case BOOLEAN -> Boolean.toString(celda.getBooleanCellValue());
            case FORMULA -> celda.getCellFormula();
            case ERROR -> Byte.toString(celda.getErrorCellValue());
            default -> "";
        };
    }

    private static List<String> seleccionar(Path reporte, Path trabajo, int cantidad) throws Exception {
        LinkedHashSet<String> creditos = new LinkedHashSet<>();
        if (reporte.toString().toLowerCase(Locale.ROOT).endsWith(".zip")) {
            try (ZipFile zip = new ZipFile(reporte.toFile())) {
                var entradas = zip.entries();
                while (entradas.hasMoreElements() && creditos.size() < cantidad) {
                    var entrada = entradas.nextElement();
                    if (entrada.isDirectory() || !entrada.getName().toLowerCase(Locale.ROOT).endsWith(".xlsx")) continue;
                    Path temporal = Files.createTempFile(trabajo, "universo_", ".xlsx");
                    try {
                        try (InputStream datos = zip.getInputStream(entrada)) {
                            Files.copy(datos, temporal, StandardCopyOption.REPLACE_EXISTING);
                        }
                        leerActivos(temporal, creditos, cantidad);
                    } finally { Files.deleteIfExists(temporal); }
                }
            }
        } else leerActivos(reporte, creditos, cantidad);
        if (creditos.size() != cantidad)
            throw new IllegalStateException("Creditos activos unicos insuficientes: " + creditos.size() + "; requeridos: " + cantidad);
        return new ArrayList<>(creditos);
    }

    /** SAX permite leer el Universo sin cargar todas sus celdas en memoria. */
    private static void leerActivos(Path archivo, Set<String> creditos, int cantidad) throws Exception {
        try (OPCPackage paquete = OPCPackage.open(archivo.toFile(), PackageAccess.READ)) {
            XSSFReader lector = new XSSFReader(paquete);
            lector.setUseReadOnlySharedStringsTable(true);
            var estilos = lector.getStylesTable();
            var textos = lector.getSharedStringsTable();
            var hojas = lector.getSheetsData();
            DataFormatter formato = new DataFormatter(Locale.ROOT) {
                @Override public String formatRawCellContents(double valor, int indice, String patron, boolean fecha1904) {
                    return java.math.BigDecimal.valueOf(valor).stripTrailingZeros().toPlainString();
                }
            };
            while (hojas.hasNext() && creditos.size() < cantidad) {
                try (InputStream hoja = hojas.next()) {
                    var parser = XMLHelper.newXMLReader();
                    parser.setContentHandler(new XSSFSheetXMLHandler(estilos, textos,
                            new XSSFSheetXMLHandler.SheetContentsHandler() {
                                private String credito = "", estado = "";
                                private boolean encabezado;
                                @Override public void startRow(int fila) { credito = ""; estado = ""; encabezado = fila == 0; }
                                @Override public void cell(String referencia, String valor, XSSFComment comentario) {
                                    int columna = new CellReference(referencia).getCol();
                                    if (columna == 0) credito = valor.trim();
                                    if (columna == 20) estado = valor.trim();
                                }
                                @Override public void endRow(int fila) {
                                    if (encabezado && !"Estado".equalsIgnoreCase(estado))
                                        throw new IllegalStateException("La columna U no es Estado: " + archivo);
                                    if (!encabezado && "Activo".equalsIgnoreCase(estado) && !credito.isBlank()) creditos.add(credito);
                                }
                            }, formato, false));
                    // Interrumpir al completar el lote, sin recorrer el resto del Universo.
                    var handler = parser.getContentHandler();
                    parser.setContentHandler(new org.xml.sax.helpers.XMLFilterImpl() {
                        { setContentHandler(handler); }
                        @Override public void endElement(String uri, String local, String nombre) throws SAXException {
                            super.endElement(uri, local, nombre);
                            if ("row".equals(local) && creditos.size() >= cantidad) throw new LoteCompleto();
                        }
                    });
                    try { parser.parse(new InputSource(hoja)); }
                    catch (LoteCompleto completo) { break; }
                }
            }
        }
    }

    private static final class LoteCompleto extends SAXException {}
}
