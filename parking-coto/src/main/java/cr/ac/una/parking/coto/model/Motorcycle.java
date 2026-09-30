package cr.ac.una.parking.coto.model;

import cr.ac.una.parking.coto.enums.SpaceType;
import cr.ac.una.parking.coto.enums.VehicleType;
import cr.ac.una.parking.coto.pricing.Tariff;

public class Motorcycle extends Vehicle {

    public Motorcycle(String plate, String brand, String model, String color) {
        super(plate, brand, model, color, VehicleType.MOTORCYCLE);
    }

    @Override
    public SpaceType getRequiredSpaceType() {
        return SpaceType.MOTORCYCLE;
    }

    @Override
    public double calculateFee(int chargedHours) {
        return Tariff.forMotorcycle().calculate(chargedHours);
    }
}
