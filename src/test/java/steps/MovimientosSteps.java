
package steps;

import automation.actions.MovimientosActions;
import automation.driver.DriverManager;

import io.cucumber.java.es.*;
import java.io.IOException;

public class MovimientosSteps {

    private MovimientosActions movimientosActions;

    private MovimientosActions actions() {

        if (movimientosActions == null) {
            movimientosActions = new MovimientosActions(
                    DriverManager.getDriver());
        }

        return movimientosActions;
    }

    @Cuando("el usuario ingresa al módulo Cuentas para importar movimientos")
    public void ingresarCuentas() {
        actions().ingresarCuentas();
    }

    @Y("selecciona la cuenta de movimientos {string}")
    public void seleccionarCuenta(String cuenta) {
        actions().seleccionarCuenta(cuenta);
    }

    @Y("el usuario abre la importación del layout bancario")
    public void abrirImportacion() {
        actions().abrirImportacion();
    }

    @Y("el usuario selecciona el banco {string}")
    public void seleccionarBanco(String banco) {
        actions().seleccionarBanco(banco);
    }

    @Cuando("el usuario carga las transacciones AZTECA")
    public void cargarTransacciones() {
        actions().cargarTransacciones();
    }

    @Entonces("todos los movimientos muestran el estado Cargado")
    public void validarCargado() {
        actions().validarCargado();
    }

    @Cuando("el usuario importa las transacciones AZTECA")
    public void importarTransacciones() {
        actions().importarTransacciones();
    }

    @Entonces("todos los movimientos muestran el estado Importado")
    public void validarImportado() {
        actions().validarImportado();
    }

    @Y("el usuario {string} carga el archivo {string} con fecha incrementada")
    public void subirArchivo(String usuario, String archivo)
            throws IOException {

        actions().subirArchivo(usuario, archivo);
    }


    @Cuando("el usuario ingresa a Exportar movimientos")
    public void ingresarExportarMovimientos() {
        actions().ingresarExportarMovimientos();
    }

    @Y("el usuario crea una nueva exportación")
    public void crearNuevaExportacion() {
        actions().crearNuevaExportacion();
    }

    @Y("el usuario establece las fechas del archivo AZTECA generado")
    public void establecerFechasExportacion() {
        actions().establecerFechasExportacion();
    }

    @Y("el usuario genera la exportación de movimientos")
    public void generarExportacion() {
        actions().generarExportacion();
    }

    @Y("el usuario confirma la exportación")
    public void confirmarExportacion() {
        actions().confirmarExportacion();
    }

    @Entonces("se genera el archivo de movimientos conciliados")
    public void validarArchivoExportado() {
        actions().validarArchivoExportado();
    }

    @Y("el usuario descarga el archivo de movimientos conciliados")
    public void descargarArchivoExportado() {
        actions().descargarArchivoExportado();
    }
    @Y("el usuario agrega los créditos al archivo descargado {string}")
    public void agregarCreditosArchivoDescargado(String nombreTemplate)
            throws IOException {

        actions().agregarCreditosArchivoDescargado(nombreTemplate);
    }

    @Cuando("el usuario ingresa a Importar movimientos")
    public void ingresarImportarMovimientos() {
        actions().ingresarImportarMovimientos();
    }

    @Y("el usuario crea una nueva importación de movimientos")
    public void crearNuevaImportacionMovimientos() {
        actions().crearNuevaImportacionMovimientos();
    }

    @Y("el usuario adjunta el archivo de movimientos modificado")
    public void adjuntarArchivoMovimientosModificado() {
        actions().adjuntarArchivoMovimientosModificado();
    }

    @Y("el usuario carga los movimientos importados")
    public void cargarMovimientosImportados() {
        actions().cargarMovimientosImportados();
    }

    @Y("el usuario confirma la carga de movimientos")
    public void confirmarCargaMovimientos() {
        actions().confirmarCargaMovimientos();
    }

    @Entonces("el usuario valida el estado de la importación de movimientos")
    public void validarEstadoImportacionMovimientos() {
        actions().validarEstadoImportacionMovimientos();
    }

    @Cuando("el usuario regresa al módulo Cuentas para conciliar movimientos")
    public void regresarCuentasParaConciliar() {
        actions().ingresarModuloCuentas();
    }

    @Y("selecciona la cuenta de conciliación {string}")
    public void seleccionarCuentaConciliacion(String cuenta) {

        if (!"General Operaciones".equals(cuenta)) {
            throw new IllegalArgumentException(
                    "Cuenta no soportada: " + cuenta);
        }

        actions().seleccionarGeneralOperaciones();
    }

    @Y("abre la conciliación de importados extrajudicial")
    public void abrirConciliacionImportados() {
        actions().abrirConciliacionImportados();
    }

    @Y("selecciona únicamente el archivo de movimientos importado")
    public void seleccionarArchivoConciliacion() {
        actions().seleccionarArchivoConciliacion();
    }

    @Y("confirma la conciliación del archivo")
    public void confirmarConciliacion() {
        actions().confirmarConciliacion();
    }

    @Entonces("el proceso de conciliación alcanza el 100 por ciento")
    public void validarConciliacionCompleta() {
        actions().validarConciliacionCompleta();
    }

}
