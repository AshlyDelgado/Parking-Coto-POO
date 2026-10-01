# Tabla de pruebas del proyecto Parking Coto

Los resultados de esta tabla **no están escritos a mano**: se generan ejecutando cada caso contra las clases reales del sistema, sobre un parqueo nuevo en cada caso. Se pueden reproducir de tres formas:

- en la interfaz, pantalla **Casos de prueba**, botón *Ejecutar casos de prueba*;
- en consola: `java -cp target/classes cr.ac.una.parking.coto.ParkingCoto --console`;
- como tabla Markdown (la de abajo): `java -cp target/classes cr.ac.una.parking.coto.ParkingCoto --markdown`.

Los casos 1 a 15 son los obligatorios del enunciado (sección 12). Los casos 16 a 19 son reglas adicionales de la sección 8 y del requerimiento del tope diario.

## Casos del enunciado y reglas adicionales

| # | Tipo | Caso | Entrada | Resultado esperado | Resultado obtenido | Veredicto |
|---|---|---|---|---|---|---|
| 1 | Obligatorio | Ingreso correcto de un automóvil | El automóvil ABC123 ingresa a las 08:00 con espacios libres de todos los tipos | Ticket Activo en espacio 1 (Automóvil), espacio Ocupado | Ticket Activo en espacio 1 (Automóvil), espacio Ocupado | Correcto |
| 2 | Obligatorio | Ingreso correcto de una motocicleta | La motocicleta MOT111 ingresa a las 08:00 con espacios libres de todos los tipos | Ticket Activo en espacio 2 (Motocicleta), espacio Ocupado | Ticket Activo en espacio 2 (Motocicleta), espacio Ocupado | Correcto |
| 3 | Obligatorio | Ingreso correcto de un vehículo de carga | El vehículo de carga CAR999 ingresa a las 08:00 con espacios libres de todos los tipos | Ticket Activo en espacio 3 (Carga), espacio Ocupado | Ticket Activo en espacio 3 (Carga), espacio Ocupado | Correcto |
| 4 | Obligatorio | Intento de asignar un espacio ocupado | Un automóvil ocupa el espacio 1 y otro automóvil intenta usar ese mismo espacio | SpaceNotAvailableException | SpaceNotAvailableException: El espacio no está disponible | Correcto |
| 5 | Obligatorio | Intento de asignar un espacio fuera de servicio | El espacio 4 se pone fuera de servicio y un automóvil intenta usarlo | SpaceNotAvailableException | SpaceNotAvailableException: El espacio no está disponible | Correcto |
| 6 | Obligatorio | Intento de asignar un espacio incompatible | Un automóvil intenta usar el espacio 2, que es para motocicletas | SpaceNotAvailableException | SpaceNotAvailableException: El espacio no es compatible con el vehículo | Correcto |
| 7 | Obligatorio | Intento de ingresar un vehículo que ya tiene ticket activo | El automóvil ABC123 ingresa y vuelve a intentar ingresar sin haber salido | ActiveTicketException | ActiveTicketException: El vehículo ya tiene un ticket activo | Correcto |
| 8 | Obligatorio | Permanencia de 1 minuto | Automóvil: entra a las 08:00 y sale a las 08:01 | Horas cobradas: 1, monto: ₡900 | Horas cobradas: 1, monto: ₡900 | Correcto |
| 9 | Obligatorio | Permanencia de 60 minutos | Automóvil: entra a las 08:00 y sale a las 09:00 | Horas cobradas: 1, monto: ₡900 | Horas cobradas: 1, monto: ₡900 | Correcto |
| 10 | Obligatorio | Permanencia de 61 minutos | Automóvil: entra a las 08:00 y sale a las 09:01 | Horas cobradas: 2, monto: ₡1 800 | Horas cobradas: 2, monto: ₡1 800 | Correcto |
| 11 | Obligatorio | Permanencia de más de 10 horas con tarifa máxima | Automóvil: entra a las 08:00 y sale 11 horas después (sin tope serían ₡9 900) | Horas cobradas: 11, monto: ₡7 000 | Horas cobradas: 11, monto: ₡7 000 | Correcto |
| 12 | Obligatorio | Cierre correcto del ticket | Un automóvil registra su salida 2 horas después de entrar | Ticket Cerrado, hora de salida registrada, monto ₡1 800 | Ticket Cerrado, hora de salida registrada, monto ₡1 800 | Correcto |
| 13 | Obligatorio | Pago correcto | Se paga en efectivo el ticket cerrado de un automóvil que estuvo 2 horas | Ticket Pagado, pago de ₡1 800 con Efectivo | Ticket Pagado, pago de ₡1 800 con Efectivo | Correcto |
| 14 | Obligatorio | Liberación del espacio | Un automóvil ocupa el espacio 1 y después registra su salida | Espacio 1: Ocupado al entrar, Disponible al salir | Espacio 1: Ocupado al entrar, Disponible al salir | Correcto |
| 15 | Obligatorio | Cálculo de ingresos totales | Se pagan un automóvil (1 hora, ₡900) y una motocicleta (1 hora, ₡500) | Ingresos totales: ₡1 400 | Ingresos totales: ₡1 400 | Correcto |
| 16 | Adicional | Salida sin ticket activo | Un automóvil registrado que nunca ingresó intenta registrar su salida | RecordNotFoundException | RecordNotFoundException: No existe un ticket activo para ese vehículo | Correcto |
| 17 | Adicional | Pago de un ticket que sigue activo | Un automóvil está dentro del parqueo y se intenta pagar su ticket | InvalidTicketStateException | InvalidTicketStateException: No se puede pagar un ticket activo | Correcto |
| 18 | Adicional | Permanencia de más de 24 horas | Automóvil: entra a las 08:00 y sale 25 horas después (un día completo más 1 hora) | Horas cobradas: 25, monto: ₡7 900 | Horas cobradas: 25, monto: ₡7 900 | Correcto |
| 19 | Adicional | Monto según el tipo real del vehículo | Una motocicleta, un automóvil y un vehículo de carga permanecen 2 horas cada uno | Motocicleta ₡1 000, Automóvil ₡1 800, Vehículo de carga ₡3 000 | Motocicleta ₡1 000, Automóvil ₡1 800, Vehículo de carga ₡3 000 | Correcto |

