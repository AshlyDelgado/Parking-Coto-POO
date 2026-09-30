# Parking Coto

Parking Coto is an object-oriented Java project designed to manage a private parking lot, including vehicle admission, occupancy control, ticket generation, payments, and billing.

## Overview
The system follows object-oriented programming principles and distributes responsibilities among domain classes instead of concentrating business logic in a single class. It includes vehicle management, parking space validation, ticket handling, pricing calculations, and payment tracking.

## Project structure

```text
Parking-Coto-POO/
├── README.md
├── docs/
│   ├── InformeProyecto.md
│   └── tabla-pruebas.md
├── ParkingCotoUML/
│   ├── Class Diagrams/
│   │   └── ParkingCoto.cdg
│   └── umlproject/
└── parking-coto/
    ├── pom.xml
    └── src/
        ├── main/java/cr/ac/una/parking/coto
        └── test/java/cr/ac/una/parking/coto
```

## Requirements
- Java 8 or newer
- Maven 3.9 or newer
- NetBeans (recommended) to open the UML diagram

## Main packages
- `cr.ac.una.parking.coto.model` — domain entities such as vehicles, spaces, tickets, and payments
- `cr.ac.una.parking.coto.enums` — enumeration types for vehicle, space, ticket, and payment states
- `cr.ac.una.parking.coto.exception` — custom exceptions for business rule violations
- `cr.ac.una.parking.coto.pricing` — tariff policies and pricing logic
- `cr.ac.una.parking.coto` — parking coordinator and main entry point

## Features implemented
- Vehicle registration
- Parking space registration
- Automatic assignment of compatible spaces
- Entry/exit flow with ticket control
- Hour-based billing and daily cap
- Payment registration
- Occupancy and income queries
- Validation of business rules through exceptions
- Unit tests covering core scenarios

## Business rules covered
- A vehicle cannot have two active tickets
- A space cannot be assigned while occupied or out of service
- A vehicle must use a compatible space type
- A ticket cannot be paid while still active
- Billing is computed from the real vehicle type through polymorphism
- Daily cap logic is centralized in the pricing policy

## Running the project
### 1. Compile and test
From the project root:

```bash
cd parking-coto
mvn test
```

### 2. Run the sample entry point

```bash
cd parking-coto
mvn exec:java -Dexec.mainClass=cr.ac.una.parking.coto.ParkingCoto
```

## UML diagram
The class diagram is included in the repository and can be opened in NetBeans:

[ParkingCotoUML/Class Diagrams/ParkingCoto.cdg](ParkingCotoUML/Class%20Diagrams/ParkingCoto.cdg)

## Testing
The project includes JUnit tests covering the main parking rules and edge cases.
Current verification status:
- 16 tests executed
- 0 failures
- 0 errors
- Build success

## Documentation
Additional project documentation is available in:
- [docs/InformeProyecto.md](docs/InformeProyecto.md)
- [docs/tabla-pruebas.md](docs/tabla-pruebas.md)

## License
This project is intended for academic use in the course EIF400 - Programming Paradigms.

