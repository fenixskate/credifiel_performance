# language: es
@web @flujoNomina
Característica: Carga de pagos y conciliación de nómina por usuario
  Cada fila completa CP01 antes de comenzar CP02 en el mismo navegador.
  Si un paso falla, se omiten los pasos restantes de esa fila.

  Esquema del escenario: CP01 seguido de CP02 para <usuario> / <dependencias>

    Dado que el usuario está en la página de login
    Y El usuario inicia sesion con "<usuario>"

    Cuando el usuario selecciona el menú de pagos con fecha "<fecha>" y periodo "<periodo>" y dependencia "<dependencias>" y pagaduría "<pagaduria>"
    Y el usuario sube el archivo "<archivos>"
    Y el usuario lee el archivo
    Y el usuario procesa el archivo
    Y el usuario registra los pagos

    Entonces el estado del archivo es Terminado

    Cuando el usuario vuelve al inicio con su sesion de "<usuario>"

    Cuando el usuario ingresa al módulo Cuentas
    Y selecciona la cuenta "<archivoCuentas>"
    Entonces se muestra el detalle de la cuenta "<archivoCuentas>"

    Cuando el usuario "<usuario>" importa el layout bancario INVEX
    Y el usuario carga las transacciones
    Entonces las transacciones muestran el estado Cargado

    Cuando el usuario importa las transacciones
    Entonces las transacciones muestran el estado Procesando
    Y las transacciones muestran el estado Sin identificar

    Cuando el usuario ingresa a General Operaciones
    Y el usuario abre las transacciones
    Y el usuario agrega un filtro personalizado
    Y el usuario filtra por la referencia capturada
    Y el usuario aplica el filtro
    Y selecciona la transacción encontrada
    Y transfiere las transacciones seleccionadas a la cuenta "<cuenta>"

    Cuando ingresa a la cuenta destino "<cuenta>"
    Y asigna el periodo operativo "<periodo>"
    Y agrega la línea del periodo

    Y el usuario confirma la asignación del periodo
    Y el usuario asigna la pagaduría "<pagaduria>"
    Y agrega la línea de pagaduría

    Y el usuario confirma la pagaduría
    Y el usuario abre la conciliación por periodo operativo
    Y el usuario configura la conciliación con periodo "<periodo>" y pagaduría "<pagaduria>"

    Y el usuario ejecuta la conciliación
    Y el usuario abre los procesos de conciliación
    Entonces la conciliación alcanza el 100 por ciento

    Ejemplos:
      | usuario                           | fecha      | periodo                     | dependencias                           | pagaduria                                       | archivos                                             | cuenta                                           | archivoCuentas       |
