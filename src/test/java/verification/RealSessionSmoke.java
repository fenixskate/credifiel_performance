package verification;

import automation.driver.DriverManager;
import automation.driver.UserSessions;
import automation.selectors.LoginSelectors;
import config.ConfigManager;
import hooks.Hooks;
import steps.LoginSteps;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.nio.file.Files;
import java.nio.file.Path;

/** Solo login y navegacion; nunca carga archivos ni registra pagos. */
public final class RealSessionSmoke {
    public static void main(String[] args) throws Exception {
        System.setProperty("execution.mode", "porUsuario");
        String user = System.getProperty("verification.user", ConfigManager.get("login.user"));
        Path evidence = Path.of("target", "session-verification", ConfigManager.get("browser"));
        Files.createDirectories(evidence);
        try {
            LoginSteps steps = new LoginSteps();
            steps.openLogin();
            steps.iniciarSesionConUsuario(user);
            String id = ((RemoteWebDriver) DriverManager.getDriver()).getSessionId().toString();
            var cookie = DriverManager.getDriver().manage().getCookieNamed("session_id");
            System.out.println("FIRST: authenticated=" + authenticated() + ", loginForm=" + loginForm());
            Files.write(evidence.resolve("first.png"), ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BYTES));
            new Hooks().closeBrowser();
            UserSessions.acquire(user);
            System.out.println("BETWEEN: sameBrowser=" + id.equals(((RemoteWebDriver) DriverManager.getDriver()).getSessionId().toString())
                    + ", authenticated=" + authenticated() + ", loginForm=" + loginForm());
            // Reproduce la navegacion realizada al inicio de la siguiente fila.
            boolean requestedLogin = new automation.pages.LoginPage(DriverManager.getDriver()).openSession();
            System.out.println("NEXT: authenticated=" + authenticated() + ", loginForm=" + loginForm()
                    + ", requiresLogin=" + requestedLogin + ", sameCookie="
                    + java.util.Objects.equals(cookie, DriverManager.getDriver().manage().getCookieNamed("session_id")));
            Files.write(evidence.resolve("next.png"), ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BYTES));
            if (requestedLogin || !authenticated()) throw new AssertionError("La siguiente fila vuelve al login");
            new Hooks().closeBrowser();
            steps = new LoginSteps();
            steps.openLogin();
            steps.iniciarSesionConUsuario(user);
            if (!id.equals(((RemoteWebDriver) DriverManager.getDriver()).getSessionId().toString())
                    || !authenticated() || loginForm()) throw new AssertionError("No se reutilizo la sesion con los pasos reales");
            Files.writeString(evidence.resolve("result.txt"),
                    "PASS: mismo navegador entre filas; cookie session_id conservada; inicio sin formulario de login; pasos LoginSteps reutilizan la sesion.\n"
                    + "Solo login y navegacion. No se cargaron archivos ni se registraron pagos.\n");
            System.out.println("PASS: sesion real conservada entre escenarios sin repetir login.");
        } finally {
            if (DriverManager.isStarted()) UserSessions.release();
            UserSessions.closeAll();
        }
    }
    private static boolean authenticated() {
        return DriverManager.getDriver().findElements(LoginSelectors.MENU_INICIO).stream().anyMatch(org.openqa.selenium.WebElement::isDisplayed);
    }
    private static boolean loginForm() {
        return DriverManager.getDriver().findElements(LoginSelectors.USERNAME).stream().anyMatch(org.openqa.selenium.WebElement::isDisplayed);
    }
}
