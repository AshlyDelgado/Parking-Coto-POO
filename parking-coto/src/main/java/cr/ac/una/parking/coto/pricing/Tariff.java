package cr.ac.una.parking.coto.pricing;

/**
 * Implements the pricing policy for the parking system.
 *
 * <p>The tariff defines the hourly rate and the maximum daily cap for each
 * vehicle category. Once a stay exceeds the threshold of ten hours, the
 * calculation switches to cumulative daily periods instead of charging the
 * remaining hours linearly.</p>
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.0
 */
public class Tariff implements PricingPolicy {

    /** Minimum hourly threshold before the daily-cap logic is applied. */
    public static final int CAP_THRESHOLD_HOURS = 10;
    /** Number of hours considered in a billing cycle for the cap calculation. */
    public static final int PERIOD_HOURS = 24;

    /** Rate charged for each hour before the cap threshold. */
    private final double hourlyRate;
    /** Maximum charge for a daily billing period. */
    private final double dailyCap;

    /**
     * Builds a tariff for a specific vehicle category.
     *
     * @param hourlyRate rate per hour for the category
     * @param dailyCap maximum charge for a full billing period
     */
    private Tariff(double hourlyRate, double dailyCap) {
        this.hourlyRate = hourlyRate;
        this.dailyCap = dailyCap;
    }

    /**
     * Returns the tariff configuration for motorcycles.
     *
     * @return motorcycle tariff
     */
    public static Tariff forMotorcycle() {
        return new Tariff(500.0, 4000.0);
    }

    /**
     * Returns the tariff configuration for cars.
     *
     * @return car tariff
     */
    public static Tariff forCar() {
        return new Tariff(900.0, 7000.0);
    }

    /**
     * Returns the tariff configuration for freight vehicles.
     *
     * @return freight tariff
     */
    public static Tariff forFreight() {
        return new Tariff(1500.0, 11000.0);
    }

    /**
     * Calculates the amount for the supplied charged hours.
     *
     * @param chargedHours number of hours used to calculate the fee
     * @return total billed amount for the stay
     */
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