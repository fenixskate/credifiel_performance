package automation.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.*;
import java.util.*;

/** Contexto y mediciones de un escenario web; nunca comparte datos entre hilos. */
public final class BrowserConciliationReport {
    private static final List<String> COLUMNS = List.of("Fecha", "Hilo", "Usuario", "Dependencia",
            "Pagaduria", "Periodo", "Cuenta", "ArchivoARR", "RegistrosArchivo", "Referencia", "Proceso",
            "ConciliacionesTotales", "ConciliacionesRealizadas", "TransaccionesProcesadas", "TransaccionesTotales",
            "Avance", "TiempoConciliacionSegundos", "Estado", "Etapa", "LimiteEsperaSegundos",
            "EsperaObservadaSegundos", "TiempoTotalHastaFalloSegundos", "Comentario");
    private static final ThreadLocal<BrowserConciliationReport> CURRENT = ThreadLocal.withInitial(BrowserConciliationReport::new);
    private final Map<String, String> values = new LinkedHashMap<>();
    private final long scenarioStart = System.nanoTime();
    private long conciliationStart;
    private long waitStart;
    private Duration waitLimit;
    private boolean recorded;
    private String attachment;

    private BrowserConciliationReport() {}

    public static void begin() { CURRENT.set(new BrowserConciliationReport()); }
    public static void clear() { CURRENT.remove(); }
    public static void set(String key, Object value) { CURRENT.get().values.put(key, value == null ? "" : value.toString()); }
    public static String get(String key) { return CURRENT.get().values.getOrDefault(key, ""); }

    public static void waiting(String stage, Duration limit) {
        var current = CURRENT.get();
        current.waitStart = System.nanoTime();
        current.waitLimit = limit;
        set("Etapa", stage);
    }

    public static void endWait() {
        var current = CURRENT.get();
        current.waitStart = 0;
        current.waitLimit = null;
        set("Etapa", "");
    }

    public static long startConciliation(Duration limit) {
        var current = CURRENT.get();
        waiting("Conciliacion", limit);
        current.conciliationStart = current.waitStart;
        return current.conciliationStart;
    }

    public static void resumeConciliation(Duration limit) {
        var current = CURRENT.get();
        current.waitStart = current.conciliationStart;
        current.waitLimit = limit;
        set("Etapa", "Cierre de conciliacion");
    }

    public static void success(long finished) {
        var current = CURRENT.get();
        if (current.conciliationStart == 0) throw new IllegalStateException("No se inicio la medicion de conciliacion");
        set("TiempoConciliacionSegundos", seconds(finished - current.conciliationStart));
        set("Estado", "Conciliado");
        set("Etapa", "Conciliacion");
        current.write();
    }

    public static void failure(Throwable error) {
        var current = CURRENT.get();
        if (current.recorded || get("Dependencia").isBlank()) return;
        long now = System.nanoTime();
        set("Estado", "FALLIDO");
        set("TiempoConciliacionSegundos", "");
        set("TiempoTotalHastaFalloSegundos", seconds(now - current.scenarioStart));
        String message = error.toString();
        if (current.waitStart != 0 && current.waitLimit != null) {
            set("LimiteEsperaSegundos", current.waitLimit.toSeconds());
            set("EsperaObservadaSegundos", seconds(now - current.waitStart));
            if (error instanceof org.openqa.selenium.TimeoutException) {
                message = (now - current.waitStart >= current.waitLimit.toNanos()
                        ? "Fallo por tiempo de espera en " : "Timeout del navegador antes de agotar el plazo general de ")
                        + get("Etapa") + ": limite configurado "
                        + current.waitLimit.toSeconds() + " s; espera observada " + get("EsperaObservadaSegundos")
                        + " s. El timeout no confirma que Odoo haya detenido el trabajo. " + message;
            }
        }
        set("Comentario", message);
        current.write();
    }

    public static byte[] takeAttachment() {
        var current = CURRENT.get();
        String csv = current.attachment;
        current.attachment = null;
        return csv == null ? null : csv.getBytes(StandardCharsets.UTF_8);
    }

    private static String seconds(long nanos) { return String.format(Locale.ROOT, "%.3f", nanos / 1_000_000_000.0); }
    private static String quote(String value) { return "\"" + value.replace("\"", "\"\"") + "\""; }

    private void write() {
        set("Fecha", ZonedDateTime.now(ZoneId.of("America/Mexico_City")));
        set("Hilo", Thread.currentThread().getName());
        String header = String.join(",", COLUMNS) + "\n";
        String row = COLUMNS.stream().map(key -> quote(values.getOrDefault(key, "")))
                .collect(java.util.stream.Collectors.joining(",")) + "\n";
        synchronized (BrowserConciliationReport.class) {
            try {
                Path folder = Path.of(Files.readString(Path.of("target", "current-run.txt")).trim());
                if (!folder.isAbsolute()) folder = Path.of(System.getProperty("user.dir")).resolve(folder);
                Files.createDirectories(folder);
                Path report = folder.resolve("metricas_cp02.csv");
                if (Files.notExists(report)) Files.writeString(report, header, StandardOpenOption.CREATE_NEW);
                else try (var reader = Files.newBufferedReader(report)) {
                    if (!header.stripTrailing().equals(reader.readLine())) throw new IllegalStateException("El CSV existente tiene otro formato: " + report);
                }
                Files.writeString(report, row, StandardOpenOption.APPEND);
                recorded = true;
                attachment = header + row;
                System.out.println("[CSV WEB] " + report.toAbsolutePath());
            } catch (IOException e) {
                throw new IllegalStateException("No se pudieron guardar las mediciones web", e);
            }
        }
    }
}
