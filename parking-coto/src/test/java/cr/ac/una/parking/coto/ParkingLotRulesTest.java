package cr.ac.una.parking.coto;

import cr.ac.una.parking.coto.enums.PaymentType;
import cr.ac.una.parking.coto.enums.SpaceStatus;
import cr.ac.una.parking.coto.enums.SpaceType;
import cr.ac.una.parking.coto.enums.TicketStatus;
import cr.ac.una.parking.coto.enums.VehicleType;
import cr.ac.una.parking.coto.exception.DuplicateRecordException;
import cr.ac.una.parking.coto.exception.InvalidTicketStateException;
import cr.ac.una.parking.coto.exception.ParkingException;
import cr.ac.una.parking.coto.exception.RecordNotFoundException;
import cr.ac.una.parking.coto.exception.SpaceNotAvailableException;
import cr.ac.una.parking.coto.model.Car;
import cr.ac.una.parking.coto.model.Motorcycle;
import cr.ac.una.parking.coto.model.ParkingSpace;
import cr.ac.una.parking.coto.model.ParkingTicket;
import cr.ac.una.parking.coto.model.Payment;
import cr.ac.una.parking.coto.model.Vehicle;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Rejections and limits of the business rules that the main test class does
 * not cover: duplicate records, unknown records, invalid data and the
 * boundaries of the daily cap.
 */
class ParkingLotRulesTest {

    private static final LocalDateTime START = LocalDateTime.of(2026, 10, 1, 8, 0);

    private ParkingLot parkingLot;
    private Vehicle car;

    @BeforeEach
    void setUp() {
        parkingLot = new ParkingLot();
        parkingLot.registerSpace(new ParkingSpace(1, SpaceType.CAR));
        parkingLot.registerSpace(new ParkingSpace(2, SpaceType.MOTORCYCLE));
        car = new Car("ABC123", "Toyota", "Corolla", "Blanco");
        parkingLot.registerVehicle(car);
    }

    // ---------- registros repetidos o desconocidos ----------

    @Test
    void shouldRejectTwoVehiclesWithTheSamePlate() {
        Vehicle sameLetters = new Motorcycle("abc123", "Honda", "CBR", "Negro");

        assertThrows(DuplicateRecordException.class, () -> parkingLot.registerVehicle(sameLetters));
        assertEquals(1, parkingLot.getVehicles().size());
    }

    @Test
    void shouldRejectTwoSpacesWithTheSameNumber() {
        assertThrows(DuplicateRecordException.class,
                () -> parkingLot.registerSpace(new ParkingSpace(1, SpaceType.FREIGHT)));
        assertEquals(2, parkingLot.getSpaces().size());
    }

    @Test
    void shouldRejectEntryOfAnUnregisteredVehicle() {
        Vehicle stranger = new Car("ZZZ999", "Kia", "Rio", "Rojo");

        assertThrows(RecordNotFoundException.class, () -> parkingLot.registerEntry(stranger, START));
    }

    @Test
    void shouldRejectEntryInAnUnregisteredSpace() {
        assertThrows(RecordNotFoundException.class,
                () -> parkingLot.registerEntry(car, new ParkingSpace(50, SpaceType.CAR), START));
    }

    @Test
    void shouldRejectPaymentOfATicketThatDoesNotBelongToTheParkingLot() {
        ParkingTicket foreign = new ParkingTicket(99L, car, new ParkingSpace(7, SpaceType.CAR), START);
        foreign.close(START.plusHours(1));

        assertThrows(RecordNotFoundException.class,
                () -> parkingLot.registerPayment(foreign, PaymentType.CASH, START.plusHours(1)));
    }

    @Test
    void shouldRejectNullArguments() {
        assertThrows(ParkingException.class, () -> parkingLot.registerVehicle(null));
        assertThrows(ParkingException.class, () -> parkingLot.registerSpace(null));
        assertThrows(ParkingException.class, () -> parkingLot.registerEntry(null, START));
        assertThrows(ParkingException.class, () -> parkingLot.registerEntry(car, (ParkingSpace) null, START));
        assertThrows(ParkingException.class, () -> parkingLot.registerExit(null, START));
        assertThrows(ParkingException.class, () -> parkingLot.registerPayment(null, PaymentType.CASH, START));
    }

