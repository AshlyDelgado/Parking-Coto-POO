package cr.ac.una.parking.coto.pricing;

import cr.ac.una.parking.coto.exception.ParkingException;

/**
 * Applies a maximum daily amount on top of any other pricing policy.
 *
 * <p>This class decorates another {@link PricingPolicy}: it asks the wrapped
 * policy for the normal amount and then limits it. Because it works with any
 * policy, the cap can change, or be removed from a vehicle type, without
 * touching the vehicles or the hourly tariffs.</p>
 *
 * <p>From {@link #CAP_THRESHOLD_HOURS} charged hours onward, every full
 * {@link #PERIOD_HOURS}-hour period costs at most the daily cap, and the
 * remaining hours of the last period are also limited by it.</p>
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.0
 */
public class DailyCapPolicy implements PricingPolicy {

    /** Charged hours from which the daily cap starts to apply. */
    private static final int CAP_THRESHOLD_HOURS = 10;
    /** Length in hours of one daily billing period. */
    private static final int PERIOD_HOURS = 24;

    /** Policy that calculates the normal amount before the cap. */
    private final PricingPolicy basePolicy;
    /** Maximum amount charged for one daily period. */
    private final double dailyCap;

    /**
     * Creates a policy that limits the amount of another policy.
     *
     * @param basePolicy policy used to calculate the amount before the cap
     * @param dailyCap maximum amount for each daily period
     * @throws ParkingException if the base policy is null or the cap is not positive
     */
    public DailyCapPolicy(PricingPolicy basePolicy, double dailyCap) {
        if (basePolicy == null) {
            throw new ParkingException("La política base no puede ser nula");
        }
        if (dailyCap <= 0) {
            throw new ParkingException("El tope diario debe ser mayor que cero");
        }
        this.basePolicy = basePolicy;
        this.dailyCap = dailyCap;
    }

    /**
     * Calculates the amount for the supplied charged hours, applying the cap
     * when the stay reaches the threshold.
     *
     * @param chargedHours number of hours to charge
     * @return amount without exceeding the daily cap for each period
     */
    @Override
    public double calculate(int chargedHours) {
        if (chargedHours < CAP_THRESHOLD_HOURS) {
            return basePolicy.calculate(chargedHours);
        }

        int fullPeriods = chargedHours / PERIOD_HOURS;
        int remainingHours = chargedHours % PERIOD_HOURS;
        double remainingAmount = Math.min(basePolicy.calculate(remainingHours), dailyCap);
        return fullPeriods * dailyCap + remainingAmount;
    }
}
