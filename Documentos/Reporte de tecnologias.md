# Reporte de tecnologías del proyecto de automatización

Fecha: 30 de octubre de 2026. Proyecto: `qa-automation`, versión `1.0-SNAPSHOT`.

Este reporte describe lo encontrado en el código y la configuración actuales. Las versiones corresponden a las declaradas en el proyecto. Las configuraciones de Docker y GitHub Actions existen en el repositorio; su presencia no confirma que se hayan ejecutado correctamente en un servidor.

## 1. Propósito

Automatizar pruebas funcionales de procesos de Odoo mediante un navegador: pagos de nómina, promesas de pago, importación y conciliación de movimientos, domiciliación y reembolsos masivos. El proyecto también prepara archivos de entrada y genera evidencias de los resultados.

## 2. Lenguajes y formatos

| Lenguaje o formato | Uso en el proyecto |
| --- | --- |
| Java 21 | Lenguaje principal: automatización, validaciones, sesiones, procesamiento de archivos y reportes. |
| Gherkin en español | Descripción de escenarios mediante Dado, Cuando, Entonces y tablas de Ejemplos en archivos `.feature`. |
| Python | Lectura y análisis del reporte de cartera, selección de créditos activos, respaldos y verificación de los archivos de promesas. |
| JavaScript con Node.js | Actualización de los archivos Excel de promesas y generación de vistas previas mediante `@oai/artifact-tool`. |
| PowerShell y shell de Unix | Ejecución de comandos y scripts locales o de contenedor. |
| XML | Configuración Maven y resultados de pruebas compatibles con JUnit. |
| YAML | Configuración de GitHub Actions y Docker Compose. |
| Properties | Parámetros de ejecución, configuración y resultados esperados. |
| JSON y CSV | Reportes estructurados, manifiestos de preparación y métricas. |

## 3. Herramientas y bibliotecas principales

| Tecnología | Versión declarada | Función |
| --- | --- | --- |
| Selenium WebDriver | 4.40.0 | Controlar el navegador, localizar elementos, completar formularios, cargar y descargar archivos y capturar pantallas. |
| Cucumber JVM | 7.34.8 | Ejecutar los escenarios Gherkin y conectarlos con los métodos Java de cada paso. |
| JUnit Platform | 1.14.2 | Descubrir y ejecutar las suites mediante el motor de Cucumber. |
| JUnit | 4.13.2 | Dependencia adicional para soporte de pruebas y aserciones. |
| Apache Maven | No fijada en el POM | Administrar dependencias, compilar y ejecutar las pruebas. |
| Maven Compiler Plugin | 3.14.0 | Compilar el código para Java 21. |
| Maven Surefire Plugin | 3.5.2 | Ejecutar las pruebas desde Maven y producir sus resultados. |
| Exec Maven Plugin | 3.5.0 | Ejecutar utilidades y verificaciones Java auxiliares. |
| Apache POI, poi-ooxml | 5.4.1 | Leer y modificar archivos Excel desde Java. |
| OpenPDF | 1.3.43 | Crear los reportes PDF de evidencias por escenario. |
| Eclipse Angus Mail / Jakarta Mail | 2.0.3 | Construir correos locales `.eml` con evidencias adjuntas. Actualmente el envío está simulado. |
| openpyxl | No fijada en el POM | Leer y verificar archivos Excel desde los scripts Python. |
| @oai/artifact-tool | No fijada en el POM | Editar, recalcular, exportar y renderizar los Excel durante la preparación de promesas. |

Python, Node.js y sus bibliotecas de preparación utilizan actualmente el runtime disponible en el equipo de trabajo. Esa preparación requiere dichos componentes además de las dependencias Java de Maven.

## 4. Navegadores y entorno

La ejecución local utilizada es Windows 11 con Firefox y Java 21. El código también admite Google Chrome. Se puede ejecutar con ventana visible o en modo headless mediante parámetros como `browser` y `headless`.

Selenium usa FirefoxDriver para Firefox y ChromeDriver para Chrome. Las versiones de los navegadores y sus controladores dependen del entorno; no están fijadas en el POM.

Odoo es la aplicación bajo prueba. Sus formularios y menús se automatizan con selectores CSS y XPath, atributos de campos y botones, esperas explícitas y comprobaciones de estados.

## 5. Organización y enfoque de automatización

Se utiliza un enfoque BDD: los escenarios expresan el comportamiento esperado y sus pasos se implementan en Java. La estructura sigue una organización por páginas y responsabilidades:

