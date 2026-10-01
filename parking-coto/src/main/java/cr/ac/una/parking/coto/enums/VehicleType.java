package cr.ac.una.parking.coto.enums;

/**
 * Supported vehicle categories in the parking system.
 *
 * <p>Each constant also carries the Spanish name used in the user interface.</p>
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.1
 */
public enum VehicleType {
    /** Motorcycles are assigned to motorcycle spaces. */
    MOTORCYCLE("Motocicleta"),

    /** Cars are assigned to passenger-car spaces. */
    CAR("Automóvil"),

    /** Freight vehicles require freight spaces. */
    FREIGHT("Vehículo de carga");

    /** Spanish name shown to the user. */
    private final String displayName;

    /**
     * Creates the constant with its Spanish display name.
     *
     * @param displayName name shown to the user
     */
    VehicleType(String displayName) {
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
