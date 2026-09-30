package cr.ac.una.parking.coto.model;

import cr.ac.una.parking.coto.enums.SpaceType;
import cr.ac.una.parking.coto.enums.VehicleType;
import cr.ac.una.parking.coto.exception.ParkingException;

public abstract class Vehicle {

    private final String plate;
    private final String brand;
    private final String model;
    private final String color;
    private final VehicleType type;

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

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public String getPlate() {
        return plate;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getColor() {
        return color;
    }

    public VehicleType getType() {
        return type;
    }

    public abstract SpaceType getRequiredSpaceType();

    public abstract double calculateFee(int chargedHours);
}