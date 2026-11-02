
package automation.actions;

import automation.pages.PagosPage;
import org.openqa.selenium.WebDriver;

public final class PagosActions {

    private final PagosPage pagosPage;

    public PagosActions(WebDriver driver) {
        this.pagosPage = new PagosPage(driver);
    }

    public void seleccionarMeNU(
            String fecha,
            String periodo,
            String dependencia,
            String pagaduria) {

        pagosPage.seleccionarMeNU(
                fecha,
                periodo,
                dependencia,
                pagaduria);
    }

    public void subirArchivo(String archivo) {
        pagosPage.subirArchivo(archivo);
    }

    public void leerArchivo() {
        pagosPage.leerArchivo();
    }

    public void seleccionarComplemento() {
        pagosPage.seleccionarComplemento();
    }

    public void procesarArchivo() {
        pagosPage.procesarArchivo();
    }

    public void registrarPagos() {
        pagosPage.registrarPagos();
    }

    public void validarArchivoTerminado() {
        pagosPage.validarArchivoTerminado();
    }
}
