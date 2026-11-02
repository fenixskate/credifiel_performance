
package steps;

import automation.actions.PagosActions;
import automation.driver.DriverManager;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;

public class PagosSteps {

    private PagosActions pagosActions;

    private PagosActions actions() {
        if (pagosActions == null) {
            pagosActions = new PagosActions(DriverManager.getDriver());
        }
        return pagosActions;
    }

    @Cuando("el usuario selecciona el menú de pagos con fecha {string} y periodo {string} y dependencia {string} y pagaduría {string}")
    public void seleccionarMenuPagos(
            String fecha,
            String periodo,
            String dependencia,
            String pagaduria) {

        actions().seleccionarMeNU(
                fecha,
                periodo,
                dependencia,
                pagaduria);
    }

    @Cuando("el usuario lee el archivo")
    public void leerArchivo() {
        actions().leerArchivo();
    }

    @Cuando("el usuario procesa el archivo")
    public void procesarArchivo() {
        actions().procesarArchivo();
    }

    @Cuando("el usuario registra los pagos")
    public void registrarPagos() {
        actions().registrarPagos();
    }

    @Entonces("el estado del archivo es Terminado")
    public void validarArchivoTerminado() {
        actions().validarArchivoTerminado();
    }

    @Cuando("el usuario sube el archivo {string}")
    public void subirArchivo(String archivo) {
        actions().subirArchivo(archivo);
    }
}
