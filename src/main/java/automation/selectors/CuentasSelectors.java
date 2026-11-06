
package automation.selectors;

import org.openqa.selenium.By;

public final class CuentasSelectors {

    private CuentasSelectors() {}

    public static final By MENU_INICIO =
            LoginSelectors.MENU_INICIO;

    public static final By MENU_CUENTAS =
            By.xpath("//a[@data-menu-xmlid='vor_cuentas.menu_vor_act_root']");

    public static final By DETALLE_CUENTA =
            By.cssSelector("ol.breadcrumb li.breadcrumb-item.active > span.text-truncate");

    public static final By IMPORTAR_LAYOUT =
            By.xpath("//button[@name='action_importar_layout']");

    public static final By LAYOUT_BANCO =
            By.id("layout_banco_id");

    public static final By OPCION_INVEX =
            By.xpath("//a[@role='option' and normalize-space()='INVEX 2595']");

    public static final By SUBIR_LAYOUT =
            By.xpath("//input[@type='file' and contains(@class,'o_input_file')]");

    public static final By CARGAR_TRANSACCIONES =
            By.xpath("//button[@name='action_cargar']");

    public static final By ESTADO_CARGADO =
            By.xpath("//span[contains(@class,'badge') and normalize-space()='Cargado']");

    public static final By IMPORTAR_TRANSACCIONES =
            By.xpath("//button[@name='action_importar']");

    public static final By ESTADO_PROCESANDO =
            By.xpath("//span[contains(@class,'badge') and normalize-space()='Procesando']");

    public static final By ESTADO_SIN_IDENTIFICAR =
            By.xpath("//span[contains(@class,'badge') and normalize-space()='Sin identificar']");


    public static final By MENU_CUENTAS_PRINCIPAL =
            By.xpath("//a[@data-menu-xmlid='vor_cuentas.menu_vor_act_root' and contains(@class,'o_menu_brand')]");

    public static final By CUENTA_GENERAL_OPERACIONES =
            By.xpath("//div[@role='article'][.//span[normalize-space()='General Operaciones']]");

    public static final By BOTON_TRANSACCIONES =
            By.xpath("//button[@name='action_view_trans']");

    public static final By BOTON_FILTROS =
            By.xpath("//button[contains(@class,'dropdown-toggle') and .//span[normalize-space()='Filtros']]");

    public static final By AGREGAR_FILTRO_PERSONALIZADO =
            By.xpath("//button[contains(normalize-space(.),'Agregar filtro personalizado')]");

    public static final By CAMPO_FILTRO =
            By.cssSelector("select.o_generator_menu_field");

    public static final By VALOR_FILTRO =
            By.xpath("//select[contains(@class,'o_generator_menu_field')]/following::input[contains(@class,'o_input')][1]");

    public static final By REFERENCIA_TRANSACCION =
            By.xpath("//td[@name='reference']");


    public static final By APLICAR_FILTRO =
            By.cssSelector("button.o_apply_filter");

    public static final By TRANSFERIR_SELECCIONADOS =
            By.xpath("//button[@name='action_transferencia_multiple']");

    public static final By NO_RECIBO =
            By.cssSelector("td[name='no_recibo']");

    public static final By CUENTA_DESTINO =
            By.id("cuenta_destino_id");

    public static final By CONFIRMAR_TRANSFERENCIA =
            By.xpath("//button[@name='action_transferir']");
    public static final By MODAL_ABIERTO =
            By.cssSelector(".modal.o_technical_modal.show, .modal.o_technical_modal.d-block");

    public static final By ASIGNAR_PERIODO =
            By.xpath("//button[@name='action_asignar_periodo_operativo']");

    public static final By CAMPO_PERIODO =
            By.id("periodo_id");

    public static By opcionPeriodo(String periodo) {
        return By.xpath(
                "//a[@role='option' and normalize-space()='" + periodo + "']");
    }

    public static final By AGREGAR_LINEA =
            By.xpath("//div[contains(@class,'o_technical_modal')]" +
                    "//a[@role='button' and normalize-space()='Agregar una línea']");

    public static final By CHECKBOX_LINEA =
            By.xpath(
                    "(//div[contains(@class,'modal') " +
                            "and contains(@class,'o_technical_modal')]" +
                            "//tr[contains(@class,'o_data_row')]" +
                            "//td[contains(@class,'o_list_record_selector')]" +
                            "//input[@type='checkbox'])[last()]"
            );

    public static final By SELECCIONAR_LINEA =
            By.cssSelector("button.o_select_button");

    public static By checkboxPorReferencia(String referencia) {
        return By.xpath(
                "//tr[contains(@class,'o_data_row')]" +
                        "[.//td[@name='reference' and normalize-space()='" + referencia + "']]" +
                        "//td[contains(@class,'o_list_record_selector')]//input[@type='checkbox']"
        );
    }


    public static final By CONFIRMAR_ASIGNAR_PERIODO =
            By.xpath("//button[@name='action_asignar_periodo']");

    public static final By ASIGNAR_PAGADURIA =
            By.xpath("//button[@name='action_asignar_pagaduria']");

    public static final By CAMPO_PAGADURIA =
            By.id("pagaduria_id");

    public static By opcionPagaduria(String pagaduria) {
        return By.xpath(
                "//a[@role='option' and normalize-space()='" + pagaduria + "']"
        );
    }

