# Informe del proyecto Parking Coto

## 1. Portada
Proyecto 2 - EIF400
Paradigmas de Programación
Sistema de gestión de parqueo privado orientado a objetos

## 2. Descripción del problema
Parking Coto necesita controlar vehículos, espacios, ingresos, tickets, salidas, cálculo de permanencia y pagos. El sistema debe permitir registrar vehículos, asignar espacios, cobrar por hora y mantener el historial de ingresos sin mezclar toda la lógica en una sola clase.

## 3. Análisis de requisitos
Se analizaron las reglas de negocio del enunciado y se identificaron los elementos clave:
- vehículos con tipos distintos
- espacios con estados y tipos compatibles
- tickets activos y cerrados
- pagos con tipos definidos
- cálculo de tarifa por hora con tope diario
- control de ocupación y disponibilidad

## 4. Modelo UML
El diagrama de clases del sistema quedó almacenado en NetBeans en:
[ParkingCotoUML/Class Diagrams/ParkingCoto.cdg](../ParkingCotoUML/Class%20Diagrams/ParkingCoto.cdg)

El modelo principal incluye estas relaciones:
- Vehicle es una clase abstracta y tiene subclases concretas: Car, Motorcycle y FreightVehicle.
- ParkingSpace representa cada espacio del parqueo y conoce su estado y tipo.
- ParkingTicket asocia un vehicle con un espacio y controla el estado del ticket.
- Payment registra el pago realizado por cada ticket.
- ParkingLot coordina las colecciones y las operaciones del sistema.

## 5. Justificación de decisiones de diseño
Se decidió separar responsabilidades para cumplir con la pregunta rectora del proyecto: cada objeto resuelve lo que le corresponde.

- Vehicle encapsula los datos comunes del vehículo y delega el cálculo de tarifa al polimorfismo.
- ParkingSpace gestiona su propio estado y compatibilidad con el tipo de vehículo.
- ParkingTicket controla la permanencia, el monto y la transición entre estados.
- ParkingLot mantiene las colecciones y valida el flujo del negocio.

Esto permite que una nueva regla o un nuevo tipo de vehículo se integre sin reescribir lógica dispersa en varios lugares.

## 6. Herencia y polimorfismo
La abstracción común está en Vehicle. Cada clase concreta define su comportamiento específico:
- Car usa tarifa de automóvil.
- Motorcycle usa tarifa de motocicleta.
- FreightVehicle usa tarifa de carga.

La lógica del cálculo no se decide por condiciones manuales sobre el tipo. En cambio, cada vehículo ejecuta su propio método calculateFee, lo que demuestra polimorfismo real.

## 7. Reglas de negocio
Las reglas clave se ubican en las clases apropiadas:
- ParkingLot valida entrada, duplicados y disponibilidad
- ParkingSpace valida ocupación y compatibilidad
- ParkingTicket valida cierre y pago
- Tariff concentra la política de precios y el tope diario

Se mantienen los principios de encapsulamiento y responsabilidad única.

## 8. Pruebas
Se validaron los escenarios de negocio principales con JUnit. Las pruebas cubren ingreso, ocupación, incompatibilidad, ticket activo, cálculo de tarifa, pago y liberación del espacio.

## 9. Conclusiones individuales
### Integrante 1
El proyecto me permitió comprender que un sistema real de parqueo no debe resolverse con lógica dispersa ni con condicionales largos. La clave fue distribuir responsabilidades entre clases y hacer que cada una controlara su propio estado. Esto fue especialmente útil al diseñar Vehicle, ParkingSpace y ParkingTicket. La parte más importante del aprendizaje fue ver cómo la tarifa y la regla del tope diario pueden centralizarse para que el sistema evolucione sin romper otras clases.

### Integrante 2
Durante el desarrollo, la mayor fortaleza del diseño fue la separación clara entre dominio y coordinación. ParkingLot hizo el trabajo de orquestación, mientras que las entidades del negocio conservaron su lógica. Entender la diferencia entre los estados de espacio y ticket fue crucial para evitar errores de flujo. El proyecto reforzó la idea de que la programación orientada a objetos debe facilitar cambios y extensiones, no solo cumplir con una funcionalidad puntual.

## 10. Conclusión final
El sistema queda estructurado como una solución orientada a objetos, con reglas de negocio bien distribuidas y un enfoque reutilizable para nuevas tarifas o tipos de vehículo. El diseño es compatible con la pregunta rectora del proyecto porque permite extender el comportamiento sin reescribir la aplicación completa.
