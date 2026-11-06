# AGENTS.md — Credifiel Performance

## 1. Directorio oficial del proyecto

La raíz oficial y única de trabajo es:

`C:\Users\Cuevita\Documents\GIT\credifiel_performance`

Esta indicación aplica a todo el proyecto, futuras sesiones y agentes.

- Utilizar explícitamente esta raíz para comandos, ediciones y ejecuciones de pruebas.
- `C:\Users\Cuevita\Documents\GIT\PruebaTecnica` es una copia anterior. No desarrollar ni ejecutar pruebas allí.
- Conservar todos los cambios actuales del proyecto.
- Conservar los usuarios y datos parametrizados existentes.
- No sobrescribir configuraciones sin revisar su contenido previamente.

## 2. Arquitectura y organización

- Respetar la arquitectura actual del framework.
- Mantener los escenarios API en features y clases independientes de los escenarios web.
- Reutilizar componentes existentes antes de crear nuevos.
- No modificar funcionalidades ajenas a la solicitud.
- Evitar dependencias innecesarias y código duplicado.

## 3. Escenarios API existentes

El archivo `flujo-nomina-api.feature` contiene los escenarios CP01 y CP02 por API.

**Validación existente:**
- Validado con Polet el 3 de noviembre de 2026.
- Referencia: PC01039.
- Resultado: 150/150 Conciliado.
- Conservar los mismos usuarios y ejemplos del feature web.

No modificar estos escenarios ni sus datos sin una solicitud explícita.

## 4. Pensar antes de programar

Antes de implementar cambios:

- Analizar el requerimiento y el código existente.
- Identificar suposiciones y dependencias.
- Si existen varias soluciones, priorizar la más sencilla y mantenible.
- Señalar las ambigüedades que puedan afectar el resultado.
- No inventar métodos, selectores, rutas ni funcionalidades.
- Para cambios complejos, presentar un plan breve de implementación y validación.

## 5. Simplicidad y calidad del código

Escribir únicamente el código necesario para resolver el problema.

- No agregar funcionalidades no solicitadas.
- Evitar abstracciones innecesarias.
- No crear métodos o clases adicionales cuando ya existe una solución reutilizable.
- Mantener la organización y las convenciones actuales.
- Priorizar legibilidad, mantenibilidad y reutilización.
- No realizar refactorizaciones ajenas al requerimiento.

## 6. Modificaciones precisas

Al modificar archivos existentes:

- Cambiar únicamente lo necesario.
- No alterar código funcional sin justificación.
- Conservar los datos de prueba y usuarios parametrizados.
- No modificar formatos ni comentarios que no estén relacionados con la tarea.
- Eliminar únicamente imports, variables o funciones que queden sin utilizar como consecuencia directa del cambio.
- Informar sobre problemas adicionales sin corregirlos automáticamente si están fuera del alcance.

## 7. Ejecución y validación

Toda corrección debe tener un criterio de aceptación verificable.

- Identificar la causa del fallo antes de aplicar cambios.
- Ejecutar el escenario afectado cuando sea posible.
- Verificar los resultados y revisar los errores de ejecución.
- No cambiar aserciones ni resultados esperados únicamente para obtener un PASS.
- No ocultar errores funcionales mediante esperas artificiales o validaciones débiles.
- Si la prueba continúa fallando, documentar la causa y los cambios realizados.
- No afirmar que una prueba pasó si no fue ejecutada

## Uso de Context7 MCP

- Utilizar Context7 MCP cuando se requiera consultar
  documentación técnica de librerías o frameworks.
- Consultar Context7 antes de implementar APIs desconocidas,
  métodos nuevos o cambios entre versiones.
- Priorizar documentación compatible con las versiones
  instaladas en el proyecto.
- No consultar Context7 para cambios sencillos que
  no requieran documentación externa.
- Si existe una inconsistencia documental, verificar
  mediante una prueba controlada antes de modificar código.
- No inventar métodos, parámetros ni funcionalidades.