## Pruebas automáticas (JUnit)

Además de los casos anteriores, el proyecto tiene pruebas JUnit que se ejecutan con `mvn test` (o con *Test* en NetBeans).

| Clase de prueba | Pruebas | Qué cubre |
|---|---|---|
| `ParkingLotTest` | 23 | Ingresos por tipo de vehículo, espacio ocupado, fuera de servicio e incompatible, ticket activo duplicado, salida sin ticket, permanencias de 1, 60 y 61 minutos, tope diario por tipo de vehículo, cierre, pago, pago de un ticket activo, liberación del espacio, ingresos totales, vehículos dentro, ocupación por tipo y hora de entrada nula |
| `ParkingLotServiceTest` | 6 | Espacios ordenados por número, asignación del espacio compatible más bajo, poner un espacio fuera de servicio y restaurarlo, historial de solo lectura |
| `TariffTest` | 6 | Tarifa por hora, tope diario desde 10 horas, tope por cada período de 24 horas (25, 34, 48 y 49 horas), redondeo de cualquier fracción de hora, rechazo de horas de salida inválidas |
| `ScenarioRunnerTest` | 3 | Los 19 casos de la tabla anterior pasan, 15 son obligatorios y están numerados en orden |
| **Total** | **38** | **0 fallos, 0 errores** |

## Observaciones

- Toda fracción de hora se cobra como hora completa, incluso si son segundos: 1 hora y 30 segundos se cobra como 2 horas.
- El tope diario se aplica cuando las horas cobradas llegan a 10, y cada período de 24 horas cuesta como máximo el tope. Por eso 9 horas de un automóvil cuestan ₡8 100 y 10 horas cuestan ₡7 000: es lo que resulta de aplicar literalmente la regla «para permanencias de 10 horas o más».
- El espacio se libera al registrar la salida (ticket cerrado), antes del pago. Un ticket cerrado todavía no pagado no cuenta como vehículo dentro del parqueo.
- Las excepciones de negocio (`SpaceNotAvailableException`, `ActiveTicketException`, `InvalidTicketStateException`, `RecordNotFoundException`, `DuplicateRecordException`) heredan de `ParkingException`; en los casos que esperan una excepción, el resultado obtenido muestra el nombre de la que lanzó el sistema y su mensaje.
