package cr.ac.una.parking.coto.model;

import cr.ac.una.parking.coto.enums.PaymentType;
import cr.ac.una.parking.coto.exception.ParkingException;
import java.time.LocalDateTime;

public class Payment {

    private final long id;
    private final ParkingTicket ticket;
    private final LocalDateTime paymentDateTime;
    private final double amount;
    private final PaymentType type;

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

    public long getId() {
        return id;
    }

    public ParkingTicket getTicket() {
        return ticket;
    }

    public LocalDateTime getPaymentDateTime() {
        return paymentDateTime;
    }

    public double getAmount() {
        return amount;
    }

    public PaymentType getType() {
        return type;
    }
}
