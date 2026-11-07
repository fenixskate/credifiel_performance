package automation.pages;
import automation.utils.ElementActions;
import automation.utils.OdooNavigation;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;
import java.util.zip.ZipFile;

public final class UniversoCarteraPage {
    private final WebDriver driver;
    public UniversoCarteraPage(WebDriver driver) { this.driver = driver; }
    private final Path folder = Path.of("Documentos", "Reporte Universo Prestamos").toAbsolutePath();
    private String text(String name) { return driver.findElement(By.cssSelector("div[name='"+name+"']")).getText().trim(); }
    private long number(String name) { return Long.parseLong(text(name).replaceAll("[^0-9]", "")); }
    private void progress(String stateField, String doneField, String totalField, String pctField, String messageField) {
        long start = System.nanoTime();
        String[] previous = {""};
        new WebDriverWait(driver, Duration.ofMinutes(60)).pollingEvery(Duration.ofSeconds(3))
            .ignoring(StaleElementReferenceException.class).ignoring(org.openqa.selenium.NoSuchElementException.class)
            .until(d -> {
                String state = d.findElement(By.cssSelector("div[name='"+stateField+"'] span[raw-value]")).getDomAttribute("raw-value");
                String message = text(messageField);
                String status = state + " | " + text(doneField) + "/" + text(totalField) + " | " + message;
                if (!status.equals(previous[0])) {
                    System.out.println("[UNIVERSO] " + stateField + " | " + status + " | " + Duration.ofNanos(System.nanoTime()-start).toSeconds()+" s");
                    previous[0]=status;
                }
                if (Set.of("error","failed","cancelled").contains(state)) throw new AssertionError(message);
                if ("done".equals(state)) {
                    long done=number(doneField), total=number(totalField);
                    double pct=Double.parseDouble(d.findElement(By.cssSelector("div[name='"+pctField+"'] [aria-valuenow]")).getDomAttribute("aria-valuenow"));
                    if (total>0 && done==total && pct>=100) return true;
                }
                d.navigate().refresh();
                return false;
            });
    }
    public Path generarYDescargar() throws Exception {
        Files.createDirectories(folder);
        try {
            ElementActions elements=new ElementActions(driver);
            OdooNavigation navigation=new OdooNavigation(driver);
            navigation.open(automation.utils.OdooMenu.PRESTAMOS);
            navigation.open(automation.utils.OdooMenu.REPORTES_CARTERA);
            elements.click(By.cssSelector("button.o_list_button_add"));
            elements.select(By.id("tipo_reporte"),"Reporte Universo de Cartera");
            new WebDriverWait(driver,Duration.ofSeconds(30)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("fecha_fin")));
            elements.click(By.cssSelector("button[name='generar_reporte']"));
            new WebDriverWait(driver,Duration.ofMinutes(2)).ignoring(StaleElementReferenceException.class)
                .until(d -> d.findElements(By.cssSelector("div[name='gen_state'] span[raw-value]"))
                    .stream().anyMatch(e -> Set.of("running","done").contains(e.getDomAttribute("raw-value"))));
            progress("gen_state","gen_progress_done","gen_progress_total","gen_progress_pct","gen_progress_msg");
            Files.write(folder.resolve("generacion-completa.png"),((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES));
            elements.click(By.cssSelector("button[name='action_generar_excel_universo_async']"));
            new WebDriverWait(driver,Duration.ofMinutes(2)).ignoring(StaleElementReferenceException.class)
                .until(d -> d.findElements(By.cssSelector("div[name='export_state'] span[raw-value]"))
                    .stream().anyMatch(e -> Set.of("running","done").contains(e.getDomAttribute("raw-value"))));
            progress("export_state","export_done_rows","export_total_rows","export_pct","export_msg");
            Files.write(folder.resolve("exportacion-completa.png"),((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES));
            Path downloads=automation.driver.DriverManager.downloadDirectory(driver);
            Map<Path,java.nio.file.attribute.FileTime> before=new HashMap<>();
            try(var paths=Files.list(downloads)) {
                for(Path p:paths.filter(Files::isRegularFile).toList()) before.put(p,Files.getLastModifiedTime(p));
            }
            elements.click(By.cssSelector("button[name='action_descargar_zip']"));
            Path[] result={null};long[] size={-1};int[] stable={0};
            new WebDriverWait(driver,Duration.ofMinutes(10)).pollingEvery(Duration.ofSeconds(1)).until(d -> {
                try(var paths=Files.list(downloads)) {
                    var matches=paths.filter(Files::isRegularFile)
                        .filter(p -> p.toString().toLowerCase(Locale.ROOT).matches(".*\\.(zip|xlsx)$"))
                        .filter(p -> {try{return !before.containsKey(p)||!before.get(p).equals(Files.getLastModifiedTime(p));}catch(Exception e){return false;}}).toList();
                    if(matches.size()!=1)return false;
                    Path p=matches.getFirst();
                    if(Files.exists(Path.of(p+".part"))||Files.exists(Path.of(p+".crdownload")))return false;
                    long current=Files.size(p);
                    stable[0]=p.equals(result[0])&&current>0&&current==size[0]?stable[0]+1:0;
                    size[0]=current;result[0]=p;
                    return stable[0]>=2;
                }catch(java.io.IOException e){throw new IllegalStateException(e);}
            });
            boolean xlsx;
            try(ZipFile archive=new ZipFile(result[0].toFile())) {
                xlsx=archive.getEntry("xl/workbook.xml")!=null;
                if(!xlsx && archive.stream().noneMatch(e->e.getName().toLowerCase(Locale.ROOT).endsWith(".xlsx")))
                    throw new AssertionError("La descarga no contiene un reporte Excel");
            }
            Path destination=folder.resolve("ReporteUniversoCartera."+(xlsx?"xlsx":"zip"));
            Path temporary=folder.resolve("universo-actualizando."+(xlsx?"xlsx":"zip"));
            Files.copy(result[0],temporary,StandardCopyOption.REPLACE_EXISTING);
            Files.move(temporary,destination,StandardCopyOption.REPLACE_EXISTING);
            Files.writeString(Path.of("target","universo-descarga-actual.txt"),destination.toString());
            System.out.println("[UNIVERSO] DESCARGA COMPLETA: "+destination+" | bytes="+Files.size(destination));
            return destination;
        }catch(Exception|AssertionError failure){
            if(driver!=null){
                Files.write(folder.resolve("error-universo.png"),((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES));
                Files.writeString(folder.resolve("error-universo.html"),driver.getPageSource());
            }
            throw failure;
        }
    }
}
