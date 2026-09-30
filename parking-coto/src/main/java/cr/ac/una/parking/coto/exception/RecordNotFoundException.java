package cr.ac.una.parking.coto.exception;

/**
 * Thrown when a required parking record cannot be found in the registry.
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.0
 */
public class RecordNotFoundException extends ParkingException {

    /**
     * Creates the exception with a message describing the missing record.
     *
     * @param message explanation of the missing entity
     */
    public RecordNotFoundException(String message) {
        super(message);
    }
}