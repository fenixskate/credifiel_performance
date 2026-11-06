package verification;

import automation.driver.DriverManager;
import automation.pages.CuentasPage;
import automation.pages.PagosPage;
import automation.utils.BrowserConciliationReport;
import java.nio.file.*;
import org.openqa.selenium.TimeoutException;

/** Controles locales en Firefox: no inicia sesion ni envia pagos a Odoo. */
public final class BrowserConciliationMetricsSmoke {
 public static void main(String[] args) throws Exception {
  Path pointer=Path.of("target/current-run.txt");
  byte[] previous=Files.exists(pointer)?Files.readAllBytes(pointer):null;
  Path out=Files.createTempDirectory(Path.of("target"),"browser-metrics-smoke-").toAbsolutePath();
  Files.writeString(pointer,out.toString());
  try {
   DriverManager.start();
   for(String kind:new String[]{"exito","cero_no_es_exito","fallido","timeout_transaccion"}){
    BrowserConciliationReport.begin();
    BrowserConciliationReport.set("Usuario","control-local");
    BrowserConciliationReport.set("Dependencia","DEP_A");
    BrowserConciliationReport.set("Pagaduria","ESTATAL, DEP_A");
    BrowserConciliationReport.set("Cuenta","Cuenta de control local");
    boolean success=kind.equals("exito"),failed=kind.equals("fallido");
    String state=success?"Conciliado":failed?"Fallido":"Conciliando";
    Path fixture=out.resolve(kind+".html");
    Files.writeString(fixture,html(success?99:failed?1:0,success?99:failed?2:0,success?100:failed?50:0,state));
    DriverManager.getDriver().get(fixture.toUri().toString());
    new PagosPage(DriverManager.getDriver()).subirArchivo("CECYTE_CHIAPAS__ESTATAL.xlsx");
    var page=new CuentasPage(DriverManager.getDriver());
    long whole=System.nanoTime();
    if(success)Thread.sleep(1000); // Representa trabajo anterior a Conciliar, fuera de la medicion.
    System.setProperty("web.conciliation.timeout.seconds",success||failed?"30":"1");
    System.setProperty("web.conciliation.poll.seconds","1");
    System.setProperty("web.transaction.timeout.seconds","1");
    try {
     if(kind.equals("timeout_transaccion")){
      page.validarEstadoSinIdentificar();page.seleccionarTransaccion();
     }else{
      page.ejecutarConciliacion();
      if(success)Thread.sleep(1000); // Debe seguir contando antes del paso de validacion.
      page.validarConciliacionCompleta();
     }
     if(!success)throw new AssertionError("El control debia fallar: "+kind);
    }catch(TimeoutException expected){
     if(success||failed)throw expected;
     BrowserConciliationReport.failure(expected);
    }catch(IllegalStateException expected){
     if(!failed)throw expected;
     BrowserConciliationReport.failure(expected);
    }
    byte[] attachment=BrowserConciliationReport.takeAttachment();
    if(attachment==null)throw new AssertionError("Sin reporte para "+kind);
    if(success){
     double measured=Double.parseDouble(BrowserConciliationReport.get("TiempoConciliacionSegundos"));
     double wholeSeconds=(System.nanoTime()-whole)/1e9;
     if(measured<1||measured>=wholeSeconds-0.7)throw new AssertionError("Limites del cronometro incorrectos");
     if(!BrowserConciliationReport.get("Proceso").equals("PC101")
       ||!BrowserConciliationReport.get("ConciliacionesRealizadas").equals("70")
       ||!BrowserConciliationReport.get("TransaccionesProcesadas").equals("99"))throw new AssertionError("Se mezclaron proceso o contadores");
    }else if(!BrowserConciliationReport.get("Estado").equals("FALLIDO")
       ||!BrowserConciliationReport.get("TiempoConciliacionSegundos").isEmpty())throw new AssertionError("Fallo registrado como cierre completo");
    if(kind.equals("cero_no_es_exito")&&!BrowserConciliationReport.get("TransaccionesTotales").equals("0"))throw new AssertionError("No se observo 0/0");
    if(kind.equals("timeout_transaccion")&&(!BrowserConciliationReport.get("LimiteEsperaSegundos").equals("1")
       ||Double.parseDouble(BrowserConciliationReport.get("EsperaObservadaSegundos"))<1))throw new AssertionError("Timeout sin plazo y espera real");
    Files.write(out.resolve(kind+".csv"),attachment);
    if(success)Files.writeString(out.resolve("duracion_control.txt"),String.valueOf((System.nanoTime()-whole)/1e9));
    System.out.println("[CONTROL OK] "+kind);
   }
   var pool=java.util.concurrent.Executors.newFixedThreadPool(2);
   var barrier=new java.util.concurrent.CyclicBarrier(2);
   try {
    var tasks=new java.util.ArrayList<java.util.concurrent.Future<?>>();
    for(int index=0;index<2;index++){
     final int id=index;
     tasks.add(pool.submit(()->{
      BrowserConciliationReport.begin();
      try {
       BrowserConciliationReport.set("Usuario","hilo-"+id);
       BrowserConciliationReport.set("Dependencia","PARALELO_"+id);
       BrowserConciliationReport.set("ConciliacionesRealizadas",7+id);
       BrowserConciliationReport.startConciliation(java.time.Duration.ofSeconds(30));
       barrier.await();
       if(!BrowserConciliationReport.get("Dependencia").equals("PARALELO_"+id))throw new AssertionError("Contexto compartido entre hilos");
       BrowserConciliationReport.success(System.nanoTime());
       return null;
      }finally{BrowserConciliationReport.clear();}
     }));
    }
    for(var task:tasks)task.get();
    System.out.println("[CONTROL OK] escritura paralela y aislamiento de contexto");
   }finally{pool.shutdown();}
   Files.writeString(Path.of("target/browser-metrics-smoke-last.txt"),out.toString());
   System.out.println("[CONTROLES LOCALES] "+out);
  }finally{
   DriverManager.quit();BrowserConciliationReport.clear();
   if(previous!=null)Files.write(pointer,previous);else Files.deleteIfExists(pointer);
  }
 }

