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
                String html = "<div name='line_count'><span>" + total + "</span></div>"
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
            System.out.println("PASS: 500 exitosas y mezclas que suman 500 aprueban; total incompleto y suma excesiva fallan.");
        } finally { DriverManager.quit(); }
    }
}
