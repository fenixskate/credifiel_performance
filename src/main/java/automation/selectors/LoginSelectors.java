package automation.selectors;

import org.openqa.selenium.By;

/** Localizadores de LoginPage. No ejecuta acciones ni mantiene estado del navegador. */
public final class LoginSelectors {
    public static final By USERNAME = By.xpath("//*[@id=\"login\"]");
    public static final By PASSWORD = By.xpath("//*[@id=\"password\"]");
    public static final By ERROR_LOGIN = By.cssSelector("form.oe_login_form .alert.alert-danger[role='alert']");
    public static final By SUBMIT = By.xpath("//*[@id=\"wrapwrap\"]/main/div/form/div[3]/button");

    public static final By CARTERACREDITO = By.xpath("/html/body/header/nav/div[2]/div[2]/button");

    public static final By PAGOS_ARCHIVOS_RESPUESTA = By.xpath("/html/body/header/nav/div[2]/div[2]/div/a[8]");

    public static final By NUEVO = By.xpath("//button[contains(@class,'o_list_button_add') and .//span[normalize-space()='Nuevo']]");


    public static final By DEPENDENCIA_25 = By.xpath("//input[@id='id_dependencia']");


    public static final By SELECCIONA_DEPENDENCIA = By.xpath("//a[@role='option' and normalize-space()='SNTE 25 QUINTANA ROO']");
    public static final By FECHA_RECEPCION = By.xpath("//input[@id='fecha_recepcion']");
    public static final By PERIODICIDAD = By.xpath("//select[@id='periodicidad']");
    public static final By SUBIR_ARCHIVO = By.xpath("//input[@type='file' and contains(@class,'o_input_file')]");

    public static final By PERIODO_OPERATIVO =
            By.xpath("//input[@id='periodo_operativo_id']");

    public static final By OPCION_PERIODO =
            By.xpath("//*[@role='option' and normalize-space()='Periodo 01/2025 - Quincenal']");

    public static final By LEER_ARCHIVO =
            By.xpath("//button[@name='cargar_archivo']");

    public static final By PROCESAR_ARCHIVO =
            By.xpath("//button[@name='procesar_archivo_pre']");

    public static final By ESTADO_TERMINADO =
            By.xpath("//span[contains(@class,'badge') and normalize-space()='Terminado']");

    public static final By REGISTRAR_PAGOS =
            By.xpath("//button[@name='action_registrar_pagos']");

    public static final By ES_COMPLEMENTO =
            By.xpath("//input[@id='es_complemento']");

    public static final By MENU_INICIO =
            By.cssSelector(".o_main_navbar .o_navbar_apps_menu > button.dropdown-toggle");

    public static final By DETALLE_CUENTA =
            By.xpath("//*[normalize-space()='Nómina | Invex 2595']");


    public static final By IMPORTAR_LAYOUT =
            By.xpath("//button[@name='action_importar_layout']");

    public static final By LAYOUT_BANCO =
            By.xpath("//input[@id='layout_banco_id']");

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
}