 private static String row(String name,String dependency,String executor,int actual,int total,int percent,String state){
  return "<tr class='o_data_row'><td name='name' onclick=\"document.getElementById('list').style.display='none';document.getElementById('form').style.display='block'\">"+name+"</td><td name='cf_dependencia_id'>"+dependency+"</td><td name='cf_ejecutado_por'>"+executor+"</td><td name='tx_actual_ui'>"+actual+"</td><td name='tx_total_ui'>"+total+"</td><td name='progreso_pct'><span class='o_progressbar_value'>"+percent+"%</span></td><td name='estatus'>"+state+"</td></tr>";
 }

 private static String html(int actual,int total,int percent,String state){
  return """
   <html><meta charset='utf-8'><body><span class='oe_topbar_name'>Polet Valdes</span>
   <input type='file' class='o_input_file'><span class='badge'>Sin identificar</span><table><tr><td name='reference'>123456789</td></tr></table>
   <button name='action_conciliar' onclick="document.getElementById('confirm').style.display='block'">Conciliar</button>
   <div id='confirm' class='modal-content' style='display:none'><h2 class='modal-title'>Confirmación</h2><div class='modal-body'><p>Esta operación no se puede deshacer.</p></div>
   <div class='modal-footer'><button class='btn-primary' onclick="document.getElementById('confirm').style.display='none';document.getElementById('warning').style.display='block'">Aceptar</button></div></div>
   <div id='warning' class='modal-content' style='display:none'><h2 class='modal-title'>Odoo</h2><div class='alert-warning'><span name='mensaje'>Pagos Registrados</span></div>
   <button name='action_confirmar' onclick="document.getElementById('warning').style.display='none'">Continuar de todas formas</button></div>
   <div id='list' class='o_list_view'><table><tbody>
   """+row("PC999","OTRA_DEPENDENCIA","Polet Valdes",999,999,100,"Conciliado")
    +row("PC888","DEP_A","Otro usuario",888,888,100,"Conciliado")
    +row("PC101","DEP_A","Polet Valdes",actual,total,percent,state)
    +"""
   </tbody></table></div><div id='form' class='o_form_view' style='display:none'><div name='total_conciliaciones'>70</div><div name='total_conciliaciones_realizadas'>70</div></div>
   </body></html>
   """;
 }
}
