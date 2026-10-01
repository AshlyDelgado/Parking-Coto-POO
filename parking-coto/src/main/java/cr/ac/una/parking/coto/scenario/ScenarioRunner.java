package cr.ac.una.parking.coto.scenario;

import cr.ac.una.parking.coto.ParkingLot;
import cr.ac.una.parking.coto.enums.PaymentType;
import cr.ac.una.parking.coto.enums.SpaceType;
import cr.ac.una.parking.coto.exception.ParkingException;
import cr.ac.una.parking.coto.model.Car;
import cr.ac.una.parking.coto.model.FreightVehicle;
import cr.ac.una.parking.coto.model.Motorcycle;
import cr.ac.una.parking.coto.model.ParkingSpace;
import cr.ac.una.parking.coto.model.ParkingTicket;
import cr.ac.una.parking.coto.model.Payment;
import cr.ac.una.parking.coto.model.Vehicle;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

/**
 * Runs the demonstration scenarios of the parking system.
 *
 * <p>It executes the fifteen cases required by the assignment, plus a few
 * additional business rules, each one on a brand-new parking lot. The result
 * of every case compares what the rules require with what the real domain
 * classes produced, so the output can be shown live or copied into the test
 * table of the report. This class contains no user-interface code.</p>
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.0
 */
public class ScenarioRunner {

    /**
     * Creates a runner that has not executed any scenario yet.
     */
    public ScenarioRunner() {
    }

    /** Category of the cases required by the assignment. */
    private static final String REQUIRED = "Obligatorio";
    /** Category of the extra business rules. */
    private static final String EXTRA = "Adicional";
    /** Moment at which every scenario starts. */
    private static final LocalDateTime START = LocalDateTime.of(2026, 10, 1, 8, 0);

    /** Number given to the next scenario of the current execution. */
    private int nextNumber;

    /**
     * Executes every scenario in order.
     *
     * @return one result per scenario, numbered from one
     */
    public List<ScenarioResult> runAll() {
        nextNumber = 1;
        List<ScenarioResult> results = new ArrayList<ScenarioResult>();
        results.add(carEntry());
        results.add(motorcycleEntry());
        results.add(freightEntry());
        results.add(occupiedSpace());
        results.add(outOfServiceSpace());
        results.add(incompatibleSpace());
        results.add(duplicateActiveTicket());
        results.add(stayOfOneMinute());
        results.add(stayOfSixtyMinutes());
        results.add(stayOfSixtyOneMinutes());
        results.add(stayBeyondTenHours());
        results.add(ticketClosing());
        results.add(payment());
        results.add(spaceRelease());
        results.add(totalIncome());
        results.add(exitWithoutTicket());
        results.add(paymentOfActiveTicket());
        results.add(stayBeyondOneDay());
        results.add(rateByRealVehicleType());
        return results;
    }

    private ScenarioResult carEntry() {
        return run(REQUIRED, "Ingreso correcto de un automóvil",
                "El automóvil ABC123 ingresa a las 08:00 con espacios libres de todos los tipos",
                "Ticket Activo en espacio 1 (Automóvil), espacio Ocupado",
                () -> {
                    ParkingLot lot = newLot();
                    Vehicle car = register(lot, new Car("ABC123", "Toyota", "Corolla", "Blanco"));
                    return describeEntry(lot.registerEntry(car, START));
                });
    }

    private ScenarioResult motorcycleEntry() {
        return run(REQUIRED, "Ingreso correcto de una motocicleta",
                "La motocicleta MOT111 ingresa a las 08:00 con espacios libres de todos los tipos",
                "Ticket Activo en espacio 2 (Motocicleta), espacio Ocupado",
                () -> {
                    ParkingLot lot = newLot();
                    Vehicle bike = register(lot, new Motorcycle("MOT111", "Honda", "CBR", "Negro"));
                    return describeEntry(lot.registerEntry(bike, START));
                });
    }

    private ScenarioResult freightEntry() {
        return run(REQUIRED, "Ingreso correcto de un vehículo de carga",
                "El vehículo de carga CAR999 ingresa a las 08:00 con espacios libres de todos los tipos",
                "Ticket Activo en espacio 3 (Carga), espacio Ocupado",
                () -> {
                    ParkingLot lot = newLot();
                    Vehicle truck = register(lot, new FreightVehicle("CAR999", "Isuzu", "NPR", "Azul"));
                    return describeEntry(lot.registerEntry(truck, START));
                });
    }