    // ---------- espacios ----------

    @Test
    void shouldRejectAutomaticAssignmentWhenThereIsNoFreeCompatibleSpace() {
        Vehicle secondCar = new Car("DEF456", "Nissan", "Sentra", "Gris");
        parkingLot.registerVehicle(secondCar);
        parkingLot.registerEntry(car, START);

        assertThrows(SpaceNotAvailableException.class, () -> parkingLot.registerEntry(secondCar, START));
        assertEquals(0, parkingLot.getActiveTickets().stream()
                .filter(ticket -> ticket.getVehicle() == secondCar).count());
    }

    @Test
    void shouldCountOnlyUsableSpaces() {
        assertEquals(2, parkingLot.countUsableSpaces());

        parkingLot.putSpaceOutOfService(2);
        assertEquals(1, parkingLot.countUsableSpaces());

        parkingLot.registerEntry(car, START);
        assertEquals(1, parkingLot.countUsableSpaces());
    }

    @Test
    void shouldReportOccupancyByTypeOnlyForOccupiedSpaces() {
        Vehicle bike = new Motorcycle("MOT111", "Honda", "CBR", "Negro");
        parkingLot.registerVehicle(bike);
        parkingLot.registerEntry(car, START);
        parkingLot.registerEntry(bike, START);

        assertEquals(Integer.valueOf(1), parkingLot.getOccupancyByType().get(SpaceType.CAR));
        assertEquals(Integer.valueOf(1), parkingLot.getOccupancyByType().get(SpaceType.MOTORCYCLE));
        assertNull(parkingLot.getOccupancyByType().get(SpaceType.FREIGHT));

        parkingLot.registerExit(bike, START.plusHours(1));
        assertNull(parkingLot.getOccupancyByType().get(SpaceType.MOTORCYCLE));
    }

    // ---------- tickets y pagos ----------

    @Test
    void shouldRejectPayingTheSameTicketTwice() {
        parkingLot.registerEntry(car, START);
        ParkingTicket ticket = parkingLot.registerExit(car, START.plusHours(1));
        parkingLot.registerPayment(ticket, PaymentType.CARD, START.plusHours(1));

        assertThrows(InvalidTicketStateException.class,
                () -> parkingLot.registerPayment(ticket, PaymentType.CASH, START.plusHours(2)));
        assertEquals(TicketStatus.PAID, ticket.getStatus());
        assertEquals(900.0, parkingLot.getTotalIncome(), 0.001);
        assertEquals(1, parkingLot.getPayments().size());
    }

    @Test
    void shouldRejectClosingATicketThatIsAlreadyClosed() {
        parkingLot.registerEntry(car, START);
        ParkingTicket ticket = parkingLot.registerExit(car, START.plusHours(1));

        assertThrows(InvalidTicketStateException.class, () -> ticket.close(START.plusHours(2)));
        assertEquals(900.0, ticket.getAmount(), 0.001);
    }

    @Test
    void shouldLeaveEverythingUntouchedWhenTheExitTimeIsBeforeTheEntry() {
        ParkingTicket ticket = parkingLot.registerEntry(car, START);

        assertThrows(ParkingException.class, () -> parkingLot.registerExit(car, START.minusMinutes(1)));

        assertEquals(TicketStatus.ACTIVE, ticket.getStatus());
        assertEquals(SpaceStatus.OCCUPIED, ticket.getSpace().getStatus());
        assertNull(ticket.getExitTime());
        assertEquals(1, parkingLot.getVehiclesInside().size());
    }

    @Test
    void shouldChargeAStayOfZeroMinutesAsNothing() {
        parkingLot.registerEntry(car, START);

        ParkingTicket ticket = parkingLot.registerExit(car, START);

        assertEquals(0.0, ticket.getAmount(), 0.001);
        assertEquals(TicketStatus.CLOSED, ticket.getStatus());
    }

