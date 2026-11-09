# Automatización Credifiel

Guía para ejecutar y mantener las pruebas web de Credifiel desde PowerShell o Git Bash.

- [Preparar el ambiente de QA](#ambiente-de-qa)
- [Ejecutar las pruebas](#nómina-carga-de-archivos-y-conciliación)
- [Explorar el árbol del proyecto](#arquitectura-del-proyecto)
- [Seguir un paso de Cucumber hasta su XPath](#ejemplo-cp02-del-feature-al-xpath)
- [Revisar resultados y fallos](#resultados)

## Requisitos

- Java JDK 21.
- Maven 3.9 o superior.
- Git.
- Google Chrome o Firefox instalado, según el navegador elegido.
- Acceso al ambiente de pruebas, usuarios con permisos e insumos en `Documentos/`.
- Internet para descargar dependencias y controladores en la primera ejecución.

Verificar la instalación:

```bash
java -version
mvn -version
```

Abre PowerShell en la carpeta raíz del proyecto. Antes de ejecutar, confirma la dirección de QA, los accesos y los archivos que usa el escenario.

Compilar sin ejecutar pruebas:

```bash
mvn -q test-compile
```

## Ambiente de QA

1. En el Explorador de archivos, abre `credifiel_performance → src → test → resources → config`.
2. Abre [qa.properties](src/test/resources/config/qa.properties) con Bloc de notas o tu editor. Busca `base.url=` para ver la dirección actual de inicio de sesión de Odoo.
3. Si QA te proporciona una dirección nueva, guarda el valor anterior y reemplaza solo lo que está después del signo `=`. Guarda con **Ctrl+S**.
4. Abre la dirección nueva en el navegador y confirma que corresponde a QA. Luego ejecuta el caso acordado con el navegador visible.

| Opción | Uso |
| --- | --- |
| `base.url` | Dirección completa de inicio de sesión en QA. |
| `timeout.explicit.seconds` | Tiempo de espera para un elemento o una condición. El archivo indica 10 segundos y los comandos de CP05 usan 60 para esa corrida. |
| `timeout.pageLoad.seconds` | Tiempo máximo para que cargue una página. El archivo indica 30 segundos. |

Si la prueba sigue abriendo otra dirección, revisa si el comando incluye `-Dbase.url` o si la sesión tiene configurada la variable `BASE_URL`. Ambos pueden prevalecer sobre el archivo. Para revertir una edición en `qa.properties`, restaura el valor que guardaste.

Los escenarios parametrizados toman el usuario y el archivo de su tabla **Ejemplos**. Confirma con QA que el usuario tenga permisos y que el insumo esté disponible en `Documentos/`. Si soporte te pide cargar la contraseña para esta sesión de PowerShell, usa la credencial de prueba autorizada:

```powershell
$qaClave = Read-Host 'Contraseña autorizada de QA' -AsSecureString
$env:LOGIN_PASSWORD = [System.Net.NetworkCredential]::new('', $qaClave).Password
```

## Nómina: carga de archivos y conciliación

Con Chrome:

```bash
mvn test "-Dtest=FlujoNominaRunner" "-Dparallel.enabled=true" "-Dparallel.threads=5" "-Dheadless=false" "-Dbrowser=chrome"
```

Con Firefox:

```bash
mvn test "-Dtest=FlujoNominaRunner" "-Dparallel.enabled=true" "-Dparallel.threads=5" "-Dheadless=false" "-Dbrowser=firefox"
```

## Extrajudicial: promesas y movimientos

Con Chrome:

```bash
mvn test "-Dtest=FlujoExtrajudicialRunner" "-Dparallel.enabled=true" "-Dparallel.threads=5" "-Dheadless=false" "-Dbrowser=chrome"
```

Con Firefox:

```bash
mvn test "-Dtest=FlujoExtrajudicialRunner" "-Dparallel.enabled=true" "-Dparallel.threads=5" "-Dheadless=false" "-Dbrowser=firefox"
```

Cada escenario del flujo integrado realiza sus etapas en orden. Si una etapa falla, las siguientes quedan omitidas.

## CP05: Domiciliación, los tres bancos

BBVA, Banamex y Santander en secuencia, con Firefox:

```bash
mvn test "-Dtest=TestRunner" "-Dcucumber.filter.tags=@web and @CP05" "-Dparallel.enabled=false" "-Dheadless=false" "-Dbrowser=firefox" "-Dtimeout.explicit.seconds=60"
```

Para ejecutarlos en paralelo:

```bash
mvn test "-Dtest=TestRunner" "-Dcucumber.filter.tags=@web and @CP05" "-Dparallel.enabled=true" "-Dparallel.threads=3" "-Dheadless=false" "-Dbrowser=firefox" "-Dtimeout.explicit.seconds=60"
```

Para usar Chrome, cambiar `"-Dbrowser=firefox"` por `"-Dbrowser=chrome"`.

## Ejecutar un caso individual

Cambiar `@CP03` por la etiqueta del caso requerido:

```bash
mvn test "-Dtest=TestRunner" "-Dcucumber.filter.tags=@CP03" "-Dparallel.enabled=false" "-Dheadless=false" "-Dbrowser=chrome"
```

## Opciones rápidas

| Opción | Uso |
|---|---|
| `-Dbrowser=chrome` | Usar Chrome. |
| `-Dbrowser=firefox` | Usar Firefox. |
| `-Dheadless=false` | Mostrar las ventanas. |
| `-Dheadless=true` | Ejecutar sin ventanas. |
| `-Dexecution.mode=porUsuario` | Conservar una sesión por usuario; es el modo predeterminado. |
| `-Dexecution.mode=aislado` | Abrir y cerrar un navegador por escenario. |

Los runners integrados de Nómina y Extrajudicial tienen actualmente una configuración de cinco hilos en sus anotaciones. Para cambiar su concurrencia hay que revisar también esa configuración; no basta con cambiar el comando.

Para llegar a 15 o 20 navegadores activos se necesitan suficientes escenarios, usuarios distintos, insumos independientes y ajustar la configuración del runner utilizado. Más hilos no generan automáticamente más escenarios.

## Arquitectura del proyecto

Abre la carpeta raíz `credifiel_performance` en el editor. El árbol muestra las carpetas y los archivos principales de las pruebas web.

```text
credifiel_performance/
├─ pom.xml                              Configuración de Maven
├─ Documentos/                          Insumos de prueba
├─ src/
│  ├─ test/
│  │  ├─ resources/
│  │  │  ├─ features/
│  │  │  │  ├─ saucedemo.feature         Casos CP01 a CP06
│  │  │  │  ├─ flujo-nomina.feature
│  │  │  │  └─ flujo-extrajudicial.feature
│  │  │  ├─ config/qa.properties        Ambiente y tiempos de espera
│  │  │  └─ junit-platform.properties    Configuración de Cucumber y reportes
│  │  └─ java/
│  │     ├─ runners/
│  │     │  ├─ TestRunner.java
│  │     │  ├─ FlujoNominaRunner.java
│  │     │  └─ FlujoExtrajudicialRunner.java
│  │     ├─ steps/                       Frases de Cucumber y métodos Java
│  │     ├─ hooks/Hooks.java              Inicio, cierre y capturas
│  │     └─ reports/ScenarioPdfReporter.java
│  └─ main/java/
│     ├─ config/ConfigManager.java       Lectura de la configuración
│     └─ automation/
│        ├─ actions/                     Acciones por módulo
│        ├─ pages/                       Operaciones en las pantallas
│        ├─ selectors/                   Identificadores de botones y campos
│        ├─ driver/                      Navegador y sesiones
│        └─ utils/                       Esperas y otras utilidades compartidas
├─ target/                              Reportes HTML, JSON y XML
└─ output/                              Evidencias PDF y métricas
```

El **runner** elige el feature. El **feature** describe el escenario y sus datos. Cucumber conecta cada frase con un método en **steps**. El método llama a **actions**, que delega la operación de la pantalla en **pages**. La pantalla usa **selectors** para encontrar el botón, campo u opción que necesita. Algunos XPath se construyen directamente en **pages** cuando dependen de un dato, como el nombre de una cuenta.

### Clases y métodos para buscar

| Carpeta | Clase | Métodos o elementos de ejemplo |
| --- | --- | --- |
| `steps/` | `LoginSteps` | `openLogin()`, `iniciarSesionConUsuario()` |
| `steps/` | `PagosSteps` | `leerArchivo()`, `procesarArchivo()`, `registrarPagos()` |
| `steps/` | `CuentasSteps` | `seleccionarCuenta()`, `transferirTransacciones()` |
| `steps/` | `PromesasSteps` | `subirArchivo()`, `procesarPromesas()` |
| `steps/` | `MovimientosSteps` | `generarExportacion()`, `confirmarConciliacion()` |
| `steps/` | `FacturacionSteps` | `crearSolicitud()`, `seleccionarBanco()` |
| `steps/` | `ReembolsosSteps` | `archivo()`, `leer()`, `validar()` |
| `actions/` | `CuentasActions` | `transferirTransacciones()` |
| `pages/` | `CuentasPage` | `transferirTransacciones()`, `opcionDestino` |
| `selectors/` | `CuentasSelectors` | `TRANSFERIR_SELECCIONADOS` |
| `driver/` | `DriverManager` | `start()`, `getDriver()`, `quit()` |
| `utils/` | `WaitUtils` | `visible()`, `clickable()` |

## Ejemplo CP02: del feature al XPath

El caso transfiere transacciones a una cuenta. Abre los archivos en este orden. Usa **Ctrl+F** con el nombre que aparece a la derecha o «Ir a definición» sobre la llamada en el editor.

```text
credifiel_performance/
├─ src/test/resources/features/saucedemo.feature → @CP02
├─ src/test/java/steps/CuentasSteps.java → transferirTransacciones()
└─ src/main/java/automation/
   ├─ actions/CuentasActions.java → transferirTransacciones()
   ├─ pages/CuentasPage.java → transferirTransacciones()
   └─ selectors/CuentasSelectors.java → TRANSFERIR_SELECCIONADOS
```

1. En [saucedemo.feature](src/test/resources/features/saucedemo.feature), busca `@CP02` y la frase `transfiere las transacciones seleccionadas a la cuenta`.
2. En [CuentasSteps.java](src/test/java/steps/CuentasSteps.java), busca la misma frase. La anotación `@Y` la liga al método de abajo. `{string}` recibe la cuenta que el feature pone entre comillas.

   ```java
   @Y("transfiere las transacciones seleccionadas a la cuenta {string}")
   public void transferirTransacciones(String cuenta) {
       actions().transferirTransacciones(cuenta);
   }
   ```

3. En [CuentasActions.java](src/main/java/automation/actions/CuentasActions.java), abre `transferirTransacciones`. Verás la llamada `cuentasPage.transferirTransacciones(cuenta)`.
4. En [CuentasPage.java](src/main/java/automation/pages/CuentasPage.java), busca el mismo método. Verás que el primer clic usa `CuentasSelectors.TRANSFERIR_SELECCIONADOS`. Si lo que falla es la opción de la lista al escribir la cuenta, busca `opcionDestino` en ese método.
5. En [CuentasSelectors.java](src/main/java/automation/selectors/CuentasSelectors.java), busca `TRANSFERIR_SELECCIONADOS`. Ahí está el XPath actual del botón:

   ```java
   public static final By TRANSFERIR_SELECCIONADOS =
           By.xpath("//button[@name='action_transferencia_multiple']");
   ```

### Si cambia el botón

En Chrome, abre la pantalla de QA y haz clic derecho en el botón para elegir **Inspeccionar**. En el panel **Elementos**, revisa su atributo `name`. Supón que ahora se llama `action_transferir_seleccionados`. El cambio ilustrativo sería:

```java
public static final By TRANSFERIR_SELECCIONADOS =
        By.xpath("//button[@name='action_transferir_seleccionados']");
```

Antes de editar, busca el XPath con **Ctrl+F** dentro del panel **Elementos**. Debe resaltar el botón correcto y mostrar un solo resultado. Usa el valor confirmado en QA, guarda el cambio y ejecuta `@CP02` para comprobar que la transferencia y la conciliación llegan al resultado previsto.

## Ejemplo CP05: cambiar una frase o un dato

```text
credifiel_performance/
├─ src/test/resources/features/saucedemo.feature → @CP05 → Ejemplos
├─ src/test/java/steps/FacturacionSteps.java → seleccionarBanco()
└─ src/main/java/automation/
   ├─ actions/FacturacionActions.java → seleccionarBanco()
   ├─ pages/FacturacionPage.java → seleccionarBanco()
   └─ selectors/FacturacionSelectors.java → BANCO
```

En `saucedemo.feature`, Cucumber reemplaza `<banco>` con el dato de cada fila de **Ejemplos**. Si cambia el banco o el archivo aprobado para una prueba, revisa la fila y columna de esa tabla. Si se acuerda cambiar la frase, actualiza también la anotación en [FacturacionSteps.java](src/test/java/steps/FacturacionSteps.java). Revisa si otros escenarios todavía usan la frase anterior.

| Archivo | Actual | Cambio ilustrativo |
| --- | --- | --- |
| `saucedemo.feature` | `Y el usuario selecciona el banco de facturación "<banco>"` | `Y el usuario elige el banco de facturación "<banco>"` |
| `FacturacionSteps.java` | `@Y("el usuario selecciona el banco de facturación {string}")` | `@Y("el usuario elige el banco de facturación {string}")` |

Si la acción sigue siendo la misma, conserva `seleccionarBanco()`. Si cambia lo que hace la prueba, sigue la llamada a `FacturacionActions.java` y después a `FacturacionPage.java`. Vuelve a ejecutar `@CP05` y confirma en el reporte que Cucumber reconoció la frase y que se ejecutaron las filas previstas.

## Resultados

- Reporte HTML: `target/cucumber-report.html`.
- Reportes JSON y XML: `target/cucumber-report.json` y `target/cucumber-report.xml`.
- Evidencias PDF y tiempos: `output/pdf/`.
- Métricas de conciliación CP05: `output/metricas_conciliacion_cp05.csv`.

Revisar que los escenarios seleccionados aparezcan como `PASSED`, sin fallas ni errores, y que Maven termine con `BUILD SUCCESS`.

Las pruebas realizan cargas y conciliaciones en el ambiente configurado. Preparar los insumos antes de repetirlas. Ejecutar un solo comando Maven a la vez por carpeta de proyecto.

Las carpetas `target/` y `output/` están excluidas de Git; `Documentos/` conserva los insumos del proyecto.

### Si falla una prueba

| Qué ves | Qué revisar |
| --- | --- |
| Maven o el navegador no abre | Guarda el primer error y confirma la instalación, el navegador elegido y el acceso a dependencias. |
| No inicia sesión o falta un archivo | Confirma la URL de QA, los permisos y la fila de **Ejemplos** que indica el archivo. |
| No encuentra un botón o se agota la espera | Guarda el primer paso fallido y la captura. Usa el recorrido de CP02 para ubicar la clase y el selector correspondientes. |
| El resultado difiere de lo esperado | Pide a QA y Negocio que comparen el estado y los totales reales con el resultado acordado. |

Antes de repetir, guarda la consola y el reporte de esa corrida. Registra el caso, la fila, el archivo y el primer paso fallido. Si la prueba ya hizo una carga, transferencia o conciliación, confirma con QA qué quedó registrado y con qué datos se puede repetir.