    private ScenarioResult occupiedSpace() {
        return run(REQUIRED, "Intento de asignar un espacio ocupado",
                "Un automóvil ocupa el espacio 1 y otro automóvil intenta usar ese mismo espacio",
                "SpaceNotAvailableException",
                () -> {
                    ParkingLot lot = newLot();
                    Vehicle first = register(lot, new Car("ABC123", "Toyota", "Corolla", "Blanco"));
                    Vehicle second = register(lot, new Car("DEF456", "Nissan", "Sentra", "Gris"));
                    ParkingSpace space = lot.registerEntry(first, START).getSpace();
                    return describeEntry(lot.registerEntry(second, space, START.plusMinutes(5)));
                });
    }

    private ScenarioResult outOfServiceSpace() {
        return run(REQUIRED, "Intento de asignar un espacio fuera de servicio",
                "El espacio 4 se pone fuera de servicio y un automóvil intenta usarlo",
                "SpaceNotAvailableException",
                () -> {
                    ParkingLot lot = newLot();
                    Vehicle car = register(lot, new Car("ABC123", "Toyota", "Corolla", "Blanco"));
                    lot.putSpaceOutOfService(4);
                    return describeEntry(lot.registerEntry(car, spaceNumber(lot, 4), START));
                });
    }

    private ScenarioResult incompatibleSpace() {
        return run(REQUIRED, "Intento de asignar un espacio incompatible",
                "Un automóvil intenta usar el espacio 2, que es para motocicletas",
                "SpaceNotAvailableException",
                () -> {
                    ParkingLot lot = newLot();
                    Vehicle car = register(lot, new Car("ABC123", "Toyota", "Corolla", "Blanco"));
                    return describeEntry(lot.registerEntry(car, spaceNumber(lot, 2), START));
                });
    }

    private ScenarioResult duplicateActiveTicket() {
        return run(REQUIRED, "Intento de ingresar un vehículo que ya tiene ticket activo",
                "El automóvil ABC123 ingresa y vuelve a intentar ingresar sin haber salido",
                "ActiveTicketException",
                () -> {
                    ParkingLot lot = newLot();
                    Vehicle car = register(lot, new Car("ABC123", "Toyota", "Corolla", "Blanco"));
                    lot.registerEntry(car, START);
                    return describeEntry(lot.registerEntry(car, START.plusMinutes(10)));
                });
    }

    private ScenarioResult stayOfOneMinute() {
        return run(REQUIRED, "Permanencia de 1 minuto",
                "Automóvil: entra a las 08:00 y sale a las 08:01",
                "Horas cobradas: 1, monto: ₡900",
                () -> describeStay(stay(new Car("ABC123", "Toyota", "Corolla", "Blanco"), 1)));
    }

    private ScenarioResult stayOfSixtyMinutes() {
        return run(REQUIRED, "Permanencia de 60 minutos",
                "Automóvil: entra a las 08:00 y sale a las 09:00",
                "Horas cobradas: 1, monto: ₡900",
                () -> describeStay(stay(new Car("ABC123", "Toyota", "Corolla", "Blanco"), 60)));
    }

    private ScenarioResult stayOfSixtyOneMinutes() {
        return run(REQUIRED, "Permanencia de 61 minutos",
                "Automóvil: entra a las 08:00 y sale a las 09:01",
                "Horas cobradas: 2, monto: ₡1 800",
                () -> describeStay(stay(new Car("ABC123", "Toyota", "Corolla", "Blanco"), 61)));
    }

    private ScenarioResult stayBeyondTenHours() {
        return run(REQUIRED, "Permanencia de más de 10 horas con tarifa máxima",
                "Automóvil: entra a las 08:00 y sale 11 horas después (sin tope serían ₡9 900)",
                "Horas cobradas: 11, monto: ₡7 000",
                () -> describeStay(stay(new Car("ABC123", "Toyota", "Corolla", "Blanco"), 11 * 60)));
    }

