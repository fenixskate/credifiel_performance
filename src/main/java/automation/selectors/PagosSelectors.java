
package automation.selectors;

import org.openqa.selenium.By;

public final class PagosSelectors {

    private PagosSelectors() {}

    public static final By CARTERACREDITO =
            By.cssSelector(
                    "button[data-menu-xmlid='vor_cartera.vor_cartera']");

    public static final By PAGOS_ARCHIVOS_RESPUESTA =
            By.xpath(
                    "//a[@role='menuitem' " +
                            "and normalize-space()='Pagos Archivos Respuesta']");

    public static final By NUEVO =
            By.xpath(
                    "//button[contains(@class,'o_list_button_add') " +
                            "or contains(@class,'o_form_button_create')]");

    public static final By FECHA_RECEPCION =
            By.id("fecha_recepcion");

    public static final By PERIODICIDAD =
            By.id("periodicidad");

    public static final By PERIODO_OPERATIVO =
            By.id("periodo_operativo_id");

    public static final By PAGADURIA =
            By.id("id_pagaduria");

    public static By opcionPeriodo(String periodo) {
        return By.xpath(
                "//*[@role='option' and normalize-space()='" + periodo + "']");
    }

    public static final By SUBIR_ARCHIVO =
            By.xpath("//input[@type='file' and contains(@class,'o_input_file')]");

    public static final By LEER_ARCHIVO =
            By.xpath("//button[@name='cargar_archivo']");

    public static final By PROCESAR_ARCHIVO =
            By.xpath("//button[@name='procesar_archivo_pre']");

    public static final By REGISTRAR_PAGOS =
            By.xpath("//button[@name='action_registrar_pagos']");

    public static final By ESTADO_TERMINADO =
            By.xpath("//span[contains(@class,'badge') and normalize-space()='Terminado']");

    public static final By ES_COMPLEMENTO =
            By.id("es_complemento");

    public static final By DEPENDENCIA_25 =
            By.cssSelector("input#id_dependencia");

    public static final By ERROR_ODOO =
            By.cssSelector(".modal.o_technical_modal .modal-body");

}
