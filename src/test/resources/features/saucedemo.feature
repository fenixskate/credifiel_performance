# language: es
@modulo1 @web
Característica: Flujos de compra en SauceDemo
  Validar el acceso, la navegación y el proceso de compra.
  Los datos de prueba se declaran en cada escenario.
  Las credenciales son públicas y pertenecen al sitio de demostración.

  @web @CP01
  Esquema del escenario: Cargar archivo de respuesta

    Dado que el usuario está en la página de login
    Y El usuario inicia sesion con "<usuario>"

    Cuando el usuario selecciona el menú de pagos con fecha "<fecha>" y periodo "<periodo>" y dependencia "<dependencias>" y pagaduría "<pagaduria>"
    Y el usuario sube el archivo "<archivos>"
    Y el usuario lee el archivo
    Y el usuario procesa el archivo
    Y el usuario registra los pagos

    Entonces el estado del archivo es Terminado

    Ejemplos:
      | usuario                              | fecha      | periodo                     | dependencias                         | pagaduria                                              | archivos                                              |
      | polet.valdes@credifiel.com.mx        | 28/10/2026 | Periodo 12/2026 - Quincenal | CECYTE CHIAPAS                       | ESTATAL, CECYTE CHIAPAS                                | CECYTE_CHIAPAS__ESTATAL.xlsx                           |
      | fernanda.jimenez@credifiel.com.mx    | 28/10/2026 | Periodo 11/2026 - Quincenal | CECYTE NAYARIT                       | ESTATAL, CECYTE NAYARIT                                | CECYTE_NAYARIT__ESTATAL.xlsx                           |
      | josafat.urbina@credifiel.com.mx      | 28/10/2026 | Periodo 10/2026 - Quincenal | CECYTE OAXACA                        | ESTATAL, CECYTE OAXACA                                 | CECYTE_OAXACA__ESTATAL.xlsx                            |
      | rita.baltazar@credifiel.com.mx       | 28/10/2026 | Periodo 09/2026 - Quincenal | COBACH BAJA CALIFORNIA SUR           | ESTATAL, COBACH BAJA CALIFORNIA SUR                    | COBACH_BAJA_CALIFORNIA_SUR__ESTATAL.xlsx               |
      | santiago.moreno@credifiel.com.mx     | 28/10/2026 | Periodo 08/2026 - Quincenal | COBACH BAJA CALIFORNIA               | ESTATAL, COBACH BAJA CALIFORNIA                        | COBACH_BAJA_CALIFORNIA__ESTATAL.xlsx                   |
      | eduardo.martinez@credifiel.com.mx    | 28/10/2026 | Periodo 07/2026 - Quincenal | COBACH CHIAPAS                       | ESTATAL, COBACH CHIAPAS                                | COBACH_CHIAPAS__ESTATAL.xlsx                           |

      | polet.valdes@credifiel.com.mx        | 28/10/2026 | Periodo 12/2026 - Quincenal | COBAO OAXACA                         | ESTATAL, COBAO OAXACA                                  | COBAO_OAXACA__ESTATAL.xlsx                             |
      | fernanda.jimenez@credifiel.com.mx    | 28/10/2026 | Periodo 11/2026 - Quincenal | COLEGIO DE BACHILLERES DE QUINTANA ROO | ESTATAL, COLEGIO DE BACHILLERES DE QUINTANA ROO      | COLEGIO_DE_BACHILLERES_DE_QUINTANA_ROO__ESTATAL.xlsx   |
      | josafat.urbina@credifiel.com.mx      | 28/10/2026 | Periodo 10/2026 - Quincenal | COMISION DE AGUA DEL ESTADO DE MEXICO | ESTATAL, COMISION DE AGUA DEL ESTADO DE MEXICO        | COMISION_DE_AGUA_DEL_ESTADO_DE_MEXICO__ESTATAL.xlsx    |
      | rita.baltazar@credifiel.com.mx       | 28/10/2026 | Periodo 09/2026 - Quincenal | COMISION DE AGUA DE VERACRUZ         | CENTRAL, COMISION DE AGUA DE VERACRUZ                  | COMISION_DE_AGUA_DE_VERACRUZ__CENTRAL.xlsx             |
      | santiago.moreno@credifiel.com.mx     | 28/10/2026 | Periodo 08/2026 - Quincenal | COMISION DE AGUA DE VERACRUZ         | ESTATAL, COMISION DE AGUA DE VERACRUZ                  | COMISION_DE_AGUA_DE_VERACRUZ__ESTATAL.xlsx             |
      #| eduardo.martinez@credifiel.com.mx    | 28/10/2026 | Periodo 07/2026 - Quincenal | CONAGUA HIDALGO                      | ESTATAL, CONAGUA HIDALGO                               | CONAGUA_HIDALGO__ESTATAL.xlsx                          |

      | polet.valdes@credifiel.com.mx        | 28/10/2026 | Periodo 12/2026 - Quincenal | CONALEP QUINTANA ROO                 | ESTATAL, CONALEP QUINTANA ROO                          | CONALEP_QUINTANA_ROO__ESTATAL.xlsx                     |
      | fernanda.jimenez@credifiel.com.mx    | 28/10/2026 | Periodo 11/2026 - Quincenal | CONGRESO DE OAXACA                   | ESTATAL, CONGRESO DE OAXACA                            | CONGRESO_DE_OAXACA__ESTATAL.xlsx                       |
      | josafat.urbina@credifiel.com.mx      | 28/10/2026 | Periodo 10/2026 - Quincenal | DIF QUINTANA ROO                     | ESTATAL, DIF QUINTANA ROO                              | DIF_QUINTANA_ROO__ESTATAL.xlsx                         |
      | rita.baltazar@credifiel.com.mx       | 28/10/2026 | Periodo 09/2026 - Quincenal | FISCALIA DE AGUASCALIENTES           | ESTATAL, FISCALIA DE AGUASCALIENTES                    | FISCALIA_DE_AGUASCALIENTES__ESTATAL.xlsx               |
      | santiago.moreno@credifiel.com.mx     | 28/10/2026 | Periodo 08/2026 - Quincenal | FISCALIA DE BAJA CALIFORNIA          | ESTATAL, FISCALIA DE BAJA CALIFORNIA                   | FISCALIA_DE_BAJA_CALIFORNIA__ESTATAL.xlsx              |
      | eduardo.martinez@credifiel.com.mx    | 28/10/2026 | Periodo 07/2026 - Quincenal | FISCALIA DE NAYARIT                  | ESTATAL, FISCALIA DE NAYARIT                           | FISCALIA_DE_NAYARIT__ESTATAL.xlsx                      |

      | polet.valdes@credifiel.com.mx        | 28/10/2026 | Periodo 12/2026 - Quincenal | FISCALIA DE QUINTANA ROO             | ESTATAL, FISCALIA DE QUINTANA ROO                      | FISCALIA_DE_QUINTANA_ROO__ESTATAL.xlsx                 |
      | fernanda.jimenez@credifiel.com.mx    | 28/10/2026 | Periodo 11/2026 - Quincenal | FISCALIA DE TAMAULIPAS               | ESTATAL, FISCALIA DE TAMAULIPAS                        | FISCALIA_DE_TAMAULIPAS__ESTATAL.xlsx                   |
      | josafat.urbina@credifiel.com.mx      | 28/10/2026 | Periodo 10/2026 - Quincenal | FISCALIA ESPECIALIZADA DE QUINTANA ROO | ESTATAL, FISCALIA ESPECIALIZADA DE QUINTANA ROO       | FISCALIA_ESPECIALIZADA_DE_QUINTANA_ROO__ESTATAL.xlsx   |
      | rita.baltazar@credifiel.com.mx       | 28/10/2026 | Periodo 09/2026 - Quincenal | FONATUR                              | CENTRAL, FONATUR                                      | FONATUR__CENTRAL.xlsx                                  |
      | santiago.moreno@credifiel.com.mx     | 28/10/2026 | Periodo 08/2026 - Quincenal | FONATUR                              | INFRAESTRUCTURA, FONATUR                              | FONATUR__INFRAESTRUCTURA.xlsx                          |
      | eduardo.martinez@credifiel.com.mx    | 28/10/2026 | Periodo 07/2026 - Quincenal | FONATUR                              | TREN MAYA, FONATUR                                    | FONATUR__TREN_MAYA.xlsx                                |

      | polet.valdes@credifiel.com.mx        | 28/10/2026 | Periodo 12/2026 - Quincenal | GOBIERNO DEL ESTADO DE MEXICO        | ESTATAL, GOBIERNO DEL ESTADO DE MEXICO                  | GOBIERNO_DEL_ESTADO_DE_MEXICO__ESTATAL.xlsx            |
      | fernanda.jimenez@credifiel.com.mx    | 28/10/2026 | Periodo 11/2026 - Quincenal | GOBIERNO DE CHIAPAS                  | CLAVE 76, GOBIERNO DE CHIAPAS                          | GOBIERNO_DE_CHIAPAS__CLAVE_76.xlsx                     |
      | josafat.urbina@credifiel.com.mx      | 28/10/2026 | Periodo 10/2026 - Quincenal | GOBIERNO DE CHIAPAS                  | CLAVE 83, GOBIERNO DE CHIAPAS                          | GOBIERNO_DE_CHIAPAS__CLAVE_83.xlsx                     |
      | rita.baltazar@credifiel.com.mx       | 28/10/2026 | Periodo 09/2026 - Quincenal | GOBIERNO DE JALISCO                  | ESTATAL, GOBIERNO DE JALISCO                           | GOBIERNO_DE_JALISCO__ESTATAL.xlsx                      |
      | santiago.moreno@credifiel.com.mx     | 28/10/2026 | Periodo 08/2026 - Quincenal | GOBIERNO DE LA CIUDAD DE MEXICO      | ESTATAL, GOBIERNO DE LA CIUDAD DE MEXICO                | GOBIERNO_DE_LA_CIUDAD_DE_MEXICO__ESTATAL.xlsx          |
      | eduardo.martinez@credifiel.com.mx    | 28/10/2026 | Periodo 07/2026 - Quincenal | GOBIERNO DE QUINTANA ROO             | ESTATAL, GOBIERNO DE QUINTANA ROO                      | GOBIERNO_DE_QUINTANA_ROO__ESTATAL.xlsx                 |

      #| polet.valdes@credifiel.com.mx        | 28/10/2026 | Periodo 12/2026 - Quincenal | GOBIERNO DE SINALOA                  | ESTATAL, GOBIERNO DE SINALOA                           | GOBIERNO_DE_SINALOA__ESTATAL.xlsx                      |
      | fernanda.jimenez@credifiel.com.mx    | 28/10/2026 | Periodo 11/2026 - Quincenal | GOBIERNO DE TAMAULIPAS               | ESTATAL, GOBIERNO DE TAMAULIPAS                        | GOBIERNO_DE_TAMAULIPAS__ESTATAL.xlsx                   |
      | josafat.urbina@credifiel.com.mx      | 28/10/2026 | Periodo 10/2026 - Quincenal | IMSS BIENESTAR                       | FEDERAL, IMSS BIENESTAR                                | IMSS_BIENESTAR__FEDERAL.xlsx                           |
      | rita.baltazar@credifiel.com.mx       | 28/10/2026 | Periodo 09/2026 - Quincenal | INSTITUTO DE PENSIONES DE JALISCO    | ESTATAL, INSTITUTO DE PENSIONES DE JALISCO              | INSTITUTO_DE_PENSIONES_DE_JALISCO__ESTATAL.xlsx        |
      | santiago.moreno@credifiel.com.mx     | 28/10/2026 | Periodo 08/2026 - Quincenal | INSTITUTO DE PENSIONES DE JALISCO    | JUBILADO, INSTITUTO DE PENSIONES DE JALISCO             | INSTITUTO_DE_PENSIONES_DE_JALISCO__JUBILADO.xlsx       |
      | eduardo.martinez@credifiel.com.mx    | 28/10/2026 | Periodo 07/2026 - Quincenal | IPAX VERACRUZ                        | ESTATAL, IPAX VERACRUZ                                 | IPAX_VERACRUZ__ESTATAL.xlsx                            |

      | polet.valdes@credifiel.com.mx        | 28/10/2026 | Periodo 12/2026 - Quincenal | ISSSTECALI BAJA CALIFORNIA           | ESTATAL, ISSSTECALI BAJA CALIFORNIA                    | ISSSTECALI_BAJA_CALIFORNIA__ESTATAL.xlsx               |
      | fernanda.jimenez@credifiel.com.mx    | 28/10/2026 | Periodo 11/2026 - Quincenal | JUDICATURA DE CHIAPAS                | ESTATAL, JUDICATURA DE CHIAPAS                         | JUDICATURA_DE_CHIAPAS__ESTATAL.xlsx                    |
      | josafat.urbina@credifiel.com.mx      | 28/10/2026 | Periodo 10/2026 - Quincenal | MUNICIPIO DE ATIZAPAN                | ESTATAL, MUNICIPIO DE ATIZAPAN                         | MUNICIPIO_DE_ATIZAPAN__ESTATAL.xlsx                    |
      | rita.baltazar@credifiel.com.mx       | 28/10/2026 | Periodo 09/2026 - Quincenal | MUNICIPIO DE NAUCALPAN               | ESTATAL, MUNICIPIO DE NAUCALPAN                        | MUNICIPIO_DE_NAUCALPAN__ESTATAL.xlsx                   |
      | santiago.moreno@credifiel.com.mx     | 28/10/2026 | Periodo 08/2026 - Quincenal | PEMEX                                | JUBILADO, PEMEX                                       | PEMEX__JUBILADO.xlsx                                   |
      | eduardo.martinez@credifiel.com.mx    | 28/10/2026 | Periodo 07/2026 - Quincenal | PENSIONES DE OAXACA                  | ESTATAL, PENSIONES DE OAXACA                           | PENSIONES_DE_OAXACA__ESTATAL.xlsx                      |

      | polet.valdes@credifiel.com.mx        | 28/10/2026 | Periodo 12/2026 - Quincenal | PODER JUDICIAL DE GUERRERO           | ESTATAL, PODER JUDICIAL DE GUERRERO                    | PODER_JUDICIAL_DE_GUERRERO__ESTATAL.xlsx               |
      | fernanda.jimenez@credifiel.com.mx    | 28/10/2026 | Periodo 11/2026 - Quincenal | SAGARPA                              | FEDERAL, SAGARPA                                      | SAGARPA__FEDERAL.xlsx                                  |
      | josafat.urbina@credifiel.com.mx      | 28/10/2026 | Periodo 10/2026 - Quincenal | SALUD AGUASCALIENTES                 | ESTATAL, SALUD AGUASCALIENTES                          | SALUD_AGUASCALIENTES__ESTATAL.xlsx                     |
      | rita.baltazar@credifiel.com.mx       | 28/10/2026 | Periodo 09/2026 - Quincenal | SALUD CAMPECHE                       | FEDERAL, SALUD CAMPECHE                                | SALUD_CAMPECHE__FEDERAL.xlsx                           |
      | santiago.moreno@credifiel.com.mx     | 28/10/2026 | Periodo 08/2026 - Quincenal | SALUD CAMPECHE                       | FORMALIZADO, SALUD CAMPECHE                            | SALUD_CAMPECHE__FORMALIZADO.xlsx                       |
      | eduardo.martinez@credifiel.com.mx    | 28/10/2026 | Periodo 07/2026 - Quincenal | SALUD CAMPECHE                       | REGULARIZADO, SALUD CAMPECHE                           | SALUD_CAMPECHE__REGULARIZADO.xlsx                      |

      | polet.valdes@credifiel.com.mx        | 28/10/2026 | Periodo 12/2026 - Quincenal | SALUD CHIAPAS                        | ESTATAL, SALUD CHIAPAS                                 | SALUD_CHIAPAS__ESTATAL.xlsx                            |
      | fernanda.jimenez@credifiel.com.mx    | 28/10/2026 | Periodo 11/2026 - Quincenal | SALUD CHIHUAHUA                      | ESTATAL, SALUD CHIHUAHUA                               | SALUD_CHIHUAHUA__ESTATAL.xlsx                          |
      | josafat.urbina@credifiel.com.mx      | 28/10/2026 | Periodo 10/2026 - Quincenal | SALUD DE LA CIUDAD DE MEXICO         | ESTATAL, SALUD DE LA CIUDAD DE MEXICO                  | SALUD_DE_LA_CIUDAD_DE_MEXICO__ESTATAL.xlsx             |
      | rita.baltazar@credifiel.com.mx       | 28/10/2026 | Periodo 09/2026 - Quincenal | SALUD DURANGO                        | ESTATAL, SALUD DURANGO                                 | SALUD_DURANGO__ESTATAL.xlsx                            |
      | santiago.moreno@credifiel.com.mx     | 28/10/2026 | Periodo 08/2026 - Quincenal | SALUD NAYARIT                        | ESTATAL, SALUD NAYARIT                                 | SALUD_NAYARIT__ESTATAL.xlsx                            |
      #| eduardo.martinez@credifiel.com.mx    | 28/10/2026 | Periodo 07/2026 - Quincenal | SALUD OAXACA                         | ESTATAL, SALUD OAXACA                                  | SALUD_OAXACA__ESTATAL.xlsx                             |

      | polet.valdes@credifiel.com.mx        | 28/10/2026 | Periodo 12/2026 - Quincenal | SALUD PUEBLA                         | ESTATAL, SALUD PUEBLA                                  | SALUD_PUEBLA__ESTATAL.xlsx                             |
      | fernanda.jimenez@credifiel.com.mx    | 28/10/2026 | Periodo 11/2026 - Quincenal | SALUD QUINTANA ROO                   | ESTATAL, SALUD QUINTANA ROO                            | SALUD_QUINTANA_ROO__ESTATAL.xlsx                       |
      | josafat.urbina@credifiel.com.mx      | 28/10/2026 | Periodo 10/2026 - Quincenal | SALUD TABASCO                        | ESTATAL, SALUD TABASCO                                 | SALUD_TABASCO__ESTATAL.xlsx                            |
      | rita.baltazar@credifiel.com.mx       | 28/10/2026 | Periodo 09/2026 - Quincenal | SALUD TAMAULIPAS                     | ESTATAL, SALUD TAMAULIPAS                              | SALUD_TAMAULIPAS__ESTATAL.xlsx                         |
      | santiago.moreno@credifiel.com.mx     | 28/10/2026 | Periodo 08/2026 - Quincenal | SALUD TAMAULIPAS                     | FEDERAL, SALUD TAMAULIPAS                              | SALUD_TAMAULIPAS__FEDERAL.xlsx                         |
      | eduardo.martinez@credifiel.com.mx    | 28/10/2026 | Periodo 07/2026 - Quincenal | SALUD TLAXCALA                       | ESTATAL, SALUD TLAXCALA                                | SALUD_TLAXCALA__ESTATAL.xlsx                           |

      | polet.valdes@credifiel.com.mx        | 28/10/2026 | Periodo 12/2026 - Quincenal | SAPASA DE ATIZAPAN DE ZARAGOZA       | ESTATAL, SAPASA DE ATIZAPAN DE ZARAGOZA                | SAPASA_DE_ATIZAPAN_DE_ZARAGOZA__ESTATAL.xlsx           |
      | fernanda.jimenez@credifiel.com.mx    | 28/10/2026 | Periodo 11/2026 - Quincenal | SECTUR                               | ESTATAL, SECTUR                                       | SECTUR__ESTATAL.xlsx                                   |
      | josafat.urbina@credifiel.com.mx      | 28/10/2026 | Periodo 10/2026 - Quincenal | SEDATU                               | FEDERAL, SEDATU                                       | SEDATU__FEDERAL.xlsx                                   |
      | rita.baltazar@credifiel.com.mx       | 28/10/2026 | Periodo 09/2026 - Quincenal | SEPOMEX                              | FEDERAL, SEPOMEX                                      | SEPOMEX__FEDERAL.xlsx                                  |
      | santiago.moreno@credifiel.com.mx     | 28/10/2026 | Periodo 08/2026 - Quincenal | SEP AFSEDF                           | FEDERAL, SEP AFSEDF                                   | SEP_AFSEDF__FEDERAL.xlsx                               |
      | eduardo.martinez@credifiel.com.mx    | 28/10/2026 | Periodo 07/2026 - Quincenal | SEP CENTRAL                          | ESTATAL, SEP CENTRAL                                  | SEP_CENTRAL__ESTATAL.xlsx                              |

      | polet.valdes@credifiel.com.mx        | 28/10/2026 | Periodo 12/2026 - Quincenal | SEP SEMS                             | ESTATAL, SEP SEMS                                     | SEP_SEMS__ESTATAL.xlsx                                 |
      #| fernanda.jimenez@credifiel.com.mx    | 28/10/2026 | Periodo 11/2026 - Quincenal | SEP SEMS                             | JUBILADO, SEP SEMS                                    | SEP_SEMS__JUBILADO.xlsx                                |
      | josafat.urbina@credifiel.com.mx      | 28/10/2026 | Periodo 10/2026 - Quincenal | SNTE 17 ESTADO DE MEXICO             | FEDERAL, SNTE 17 ESTADO DE MEXICO                      | SNTE_17_ESTADO_DE_MEXICO__FEDERAL.xlsx                 |
      | rita.baltazar@credifiel.com.mx       | 28/10/2026 | Periodo 09/2026 - Quincenal | SNTE 26 SAN LUIS POTOSI              | FEDERAL, SNTE 26 SAN LUIS POTOSI                       | SNTE_26_SAN_LUIS_POTOSI__FEDERAL.xlsx                  |
      | santiago.moreno@credifiel.com.mx     | 28/10/2026 | Periodo 08/2026 - Quincenal | SNTE 30 TAMAULIPAS                   | ESTATAL, SNTE 30 TAMAULIPAS                            | SNTE_30_TAMAULIPAS__ESTATAL.xlsx                       |
      | eduardo.martinez@credifiel.com.mx    | 28/10/2026 | Periodo 07/2026 - Quincenal | SNTE 30 TAMAULIPAS                   | FEDERAL, SNTE 30 TAMAULIPAS                            | SNTE_30_TAMAULIPAS__FEDERAL.xlsx                       |

      | polet.valdes@credifiel.com.mx        | 28/10/2026 | Periodo 12/2026 - Quincenal | SNTE 32 VERACRUZ                    | FEDERAL, SNTE 32 VERACRUZ                             | SNTE_32_VERACRUZ__FEDERAL.xlsx                         |
      | fernanda.jimenez@credifiel.com.mx    | 28/10/2026 | Periodo 11/2026 - Quincenal | SNTE 34 ZACATECAS                    | ESTATAL, SNTE 34 ZACATECAS                             | SNTE_34_ZACATECAS__ESTATAL.xlsx                        |
      | josafat.urbina@credifiel.com.mx      | 28/10/2026 | Periodo 10/2026 - Quincenal | SNTE 34 ZACATECAS                    | FEDERAL, SNTE 34 ZACATECAS                             | SNTE_34_ZACATECAS__FEDERAL.xlsx                        |
      | rita.baltazar@credifiel.com.mx       | 28/10/2026 | Periodo 09/2026 - Quincenal | SNTE 37 BAJA CALIFORNIA              | ESTATAL, SNTE 37 BAJA CALIFORNIA                       | SNTE_37_BAJA_CALIFORNIA__ESTATAL.xlsx                  |
      | santiago.moreno@credifiel.com.mx     | 28/10/2026 | Periodo 08/2026 - Quincenal | SNTE 3 BAJA CALIFORNIA SUR           | FEDERAL, SNTE 3 BAJA CALIFORNIA SUR                    | SNTE_3_BAJA_CALIFORNIA_SUR__FEDERAL.xlsx               |
      | eduardo.martinez@credifiel.com.mx    | 28/10/2026 | Periodo 07/2026 - Quincenal | SNTE 44 DURANGO                      | ESTATAL, SNTE 44 DURANGO                               | SNTE_44_DURANGO__ESTATAL.xlsx                          |

      | polet.valdes@credifiel.com.mx        | 28/10/2026 | Periodo 12/2026 - Quincenal | SNTE 47 JALISCO                      | ESTATAL, SNTE 47 JALISCO                               | SNTE_47_JALISCO__ESTATAL.xlsx                          |
      | fernanda.jimenez@credifiel.com.mx    | 28/10/2026 | Periodo 11/2026 - Quincenal | SNTE 51 PUEBLA                       | ESTATAL, SNTE 51 PUEBLA                                | SNTE_51_PUEBLA__ESTATAL.xlsx                           |
      | josafat.urbina@credifiel.com.mx      | 28/10/2026 | Periodo 10/2026 - Quincenal | SNTE 53 SINALOA                      | ESTATAL, SNTE 53 SINALOA                               | SNTE_53_SINALOA__ESTATAL.xlsx                          |
      | rita.baltazar@credifiel.com.mx       | 28/10/2026 | Periodo 09/2026 - Quincenal | SNTE 56 VERACRUZ                     | ESTATAL, SNTE 56 VERACRUZ                              | SNTE_56_VERACRUZ__ESTATAL.xlsx                         |
      | santiago.moreno@credifiel.com.mx     | 28/10/2026 | Periodo 08/2026 - Quincenal | SNTE 7 CHIAPAS                       | FEDERAL, SNTE 7 CHIAPAS                                | SNTE_7_CHIAPAS__FEDERAL.xlsx                           |
      | eduardo.martinez@credifiel.com.mx    | 28/10/2026 | Periodo 07/2026 - Quincenal | SUTESUAEM ESTADO DE MEXICO           | ESTATAL, SUTESUAEM ESTADO DE MEXICO                    | SUTESUAEM_ESTADO_DE_MEXICO__ESTATAL.xlsx               |

      | polet.valdes@credifiel.com.mx        | 28/10/2026 | Periodo 12/2026 - Quincenal | UABJO OAXACA                         | ESTATAL, UABJO OAXACA                                  | UABJO_OAXACA__ESTATAL.xlsx                             |
      | fernanda.jimenez@credifiel.com.mx    | 28/10/2026 | Periodo 11/2026 - Quincenal | UNACH CHIAPAS                        | ESTATAL, UNACH CHIAPAS                                 | UNACH_CHIAPAS__ESTATAL.xlsx                            |


  @web @CP02
  Esquema del escenario: Importar y transferir transacciones INVEX

    Dado que el usuario está en la página de login
    Y El usuario inicia sesion con "<usuario>"

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
      | usuario                              | periodo                     | pagaduria                                            | cuenta                                            | archivoCuentas       |
      | polet.valdes@credifiel.com.mx        | Periodo 12/2026 - Quincenal | ESTATAL, CECYTE CHIAPAS                              | Nómina \| CECYTE CHIAPAS                          | Nómina \| Invex 2595 |
      | fernanda.jimenez@credifiel.com.mx    | Periodo 11/2026 - Quincenal | ESTATAL, CECYTE NAYARIT                              | Nómina \| CECYTE NAYARIT                          | Nómina \| Invex 2595 |
      | josafat.urbina@credifiel.com.mx      | Periodo 10/2026 - Quincenal | ESTATAL, CECYTE OAXACA                               | Nómina \| CECYTE OAXACA                           | Nómina \| Invex 2595 |
      | rita.baltazar@credifiel.com.mx       | Periodo 09/2026 - Quincenal | ESTATAL, COBACH BAJA CALIFORNIA SUR                  | Nómina \| COBACH BAJA CALIFORNIA SUR              | Nómina \| Invex 2595 |
      | santiago.moreno@credifiel.com.mx     | Periodo 08/2026 - Quincenal | ESTATAL, COBACH BAJA CALIFORNIA                      | Nómina \| COBACH BAJA CALIFORNIA                  | Nómina \| Invex 2595 |
      | eduardo.martinez@credifiel.com.mx    | Periodo 07/2026 - Quincenal | ESTATAL, COBACH CHIAPAS                              | Nómina \| COBACH CHIAPAS                          | Nómina \| Invex 2595 |

      | polet.valdes@credifiel.com.mx        | Periodo 12/2026 - Quincenal | ESTATAL, COBAO OAXACA                                | Nómina \| COBAO OAXACA                            | Nómina \| Invex 2595 |
      | fernanda.jimenez@credifiel.com.mx    | Periodo 11/2026 - Quincenal | ESTATAL, COLEGIO DE BACHILLERES DE QUINTANA ROO      | Nómina \| COLEGIO DE BACHILLERES DE QUINTANA ROO  | Nómina \| Invex 2595 |
      | josafat.urbina@credifiel.com.mx      | Periodo 10/2026 - Quincenal | ESTATAL, COMISION DE AGUA DEL ESTADO DE MEXICO       | Nómina \| COMISION DE AGUA DEL ESTADO DE MEXICO   | Nómina \| Invex 2595 |
      | rita.baltazar@credifiel.com.mx       | Periodo 09/2026 - Quincenal | CENTRAL, COMISION DE AGUA DE VERACRUZ                | Nómina \| COMISION DE AGUA DE VERACRUZ            | Nómina \| Invex 2595 |
      | santiago.moreno@credifiel.com.mx     | Periodo 08/2026 - Quincenal | ESTATAL, COMISION DE AGUA DE VERACRUZ                | Nómina \| COMISION DE AGUA DE VERACRUZ            | Nómina \| Invex 2595 |
      #| eduardo.martinez@credifiel.com.mx    | Periodo 07/2026 - Quincenal | ESTATAL, CONAGUA HIDALGO                             | Nómina \| CONAGUA HIDALGO                         | Nómina \| Invex 2595 |

      | polet.valdes@credifiel.com.mx        | Periodo 12/2026 - Quincenal | ESTATAL, CONALEP QUINTANA ROO                        | Nómina \| CONALEP QUINTANA ROO                    | Nómina \| Invex 2595 |
      | fernanda.jimenez@credifiel.com.mx    | Periodo 11/2026 - Quincenal | ESTATAL, CONGRESO DE OAXACA                          | Nómina \| CONGRESO DE OAXACA                      | Nómina \| Invex 2595 |
      | josafat.urbina@credifiel.com.mx      | Periodo 10/2026 - Quincenal | ESTATAL, DIF QUINTANA ROO                            | Nómina \| DIF QUINTANA ROO                        | Nómina \| Invex 2595 |
      | rita.baltazar@credifiel.com.mx       | Periodo 09/2026 - Quincenal | ESTATAL, FISCALIA DE AGUASCALIENTES                  | Nómina \| FISCALIA DE AGUASCALIENTES              | Nómina \| Invex 2595 |
      | santiago.moreno@credifiel.com.mx     | Periodo 08/2026 - Quincenal | ESTATAL, FISCALIA DE BAJA CALIFORNIA                 | Nómina \| FISCALIA DE BAJA CALIFORNIA             | Nómina \| Invex 2595 |
      | eduardo.martinez@credifiel.com.mx    | Periodo 07/2026 - Quincenal | ESTATAL, FISCALIA DE NAYARIT                         | Nómina \| FISCALIA DE NAYARIT                     | Nómina \| Invex 2595 |

      | polet.valdes@credifiel.com.mx        | Periodo 12/2026 - Quincenal | ESTATAL, FISCALIA DE QUINTANA ROO                    | Nómina \| FISCALIA DE QUINTANA ROO                | Nómina \| Invex 2595 |
      | fernanda.jimenez@credifiel.com.mx    | Periodo 11/2026 - Quincenal | ESTATAL, FISCALIA DE TAMAULIPAS                      | Nómina \| FISCALIA DE TAMAULIPAS                  | Nómina \| Invex 2595 |
      | josafat.urbina@credifiel.com.mx      | Periodo 10/2026 - Quincenal | ESTATAL, FISCALIA ESPECIALIZADA DE QUINTANA ROO      | Nómina \| FISCALIA ESPECIALIZADA DE QUINTANA ROO  | Nómina \| Invex 2595 |
      | rita.baltazar@credifiel.com.mx       | Periodo 09/2026 - Quincenal | CENTRAL, FONATUR                                     | Nómina \| FONATUR                                 | Nómina \| Invex 2595 |
      | santiago.moreno@credifiel.com.mx     | Periodo 08/2026 - Quincenal | INFRAESTRUCTURA, FONATUR                             | Nómina \| FONATUR                                 | Nómina \| Invex 2595 |
      | eduardo.martinez@credifiel.com.mx    | Periodo 07/2026 - Quincenal | TREN MAYA, FONATUR                                   | Nómina \| FONATUR                                 | Nómina \| Invex 2595 |

      | polet.valdes@credifiel.com.mx        | Periodo 12/2026 - Quincenal | ESTATAL, GOBIERNO DEL ESTADO DE MEXICO               | Nómina \| GOBIERNO DEL ESTADO DE MEXICO           | Nómina \| Invex 2595 |
      | fernanda.jimenez@credifiel.com.mx    | Periodo 11/2026 - Quincenal | CLAVE 76, GOBIERNO DE CHIAPAS                        | Nómina \| GOBIERNO DE CHIAPAS                     | Nómina \| Invex 2595 |
      | josafat.urbina@credifiel.com.mx      | Periodo 10/2026 - Quincenal | CLAVE 83, GOBIERNO DE CHIAPAS                        | Nómina \| GOBIERNO DE CHIAPAS               | Nómina \| Invex 2595 |
      | rita.baltazar@credifiel.com.mx       | Periodo 09/2026 - Quincenal | ESTATAL, GOBIERNO DE JALISCO                         | Nómina \| GOBIERNO DE JALISCO               | Nómina \| Invex 2595 |
      | santiago.moreno@credifiel.com.mx     | Periodo 08/2026 - Quincenal | ESTATAL, GOBIERNO DE LA CIUDAD DE MEXICO             | Nómina \| GOBIERNO DE LA CIUDAD DE MEXICO   | Nómina \| Invex 2595 |
      | eduardo.martinez@credifiel.com.mx    | Periodo 07/2026 - Quincenal | ESTATAL, GOBIERNO DE QUINTANA ROO                    | Nómina \| GOBIERNO DE QUINTANA ROO          | Nómina \| Invex 2595 |

      #| polet.valdes@credifiel.com.mx        | Periodo 12/2026 - Quincenal | ESTATAL, GOBIERNO DE SINALOA                         | Nómina \| GOBIERNO DE SINALOA               | Nómina \| Invex 2595 |
      | fernanda.jimenez@credifiel.com.mx    | Periodo 11/2026 - Quincenal | ESTATAL, GOBIERNO DE TAMAULIPAS                      | Nómina \| GOBIERNO DE TAMAULIPAS            | Nómina \| Invex 2595 |
      | josafat.urbina@credifiel.com.mx      | Periodo 10/2026 - Quincenal | FEDERAL, IMSS BIENESTAR                              | Nómina \| IMSS BIENESTAR                    | Nómina \| Invex 2595 |
      | rita.baltazar@credifiel.com.mx       | Periodo 09/2026 - Quincenal | ESTATAL, INSTITUTO DE PENSIONES DE JALISCO           | Nómina \| INSTITUTO DE PENSIONES DE JALISCO | Nómina \| Invex 2595 |
      | santiago.moreno@credifiel.com.mx     | Periodo 08/2026 - Quincenal | JUBILADO, INSTITUTO DE PENSIONES DE JALISCO          | Nómina \| INSTITUTO DE PENSIONES DE JALISCO | Nómina \| Invex 2595 |
      | eduardo.martinez@credifiel.com.mx    | Periodo 07/2026 - Quincenal | ESTATAL, IPAX VERACRUZ                               | Nómina \| IPAX VERACRUZ                     | Nómina \| Invex 2595 |

      | polet.valdes@credifiel.com.mx        | Periodo 12/2026 - Quincenal | ESTATAL, ISSSTECALI BAJA CALIFORNIA                  | Nómina \| ISSSTECALI BAJA CALIFORNIA        | Nómina \| Invex 2595 |
      | fernanda.jimenez@credifiel.com.mx    | Periodo 11/2026 - Quincenal | ESTATAL, JUDICATURA DE CHIAPAS                       | Nómina \| JUDICATURA DE CHIAPAS             | Nómina \| Invex 2595 |
      | josafat.urbina@credifiel.com.mx      | Periodo 10/2026 - Quincenal | ESTATAL, MUNICIPIO DE ATIZAPAN                       | Nómina \| MUNICIPIO DE ATIZAPAN             | Nómina \| Invex 2595 |
      | rita.baltazar@credifiel.com.mx       | Periodo 09/2026 - Quincenal | ESTATAL, MUNICIPIO DE NAUCALPAN                      | Nómina \| MUNICIPIO DE NAUCALPAN            | Nómina \| Invex 2595 |
      | santiago.moreno@credifiel.com.mx     | Periodo 08/2026 - Quincenal | JUBILADO, PEMEX                                      | Nómina \| PEMEX                             | Nómina \| Invex 2595 |
      | eduardo.martinez@credifiel.com.mx    | Periodo 07/2026 - Quincenal | ESTATAL, PENSIONES DE OAXACA                         | Nómina \| PENSIONES DE OAXACA               | Nómina \| Invex 2595 |

      | polet.valdes@credifiel.com.mx        | Periodo 12/2026 - Quincenal | ESTATAL, PODER JUDICIAL DE GUERRERO                  | Nómina \| PODER JUDICIAL DE GUERRERO        | Nómina \| Invex 2595 |
      | fernanda.jimenez@credifiel.com.mx    | Periodo 11/2026 - Quincenal | FEDERAL, SAGARPA                                     | Nómina \| SAGARPA                           | Nómina \| Invex 2595 |
      | josafat.urbina@credifiel.com.mx      | Periodo 10/2026 - Quincenal | ESTATAL, SALUD AGUASCALIENTES                        | Nómina \| SALUD AGUASCALIENTES              | Nómina \| Invex 2595 |
      | rita.baltazar@credifiel.com.mx       | Periodo 09/2026 - Quincenal | FEDERAL, SALUD CAMPECHE                              | Nómina \| SALUD CAMPECHE                    | Nómina \| Invex 2595 |
      | santiago.moreno@credifiel.com.mx     | Periodo 08/2026 - Quincenal | FORMALIZADO, SALUD CAMPECHE                          | Nómina \| SALUD CAMPECHE                    | Nómina \| Invex 2595 |
      | eduardo.martinez@credifiel.com.mx    | Periodo 07/2026 - Quincenal | REGULARIZADO, SALUD CAMPECHE                         | Nómina \| SALUD CAMPECHE                    | Nómina \| Invex 2595 |

      | polet.valdes@credifiel.com.mx        | Periodo 12/2026 - Quincenal | ESTATAL, SALUD CHIAPAS                               | Nómina \| SALUD CHIAPAS                     | Nómina \| Invex 2595 |
      | fernanda.jimenez@credifiel.com.mx    | Periodo 11/2026 - Quincenal | ESTATAL, SALUD CHIHUAHUA                             | Nómina \| SALUD CHIHUAHUA                   | Nómina \| Invex 2595 |
      | josafat.urbina@credifiel.com.mx      | Periodo 10/2026 - Quincenal | ESTATAL, SALUD DE LA CIUDAD DE MEXICO                | Nómina \| SALUD DE LA CIUDAD DE MEXICO      | Nómina \| Invex 2595 |
      | rita.baltazar@credifiel.com.mx       | Periodo 09/2026 - Quincenal | ESTATAL, SALUD DURANGO                               | Nómina \| SALUD DURANGO                     | Nómina \| Invex 2595 |
      | santiago.moreno@credifiel.com.mx     | Periodo 08/2026 - Quincenal | ESTATAL, SALUD NAYARIT                               | Nómina \| SALUD NAYARIT                     | Nómina \| Invex 2595 |
      #| eduardo.martinez@credifiel.com.mx    | Periodo 07/2026 - Quincenal | ESTATAL, SALUD OAXACA                                | Nómina \| SALUD OAXACA                      | Nómina \| Invex 2595 |

      | polet.valdes@credifiel.com.mx        | Periodo 12/2026 - Quincenal | ESTATAL, SALUD PUEBLA                                | Nómina \| SALUD PUEBLA                      | Nómina \| Invex 2595 |
      | fernanda.jimenez@credifiel.com.mx    | Periodo 11/2026 - Quincenal | ESTATAL, SALUD QUINTANA ROO                          | Nómina \| SALUD QUINTANA ROO                | Nómina \| Invex 2595 |
      | josafat.urbina@credifiel.com.mx      | Periodo 10/2026 - Quincenal | ESTATAL, SALUD TABASCO                               | Nómina \| SALUD TABASCO                     | Nómina \| Invex 2595 |
      | rita.baltazar@credifiel.com.mx       | Periodo 09/2026 - Quincenal | ESTATAL, SALUD TAMAULIPAS                            | Nómina \| SALUD TAMAULIPAS                  | Nómina \| Invex 2595 |
      | santiago.moreno@credifiel.com.mx     | Periodo 08/2026 - Quincenal | FEDERAL, SALUD TAMAULIPAS                            | Nómina \| SALUD TAMAULIPAS                  | Nómina \| Invex 2595 |
      | eduardo.martinez@credifiel.com.mx    | Periodo 07/2026 - Quincenal | ESTATAL, SALUD TLAXCALA                              | Nómina \| SALUD TLAXCALA                    | Nómina \| Invex 2595 |

      | polet.valdes@credifiel.com.mx        | Periodo 12/2026 - Quincenal | ESTATAL, SAPASA DE ATIZAPAN DE ZARAGOZA              | Nómina \| SAPASA DE ATIZAPAN DE ZARAGOZA    | Nómina \| Invex 2595 |
      | fernanda.jimenez@credifiel.com.mx    | Periodo 11/2026 - Quincenal | ESTATAL, SECTUR                                      | Nómina \| SECTUR                            | Nómina \| Invex 2595 |
      | josafat.urbina@credifiel.com.mx      | Periodo 10/2026 - Quincenal | FEDERAL, SEDATU                                      | Nómina \| SEDATU                            | Nómina \| Invex 2595 |
      | rita.baltazar@credifiel.com.mx       | Periodo 09/2026 - Quincenal | FEDERAL, SEPOMEX                                     | Nómina \| SEPOMEX                           | Nómina \| Invex 2595 |
      | santiago.moreno@credifiel.com.mx     | Periodo 08/2026 - Quincenal | FEDERAL, SEP AFSEDF                                  | Nómina \| SEP AFSEDF                        | Nómina \| Invex 2595 |
      | eduardo.martinez@credifiel.com.mx    | Periodo 07/2026 - Quincenal | ESTATAL, SEP CENTRAL                                 | Nómina \| SEP CENTRAL                       | Nómina \| Invex 2595 |

      | polet.valdes@credifiel.com.mx        | Periodo 12/2026 - Quincenal | ESTATAL, SEP SEMS                                    | Nómina \| SEP SEMS                          | Nómina \| Invex 2595 |
      #| fernanda.jimenez@credifiel.com.mx    | Periodo 11/2026 - Quincenal | JUBILADO, SEP SEMS                                   | Nómina \| SEP SEMS                          | Nómina \| Invex 2595 |
      | josafat.urbina@credifiel.com.mx      | Periodo 10/2026 - Quincenal | FEDERAL, SNTE 17 ESTADO DE MEXICO                    | Nómina \| SNTE 17 ESTADO DE MEXICO          | Nómina \| Invex 2595 |
      | rita.baltazar@credifiel.com.mx       | Periodo 09/2026 - Quincenal | FEDERAL, SNTE 26 SAN LUIS POTOSI                     | Nómina \| SNTE 26 SAN LUIS POTOSI           | Nómina \| Invex 2595 |
      | santiago.moreno@credifiel.com.mx     | Periodo 08/2026 - Quincenal | ESTATAL, SNTE 30 TAMAULIPAS                          | Nómina \| SNTE 30 TAMAULIPAS                | Nómina \| Invex 2595 |
      | eduardo.martinez@credifiel.com.mx    | Periodo 07/2026 - Quincenal | FEDERAL, SNTE 30 TAMAULIPAS                          | Nómina \| SNTE 30 TAMAULIPAS                | Nómina \| Invex 2595 |

      | polet.valdes@credifiel.com.mx        | Periodo 12/2026 - Quincenal | FEDERAL, SNTE 32 VERACRUZ                            | Nómina \| SNTE 32 VERACRUZ                  | Nómina \| Invex 2595 |
      | fernanda.jimenez@credifiel.com.mx    | Periodo 11/2026 - Quincenal | ESTATAL, SNTE 34 ZACATECAS                           | Nómina \| SNTE 34 ZACATECAS                 | Nómina \| Invex 2595 |
      | josafat.urbina@credifiel.com.mx      | Periodo 10/2026 - Quincenal | FEDERAL, SNTE 34 ZACATECAS                           | Nómina \| SNTE 34 ZACATECAS                 | Nómina \| Invex 2595 |
      | rita.baltazar@credifiel.com.mx       | Periodo 09/2026 - Quincenal | ESTATAL, SNTE 37 BAJA CALIFORNIA                     | Nómina \| SNTE 37 BAJA CALIFORNIA           | Nómina \| Invex 2595 |
      | santiago.moreno@credifiel.com.mx     | Periodo 08/2026 - Quincenal | FEDERAL, SNTE 3 BAJA CALIFORNIA SUR                  | Nómina \| SNTE 3 BAJA CALIFORNIA SUR        | Nómina \| Invex 2595 |
      | eduardo.martinez@credifiel.com.mx    | Periodo 07/2026 - Quincenal | ESTATAL, SNTE 44 DURANGO                             | Nómina \| SNTE 44 DURANGO                   | Nómina \| Invex 2595 |
      | polet.valdes@credifiel.com.mx        | Periodo 12/2026 - Quincenal | ESTATAL, SNTE 47 JALISCO                             | Nómina \| SNTE 47 JALISCO                   | Nómina \| Invex 2595 |
      | fernanda.jimenez@credifiel.com.mx    | Periodo 11/2026 - Quincenal | ESTATAL, SNTE 51 PUEBLA                              | Nómina \| SNTE 51 PUEBLA                    | Nómina \| Invex 2595 |
      | josafat.urbina@credifiel.com.mx      | Periodo 10/2026 - Quincenal | ESTATAL, SNTE 53 SINALOA                             | Nómina \| SNTE 53 SINALOA                   | Nómina \| Invex 2595 |
      | rita.baltazar@credifiel.com.mx       | Periodo 09/2026 - Quincenal | ESTATAL, SNTE 56 VERACRUZ                            | Nómina \| SNTE 56 VERACRUZ                  | Nómina \| Invex 2595 |
      | santiago.moreno@credifiel.com.mx     | Periodo 08/2026 - Quincenal | FEDERAL, SNTE 7 CHIAPAS                              | Nómina \| SNTE 7 CHIAPAS                    | Nómina \| Invex 2595 |
      | eduardo.martinez@credifiel.com.mx    | Periodo 07/2026 - Quincenal | ESTATAL, SUTESUAEM ESTADO DE MEXICO                  | Nómina \| SUTESUAEM ESTADO DE MEXICO        | Nómina \| Invex 2595 |
      | polet.valdes@credifiel.com.mx        | Periodo 12/2026 - Quincenal | ESTATAL, UABJO OAXACA                                | Nómina \| UABJO OAXACA                      | Nómina \| Invex 2595 |
      | fernanda.jimenez@credifiel.com.mx    | Periodo 11/2026 - Quincenal | ESTATAL, UNACH CHIAPAS                               | Nómina \| UNACH CHIAPAS                     | Nómina \| Invex 2595 |



  @web @CP03
  Esquema del escenario: Carga de promesas de pago - Extrajudicial

    Dado que el usuario está en la página de login
    Y El usuario inicia sesion con "<usuario>"
    Y el usuario actualiza los 20 archivos de promesas desde el Universo de Cartera

    Cuando el usuario ingresa a Carga Masiva de Promesas de Pago
    Y el usuario sube el archivo de promesas "<archivo>"
    Y el usuario lee el archivo de promesas

    Entonces todas las promesas muestran el estado Pendiente

    Cuando el usuario procesa las promesas de pago

    Entonces todas las promesas muestran el estado Exitoso

    Ejemplos:
      | usuario                           | archivo                                 |
      | polet.valdes@credifiel.com.mx     | Template de Carga de Promesas v2.xlsx    |
      | fernanda.jimenez@credifiel.com.mx | Template de Carga de Promesas v2_2.xlsx  |
      | josafat.urbina@credifiel.com.mx   | Template de Carga de Promesas v2_3.xlsx  |
      | rita.baltazar@credifiel.com.mx    | Template de Carga de Promesas v2_4.xlsx  |
      | santiago.moreno@credifiel.com.mx  | Template de Carga de Promesas v2_5.xlsx  |

      | polet.valdes@credifiel.com.mx     | Template de Carga de Promesas v2_6.xlsx  |
      | fernanda.jimenez@credifiel.com.mx | Template de Carga de Promesas v2_7.xlsx  |
      | josafat.urbina@credifiel.com.mx   | Template de Carga de Promesas v2_8.xlsx  |
      | rita.baltazar@credifiel.com.mx    | Template de Carga de Promesas v2_9.xlsx  |
      | santiago.moreno@credifiel.com.mx  | Template de Carga de Promesas v2_10.xlsx |

      | polet.valdes@credifiel.com.mx     | Template de Carga de Promesas v2_11.xlsx |
      | fernanda.jimenez@credifiel.com.mx | Template de Carga de Promesas v2_12.xlsx |
      | josafat.urbina@credifiel.com.mx   | Template de Carga de Promesas v2_13.xlsx |
      | rita.baltazar@credifiel.com.mx    | Template de Carga de Promesas v2_14.xlsx |
      | santiago.moreno@credifiel.com.mx  | Template de Carga de Promesas v2_15.xlsx |

      | polet.valdes@credifiel.com.mx     | Template de Carga de Promesas v2_16.xlsx |
      | fernanda.jimenez@credifiel.com.mx | Template de Carga de Promesas v2_17.xlsx |
      | josafat.urbina@credifiel.com.mx   | Template de Carga de Promesas v2_18.xlsx |
      | rita.baltazar@credifiel.com.mx    | Template de Carga de Promesas v2_19.xlsx |
      | santiago.moreno@credifiel.com.mx  | Template de Carga de Promesas v2_20.xlsx |

