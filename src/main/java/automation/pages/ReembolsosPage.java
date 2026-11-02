package automation.pages;

import automation.selectors.ReembolsosSelectors;
import automation.utils.ElementActions;
import automation.utils.OdooNavigation;

import java.nio.file.Path;
import java.time.Duration;
import java.text.Normalizer;
import java.util.Locale;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

public final class ReembolsosPage {
    private final WebDriver driver;
    private final ElementActions elements;
    private final WebDriverWait wait;

    public ReembolsosPage(WebDriver driver) {
        this.driver = driver;
        elements = new ElementActions(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(60));
        wait.ignoring(StaleElementReferenceException.class);
    }

    public void abrir() {
        new OdooNavigation(driver).open(automation.utils.OdooMenu.REEMBOLSOS);
    }

    public void nuevo() {
        elements.click(ReembolsosSelectors.NUEVO);
        wait.until(ExpectedConditions.presenceOfElementLocated(ReembolsosSelectors.MASIVO));
    }

    public void seleccionarMasivo() {
        elements.check(ReembolsosSelectors.MASIVO);
    }

    public void seleccionarExcel() {
        elements.check(ReembolsosSelectors.EXCEL);
    }

    public void seleccionarSubtipo(String subtipo) {
        elements.select(ReembolsosSelectors.SUBTIPO, subtipo);
    }

    public void subirArchivo(String nombre) {
        Path base = Path.of(System.getProperty("user.dir"), "Documentos", "Insumos Flujo 6").toAbsolutePath().normalize();
        Path archivo = base.resolve(nombre).normalize();
        if (!archivo.startsWith(base)) throw new IllegalArgumentException("Ruta de archivo fuera del Flujo 6");
        WebElement campo = wait.until(d -> {
            var campos = d.findElements(By.cssSelector(".o_form_view .o_field_binary")).stream()
                    .filter(WebElement::isDisplayed)
                    .filter(e -> !e.findElements(By.cssSelector("input[type='file']")).isEmpty()).toList();
            if (campos.size() > 1)
                throw new IllegalStateException("Hay varios campos de archivo visibles; se requiere identificar el layout");
            return campos.isEmpty() ? null : campos.getFirst();
        });
        String nombreCampo = campo.getDomAttribute("name");
        if (nombreCampo == null || !nombreCampo.matches("[A-Za-z0-9_]+"))
            throw new IllegalStateException("El campo de archivo no tiene nombre válido");
        By contenedor = By.cssSelector(".o_form_view div[name='" + nombreCampo + "']");
        elements.upload(By.cssSelector(".o_form_view div[name='" + nombreCampo + "'] input[type='file']"), archivo);
        wait.until(d -> d.findElements(contenedor).stream().anyMatch(e -> e.getText().contains(nombre)
                || e.findElements(By.cssSelector("input:not([type='file'])")).stream()
                .anyMatch(i -> nombre.equals(i.getDomProperty("value")))));
        System.out.println("[REEMBOLSOS] Archivo adjunto: " + archivo);
    }

    public void leerArchivo() {
        elements.click(ReembolsosSelectors.LEER);
        esperarSinCarga();
        wait.until(ExpectedConditions.elementToBeClickable(ReembolsosSelectors.AVANZAR));
    }

    public void avanzarAutorizacion() {
        elements.click(ReembolsosSelectors.AVANZAR);
        esperarSinCarga();
    }

    public void validarAutorizacion() {
        validarEtapa("Autorización");
    }

    public void seleccionarTipoRechazo(String tipo) {
        elements.select(ReembolsosSelectors.TIPO_RECHAZO, tipo);
        wait.until(d -> new Select(d.findElement(ReembolsosSelectors.TIPO_RECHAZO)).getFirstSelectedOption().getText().equals(tipo));
    }

    public void seleccionarMotivoDocumentos(String motivo) {
        elements.select(ReembolsosSelectors.MOTIVO_DOCUMENTOS, motivo);
        wait.until(d -> new Select(d.findElement(ReembolsosSelectors.MOTIVO_DOCUMENTOS)).getFirstSelectedOption().getText().equals(motivo));
    }

