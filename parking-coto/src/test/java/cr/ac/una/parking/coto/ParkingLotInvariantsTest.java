package cr.ac.una.parking.coto;

import cr.ac.una.parking.coto.enums.PaymentType;
import cr.ac.una.parking.coto.enums.SpaceStatus;
import cr.ac.una.parking.coto.enums.SpaceType;
import cr.ac.una.parking.coto.enums.TicketStatus;
import cr.ac.una.parking.coto.enums.VehicleType;
import cr.ac.una.parking.coto.exception.DuplicateRecordException;
import cr.ac.una.parking.coto.exception.ParkingException;
import cr.ac.una.parking.coto.model.Car;
import cr.ac.una.parking.coto.model.FreightVehicle;
import cr.ac.una.parking.coto.model.Motorcycle;
import cr.ac.una.parking.coto.model.ParkingSpace;
import cr.ac.una.parking.coto.model.ParkingTicket;
import cr.ac.una.parking.coto.model.Payment;
import cr.ac.una.parking.coto.model.Vehicle;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Randomized consistency test.
 *
 * <p>It runs thousands of random operations (entries, exits, payments, spaces
 * out of service, duplicate records, invalid times) on many parking lots and,
 * after every single operation, checks the rules that must always be true. The
 * amount of every ticket is compared with an independent calculation of the
 * rule in the assignment. Seeds are fixed, so a failure can always be
 * reproduced.</p>
 */
class ParkingLotInvariantsTest {

    private static final LocalDateTime START = LocalDateTime.of(2026, 10, 1, 6, 0);
    private static final int SEEDS = 150;
    private static final int STEPS = 250;

    /** Hourly rate of each type, taken from the assignment. */
    private static final Map<VehicleType, Double> RATE = new EnumMap<VehicleType, Double>(VehicleType.class);
    /** Maximum amount per daily period of each type, taken from the assignment. */
    private static final Map<VehicleType, Double> CAP = new EnumMap<VehicleType, Double>(VehicleType.class);

    static {
        RATE.put(VehicleType.MOTORCYCLE, 500.0);
        RATE.put(VehicleType.CAR, 900.0);
        RATE.put(VehicleType.FREIGHT, 1500.0);
        CAP.put(VehicleType.MOTORCYCLE, 4000.0);
        CAP.put(VehicleType.CAR, 7000.0);
        CAP.put(VehicleType.FREIGHT, 11000.0);
    }

    private int entries;
    private int exits;
    private int payments;
    private int rejections;
    private int outOfServiceChanges;

    @Test
    void shouldKeepTheStateConsistentAfterThousandsOfRandomOperations() {
        for (long seed = 1; seed <= SEEDS; seed++) {
            simulate(seed);
        }
        // the simulation must really exercise the system, not just pass empty
        assertTrue(entries > 3000, "entradas: " + entries);
        assertTrue(exits > 2000, "salidas: " + exits);
        assertTrue(payments > 1000, "pagos: " + payments);
        assertTrue(rejections > 3000, "rechazos: " + rejections);
        assertTrue(outOfServiceChanges > 500, "cambios de servicio: " + outOfServiceChanges);
    }

    private void simulate(long seed) {
        Random random = new Random(seed);
        ParkingLot lot = new ParkingLot();
        int number = 1;
        for (SpaceType type : SpaceType.values()) {
            int quantity = 1 + random.nextInt(3);
            for (int i = 0; i < quantity; i++) {
                lot.registerSpace(new ParkingSpace(number++, type));
            }
        }
        List<Vehicle> vehicles = new ArrayList<Vehicle>();
        int vehicleCount = 4 + random.nextInt(7);
        for (int i = 0; i < vehicleCount; i++) {
            Vehicle vehicle = newVehicle(random.nextInt(3), "P" + seed + "-" + i);
            vehicles.add(vehicle);
            lot.registerVehicle(vehicle);
        }

        LocalDateTime clock = START;
        for (int step = 0; step < STEPS; step++) {
            clock = clock.plusMinutes(random.nextInt(300));
            String context = "semilla " + seed + ", paso " + step;
            try {
                operate(lot, vehicles, random, clock, context);
            } catch (ParkingException expectedRejection) {
                rejections++;
            } catch (RuntimeException unexpected) {
                fail(context + ": excepción inesperada " + unexpected, unexpected);
            }
            checkInvariants(lot, context);
        }
    }

