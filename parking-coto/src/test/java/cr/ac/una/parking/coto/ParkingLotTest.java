package cr.ac.una.parking.coto;

import cr.ac.una.parking.coto.enums.PaymentType;
import cr.ac.una.parking.coto.enums.SpaceStatus;
import cr.ac.una.parking.coto.enums.SpaceType;
import cr.ac.una.parking.coto.enums.TicketStatus;
import cr.ac.una.parking.coto.exception.ActiveTicketException;
import cr.ac.una.parking.coto.exception.InvalidTicketStateException;
import cr.ac.una.parking.coto.exception.ParkingException;
import cr.ac.una.parking.coto.exception.RecordNotFoundException;
import cr.ac.una.parking.coto.exception.SpaceNotAvailableException;
import cr.ac.una.parking.coto.model.Car;
import cr.ac.una.parking.coto.model.FreightVehicle;
import cr.ac.una.parking.coto.model.Motorcycle;
import cr.ac.una.parking.coto.model.ParkingSpace;
import cr.ac.una.parking.coto.model.ParkingTicket;
import cr.ac.una.parking.coto.model.Vehicle;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParkingLotTest {

    private ParkingLot parkingLot;

    @BeforeEach
    void setUp() {
        parkingLot = new ParkingLot();
        parkingLot.registerSpace(new ParkingSpace(1, SpaceType.CAR));
        parkingLot.registerSpace(new ParkingSpace(2, SpaceType.MOTORCYCLE));
        parkingLot.registerSpace(new ParkingSpace(3, SpaceType.FREIGHT));
        parkingLot.registerSpace(new ParkingSpace(4, SpaceType.CAR));
    }

    @Test
    void shouldRegisterCarEntry() {
        Vehicle car = new Car("B123456", "Toyota", "Corolla", "Blanco");
        parkingLot.registerVehicle(car);

        ParkingTicket ticket = parkingLot.registerEntry(car, LocalDateTime.of(2026, 9, 30, 8, 0));

        assertNotNull(ticket);
        assertEquals(TicketStatus.ACTIVE, ticket.getStatus());
        assertEquals(SpaceType.CAR, ticket.getSpace().getType());
        assertEquals(SpaceStatus.OCCUPIED, ticket.getSpace().getStatus());
    }

    @Test
    void shouldRegisterMotorcycleEntry() {
        Vehicle bike = new Motorcycle("M987654", "Honda", "CBR", "Negro");
        parkingLot.registerVehicle(bike);

        ParkingTicket ticket = parkingLot.registerEntry(bike, LocalDateTime.of(2026, 9, 30, 9, 30));

        assertNotNull(ticket);
        assertEquals(SpaceType.MOTORCYCLE, ticket.getSpace().getType());
    }

    @Test
    void shouldRegisterFreightVehicleEntry() {
        Vehicle freight = new FreightVehicle("C456789", "Mercedes", "Actros", "Azul");
        parkingLot.registerVehicle(freight);

        ParkingTicket ticket = parkingLot.registerEntry(freight, LocalDateTime.of(2026, 9, 30, 7, 15));

        assertNotNull(ticket);
        assertEquals(SpaceType.FREIGHT, ticket.getSpace().getType());
    }

    @Test
    void shouldRejectOccupiedSpace() {
        Vehicle firstCar = new Car("X111111", "Nissan", "Sentra", "Rojo");
        Vehicle secondCar = new Car("X222222", "Hyundai", "Accent", "Gris");
        parkingLot.registerVehicle(firstCar);
        parkingLot.registerVehicle(secondCar);

        ParkingTicket ticket = parkingLot.registerEntry(firstCar, LocalDateTime.of(2026, 9, 30, 8, 0));
        assertNotNull(ticket);

        assertThrows(SpaceNotAvailableException.class,
                () -> parkingLot.registerEntry(secondCar, new ParkingSpace(1, SpaceType.CAR),
                        LocalDateTime.of(2026, 9, 30, 8, 15)));
    }

    @Test
    void shouldRejectOutOfServiceSpace() {
        Vehicle car = new Car("R123456", "Kia", "Rio", "Rojo");
        parkingLot.registerVehicle(car);

        ParkingSpace space = new ParkingSpace(10, SpaceType.CAR);
        parkingLot.registerSpace(space);
        space.putOutOfService();

        assertThrows(SpaceNotAvailableException.class,
                () -> parkingLot.registerEntry(car, space, LocalDateTime.of(2026, 9, 30, 9, 0)));
    }

    @Test
    void shouldRejectIncompatibleSpace() {
        Vehicle car = new Car("T456789", "Chevrolet", "Cruze", "Blanco");
        parkingLot.registerVehicle(car);

        ParkingSpace motorcycleSpace = new ParkingSpace(20, SpaceType.MOTORCYCLE);
        parkingLot.registerSpace(motorcycleSpace);

        assertThrows(SpaceNotAvailableException.class,
                () -> parkingLot.registerEntry(car, motorcycleSpace, LocalDateTime.of(2026, 9, 30, 9, 0)));
    }

    @Test
    void shouldRejectDuplicateActiveTicketForSameVehicle() {
        Vehicle car = new Car("P123ABC", "Mazda", "3", "Blanco");
        parkingLot.registerVehicle(car);
        parkingLot.registerEntry(car, LocalDateTime.of(2026, 9, 30, 8, 0));

        assertThrows(ActiveTicketException.class,
                () -> parkingLot.registerEntry(car, LocalDateTime.of(2026, 9, 30, 8, 10)));
    }

    @Test
    void shouldRejectExitWithoutTicket() {
        Vehicle car = new Car("S555444", "Honda", "Civic", "Azul");
        parkingLot.registerVehicle(car);

        assertThrows(RecordNotFoundException.class,
                () -> parkingLot.registerExit(car, LocalDateTime.of(2026, 9, 30, 10, 0)));
    }

    @Test
    void shouldChargeOneMinuteAsOneHourForMotorcycle() {
        Vehicle bike = new Motorcycle("K998877", "Yamaha", "R15", "Verde");
        parkingLot.registerVehicle(bike);
        ParkingTicket ticket = parkingLot.registerEntry(bike, LocalDateTime.of(2026, 9, 30, 8, 0));

        ticket.close(LocalDateTime.of(2026, 9, 30, 8, 1));

        assertEquals(500.0, ticket.getAmount(), 0.001);
    }

    @Test
    void shouldChargeSixtyMinutesAsOneHourForCar() {
        Vehicle car = new Car("H333444", "Toyota", "Yaris", "Plata");
        parkingLot.registerVehicle(car);
        ParkingTicket ticket = parkingLot.registerEntry(car, LocalDateTime.of(2026, 9, 30, 8, 0));

        ticket.close(LocalDateTime.of(2026, 9, 30, 9, 0));

        assertEquals(900.0, ticket.getAmount(), 0.001);
    }

    @Test
    void shouldChargeSixtyOneMinutesAsTwoHoursForCar() {
        Vehicle car = new Car("N777888", "Ford", "Focus", "Gris");
        parkingLot.registerVehicle(car);
        ParkingTicket ticket = parkingLot.registerEntry(car, LocalDateTime.of(2026, 9, 30, 8, 0));

        ticket.close(LocalDateTime.of(2026, 9, 30, 9, 1));

        assertEquals(1800.0, ticket.getAmount(), 0.001);
    }

    @Test
    void shouldApplyDailyCapAfterTenHoursForCar() {
        Vehicle car = new Car("A111222", "Nissan", "Versa", "Negro");
        parkingLot.registerVehicle(car);
        ParkingTicket ticket = parkingLot.registerEntry(car, LocalDateTime.of(2026, 9, 30, 8, 0));

        ticket.close(LocalDateTime.of(2026, 9, 30, 18, 0));

        assertEquals(7000.0, ticket.getAmount(), 0.001);
    }

    @Test
    void shouldCloseTicketAndPayIt() {
        Vehicle car = new Car("Q321XYZ", "Ford", "Focus", "Negro");
        parkingLot.registerVehicle(car);
        ParkingTicket ticket = parkingLot.registerEntry(car, LocalDateTime.of(2026, 9, 30, 10, 0));

        ticket.close(LocalDateTime.of(2026, 9, 30, 11, 1));
        parkingLot.registerPayment(ticket, PaymentType.CASH, LocalDateTime.of(2026, 9, 30, 11, 5));

        assertEquals(TicketStatus.PAID, ticket.getStatus());
        assertEquals(SpaceStatus.AVAILABLE, ticket.getSpace().getStatus());
        assertEquals(1800.0, ticket.getAmount(), 0.001);
        assertEquals(1800.0, parkingLot.getTotalIncome(), 0.001);
    }

    @Test
    void shouldReleaseSpaceAfterClosingTicket() {
        Vehicle car = new Car("L333444", "Toyota", "Prius", "Gris");
        parkingLot.registerVehicle(car);
        ParkingTicket ticket = parkingLot.registerEntry(car, LocalDateTime.of(2026, 9, 30, 12, 0));

        ticket.close(LocalDateTime.of(2026, 9, 30, 13, 0));

        assertEquals(SpaceStatus.AVAILABLE, ticket.getSpace().getStatus());
    }

    @Test
    void shouldCalculateTotalIncome() {
        Vehicle car = new Car("V555666", "Mazda", "CX-5", "Blanco");
        parkingLot.registerVehicle(car);
        ParkingTicket ticket = parkingLot.registerEntry(car, LocalDateTime.of(2026, 9, 30, 8, 0));
        ticket.close(LocalDateTime.of(2026, 9, 30, 9, 0));
        parkingLot.registerPayment(ticket, PaymentType.CARD, LocalDateTime.of(2026, 9, 30, 9, 5));

        assertEquals(900.0, parkingLot.getTotalIncome(), 0.001);
    }

    @Test
    void shouldListVehiclesInside() {
        Vehicle car = new Car("L333444", "Toyota", "Prius", "Gris");
        parkingLot.registerVehicle(car);
        parkingLot.registerEntry(car, LocalDateTime.of(2026, 9, 30, 12, 0));

        List<Vehicle> vehicles = parkingLot.getVehiclesInside();

        assertEquals(1, vehicles.size());
        assertEquals("L333444", vehicles.get(0).getPlate());
    }

    @Test
    void shouldRejectPaymentOfActiveTicket() {
        Vehicle car = new Car("W111222", "Suzuki", "Swift", "Rojo");
        parkingLot.registerVehicle(car);
        ParkingTicket ticket = parkingLot.registerEntry(car, LocalDateTime.of(2026, 9, 30, 8, 0));

        assertThrows(InvalidTicketStateException.class,
                () -> parkingLot.registerPayment(ticket, PaymentType.CASH, LocalDateTime.of(2026, 9, 30, 8, 30)));
        assertEquals(TicketStatus.ACTIVE, ticket.getStatus());
        assertEquals(0.0, parkingLot.getTotalIncome(), 0.001);
    }

    @Test
    void shouldNotListVehicleInsideAfterExit() {
        Vehicle car = new Car("E444555", "Kia", "Soul", "Verde");
        parkingLot.registerVehicle(car);
        parkingLot.registerEntry(car, LocalDateTime.of(2026, 9, 30, 8, 0));
        assertEquals(1, parkingLot.getVehiclesInside().size());

        parkingLot.registerExit(car, LocalDateTime.of(2026, 9, 30, 9, 0));

        assertTrue(parkingLot.getVehiclesInside().isEmpty());
        assertTrue(parkingLot.getActiveTickets().isEmpty());
    }

    @Test
    void shouldAllowSameVehicleToEnterAgainAfterExit() {
        Vehicle car = new Car("R999000", "Ford", "Fiesta", "Azul");
        parkingLot.registerVehicle(car);
        parkingLot.registerEntry(car, LocalDateTime.of(2026, 9, 30, 8, 0));
        parkingLot.registerExit(car, LocalDateTime.of(2026, 9, 30, 9, 0));

        ParkingTicket secondTicket = parkingLot.registerEntry(car, LocalDateTime.of(2026, 9, 30, 10, 0));

        assertEquals(TicketStatus.ACTIVE, secondTicket.getStatus());
        assertEquals(1, parkingLot.getVehiclesInside().size());
    }

    @Test
    void shouldChargeEachVehicleTypeWithItsOwnRate() {
        Vehicle car = new Car("Z100100", "Toyota", "Rav4", "Gris");
        Vehicle bike = new Motorcycle("Z200200", "Honda", "Navi", "Rojo");
        Vehicle truck = new FreightVehicle("Z300300", "Isuzu", "NPR", "Blanco");
        parkingLot.registerVehicle(car);
        parkingLot.registerVehicle(bike);
        parkingLot.registerVehicle(truck);
        LocalDateTime entry = LocalDateTime.of(2026, 9, 30, 8, 0);

        ParkingTicket carTicket = parkingLot.registerEntry(car, entry);
        ParkingTicket bikeTicket = parkingLot.registerEntry(bike, entry);
        ParkingTicket truckTicket = parkingLot.registerEntry(truck, entry);
        carTicket.close(entry.plusHours(2));
        bikeTicket.close(entry.plusHours(2));
        truckTicket.close(entry.plusHours(2));

        assertEquals(1800.0, carTicket.getAmount(), 0.001);
        assertEquals(1000.0, bikeTicket.getAmount(), 0.001);
        assertEquals(3000.0, truckTicket.getAmount(), 0.001);
    }

    @Test
    void shouldApplyDailyCapToMotorcycleAndFreightVehicle() {
        Vehicle bike = new Motorcycle("Y100100", "Yamaha", "FZ", "Negro");
        Vehicle truck = new FreightVehicle("Y200200", "Hino", "300", "Rojo");
        parkingLot.registerVehicle(bike);
        parkingLot.registerVehicle(truck);
        LocalDateTime entry = LocalDateTime.of(2026, 9, 30, 6, 0);

        ParkingTicket bikeTicket = parkingLot.registerEntry(bike, entry);
        ParkingTicket truckTicket = parkingLot.registerEntry(truck, entry);
        bikeTicket.close(entry.plusHours(11));
        truckTicket.close(entry.plusHours(11));

        assertEquals(4000.0, bikeTicket.getAmount(), 0.001);
        assertEquals(11000.0, truckTicket.getAmount(), 0.001);
    }

    @Test
    void shouldKeepSpaceAvailableWhenEntryTimeIsNull() {
        Vehicle car = new Car("N000111", "Mazda", "2", "Plata");
        parkingLot.registerVehicle(car);
        int availableBefore = parkingLot.getAvailableSpaces().size();

        assertThrows(ParkingException.class, () -> parkingLot.registerEntry(car, null));

        assertEquals(availableBefore, parkingLot.getAvailableSpaces().size());
    }

    @Test
    void shouldCountOccupancyByType() {
        Vehicle car = new Car("O111222", "Nissan", "Kicks", "Blanco");
        parkingLot.registerVehicle(car);
        parkingLot.registerEntry(car, LocalDateTime.of(2026, 9, 30, 8, 0));

        assertEquals(Integer.valueOf(1), parkingLot.getOccupancyByType().get(SpaceType.CAR));
        assertNull(parkingLot.getOccupancyByType().get(SpaceType.MOTORCYCLE));
    }
}
