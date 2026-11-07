# language: es
@web @flujoExtrajudicial
Característica: Promesas y conciliacion por usuario
  Cada fila completa CP03 antes de comenzar CP04, en el mismo navegador.
  Si un paso falla, Cucumber omite los pasos restantes de esa fila.

  Esquema del escenario: CP03 seguido de CP04 para <usuario> con <promesas>

    Dado que el usuario está en la página de login
    Y El usuario inicia sesion con "<usuario>"
    Y el usuario actualiza los 40 archivos de promesas desde el Universo de Cartera

    Cuando el usuario ingresa a Carga Masiva de Promesas de Pago
    Y el usuario "<usuario>" sube el archivo de promesas "<promesas>"
    Y el usuario lee el archivo de promesas

    Entonces todas las promesas muestran el estado Pendiente

    Cuando el usuario procesa las promesas de pago

    Entonces todas las promesas muestran el estado Exitoso

    Cuando el usuario vuelve al inicio con su sesion de "<usuario>"

    Cuando el usuario ingresa al módulo Cuentas para importar movimientos
    Y selecciona la cuenta de movimientos "Extrajudicial | Azteca 8646"
    Y el usuario abre la importación del layout bancario
    Y el usuario selecciona el banco "BANCO AZTECA"
    Y el usuario "<usuario>" carga el archivo "<bancario>" con fecha incrementada

    Cuando el usuario carga las transacciones AZTECA
    Entonces todos los movimientos muestran el estado Cargado

    Cuando el usuario importa las transacciones AZTECA
    Entonces todos los movimientos muestran el estado Importado

    Cuando el usuario ingresa a Exportar movimientos
    Y el usuario crea una nueva exportación
    Y el usuario establece las fechas del archivo AZTECA generado
    Y el usuario genera la exportación de movimientos
    Y el usuario confirma la exportación

    Entonces se genera el archivo de movimientos conciliados
    Y el usuario descarga el archivo de movimientos conciliados
    Y el usuario agrega los créditos al archivo descargado "<promesas>"
    Cuando el usuario ingresa a Importar movimientos
    Y el usuario crea una nueva importación de movimientos
    Y el usuario adjunta el archivo de movimientos modificado
    Y el usuario carga los movimientos importados
    Y el usuario confirma la carga de movimientos
    Entonces el usuario valida el estado de la importación de movimientos

    Cuando el usuario regresa al módulo Cuentas para conciliar movimientos
    Y selecciona la cuenta de conciliación "General Operaciones"
    Y abre la conciliación de importados extrajudicial
    Y selecciona únicamente el archivo de movimientos importado
    Y confirma la conciliación del archivo

    Entonces el proceso de conciliación alcanza el 100 por ciento

    Ejemplos:
      | usuario                             | promesas                                | bancario       |
      | eduardo.martinez@credifiel.com.mx   | Template de Carga de Promesas v2.xlsx   | AZTECA_QA.xlsx |
      | alma.hernandez@credifiel.com.mx     | Template de Carga de Promesas v2_2.xlsx | AZTECA_QA.xlsx |
      | santiago.moreno@credifiel.com.mx    | Template de Carga de Promesas v2_3.xlsx | AZTECA_QA.xlsx |
      | david.hernandez@credifiel.com.mx    | Template de Carga de Promesas v2_4.xlsx | AZTECA_QA.xlsx |
      | andrea.garcia@credifiel.com.mx      | Template de Carga de Promesas v2_5.xlsx | AZTECA_QA.xlsx |

      | eduardo.martinez@credifiel.com.mx   | Template de Carga de Promesas v2_6.xlsx  | AZTECA_QA.xlsx |
      | alma.hernandez@credifiel.com.mx     | Template de Carga de Promesas v2_7.xlsx  | AZTECA_QA.xlsx |
      | santiago.moreno@credifiel.com.mx    | Template de Carga de Promesas v2_8.xlsx  | AZTECA_QA.xlsx |
      | david.hernandez@credifiel.com.mx    | Template de Carga de Promesas v2_9.xlsx  | AZTECA_QA.xlsx |
      | andrea.garcia@credifiel.com.mx      | Template de Carga de Promesas v2_10.xlsx | AZTECA_QA.xlsx |

      | eduardo.martinez@credifiel.com.mx   | Template de Carga de Promesas v2_11.xlsx | AZTECA_QA.xlsx |
      | alma.hernandez@credifiel.com.mx     | Template de Carga de Promesas v2_12.xlsx | AZTECA_QA.xlsx |
      | santiago.moreno@credifiel.com.mx    | Template de Carga de Promesas v2_13.xlsx | AZTECA_QA.xlsx |
      | david.hernandez@credifiel.com.mx    | Template de Carga de Promesas v2_14.xlsx | AZTECA_QA.xlsx |
      | andrea.garcia@credifiel.com.mx      | Template de Carga de Promesas v2_15.xlsx | AZTECA_QA.xlsx |

      | eduardo.martinez@credifiel.com.mx   | Template de Carga de Promesas v2_16.xlsx | AZTECA_QA.xlsx |
      | alma.hernandez@credifiel.com.mx     | Template de Carga de Promesas v2_17.xlsx | AZTECA_QA.xlsx |
      | santiago.moreno@credifiel.com.mx    | Template de Carga de Promesas v2_18.xlsx | AZTECA_QA.xlsx |
      | david.hernandez@credifiel.com.mx    | Template de Carga de Promesas v2_19.xlsx | AZTECA_QA.xlsx |
      | andrea.garcia@credifiel.com.mx      | Template de Carga de Promesas v2_20.xlsx | AZTECA_QA.xlsx |
