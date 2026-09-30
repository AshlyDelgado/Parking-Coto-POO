package cr.ac.una.parking.coto.model;

import cr.ac.una.parking.coto.enums.SpaceType;
import cr.ac.una.parking.coto.enums.VehicleType;
import cr.ac.una.parking.coto.exception.ParkingException;

/**
 * Represents the common abstraction for every vehicle admitted by the parking
 * system.
 *
 * <p>All vehicles share the same core information: plate, brand, model, color,
 * and category. Concrete subclasses specialize their behavior by redefining
 * the required parking space type and the fee calculation, which is the main
 * mechanism used to apply polymorphism in the project.</p>
 *
 * <p>The design avoids external type checks such as {@code instanceof} or
 * chained conditionals to decide pricing. Instead, the system delegates the
 * calculation to the concrete object that is actually being used.</p>
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.0
 */
public abstract class Vehicle {

    /** Unique plate used to identify the vehicle in the parking registry. */
    private final String plate;
    /** Vehicle brand. */
    private final String brand;
    /** Vehicle model. */
    private final String model;
    /** Vehicle color. */
    private final String color;
    /** Vehicle category, such as car, motorcycle, or freight. */
    private final VehicleType type;

    /**
     * Creates a vehicle with the required basic information.
     *
     * @param plate vehicle plate identifier
     * @param brand vehicle brand
     * @param model vehicle model
     * @param color vehicle color
     * @param type vehicle category
     * @throws ParkingException if any required field is blank or the type is null
     */
    protected Vehicle(String plate, String brand, String model,
                      String color, VehicleType type) {
        if (isBlank(plate)) {
            throw new ParkingException("La placa del vehículo no puede estar vacía");
        }
        if (isBlank(brand)) {
            throw new ParkingException("La marca del vehículo no puede estar vacía");
        }
        if (isBlank(model)) {
            throw new ParkingException("El modelo del vehículo no puede estar vacío");
        }
        if (isBlank(color)) {
            throw new ParkingException("El color del vehículo no puede estar vacío");
        }
        if (type == null) {
            throw new ParkingException("El tipo de vehículo no puede ser nulo");
        }
        this.plate = plate.trim().toUpperCase();
        this.brand = brand.trim();
        this.model = model.trim();
        this.color = color.trim();
        this.type = type;
    }

    /**
     * Validates whether a string value is null or blank.
     *
     * @param value value to validate
     * @return {@code true} if the value is null or empty after trimming
     */
    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Returns the vehicle plate.
     *
     * @return plate identifier
     */
    public String getPlate() {
        return plate;
    }

    /**
     * Returns the vehicle brand.
     *
     * @return brand name
     */
    public String getBrand() {
        return brand;
    }

    /**
     * Returns the vehicle model.
     *
     * @return model name
     */
    public String getModel() {
        return model;
    }

    /**
     * Returns the vehicle color.
     *
     * @return color description
     */
    public String getColor() {
        return color;
    }

    /**
     * Returns the vehicle category.
     *
     * @return enumerated type representing the vehicle category
     */
    public VehicleType getType() {
        return type;
    }

    /**
     * Returns the parking space type required by the concrete vehicle.
     *
     * @return compatible parking-space category
     */
    public abstract SpaceType getRequiredSpaceType();

    /**
     * Calculates the fee for a given number of charged hours according to the
     * concrete vehicle subtype.
     *
     * @param chargedHours total hours charged for the stay
     * @return calculated amount for the current vehicle type
     */
    public abstract double calculateFee(int chargedHours);
}