package cr.ac.una.parking.coto.enums;

/**
 * Lifecycle states of a parking ticket.
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.0
 */
public enum TicketStatus {
    /** The ticket is currently active and the vehicle remains in the lot. */
    ACTIVE,
    /** The ticket has been closed after the vehicle leaves. */
    CLOSED,
    /** The ticket has been paid and settled. */
    PAID
}