    private ScenarioResult ticketClosing() {
        return run(REQUIRED, "Cierre correcto del ticket",
                "Un automóvil registra su salida 2 horas después de entrar",
                "Ticket Cerrado, hora de salida registrada, monto ₡1 800",
                () -> {
                    ParkingTicket ticket = stay(new Car("ABC123", "Toyota", "Corolla", "Blanco"), 120);
                    String exit = ticket.getExitTime() == null ? "sin hora de salida" : "hora de salida registrada";
                    return "Ticket " + ticket.getStatus().getDisplayName() + ", " + exit
                            + ", monto " + money(ticket.getAmount());
                });
    }

    private ScenarioResult payment() {
        return run(REQUIRED, "Pago correcto",
                "Se paga en efectivo el ticket cerrado de un automóvil que estuvo 2 horas",
                "Ticket Pagado, pago de ₡1 800 con Efectivo",
                () -> {
                    ParkingLot lot = newLot();
                    Vehicle car = register(lot, new Car("ABC123", "Toyota", "Corolla", "Blanco"));
                    lot.registerEntry(car, START);
                    ParkingTicket ticket = lot.registerExit(car, START.plusHours(2));
                    Payment payment = lot.registerPayment(ticket, PaymentType.CASH, START.plusHours(2));
                    return "Ticket " + ticket.getStatus().getDisplayName() + ", pago de "
                            + money(payment.getAmount()) + " con " + payment.getType().getDisplayName();
                });
    }

    private ScenarioResult spaceRelease() {
        return run(REQUIRED, "Liberación del espacio",
                "Un automóvil ocupa el espacio 1 y después registra su salida",
                "Espacio 1: Ocupado al entrar, Disponible al salir",
                () -> {
                    ParkingLot lot = newLot();
                    Vehicle car = register(lot, new Car("ABC123", "Toyota", "Corolla", "Blanco"));
                    ParkingSpace space = lot.registerEntry(car, START).getSpace();
                    String before = space.getStatus().getDisplayName();
                    lot.registerExit(car, START.plusHours(1));
                    return "Espacio " + space.getNumber() + ": " + before + " al entrar, "
                            + space.getStatus().getDisplayName() + " al salir";
                });
    }

    private ScenarioResult totalIncome() {
        return run(REQUIRED, "Cálculo de ingresos totales",
                "Se pagan un automóvil (1 hora, ₡900) y una motocicleta (1 hora, ₡500)",
                "Ingresos totales: ₡1 400",
                () -> {
                    ParkingLot lot = newLot();
                    Vehicle car = register(lot, new Car("ABC123", "Toyota", "Corolla", "Blanco"));
                    Vehicle bike = register(lot, new Motorcycle("MOT111", "Honda", "CBR", "Negro"));
                    lot.registerEntry(car, START);
                    lot.registerEntry(bike, START);
                    ParkingTicket carTicket = lot.registerExit(car, START.plusHours(1));
                    ParkingTicket bikeTicket = lot.registerExit(bike, START.plusHours(1));
                    lot.registerPayment(carTicket, PaymentType.CARD, START.plusHours(1));
                    lot.registerPayment(bikeTicket, PaymentType.SINPE_MOVIL, START.plusHours(1));
                    return "Ingresos totales: " + money(lot.getTotalIncome());
                });
    }

    private ScenarioResult exitWithoutTicket() {
        return run(EXTRA, "Salida sin ticket activo",
                "Un automóvil registrado que nunca ingresó intenta registrar su salida",
                "RecordNotFoundException",
                () -> {
                    ParkingLot lot = newLot();
                    Vehicle car = register(lot, new Car("ABC123", "Toyota", "Corolla", "Blanco"));
                    return describeStay(lot.registerExit(car, START));
                });
    }

    private ScenarioResult paymentOfActiveTicket() {
        return run(EXTRA, "Pago de un ticket que sigue activo",
                "Un automóvil está dentro del parqueo y se intenta pagar su ticket",
                "InvalidTicketStateException",
                () -> {
                    ParkingLot lot = newLot();
                    Vehicle car = register(lot, new Car("ABC123", "Toyota", "Corolla", "Blanco"));
                    ParkingTicket ticket = lot.registerEntry(car, START);
                    return describePayment(lot.registerPayment(ticket, PaymentType.CASH, START.plusHours(1)));
                });
    }

    private ScenarioResult stayBeyondOneDay() {
        return run(EXTRA, "Permanencia de más de 24 horas",
                "Automóvil: entra a las 08:00 y sale 25 horas después (un día completo más 1 hora)",
                "Horas cobradas: 25, monto: ₡7 900",
                () -> describeStay(stay(new Car("ABC123", "Toyota", "Corolla", "Blanco"), 25 * 60)));
    }

