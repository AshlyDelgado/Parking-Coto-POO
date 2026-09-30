package cr.ac.una.parking.coto.exception;

/**
 * Thrown when a ticket operation is attempted outside its valid lifecycle state.
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.0
 */
public class InvalidTicketStateException extends ParkingException {

    /**
     * Creates the exception with a detailed lifecycle-message.
     *
     * @param message business-rule validation message
     */
    public InvalidTicketStateException(String message) {
        super(message);
    }
}
