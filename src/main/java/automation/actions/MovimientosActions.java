
package automation.actions;

import automation.pages.MovimientosPage;
import org.openqa.selenium.WebDriver;
import java.io.IOException;

public class MovimientosActions {

    private final MovimientosPage page;

    public MovimientosActions(WebDriver driver) {
        page = new MovimientosPage(driver);
    }

    public void ingresarCuentas() {
        page.ingresarCuentas();
    }

    public void seleccionarCuenta(String cuenta) {
        page.seleccionarCuenta(cuenta);
    }

    public void abrirImportacion() {
        page.abrirImportacion();
    }

    public void seleccionarBanco(String banco) {
        page.seleccionarBanco(banco);
    }

    public void subirArchivo(String usuario) throws IOException {
        page.subirArchivo(usuario);
    }

    public void cargarTransacciones() {
        page.cargarTransacciones();
    }

    public void validarCargado() {
        page.validarCargado();
    }

    public void importarTransacciones() {
        page.importarTransacciones();
    }

    public void validarImportado() {
        page.validarImportado();
    }

    public void subirArchivo(String usuario, String archivo)
            throws IOException {

        page.subirArchivo(usuario, archivo);
    }

    public void ingresarExportarMovimientos() {
        page.ingresarExportarMovimientos();
    }

    public void crearNuevaExportacion() {
        page.crearNuevaExportacion();
    }

    public void establecerFechasExportacion() {
        page.establecerFechasExportacion();
    }

    public void generarExportacion() {
        page.generarExportacion();
    }

    public void confirmarExportacion() {
        page.confirmarExportacion();
    }

    public void validarArchivoExportado() {
        page.validarArchivoExportado();
    }

    public void descargarArchivoExportado() {
        page.descargarArchivoExportado();
    }

    public void agregarCreditosArchivoDescargado(String nombreTemplate)
            throws IOException {

        page.agregarCreditosArchivoDescargado(nombreTemplate);
    }
    public void ingresarImportarMovimientos() {
        page.ingresarImportarMovimientos();
    }

    public void crearNuevaImportacionMovimientos() {
        page.crearNuevaImportacionMovimientos();
    }

    public void adjuntarArchivoMovimientosModificado() {
        page.adjuntarArchivoMovimientosModificado();
    }

    public void cargarMovimientosImportados() {
        page.cargarMovimientosImportados();
    }

    public void confirmarCargaMovimientos() {
        page.confirmarCargaMovimientos();
    }

    public void validarEstadoImportacionMovimientos() {
        page.validarEstadoImportacionMovimientos();
    }

    public void ingresarModuloCuentas() {
        page.ingresarModuloCuentas();
    }

    public void seleccionarGeneralOperaciones() {
        page.seleccionarGeneralOperaciones();
    }

    public void abrirConciliacionImportados() {
        page.abrirConciliacionImportados();
    }

    public void seleccionarArchivoConciliacion() {
        page.seleccionarArchivoConciliacion();
    }

    public void confirmarConciliacion() {
        page.confirmarConciliacion();
    }

    public void validarConciliacionCompleta() {
        page.validarConciliacionCompleta();
    }
}
