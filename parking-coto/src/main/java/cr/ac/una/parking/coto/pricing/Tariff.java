/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.parking.coto.pricing;

/**
 *
 * @author Ashly
 */
public class Tariff implements PricingPolicy {

    private final double hourlyRate;
    private final double dailyCap;
    private final int capThresholdHours;
    private final int periodHours;

    public Tariff(double hourlyRate, double dailyCap,
                  int capThresholdHours, int periodHours) {
        this.hourlyRate = hourlyRate;
        this.dailyCap = dailyCap;
        this.capThresholdHours = capThresholdHours;
        this.periodHours = periodHours;
    }

    @Override
    public double calculate(int chargedHours) {
        if (chargedHours < capThresholdHours) {
            return chargedHours * hourlyRate;
        }
        int fullPeriods = chargedHours / periodHours;
        int remainingHours = chargedHours % periodHours;
        double remainder = Math.min(remainingHours * hourlyRate, dailyCap);
        return fullPeriods * dailyCap + remainder;
    }
}