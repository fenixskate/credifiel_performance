package verification;

import automation.driver.DriverManager;
import automation.pages.CuentasPage;
import java.nio.file.*;
import java.time.Duration;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Inspeccion de procesos existentes en el navegador; no carga ni concilia pagos. */
public final class BrowserConciliationDiscovery {
 public static void main(String[] args) throws Exception {
  System.setProperty("execution.mode","aislado");
  var line=Files.readAllLines(Path.of("src/test/resources/features/flujo-nomina.feature")).stream().filter(s->s.stripLeading().startsWith("|")&&s.contains("@")).findFirst().orElseThrow();
  var cells=line.replace("\\|","__PIPE__").split("\\|",-1);
  String user=cells[1].trim(),account=cells[7].trim().replace("__PIPE__","|");
  DriverManager.start();
  try {
   new automation.actions.LoginActions(DriverManager.getDriver()).openLogin();
   new steps.LoginSteps().iniciarSesionConUsuario(user);
   var driver=DriverManager.getDriver();var page=new CuentasPage(driver);
   var wait=new WebDriverWait(driver,Duration.ofSeconds(60));
   page.ingresarModuloCuentas();
   boolean found=false;
   for(int index=0;index<10&&!found;index++){
    wait.until(d->!d.findElements(By.cssSelector(".o_kanban_details strong span")).isEmpty());
    for(var title:driver.findElements(By.cssSelector(".o_kanban_details strong span"))){
     if(title.getText().trim().equals(account)){title.click();found=true;break;}
    }
    if(!found){
     String first=driver.findElements(By.cssSelector(".o_kanban_details strong span")).getFirst().getText();
     var next=driver.findElement(By.cssSelector("button.o_pager_next"));
     if(!next.isEnabled())break;
     next.click();
     wait.until(d->!d.findElements(By.cssSelector(".o_kanban_details strong span")).getFirst().getText().equals(first));
    }
   }
   if(!found)throw new IllegalStateException("No se encontro la cuenta en las paginas visibles: "+account);
   page.abrirProcesosConciliacion();
   for(var facet:driver.findElements(By.cssSelector(".o_searchview_facet"))){
    if(facet.getText().trim().equals("Hoy"))facet.findElement(By.cssSelector(".o_facet_remove")).click();
   }
   Files.writeString(Path.of("target/browser-conciliacion-lista.html"),driver.getPageSource());
   System.out.println("[CAMPOS LISTA] "+((JavascriptExecutor)driver).executeScript("return Array.from(document.querySelectorAll('thead th')).map(e=>({name:e.getAttribute('name'),text:e.innerText}))"));
   wait.until(d->!d.findElements(automation.selectors.CuentasSelectors.ID_PROCESO).isEmpty());
   driver.findElements(automation.selectors.CuentasSelectors.ID_PROCESO).getFirst().click();
   wait.until(d->!d.findElements(By.cssSelector(".o_form_view")).isEmpty());
   Files.writeString(Path.of("target/browser-conciliacion-formulario.html"),driver.getPageSource());
   System.out.println("[CAMPOS FORMULARIO] "+((JavascriptExecutor)driver).executeScript("return Array.from(document.querySelectorAll('.o_form_view [name]')).map(e=>({name:e.getAttribute('name'),text:e.innerText.slice(0,100)})).filter(e=>/conciliaciones|dependencia|ejecutado|lanzador|periodo|pagaduria|tx_|estatus/.test(e.name))"));
   Files.write(Path.of("target/browser-conciliacion-formulario.png"),((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES));
  }finally{
   Files.writeString(Path.of("target/browser-conciliacion-ultima-vista.html"),DriverManager.getDriver().getPageSource());
   System.out.println("[URL VISTA] "+DriverManager.getDriver().getCurrentUrl());
   DriverManager.quit();
  }
 }
}
