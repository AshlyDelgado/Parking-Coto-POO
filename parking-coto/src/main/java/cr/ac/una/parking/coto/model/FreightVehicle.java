package cr.ac.una.parking.coto.model;

import cr.ac.una.parking.coto.enums.SpaceType;
import cr.ac.una.parking.coto.enums.VehicleType;
import cr.ac.una.parking.coto.pricing.Tariff;

public class FreightVehicle extends Vehicle {

    public FreightVehicle(String plate, String brand, String model, String color) {
        super(plate, brand, model, color, VehicleType.FREIGHT);
    }

    @Override
    public SpaceType getRequiredSpaceType() {
        return SpaceType.FREIGHT;
    }

    @Override
    public double calculateFee(int chargedHours) {
        return Tariff.forFreight().calculate(chargedHours);
    }
}
