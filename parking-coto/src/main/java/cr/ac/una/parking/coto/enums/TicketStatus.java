package cr.ac.una.parking.coto.enums;

/**
 * Lifecycle states of a parking ticket.
 *
 * <p>Each constant also carries the Spanish name used in the user interface.</p>
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.1
 */
public enum TicketStatus {
    /** The vehicle is inside the parking lot. */
    ACTIVE("Activo"),

    /** The exit was registered and the amount is pending payment. */
    CLOSED("Cerrado"),

    /** The amount was paid. */
    PAID("Pagado");

    /** Spanish name shown to the user. */
    private final String displayName;

    /**
     * Creates the constant with its Spanish display name.
     *
     * @param displayName name shown to the user
     */
    TicketStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the Spanish name shown to the user.
     *
     * @return display name of the constant
     */
    public String getDisplayName() {
        return displayName;
    }
}
