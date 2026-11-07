package verification;

import automation.driver.DriverManager;
import automation.pages.LoginPage;
import automation.selectors.LoginSelectors;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

/** Login local: menu demorado, HTML real opcional y rechazo explicito; no accede a Odoo. */
public final class LoginReadinessSmoke {
    public static void main(String[] args) throws Exception {
        String menu = "<nav class='o_main_navbar'><div class='o_navbar_apps_menu'>"
                + "<button class='dropdown-toggle' title='Menú de Inicio'>Inicio</button></div></nav>";
        String archivo = System.getProperty("verification.menu.html", "");
        if (!archivo.isBlank()) {
            String fuente = Files.readString(Path.of(archivo));
            var encabezado = java.util.regex.Pattern.compile("<header\\b[\\s\\S]*?</header>").matcher(fuente);
            if (!encabezado.find()) throw new AssertionError("HTML sin encabezado");
            menu = encabezado.group().replaceAll("<script\\b[\\s\\S]*?</script>", "");
        }
        String menuHtml = menu;
        HttpServer servidor = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        servidor.createContext("/", exchange -> {
            String ruta = exchange.getRequestURI().getPath();
            String error = ruta.equals("/rechazado")
                    ? "<p class='alert alert-danger' role='alert'>Nombre de usuario o contraseña incorrectos</p>" : "";
            String pagina;
            if (ruta.equals("/demorado")) {
                String literal = new org.openqa.selenium.json.Json().toJson(menuHtml);
                pagina = "<div id='menu'></div><script>setTimeout(()=>document.getElementById('menu').innerHTML="
                        + literal + ",11000)</script>";
            } else {
                String destino = ruta.equals("/login-invalido") ? "/rechazado" : "/demorado";
                pagina = "<div id='wrapwrap'><main><div><form class='oe_login_form' action='" + destino + "'>"
                        + "<div><input id='login'></div><div><input id='password' type='password'></div>"
                        + "<div><button>Entrar</button></div>" + error + "</form></div></main></div>";
            }
            byte[] respuesta = ("<!doctype html><meta charset='utf-8'>" + pagina).getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
            exchange.sendResponseHeaders(200, respuesta.length);
            try (var salida = exchange.getResponseBody()) { salida.write(respuesta); }
        });
        servidor.start();
        System.setProperty("headless", "true");
        System.setProperty("browser", "chrome");
        System.setProperty("timeout.explicit.seconds", "1");
        System.setProperty("login.timeout.seconds", "20");
        String base = "http://127.0.0.1:" + servidor.getAddress().getPort();
        try {
            DriverManager.start();
            var driver = DriverManager.getDriver();
            driver.get(base + "/login");
            new LoginPage(driver).signIn("control-local", "clave-local");
            if (driver.findElements(LoginSelectors.MENU_INICIO).size() != 1)
                throw new AssertionError("El menu no coincide de forma unica");
            System.out.println("PASS: menu reconocido tras 11 segundos, con el selector actual");
            driver.get(base + "/login-invalido");
            try {
                new LoginPage(driver).signIn("control-rechazado", "clave-local");
                throw new AssertionError("Se acepto un login rechazado");
            } catch (IllegalStateException esperado) {
                if (!esperado.getMessage().contains("Odoo rechazo el acceso de control-rechazado")) throw esperado;
            }
            System.out.println("PASS: rechazo de credenciales reportado sin confundirlo con ausencia del menu");
        } finally { DriverManager.quit(); servidor.stop(0); }
    }
}
