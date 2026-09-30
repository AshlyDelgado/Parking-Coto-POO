# Parking-Coto-POO

Sistema de parqueo privado orientado a objetos en Java.

## Requisitos
- Java 8+
- Maven 3.9+

## Ejecutar pruebas
En la carpeta del proyecto:

```bash
cd parking-coto
mvn test
```

## Ejecutar la aplicación de ejemplo

```bash
cd parking-coto
mvn exec:java -Dexec.mainClass=cr.ac.una.parking.coto.ParkingCoto
```

## Estructura principal
- `model`: vehículos, espacios, tickets y pagos
- `enums`: tipos y estados del dominio
- `exception`: excepciones de reglas de negocio
- `pricing`: políticas de tarifa
- `ParkingLot`: gestor del flujo del parqueo

## Estado actual
La base del dominio y la lógica principal del parqueo ya está implementada y validada con pruebas JUnit.