    public static final By AGREGAR_LINEA_PAGADURIA =
            By.xpath("//div[contains(@class,'o_technical_modal')]//td[contains(@class,'o_field_x2many_list_row_add')]//a[normalize-space()='Agregar una línea']");

    public static final By SELECCIONAR_LINEA_PAGADURIA =
            By.cssSelector("button.o_select_button");

    public static final By CONFIRMAR_PAGADURIA =
            By.xpath("//div[contains(@class,'o_technical_modal')]" +
                    "//button[@name='action_asignar_pagaduria']");

    public static final By CONCILIAR_PERIODO =
            By.xpath("//button[@name='action_conciliar_periodo_operativo']");

    public static final By PERIODO_CONCILIACION =
            By.id("periodo_id");

    public static final By PAGADURIA_CONCILIACION =
            By.id("pagaduria_id");

    public static final By BOTON_CONCILIAR =
            By.xpath("//button[@name='action_conciliar']");

    public static final By CONTINUAR_TODAS_FORMAS =
            By.xpath("//button[@name='action_confirmar' and .//span[normalize-space()='Continuar de todas formas']]");

    public static final By PROCESOS_CONCILIACION =
            By.xpath("//button[@name='action_proceso_conciliacion_tree']");

    public static final By PORCENTAJE_CONCILIACION =
            By.cssSelector("span.mx-1");

    public static final By TRANSACCIONES_ACTUALES =
            By.cssSelector("td[name='tx_actual_ui']");

    public static final By TRANSACCIONES_TOTALES =
            By.cssSelector("td[name='tx_total_ui']");
    public static final By DE_ACUERDO_CONCILIACION =
            By.xpath("//button[contains(@class,'btn-primary') " +
                    "and normalize-space(.)='De acuerdo']");

    public static By checkboxPagaduriaPorReferencia(String referencia) {
        return By.xpath(
                "(//div[contains(@class,'modal') " +
                        "and contains(@class,'o_technical_modal') " +
                        "and contains(@class,'d-block')])[last()]" +
                        "//tr[contains(@class,'o_data_row')]" +
                        "[.//td[@name='reference' and normalize-space()='" + referencia + "']]" +
                        "//td[contains(@class,'o_list_record_selector')]" +
                        "//input[@type='checkbox']"
        );
    }

    public static final By BUSCADOR_CUENTAS =
            By.cssSelector("input.o_searchview_input[role='searchbox']");

    public static final By MODAL_CONFIRMACION =
            By.cssSelector(".modal-content");

    public static final By ACEPTAR_CONFIRMACION =
            By.cssSelector(".modal-content .modal-footer button.btn-primary");

    public static final By PANTALLA_PROCESOS_CONCILIACION =
            By.cssSelector(
                    ".o_list_view ol.breadcrumb li.breadcrumb-item.active span.text-truncate");


    public static final By FILAS_CONCILIACION =
            By.cssSelector(".o_list_view tbody tr.o_data_row");

    public static final By ID_PROCESO =
            By.cssSelector("td[name='name']");

    public static final By TX_PROCESADAS =
            By.cssSelector("td[name='tx_actual_ui']");

    public static final By TX_TOTALES =
            By.cssSelector("td[name='tx_total_ui']");

    public static final By AVANCE_PROCESO =
            By.cssSelector("td[name='progreso_pct'] .o_progressbar_value");

    public static final By ESTATUS_PROCESO =
            By.cssSelector("td[name='estatus']");

    public static final By DEPENDENCIA_PROCESO =
            By.cssSelector("td[name='cf_dependencia_id']");

    public static final By EJECUTOR_PROCESO =
            By.cssSelector("td[name='cf_ejecutado_por']");

    public static final By USUARIO_VISIBLE =
            By.cssSelector(".oe_topbar_name");

    public static final By CONCILIACIONES_TOTALES =
            By.cssSelector(".o_form_view [name='total_conciliaciones']");

    public static final By CONCILIACIONES_REALIZADAS =
            By.cssSelector(".o_form_view [name='total_conciliaciones_realizadas']");


    // Nuevo modal de advertencia
    public static final By MODAL_ADVERTENCIA_CONCILIACION =
            By.cssSelector(".modal-content");

    public static final By MENSAJE_ADVERTENCIA_CONCILIACION =
            By.cssSelector(".alert-warning [name='mensaje']");

    public static final By CONTINUAR_CONCILIACION =
            By.cssSelector("button[name='action_confirmar']");


    public static final By TITULO_MODAL =
            By.cssSelector(".modal-title");

    public static final By MENSAJE_CONFIRMACION =
            By.cssSelector(".modal-body p");

    public static By checkboxPorReferenciaYRecibo(
            String referencia,
            String recibo) {

        return By.cssSelector(
                "tr.o_data_row" +
                        ":has(td[name='reference'][data-tooltip='" + referencia + "'])" +
                        ":has(td[name='no_recibo'][data-tooltip='" + recibo + "'])" +
                        " td.o_list_record_selector input[type='checkbox']");
    }

    public static By filasPorReferencia(String referencia) {
        return By.cssSelector(
                "tr.o_data_row:has(td[name='reference'][data-tooltip='"
                        + referencia + "'])");
    }
}
