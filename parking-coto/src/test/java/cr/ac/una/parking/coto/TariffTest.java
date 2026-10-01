package cr.ac.una.parking.coto;

import cr.ac.una.parking.coto.enums.SpaceType;
import cr.ac.una.parking.coto.enums.TicketStatus;
import cr.ac.una.parking.coto.exception.ParkingException;
import cr.ac.una.parking.coto.model.Car;
import cr.ac.una.parking.coto.model.FreightVehicle;
import cr.ac.una.parking.coto.model.Motorcycle;
import cr.ac.una.parking.coto.model.ParkingSpace;
import cr.ac.una.parking.coto.model.ParkingTicket;
import cr.ac.una.parking.coto.model.Vehicle;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TariffTest {

    @Test
    void shouldCalculateTheAmountToPayWithoutClosingTheTicket() {
        LocalDateTime entry = LocalDateTime.of(2026, 9, 30, 8, 0);
        ParkingTicket carTicket = new ParkingTicket(1L, car, new ParkingSpace(1, SpaceType.CAR), entry);
        ParkingTicket bikeTicket = new ParkingTicket(2L, bike, new ParkingSpace(2, SpaceType.MOTORCYCLE), entry);
        ParkingTicket truckTicket = new ParkingTicket(3L, truck, new ParkingSpace(3, SpaceType.FREIGHT), entry);

        assertEquals(1800.0, carTicket.calculateAmount(entry.plusMinutes(61)), 0.001);
        assertEquals(1000.0, bikeTicket.calculateAmount(entry.plusMinutes(61)), 0.001);
        assertEquals(3000.0, truckTicket.calculateAmount(entry.plusMinutes(61)), 0.001);
        assertEquals(7000.0, carTicket.calculateAmount(entry.plusHours(11)), 0.001);
        assertEquals(TicketStatus.ACTIVE, carTicket.getStatus());
        assertEquals(0.0, carTicket.getAmount(), 0.001);
        assertThrows(ParkingException.class, () -> carTicket.calculateAmount(entry.minusMinutes(1)));
    }

    @Test
    void shouldRejectAnInvalidExitTimeWhenCalculating() {
        LocalDateTime entry = LocalDateTime.of(2026, 9, 30, 8, 0);
        ParkingTicket ticket = new ParkingTicket(1L, car, new ParkingSpace(1, SpaceType.CAR), entry);

        assertThrows(ParkingException.class, () -> ticket.calculateChargedHours(null));
        assertThrows(ParkingException.class, () -> ticket.calculateStayMinutes(null));
        assertThrows(ParkingException.class, () -> ticket.calculateChargedHours(entry.minusMinutes(1)));
        assertThrows(ParkingException.class, () -> ticket.close(entry.minusMinutes(1)));
    }

    private final Vehicle car = new Car("T000001", "Toyota", "Corolla", "Blanco");
    private final Vehicle bike = new Motorcycle("T000002", "Honda", "CBR", "Negro");
    private final Vehicle truck = new FreightVehicle("T000003", "Isuzu", "NPR", "Azul");

    @Test
    void shouldChargeHourlyRateBelowTheCapThreshold() {
        assertEquals(0.0, car.calculateFee(0), 0.001);
        assertEquals(900.0, car.calculateFee(1), 0.001);
        assertEquals(500.0, bike.calculateFee(1), 0.001);
        assertEquals(1500.0, truck.calculateFee(1), 0.001);
        assertEquals(8100.0, car.calculateFee(9), 0.001);
    }

    @Test
    void shouldApplyDailyCapFromTenHours() {
        assertEquals(4000.0, bike.calculateFee(10), 0.001);
        assertEquals(7000.0, car.calculateFee(10), 0.001);
        assertEquals(11000.0, truck.calculateFee(10), 0.001);
    }

    @Test
    void shouldChargeTheCapOnceForAFullDay() {
        assertEquals(7000.0, car.calculateFee(24), 0.001);
    }

    @Test
    void shouldChargeRemainingHoursAfterAFullDay() {
        assertEquals(7900.0, car.calculateFee(25), 0.001);
        assertEquals(14000.0, car.calculateFee(34), 0.001);
        assertEquals(14000.0, car.calculateFee(48), 0.001);
        assertEquals(14900.0, car.calculateFee(49), 0.001);
    }

    @Test
    void shouldRoundAnyFractionOfAnHourUp() {
        LocalDateTime entry = LocalDateTime.of(2026, 9, 30, 8, 0);
        ParkingTicket ticket = new ParkingTicket(1L, car, new ParkingSpace(1, SpaceType.CAR), entry);

        assertEquals(1, ticket.calculateChargedHours(entry.plusMinutes(35)));
        assertEquals(2, ticket.calculateChargedHours(entry.plusMinutes(70)));
        assertEquals(3, ticket.calculateChargedHours(entry.plusMinutes(121)));
        assertEquals(3, ticket.calculateChargedHours(entry.plusHours(3)));
        assertEquals(2, ticket.calculateChargedHours(entry.plusHours(1).plusSeconds(30)));
        assertEquals(75L, ticket.calculateStayMinutes(entry.plusMinutes(75)));
    }
}
