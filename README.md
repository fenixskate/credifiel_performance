# Automatización Credifiel

Guía rápida para ejecutar las pruebas desde Git Bash o PowerShell, dentro de la carpeta del proyecto.

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

Revisar la URL y configuración en `src/test/resources/config/qa.properties`, así como los usuarios y archivos declarados en los escenarios de `src/test/resources/features/`.

Compilar sin ejecutar pruebas:

```bash
mvn -q test-compile
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

## Resultados

- Reporte HTML: `target/cucumber-report.html`.
- Reportes JSON y XML: `target/cucumber-report.json` y `target/cucumber-report.xml`.
- Evidencias PDF y tiempos: `output/pdf/`.
- Métricas de conciliación CP05: `output/metricas_conciliacion_cp05.csv`.

Revisar que los escenarios seleccionados aparezcan como `PASSED`, sin fallas ni errores, y que Maven termine con `BUILD SUCCESS`.

Las pruebas realizan cargas y conciliaciones en el ambiente configurado. Preparar los insumos antes de repetirlas. Ejecutar un solo comando Maven a la vez por carpeta de proyecto.

Las carpetas `target/` y `output/` están excluidas de Git; `Documentos/` conserva los insumos del proyecto.
