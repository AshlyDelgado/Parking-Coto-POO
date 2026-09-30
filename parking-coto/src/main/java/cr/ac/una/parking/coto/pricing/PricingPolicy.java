package cr.ac.una.parking.coto.pricing;

/**
 * Defines the contract for charging a stay according to a vehicle type.
 *
 * <p>The parking system delegates fee calculation to implementations of this
 * interface, making the pricing rules polymorphic and centralized in a single
 * policy component.</p>
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.0
 */
public interface PricingPolicy {
    /**
     * Calculates the amount corresponding to a number of charged hours.
     *
     * @param chargedHours number of hours considered in the calculation
     * @return total charge for the supplied stay duration
     */
    double calculate(int chargedHours);
}
