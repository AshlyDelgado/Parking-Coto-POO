package cr.ac.una.parking.coto.exception;

/**
 * Thrown when a registry attempts to store a duplicate object value.
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.0
 */
public class DuplicateRecordException extends ParkingException {

    /**
     * Creates the exception with the validation message.
     *
     * @param message explanation of the duplicate conflict
     */
    public DuplicateRecordException(String message) {
        super(message);
    }
}