    private void operate(ParkingLot lot, List<Vehicle> vehicles, Random random, LocalDateTime clock, String context) {
        int choice = random.nextInt(100);
        Vehicle vehicle = vehicles.get(random.nextInt(vehicles.size()));
        List<ParkingSpace> spaces = lot.getSpaces();
        if (choice < 30) {
            lot.registerEntry(vehicle, clock);
            entries++;
        } else if (choice < 42) {
            lot.registerEntry(vehicle, spaces.get(random.nextInt(spaces.size())), clock);
            entries++;
        } else if (choice < 68) {
            lot.registerExit(vehicle, exitTimeFor(lot, vehicle, random, clock));
            exits++;
        } else if (choice < 85) {
            List<ParkingTicket> tickets = lot.getTickets();
            if (!tickets.isEmpty()) {
                ParkingTicket ticket = tickets.get(random.nextInt(tickets.size()));
                PaymentType type = PaymentType.values()[random.nextInt(PaymentType.values().length)];
                lot.registerPayment(ticket, type, clock);
                payments++;
            }
        } else if (choice < 92) {
            lot.putSpaceOutOfService(spaces.get(random.nextInt(spaces.size())).getNumber());
            outOfServiceChanges++;
        } else if (choice < 97) {
            lot.restoreSpaceService(spaces.get(random.nextInt(spaces.size())).getNumber());
            outOfServiceChanges++;
        } else {
            try {
                lot.registerVehicle(newVehicle(random.nextInt(3), vehicle.getPlate().toLowerCase()));
                fail(context + ": se aceptó una placa repetida");
            } catch (DuplicateRecordException expected) {
                rejections++;
            }
            try {
                lot.registerSpace(new ParkingSpace(spaces.get(0).getNumber(), SpaceType.CAR));
                fail(context + ": se aceptó un número de espacio repetido");
            } catch (DuplicateRecordException expected) {
                rejections++;
            }
        }
    }

    /** Chooses an exit time: mostly around interesting durations, sometimes before the entry. */
    private LocalDateTime exitTimeFor(ParkingLot lot, Vehicle vehicle, Random random, LocalDateTime clock) {
        for (ParkingTicket ticket : lot.getActiveTickets()) {
            if (ticket.getVehicle() == vehicle) {
                int[] interesting = {0, 1, 59, 60, 61, 119, 120, 121, 539, 540, 541, 599, 600, 601, 1439, 1440, 1441, 2000, 3000};
                long minutes = random.nextInt(10) == 0
                        ? -1 - random.nextInt(30)
                        : (random.nextBoolean() ? interesting[random.nextInt(interesting.length)] : random.nextInt(4500));
                return ticket.getEntryTime().plusMinutes(minutes);
            }
        }
        return clock;
    }

    private Vehicle newVehicle(int kind, String plate) {
        if (kind == 0) {
            return new Motorcycle(plate, "Honda", "CBR", "Negro");
        }
        if (kind == 1) {
            return new Car(plate, "Toyota", "Corolla", "Blanco");
        }
        return new FreightVehicle(plate, "Isuzu", "NPR", "Azul");
    }

    /** The rule of the assignment written again, independent from the production classes. */
    private double expectedAmount(Vehicle vehicle, long minutes) {
        long hours = minutes <= 0 ? 0 : (minutes + 59) / 60;
        double rate = RATE.get(vehicle.getType());
        double cap = CAP.get(vehicle.getType());
        if (hours < 10) {
            return hours * rate;
        }
        long fullDays = hours / 24;
        long remaining = hours % 24;
        return fullDays * cap + Math.min(remaining * rate, cap);
    }

