package cr.ac.una.parking.coto.exception;

/**
 * Base runtime exception for all business-rule violations in the parking lot.
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.0
 */
public class ParkingException extends RuntimeException {

    /**
     * Creates the exception with the validation message.
     *
     * @param message descriptive message for the rule violation
     */
    public ParkingException(String message) {
        super(message);
    }
}

