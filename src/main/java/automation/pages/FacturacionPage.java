
package automation.pages;

import automation.selectors.FacturacionSelectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

public class FacturacionPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final automation.utils.ElementActions elements;
    private Path archivoDescargado;
    private String bancoSeleccionado;
    private int totalGenerado;
    private String solicitudId;
    private String referenciaDeposito;
    private String reciboDeposito;
    private java.math.BigDecimal importeDeposito;
    private String fechaDeposito;
    private long inicioConciliacion;
    private java.time.LocalDateTime fechaInicioConciliacion;
    private String ejecutorConciliacion;
    private String procesoConciliacion;
    private static final Object METRICAS_LOCK = new Object();

    private static final Path CARPETA_DOCUMENTOS =
            Path.of(System.getProperty("user.dir"), "Documentos");

    public FacturacionPage(WebDriver driver) {
        this.driver = driver;
        this.elements = new automation.utils.ElementActions(driver);
        this.wait = new WebDriverWait(
                driver, Duration.ofSeconds(30));
        this.wait.ignoring(org.openqa.selenium.StaleElementReferenceException.class);
    }


    public void ingresarDomiciliacion() {
        new automation.utils.OdooNavigation(driver).open(
                automation.utils.OdooMenu.DOMICILIACION);

        System.out.println("[OK] Aplicación Domiciliación abierta");
    }


    public void ingresarSolicitudFacturacion() {

        new automation.utils.OdooNavigation(driver).open(automation.utils.OdooMenu.SOLICITUD_FACTURACION);

        System.out.println("[OK] Pantalla Solicitud de Facturación abierta");
    }


    public void crearSolicitud() {

        wait.until(ExpectedConditions.elementToBeClickable(
                FacturacionSelectors.BOTON_NUEVO)).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                FacturacionSelectors.BANCO));

        System.out.println("[OK] Nueva solicitud de facturación");
    }

    public void seleccionarBanco(String banco) {

        WebElement elemento = wait.until(
                ExpectedConditions.elementToBeClickable(
                        FacturacionSelectors.BANCO));

        Select select = new Select(elemento);

        if ("BBVA".equalsIgnoreCase(banco)) {
            select.selectByVisibleText("BBVA Bancomer");
        } else {
            select.selectByVisibleText(banco);
        }

        System.out.println("[OK] Banco seleccionado: " + banco);
        bancoSeleccionado = banco;
        if (Boolean.getBoolean("debug.cp05.bank.pause")) {
            Path marker = Path.of("target", "cp05-banco-" + banco + ".pause");
            try {
                Files.writeString(marker, select.getFirstSelectedOption().getText());
                Files.write(Path.of("target", "cp05-banco-" + banco + ".png"),
                        automation.utils.ScreenshotUtils.capture(driver));
                System.out.println("[BREAKPOINT BANCO] " + banco + ". Elimina " + marker + " para continuar.");
                while (Files.exists(marker)) Thread.sleep(500);
            } catch (java.io.IOException e) {
                throw new IllegalStateException("No se pudo guardar la pausa del banco", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Pausa del banco interrumpida", e);
            }
        }
    }

    public void seleccionarServicio(String servicio) {
        if (!"BBVA".equalsIgnoreCase(bancoSeleccionado)) {
            System.out.println("[DOMI] Servicio no aplica a " + bancoSeleccionado);
            return;
        }

        WebElement elemento = wait.until(
                ExpectedConditions.elementToBeClickable(
                        FacturacionSelectors.SERVICIO));

        Select select = new Select(elemento);

        if ("Interbancario".equalsIgnoreCase(servicio)) {
            select.selectByVisibleText("Interbancario");
        } else {
            select.selectByVisibleText(servicio);
        }

        System.out.println("[OK] Servicio seleccionado: " + servicio);
    }

    private void activarCheckbox(org.openqa.selenium.By selector) {
        elements.check(selector);
    }

    public void activarBancos() {

        activarCheckbox(FacturacionSelectors.INCLUIR_BANCOS);
        activarCheckbox(FacturacionSelectors.INCLUIR_BANORTE);
        activarCheckbox(FacturacionSelectors.INCLUIR_BANAMEX);
        activarCheckbox(FacturacionSelectors.INCLUIR_SANTANDER);
        activarCheckbox(FacturacionSelectors.CREDITOS_NO_DOMICILIACION);

        System.out.println("[OK] Los cinco checkbox están activados");
    }

    public void validarOpcionesDesmarcadas() {
        for (By selector : java.util.List.of(FacturacionSelectors.INCLUIR_BANCOS,
                FacturacionSelectors.INCLUIR_BANORTE, FacturacionSelectors.INCLUIR_BANAMEX,
                FacturacionSelectors.INCLUIR_SANTANDER)) {
            for (WebElement checkbox : driver.findElements(selector)) {
                if (checkbox.isSelected()) throw new AssertionError("Checkbox marcado en Tradicional: " + selector);
            }
        }
    }

    public void activarImportarCreditos() {
        activarCheckbox(FacturacionSelectors.CREDITOS_NO_DOMICILIACION);
    }

    public void subirArchivo(String nombreArchivo) {

        Path ruta = CARPETA_DOCUMENTOS.resolve("Insumos Flujo 5").resolve("Archivos domi").resolve(nombreArchivo)
                .toAbsolutePath().normalize();

        if (!Files.isRegularFile(ruta)) {
            throw new IllegalStateException(
                    "No existe el archivo: " + ruta);
        }

        elements.upload(FacturacionSelectors.SUBIR_ARCHIVO, ruta);
        wait.until(d -> d.findElements(By.cssSelector("div[name='layout_file']")).stream()
                .anyMatch(e -> e.getText().contains(nombreArchivo)
                        || e.findElements(By.cssSelector("input")).stream()
                        .anyMatch(field -> {
                            String value = field.getDomProperty("value");
                            return value != null && value.endsWith(nombreArchivo);
                        })));

        System.out.println("[OK] Archivo cargado: " + ruta);
    }

    public void completarFechasSantander() {
        if (!"Santander".equalsIgnoreCase(bancoSeleccionado)) return;
        java.time.LocalDateTime ahora = java.time.LocalDateTime.now(java.time.ZoneId.of("America/Mexico_City"));
        completarFecha("Hora y Fecha inicio de cobranza", ahora.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        completarFecha("Fecha de Aplicación de Pago", ahora.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    private void completarFecha(String etiqueta, String valor) {
        WebElement label = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//label[normalize-space()='" + etiqueta + "']")));
        By selector = By.id(label.getDomAttribute("for"));
        elements.type(selector, valor);
        driver.findElement(selector).sendKeys(org.openqa.selenium.Keys.TAB);
        System.out.println("[DOMI] " + etiqueta + ": " + driver.findElement(selector).getDomProperty("value"));
    }

    private void confirmar(String boton, String mensaje) {
        elements.click(By.cssSelector("button[name='" + boton + "']"));
        By modal = By.xpath("//div[contains(@class,'modal-content')][.//p[normalize-space()='" + mensaje + "']]");
        wait.until(ExpectedConditions.visibilityOfElementLocated(modal));
        elements.click(By.xpath("//div[contains(@class,'modal-content')][.//p[normalize-space()='" + mensaje
                + "']]//button[normalize-space()='De acuerdo' or normalize-space()='Aceptar']"));
        new WebDriverWait(driver, Duration.ofMinutes(5)).until(ExpectedConditions.invisibilityOfElementLocated(modal));
    }

    public void generarDatos() {
        WebElement boton = wait.until(d -> d.findElements(By.cssSelector("button[name^='cf_generar_datos']"))
                .stream().filter(WebElement::isDisplayed).filter(WebElement::isEnabled).findFirst().orElse(null));
        confirmar(boton.getDomAttribute("name"), "¿Desea generar el detalle de los préstamos a domiciliar?");
    }

    public void validarRegistros(int esperados) {
        wait.until(d -> d.findElements(By.cssSelector(".o_pager_limit")).stream()
                .filter(WebElement::isDisplayed)
                .anyMatch(e -> e.getText().replaceAll("[^0-9]", "").equals(Integer.toString(esperados))));
        System.out.println("[DOMI] Registros generados: " + esperados);
    }

    public void validarCargaCompleta() {
        new WebDriverWait(driver, Duration.ofMinutes(5)).until(d -> {
            boolean cargando = d.findElements(By.cssSelector(
                    ".o_loading_indicator, .o_blockUI, .blockUI.blockOverlay, .o_spinner, .fa-spinner"))
                    .stream().anyMatch(WebElement::isDisplayed);
            if (cargando) return false;
            // El botón de la siguiente etapa evita aceptar la ausencia del spinner antes de empezar la carga.
            boolean listo = d.findElements(By.cssSelector("button[name='generar_archivo']")).stream()
                    .anyMatch(e -> e.isDisplayed() && e.isEnabled());
            if (!listo) return false;
            totalGenerado = d.findElements(By.cssSelector(".o_field_one2many .o_pager_limit")).stream()
                    .map(WebElement::getText).map(s -> s.replaceAll("[^0-9]", ""))
                    .filter(s -> !s.isBlank()).mapToInt(Integer::parseInt).max().orElse(0);
            if (totalGenerado == 0) {
                // Odoo oculta el paginador cuando todos los registros caben en la tabla.
                totalGenerado = d.findElements(By.cssSelector(".o_field_one2many table")).stream()
                        .filter(WebElement::isDisplayed)
                        .mapToInt(tabla -> (int) tabla.findElements(By.cssSelector("tbody tr.o_data_row"))
                                .stream().filter(WebElement::isDisplayed).count()).max().orElse(0);
            }
            return totalGenerado > 0;
        });
        System.out.println("[DOMI] Carga finalizada sin spinner | " + bancoSeleccionado + " | Registros=" + totalGenerado);
    }

    public void validarRespuestaCompleta() {
        if (totalGenerado <= 0) throw new IllegalStateException("Primero debe validarse la carga de datos");
        validarRespuesta(totalGenerado);
    }

    public void guardarSolicitud() {
        String titulo = wait.until(d -> d.findElements(By.cssSelector(".breadcrumb .text-truncate, .o_breadcrumb .text-truncate"))
                .stream().map(WebElement::getText).filter(t -> t.matches(".*Solicitud\\s*#\\d+.*"))
                .findFirst().orElse(null));
        java.util.regex.Matcher match = java.util.regex.Pattern.compile("Solicitud\\s*#(\\d+)").matcher(titulo);
        if (!match.find()) throw new AssertionError("No se encontró el ID de la solicitud");
        solicitudId = match.group(1);
        try {
            Files.writeString(Path.of("target", "cp05-solicitud-" + bancoSeleccionado + "-" + solicitudId + ".txt"),
                    "Solicitud #" + solicitudId + System.lineSeparator() + "Banco: " + bancoSeleccionado);
        } catch (java.io.IOException e) { throw new IllegalStateException("No se pudo guardar la solicitud", e); }
        System.out.println("[DOMI] Solicitud guardada: #" + solicitudId);
    }

    public void abrirCuentaDomiciliacion() {
        new automation.utils.OdooNavigation(driver).open(
                automation.utils.OdooMenu.CUENTAS);
        new CuentasPage(driver).seleccionarCuenta("Domiciliación");
    }

    public void cargarDepositoBbva() {
        boolean banamex = "Banamex".equalsIgnoreCase(bancoSeleccionado);
        boolean santander = "Santander".equalsIgnoreCase(bancoSeleccionado);
        if (!"BBVA".equalsIgnoreCase(bancoSeleccionado) && !banamex && !santander)
            throw new IllegalStateException("No hay depósito configurado para " + bancoSeleccionado);
        Path base = CARPETA_DOCUMENTOS.resolve("Insumos Flujo 5").resolve("Archivos Layout");
        Path archivo;
        try {
            archivo = santander ? automation.utils.DomiciliacionDepositoUtils.generarSantander(
                    base.resolve("Santander_9783.csv"), base.resolve("Generados"))
                    : banamex ? automation.utils.DomiciliacionDepositoUtils.generarBanamex(
                    base.resolve("Banamex 5794.csv"), base.resolve("Generados"))
                    : automation.utils.DomiciliacionDepositoUtils.generar(
                    base.resolve("bbva_historico_5481.csv"), base.resolve("Generados"));
            String contenido = Files.readString(archivo, java.nio.charset.StandardCharsets.ISO_8859_1);
            if (santander) {
                String[] campos = contenido.split("\\r?\\n")[1].split(",", -1);
                referenciaDeposito = campos[4].trim();
                importeDeposito = new java.math.BigDecimal(campos[6].trim()).abs();
                String fecha = String.format("%08d", Integer.parseInt(campos[1].trim()));
                fechaDeposito = java.time.LocalDate.parse(fecha, java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"))
                        .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } else {
            java.util.regex.Matcher referencia = java.util.regex.Pattern.compile(banamex
                    ? "Referencia N[^:]*merica:\\s*(\\d{7})" : "(DOMICILIACION_\\d+)").matcher(contenido);
            if (!referencia.find()) throw new IllegalStateException("El depósito no tiene referencia");
            referenciaDeposito = referencia.group(1);
            if (banamex) {
                fechaDeposito = contenido.split("\\r?\\n")[13].split(",", 2)[0];
            } else {
                // BBVA HISTORICO importa la fecha del encabezado, no la fecha de ejecución.
                java.util.regex.Matcher fechaBbva = java.util.regex.Pattern
                        .compile("\\A\\d{9} (\\d{6})\\d{6}").matcher(contenido);
                if (!fechaBbva.find()) throw new IllegalStateException("El encabezado BBVA no tiene fecha de operación");
                fechaDeposito = java.time.LocalDate.parse(fechaBbva.group(1),
                        java.time.format.DateTimeFormatter.ofPattern("uuMMdd")
                                .withResolverStyle(java.time.format.ResolverStyle.STRICT))
                        .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }
            System.out.println("[DEPOSITO] Referencia=" + referenciaDeposito + " | Fecha del archivo=" + fechaDeposito);
            }
        } catch (java.io.IOException e) { throw new IllegalStateException("No se pudo generar el CSV de depósito", e); }
        new automation.utils.OdooNavigation(driver).open(
                automation.utils.OdooMenu.CUENTAS);
        new CuentasPage(driver).seleccionarCuenta(santander ? "Domiciliación | Santander 9783"
                : banamex ? "Domiciliación | Banamex 5794" : "Domiciliación | BBVA 5481");
        elements.click(By.cssSelector("button[name='action_importar_layout']"));
        String layout = santander ? "SANTANDER" : banamex ? "BANAMEX" : "BBVA HISTORICO";
        elements.type(By.id("layout_banco_id"), layout);
        elements.click(By.xpath("//a[@role='option' and normalize-space()='" + layout + "']"));
        elements.upload(By.cssSelector("div[name='archivo'] input[type='file']"), archivo);
        elements.select(By.id("tipo_archivo"), "csv");
        MovimientosPage movimientos = new MovimientosPage(driver);
        movimientos.cargarTransacciones();
        movimientos.validarCargado();
        movimientos.importarTransacciones();
        new WebDriverWait(driver, Duration.ofMinutes(3)).until(d -> d.findElements(By.cssSelector("td[name='estatus']"))
                .stream().anyMatch(e -> e.getText().trim().equals("Procesando") || e.getText().trim().equals("Importado")));
        driver.navigate().refresh();
        movimientos.validarTransaccionesImportadas();
        if (santander) {
            elements.click(By.cssSelector("button[name='action_ver_transacciones_cuenta']"));
            reciboDeposito = wait.until(d -> {
                return d.findElements(By.cssSelector("tr.o_data_row")).stream().filter(WebElement::isDisplayed)
                        .filter(this::coincideImporteDeposito)
                        .map(f -> f.findElement(By.cssSelector("td[name='no_recibo']")).getText().trim())
                        .filter(s -> s.replaceAll("[^0-9]", "").matches("[0-9]+"))
                        .max(java.util.Comparator.comparing(s -> new java.math.BigInteger(s.replaceAll("[^0-9]", ""))))
                        .orElse(null);
            });
            if (reciboDeposito.isBlank()) throw new AssertionError("El depósito Santander no tiene No. Recibo");
            System.out.println("[DEPOSITO SANTANDER] Último No. Recibo=" + reciboDeposito + " | Importe G2=" + importeDeposito);
        }
        if (Boolean.getBoolean("debug.cp05.deposit.pause")) {
            Path marker = Path.of("target", "cp05-deposito.pause");
            try {
                Files.write(Path.of("target", "cp05-deposito.png"), automation.utils.ScreenshotUtils.capture(driver));
                Files.writeString(marker, "Depósito cargado y validado");
                System.out.println("[BREAKPOINT DEPOSITO] Carga validada. Elimina " + marker + " para continuar.");
                while (Files.exists(marker)) Thread.sleep(500);
            } catch (java.io.IOException e) {
                throw new IllegalStateException("No se pudo guardar la pausa del depósito", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Pausa del depósito interrumpida", e);
            }
        }
    }

    public void asignarSolicitud() {
        elements.click(By.cssSelector("button[name='action_asignar_solicitud_facturacion']"));
        elegirSolicitudGuardada();
        if (referenciaDeposito != null) agregarTransaccionDeposito();
        elements.click(By.cssSelector(".modal button[name='action_asignar_solicitud_facturacion']"));
        new WebDriverWait(driver, Duration.ofMinutes(3)).until(
                ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".modal")));
    }

    private void agregarTransaccionDeposito() {
        elements.click(By.xpath("//div[contains(@class,'modal')]//a[normalize-space()='Agregar una línea']"));
        String hoy = fechaDeposito;
        By filas = By.cssSelector(".modal tbody tr.o_data_row");
        java.util.regex.Pattern identificador = java.util.regex.Pattern.compile(
                "(?<![\\p{L}\\p{N}_])" + java.util.regex.Pattern.quote(referenciaDeposito) + "(?![\\p{L}\\p{N}_])");
        java.util.List<WebElement> coincidencias = wait.until(d -> {
            java.util.List<WebElement> encontradas = d.findElements(filas).stream().filter(WebElement::isDisplayed)
                    .filter(f -> {
                        if (reciboDeposito != null) return coincideImporteDeposito(f) && f.findElements(By.cssSelector("td[name='no_recibo']"))
                                .stream().anyMatch(e -> e.getText().trim().equals(reciboDeposito));
                        boolean descripcion = f.findElements(By.cssSelector("td[name='name']")).stream()
                                .anyMatch(e -> identificador.matcher(e.getText()).find());
                        if (descripcion) return true;
                        return "BBVA".equalsIgnoreCase(bancoSeleccionado)
                                && f.findElements(By.cssSelector("td[name='reference']")).stream()
                                .anyMatch(e -> e.getText().trim().equals(referenciaDeposito));
                    })
                    .filter(f -> "BBVA".equalsIgnoreCase(bancoSeleccionado)
                            || f.findElements(By.cssSelector("td[name='post_date']")).stream()
                            .anyMatch(e -> e.getText().trim().equals(hoy))).toList();
            return encontradas.isEmpty() ? null : encontradas;
        });
        if (coincidencias.size() != 1) throw new AssertionError("Se esperaba una transacción de fecha " + hoy + " para " + referenciaDeposito);
        WebElement fila = coincidencias.getFirst();
        String campoCoincidente = reciboDeposito != null ? "No. Recibo" : fila.findElements(By.cssSelector("td[name='name']")).stream()
                .anyMatch(e -> identificador.matcher(e.getText()).find()) ? "Descripción" : "Referencia";
        System.out.println("[DEPOSITO] Coincidencia exacta en " + campoCoincidente + ": " + referenciaDeposito);
        String recibo = fila.findElement(By.cssSelector("td[name='no_recibo']")).getText().trim();
        WebElement checkbox = fila.findElement(By.cssSelector("input[type='checkbox']"));
        if (!checkbox.isSelected()) elements.click(checkbox);
        elements.click(By.cssSelector(".modal button.o_select_button"));
        wait.until(d -> d.findElements(By.cssSelector(".modal td[name='no_recibo']")).stream()
                .filter(WebElement::isDisplayed).anyMatch(e -> e.getText().trim().equals(recibo)));
        System.out.println("[DEPOSITO] Agregada transacción única: " + referenciaDeposito + " | " + hoy);
    }

    private boolean coincideImporteDeposito(WebElement fila) {
        return fila.findElements(By.cssSelector("td[name='net_balance']")).stream().anyMatch(celda -> {
            String valor = celda.getText().replace(",", "").replaceAll("[^0-9.\\-]", "");
            return !valor.isBlank() && new java.math.BigDecimal(valor).abs().compareTo(importeDeposito) == 0;
        });
    }

    public void seleccionarSolicitudConciliacion() {
        elements.click(By.cssSelector("button[name='action_conciliar_solicitud_facturacion']"));
        elegirSolicitudGuardada();
    }

    public void iniciarConciliacion() {
        ejecutorConciliacion = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".o_user_menu"))).getText().trim().toUpperCase(java.util.Locale.ROOT);
        fechaInicioConciliacion = java.time.LocalDateTime.now(java.time.ZoneId.of("America/Mexico_City"));
        inicioConciliacion = System.nanoTime();
        confirmar("action_conciliar", "Asegúrese que sus pagos y depósitos son correctos. Esta operación no se puede deshacer.");
        elements.click(By.cssSelector("button[name='action_proceso_conciliacion_tree']"));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("th[data-name='progreso_pct']")));
    }

    public void validarConciliacion() {
        if (inicioConciliacion == 0) throw new IllegalStateException("No se inició la conciliación");
        long limite = System.nanoTime() + Duration.ofSeconds(Long.getLong("domi.conciliacion.timeout.seconds", 3600L)).toNanos();
        java.time.format.DateTimeFormatter formato = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        automation.utils.AsyncRefresh refresco = new automation.utils.AsyncRefresh(driver);
        while (System.nanoTime() < limite) {
            try {
                java.util.List<WebElement> filas = driver.findElements(By.cssSelector("tr.o_data_row"));
                if (procesoConciliacion == null) {
                    java.time.LocalDateTime ultima = null;
                    for (WebElement fila : filas) {
                        String fecha = fila.findElement(By.cssSelector("td[name='create_date']")).getText().trim();
                        java.time.LocalDateTime creada = java.time.LocalDateTime.parse(fecha, formato);
                        String ejecutor = fila.findElement(By.cssSelector("td[name='cf_ejecutado_por']"))
                                .getText().trim().toUpperCase(java.util.Locale.ROOT);
                        if (ejecutor.isBlank() || (!ejecutorConciliacion.contains(ejecutor) && !ejecutor.contains(ejecutorConciliacion))) continue;
                        if (creada.isBefore(fechaInicioConciliacion.minusSeconds(3))) continue;
                        if (ultima == null || creada.isAfter(ultima)) {
                            ultima = creada;
                            procesoConciliacion = fila.findElement(By.cssSelector("td[name='name']")).getText().trim();
                        }
                    }
                    if (procesoConciliacion != null) System.out.println("[DOMI CONCILIACION] Proceso reciente de esta ejecución: " + procesoConciliacion);
                }
                for (WebElement fila : filas) {
                    if (procesoConciliacion == null || !fila.findElement(By.cssSelector("td[name='name']")).getText().trim().equals(procesoConciliacion)) continue;
                    String porcentaje = fila.findElement(By.cssSelector("td[name='progreso_pct']")).getText().replace("%", "").trim();
                    String estado = fila.findElement(By.cssSelector("td[name='estatus']")).getText().trim();
                    String actual = fila.findElement(By.cssSelector("td[name='tx_actual_ui']")).getText().replaceAll("[^0-9]", "");
                    String total = fila.findElement(By.cssSelector("td[name='tx_total_ui']")).getText().replaceAll("[^0-9]", "");
                    System.out.println("[DOMI CONCILIACION] " + bancoSeleccionado + " | Solicitud #" + solicitudId
                            + " | " + procesoConciliacion + " | " + actual + "/" + total + " | " + porcentaje + "% | " + estado);
                    if (estado.equalsIgnoreCase("Fallido") || estado.equalsIgnoreCase("Error") || estado.equalsIgnoreCase("Conciliado Parcialmente")) {
                        guardarMetricaConciliacion(estado);
                        throw new AssertionError("Conciliación " + procesoConciliacion + ": " + estado);
                    }
                    if (porcentaje.matches("100(?:[.,]0+)?") && estado.equalsIgnoreCase("Conciliado")
                            && !total.isBlank() && actual.equals(total)) {
                        elements.highlight(By.xpath("//tr[td[@name='name' and normalize-space()='" + procesoConciliacion + "']]/td[@name='progreso_pct']"));
                        guardarMetricaConciliacion("EXITO");
                        return;
                    }
                }
            } catch (org.openqa.selenium.StaleElementReferenceException | org.openqa.selenium.NoSuchElementException actualizando) {
                System.out.println("[DOMI CONCILIACION] Actualizando tabla");
            }
            refresco.poll();
        }
        guardarMetricaConciliacion("TIMEOUT");
        throw new AssertionError("La conciliación no terminó al 100%: " + procesoConciliacion);
    }

    private void guardarMetricaConciliacion(String estado) {
        double segundos = Duration.ofNanos(System.nanoTime() - inicioConciliacion).toMillis() / 1000.0;
        Path reporte = Path.of("output", "metricas_conciliacion_cp05.csv");
        synchronized (METRICAS_LOCK) {
            try {
                Files.createDirectories(reporte.getParent());
                if (Files.notExists(reporte)) Files.writeString(reporte, "Fecha,Banco,Solicitud,Proceso,DuracionSeg,Estado\n", java.nio.file.StandardOpenOption.CREATE_NEW);
                Files.writeString(reporte, String.format(java.util.Locale.ROOT, "%s,%s,%s,%s,%.3f,%s%n",
                        fechaInicioConciliacion, bancoSeleccionado, solicitudId, procesoConciliacion, segundos, estado),
                        java.nio.file.StandardOpenOption.APPEND);
            } catch (java.io.IOException e) { throw new IllegalStateException("No se pudo guardar la métrica CP05", e); }
        }
        System.out.println("[DOMI CONCILIACION] Duración=" + segundos + " s | " + estado + " | " + reporte.toAbsolutePath());
    }

    private void elegirSolicitudGuardada() {
        if (solicitudId == null) throw new IllegalStateException("Primero debe guardarse el ID de la solicitud");
        elements.click(By.cssSelector(".modal input[id='solicitud_facturacion_id']"));
        elements.click(By.xpath("//a[@role='option'][contains(normalize-space(),'Buscar más')]"));
        By filas = By.cssSelector(".modal tbody tr.o_data_row td[name='id']");
        By filaExacta = By.xpath("//div[contains(@class,'modal')]//tr[contains(@class,'o_data_row')]"
                + "[td[@name='id' and translate(normalize-space(.), ',. ', '')='" + solicitudId + "']]/td[@name='id']");
        java.util.Set<String> paginas = new java.util.HashSet<>();
        while (true) {
            wait.until(ExpectedConditions.presenceOfElementLocated(filas));
            java.util.List<WebElement> coincidencias = driver.findElements(filaExacta).stream().filter(WebElement::isDisplayed).toList();
            if (!coincidencias.isEmpty()) {
                elements.click(coincidencias.getFirst());
                wait.until(d -> d.findElements(By.id("solicitud_facturacion_id")).stream()
                        .filter(WebElement::isDisplayed).anyMatch(e -> {
                            String valor = e.getDomProperty("value");
                            return valor != null && valor.replaceAll("[^0-9]", "").equals(solicitudId);
                        }));
                System.out.println("[DOMI] Seleccionada exactamente Solicitud #" + solicitudId);
                return;
            }
            WebElement primera = driver.findElements(filas).stream().filter(WebElement::isDisplayed).findFirst().orElseThrow();
            String pagina = primera.getText();
            if (!paginas.add(pagina)) throw new AssertionError("No se encontró Solicitud #" + solicitudId);
            java.util.List<WebElement> siguientes = driver.findElements(By.cssSelector(".modal .o_pager_next"))
                    .stream().filter(WebElement::isDisplayed).filter(WebElement::isEnabled).toList();
            if (siguientes.isEmpty()) throw new AssertionError("No se encontró Solicitud #" + solicitudId);
            elements.click(siguientes.getLast());
            wait.until(d -> d.findElements(filas).stream().filter(WebElement::isDisplayed)
                    .findFirst().map(e -> !e.getText().equals(pagina)).orElse(false));
        }
    }

    public void generarArchivo() {
        confirmar("generar_archivo", "¿Desea generar el archivo de domiciliación?");
        driver.navigate().refresh();
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div[name='archivo_solicitud'] a")));
    }

    public void descargarArchivo() {
        WebElement enlace = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("div[name='archivo_solicitud'] a")));
        String nombre = enlace.getText().trim();
        if (nombre.isBlank() || !Path.of(nombre).getFileName().toString().equals(nombre))
            throw new IllegalStateException("Nombre de descarga inválido: " + nombre);
        Path descargas = Path.of(System.getProperty("user.home"), "Downloads");
        java.util.Map<Path, java.nio.file.attribute.FileTime> anteriores = new java.util.HashMap<>();
        try (var archivos = Files.list(descargas)) {
            for (Path p : archivos.filter(Files::isRegularFile).toList()) anteriores.put(p, Files.getLastModifiedTime(p));
        } catch (java.io.IOException e) { throw new IllegalStateException("No se pudieron revisar las descargas", e); }
        int punto = nombre.lastIndexOf('.');
        String base = punto < 0 ? nombre : nombre.substring(0, punto);
        String extension = punto < 0 ? "" : nombre.substring(punto);
        java.util.regex.Pattern patron = java.util.regex.Pattern.compile(java.util.regex.Pattern.quote(base)
                + "(?: ?\\(\\d+\\))?" + java.util.regex.Pattern.quote(extension));
        final Path[] origen = {null};
        Path destino = CARPETA_DOCUMENTOS.resolve("Insumos Flujo 5").resolve("Archivos Descargados").resolve(nombre);
        if ("Santander".equalsIgnoreCase(bancoSeleccionado)) {
            String hora = java.time.LocalTime.now(java.time.ZoneId.of("America/Mexico_City"))
                    .format(java.time.format.DateTimeFormatter.ofPattern("HHmmss"));
            destino = destino.resolveSibling(base + "_" + hora + extension);
        }
        if (Files.exists(destino)) {
            destino = destino.getParent().resolve(java.util.UUID.randomUUID().toString()).resolve(nombre);
        }
        elements.click(enlace);
        final long[] anterior = {-1};
        final int[] estable = {0};
        new WebDriverWait(driver, Duration.ofMinutes(3)).pollingEvery(Duration.ofSeconds(1)).until(d -> {
            try {
                if (origen[0] == null) {
                    try (var archivos = Files.list(descargas)) {
                        origen[0] = archivos.filter(Files::isRegularFile)
                                .filter(p -> patron.matcher(p.getFileName().toString()).matches())
                                .filter(p -> {
                                    try { return !anteriores.containsKey(p) || !anteriores.get(p).equals(Files.getLastModifiedTime(p)); }
                                    catch (java.io.IOException e) { return false; }
                                }).findFirst().orElse(null);
                    }
                }
                if (origen[0] == null || Files.exists(Path.of(origen[0] + ".part"))
                        || Files.exists(Path.of(origen[0] + ".crdownload"))) return false;
                long size = Files.size(origen[0]);
                estable[0] = size > 0 && size == anterior[0] ? estable[0] + 1 : 0;
                anterior[0] = size;
                return estable[0] >= 2;
            } catch (java.io.IOException e) { throw new IllegalStateException("Error leyendo descarga", e); }
        });
        try {
            Files.createDirectories(destino.getParent());
            archivoDescargado = Files.move(origen[0], destino).toAbsolutePath();
        } catch (java.io.IOException e) { throw new IllegalStateException("Error guardando descarga", e); }
        System.out.println("[DOMI] Archivo descargado: " + archivoDescargado);
    }

    public void subirRespuesta() {
        if (archivoDescargado == null || !Files.isRegularFile(archivoDescargado))
            throw new IllegalStateException("Primero debe descargarse el archivo de esta solicitud");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(
                "div[name='archivo_respuesta'] input[type='file']"))).sendKeys(archivoDescargado.toString());
    }

    public void procesarRespuesta() {
        confirmar("procesar_archivo_respuesta", "¿Desea procesar el archivo de domiciliación?");
    }

    public void validarRespuesta(int esperados) {
        new WebDriverWait(driver, Duration.ofSeconds(Long.getLong("domi.timeout.seconds", 600L)))
                .pollingEvery(Duration.ofSeconds(3)).until(d -> {
                    d.navigate().refresh();
                    String estado = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(
                            "div[name='job_status'] button[aria-checked='true']"))).getDomAttribute("data-value");
                    String mensaje = wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("div[name='job_message']"))).getText();
                    String progreso = d.findElement(By.cssSelector("div[name='job_progress'] [aria-valuenow]"))
                            .getDomAttribute("aria-valuenow");
                    System.out.println("[DOMI] " + estado + " | " + progreso + "% | " + mensaje);
                    if ("error".equals(estado)) throw new AssertionError("Error procesando respuesta: " + mensaje);
                    String normalizado = mensaje.replace(",", "").replace(".", "");
                    return "done".equals(estado) && Double.parseDouble(progreso) == 100
                            && normalizado.matches("(?s).*Respuesta aplicada:\\s*" + esperados
                            + "\\s+de\\s+" + esperados + "\\s+registros.*");
                });
    }
}
