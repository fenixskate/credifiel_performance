
package automation.actions;

import automation.pages.PromesasPage;
import org.openqa.selenium.WebDriver;

public class PromesasActions {

    private final PromesasPage page;

    public PromesasActions(WebDriver driver) {
        this.page = new PromesasPage(driver);
    }

    public void ingresarModulo() {
        page.ingresarModulo();
    }

    public void subirArchivo(String archivo) {
        page.subirArchivo(archivo);
    }

    public void leerArchivo() {
        page.leerArchivo();
    }

    public void validarPendientes() {
        page.validarPendientes();
    }

    public void procesarPromesas() {
        page.procesarPromesas();
    }

    public void validarExitosas() {
        page.validarExitosas();
    }

    public void establecerUsuario(String usuario) {
        page.establecerUsuario(usuario);
    }
}
