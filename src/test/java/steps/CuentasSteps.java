
package steps;

import automation.actions.CuentasActions;
import automation.driver.DriverManager;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;

import java.io.IOException;

public class CuentasSteps {

    private CuentasActions cuentasActions;

    private CuentasActions actions() {
        if (cuentasActions == null) {
            cuentasActions = new CuentasActions(DriverManager.getDriver());
        }
        return cuentasActions;
    }

    @Cuando("el usuario ingresa al módulo Cuentas")
    public void ingresarModuloCuentas() {
        actions().ingresarModuloCuentas();
    }

    @Cuando("selecciona la cuenta {string}")
    public void seleccionarCuenta(String nombreCuenta) {
        actions().seleccionarCuenta(nombreCuenta);
    }

    @Entonces("se muestra el detalle de la cuenta {string}")
    public void validarDetalleCuenta(String nombreCuenta) {
        actions().validarDetalleCuenta(nombreCuenta);
    }

    @Cuando("el usuario {string} importa el layout bancario INVEX")
    public void importarLayoutBancario(String usuario) throws IOException {
        actions().importarLayoutBancario(usuario);
    }

    @Cuando("el usuario carga las transacciones")
    public void cargarTransacciones() {
        actions().cargarTransacciones();
    }

    @Entonces("las transacciones muestran el estado Cargado")
    public void validarEstadoCargado() {
        actions().validarEstadoCargado();
    }

    @Cuando("el usuario importa las transacciones")
    public void importarTransacciones() {
        actions().importarTransacciones();
    }

    @Entonces("las transacciones muestran el estado Procesando")
    public void validarEstadoProcesando() {
        actions().validarEstadoProcesando();
    }

    @Entonces("las transacciones muestran el estado Sin identificar")
    public void validarEstadoSinIdentificar() {
        actions().validarEstadoSinIdentificar();
    }

    @Cuando("el usuario ingresa a General Operaciones")
    public void ingresarGeneralOperaciones() {
        actions().ingresarGeneralOperaciones();
    }

    @Cuando("el usuario abre las transacciones")
    public void abrirTransacciones() {
        actions().abrirTransacciones();
    }

    @Cuando("el usuario agrega un filtro personalizado")
    public void abrirFiltroPersonalizado() {
        actions().abrirFiltroPersonalizado();
    }

    @Cuando("el usuario filtra por la referencia capturada")
    public void filtrarPorReferenciaCapturada() {

        String referencia = actions().obtenerReferenciaCapturada();

        if (referencia == null || referencia.isBlank()) {
            throw new IllegalStateException(
                    "No se capturó la referencia de la transacción");
        }

        actions().filtrarPorReferencia(referencia);
    }

    @Cuando("el usuario aplica el filtro")
    public void aplicarFiltro() {
        actions().aplicarFiltro();
    }

    @Cuando("selecciona la transacción encontrada")
    public void seleccionarTransaccion() {
        actions().seleccionarTransaccion();
    }

    @Y("transfiere las transacciones seleccionadas a la cuenta {string}")
    public void transferirTransacciones(String cuenta) {
        actions().transferirTransacciones(cuenta);
    }

    @Cuando("ingresa a la cuenta destino {string}")
    public void ingresarCuentaDestino(String cuenta) {
        actions().ingresarCuentaDestino(cuenta);
    }

    @Cuando("asigna el periodo operativo {string}")
    public void asignarPeriodoOperativo(String periodo) {
        actions().asignarPeriodoOperativo(periodo);
    }

    @Cuando("agrega la línea del periodo")
    public void agregarLineaPeriodo() {
        actions().agregarLineaPeriodo(
                actions().obtenerReferenciaCapturada()
        );
    }

    @Cuando("el usuario confirma la asignación del periodo")
    public void confirmarAsignacionPeriodo() {
        actions().confirmarAsignacionPeriodo();
    }

    @Cuando("el usuario asigna la pagaduría {string}")
    public void asignarPagaduria(String pagaduria) {
        actions().asignarPagaduria(pagaduria);
    }

    @Cuando("agrega la línea de pagaduría")
    public void agregarLineaPagaduria() {
        actions().agregarLineaPagaduria(
                actions().obtenerReferenciaCapturada()
        );
    }

    @Cuando("el usuario confirma la pagaduría")
    public void confirmarPagaduria() {
        actions().confirmarPagaduria();
    }

    @Cuando("el usuario abre la conciliación por periodo operativo")
    public void abrirConciliacionPeriodo() {
        actions().abrirConciliacionPeriodo();
    }

    @Y("el usuario configura la conciliación con periodo {string} y pagaduría {string}")
    public void configurarConciliacion(String periodo, String pagaduria) {
        actions().configurarConciliacion(periodo, pagaduria);
    }

    @Cuando("el usuario ejecuta la conciliación")
    public void ejecutarConciliacion() {
        actions().ejecutarConciliacion();
    }

    @Cuando("el usuario abre los procesos de conciliación")
    public void abrirProcesosConciliacion() {
        actions().abrirProcesosConciliacion();
    }

    @Entonces("la conciliación alcanza el 100 por ciento")
    public void validarConciliacionCompleta() {
            actions().validarConciliacionCompleta();
    }
}
