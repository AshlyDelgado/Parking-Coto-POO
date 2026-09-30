package cr.ac.una.parking.coto.model;

import cr.ac.una.parking.coto.enums.SpaceType;
import cr.ac.una.parking.coto.enums.VehicleType;
import cr.ac.una.parking.coto.pricing.PricingPolicy;
import cr.ac.una.parking.coto.pricing.Tariff;

public class Car extends Vehicle {

    private final PricingPolicy pricing;

    public Car(String plate, String brand, String model, String color) {
        super(plate, brand, model, color, VehicleType.CAR);
        this.pricing = Tariff.forCar();
    }

    @Override
    public SpaceType getRequiredSpaceType() {
        return SpaceType.CAR;
    }

    @Override
    public double calculateFee(int chargedHours) {
        return pricing.calculate(chargedHours);
    }
}