| Capa | Responsabilidad |
| --- | --- |
| Features | Describir los casos y sus datos de prueba. |
| Steps | Asociar las frases Gherkin con las acciones de automatización. |
| Actions | Agrupar acciones del flujo. |
| Pages | Implementar la interacción y las validaciones de cada módulo. |
| Selectors | Centralizar los localizadores de los elementos. |
| Driver y sesiones | Crear y administrar los navegadores y el acceso por usuario. |
| Utils | Reutilizar navegación, esperas, resaltados, capturas y procesamiento de archivos. |
| Hooks y reports | Gestionar el ciclo de ejecución y generar evidencias y reportes. |

Los datos se parametrizan con Esquemas del escenario y tablas de Ejemplos. Los runners permiten ejecutar casos individuales o flujos completos, como `FlujoNominaRunner` y `FlujoExtrajudicialRunner`.

El flujo extrajudicial une CP03 y CP04 en una misma fila y sesión: carga de promesas seguida de importación y conciliación de movimientos.

## 6. Concurrencia y sincronización

Cucumber y JUnit Platform proporcionan la ejecución paralela. La cantidad de trabajadores se configura mediante propiedades y anotaciones de los runners; algunos runners tienen valores propios.

El código Java utiliza `ThreadLocal`, `ConcurrentHashMap` y `ReentrantLock` para administrar sesiones. En modo `porUsuario`, un usuario reutiliza su navegador y sus escenarios se ejecutan por turnos; distintos usuarios pueden avanzar en paralelo.

La preparación de los 20 archivos de promesas se coordina con `CompletableFuture`: se realiza una sola vez por corrida/JVM, y los demás escenarios esperan su resultado antes de utilizar los archivos.

Los procesos asíncronos de Odoo se verifican mediante consultas periódicas, actualización de la página y validación de estados, porcentajes y cantidades procesadas. El framework también permite pausas de depuración controladas por archivos marcadores.

## 7. Datos de prueba y archivos

Se trabajan archivos XLSX, CSV, TXT y ZIP. Las utilidades generan nombres y referencias variables para identificar las transacciones creadas durante cada prueba.

Para las promesas se descarga el Reporte Universo de Cartera, se seleccionan 10,000 créditos únicos con estado Activo y se distribuyen en 20 archivos de 500 registros. Se respaldan los archivos anteriores, se conservan las demás columnas de las plantillas y se verifican los resultados antes de publicarlos en la carpeta Generados.

## 8. Evidencias y reportes

| Salida | Contenido |
| --- | --- |
| PDF por escenario | Pasos, resultados y capturas de pantalla. |
| Capturas PNG y resaltados | Evidencia visual de las acciones realizadas en Odoo. |
| HTML de Cucumber | Consulta de los resultados de ejecución. |
| JSON de Cucumber | Resultados estructurados para procesamiento posterior. |
| XML de JUnit | Resultados compatibles con herramientas de integración continua. |
| CSV | Tiempos por escenario y métricas de procesos como la conciliación. |
| Logs | Detalle de ejecución, esperas, estados y errores. |

Las salidas principales se guardan en `output` y `target`.

## 9. Contenedores, integración continua e integraciones

Existe un Dockerfile basado en `selenium/standalone-firefox:latest` y una configuración Docker Compose para ejecutar la automatización y conservar las evidencias mediante un volumen.

Existe un workflow de GitHub Actions que contempla ejecución por push, pull request y lanzamiento manual, construcción del contenedor y publicación de evidencias con retención de 14 días. Esta configuración debe validarse por separado para los flujos actuales: la preparación de Excel utiliza componentes del runtime local y el workflow conserva referencias de nombre a SauceDemo.

La integración con Jira/Xray es una simulación HTTP local que genera archivos JSON de solicitud, respuesta y estados. No modifica Jira real. La notificación por correo genera un archivo `.eml` local y un adjunto; no realiza envío SMTP.

## 10. Archivos de referencia revisados

- `pom.xml`: dependencias, versiones y plugins de compilación y ejecución.
- `src/test/resources/junit-platform.properties`: motor, reportes y configuración de Cucumber.
- `src/test/java/runners`: suites de ejecución.
- `src/main/java/automation`: páginas, acciones, selectores, sesiones y utilidades.
- `scripts/promesas`: preparación, generación y verificación de plantillas.
- `src/test/java/reports/ScenarioPdfReporter.java`: evidencia PDF.
- `src/main/java/integrations`: simulaciones de Xray y correo.
- `Dockerfile`, `docker-compose.yml` y `.github/workflows/qa.yml`: contenedores e integración continua.