    public void aprobarSubdirectorOperaciones() {
        elements.click(ReembolsosSelectors.SUBDIRECTOR_OPERACIONES);
        esperarSinCarga();
        wait.until(ExpectedConditions.elementToBeClickable(ReembolsosSelectors.AVANZAR_LIBERACION));
    }

    public void avanzarLiberacion() {
        elements.click(ReembolsosSelectors.AVANZAR_LIBERACION);
        esperarSinCarga();
    }

    public void avanzarAprobado() {
        elements.click(ReembolsosSelectors.AVANZAR_APROBADO);
        esperarSinCarga();

    }

    public void validarEtapa(String etapa) {
        String esperada = normalizar(etapa);
        wait.until(d -> d.findElements(By.cssSelector(".o_field_statusbar [aria-checked='true']"))
                .stream().filter(WebElement::isDisplayed).anyMatch(e -> {
                    String actual = normalizar(e.getText());
                    return actual.equals(esperada) || actual.equals("en " + esperada);
                }));
        System.out.println("[REEMBOLSOS] Reembolso masivo en " + etapa);
    }

    public void validarEstatusDetalle() {
        By tabla = By.xpath("//table[contains(@class,'o_list_table')][.//th[@data-name='cf_estatus_det']]");
        java.util.concurrent.atomic.AtomicReference<java.util.List<String>> observados =
                new java.util.concurrent.atomic.AtomicReference<>(java.util.List.of());
        long[] ultimoRefresco = {System.nanoTime()};
        boolean[] refrescado = {false};
        int segundos = Integer.getInteger("reembolsos.estatus.timeout.seconds", 300);
        if (segundos <= 0) throw new IllegalArgumentException("El tiempo de espera de reembolsos debe ser positivo");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(segundos))
                    .ignoring(StaleElementReferenceException.class).until(d -> {
                boolean cargando = d.findElements(By.cssSelector(
                        ".o_loading_indicator, .o_blockUI, .blockUI.blockOverlay"))
                        .stream().anyMatch(WebElement::isDisplayed);
                if (cargando) return false;
                if (System.nanoTime() - ultimoRefresco[0] >= Duration.ofSeconds(10).toNanos()) {
                    System.out.println("[REEMBOLSOS] Reconsultando estados tras esperar procesamiento: " + observados.get());
                    d.navigate().refresh();
                    ultimoRefresco[0] = System.nanoTime();
                    refrescado[0] = true;
                    return false;
                }
                if (!refrescado[0]) return false;
                var tablas = d.findElements(tabla).stream().filter(WebElement::isDisplayed).toList();
                if (tablas.size() != 1) return false;
                var filas = tablas.getFirst().findElements(By.cssSelector("tbody tr.o_data_row"))
                        .stream().filter(WebElement::isDisplayed).toList();
                if (filas.isEmpty()) return false;
                var estados = new java.util.ArrayList<String>();
                for (WebElement fila : filas) {
                    var celdas = fila.findElements(By.cssSelector("td[name='cf_estatus_det']"));
                    estados.add(celdas.size() == 1 ? celdas.getFirst().getText().trim() : "SIN ESTATUS");
                }
                observados.set(java.util.List.copyOf(estados));
                return estados.stream().map(this::normalizar)
                        .allMatch(e -> e.equals("en confirmacion stp") || e.equals("rechazado"));
            });
        } catch (TimeoutException e) {
            throw new AssertionError("La tabla de reembolsos debe contener registros y todos deben estar "
                    + "En confirmación STP o Rechazado. Estados visibles: " + observados.get(), e);
        }
        long rechazados = observados.get().stream().map(this::normalizar).filter("rechazado"::equals).count();
        System.out.println("[REEMBOLSOS] Detalle validado: " + observados.get().size()
                + " filas visibles | En confirmación STP=" + (observados.get().size() - rechazados)
                + " | Rechazado=" + rechazados);
    }
    private String normalizar(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                .trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }

    private void esperarSinCarga() {
        new WebDriverWait(driver, Duration.ofMinutes(5)).until(d -> d.findElements(By.cssSelector(
                ".o_loading_indicator, .o_blockUI, .blockUI.blockOverlay")).stream().noneMatch(WebElement::isDisplayed));
    }
}
