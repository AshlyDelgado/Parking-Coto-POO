package cr.ac.una.parking.coto.enums;

/**
 * Operational states a parking space can be in.
 *
 * <p>Each constant also carries the Spanish name used in the user interface.</p>
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.1
 */
public enum SpaceStatus {
    /** The space is free and can be assigned. */
    AVAILABLE("Disponible"),

    /** The space is assigned to an active ticket. */
    OCCUPIED("Ocupado"),

    /** The space cannot be assigned until service is restored. */
    OUT_OF_SERVICE("Fuera de servicio");

    /** Spanish name shown to the user. */
    private final String displayName;

    /**
     * Creates the constant with its Spanish display name.
     *
     * @param displayName name shown to the user
     */
    SpaceStatus(String displayName) {
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
