package automation.selectors;

import org.openqa.selenium.By;

public final class ReembolsosSelectors {
    private ReembolsosSelectors() {}
    public static final By MENU_INICIO = By.cssSelector("button[title='Menú de Inicio']");
    public static final By REEMBOLSOS = By.cssSelector("a[data-menu-xmlid='vor_cte_credifiel.vor_cf_reembolsos']");
    public static final By NUEVO = By.cssSelector("button.o-kanban-button-new, button.o_list_button_add");
    public static final By MASIVO = By.cssSelector("input[type='radio'][data-value='masivo']");
    public static final By EXCEL = By.cssSelector("input[type='radio'][data-value='xlsx']");
    public static final By SUBTIPO = By.id("cf_subtipo_reembolso");
    public static final By LEER = By.cssSelector("button[name='cargar_archivo']");
    public static final By AVANZAR = By.cssSelector("button[name='avanzar_reg_a_autorizacion']");
    public static final By TIPO_RECHAZO = By.id("cf_tipo_rechazo");
    public static final By MOTIVO_DOCUMENTOS = By.id("cf_motivo_rechazo_docu");
    public static final By SUBDIRECTOR_OPERACIONES = By.cssSelector("button[name='action_aprobado3']");
    public static final By AVANZAR_LIBERACION = By.cssSelector("button[name='avanzar_aut_a_lib']");
    public static final By AVANZAR_APROBADO = By.cssSelector("button[name='avanzar_lib_a_aprob']");
}
