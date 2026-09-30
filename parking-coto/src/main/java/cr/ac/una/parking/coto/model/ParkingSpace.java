package cr.ac.una.parking.coto.model;

import cr.ac.una.parking.coto.enums.SpaceStatus;
import cr.ac.una.parking.coto.enums.SpaceType;
import cr.ac.una.parking.coto.exception.ParkingException;

public class ParkingSpace {

    private final int number;
    private final SpaceType type;
    private SpaceStatus status;

    public ParkingSpace(int number, SpaceType type) {
        if (number <= 0) {
            throw new ParkingException("El número del espacio debe ser mayor que cero");
        }
        if (type == null) {
            throw new ParkingException("El tipo de espacio no puede ser nulo");
        }

        this.number = number;
        this.type = type;
        this.status = SpaceStatus.AVAILABLE;
    }

    public int getNumber() {
        return number;
    }

    public SpaceType getType() {
        return type;
    }

    public SpaceStatus getStatus() {
        return status;
    }

    public boolean isAvailable() {
        return status == SpaceStatus.AVAILABLE;
    }

    public boolean isUsable() {
        return status != SpaceStatus.OUT_OF_SERVICE;
    }

    public boolean isCompatibleWith(Vehicle vehicle) {
        return vehicle != null && type == vehicle.getRequiredSpaceType();
    }

    public boolean occupy() {
        if (!isAvailable()) {
            return false;
        }
        status = SpaceStatus.OCCUPIED;
        return true;
    }

    public boolean release() {
        if (status != SpaceStatus.OCCUPIED) {
            return false;
        }
        status = SpaceStatus.AVAILABLE;
        return true;
    }

    public boolean putOutOfService() {
        if (status != SpaceStatus.AVAILABLE) {
            return false;
        }
        status = SpaceStatus.OUT_OF_SERVICE;
        return true;
    }

    public boolean restoreService() {
        if (status != SpaceStatus.OUT_OF_SERVICE) {
            return false;
        }
        status = SpaceStatus.AVAILABLE;
        return true;
    }
}
