package automation.driver;

import config.ConfigManager;
import java.time.Duration;
import java.util.Locale;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public final class DriverManager {
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
    private static final java.util.Map<WebDriver, java.nio.file.Path> DOWNLOADS =
            java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());

    private DriverManager() {}

    public static void start() {
        if (isStarted()) {
            throw new IllegalStateException("Ya existe un navegador para este escenario");
        }
        boolean headless = ConfigManager.booleanValue("headless");
        String browser = ConfigManager.get("browser").toLowerCase(Locale.ROOT);
        java.nio.file.Path downloads;
        try {
            java.nio.file.Path raiz = java.nio.file.Path.of("target", "browser-downloads").toAbsolutePath();
            java.nio.file.Files.createDirectories(raiz);
            downloads = java.nio.file.Files.createTempDirectory(raiz, "browser-");
        } catch (java.io.IOException e) { throw new java.io.UncheckedIOException("No se pudo crear la carpeta de descargas", e); }
        WebDriver driver = switch (browser) {
            case "chrome" -> {
                ChromeOptions options = new ChromeOptions();
                org.openqa.selenium.logging.LoggingPreferences logs = new org.openqa.selenium.logging.LoggingPreferences();
                logs.enable(org.openqa.selenium.logging.LogType.BROWSER, java.util.logging.Level.ALL);
                options.setCapability("goog:loggingPrefs", logs);
                if (headless) options.addArguments("--headless=new");
                options.addArguments("--window-size=1440,900");
                options.setExperimentalOption("prefs", java.util.Map.of(
                        "download.default_directory", downloads.toString(),
                        "download.prompt_for_download", false));
                yield new ChromeDriver(options);
            }
            case "firefox" -> {
                FirefoxOptions options = new FirefoxOptions();
                options.addPreference("browser.download.folderList", 2);
                options.addPreference("browser.download.dir", downloads.toString());
                options.addPreference("browser.download.useDownloadDir", true);
                options.addPreference("browser.helperApps.neverAsk.saveToDisk",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/zip,application/octet-stream");
                if (headless) options.addArguments("-headless");
                yield new FirefoxDriver(options);
            }
            default -> throw new IllegalArgumentException("Navegador no soportado: " + browser);
        };
        DRIVER.set(driver);
        DOWNLOADS.put(driver, downloads);
        try {
            driver.manage().timeouts().pageLoadTimeout(
                    Duration.ofSeconds(ConfigManager.positiveInt("timeout.pageLoad.seconds")));
            driver.manage().window().setSize(new org.openqa.selenium.Dimension(1440, 900));
        } catch (RuntimeException e) {
            try { quit(); } catch (RuntimeException cleanup) { e.addSuppressed(cleanup); }
            throw e;
        }
    }

    public static boolean isStarted() {
        return DRIVER.get() != null;
    }

    static void bind(WebDriver driver) {
        DRIVER.set(driver);
    }

    static void unbind() {
        DRIVER.remove();
    }

    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) throw new IllegalStateException("El navegador no esta iniciado");
        return driver;
    }

    public static java.nio.file.Path downloadDirectory(WebDriver driver) {
        java.nio.file.Path folder = DOWNLOADS.get(driver);
        return folder == null ? java.nio.file.Path.of(System.getProperty("user.home"), "Downloads") : folder;
    }

    public static void quit() {
        WebDriver driver = DRIVER.get();
        try {
            if (driver != null) driver.quit();
        } finally {
            DOWNLOADS.remove(driver);
            DRIVER.remove();
        }
    }
}
