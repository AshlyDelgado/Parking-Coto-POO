package cr.ac.una.parking.coto;

import cr.ac.una.parking.coto.enums.SpaceStatus;
import cr.ac.una.parking.coto.enums.SpaceType;
import cr.ac.una.parking.coto.exception.RecordNotFoundException;
import cr.ac.una.parking.coto.exception.SpaceNotAvailableException;
import cr.ac.una.parking.coto.model.Car;
import cr.ac.una.parking.coto.model.ParkingSpace;
import cr.ac.una.parking.coto.model.Vehicle;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParkingLotServiceTest {

    private ParkingLot parkingLot;

    @BeforeEach
    void setUp() {
        parkingLot = new ParkingLot();
        parkingLot.registerSpace(new ParkingSpace(5, SpaceType.CAR));
        parkingLot.registerSpace(new ParkingSpace(2, SpaceType.MOTORCYCLE));
        parkingLot.registerSpace(new ParkingSpace(9, SpaceType.CAR));
    }

    @Test
    void shouldListSpacesOrderedByNumber() {
        assertEquals(2, parkingLot.getSpaces().get(0).getNumber());
        assertEquals(5, parkingLot.getSpaces().get(1).getNumber());
        assertEquals(9, parkingLot.getSpaces().get(2).getNumber());
    }

    @Test
    void shouldAssignTheLowestCompatibleSpaceAutomatically() {
        Vehicle car = new Car("A1", "Toyota", "Yaris", "Rojo");
        parkingLot.registerVehicle(car);

        assertEquals(5, parkingLot.registerEntry(car, LocalDateTime.of(2026, 10, 1, 8, 0)).getSpace().getNumber());
    }

    @Test
    void shouldPutAnAvailableSpaceOutOfServiceAndRestoreIt() {
        parkingLot.putSpaceOutOfService(5);
        assertEquals(SpaceStatus.OUT_OF_SERVICE, parkingLot.getSpaces().get(1).getStatus());
        assertEquals(2, parkingLot.getAvailableSpaces().size());

        parkingLot.restoreSpaceService(5);
        assertEquals(SpaceStatus.AVAILABLE, parkingLot.getSpaces().get(1).getStatus());
    }

    @Test
    void shouldNotPutAnOccupiedSpaceOutOfService() {
        Vehicle car = new Car("A1", "Toyota", "Yaris", "Rojo");
        parkingLot.registerVehicle(car);
        parkingLot.registerEntry(car, LocalDateTime.of(2026, 10, 1, 8, 0));

        assertThrows(SpaceNotAvailableException.class, () -> parkingLot.putSpaceOutOfService(5));
    }

    @Test
    void shouldRejectChangingTheServiceOfAnUnknownSpace() {
        assertThrows(RecordNotFoundException.class, () -> parkingLot.putSpaceOutOfService(77));
        assertThrows(RecordNotFoundException.class, () -> parkingLot.restoreSpaceService(77));
        assertThrows(SpaceNotAvailableException.class, () -> parkingLot.restoreSpaceService(5));
    }

    @Test
    void shouldExposeReadOnlyHistory() {
        Vehicle car = new Car("A1", "Toyota", "Yaris", "Rojo");
        parkingLot.registerVehicle(car);
        parkingLot.registerEntry(car, LocalDateTime.of(2026, 10, 1, 8, 0));

        assertEquals(1, parkingLot.getVehicles().size());
        assertEquals(1, parkingLot.getTickets().size());
        assertTrue(parkingLot.getPayments().isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> parkingLot.getTickets().clear());
    }
}