#      | usuario                              | archivo                                  |
#      | laura.gomez@credifiel.com.mx         | Template de Carga de Promesas v2.xlsx    |
#      | valeria.padilla@credifiel.com.mx     | Template de Carga de Promesas v2_2.xlsx  |
#      | brenda.pacheco@credifiel.com.mx      | Template de Carga de Promesas v2_3.xlsx  |
#      | daniel.velazquez@credifiel.com.mx    | Template de Carga de Promesas v2_4.xlsx  |
#      | jorge.toledo@credifiel.com.mx        | Template de Carga de Promesas v2_5.xlsx  |
#
#      | laura.gomez@credifiel.com.mx        | Template de Carga de Promesas v2_6.xlsx  |
#      | valeria.padilla@credifiel.com.mx    | Template de Carga de Promesas v2_7.xlsx  |
#      | brenda.pacheco@credifiel.com.mx     | Template de Carga de Promesas v2_8.xlsx  |
#      | daniel.velazquez@credifiel.com.mx   | Template de Carga de Promesas v2_9.xlsx  |
#      | jorge.toledo@credifiel.com.mx       | Template de Carga de Promesas v2_10.xlsx |
#
#      | laura.gomez@credifiel.com.mx        | Template de Carga de Promesas v2_11.xlsx |
#      | valeria.padilla@credifiel.com.mx    | Template de Carga de Promesas v2_12.xlsx |
#      | brenda.pacheco@credifiel.com.mx     | Template de Carga de Promesas v2_13.xlsx |
#      | daniel.velazquez@credifiel.com.mx   | Template de Carga de Promesas v2_14.xlsx |
#      | jorge.toledo@credifiel.com.mx       | Template de Carga de Promesas v2_15.xlsx |
#
#      | laura.gomez@credifiel.com.mx        | Template de Carga de Promesas v2_16.xlsx |
#      | valeria.padilla@credifiel.com.mx    | Template de Carga de Promesas v2_17.xlsx |
#      | brenda.pacheco@credifiel.com.mx     | Template de Carga de Promesas v2_18.xlsx |
#      | daniel.velazquez@credifiel.com.mx   | Template de Carga de Promesas v2_19.xlsx |
#      | jorge.toledo@credifiel.com.mx       | Template de Carga de Promesas v2_20.xlsx |



  @web @CP04
  Esquema del escenario: Exportacion e Importación de archivo de movimientos conciliados

    Dado que el usuario está en la página de login
    Y El usuario inicia sesion con "<usuario>"

    Cuando el usuario ingresa al módulo Cuentas para importar movimientos
    Y selecciona la cuenta de movimientos "Extrajudicial | Azteca 8646"
    Y el usuario abre la importación del layout bancario
    Y el usuario selecciona el banco "BANCO AZTECA"
    Y el usuario "<usuario>" carga el archivo "<archivo>" con fecha incrementada

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
    Y el usuario agrega los créditos al archivo descargado "<template>"
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
      | usuario                           | archivo        | template                                 |
      | laura.gomez@credifiel.com.mx      | AZTECA_QA.xlsx | Template de Carga de Promesas v2.xlsx    |
