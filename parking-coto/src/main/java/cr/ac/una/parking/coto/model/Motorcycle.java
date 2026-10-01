package cr.ac.una.parking.coto.model;

import cr.ac.una.parking.coto.enums.SpaceType;
import cr.ac.una.parking.coto.enums.VehicleType;
import cr.ac.una.parking.coto.pricing.PricingPolicy;
import cr.ac.una.parking.coto.pricing.Tariff;

/**
 * Represents a motorcycle in the parking management system.
 *
 * <p>The class stores the pricing policy configured for motorcycles and
 * delegates the fee calculation to it.</p>
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.1
 */
public class Motorcycle extends Vehicle {

    /** Pricing policy used to compute the amount for a motorcycle. */
    private final PricingPolicy pricing;

    /**
     * Creates a motorcycle with the required attributes.
     *
     * @param plate vehicle plate
     * @param brand vehicle brand
     * @param model vehicle model
     * @param color vehicle color
     */
    public Motorcycle(String plate, String brand, String model, String color) {
        super(plate, brand, model, color, VehicleType.MOTORCYCLE);
        this.pricing = Tariff.forMotorcycle();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SpaceType getRequiredSpaceType() {
        return SpaceType.MOTORCYCLE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double calculateFee(int chargedHours) {
        return pricing.calculate(chargedHours);
    }
}
