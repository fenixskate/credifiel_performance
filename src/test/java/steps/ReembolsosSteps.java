package steps;

import automation.actions.ReembolsosActions;
import automation.driver.DriverManager;
import io.cucumber.java.es.*;

public final class ReembolsosSteps {
    private ReembolsosActions actions;

    private ReembolsosActions actions() {
        if (actions == null) actions = new ReembolsosActions(DriverManager.getDriver());
        return actions;
    }

    @Cuando("el usuario abre Reembolsos desde el menú de inicio")
    public void abrir() {
        actions().abrir();
    }

    @Y("el usuario crea un nuevo reembolso")
    public void nuevo() {
        actions().nuevo();
    }

    @Y("el usuario selecciona reembolso Masivo")
    public void masivo() {
        actions().seleccionarMasivo();
    }

    @Y("el usuario selecciona Excel para el reembolso")
    public void excel() {
        actions().seleccionarExcel();
    }

    @Y("el usuario selecciona el subtipo de reembolso {string}")
    public void subtipo(String subtipo) {
        actions().seleccionarSubtipo(subtipo);
    }

    @Y("el usuario adjunta el archivo de reembolsos {string}")
    public void archivo(String archivo) {
        actions().subirArchivo(archivo);
    }

    @Cuando("el usuario lee el archivo de reembolsos")
    public void leer() {
        actions().leerArchivo();
    }

    @Y("el usuario avanza el reembolso a Autorización")
    public void avanzar() {
        actions().avanzarAutorizacion();
    }

    @Entonces("el reembolso masivo queda en Autorización")
    public void validar() {
        actions().validarAutorizacion();
    }

    @Cuando("el usuario selecciona el tipo de rechazo del reembolso {string}")
    public void tipoRechazo(String tipo) {
        actions().seleccionarTipoRechazo(tipo);
    }

    @Y("el usuario selecciona el motivo de documentos del reembolso {string}")
    public void motivoDocumentos(String motivo) {
        actions().seleccionarMotivoDocumentos(motivo);
    }

    @Y("el usuario aprueba el reembolso como Subdirector de Operaciones")
    public void aprobarSubdirector() {
        actions().aprobarSubdirectorOperaciones();
    }

    @Cuando("el usuario avanza el reembolso a Liberación")
    public void avanzarLiberacion() {
        actions().avanzarLiberacion();
    }

    @Cuando("el usuario avanza el reembolso a Aprobado")
    public void avanzarAprobado() {
        actions().avanzarAprobado();
    }

    @Entonces("los registros del reembolso están En confirmación STP o Rechazado")
    public void validarEstatusDetalle() {
        actions().validarEstatusDetalle();
    }
    @Entonces("el reembolso masivo queda en la etapa {string}")
    public void validarEtapa(String etapa) {
        actions().validarEtapa(etapa);
    }
}
