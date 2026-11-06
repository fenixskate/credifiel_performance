
package steps;

import automation.actions.PromesasActions;
import automation.driver.DriverManager;
import io.cucumber.java.es.*;

public class PromesasSteps {

    private PromesasActions promesasActions;

    private PromesasActions actions() {
        if (promesasActions == null) {
            promesasActions = new PromesasActions(
                    DriverManager.getDriver());
        }
        return promesasActions;
    }

    @Cuando("el usuario ingresa a Carga Masiva de Promesas de Pago")
    public void ingresarModulo() {
        actions().ingresarModulo();
    }

    @Y("el usuario actualiza los {int} archivos de promesas desde el Universo de Cartera")
    public void actualizarArchivosPromesas(int cantidad) throws Exception {
        automation.utils.PreparacionPromesas.actualizarUnaVez(DriverManager.getDriver(),cantidad);
    }

    @Y("el usuario sube el archivo de promesas {string}")
    public void subirArchivo(String archivo) {
        actions().subirArchivo(archivo);
    }

    @Y("el usuario lee el archivo de promesas")
    public void leerArchivo() {
        actions().leerArchivo();
    }

    @Entonces("todas las promesas muestran el estado Pendiente")
    public void validarPendientes() {
        actions().validarPendientes();
    }

    @Cuando("el usuario procesa las promesas de pago")
    public void procesarPromesas() {
        actions().procesarPromesas();
    }

    @Entonces("todas las promesas muestran el estado Exitoso")
    public void validarExitosas() {
        actions().validarExitosas();
    }

    @Y("el usuario {string} sube el archivo de promesas {string}")
    public void subirArchivo(String usuario, String archivo) {
        actions().establecerUsuario(usuario);
        actions().subirArchivo(archivo);
    }
}
