package automation.utils;

import automation.pages.UniversoCarteraPage;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import org.openqa.selenium.WebDriver;

/** Una preparación compartida por todas las filas CP03 de la misma corrida. */
public final class PreparacionPromesas {
    private static CompletableFuture<Path> preparacion;
    private PreparacionPromesas() {}

    public static Path actualizarUnaVez(WebDriver driver) throws Exception {
        CompletableFuture<Path> resultado;
        boolean encargado;
        synchronized (PreparacionPromesas.class) {
            encargado = preparacion == null;
            if (encargado) preparacion = new CompletableFuture<>();
            resultado = preparacion;
        }
        if (encargado) {
            try {
                Path runtime = Path.of(System.getProperty("promesas.runtime.root", Path.of(
                        System.getProperty("user.home"), ".cache", "codex-runtimes", "codex-primary-runtime", "dependencies").toString()));
                Path python = runtime.resolve("python/python.exe");
                Path node = runtime.resolve("node/bin/node.exe");
                Path modules = runtime.resolve("node/node_modules");
                if (!Files.isRegularFile(python) || !Files.isRegularFile(node) || !Files.isDirectory(modules))
                    throw new IllegalStateException("No se encontró el runtime de hojas de cálculo; configura promesas.runtime.root");
                Path trabajo = Path.of("target", "preparacion-promesas", UUID.randomUUID().toString()).toAbsolutePath();
                Files.createDirectories(trabajo);
                System.out.println("[PREPARACION PROMESAS] Generando Universo y actualizando 20 archivos una sola vez");
                Path reporte = new UniversoCarteraPage(driver).generarYDescargar();
                ejecutar(trabajo, "seleccion", python.toString(), "scripts/promesas/preparar.py", reporte.toString(), trabajo.toString());
                ejecutar(trabajo, "generacion", node.toString(), "scripts/promesas/generar.mjs", trabajo.toString(), modules.toString());
                ejecutar(trabajo, "verificacion", python.toString(), "scripts/promesas/verificar_publicar.py", trabajo.toString());
                resultado.complete(trabajo);
                System.out.println("[PREPARACION PROMESAS] LISTO: 20 archivos, 500 activos por archivo, 10000 créditos únicos");
            } catch (Exception | Error failure) {
                resultado.completeExceptionally(failure);
                throw failure;
            }
        } else {
            System.out.println("[PREPARACION PROMESAS] Esperando preparación compartida | " + Thread.currentThread().getName());
        }
        long limite = System.nanoTime() + Duration.ofHours(3).toNanos();
        while (System.nanoTime() < limite) {
            try { return resultado.get(30, TimeUnit.SECONDS); }
            catch (TimeoutException esperando) {
                System.out.println("[PREPARACION PROMESAS] El reporte y los archivos siguen en preparación");
            } catch (ExecutionException failure) {
                throw new IllegalStateException("Falló la preparación compartida; no se cargarán archivos anteriores", failure.getCause());
            } catch (InterruptedException failure) {
                Thread.currentThread().interrupt();
                throw failure;
            }
        }
        throw new IllegalStateException("Se agotó el tiempo de preparación compartida de promesas");
    }

    private static void ejecutar(Path trabajo, String etapa, String... comando) throws Exception {
        Path log = trabajo.resolve(etapa + ".log");
        Process process = new ProcessBuilder(comando).redirectErrorStream(true).redirectOutput(log.toFile()).start();
        long limite = System.nanoTime() + Duration.ofMinutes(30).toNanos();
        try {
            while (!process.waitFor(30, TimeUnit.SECONDS)) {
                System.out.println("[PREPARACION PROMESAS] " + etapa + " en curso | " + log);
                if (System.nanoTime() >= limite) throw new IllegalStateException("Tiempo agotado en " + etapa);
            }
            if (process.exitValue() != 0)
                throw new IllegalStateException("Falló " + etapa + "; revisar " + log + "\n" + Files.readString(log));
            System.out.println("[PREPARACION PROMESAS] " + etapa + " completada");
        } finally {
            if (process.isAlive()) process.destroyForcibly();
        }
    }
}
