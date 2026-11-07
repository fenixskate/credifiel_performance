package verification;

import automation.driver.DriverManager;
import automation.pages.PromesasPage;
import java.nio.charset.StandardCharsets;

/** Valida los contadores sobre HTML local, sin crear promesas reales. */
public final class PromesasValidationSmoke {
    public static void main(String[] args) throws Exception {
        System.setProperty("headless", "true");
        java.nio.file.Files.createDirectories(java.nio.file.Path.of("target", "promesas-validation"));
        System.setProperty("verification.metrics.directory", "target/promesas-validation");
        try {
            DriverManager.start();
            for (int[] counts : new int[][]{{500,500,0}, {500,378,122}, {499,377,122}, {500,500,1}}) {
                int total = counts[0], success = counts[1], failed = counts[2];
                boolean valid = total == 500 && success + failed == 500;
                String html = "<span>Procesando</span><div name='line_count'><span>" + total + "</span></div>"
                        + "<div name='success_line_count'><span>" + success + "</span></div>"
                        + "<div name='failed_line_count'><span>" + failed + "</span></div>";
                DriverManager.getDriver().get("data:text/html;base64," + java.util.Base64.getEncoder().encodeToString(html.getBytes(StandardCharsets.UTF_8)));
                var page = new PromesasPage(DriverManager.getDriver());
                var started = PromesasPage.class.getDeclaredField("inicioProcesamiento");
                started.setAccessible(true);
                started.setLong(page, System.nanoTime());
                try {
                    page.validarExitosas();
                    if (!valid) throw new IllegalStateException("Se aceptaron contadores inconsistentes");
                } catch (AssertionError expected) {
                    if (valid) throw expected;
                }
            }
            var lecturas = new java.util.concurrent.atomic.AtomicInteger();
            var servidor = com.sun.net.httpserver.HttpServer.create(new java.net.InetSocketAddress("127.0.0.1", 0), 0);
            servidor.createContext("/progreso", exchange -> {
                int n = lecturas.getAndIncrement();
                String progreso = "<span>Procesando</span><div name='line_count'><span>500</span></div>"
                        + "<div name='success_line_count'><span>" + (n == 0 ? 0 : n == 1 ? 250 : 500) + "</span></div>"
                        + "<div name='failed_line_count'><span>0</span></div>";
                byte[] respuesta = progreso.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
                exchange.sendResponseHeaders(200, respuesta.length);
                try (var salida = exchange.getResponseBody()) { salida.write(respuesta); }
            });
            servidor.start();
            try {
            DriverManager.getDriver().get("http://127.0.0.1:" + servidor.getAddress().getPort() + "/progreso");
            var page = new PromesasPage(DriverManager.getDriver());
            var started = PromesasPage.class.getDeclaredField("inicioProcesamiento");
            started.setAccessible(true);
            long comienzo = System.nanoTime();
            started.setLong(page, comienzo);
            page.validarExitosas();
            if (lecturas.get() != 3)
                throw new AssertionError("Debe esperar los dos refrescos: 0, 250 y 500 procesadas");
            if (System.nanoTime() - comienzo < java.time.Duration.ofSeconds(4).toNanos())
                throw new AssertionError("Los refrescos deben estar separados al menos por 2 segundos");
            System.out.println("PASS: 500 procesadas aprueban aunque el layout diga Procesando; 250 y contadores inconsistentes no aprueban.");
            } finally { servidor.stop(0); }
        } finally { DriverManager.quit(); }
    }
}
