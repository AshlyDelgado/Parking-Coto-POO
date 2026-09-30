package cr.ac.una.parking.coto.model;

import cr.ac.una.parking.coto.enums.PaymentType;
import cr.ac.una.parking.coto.exception.ParkingException;
import java.time.LocalDateTime;

/**
 * Represents a payment transaction associated with a closed ticket.
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.0
 */
public class Payment {

    /** Unique payment identifier. */
    private final long id;
    /** Ticket that generated the payment. */
    private final ParkingTicket ticket;
    /** Date and time when the payment was registered. */
    private final LocalDateTime paymentDateTime;
    /** Amount paid by the customer. */
    private final double amount;
    /** Payment method used. */
    private final PaymentType type;

    /**
     * Creates a new payment record.
     *
     * @param id unique payment identifier
     * @param ticket closed ticket that was paid
     * @param paymentDateTime date and time of payment
     * @param amount amount charged
     * @param type method used to settle the payment
     * @throws ParkingException if required values are null or invalid
     */
    public Payment(long id, ParkingTicket ticket, LocalDateTime paymentDateTime,
                  double amount, PaymentType type) {
        if (ticket == null) {
            throw new ParkingException("El ticket no puede ser nulo");
        }
        if (paymentDateTime == null) {
            throw new ParkingException("La fecha del pago no puede ser nula");
        }
        if (type == null) {
            throw new ParkingException("El tipo de pago no puede ser nulo");
        }
        if (amount < 0) {
            throw new ParkingException("El monto del pago no puede ser negativo");
        }

        this.id = id;
        this.ticket = ticket;
        this.paymentDateTime = paymentDateTime;
        this.amount = amount;
        this.type = type;
    }

    /**
     * Returns the payment identifier.
     *
     * @return payment numeric identifier
     */
    public long getId() {
        return id;
    }

    /**
     * Returns the ticket related to this payment.
     *
     * @return associated parking ticket
     */
    public ParkingTicket getTicket() {
        return ticket;
    }

    /**
     * Returns the date and time when the payment was registered.
     *
     * @return local payment timestamp
     */
    public LocalDateTime getPaymentDateTime() {
        return paymentDateTime;
    }

    /**
     * Returns the amount paid.
     *
     * @return payment value
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Returns the payment method used to settle the ticket.
     *
     * @return payment type such as cash or card
     */
    public PaymentType getType() {
        return type;
    }
}
