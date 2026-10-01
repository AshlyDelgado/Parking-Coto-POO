package cr.ac.una.parking.coto.pricing;

/**
 * Implements the hourly pricing of the parking system.
 *
 * <p>This class is the only place where the hourly rates and the daily caps of
 * every vehicle category are defined. A {@code Tariff} only knows how to
 * charge by the hour; the daily cap is applied by wrapping it in a
 * {@link DailyCapPolicy}, so each rule can change independently of the
 * other.</p>
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.1
 */
public class Tariff implements PricingPolicy {

    /** Hourly rate for motorcycles. */
    private static final double MOTORCYCLE_HOURLY_RATE = 500.0;
    /** Hourly rate for cars. */
    private static final double CAR_HOURLY_RATE = 900.0;
    /** Hourly rate for freight vehicles. */
    private static final double FREIGHT_HOURLY_RATE = 1500.0;

    /** Maximum charge per daily period for motorcycles. */
    private static final double MOTORCYCLE_DAILY_CAP = 4000.0;
    /** Maximum charge per daily period for cars. */
    private static final double CAR_DAILY_CAP = 7000.0;
    /** Maximum charge per daily period for freight vehicles. */
    private static final double FREIGHT_DAILY_CAP = 11000.0;

    /** Rate charged for each hour of stay. */
    private final double hourlyRate;

    /**
     * Builds a plain hourly tariff.
     *
     * @param hourlyRate rate charged per hour
     */
    private Tariff(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    /**
     * Returns the complete pricing policy for motorcycles.
     *
     * @return hourly tariff for motorcycles with its daily cap applied
     */
    public static PricingPolicy forMotorcycle() {
        return new DailyCapPolicy(new Tariff(MOTORCYCLE_HOURLY_RATE), MOTORCYCLE_DAILY_CAP);
    }

    /**
     * Returns the complete pricing policy for cars.
     *
     * @return hourly tariff for cars with its daily cap applied
     */
    public static PricingPolicy forCar() {
        return new DailyCapPolicy(new Tariff(CAR_HOURLY_RATE), CAR_DAILY_CAP);
    }

    /**
     * Returns the complete pricing policy for freight vehicles.
     *
     * @return hourly tariff for freight vehicles with its daily cap applied
     */
    public static PricingPolicy forFreight() {
        return new DailyCapPolicy(new Tariff(FREIGHT_HOURLY_RATE), FREIGHT_DAILY_CAP);
    }

    /**
     * Calculates the plain hourly amount for the supplied charged hours.
     *
     * @param chargedHours number of hours to charge
     * @return hours multiplied by the hourly rate, or zero if there are no hours
     */
    @Override
    public double calculate(int chargedHours) {
        if (chargedHours <= 0) {
            return 0.0;
        }
        return chargedHours * hourlyRate;
    }
}
