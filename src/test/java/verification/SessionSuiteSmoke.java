package verification;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

/** Ejecuta el motor real de Cucumber con hooks, capturas y PDF, contra localhost. */
public final class SessionSuiteSmoke {
    public static void main(String[] args) throws Exception {
        AtomicInteger logins = new AtomicInteger();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            String cookie = exchange.getRequestHeaders().getFirst("Cookie");
            boolean authenticated = cookie != null && cookie.contains("session=");
            if (exchange.getRequestURI().getPath().equals("/web/login")) authenticated = false;
            if (exchange.getRequestURI().getPath().equals("/signin")) {
                exchange.getResponseHeaders().add("Set-Cookie", "session=user" + logins.incrementAndGet() + "; Path=/; HttpOnly");
                authenticated = true;
            }
            String html = authenticated ? "<nav class='o_main_navbar'><div class='o_navbar_apps_menu'><button class='dropdown-toggle' title='Menú de Inicio'>Inicio</button></div></nav><div class='o_form_view'><span class='badge rounded-pill text-bg-success'>Terminado</span></div>"
                    : "<div id='wrapwrap'><main><div><form action='/signin'><div><input id='login'></div>"
                    + "<div><input id='password' type='password'></div><div><button>Entrar</button></div></form></div></main></div>";
            byte[] body = ("<!doctype html><meta charset='utf-8'>" + html).getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
            exchange.sendResponseHeaders(200, body.length);
            try (var output = exchange.getResponseBody()) { output.write(body); }
        });
        server.start();
        System.setProperty("base.url", "http://127.0.0.1:" + server.getAddress().getPort() + "/web/login");
        System.setProperty("headless", "true");
        System.setProperty("browser", "chrome");
        System.setProperty("login.password", "local-test-only");
        System.setProperty("execution.mode", "porUsuario");
        try {
            for (int threads : new int[]{3, 10}) {
                logins.set(0);
                SessionSuiteSteps.IDS.clear();
                var listener = new SummaryGeneratingListener();
                var request = LauncherDiscoveryRequestBuilder.request()
                        .selectors(DiscoverySelectors.selectClasspathResource("verification/session-suite.feature"))
                        .filters(org.junit.platform.launcher.EngineFilter.includeEngines("cucumber"))
                        .configurationParameter("cucumber.glue", "steps,hooks,verification")
                        .configurationParameter("cucumber.filter.tags", "@verificacionSesion")
                        .configurationParameter("cucumber.plugin", "pretty,reports.ScenarioPdfReporter")
                        .configurationParameter("cucumber.execution.parallel.enabled", "true")
                        .configurationParameter("cucumber.execution.parallel.config.strategy", "fixed")
                        .configurationParameter("cucumber.execution.parallel.config.fixed.parallelism", Integer.toString(threads))
                        .configurationParameter("cucumber.execution.parallel.config.fixed.max-pool-size", Integer.toString(threads))
                        .build();
                var launcher = LauncherFactory.create();
                launcher.registerTestExecutionListeners(listener);
                long started = System.nanoTime();
                launcher.execute(request);
                var summary = listener.getSummary();
                summary.printTo(new java.io.PrintWriter(System.out));
                summary.printFailuresTo(new java.io.PrintWriter(System.out));
                if (summary.getTestsSucceededCount() != 12 || summary.getTotalFailureCount() != 0
                        || logins.get() != 6 || SessionSuiteSteps.IDS.size() != 6)
                    throw new AssertionError("Suite local fallo con " + threads + " hilos. Logins=" + logins.get());
                System.out.println("PASS SUITE: " + threads + " hilos; 12 escenarios; 6 navegadores; 6 logins; "
                        + java.time.Duration.ofNanos(System.nanoTime() - started).toSeconds() + " s");
            }
        } finally {
            automation.driver.UserSessions.closeAll();
            server.stop(0);
        }
    }
}
