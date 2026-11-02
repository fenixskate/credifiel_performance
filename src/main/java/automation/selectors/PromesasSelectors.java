
package automation.selectors;

import org.openqa.selenium.By;

public final class PromesasSelectors {

    private PromesasSelectors() {}

    public static final By CARTERA_CREDITO =
            By.cssSelector(
                    "button[data-menu-xmlid='vor_cartera.vor_cartera']");

    public static final By CARGA_PROMESAS =
            By.cssSelector(
                    "a[data-menu-xmlid='vor_cte_credifiel.menu_carga_promesas']");

    public static final By NUEVO =
            By.cssSelector("button.o_list_button_add");

    public static final By SUBIR_ARCHIVO =
            By.cssSelector(
                    "div[name='file'] input[type='file']");

    public static final By LEER_ARCHIVO =
            By.cssSelector(
                    "button[name='action_procesar_excel']");

    public static final By PROCESAR_PROMESAS =
            By.cssSelector(
                    "button[name='procesar_promesas_pago']");

    // Tabla de promesas identificada por sus encabezados
    public static final By FILAS_PROMESAS =
            By.cssSelector(
                    "div[name='line_ids'] tbody tr.o_data_row");

    public static final By ESTATUS_FILA =
            By.cssSelector("td[name='status']");

    public static final By NOMBRE_FILA =
            By.cssSelector("td[name='name']");

    public static final By MENSAJE_FILA =
            By.cssSelector("td[name='message']");
    public static final By SIGUIENTE_PAGINA =
            By.cssSelector(
                    "div[name='line_ids'] button.o_pager_next");

    public static final By PAGINA_ANTERIOR =
            By.cssSelector(
                    "div[name='line_ids'] button.o_pager_previous");


    public static final By RANGO_PROMESAS =
            By.cssSelector(
                    "div[name='line_ids'] .o_pager_value");

    public static final By TOTAL_PROMESAS =
            By.cssSelector(
                    "div[name='line_ids'] .o_pager_limit");


    public static final By TOTAL_REGISTROS =
            By.cssSelector("div[name='line_count'] span");

    public static final By REGISTROS_EXITOSOS =
            By.cssSelector("div[name='success_line_count'] span");

    public static final By REGISTROS_FALLIDOS =
            By.cssSelector("div[name='failed_line_count'] span");

}
