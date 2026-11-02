
package automation.actions;

import automation.pages.CuentasPage;
import org.openqa.selenium.WebDriver;

import java.io.IOException;

public final class CuentasActions {

    private final CuentasPage cuentasPage;

    public CuentasActions(WebDriver driver) {
        this.cuentasPage = new CuentasPage(driver);
    }

    public void ingresarModuloCuentas() {
        cuentasPage.ingresarModuloCuentas();
    }

    public void seleccionarCuenta(String nombreCuenta) {
        cuentasPage.seleccionarCuenta(nombreCuenta);
    }

    public void validarDetalleCuenta(String nombreCuenta) {
        cuentasPage.validarDetalleCuenta(nombreCuenta);
    }
    public void importarLayoutBancario(String usuario) throws IOException {
        cuentasPage.importarLayoutBancario(usuario);
    }
    public void cargarTransacciones() {
        cuentasPage.cargarTransacciones();
    }

    public void validarEstadoCargado() {
        cuentasPage.validarEstadoCargado();
    }

    public void importarTransacciones() {
        cuentasPage.importarTransacciones();
    }

    public void validarEstadoProcesando() {
        cuentasPage.validarEstadoProcesando();
    }

    public void validarEstadoSinIdentificar() {
        cuentasPage.validarEstadoSinIdentificar();
    }

    public void ingresarGeneralOperaciones() {
        cuentasPage.ingresarGeneralOperaciones();
    }

    public void abrirTransacciones() {
        cuentasPage.abrirTransacciones();
    }

    public void abrirFiltroPersonalizado() {
        cuentasPage.abrirFiltroPersonalizado();
    }

    public void filtrarPorReferencia(String referencia) {
        cuentasPage.filtrarPorReferencia(referencia);
    }

    public String obtenerReferenciaCapturada() {
        return cuentasPage.obtenerReferenciaCapturada();
    }
    public void aplicarFiltro() {
        cuentasPage.aplicarFiltro();
    }

    public void seleccionarTransaccion() {
        cuentasPage.seleccionarTransaccion();
    }

    public void transferirTransacciones(String cuenta) {
        cuentasPage.transferirTransacciones(cuenta);
    }

    public void ingresarCuentaDestino(String cuenta) {
        cuentasPage.ingresarCuentaDestino(cuenta);
    }

    public void asignarPeriodoOperativo(String periodo) {
        cuentasPage.asignarPeriodoOperativo(periodo);
    }

    public void agregarLineaPeriodo(String referencia) {
        cuentasPage.agregarLineaPeriodo(referencia);
    }

    public void confirmarAsignacionPeriodo() {
        cuentasPage.confirmarAsignacionPeriodo();
    }

    public void asignarPagaduria(String pagaduria) {
        cuentasPage.asignarPagaduria(pagaduria);
    }
    public void agregarLineaPagaduria(String referencia) {
        cuentasPage.agregarLineaPagaduria(referencia);
    }

    public void confirmarPagaduria() {
        cuentasPage.confirmarPagaduria();
    }

    public void abrirConciliacionPeriodo() {
        cuentasPage.abrirConciliacionPeriodo();
    }

    public void configurarConciliacion(String periodo, String pagaduria) {
        cuentasPage.configurarConciliacion(periodo, pagaduria);
    }

    public void ejecutarConciliacion() {
        cuentasPage.ejecutarConciliacion();
    }

    public void abrirProcesosConciliacion() {
        cuentasPage.abrirProcesosConciliacion();
    }

    public void validarConciliacionCompleta() {
        cuentasPage.validarConciliacionCompleta();
    }
}
