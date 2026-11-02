package reports;

import io.cucumber.plugin.event.Result;
import io.cucumber.plugin.event.Status;
import org.openqa.selenium.json.Json;
import java.nio.file.*;
import java.time.*;
import java.util.*;

/** Recupera los mismos PDF desde el JSON de Cucumber sin ejecutar pruebas ni abrir Odoo. */
public final class RecoverPdfEvidence {
    private RecoverPdfEvidence() {}

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        if (args.length != 1) throw new IllegalArgumentException("Indique la ruta al JSON de Cucumber");
        if (ScenarioPdfReporter.isDryRun()) throw new IllegalStateException("No recuperar en modo dry-run");
        Path source = Path.of(args[0]).toAbsolutePath();
        var features = (List<Map<String, Object>>) new Json().toType(Files.readString(source), List.class);
        // Validar antes de crear archivos. Un dry-run no contiene imagenes y no es evidencia real.
        if (lastPng(features) == null) throw new IllegalArgumentException("El JSON no contiene capturas PNG");
        ScenarioPdfReporter reporter = new ScenarioPdfReporter();
        int passed = 0, failed = 0, captures = 0;
        for (var feature : features) {
            for (var scenario : (List<Map<String, Object>>) feature.getOrDefault("elements", List.of())) {
                if (!"scenario".equals(scenario.get("type"))) continue;
                var tags = ((List<Map<String, Object>>) scenario.getOrDefault("tags", List.of()))
                        .stream().map(tag -> tag.get("name").toString()).toList();
                var evidence = new ScenarioPdfReporter.ScenarioEvidence(scenario.get("name").toString(), tags,
                        Instant.parse(scenario.get("start_timestamp").toString()));
                evidence.source = source.toString();
                List<Result> results = new ArrayList<>();
                collectHookResults(scenario.get("before"), results, evidence);
                for (var step : (List<Map<String, Object>>) scenario.getOrDefault("steps", List.of())) {
                    var item = new ScenarioPdfReporter.StepEvidence(
                            step.getOrDefault("keyword", "").toString() + step.get("name"));
                    item.result = result((Map<String, Object>) step.getOrDefault("result", Map.of()));
                    item.screenshot = lastPng(step);
                    if (item.screenshot != null) captures++;
                    evidence.steps.add(item);
                    results.add(item.result);
                    collectHookResults(step.get("before"), results, evidence);
                    collectHookResults(step.get("after"), results, evidence);
                }
                collectHookResults(scenario.get("after"), results, evidence);
                Status status = results.stream().anyMatch(r -> r.getStatus() == Status.FAILED)
                        ? Status.FAILED : results.stream().map(Result::getStatus)
                        .filter(s -> s != Status.PASSED).findFirst().orElse(Status.PASSED);
                Duration duration = results.stream().map(Result::getDuration).reduce(Duration.ZERO, Duration::plus);
                reporter.writePdf(evidence, new Result(status, duration, null));
                if (status == Status.PASSED) passed++; else failed++;
            }
        }
        System.out.printf("[RECUPERACION] %d PASSED, %d no aprobados, %d capturas. Sin operaciones en Odoo.%n",
                passed, failed, captures);
    }

    private static Result result(Map<String, Object> data) {
        Status status = Status.valueOf(data.getOrDefault("status", "undefined").toString().toUpperCase(Locale.ROOT));
        long nanos = ((Number) data.getOrDefault("duration", 0L)).longValue();
        Throwable error = data.get("error_message") == null ? null
                : new IllegalStateException(data.get("error_message").toString());
        return new Result(status, Duration.ofNanos(nanos), error);
    }

    @SuppressWarnings("unchecked")
    private static void collectHookResults(Object hooks, List<Result> results,
            ScenarioPdfReporter.ScenarioEvidence evidence) {
        if (!(hooks instanceof List<?> list)) return;
        for (Object value : list) {
            var hook = (Map<String, Object>) value;
            if (!(hook.get("result") instanceof Map<?, ?> data)) continue;
            Result result = result((Map<String, Object>) data);
            results.add(result);
            if (result.getError() != null) evidence.hookErrors.add(result.getError().getMessage());
        }
    }

    private static byte[] lastPng(Object value) {
        if (value instanceof Map<?, ?> map) {
            if ("image/png".equals(map.get("mime_type")) && map.get("data") instanceof String data)
                return Base64.getDecoder().decode(data);
            byte[] found = null;
            for (Object child : map.values()) {
                byte[] image = lastPng(child);
                if (image != null) found = image;
            }
            return found;
        }
        if (value instanceof List<?> list) {
            byte[] found = null;
            for (Object child : list) {
                byte[] image = lastPng(child);
                if (image != null) found = image;
            }
            return found;
        }
        return null;
    }
}