#      | polet.valdes@credifiel.com.mx     | 28/10/2026 | Periodo 12/2026 - Quincenal | CECYTE CHIAPAS                         | ESTATAL, CECYTE CHIAPAS                         | CECYTE_CHIAPAS__ESTATAL.xlsx                         | Nómina \| CECYTE CHIAPAS                         | Nómina \| Invex 2595 |
#      | fernanda.jimenez@credifiel.com.mx | 28/10/2026 | Periodo 11/2026 - Quincenal | CECYTE NAYARIT                         | ESTATAL, CECYTE NAYARIT                         | CECYTE_NAYARIT__ESTATAL.xlsx                         | Nómina \| CECYTE NAYARIT                         | Nómina \| Invex 2595 |
#      | josafat.urbina@credifiel.com.mx   | 28/10/2026 | Periodo 10/2026 - Quincenal | CECYTE OAXACA                          | ESTATAL, CECYTE OAXACA                          | CECYTE_OAXACA__ESTATAL.xlsx                          | Nómina \| CECYTE OAXACA                          | Nómina \| Invex 2595 |
#      | rita.baltazar@credifiel.com.mx    | 28/10/2026 | Periodo 09/2026 - Quincenal | COBACH BAJA CALIFORNIA SUR             | ESTATAL, COBACH BAJA CALIFORNIA SUR             | COBACH_BAJA_CALIFORNIA_SUR__ESTATAL.xlsx             | Nómina \| COBACH BAJA CALIFORNIA SUR             | Nómina \| Invex 2595 |
#      | santiago.moreno@credifiel.com.mx  | 28/10/2026 | Periodo 08/2026 - Quincenal | COBACH BAJA CALIFORNIA                 | ESTATAL, COBACH BAJA CALIFORNIA                 | COBACH_BAJA_CALIFORNIA__ESTATAL.xlsx                 | Nómina \| COBACH BAJA CALIFORNIA                 | Nómina \| Invex 2595 |
#      | eduardo.martinez@credifiel.com.mx | 28/10/2026 | Periodo 07/2026 - Quincenal | COBACH CHIAPAS                         | ESTATAL, COBACH CHIAPAS                         | COBACH_CHIAPAS__ESTATAL.xlsx                         | Nómina \| COBACH CHIAPAS                         | Nómina \| Invex 2595 |
#
#      | polet.valdes@credifiel.com.mx     | 28/10/2026 | Periodo 12/2026 - Quincenal | COBAO OAXACA                           | ESTATAL, COBAO OAXACA                           | COBAO_OAXACA__ESTATAL.xlsx                           | Nómina \| COBAO OAXACA                           | Nómina \| Invex 2595 |
#      | fernanda.jimenez@credifiel.com.mx | 28/10/2026 | Periodo 11/2026 - Quincenal | COLEGIO DE BACHILLERES DE QUINTANA ROO | ESTATAL, COLEGIO DE BACHILLERES DE QUINTANA ROO | COLEGIO_DE_BACHILLERES_DE_QUINTANA_ROO__ESTATAL.xlsx | Nómina \| COLEGIO DE BACHILLERES DE QUINTANA ROO | Nómina \| Invex 2595 |
#      | josafat.urbina@credifiel.com.mx   | 28/10/2026 | Periodo 10/2026 - Quincenal | COMISION DE AGUA DEL ESTADO DE MEXICO  | ESTATAL, COMISION DE AGUA DEL ESTADO DE MEXICO  | COMISION_DE_AGUA_DEL_ESTADO_DE_MEXICO__ESTATAL.xlsx  | Nómina \| COMISION DE AGUA DEL ESTADO DE MEXICO  | Nómina \| Invex 2595 |
#      | rita.baltazar@credifiel.com.mx    | 28/10/2026 | Periodo 09/2026 - Quincenal | COMISION DE AGUA DE VERACRUZ           | CENTRAL, COMISION DE AGUA DE VERACRUZ           | COMISION_DE_AGUA_DE_VERACRUZ__CENTRAL.xlsx           | Nómina \| COMISION DE AGUA DE VERACRUZ           | Nómina \| Invex 2595 |
#      | santiago.moreno@credifiel.com.mx  | 28/10/2026 | Periodo 08/2026 - Quincenal | COMISION DE AGUA DE VERACRUZ           | ESTATAL, COMISION DE AGUA DE VERACRUZ           | COMISION_DE_AGUA_DE_VERACRUZ__ESTATAL.xlsx           | Nómina \| COMISION DE AGUA DE VERACRUZ           | Nómina \| Invex 2595 |
#      #| eduardo.martinez@credifiel.com.mx  | 28/10/2026 | Periodo 07/2026 - Quincenal | CONAGUA HIDALGO | ESTATAL, CONAGUA HIDALGO | CONAGUA_HIDALGO__ESTATAL.xlsx | Nómina \| CONAGUA HIDALGO | Nómina \| Invex 2595 |
#
#      | polet.valdes@credifiel.com.mx     | 28/10/2026 | Periodo 12/2026 - Quincenal | CONALEP QUINTANA ROO                   | ESTATAL, CONALEP QUINTANA ROO                   | CONALEP_QUINTANA_ROO__ESTATAL.xlsx                   | Nómina \| CONALEP QUINTANA ROO                   | Nómina \| Invex 2595 |
#      | fernanda.jimenez@credifiel.com.mx | 28/10/2026 | Periodo 11/2026 - Quincenal | CONGRESO DE OAXACA                     | ESTATAL, CONGRESO DE OAXACA                     | CONGRESO_DE_OAXACA__ESTATAL.xlsx                     | Nómina \| CONGRESO DE OAXACA                     | Nómina \| Invex 2595 |
#      | josafat.urbina@credifiel.com.mx   | 28/10/2026 | Periodo 10/2026 - Quincenal | DIF QUINTANA ROO                       | ESTATAL, DIF QUINTANA ROO                       | DIF_QUINTANA_ROO__ESTATAL.xlsx                       | Nómina \| DIF QUINTANA ROO                       | Nómina \| Invex 2595 |
#      | rita.baltazar@credifiel.com.mx    | 28/10/2026 | Periodo 09/2026 - Quincenal | FISCALIA DE AGUASCALIENTES             | ESTATAL, FISCALIA DE AGUASCALIENTES             | FISCALIA_DE_AGUASCALIENTES__ESTATAL.xlsx             | Nómina \| FISCALIA DE AGUASCALIENTES             | Nómina \| Invex 2595 |
#      | santiago.moreno@credifiel.com.mx  | 28/10/2026 | Periodo 08/2026 - Quincenal | FISCALIA DE BAJA CALIFORNIA            | ESTATAL, FISCALIA DE BAJA CALIFORNIA            | FISCALIA_DE_BAJA_CALIFORNIA__ESTATAL.xlsx            | Nómina \| FISCALIA DE BAJA CALIFORNIA            | Nómina \| Invex 2595 |
#      | eduardo.martinez@credifiel.com.mx | 28/10/2026 | Periodo 07/2026 - Quincenal | FISCALIA DE NAYARIT                    | ESTATAL, FISCALIA DE NAYARIT                    | FISCALIA_DE_NAYARIT__ESTATAL.xlsx                    | Nómina \| FISCALIA DE NAYARIT                    | Nómina \| Invex 2595 |
#
#      | polet.valdes@credifiel.com.mx     | 28/10/2026 | Periodo 12/2026 - Quincenal | FISCALIA DE QUINTANA ROO               | ESTATAL, FISCALIA DE QUINTANA ROO               | FISCALIA_DE_QUINTANA_ROO__ESTATAL.xlsx               | Nómina \| FISCALIA DE QUINTANA ROO               | Nómina \| Invex 2595 |
#      | fernanda.jimenez@credifiel.com.mx | 28/10/2026 | Periodo 11/2026 - Quincenal | FISCALIA DE TAMAULIPAS                 | ESTATAL, FISCALIA DE TAMAULIPAS                 | FISCALIA_DE_TAMAULIPAS__ESTATAL.xlsx                 | Nómina \| FISCALIA DE TAMAULIPAS                 | Nómina \| Invex 2595 |
#      | josafat.urbina@credifiel.com.mx   | 28/10/2026 | Periodo 10/2026 - Quincenal | FISCALIA ESPECIALIZADA DE QUINTANA ROO | ESTATAL, FISCALIA ESPECIALIZADA DE QUINTANA ROO | FISCALIA_ESPECIALIZADA_DE_QUINTANA_ROO__ESTATAL.xlsx | Nómina \| FISCALIA ESPECIALIZADA DE QUINTANA ROO | Nómina \| Invex 2595 |
#      | rita.baltazar@credifiel.com.mx    | 28/10/2026 | Periodo 09/2026 - Quincenal | FONATUR                                | CENTRAL, FONATUR                                | FONATUR__CENTRAL.xlsx                                | Nómina \| FONATUR                                | Nómina \| Invex 2595 |
#      | santiago.moreno@credifiel.com.mx  | 28/10/2026 | Periodo 08/2026 - Quincenal | FONATUR                                | INFRAESTRUCTURA, FONATUR                        | FONATUR__INFRAESTRUCTURA.xlsx                        | Nómina \| FONATUR                                | Nómina \| Invex 2595 |
#      | eduardo.martinez@credifiel.com.mx | 28/10/2026 | Periodo 07/2026 - Quincenal | FONATUR                                | TREN MAYA, FONATUR                              | FONATUR__TREN_MAYA.xlsx                              | Nómina \| FONATUR                                | Nómina \| Invex 2595 |
#
#      | polet.valdes@credifiel.com.mx     | 28/10/2026 | Periodo 12/2026 - Quincenal | GOBIERNO DEL ESTADO DE MEXICO          | ESTATAL, GOBIERNO DEL ESTADO DE MEXICO          | GOBIERNO_DEL_ESTADO_DE_MEXICO__ESTATAL.xlsx          | Nómina \| GOBIERNO DEL ESTADO DE MEXICO          | Nómina \| Invex 2595 |
#      | fernanda.jimenez@credifiel.com.mx | 28/10/2026 | Periodo 11/2026 - Quincenal | GOBIERNO DE CHIAPAS                    | CLAVE 76, GOBIERNO DE CHIAPAS                   | GOBIERNO_DE_CHIAPAS__CLAVE_76.xlsx                   | Nómina \| GOBIERNO DE CHIAPAS                    | Nómina \| Invex 2595 |
#      | josafat.urbina@credifiel.com.mx   | 28/10/2026 | Periodo 10/2026 - Quincenal | GOBIERNO DE CHIAPAS                    | CLAVE 83, GOBIERNO DE CHIAPAS                   | GOBIERNO_DE_CHIAPAS__CLAVE_83.xlsx                   | Nómina \| GOBIERNO DE CHIAPAS                    | Nómina \| Invex 2595 |
#      | rita.baltazar@credifiel.com.mx    | 28/10/2026 | Periodo 09/2026 - Quincenal | GOBIERNO DE JALISCO                    | ESTATAL, GOBIERNO DE JALISCO                    | GOBIERNO_DE_JALISCO__ESTATAL.xlsx                    | Nómina \| GOBIERNO DE JALISCO                    | Nómina \| Invex 2595 |
#      | santiago.moreno@credifiel.com.mx  | 28/10/2026 | Periodo 08/2026 - Quincenal | GOBIERNO DE LA CIUDAD DE MEXICO        | ESTATAL, GOBIERNO DE LA CIUDAD DE MEXICO        | GOBIERNO_DE_LA_CIUDAD_DE_MEXICO__ESTATAL.xlsx        | Nómina \| GOBIERNO DE LA CIUDAD DE MEXICO        | Nómina \| Invex 2595 |
#      | eduardo.martinez@credifiel.com.mx | 28/10/2026 | Periodo 07/2026 - Quincenal | GOBIERNO DE QUINTANA ROO               | ESTATAL, GOBIERNO DE QUINTANA ROO               | GOBIERNO_DE_QUINTANA_ROO__ESTATAL.xlsx               | Nómina \| GOBIERNO DE QUINTANA ROO               | Nómina \| Invex 2595 |
#
#     #| polet.valdes@credifiel.com.mx       | 28/10/2026 | Periodo 12/2026 - Quincenal | GOBIERNO DE SINALOA | ESTATAL, GOBIERNO DE SINALOA | GOBIERNO_DE_SINALOA__ESTATAL.xlsx | Nómina \| GOBIERNO DE SINALOA | Nómina \| Invex 2595 |
#      | fernanda.jimenez@credifiel.com.mx | 28/10/2026 | Periodo 11/2026 - Quincenal | GOBIERNO DE TAMAULIPAS                 | ESTATAL, GOBIERNO DE TAMAULIPAS                 | GOBIERNO_DE_TAMAULIPAS__ESTATAL.xlsx                 | Nómina \| GOBIERNO DE TAMAULIPAS                 | Nómina \| Invex 2595 |
#      | josafat.urbina@credifiel.com.mx   | 28/10/2026 | Periodo 10/2026 - Quincenal | IMSS BIENESTAR                         | FEDERAL, IMSS BIENESTAR                         | IMSS_BIENESTAR__FEDERAL.xlsx                         | Nómina \| IMSS BIENESTAR                         | Nómina \| Invex 2595 |
#      | rita.baltazar@credifiel.com.mx    | 28/10/2026 | Periodo 09/2026 - Quincenal | INSTITUTO DE PENSIONES DE JALISCO      | ESTATAL, INSTITUTO DE PENSIONES DE JALISCO      | INSTITUTO_DE_PENSIONES_DE_JALISCO__ESTATAL.xlsx      | Nómina \| INSTITUTO DE PENSIONES DE JALISCO      | Nómina \| Invex 2595 |
#      | santiago.moreno@credifiel.com.mx  | 28/10/2026 | Periodo 08/2026 - Quincenal | INSTITUTO DE PENSIONES DE JALISCO      | JUBILADO, INSTITUTO DE PENSIONES DE JALISCO     | INSTITUTO_DE_PENSIONES_DE_JALISCO__JUBILADO.xlsx     | Nómina \| INSTITUTO DE PENSIONES DE JALISCO      | Nómina \| Invex 2595 |
#      | eduardo.martinez@credifiel.com.mx | 28/10/2026 | Periodo 07/2026 - Quincenal | IPAX VERACRUZ                          | ESTATAL, IPAX VERACRUZ                          | IPAX_VERACRUZ__ESTATAL.xlsx                          | Nómina \| IPAX VERACRUZ                          | Nómina \| Invex 2595 |
#
#      | polet.valdes@credifiel.com.mx     | 28/10/2026 | Periodo 12/2026 - Quincenal | ISSSTECALI BAJA CALIFORNIA             | ESTATAL, ISSSTECALI BAJA CALIFORNIA             | ISSSTECALI_BAJA_CALIFORNIA__ESTATAL.xlsx             | Nómina \| ISSSTECALI BAJA CALIFORNIA             | Nómina \| Invex 2595 |
#      | fernanda.jimenez@credifiel.com.mx | 28/10/2026 | Periodo 11/2026 - Quincenal | JUDICATURA DE CHIAPAS                  | ESTATAL, JUDICATURA DE CHIAPAS                  | JUDICATURA_DE_CHIAPAS__ESTATAL.xlsx                  | Nómina \| JUDICATURA DE CHIAPAS                  | Nómina \| Invex 2595 |
#      | josafat.urbina@credifiel.com.mx   | 28/10/2026 | Periodo 10/2026 - Quincenal | MUNICIPIO DE ATIZAPAN                  | ESTATAL, MUNICIPIO DE ATIZAPAN                  | MUNICIPIO_DE_ATIZAPAN__ESTATAL.xlsx                  | Nómina \| MUNICIPIO DE ATIZAPAN                  | Nómina \| Invex 2595 |
#      | rita.baltazar@credifiel.com.mx    | 28/10/2026 | Periodo 09/2026 - Quincenal | MUNICIPIO DE NAUCALPAN                 | ESTATAL, MUNICIPIO DE NAUCALPAN                 | MUNICIPIO_DE_NAUCALPAN__ESTATAL.xlsx                 | Nómina \| MUNICIPIO DE NAUCALPAN                 | Nómina \| Invex 2595 |
#      | santiago.moreno@credifiel.com.mx  | 28/10/2026 | Periodo 08/2026 - Quincenal | PEMEX                                  | JUBILADO, PEMEX                                 | PEMEX__JUBILADO.xlsx                                 | Nómina \| PEMEX                                  | Nómina \| Invex 2595 |
#      | eduardo.martinez@credifiel.com.mx | 28/10/2026 | Periodo 07/2026 - Quincenal | PENSIONES DE OAXACA                    | ESTATAL, PENSIONES DE OAXACA                    | PENSIONES_DE_OAXACA__ESTATAL.xlsx                    | Nómina \| PENSIONES DE OAXACA                    | Nómina \| Invex 2595 |
#
#      | polet.valdes@credifiel.com.mx     | 28/10/2026 | Periodo 12/2026 - Quincenal | PODER JUDICIAL DE GUERRERO             | ESTATAL, PODER JUDICIAL DE GUERRERO             | PODER_JUDICIAL_DE_GUERRERO__ESTATAL.xlsx             | Nómina \| PODER JUDICIAL DE GUERRERO             | Nómina \| Invex 2595 |
#      | fernanda.jimenez@credifiel.com.mx | 28/10/2026 | Periodo 11/2026 - Quincenal | SAGARPA                                | FEDERAL, SAGARPA                                | SAGARPA__FEDERAL.xlsx                                | Nómina \| SAGARPA                                | Nómina \| Invex 2595 |
#      | josafat.urbina@credifiel.com.mx   | 28/10/2026 | Periodo 10/2026 - Quincenal | SALUD AGUASCALIENTES                   | ESTATAL, SALUD AGUASCALIENTES                   | SALUD_AGUASCALIENTES__ESTATAL.xlsx                   | Nómina \| SALUD AGUASCALIENTES                   | Nómina \| Invex 2595 |
#      | rita.baltazar@credifiel.com.mx    | 28/10/2026 | Periodo 09/2026 - Quincenal | SALUD CAMPECHE                         | FEDERAL, SALUD CAMPECHE                         | SALUD_CAMPECHE__FEDERAL.xlsx                         | Nómina \| SALUD CAMPECHE                         | Nómina \| Invex 2595 |
#      | santiago.moreno@credifiel.com.mx  | 28/10/2026 | Periodo 08/2026 - Quincenal | SALUD CAMPECHE                         | FORMALIZADO, SALUD CAMPECHE                     | SALUD_CAMPECHE__FORMALIZADO.xlsx                     | Nómina \| SALUD CAMPECHE                         | Nómina \| Invex 2595 |
#      | eduardo.martinez@credifiel.com.mx | 28/10/2026 | Periodo 07/2026 - Quincenal | SALUD CAMPECHE                         | REGULARIZADO, SALUD CAMPECHE                    | SALUD_CAMPECHE__REGULARIZADO.xlsx                    | Nómina \| SALUD CAMPECHE                         | Nómina \| Invex 2595 |
#
#      | polet.valdes@credifiel.com.mx     | 28/10/2026 | Periodo 12/2026 - Quincenal | SALUD CHIAPAS                          | ESTATAL, SALUD CHIAPAS                          | SALUD_CHIAPAS__ESTATAL.xlsx                          | Nómina \| SALUD CHIAPAS                          | Nómina \| Invex 2595 |
#      | fernanda.jimenez@credifiel.com.mx | 28/10/2026 | Periodo 11/2026 - Quincenal | SALUD CHIHUAHUA                        | ESTATAL, SALUD CHIHUAHUA                        | SALUD_CHIHUAHUA__ESTATAL.xlsx                        | Nómina \| SALUD CHIHUAHUA                        | Nómina \| Invex 2595 |
#      | josafat.urbina@credifiel.com.mx   | 28/10/2026 | Periodo 10/2026 - Quincenal | SALUD DE LA CIUDAD DE MEXICO           | ESTATAL, SALUD DE LA CIUDAD DE MEXICO           | SALUD_DE_LA_CIUDAD_DE_MEXICO__ESTATAL.xlsx           | Nómina \| SALUD DE LA CIUDAD DE MEXICO           | Nómina \| Invex 2595 |
#      | rita.baltazar@credifiel.com.mx    | 28/10/2026 | Periodo 09/2026 - Quincenal | SALUD DURANGO                          | ESTATAL, SALUD DURANGO                          | SALUD_DURANGO__ESTATAL.xlsx                          | Nómina \| SALUD DURANGO                          | Nómina \| Invex 2595 |
#      | santiago.moreno@credifiel.com.mx  | 28/10/2026 | Periodo 08/2026 - Quincenal | SALUD NAYARIT                          | ESTATAL, SALUD NAYARIT                          | SALUD_NAYARIT__ESTATAL.xlsx                          | Nómina \| SALUD NAYARIT                          | Nómina \| Invex 2595 |
#
#     #| eduardo.martinez@credifiel.com.mx  | 28/10/2026 | Periodo 07/2026 - Quincenal | SALUD OAXACA | ESTATAL, SALUD OAXACA | SALUD_OAXACA__ESTATAL.xlsx | Nómina \| SALUD OAXACA | Nómina \| Invex 2595 |
#      | polet.valdes@credifiel.com.mx     | 28/10/2026 | Periodo 12/2026 - Quincenal | SALUD PUEBLA                           | ESTATAL, SALUD PUEBLA                           | SALUD_PUEBLA__ESTATAL.xlsx                           | Nómina \| SALUD PUEBLA                           | Nómina \| Invex 2595 |
#      | fernanda.jimenez@credifiel.com.mx | 28/10/2026 | Periodo 11/2026 - Quincenal | SALUD QUINTANA ROO                     | ESTATAL, SALUD QUINTANA ROO                     | SALUD_QUINTANA_ROO__ESTATAL.xlsx                     | Nómina \| SALUD QUINTANA ROO                     | Nómina \| Invex 2595 |
#      | josafat.urbina@credifiel.com.mx   | 28/10/2026 | Periodo 10/2026 - Quincenal | SALUD TABASCO                          | ESTATAL, SALUD TABASCO                          | SALUD_TABASCO__ESTATAL.xlsx                          | Nómina \| SALUD TABASCO                          | Nómina \| Invex 2595 |
#      | rita.baltazar@credifiel.com.mx    | 28/10/2026 | Periodo 09/2026 - Quincenal | SALUD TAMAULIPAS                       | ESTATAL, SALUD TAMAULIPAS                       | SALUD_TAMAULIPAS__ESTATAL.xlsx                       | Nómina \| SALUD TAMAULIPAS                       | Nómina \| Invex 2595 |
#      | santiago.moreno@credifiel.com.mx  | 28/10/2026 | Periodo 08/2026 - Quincenal | SALUD TAMAULIPAS                       | FEDERAL, SALUD TAMAULIPAS                       | SALUD_TAMAULIPAS__FEDERAL.xlsx                       | Nómina \| SALUD TAMAULIPAS                       | Nómina \| Invex 2595 |
#
#      | eduardo.martinez@credifiel.com.mx | 28/10/2026 | Periodo 07/2026 - Quincenal | SALUD TLAXCALA                         | ESTATAL, SALUD TLAXCALA                         | SALUD_TLAXCALA__ESTATAL.xlsx                         | Nómina \| SALUD TLAXCALA                         | Nómina \| Invex 2595 |
#      | polet.valdes@credifiel.com.mx     | 28/10/2026 | Periodo 12/2026 - Quincenal | SAPASA DE ATIZAPAN DE ZARAGOZA         | ESTATAL, SAPASA DE ATIZAPAN DE ZARAGOZA         | SAPASA_DE_ATIZAPAN_DE_ZARAGOZA__ESTATAL.xlsx         | Nómina \| SAPASA DE ATIZAPAN DE ZARAGOZA         | Nómina \| Invex 2595 |
#      | fernanda.jimenez@credifiel.com.mx | 28/10/2026 | Periodo 11/2026 - Quincenal | SECTUR                                 | ESTATAL, SECTUR                                 | SECTUR__ESTATAL.xlsx                                 | Nómina \| SECTUR                                 | Nómina \| Invex 2595 |
#      | josafat.urbina@credifiel.com.mx   | 28/10/2026 | Periodo 10/2026 - Quincenal | SEDATU                                 | FEDERAL, SEDATU                                 | SEDATU__FEDERAL.xlsx                                 | Nómina \| SEDATU                                 | Nómina \| Invex 2595 |
#      | rita.baltazar@credifiel.com.mx    | 28/10/2026 | Periodo 09/2026 - Quincenal | SEPOMEX                                | FEDERAL, SEPOMEX                                | SEPOMEX__FEDERAL.xlsx                                | Nómina \| SEPOMEX                                | Nómina \| Invex 2595 |
#      | santiago.moreno@credifiel.com.mx  | 28/10/2026 | Periodo 08/2026 - Quincenal | SEP AFSEDF                             | FEDERAL, SEP AFSEDF                             | SEP_AFSEDF__FEDERAL.xlsx                             | Nómina \| SEP AFSEDF                             | Nómina \| Invex 2595 |
#
#      | eduardo.martinez@credifiel.com.mx | 28/10/2026 | Periodo 07/2026 - Quincenal | SEP CENTRAL                            | ESTATAL, SEP CENTRAL                            | SEP_CENTRAL__ESTATAL.xlsx                            | Nómina \| SEP CENTRAL                            | Nómina \| Invex 2595 |
#      | polet.valdes@credifiel.com.mx     | 28/10/2026 | Periodo 12/2026 - Quincenal | SEP SEMS                               | ESTATAL, SEP SEMS                               | SEP_SEMS__ESTATAL.xlsx                               | Nómina \| SEP SEMS                               | Nómina \| Invex 2595 |
#      #| fernanda.jimenez@credifiel.com.mx  | 28/10/2026 | Periodo 11/2026 - Quincenal | SEP SEMS | JUBILADO, SEP SEMS | SEP_SEMS__JUBILADO.xlsx | Nómina \| SEP SEMS | Nómina \| Invex 2595 |
#      | josafat.urbina@credifiel.com.mx   | 28/10/2026 | Periodo 10/2026 - Quincenal | SNTE 17 ESTADO DE MEXICO               | FEDERAL, SNTE 17 ESTADO DE MEXICO               | SNTE_17_ESTADO_DE_MEXICO__FEDERAL.xlsx               | Nómina \| SNTE 17 ESTADO DE MEXICO               | Nómina \| Invex 2595 |
#      | rita.baltazar@credifiel.com.mx    | 28/10/2026 | Periodo 09/2026 - Quincenal | SNTE 26 SAN LUIS POTOSI                | FEDERAL, SNTE 26 SAN LUIS POTOSI                | SNTE_26_SAN_LUIS_POTOSI__FEDERAL.xlsx                | Nómina \| SNTE 26 SAN LUIS POTOSI                | Nómina \| Invex 2595 |
#      | santiago.moreno@credifiel.com.mx  | 28/10/2026 | Periodo 08/2026 - Quincenal | SNTE 30 TAMAULIPAS                     | ESTATAL, SNTE 30 TAMAULIPAS                     | SNTE_30_TAMAULIPAS__ESTATAL.xlsx                     | Nómina \| SNTE 30 TAMAULIPAS                     | Nómina \| Invex 2595 |
#      | eduardo.martinez@credifiel.com.mx | 28/10/2026 | Periodo 07/2026 - Quincenal | SNTE 30 TAMAULIPAS                     | FEDERAL, SNTE 30 TAMAULIPAS                     | SNTE_30_TAMAULIPAS__FEDERAL.xlsx                     | Nómina \| SNTE 30 TAMAULIPAS                     | Nómina \| Invex 2595 |
#      | polet.valdes@credifiel.com.mx     | 28/10/2026 | Periodo 12/2026 - Quincenal | SNTE 32 VERACRUZ                       | FEDERAL, SNTE 32 VERACRUZ                       | SNTE_32_VERACRUZ__FEDERAL.xlsx                       | Nómina \| SNTE 32 VERACRUZ                       | Nómina \| Invex 2595 |
#      | fernanda.jimenez@credifiel.com.mx | 28/10/2026 | Periodo 11/2026 - Quincenal | SNTE 34 ZACATECAS                      | ESTATAL, SNTE 34 ZACATECAS                      | SNTE_34_ZACATECAS__ESTATAL.xlsx                      | Nómina \| SNTE 34 ZACATECAS                      | Nómina \| Invex 2595 |
#      | josafat.urbina@credifiel.com.mx   | 28/10/2026 | Periodo 10/2026 - Quincenal | SNTE 34 ZACATECAS                      | FEDERAL, SNTE 34 ZACATECAS                      | SNTE_34_ZACATECAS__FEDERAL.xlsx                      | Nómina \| SNTE 34 ZACATECAS                      | Nómina \| Invex 2595 |
#      | rita.baltazar@credifiel.com.mx    | 28/10/2026 | Periodo 09/2026 - Quincenal | SNTE 37 BAJA CALIFORNIA                | ESTATAL, SNTE 37 BAJA CALIFORNIA                | SNTE_37_BAJA_CALIFORNIA__ESTATAL.xlsx                | Nómina \| SNTE 37 BAJA CALIFORNIA                | Nómina \| Invex 2595 |
#      | santiago.moreno@credifiel.com.mx  | 28/10/2026 | Periodo 08/2026 - Quincenal | SNTE 3 BAJA CALIFORNIA SUR             | FEDERAL, SNTE 3 BAJA CALIFORNIA SUR             | SNTE_3_BAJA_CALIFORNIA_SUR__FEDERAL.xlsx             | Nómina \| SNTE 3 BAJA CALIFORNIA SUR             | Nómina \| Invex 2595 |
#      | eduardo.martinez@credifiel.com.mx | 28/10/2026 | Periodo 07/2026 - Quincenal | SNTE 44 DURANGO                        | ESTATAL, SNTE 44 DURANGO                        | SNTE_44_DURANGO__ESTATAL.xlsx                        | Nómina \| SNTE 44 DURANGO                        | Nómina \| Invex 2595 |
#      | polet.valdes@credifiel.com.mx     | 28/10/2026 | Periodo 12/2026 - Quincenal | SNTE 47 JALISCO                        | ESTATAL, SNTE 47 JALISCO                        | SNTE_47_JALISCO__ESTATAL.xlsx                        | Nómina \| SNTE 47 JALISCO                        | Nómina \| Invex 2595 |
#      | fernanda.jimenez@credifiel.com.mx | 28/10/2026 | Periodo 11/2026 - Quincenal | SNTE 51 PUEBLA                         | ESTATAL, SNTE 51 PUEBLA                         | SNTE_51_PUEBLA__ESTATAL.xlsx                         | Nómina \| SNTE 51 PUEBLA                         | Nómina \| Invex 2595 |
#      | josafat.urbina@credifiel.com.mx   | 28/10/2026 | Periodo 10/2026 - Quincenal | SNTE 53 SINALOA                        | ESTATAL, SNTE 53 SINALOA                        | SNTE_53_SINALOA__ESTATAL.xlsx                        | Nómina \| SNTE 53 SINALOA                        | Nómina \| Invex 2595 |
#      | rita.baltazar@credifiel.com.mx    | 28/10/2026 | Periodo 09/2026 - Quincenal | SNTE 56 VERACRUZ                       | ESTATAL, SNTE 56 VERACRUZ                       | SNTE_56_VERACRUZ__ESTATAL.xlsx                       | Nómina \| SNTE 56 VERACRUZ                       | Nómina \| Invex 2595 |
#      | santiago.moreno@credifiel.com.mx  | 28/10/2026 | Periodo 08/2026 - Quincenal | SNTE 7 CHIAPAS                         | FEDERAL, SNTE 7 CHIAPAS                         | SNTE_7_CHIAPAS__FEDERAL.xlsx                         | Nómina \| SNTE 7 CHIAPAS                         | Nómina \| Invex 2595 |
#      | eduardo.martinez@credifiel.com.mx | 28/10/2026 | Periodo 07/2026 - Quincenal | SUTESUAEM ESTADO DE MEXICO             | ESTATAL, SUTESUAEM ESTADO DE MEXICO             | SUTESUAEM_ESTADO_DE_MEXICO__ESTATAL.xlsx             | Nómina \| SUTESUAEM ESTADO DE MEXICO             | Nómina \| Invex 2595 |
#      | polet.valdes@credifiel.com.mx     | 28/10/2026 | Periodo 12/2026 - Quincenal | UABJO OAXACA                           | ESTATAL, UABJO OAXACA                           | UABJO_OAXACA__ESTATAL.xlsx                           | Nómina \| UABJO OAXACA                           | Nómina \| Invex 2595 |
#      | fernanda.jimenez@credifiel.com.mx | 28/10/2026 | Periodo 11/2026 - Quincenal | UNACH CHIAPAS                          | ESTATAL, UNACH CHIAPAS                          | UNACH_CHIAPAS__ESTATAL.xlsx                          | Nómina \| UNACH CHIAPAS                          | Nómina \| Invex 2595 |


      | josafat.urbina@credifiel.com.mx   | 28/10/2026 | Periodo 10/2026 - Quincenal | IMSS BIENESTAR                         | FEDERAL, IMSS BIENESTAR                         | IMSS_BIENESTAR__FEDERAL.xlsx                         | Nómina \| IMSS BIENESTAR                         | Nómina \| Invex 2595 |
      | eduardo.martinez@credifiel.com.mx | 28/10/2026 | Periodo 07/2026 - Quincenal | SNTE 1 AGUASCALIENTES                  | ESTATAL, SNTE 1 AGUASCALIENTES                  | SNTE_1_AGUASCALIENTES__ESTATAL.xlsx                  | Nómina \| SNTE 1 AGUASCALIENTES                  | Nómina \| Invex 2595 |
      | polet.valdes@credifiel.com.mx     | 28/10/2026 | Periodo 12/2026 - Quincenal | SNTE 4 CAMPECHE                        | FEDERAL, SNTE 4 CAMPECHE                        | SNTE_4_CAMPECHE__FEDERAL.xlsx                        | Nómina \| SNTE 4 CAMPECHE                        | Nómina \| Invex 2595 |
      | fernanda.jimenez@credifiel.com.mx | 28/10/2026 | Periodo 11/2026 - Quincenal | SNTE 18 MICHOACAN                      | FEDERAL, SNTE 18 MICHOACAN                      | SNTE 18 MICHOACAN CARGA.xlsx                         | Nómina \| SNTE 18 MICHOACAN                      | Nómina \| Invex 2595 |
      | fernanda.jimenez@credifiel.com.mx | 28/10/2026 | Periodo 11/2026 - Quincenal | SNTE 26 SAN LUIS POTOSI                | FEDERAL, SNTE 26 SAN LUIS POTOSI                | SNTE_26_SAN_LUIS_POTOSI__FEDERAL.xlsx                | Nómina \| SNTE 26 SAN LUIS POTOSI                | Nómina \| Invex 2595 |
