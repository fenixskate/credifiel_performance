package verification;

import automation.driver.DriverManager;
import automation.pages.PagosPage;
import automation.selectors.PagosSelectors;
import automation.utils.PaymentStateWait;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import org.openqa.selenium.TimeoutException;

/** Regresion de esperas de pagos, exclusivamente con HTML local. */
public final class PaymentWaitSmoke {
    public static void main(String[] args) throws Exception {
        AtomicInteger requests = new AtomicInteger();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        String finished = "<span class='badge rounded-pill text-bg-success'>Terminado</span>";
        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            String content = switch (path) {
                case "/instant" -> finished;
                case "/async" -> "<script>setTimeout(()=>document.querySelector('.o_form_view').innerHTML=\"" + finished + "\",800)</script>";
                case "/refresh" -> requests.incrementAndGet() > 1 ? finished : "<span class='badge'>Procesando</span>";
                case "/error" -> "<div class='modal o_technical_modal'><div class='modal-body'>Error de prueba local</div></div>";
                default -> "<span class='badge'>Procesando</span>";
            };
            byte[] body = ("<!doctype html><meta charset='utf-8'><div class='o_form_view'>" + content + "</div>").getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
            exchange.sendResponseHeaders(200, body.length);
            try (var output = exchange.getResponseBody()) { output.write(body); }
        });
        server.start();
        System.setProperty("headless", "true");
        System.setProperty("payments.finished.timeout.seconds", "5");
        String base = "http://127.0.0.1:" + server.getAddress().getPort();
        try {
            DriverManager.start();
            var driver = DriverManager.getDriver();
            driver.get(base + "/instant");
            long start = System.nanoTime();
            new PagosPage(driver).validarArchivoTerminado();
            check(Duration.ofNanos(System.nanoTime() - start).toSeconds() < 3, "Terminado visible no debe esperar minutos");
            driver.get(base + "/async");
            new PagosPage(driver).validarArchivoTerminado();
            System.setProperty("payments.refresh.seconds", "1");
            driver.get(base + "/refresh");
            new PagosPage(driver).validarArchivoTerminado();
            check(requests.get() == 2, "Debe bastar un refresco para detectar Terminado");
            driver.get(base + "/error");
            try {
                new PaymentStateWait(driver).until(PagosSelectors.ESTADO_TERMINADO, "Terminado", Duration.ofSeconds(2));
                throw new AssertionError("Debe detectar el error de Odoo");
            } catch (IllegalStateException expected) {
                check(expected.getMessage().contains("Error de prueba local"), "Debe conservar el mensaje del servidor");
            }
            System.setProperty("payments.refresh.seconds", "15");
            driver.get(base + "/never");
            start = System.nanoTime();
            try {
                new PaymentStateWait(driver).until(PagosSelectors.ESTADO_TERMINADO, "Terminado", Duration.ofSeconds(2));
                throw new AssertionError("No debe aprobar un archivo que sigue Procesando");
            } catch (TimeoutException expected) {
                check(expected.getMessage().contains("Procesando"), "Debe informar el ultimo estado");
                check(Duration.ofNanos(System.nanoTime() - start).toSeconds() < 5, "La espera debe estar acotada");
            }
            System.out.println("PASS: Terminado visible, actualizacion asincrona, refresco necesario, error Odoo y timeout con ultimo estado.");
        } finally {
            DriverManager.quit();
            server.stop(0);
        }
    }
    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
