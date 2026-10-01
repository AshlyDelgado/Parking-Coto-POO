package cr.ac.una.parking.coto.enums;

/**
 * Categories of parking spaces managed by the system.
 *
 * <p>Each constant also carries the Spanish name used in the user interface.</p>
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.1
 */
public enum SpaceType {
    /** Space reserved for motorcycles. */
    MOTORCYCLE("Motocicleta"),

    /** Space reserved for passenger cars. */
    CAR("Automóvil"),

    /** Space reserved for freight vehicles. */
    FREIGHT("Carga");

    /** Spanish name shown to the user. */
    private final String displayName;

    /**
     * Creates the constant with its Spanish display name.
     *
     * @param displayName name shown to the user
     */
    SpaceType(String displayName) {
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
