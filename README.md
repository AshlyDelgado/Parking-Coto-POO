# Parking-Coto-POO

Sistema de gestión de parqueo privado orientado a objetos en Java.

## Descripción del proyecto
Este proyecto implementa un sistema para controlar ingresos, permanencia, cobro, pagos y salida de vehículos en un parqueo privado, respetando reglas de negocio y un diseño orientado a objetos.

## Requisitos
- Java 8 o superior
- Maven 3.9 o superior
- NetBeans para visualizar el diagrama UML

## Estructura del repositorio
- [parking-coto](parking-coto): proyecto Maven con la lógica del dominio
- [ParkingCotoUML/Class Diagrams/ParkingCoto.cdg](ParkingCotoUML/Class%20Diagrams/ParkingCoto.cdg): diagrama UML del sistema
- [docs](docs): documentación y tabla de pruebas

## Ejecutar pruebas
Desde la carpeta del proyecto Maven:

```bash
cd parking-coto
mvn test
```

## Ejecutar la aplicación de ejemplo

```bash
cd parking-coto
mvn exec:java -Dexec.mainClass=cr.ac.una.parking.coto.ParkingCoto
```

## Paquetes principales
- model: vehículos, espacios, tickets y pagos
- enums: tipos y estados del dominio
- exception: excepciones para las reglas de negocio
- pricing: políticas de tarifa y tope diario
- ParkingLot: coordinación del flujo del sistema

## Principios aplicados
- Abstracción y encapsulamiento
- Herencia y polimorfismo
- Composición y asociaciones entre objetos
- Cálculo de tarifas centralizado en la política de precios
- Reglas de negocio distribuidas en las clases responsables

## UML
El diagrama de clases ya está disponible en NetBeans en:
[ParkingCotoUML/Class Diagrams/ParkingCoto.cdg](ParkingCotoUML/Class%20Diagrams/ParkingCoto.cdg)

## Estado actual
La base del dominio, la lógica de negocio y la validación con pruebas JUnit ya quedaron implementadas y verificadas.

## Documentación adicional
- [docs/InformeProyecto.md](docs/InformeProyecto.md)
- [docs/tabla-pruebas.md](docs/tabla-pruebas.md)

