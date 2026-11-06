
package automation.selectors;

import org.openqa.selenium.By;

public class FacturacionSelectors {

    public static final By MENU_INICIO =
            LoginSelectors.MENU_INICIO;

    public static final By APP_DOMICILIACION =
            By.cssSelector(
                    "a[data-menu-xmlid='vor_cre_domiciliacion.vor_menu_domiciliacion_app']");

    public static final By MENU_FACTURACION =
            By.cssSelector(
                    "button[data-menu-xmlid='vor_cre_domiciliacion.vor_menu_facturacion_top']");

    public static final By SOLICITUD_FACTURACION =
            By.cssSelector(
                    "a[data-menu-xmlid='vor_cre_domiciliacion.vor_menu_facturacion_ver']");

    public static final By BOTON_NUEVO =
            By.cssSelector("button.o_list_button_add");

    public static final By BANCO =
            By.id("banco_domic_aux");

    public static final By SERVICIO =
            By.id("servicio");

    public static final By INCLUIR_BANCOS =
            By.id("incluir_bancos");

    public static final By INCLUIR_BANORTE =
            By.id("incluir_banorte");

    public static final By INCLUIR_BANAMEX =
            By.id("incluir_banamex");

    public static final By INCLUIR_SANTANDER =
            By.id("incluir_santander");

    public static final By CREDITOS_NO_DOMICILIACION =
            By.id("cf_creditos_no_domiciliacion");

    public static final By SUBIR_ARCHIVO =
            By.cssSelector(
                    "div[name='layout_file'] input[type='file']");


}
