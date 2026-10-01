package cr.ac.una.parking.coto.enums;

/**
 * Payment methods accepted by the parking lot.
 *
 * <p>Each constant also carries the Spanish name used in the user interface.</p>
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.1
 */
public enum PaymentType {
    /** Payment in cash. */
    CASH("Efectivo"),

    /** Payment with a credit or debit card. */
    CARD("Tarjeta"),

    /** Payment through SINPE Móvil. */
    SINPE_MOVIL("SINPE Móvil");

    /** Spanish name shown to the user. */
    private final String displayName;

    /**
     * Creates the constant with its Spanish display name.
     *
     * @param displayName name shown to the user
     */
    PaymentType(String displayName) {
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
