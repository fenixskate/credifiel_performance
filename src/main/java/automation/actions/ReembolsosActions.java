package automation.actions;

import automation.pages.ReembolsosPage;
import org.openqa.selenium.WebDriver;

public final class ReembolsosActions {
    private final ReembolsosPage page;

    public ReembolsosActions(WebDriver driver) {
        page = new ReembolsosPage(driver);
    }

    public void abrir() {
        page.abrir();
    }

    public void nuevo() {
        page.nuevo();
    }

    public void seleccionarMasivo() {
        page.seleccionarMasivo();
    }

    public void seleccionarExcel() {
        page.seleccionarExcel();
    }

    public void seleccionarSubtipo(String subtipo) {
        page.seleccionarSubtipo(subtipo);
    }

    public void subirArchivo(String archivo) {
        page.subirArchivo(archivo);
    }

    public void leerArchivo() {
        page.leerArchivo();
    }

    public void avanzarAutorizacion() {
        page.avanzarAutorizacion();
    }

    public void validarAutorizacion() {
        page.validarAutorizacion();
    }

    public void seleccionarTipoRechazo(String tipo) {
        page.seleccionarTipoRechazo(tipo);
    }

    public void seleccionarMotivoDocumentos(String motivo) {
        page.seleccionarMotivoDocumentos(motivo);
    }

    public void aprobarSubdirectorOperaciones() {
        page.aprobarSubdirectorOperaciones();
    }

    public void avanzarLiberacion() {
        page.avanzarLiberacion();
    }

    public void avanzarAprobado() {
        page.avanzarAprobado();
    }

    public void validarEstatusDetalle() {
        page.validarEstatusDetalle();
    }

    public void validarEtapa(String etapa) {
        page.validarEtapa(etapa);
    }
}
