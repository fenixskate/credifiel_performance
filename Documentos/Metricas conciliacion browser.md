# Mediciones de conciliacion en navegador

Las ejecuciones web de nomina guardan `metricas_cp02.csv` en la misma carpeta de evidencias que los PDF, indicada por `target/current-run.txt`. Cada escenario conserva su usuario, dependencia, pagaduria, periodo, cuenta, referencia y archivo ARR en un contexto separado por hilo. Los usuarios y ejemplos permanecen sin cambios.

`TiempoConciliacionSegundos` comienza inmediatamente antes del clic real en **Conciliar**, con el boton ya disponible. Termina cuando el navegador confirma **Conciliado**, **100 %**, total positivo y transacciones procesadas iguales al total. No se reinicia al entrar al paso de validacion. Incluye las confirmaciones y la observacion del DOM; excluye CP01, importacion, transferencia, lectura posterior de los contadores y generacion de reportes. Es tiempo observado en el navegador, no tiempo interno de CPU del servidor.

El proceso se identifica dentro de la cuenta abierta por dependencia y ejecutor visible. Al cerrar, se abre ese proceso y se leen los campos visibles `total_conciliaciones` y `total_conciliaciones_realizadas`. Se reportan separados de `tx_actual_ui`/`tx_total_ui` y de las filas del ARR (sin cabecera ni filas vacias). No se supone equivalencia entre estos contadores. Los valores ausentes quedan vacios y no se sustituyen por cero.

Los fallos incluyen etapa, ultimo avance observado, limite configurado, espera observada y comentario. `TiempoConciliacionSegundos` queda vacio si no se confirmo el cierre. `TiempoTotalHastaFalloSegundos` se mide desde el inicio del escenario web y se mantiene separado. Un timeout no demuestra que Odoo haya detenido el trabajo. Si una operacion del navegador agota un plazo menor antes del limite general, el comentario lo distingue. El CSV tambien se adjunta como evidencia del paso en Cucumber; los PDF conservan sus capturas habituales.

Plazos predeterminados:

- `web.transaction.timeout.seconds=1200`: 20 minutos para Sin identificar o la aparicion de la transaccion bancaria.
- `web.conciliation.timeout.seconds=1800`: 30 minutos desde Conciliar hasta el cierre observado.
- `web.conciliation.poll.seconds=4`: intervalo de consulta de cierre.
- La aparicion de transacciones reutiliza `PaymentStateWait`: consulta el DOM y refresca con el intervalo existente `payments.refresh.seconds` (15 segundos por defecto).
- Identificar el proceso, seleccionar el recibo y leer los contadores conservan plazos de interfaz de 60 segundos, registrados como etapas distintas.

Se crea una fila tanto para exito como para fallo. La escritura CSV se sincroniza entre hilos, escapa comas/comillas y conserva los archivos anteriores. Un archivo existente con encabezados del formato anterior no se sobrescribe ni mezcla: se informa el conflicto. Iniciar una ejecucion normal nueva reserva su propia carpeta de evidencias.

Validacion local, sin iniciar sesion ni registrar pagos en Odoo:

```powershell
mvn -q test-compile exec:java "-Dexec.mainClass=verification.BrowserConciliationMetricsSmoke" "-Dbrowser=firefox" "-Dheadless=true"
```

Los controles usan paginas locales y revisan cierre correcto, rechazo de 0/0, estado Fallido, timeout de transaccion y escritura concurrente con contextos independientes. Sus archivos quedan en `target/browser-metrics-smoke-*`; no son resultados de negocio. El archivo `current-run.txt` se restaura al terminar.

`verification.BrowserConciliationDiscovery` inspecciona procesos ya existentes, sin crear ni conciliar pagos. Su navegacion por paginacion evita depender del buscador de cuentas durante la inspeccion; solo esta inspeccion retira el filtro Hoy para leer datos historicos. No cambia el buscador del flujo de produccion.