    private void checkInvariants(ParkingLot lot, String context) {
        List<ParkingTicket> tickets = lot.getTickets();
        List<ParkingTicket> active = lot.getActiveTickets();

        // espacios: ocupado si y solo si tiene exactamente un ticket activo
        for (ParkingSpace space : lot.getSpaces()) {
            int ticketsInSpace = 0;
            for (ParkingTicket ticket : active) {
                if (ticket.getSpace() == space) {
                    ticketsInSpace++;
                }
            }
            if (space.getStatus() == SpaceStatus.OCCUPIED) {
                assertEquals(1, ticketsInSpace, context + ": espacio ocupado sin un único ticket activo " + space.getNumber());
            } else {
                assertEquals(0, ticketsInSpace, context + ": ticket activo en un espacio no ocupado " + space.getNumber());
            }
        }

        // vehículos: máximo un ticket activo
        Set<String> platesWithActiveTicket = new HashSet<String>();
        for (ParkingTicket ticket : active) {
            assertTrue(platesWithActiveTicket.add(ticket.getVehicle().getPlate()),
                    context + ": dos tickets activos para " + ticket.getVehicle().getPlate());
            assertTrue(ticket.getSpace().isCompatibleWith(ticket.getVehicle()), context + ": espacio incompatible");
            assertEquals(TicketStatus.ACTIVE, ticket.getStatus());
            assertTrue(ticket.getExitTime() == null, context + ": ticket activo con hora de salida");
            assertEquals(0.0, ticket.getAmount(), 0.0001, context + ": ticket activo con monto");
        }

        // tickets cerrados y pagados: monto correcto según la regla del enunciado
        long paidTickets = 0;
        double paidAmount = 0;
        long previousId = 0;
        for (ParkingTicket ticket : tickets) {
            assertTrue(ticket.getId() > previousId, context + ": números de ticket no crecientes");
            previousId = ticket.getId();
            if (ticket.getStatus() != TicketStatus.ACTIVE) {
                assertTrue(ticket.getExitTime() != null && !ticket.getExitTime().isBefore(ticket.getEntryTime()),
                        context + ": salida inválida en el ticket " + ticket.getId());
                long minutes = Duration.between(ticket.getEntryTime(), ticket.getExitTime()).toMinutes();
                assertEquals(expectedAmount(ticket.getVehicle(), minutes), ticket.getAmount(), 0.0001,
                        context + ": monto incorrecto en el ticket " + ticket.getId() + " con " + minutes + " minutos de "
                        + ticket.getVehicle().getType());
                assertTrue(ticket.getAmount() <= RATE.get(ticket.getVehicle().getType()) * ((minutes + 59) / 60) + 0.0001,
                        context + ": el tope nunca debe subir el precio");
            }
            if (ticket.getStatus() == TicketStatus.PAID) {
                paidTickets++;
                paidAmount += ticket.getAmount();
            }
        }

        // pagos: uno por ticket pagado, con el mismo monto
        List<Payment> payments = lot.getPayments();
        assertEquals(paidTickets, payments.size(), context + ": pagos distintos de tickets pagados");
        double paymentsTotal = 0;
        Set<Long> paidTicketIds = new HashSet<Long>();
        for (Payment payment : payments) {
            assertEquals(TicketStatus.PAID, payment.getTicket().getStatus(), context + ": pago de un ticket no pagado");
            assertEquals(payment.getTicket().getAmount(), payment.getAmount(), 0.0001, context + ": pago con otro monto");
            assertTrue(paidTicketIds.add(payment.getTicket().getId()), context + ": ticket pagado dos veces");
            paymentsTotal += payment.getAmount();
        }
        assertEquals(paidAmount, paymentsTotal, 0.0001, context + ": suma de pagos");
        assertEquals(paymentsTotal, lot.getTotalIncome(), 0.0001, context + ": ingresos totales");

        // consultas: coherentes entre sí
        assertEquals(active.size(), lot.getVehiclesInside().size(), context + ": vehículos dentro");
        int occupiedByType = 0;
        for (Integer count : lot.getOccupancyByType().values()) {
            occupiedByType += count;
        }
        assertEquals(active.size(), occupiedByType, context + ": ocupación por tipo");
        int usable = 0;
        int available = 0;
        for (ParkingSpace space : lot.getSpaces()) {
            if (space.getStatus() != SpaceStatus.OUT_OF_SERVICE) {
                usable++;
            }
            if (space.getStatus() == SpaceStatus.AVAILABLE) {
                available++;
            }
        }
        assertEquals(usable, lot.countUsableSpaces(), context + ": espacios utilizables");
        assertEquals(available, lot.getAvailableSpaces().size(), context + ": espacios disponibles");
    }
}