#      | valeria.padilla@credifiel.com.mx  | AZTECA_QA.xlsx | Template de Carga de Promesas v2_2.xlsx  |
#      | brenda.pacheco@credifiel.com.mx   | AZTECA_QA.xlsx | Template de Carga de Promesas v2_3.xlsx  |
#      | daniel.velazquez@credifiel.com.mx | AZTECA_QA.xlsx | Template de Carga de Promesas v2_4.xlsx  |
#      | jorge.toledo@credifiel.com.mx     | AZTECA_QA.xlsx | Template de Carga de Promesas v2_5.xlsx  |
#
#      | laura.gomez@credifiel.com.mx      | AZTECA_QA.xlsx | Template de Carga de Promesas v2_6.xlsx  |
#      | valeria.padilla@credifiel.com.mx  | AZTECA_QA.xlsx | Template de Carga de Promesas v2_7.xlsx  |
#      | brenda.pacheco@credifiel.com.mx   | AZTECA_QA.xlsx | Template de Carga de Promesas v2_8.xlsx  |
#      | daniel.velazquez@credifiel.com.mx | AZTECA_QA.xlsx | Template de Carga de Promesas v2_9.xlsx  |
#      | jorge.toledo@credifiel.com.mx     | AZTECA_QA.xlsx | Template de Carga de Promesas v2_10.xlsx |
#
#      | laura.gomez@credifiel.com.mx      | AZTECA_QA.xlsx | Template de Carga de Promesas v2_11.xlsx |
#      | valeria.padilla@credifiel.com.mx  | AZTECA_QA.xlsx | Template de Carga de Promesas v2_12.xlsx |
#      | brenda.pacheco@credifiel.com.mx   | AZTECA_QA.xlsx | Template de Carga de Promesas v2_13.xlsx |
#      | daniel.velazquez@credifiel.com.mx | AZTECA_QA.xlsx | Template de Carga de Promesas v2_14.xlsx |
#      | jorge.toledo@credifiel.com.mx     | AZTECA_QA.xlsx | Template de Carga de Promesas v2_15.xlsx |
#
#      | laura.gomez@credifiel.com.mx      | AZTECA_QA.xlsx | Template de Carga de Promesas v2_16.xlsx |
#      | valeria.padilla@credifiel.com.mx  | AZTECA_QA.xlsx | Template de Carga de Promesas v2_17.xlsx |
#      | brenda.pacheco@credifiel.com.mx   | AZTECA_QA.xlsx | Template de Carga de Promesas v2_18.xlsx |
#      | daniel.velazquez@credifiel.com.mx | AZTECA_QA.xlsx | Template de Carga de Promesas v2_19.xlsx |
#      | jorge.toledo@credifiel.com.mx     | AZTECA_QA.xlsx | Template de Carga de Promesas v2_20.xlsx |


  @web @CP05
  Esquema del escenario: Conciliación de pagos  Domiciliación

    Dado que el usuario está en la página de login
    Y El usuario inicia sesion con "<usuario>"

    Cuando el usuario abre Domiciliación desde el menú de inicio
    Y el usuario ingresa a Solicitud de Facturación
    Y el usuario crea una nueva solicitud de facturación
    Y el usuario selecciona el banco de facturación "<banco>"
    Y el usuario selecciona el servicio de facturación "Tradicional"
    Y las opciones bancarias de facturación permanecen desmarcadas
    Y el usuario activa Importar créditos Nómina Extrajudicial
    Y el usuario adjunta el layout de facturación "<archivo>"
    Y el usuario completa las fechas actuales únicamente para Santander

    Cuando el usuario genera los datos de domiciliación
    Entonces la carga de datos de domiciliación finaliza sin indicador de carga
    Cuando el usuario genera el archivo de domiciliación
    Y el usuario descarga el archivo de esta solicitud de domiciliación
    Y el usuario adjunta como respuesta el mismo archivo descargado de domiciliación
    Y el usuario procesa la respuesta de domiciliación
    Entonces la respuesta de domiciliación termina al 100 por ciento con todos los registros generados
    Y el usuario guarda el ID de la solicitud de facturación actual
    Cuando el usuario carga e importa el depósito de su banco antes de conciliar
    Cuando el usuario abre la cuenta Domiciliación
    Y el usuario asigna la solicitud de facturación guardada
    Y el usuario selecciona la solicitud guardada para conciliar
    Cuando el usuario confirma la conciliación de la solicitud de facturación
    Entonces el proceso reciente de conciliación de domiciliación termina al 100 por ciento

    Ejemplos:
      | usuario                              | banco     | archivo                    |
      | polet.valdes@credifiel.com.mx        | BBVA      | Layout_domi_bbva.xlsx      |
      | fernanda.jimenez@credifiel.com.mx    | Banamex   | Layout_domi_banamex.xlsx   |
      | josafat.urbina@credifiel.com.mx      | Santander | Layout_domi_santander.xlsx |

  @web @CP06
  Esquema del escenario: Reembolsos masivos para <usuario>
    Dado que el usuario está en la página de login
    Y El usuario inicia sesion con "<usuario>"
    Cuando el usuario abre Reembolsos desde el menú de inicio
    Y el usuario crea un nuevo reembolso
    Y el usuario selecciona reembolso Masivo
    Y el usuario selecciona Excel para el reembolso
    Y el usuario selecciona el subtipo de reembolso "Domiciliación-Nómina"
    Y el usuario adjunta el archivo de reembolsos "<archivo>"
    Cuando el usuario lee el archivo de reembolsos
    Y el usuario avanza el reembolso a Autorización
    Entonces el reembolso masivo queda en Autorización
    Cuando el usuario selecciona el tipo de rechazo del reembolso "Documentos"
    Y el usuario selecciona el motivo de documentos del reembolso "Documentos ilegibles"
    Y el usuario aprueba el reembolso como Subdirector de Operaciones
    Cuando el usuario avanza el reembolso a Liberación
    Entonces el reembolso masivo queda en la etapa "Liberación"
    Cuando el usuario avanza el reembolso a Aprobado
    Entonces el reembolso masivo queda en la etapa "Aprobado"

    Ejemplos:
      | usuario                            | archivo                        |
      | polet.valdes@credifiel.com.mx      | reembolsos_desde_captura.xlsx |


