package cr.ac.una.parking.coto.exception;

/**
 * Thrown when a vehicle requests an unavailable, incompatible, or unusable
 * parking space.
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.0
 */
public class SpaceNotAvailableException extends ParkingException {

    /**
     * Creates the exception with the business-rule message.
     *
     * @param message description of the space availability problem
     */
    public SpaceNotAvailableException(String message) {
        super(message);
    }
}
