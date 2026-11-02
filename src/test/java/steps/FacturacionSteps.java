
package steps;

import automation.actions.FacturacionActions;
import automation.driver.DriverManager;

import io.cucumber.java.es.*;

public class FacturacionSteps {

    private FacturacionActions facturacionActions;

    private FacturacionActions actions() {
        if (facturacionActions == null) {
            facturacionActions = new FacturacionActions(
                    DriverManager.getDriver());
        }
        return facturacionActions;
    }

    @Cuando("el usuario abre Domiciliación desde el menú de inicio")
    public void abrirDomiciliacion() {
        actions().ingresarDomiciliacion();
    }

    @Y("el usuario ingresa a Solicitud de Facturación")
    public void ingresarFacturacion() {
        actions().ingresarSolicitudFacturacion();
    }

    @Y("el usuario crea una nueva solicitud de facturación")
    public void crearSolicitud() {
        actions().crearSolicitud();
    }

    @Y("el usuario selecciona el banco de facturación {string}")
    public void seleccionarBanco(String banco) {
        actions().seleccionarBanco(banco);
    }

    @Y("el usuario selecciona el servicio de facturación {string}")
    public void seleccionarServicio(String servicio) {
        actions().seleccionarServicio(servicio);
    }

    @Y("el usuario activa todas las opciones bancarias de facturación")
    public void activarOpcionesBancarias() {
        actions().activarBancos();
    }

    @Y("el usuario adjunta el layout de facturación {string}")
    public void adjuntarLayout(String archivo) {
        actions().subirArchivo(archivo);
    }

    @Cuando("el usuario genera los datos de domiciliación")
    public void generarDatos() { actions().generarDatos(); }
    @Y("el usuario completa las fechas actuales únicamente para Santander")
    public void completarFechasSantander() { actions().completarFechasSantander(); }
    @Y("las opciones bancarias de facturación permanecen desmarcadas")
    public void validarOpcionesDesmarcadas() { actions().validarOpcionesDesmarcadas(); }
    @Y("el usuario activa Importar créditos Nómina Extrajudicial")
    public void activarImportarCreditos() { actions().activarImportarCreditos(); }
    @Entonces("se muestran {int} registros de domiciliación")
    public void validarRegistros(int total) { actions().validarRegistros(total); }
    @Entonces("la carga de datos de domiciliación finaliza sin indicador de carga")
    public void validarCargaCompleta() { actions().validarCargaCompleta(); }
    @Entonces("la respuesta de domiciliación termina al 100 por ciento con todos los registros generados")
    public void validarRespuestaCompleta() { actions().validarRespuestaCompleta(); }
    @Y("el usuario guarda el ID de la solicitud de facturación actual")
    public void guardarSolicitud() { actions().guardarSolicitud(); }
    @Cuando("el usuario abre la cuenta Domiciliación")
    public void abrirCuentaDomiciliacion() { actions().abrirCuentaDomiciliacion(); }
    @Cuando("el usuario carga e importa el depósito de su banco antes de conciliar")
    public void cargarDepositoBbva() { actions().cargarDepositoBbva(); }
    @Y("el usuario asigna la solicitud de facturación guardada")
    public void asignarSolicitud() { actions().asignarSolicitud(); }
    @Y("el usuario selecciona la solicitud guardada para conciliar")
    public void seleccionarSolicitudConciliacion() { actions().seleccionarSolicitudConciliacion(); }
    @Cuando("el usuario confirma la conciliación de la solicitud de facturación")
    public void iniciarConciliacion() { actions().iniciarConciliacion(); }
    @Entonces("el proceso reciente de conciliación de domiciliación termina al 100 por ciento")
    public void validarConciliacion() { actions().validarConciliacion(); }
    @Cuando("el usuario genera el archivo de domiciliación")
    public void generarArchivo() { actions().generarArchivo(); }
    @Y("el usuario descarga el archivo de esta solicitud de domiciliación")
    public void descargarArchivo() { actions().descargarArchivo(); }
    @Y("el usuario adjunta como respuesta el mismo archivo descargado de domiciliación")
    public void subirRespuesta() { actions().subirRespuesta(); }
    @Y("el usuario procesa la respuesta de domiciliación")
    public void procesarRespuesta() { actions().procesarRespuesta(); }
    @Entonces("la respuesta de domiciliación termina al 100 por ciento con {int} de {int} registros")
    public void validarRespuesta(int procesados, int total) {
        if (procesados != total) throw new IllegalArgumentException("Se requiere procesar todos los registros");
        actions().validarRespuesta(total);
    }

}
