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
    private static int cantidadPreparada;
    private PreparacionPromesas() {}

    public static Path actualizarUnaVez(WebDriver driver) throws Exception {
        return actualizarUnaVez(driver,Integer.getInteger("promesas.archivos",40));
    }
    public static Path actualizarUnaVez(WebDriver driver,int cantidad) throws Exception {
        if(cantidad<1)throw new IllegalArgumentException("Cantidad de archivos invalida");
        CompletableFuture<Path> resultado;
        boolean encargado;
        synchronized (PreparacionPromesas.class) {
            encargado = preparacion == null;
            if (encargado) {preparacion = new CompletableFuture<>();cantidadPreparada=cantidad;}
            else if(cantidadPreparada!=cantidad)throw new IllegalStateException("La preparacion compartida solicita "+cantidadPreparada+" archivos, no "+cantidad);
            resultado = preparacion;
        }
        if (encargado) {
            try {
                Path trabajo = Path.of("target", "preparacion-promesas", UUID.randomUUID().toString()).toAbsolutePath();
                Files.createDirectories(trabajo);
                System.out.println("[PREPARACION PROMESAS] Generando Universo y actualizando "+cantidad+" archivos una sola vez");
                Path reporte = new UniversoCarteraPage(driver).generarYDescargar();
                Path templates = Path.of("Documentos", "Insumos Flujo 4", "Template");
                PromesasExcelUtils.preparar(reporte, trabajo, templates.resolve("Generados"),
                        templates.resolve("Respaldos"), templates.resolve("Generados"), cantidad);
                resultado.complete(trabajo);
                System.out.println("[PREPARACION PROMESAS] LISTO: "+cantidad+" archivos, 500 activos por archivo, "+(cantidad*500)+" creditos unicos");
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

}
