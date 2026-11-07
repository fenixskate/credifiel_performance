
package automation.selectors;

import org.openqa.selenium.By;

public final class MovimientosSelectors {

    private MovimientosSelectors() {}

    public static final By MENU_INICIO =
            LoginSelectors.MENU_INICIO;

    public static final By CUENTAS =
            By.cssSelector(
                    "a[data-menu-xmlid='vor_cuentas.menu_vor_act_root']");

    public static By cuenta(String nombre) {
        return By.xpath(
                "//div[contains(@class,'o_kanban_record')]" +
                        "[.//strong/span[normalize-space()='" +
                        nombre + "']]");
    }

    public static final By IMPORTAR_LAYOUT =
            By.cssSelector(
                    "button[name='action_importar_layout']");

    public static final By LAYOUT_BANCO =
            By.id("layout_banco_id");

    public static By opcionBanco(String banco) {
        return By.xpath(
                "//a[@role='option' and normalize-space()='" +
                        banco + "']");
    }

    public static final By SUBIR_ARCHIVO =
            By.cssSelector(
                    "div[name='archivo'] input[type='file']");

    public static final By CARGAR_TRANSACCIONES =
            By.cssSelector("button[name='action_cargar']");

    public static final By IMPORTAR_TRANSACCIONES =
            By.cssSelector("button[name='action_importar']");

    public static final By FILAS =
            By.cssSelector(
                    "table.o_list_table tbody tr.o_data_row");

    public static final By ESTATUS =
            By.cssSelector("td[name='estatus']");

    public static final By TOTAL_ARCHIVO = By.cssSelector(".o_form_view div[name='total_transacciones_archivo']");
    public static final By TOTAL_CARGADAS = By.cssSelector(".o_form_view div[name='total_transacciones_cargadas']");
    public static final By TOTAL_DUPLICADAS = By.cssSelector(".o_form_view div[name='total_transacciones_duplicadas']");
    public static final By TOTAL_IMPORTADAS = By.cssSelector(".o_form_view div[name='total_transacciones_importadas']");
    public static final By TOTAL_FALLIDAS = By.cssSelector(".o_form_view div[name='total_transacciones_fallidas']");


    public static final By MENU_MOVIMIENTOS_NO_CONCILIADOS =
            By.cssSelector(
                    "[data-menu-xmlid='vor_cte_credifiel.menu_movimientos_conciliados']");

    public static final By EXPORTAR_MOVIMIENTOS =
            By.cssSelector(
                    "a[data-menu-xmlid='vor_cte_credifiel.menu_exportar_movimientos_tasks']");

    public static final By NUEVA_EXPORTACION =
            By.cssSelector("button.o_list_button_add");

    public static final By FECHA_INICIAL_EXPORTACION =
            By.id("cf_fecha_inicial");

    public static final By FECHA_FINAL_EXPORTACION =
            By.id("cf_fecha_final");

    public static final By GENERAR_EXPORTACION =
            By.cssSelector(
                    "button[name='action_export_movimientos']");



    public static final By LINK_ARCHIVO_EXPORTADO =
            By.cssSelector("div[name='cf_file'] > a.o_form_uri");


    public static final By IMPORTAR_MOVIMIENTOS_MENU =
            By.cssSelector(
                    "a[data-menu-xmlid='vor_cte_credifiel.menu_importar_movimientos']");

    public static final By NUEVA_IMPORTACION_MOVIMIENTOS =
            By.cssSelector(
                    "button.o_list_button_add");

    public static final By ARCHIVO_IMPORTACION_MOVIMIENTOS =
            By.cssSelector(
                    "label.o_select_file_button input[type='file']");

    public static final By CARGAR_MOVIMIENTOS_IMPORTACION =
            By.cssSelector(
                    "button[name='action_import_movimientos']");

    public static final By MODAL_CONFIRMACION_IMPORTACION =
            By.xpath(
                    "//div[@role='dialog' and contains(@class,'o_technical_modal')]" +
                            "[.//p[contains(.,'Se cargarán los movimientos del archivo adjunto')]]");

    public static final By ACEPTAR_IMPORTACION_MOVIMIENTOS =
            By.xpath(
                    ".//button[contains(@class,'btn-primary') and (" +
                            "translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')='aceptar' or " +
                            "translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')='de acuerdo')]");

    public static final By ESTADO_IMPORTACION_MOVIMIENTOS =
            By.cssSelector("div[name='cf_state']");

    public static final By ESTADOS_FILAS_IMPORTADAS =
            By.cssSelector("tr.o_data_row td[name='cf_estatus_fila']");

    public static final By MENU_CUENTAS_DIRECTO =
            By.cssSelector(
                    "a[data-menu-xmlid='vor_cuentas.menu_vor_act_root']");

    public static final By CUENTA_GENERAL_OPERACIONES =
            By.xpath(
                    "//div[contains(@class,'o_kanban_record')]" +
                            "[.//strong/span[normalize-space()='General Operaciones']]");

    public static final By CONCILIAR_IMPORTADOS =
            By.cssSelector(
                    "button[name='action_conciliar_importados']");

    public static final By FILAS_ARCHIVOS_CONCILIACION =
            By.cssSelector(
                    "div[role='dialog'] div[name='line_ids'] " +
                            "table.o_list_table tbody tr.o_data_row");

    public static final By CONFIRMAR_CONCILIACION =
            By.cssSelector("button[name='action_confirm']");

    public static final By FILAS_PROCESO_CONCILIACION =
            By.cssSelector(
                    "table.o_list_table tbody tr.o_data_row");

    public static final By COLUMNA_AVANCE =
            By.cssSelector("td[name='progreso_pct']");

    public static final By COLUMNA_ESTADO_PROCESO =
            By.cssSelector("td[name='estatus']");

    public static final By CONFIRMAR_EXPORTACION =
            By.cssSelector(".modal-footer button.btn-primary");

    public static final By TOTAL_FILAS =
            By.cssSelector("div[name='cf_total_lines'] span");

    public static final By FILAS_VALIDAS =
            By.cssSelector("div[name='cf_processed_lines'] span");

    public static final By FILAS_RECHAZADAS =
            By.cssSelector("div[name='cf_error_lines'] span");
}
