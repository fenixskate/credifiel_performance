
package automation.actions;

import automation.pages.FacturacionPage;
import org.openqa.selenium.WebDriver;

public class FacturacionActions {

    private final FacturacionPage page;

    public FacturacionActions(WebDriver driver) {
        this.page = new FacturacionPage(driver);
    }

    public void ingresarDomiciliacion() {
        page.ingresarDomiciliacion();
    }

    public void ingresarSolicitudFacturacion() {
        page.ingresarSolicitudFacturacion();
    }

    public void crearSolicitud() {
        page.crearSolicitud();
    }

    public void seleccionarBanco(String banco) {
        page.seleccionarBanco(banco);
    }

    public void seleccionarServicio(String servicio) {
        page.seleccionarServicio(servicio);
    }

    public void activarBancos() {
        page.activarBancos();
    }

    public void subirArchivo(String archivo) {
        page.subirArchivo(archivo);
    }
    public void generarDatos() { page.generarDatos(); }
    public void completarFechasSantander() { page.completarFechasSantander(); }
    public void validarOpcionesDesmarcadas() { page.validarOpcionesDesmarcadas(); }
    public void activarImportarCreditos() { page.activarImportarCreditos(); }
    public void validarRegistros(int total) { page.validarRegistros(total); }
    public void validarCargaCompleta() { page.validarCargaCompleta(); }
    public void validarRespuestaCompleta() { page.validarRespuestaCompleta(); }
    public void guardarSolicitud() { page.guardarSolicitud(); }
    public void abrirCuentaDomiciliacion() { page.abrirCuentaDomiciliacion(); }
    public void cargarDepositoBbva() { page.cargarDepositoBbva(); }
    public void asignarSolicitud() { page.asignarSolicitud(); }
    public void seleccionarSolicitudConciliacion() { page.seleccionarSolicitudConciliacion(); }
    public void iniciarConciliacion() { page.iniciarConciliacion(); }
    public void validarConciliacion() { page.validarConciliacion(); }
    public void generarArchivo() { page.generarArchivo(); }
    public void descargarArchivo() { page.descargarArchivo(); }
    public void subirRespuesta() { page.subirRespuesta(); }
    public void procesarRespuesta() { page.procesarRespuesta(); }
    public void validarRespuesta(int total) { page.validarRespuesta(total); }
}