    @Test
    void shouldAcceptPaymentsOfZeroOnlyWhenTheTicketCostsNothing() {
        parkingLot.registerEntry(car, START);
        ParkingTicket ticket = parkingLot.registerExit(car, START);

        Payment payment = parkingLot.registerPayment(ticket, PaymentType.CASH, START);

        assertEquals(0.0, payment.getAmount(), 0.001);
    }

    // ---------- límites del tope diario ----------

    @Test
    void shouldCapAStayOfNineHoursAndFiftyNineMinutesBecauseItIsChargedAsTen() {
        parkingLot.registerEntry(car, START);

        ParkingTicket ticket = parkingLot.registerExit(car, START.plusHours(9).plusMinutes(59));

        assertEquals(10, ticket.calculateChargedHours(ticket.getExitTime()));
        assertEquals(7000.0, ticket.getAmount(), 0.001);
    }

    @Test
    void shouldNotCapAStayOfNineHoursExactly() {
        parkingLot.registerEntry(car, START);

        ParkingTicket ticket = parkingLot.registerExit(car, START.plusHours(9));

        assertEquals(8100.0, ticket.getAmount(), 0.001);
    }

    @Test
    void shouldChargeATwentyFourHourStayAsOneDailyPeriod() {
        parkingLot.registerEntry(car, START);

        ParkingTicket ticket = parkingLot.registerExit(car, START.plusHours(24));

        assertEquals(7000.0, ticket.getAmount(), 0.001);
    }

    // ---------- validación de datos ----------

    @Test
    void shouldRejectVehiclesWithMissingData() {
        assertThrows(ParkingException.class, () -> new Car("  ", "Toyota", "Corolla", "Blanco"));
        assertThrows(ParkingException.class, () -> new Car("ABC", null, "Corolla", "Blanco"));
        assertThrows(ParkingException.class, () -> new Car("ABC", "Toyota", "", "Blanco"));
        assertThrows(ParkingException.class, () -> new Car("ABC", "Toyota", "Corolla", " "));
    }

    @Test
    void shouldNormalizeThePlateOfAVehicle() {
        Vehicle vehicle = new Car("  abc123 ", "Toyota", "Corolla", "Blanco");

        assertEquals("ABC123", vehicle.getPlate());
        assertEquals(VehicleType.CAR, vehicle.getType());
    }

    @Test
    void shouldRejectInvalidSpaces() {
        assertThrows(ParkingException.class, () -> new ParkingSpace(0, SpaceType.CAR));
        assertThrows(ParkingException.class, () -> new ParkingSpace(-3, SpaceType.CAR));
        assertThrows(ParkingException.class, () -> new ParkingSpace(5, null));
    }

    @Test
    void shouldRejectInvalidPayments() {
        ParkingTicket ticket = new ParkingTicket(1L, car, new ParkingSpace(9, SpaceType.CAR), START);

        assertThrows(ParkingException.class, () -> new Payment(1L, null, START, 100.0, PaymentType.CASH));
        assertThrows(ParkingException.class, () -> new Payment(1L, ticket, null, 100.0, PaymentType.CASH));
        assertThrows(ParkingException.class, () -> new Payment(1L, ticket, START, -1.0, PaymentType.CASH));
        assertThrows(ParkingException.class, () -> new Payment(1L, ticket, START, 100.0, null));
    }

    @Test
    void shouldGiveEachNewTicketAndPaymentItsOwnNumber() {
        Vehicle bike = new Motorcycle("MOT111", "Honda", "CBR", "Negro");
        parkingLot.registerVehicle(bike);
        ParkingTicket first = parkingLot.registerEntry(car, START);
        ParkingTicket second = parkingLot.registerEntry(bike, START);
        parkingLot.registerExit(car, START.plusHours(1));
        parkingLot.registerExit(bike, START.plusHours(1));
        Payment firstPayment = parkingLot.registerPayment(first, PaymentType.CASH, START.plusHours(1));
        Payment secondPayment = parkingLot.registerPayment(second, PaymentType.SINPE_MOVIL, START.plusHours(1));

        assertEquals(1L, first.getId());
        assertEquals(2L, second.getId());
        assertEquals(1L, firstPayment.getId());
        assertEquals(2L, secondPayment.getId());
        assertTrue(parkingLot.getActiveTickets().isEmpty());
    }
}