    private ScenarioResult rateByRealVehicleType() {
        return run(EXTRA, "Monto según el tipo real del vehículo",
                "Una motocicleta, un automóvil y un vehículo de carga permanecen 2 horas cada uno",
                "Motocicleta ₡1 000, Automóvil ₡1 800, Vehículo de carga ₡3 000",
                () -> {
                    ParkingLot lot = newLot();
                    List<Vehicle> vehicles = new ArrayList<Vehicle>();
                    vehicles.add(register(lot, new Motorcycle("MOT111", "Honda", "CBR", "Negro")));
                    vehicles.add(register(lot, new Car("ABC123", "Toyota", "Corolla", "Blanco")));
                    vehicles.add(register(lot, new FreightVehicle("CAR999", "Isuzu", "NPR", "Azul")));
                    StringBuilder result = new StringBuilder();
                    for (Vehicle vehicle : vehicles) {
                        lot.registerEntry(vehicle, START);
                        ParkingTicket ticket = lot.registerExit(vehicle, START.plusHours(2));
                        if (result.length() > 0) {
                            result.append(", ");
                        }
                        result.append(vehicle.getType().getDisplayName()).append(' ').append(money(ticket.getAmount()));
                    }
                    return result.toString();
                });
    }

    /**
     * Runs one scenario and compares what happened with what was expected.
     * Business-rule violations are not failures: they are the expected outcome
     * of several scenarios, so the exception name is reported as the result.
     */
    private ScenarioResult run(String category, String name, String input,
                               String expected, Callable<String> action) {
        String obtained;
        try {
            obtained = action.call();
        } catch (ParkingException e) {
            obtained = e.getClass().getSimpleName() + ": " + e.getMessage();
        } catch (Exception e) {
            obtained = "Error inesperado: " + e;
        }
        return new ScenarioResult(nextNumber++, category, name, input, expected,
                obtained, obtained.startsWith(expected));
    }

    /** Creates a clean parking lot with one space of each type and a second car space. */
    private ParkingLot newLot() {
        ParkingLot lot = new ParkingLot();
        lot.registerSpace(new ParkingSpace(1, SpaceType.CAR));
        lot.registerSpace(new ParkingSpace(2, SpaceType.MOTORCYCLE));
        lot.registerSpace(new ParkingSpace(3, SpaceType.FREIGHT));
        lot.registerSpace(new ParkingSpace(4, SpaceType.CAR));
        return lot;
    }

    /** Registers the vehicle in the parking lot and returns it. */
    private Vehicle register(ParkingLot lot, Vehicle vehicle) {
        lot.registerVehicle(vehicle);
        return vehicle;
    }

    /** Finds a registered space by its number. */
    private ParkingSpace spaceNumber(ParkingLot lot, int number) {
        for (ParkingSpace space : lot.getSpaces()) {
            if (space.getNumber() == number) {
                return space;
            }
        }
        throw new ParkingException("El espacio " + number + " no existe");
    }

    /** Enters and exits a vehicle on a new parking lot after the given minutes. */
    private ParkingTicket stay(Vehicle vehicle, int minutes) {
        ParkingLot lot = newLot();
        register(lot, vehicle);
        lot.registerEntry(vehicle, START);
        return lot.registerExit(vehicle, START.plusMinutes(minutes));
    }

    private String describeEntry(ParkingTicket ticket) {
        ParkingSpace space = ticket.getSpace();
        return "Ticket " + ticket.getStatus().getDisplayName() + " en espacio " + space.getNumber()
                + " (" + space.getType().getDisplayName() + "), espacio " + space.getStatus().getDisplayName();
    }

    private String describeStay(ParkingTicket ticket) {
        return "Horas cobradas: " + ticket.calculateChargedHours(ticket.getExitTime())
                + ", monto: " + money(ticket.getAmount());
    }

    private String describePayment(Payment payment) {
        return "Pago de " + money(payment.getAmount()) + " con " + payment.getType().getDisplayName();
    }

    /** Formats an amount like the assignment does, for example ₡1 800. */
    private String money(double amount) {
        return "₡" + String.format(Locale.ROOT, "%,.0f", amount).replace(',', ' ');
    }
}
