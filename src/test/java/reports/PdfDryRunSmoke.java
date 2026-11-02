package reports;

import io.cucumber.plugin.event.EventPublisher;
import java.lang.reflect.Proxy;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicInteger;

/** Comprueba sin navegador que un dry-run no registre eventos ni cree evidencias. */
public final class PdfDryRunSmoke {
    public static void main(String[] args) throws Exception {
        String previous = System.getProperty("cucumber.execution.dry-run");
        try {
            System.setProperty("cucumber.execution.dry-run", "true");
            long before = directories();
            AtomicInteger registrations = new AtomicInteger();
            EventPublisher publisher = (EventPublisher) Proxy.newProxyInstance(
                    EventPublisher.class.getClassLoader(), new Class<?>[]{EventPublisher.class},
                    (proxy, method, values) -> { registrations.incrementAndGet(); return null; });
            new ScenarioPdfReporter().setEventPublisher(publisher);
            if (registrations.get() != 0 || directories() != before)
                throw new AssertionError("Dry-run genero evidencias o registro eventos del PDF");
            System.out.println("PASS: dry-run no crea carpeta, PDF ni registros PASSED de evidencia.");
        } finally {
            if (previous == null) System.clearProperty("cucumber.execution.dry-run");
            else System.setProperty("cucumber.execution.dry-run", previous);
        }
    }

    private static long directories() throws Exception {
        Path root = Path.of("output", "pdf");
        if (!Files.exists(root)) return 0;
        try (var paths = Files.list(root)) { return paths.filter(Files::isDirectory).count(); }
    }
}
