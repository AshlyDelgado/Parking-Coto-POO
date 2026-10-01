package cr.ac.una.parking.coto.model;

import cr.ac.una.parking.coto.enums.SpaceType;
import cr.ac.una.parking.coto.enums.VehicleType;
import cr.ac.una.parking.coto.pricing.PricingPolicy;
import cr.ac.una.parking.coto.pricing.Tariff;

/**
 * Represents a freight vehicle within the parking system.
 *
 * <p>The class stores the pricing policy configured for freight vehicles and
 * delegates the fee calculation to it.</p>
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.1
 */
public class FreightVehicle extends Vehicle {

    /** Pricing policy used to compute the amount for a freight vehicle. */
    private final PricingPolicy pricing;

    /**
     * Creates a freight vehicle with the required property values.
     *
     * @param plate vehicle plate
     * @param brand vehicle brand
     * @param model vehicle model
     * @param color vehicle color
     */
    public FreightVehicle(String plate, String brand, String model, String color) {
        super(plate, brand, model, color, VehicleType.FREIGHT);
        this.pricing = Tariff.forFreight();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SpaceType getRequiredSpaceType() {
        return SpaceType.FREIGHT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double calculateFee(int chargedHours) {
        return pricing.calculate(chargedHours);
    }
}
