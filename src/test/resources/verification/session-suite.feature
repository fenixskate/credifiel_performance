# language: es
@web @verificacionSesion @pendiente
Característica: Reutilizacion de sesiones en suite local

  Esquema del escenario: Primer flujo local
    Dado que el usuario está en la página de login
    Y El usuario inicia sesion con "<usuario>"
    Entonces la sesion local de "<usuario>" conserva su navegador
    Ejemplos:
      | usuario |
      | local1 |
      | local2 |
      | local3 |
      | local4 |
      | local5 |
      | local6 |

  Esquema del escenario: Siguiente flujo local
    Dado que el usuario está en la página de login
    Y El usuario inicia sesion con "<usuario>"
    Entonces la sesion local de "<usuario>" conserva su navegador
    Ejemplos:
      | usuario |
      | local1 |
      | local2 |
      | local3 |
      | local4 |
      | local5 |
      | local6 |
