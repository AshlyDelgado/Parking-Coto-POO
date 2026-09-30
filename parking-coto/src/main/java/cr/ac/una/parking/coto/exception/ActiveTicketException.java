package cr.ac.una.parking.coto.exception;

/**
 * Thrown when a vehicle attempts to register a second active ticket.
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.0
 */
public class ActiveTicketException extends ParkingException {

    /**
     * Creates the exception with the associated business-rule message.
     *
     * @param message description of why the operation is invalid
     */
    public ActiveTicketException(String message) {
        super(message);
    }
}