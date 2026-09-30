package cr.ac.una.parking.coto.model;

import cr.ac.una.parking.coto.enums.SpaceType;
import cr.ac.una.parking.coto.enums.VehicleType;
import cr.ac.una.parking.coto.pricing.Tariff;

/**
 * Represents a freight vehicle within the parking system.
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.0
 */
public class FreightVehicle extends Vehicle {

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
        return Tariff.forFreight().calculate(chargedHours);
    }
}
