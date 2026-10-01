# Pruebas manuales de Parking Coto

Guía para probar todo el sistema a mano, en la interfaz. Incluye los casos del enunciado y otros que no pide, pero que dan consistencia al proyecto. Todos los pasos de esta guía se ejecutaron en un recorrido automático de la interfaz (77 revisiones, 0 fallas) en **Java 8 con JavaFX 8** y en **Java 21 con JavaFX 21**; los resultados esperados son los que se obtuvieron.

Para abrir la aplicación: [README](../README.md#cómo-ejecutar).

## 0. Preparación

1. Abrir la aplicación y entrar a **Vehículos → Cargar datos de ejemplo**.
2. Quedan 7 espacios (1 y 2 motocicleta; 3, 4 y 5 automóvil; 6 y 7 carga) y 7 vehículos (`ABC123`, `DEF456`, `GHI789` automóviles; `MOT111`, `MOT222` motocicletas; `CAR999`, `CAR888` carga).

## 1. Casos del enunciado en un clic

**Casos de prueba → Ejecutar casos de prueba**: debe decir **19 de 19 casos correctos** (los 15 obligatorios y 4 adicionales). El detalle está en [tabla-pruebas.md](tabla-pruebas.md).

## 2. Flujo normal, de principio a fin

| Paso | Qué hacer | Qué debe pasar |
|---|---|---|
| 1 | **Registrar ingreso**: vehículo `ABC123`, fecha 01/10/2026, hora 08:00 | Aviso verde. Ticket #1 en el espacio 3 (el primer espacio de automóvil libre) |
| 2 | **Registrar salida**: `ABC123`, atajo **+61 min**, botón *Calcular monto a pagar* | Vista previa: 61 min, 2 horas cobradas, ₡1 800. El ticket sigue activo |
| 3 | Mismo vehículo, *Registrar salida* | Ticket cerrado, ₡1 800, el espacio 3 vuelve a estar disponible. Dice que falta registrar el pago |
| 4 | **Registrar pago**: elegir el ticket, tipo *Efectivo* | Pago #1. Ingresos totales: ₡1 800 |
| 5 | **Dashboard** | Ingresos ₡1 800, 0 tickets activos, sin espacios ocupados |

## 3. Datos inválidos y rechazos, pantalla por pantalla

### Vehículos

| Qué hacer | Qué debe pasar |
|---|---|
| Registrar con la placa `" xyz789 "` (con espacios y minúsculas) | Se guarda como `XYZ789` y el formulario se limpia |
| Registrar otra vez la placa `xyz789` | `DuplicateRecordException`; no se duplica |
| Dejar la placa vacía | `ParkingException` |
| No elegir el tipo | "Datos incorrectos" |
| Pulsar *Cargar datos de ejemplo* por segunda vez | `DuplicateRecordException`; no se duplican los datos |

### Espacios

| Qué hacer | Qué debe pasar |
|---|---|
| Número `abc` | "Datos incorrectos" |
| Número `-4` | `ParkingException` |
| Número `12`, tipo carga | Se registra |
| Registrar otra vez el `12` | `DuplicateRecordException` |
| *Fuera de servicio* sin elegir un espacio de la tabla | "Datos incorrectos" |
| Elegir el espacio 12 y *Fuera de servicio* | La tabla muestra "Fuera de servicio" |
| *Restaurar* y volver a pulsar *Restaurar* | La primera lo deja disponible; la segunda da `SpaceNotAvailableException` |
| Poner fuera de servicio un espacio ocupado | `SpaceNotAvailableException` |

### Registrar ingreso

| Qué hacer | Qué debe pasar |
|---|---|
| No elegir vehículo | "Datos incorrectos" |
| Fecha `2026-10-01` | Mensaje de formato `dd/MM/aaaa`; no se crea ticket |
| Hora `25:99` o vacía | Mensaje de formato `HH:mm`; no se crea ticket |
| Fecha vacía | "Seleccione la fecha" |
| Fecha `1/10/2026` y hora `8:00` (sin ceros) | Se aceptan, igual que `01/10/2026` y `08:00` |
| El mismo vehículo ingresa otra vez | `ActiveTicketException` |
| Entrar `ABC123`, `DEF456` y `GHI789`, y después un cuarto automóvil | Ocupan los espacios 3, 4 y 5; el cuarto da `SpaceNotAvailableException` (no hay espacio libre) |
| Marcar *Elegir el espacio manualmente* sin elegir uno | "Datos incorrectos" |
| Elegir manualmente un espacio ocupado, uno fuera de servicio o uno de otro tipo | `SpaceNotAvailableException` y el vehículo no queda dentro |

### Registrar salida

| Qué hacer | Qué debe pasar |
|---|---|
| Un atajo (`+1 min`, etc.) sin elegir vehículo | "Datos incorrectos" |
| Un atajo con un vehículo que no está dentro | Mensaje de que no tiene ticket activo |
| *Registrar salida* de un vehículo que no está dentro | `RecordNotFoundException` |
| Hora de salida anterior a la entrada (07:59 con entrada 08:00) | `ParkingException`; el ticket sigue activo y el espacio sigue ocupado |
| *Calcular monto a pagar* | Muestra permanencia, horas y monto; no cierra el ticket |
| Salir dos veces con el mismo vehículo | La segunda da `RecordNotFoundException` |

Permanencias que conviene mostrar en vivo (entrada a las 08:00):

| Vehículo | Atajo | Monto esperado |
|---|---|---|
| Automóvil | +1 min | ₡900 |
| Automóvil | +60 min | ₡900 |
| Automóvil | +61 min | ₡1 800 |
| Automóvil | +11 h | ₡7 000 (tope diario) |
| Motocicleta | +25 h | ₡4 500 (un día completo ₡4 000 más 1 hora ₡500) |
| Carga | +2 h | ₡3 000 |
| Carga | +60 min | ₡1 500 |

### Registrar pago

| Qué hacer | Qué debe pasar |
|---|---|
| Pagar un ticket que sigue activo | `InvalidTicketStateException`; no se registra el pago |
| Pagar sin elegir ticket | "Datos incorrectos" |
| Pagar los tickets cerrados con *Efectivo*, *Tarjeta* y *SINPE Móvil* | Cada pago sale de la lista; los ingresos suben por el monto del ticket |

## 4. Consistencia entre pantallas

Después de las salidas y pagos de las tablas anteriores (seis tickets pagados por ₡18 700 en total, y `ABC123` dentro otra vez):

| Pantalla | Debe mostrar |
|---|---|
| Dashboard | 8 espacios, 1 ocupado, 7 disponibles, 1 ticket activo, ingresos ₡18 700 |
| Tickets | 1 activo y 7 en el historial |
| Consultas y reportes | 1 vehículo dentro, 7 espacios disponibles, 6 pagos |
| Actividad reciente | Una línea por cada operación, aceptada (✔) o rechazada (✘) |

Cambiar de pantalla con el menú lateral siempre muestra una sola pantalla y recarga sus datos.

## 5. Pruebas automáticas que complementan estas

- **Simulación aleatoria** (`ParkingLotInvariantsTest`): 150 parqueos distintos con 250 operaciones al azar cada uno (unas 37 500 en total: ingresos, salidas con horas en los límites, pagos, espacios fuera de servicio, registros repetidos). Después de **cada** operación comprueba que el estado sea coherente: espacio ocupado solo si tiene un ticket activo, un solo ticket activo por vehículo, ingresos igual a la suma de los pagos, y que el monto de cada ticket coincida con la regla del enunciado calculada de forma independiente. Para confirmar que detecta fallos se le inyectaron dos errores a propósito (tope desde 11 horas en vez de 10, y no liberar el espacio al cerrar) y los atrapó.
- **`ParkingLotRulesTest`**: rechazos y límites de las reglas.
- **`mvn test`**: 62 pruebas en total.

## 6. Compatibilidad

| Entorno | Resultado |
|---|---|
| Java 8 de Oracle (JavaFX 8 incluido) | Compila, 62 pruebas pasan, la ventana abre sin avisos |
| Java 21 (JavaFX 21 por Maven) | Igual |
| Ventana en su tamaño mínimo (1260 × 640) | Todo se ve completo |
| Modo consola (`--console`, `--markdown`) en Java 8 y 21 | 19 de 19 casos correctos |

## 7. Comportamiento que conviene saber explicar

- Con el tope diario, **9 horas de un automóvil cuestan ₡8 100 y 10 horas cuestan ₡7 000**. Es el resultado de aplicar literalmente "para permanencias de 10 horas o más".
- Toda fracción de hora cuenta como hora completa, incluso 1 hora y 1 segundo.
- El espacio se libera al registrar la salida, antes del pago. Un ticket cerrado y sin pagar no cuenta como vehículo dentro del parqueo.
