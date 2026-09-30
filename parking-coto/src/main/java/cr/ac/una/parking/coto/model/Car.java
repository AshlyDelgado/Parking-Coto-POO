package cr.ac.una.parking.coto.model;

import cr.ac.una.parking.coto.enums.SpaceType;
import cr.ac.una.parking.coto.enums.VehicleType;
import cr.ac.una.parking.coto.pricing.PricingPolicy;
import cr.ac.una.parking.coto.pricing.Tariff;

/**
 * Represents a standard passenger car in the parking lot.
 *
 * <p>The class stores the specific pricing policy configured for cars and uses
 * the {@link Vehicle} polymorphic API to compute the fee by delegating to the
 * tariff implementation.</p>
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.0
 */
public class Car extends Vehicle {

    /** Pricing policy used to compute the amount for a car. */
    private final PricingPolicy pricing;

    /**
     * Creates a car with the required identification fields.
     *
     * @param plate vehicle plate
     * @param brand vehicle brand
     * @param model vehicle model
     * @param color vehicle color
     */
    public Car(String plate, String brand, String model, String color) {
        super(plate, brand, model, color, VehicleType.CAR);
        this.pricing = Tariff.forCar();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SpaceType getRequiredSpaceType() {
        return SpaceType.CAR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double calculateFee(int chargedHours) {
        return pricing.calculate(chargedHours);
    }
}