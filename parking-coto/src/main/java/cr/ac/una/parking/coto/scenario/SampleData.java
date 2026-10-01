package cr.ac.una.parking.coto.scenario;

import cr.ac.una.parking.coto.ParkingLot;
import cr.ac.una.parking.coto.enums.SpaceType;
import cr.ac.una.parking.coto.exception.DuplicateRecordException;
import cr.ac.una.parking.coto.model.Car;
import cr.ac.una.parking.coto.model.FreightVehicle;
import cr.ac.una.parking.coto.model.Motorcycle;
import cr.ac.una.parking.coto.model.ParkingSpace;

/**
 * Loads a small set of spaces and vehicles so the system can be demonstrated
 * without typing every record.
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.0
 */
public final class SampleData {

    /** Utility class: it is never instantiated. */
    private SampleData() {
    }

    /**
     * Registers seven spaces (2 motorcycle, 3 car, 2 freight) and seven
     * vehicles in an empty parking lot.
     *
     * @param parkingLot parking lot that receives the data
     * @throws DuplicateRecordException if the parking lot already has spaces or vehicles
     */
    public static void load(ParkingLot parkingLot) {
        if (!parkingLot.getSpaces().isEmpty() || !parkingLot.getVehicles().isEmpty()) {
            throw new DuplicateRecordException(
                    "Los datos de ejemplo solo se pueden cargar cuando no hay espacios ni vehículos registrados");
        }
        parkingLot.registerSpace(new ParkingSpace(1, SpaceType.MOTORCYCLE));
        parkingLot.registerSpace(new ParkingSpace(2, SpaceType.MOTORCYCLE));
        parkingLot.registerSpace(new ParkingSpace(3, SpaceType.CAR));
        parkingLot.registerSpace(new ParkingSpace(4, SpaceType.CAR));
        parkingLot.registerSpace(new ParkingSpace(5, SpaceType.CAR));
        parkingLot.registerSpace(new ParkingSpace(6, SpaceType.FREIGHT));
        parkingLot.registerSpace(new ParkingSpace(7, SpaceType.FREIGHT));

        parkingLot.registerVehicle(new Car("ABC123", "Toyota", "Corolla", "Blanco"));
        parkingLot.registerVehicle(new Car("DEF456", "Nissan", "Sentra", "Gris"));
        parkingLot.registerVehicle(new Car("GHI789", "Hyundai", "Tucson", "Rojo"));
        parkingLot.registerVehicle(new Motorcycle("MOT111", "Honda", "CBR", "Negro"));
        parkingLot.registerVehicle(new Motorcycle("MOT222", "Yamaha", "FZ", "Azul"));
        parkingLot.registerVehicle(new FreightVehicle("CAR999", "Isuzu", "NPR", "Azul"));
        parkingLot.registerVehicle(new FreightVehicle("CAR888", "Hino", "300", "Blanco"));
    }
}
