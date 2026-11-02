package automation.utils;

import automation.selectors.*;
import org.openqa.selenium.By;

/** Parámetros de navegación; todas las rutas usan el mismo algoritmo de OdooNavigation. */
public enum OdooMenu {
    PRESTAMOS(CuentasSelectors.MENU_INICIO,
            By.cssSelector("a[data-menu-xmlid='vor_credito_core.vor_gestion_cartera_top']"),
            By.cssSelector("button[data-menu-xmlid='vor_cartera.vor_cobranza_menu']")),
    REPORTES_CARTERA(By.cssSelector("button[data-menu-xmlid='vor_cartera.vor_cobranza_menu']"),
            By.cssSelector("a[data-menu-xmlid='vor_cartera.menu_cre_reporte_central']"),
            By.cssSelector("button.o_list_button_add")),
    PAGOS(PagosSelectors.CARTERACREDITO, PagosSelectors.PAGOS_ARCHIVOS_RESPUESTA, PagosSelectors.NUEVO),
    CUENTAS(CuentasSelectors.MENU_INICIO, CuentasSelectors.MENU_CUENTAS, By.cssSelector(".o_kanban_renderer")),
    LISTADO_CUENTAS(CuentasSelectors.MENU_INICIO, CuentasSelectors.MENU_CUENTAS_PRINCIPAL, By.cssSelector(".o_kanban_renderer")),
    PROMESAS(PromesasSelectors.CARTERA_CREDITO, PromesasSelectors.CARGA_PROMESAS, PromesasSelectors.NUEVO),
    EXPORTAR_MOVIMIENTOS(MovimientosSelectors.MENU_MOVIMIENTOS_NO_CONCILIADOS,
            MovimientosSelectors.EXPORTAR_MOVIMIENTOS, MovimientosSelectors.NUEVA_EXPORTACION),
    IMPORTAR_MOVIMIENTOS(MovimientosSelectors.MENU_MOVIMIENTOS_NO_CONCILIADOS,
            MovimientosSelectors.IMPORTAR_MOVIMIENTOS_MENU, MovimientosSelectors.NUEVA_IMPORTACION_MOVIMIENTOS),
    DOMICILIACION(FacturacionSelectors.MENU_INICIO, FacturacionSelectors.APP_DOMICILIACION, FacturacionSelectors.MENU_FACTURACION),
    SOLICITUD_FACTURACION(FacturacionSelectors.MENU_FACTURACION, FacturacionSelectors.SOLICITUD_FACTURACION,
            By.xpath("//ol[contains(@class,'breadcrumb')]//*[normalize-space()='Solicitud de Facturación']")),
    REEMBOLSOS(ReembolsosSelectors.MENU_INICIO, ReembolsosSelectors.REEMBOLSOS, ReembolsosSelectors.NUEVO);

    final By parent;
    final By option;
    final By destination;

    OdooMenu(By parent, By option, By destination) {
        this.parent = parent;
        this.option = option;
        this.destination = destination;
    }
}
