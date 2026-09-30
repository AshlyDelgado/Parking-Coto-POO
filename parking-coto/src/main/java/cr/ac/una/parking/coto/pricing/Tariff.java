package cr.ac.una.parking.coto.pricing;

public class Tariff implements PricingPolicy {

    public static final int CAP_THRESHOLD_HOURS = 10;
    public static final int PERIOD_HOURS = 24;

    private final double hourlyRate;
    private final double dailyCap;

    private Tariff(double hourlyRate, double dailyCap) {
        this.hourlyRate = hourlyRate;
        this.dailyCap = dailyCap;
    }

    public static Tariff forMotorcycle() {
        return new Tariff(500.0, 4000.0);
    }

    public static Tariff forCar() {
        return new Tariff(900.0, 7000.0);
    }

    public static Tariff forFreight() {
        return new Tariff(1500.0, 11000.0);
    }

    @Override
    public double calculate(int chargedHours) {
        if (chargedHours <= 0) {
            return 0.0;
        }

        if (chargedHours < CAP_THRESHOLD_HOURS) {
            return chargedHours * hourlyRate;
        }

        int dailyPeriods = (chargedHours + PERIOD_HOURS - 1) / PERIOD_HOURS;
        return dailyPeriods * dailyCap;
    }
}