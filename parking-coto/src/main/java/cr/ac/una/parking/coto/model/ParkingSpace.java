package cr.ac.una.parking.coto.model;

import cr.ac.una.parking.coto.enums.SpaceStatus;
import cr.ac.una.parking.coto.enums.SpaceType;
import cr.ac.una.parking.coto.exception.ParkingException;

/**
 * Represents a physical parking slot and its current state.
 *
 * <p>Each space is identified by a numeric code and a {@link SpaceType}. The
 * state can be available, occupied, or out of service. The class validates the
 * compatibility between the space and the vehicle using the vehicle type
 * information exposed by the abstraction.</p>
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.0
 */
public class ParkingSpace {

    /** Unique numeric identifier of the parking space. */
    private final int number;
    /** Type of vehicle that matches this parking space. */
    private final SpaceType type;
    /** Current availability state. */
    private SpaceStatus status;

    /**
     * Creates a new parking space with a positive identifier and its category.
     *
     * @param number numeric identifier of the space
     * @param type compatible vehicle type for the space
     * @throws ParkingException if the identifier is invalid or the type is null
     */
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

    /**
     * Returns the numeric identifier of the space.
     *
     * @return space number
     */
    public int getNumber() {
        return number;
    }

    /**
     * Returns the type of vehicle compatible with this space.
     *
     * @return compatible space type
     */
    public SpaceType getType() {
        return type;
    }

    /**
     * Returns the current state of the space.
     *
     * @return current status: available, occupied, or out of service
     */
    public SpaceStatus getStatus() {
        return status;
    }

    /**
     * Checks whether the space is currently free.
     *
     * @return {@code true} if the space is available
     */
    public boolean isAvailable() {
        return status == SpaceStatus.AVAILABLE;
    }

    /**
     * Checks whether the space may be used by the parking system.
     *
     * @return {@code true} unless the space is out of service
     */
    public boolean isUsable() {
        return status != SpaceStatus.OUT_OF_SERVICE;
    }

    /**
     * Determines whether the space is compatible with a given vehicle.
     *
     * @param vehicle vehicle being evaluated
     * @return {@code true} if the vehicle type matches the space type
     */
    public boolean isCompatibleWith(Vehicle vehicle) {
        return vehicle != null && type == vehicle.getRequiredSpaceType();
    }

    /**
     * Occupies the space if it is currently free.
     *
     * @return {@code true} when the state changed to occupied
     */
    public boolean occupy() {
        if (!isAvailable()) {
            return false;
        }
        status = SpaceStatus.OCCUPIED;
        return true;
    }

    /**
     * Releases the space if it is occupied.
     *
     * @return {@code true} when the state changed to available
     */
    public boolean release() {
        if (status != SpaceStatus.OCCUPIED) {
            return false;
        }
        status = SpaceStatus.AVAILABLE;
        return true;
    }

    /**
     * Moves the space to out-of-service mode, but only while it is available.
     *
     * @return {@code true} if the space was successfully marked out of service
     */
    public boolean putOutOfService() {
        if (status != SpaceStatus.AVAILABLE) {
            return false;
        }
        status = SpaceStatus.OUT_OF_SERVICE;
        return true;
    }

    /**
     * Restores the space to service when it was previously out of service.
     *
     * @return {@code true} if the state returned to available
     */
    public boolean restoreService() {
        if (status != SpaceStatus.OUT_OF_SERVICE) {
            return false;
        }
        status = SpaceStatus.AVAILABLE;
        return true;
    }
}
