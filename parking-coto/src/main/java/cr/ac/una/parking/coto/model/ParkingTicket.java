package cr.ac.una.parking.coto.model;

import cr.ac.una.parking.coto.enums.TicketStatus;
import cr.ac.una.parking.coto.exception.InvalidTicketStateException;
import cr.ac.una.parking.coto.exception.ParkingException;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Represents an active or closed vehicle stay in the parking lot.
 *
 * <p>Each ticket records the entry time, the assigned space, the vehicle, and
 * the resulting amount whenever the stay is closed. The amount is calculated by
 * the concrete vehicle implementation, which delegates pricing to the tariff
 * rules defined for the vehicle type.</p>
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.0
 */
public class ParkingTicket {

    /** Unique ticket identifier. */
    private final long id;
    /** Vehicle associated with the ticket. */
    private final Vehicle vehicle;
    /** Space assigned to the entry. */
    private final ParkingSpace space;
    /** Entry date and time. */
    private final LocalDateTime entryTime;
    /** Exit date and time, set only when the ticket is closed. */
    private LocalDateTime exitTime;
    /** Current lifecycle state of the ticket. */
    private TicketStatus status;
    /** Amount charged to the customer. */
    private double amount;

    /**
     * Creates a new parking ticket with an active status.
     *
     * @param id unique identifier for the ticket
     * @param vehicle vehicle entering the lot
     * @param space assigned parking space
     * @param entryTime time at which the vehicle entered
     * @throws ParkingException if any required input is null or invalid
     */
    public ParkingTicket(long id, Vehicle vehicle, ParkingSpace space, LocalDateTime entryTime) {
        if (vehicle == null) {
            throw new ParkingException("El vehículo no puede ser nulo");
        }
        if (space == null) {
            throw new ParkingException("El espacio no puede ser nulo");
        }
        if (entryTime == null) {
            throw new ParkingException("La fecha de entrada no puede ser nula");
        }

        this.id = id;
        this.vehicle = vehicle;
        this.space = space;
        this.entryTime = entryTime;
        this.status = TicketStatus.ACTIVE;
        this.amount = 0.0;
    }

    /**
     * Returns the ticket identifier.
     *
     * @return ticket number
     */
    public long getId() {
        return id;
    }

    /**
     * Returns the vehicle attached to the ticket.
     *
     * @return vehicle associated to the stay
     */
    public Vehicle getVehicle() {
        return vehicle;
    }

    /**
     * Returns the parking space assigned to the entry.
     *
     * @return allocated space
     */
    public ParkingSpace getSpace() {
        return space;
    }

    /**
     * Returns the entry timestamp.
     *
     * @return entry time in local date-time format
     */
    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    /**
     * Returns the exit timestamp, if the ticket has already been closed.
     *
     * @return exit time; may be null for active tickets
     */
    public LocalDateTime getExitTime() {
        return exitTime;
    }

    /**
     * Returns the current ticket status.
     *
     * @return lifecycle state of the ticket
     */
    public TicketStatus getStatus() {
        return status;
    }

    /**
     * Returns the amount charged for the stay.
     *
     * @return computed amount in local currency units
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Indicates whether the ticket is still active, that is, whether the
     * vehicle is currently inside the parking lot.
     *
     * @return {@code true} if the ticket status is {@code ACTIVE}
     */
    public boolean isActive() {
        return status == TicketStatus.ACTIVE;
    }

    /**
     * Calculates how long the vehicle stayed, in whole minutes.
     *
     * @param exitTime time at which the vehicle leaves the parking lot
     * @return minutes elapsed between the entry and the supplied exit time
     * @throws ParkingException if the exit time is null or earlier than the entry time
     */
    public long calculateStayMinutes(LocalDateTime exitTime) {
        requireValidExit(exitTime);
        return Duration.between(entryTime, exitTime).toMinutes();
    }

    /**
     * Calculates the hours to charge for the stay. Any fraction of an hour is
     * charged as a complete hour.
     *
     * @param exitTime time at which the vehicle leaves the parking lot
     * @return charged hours, or zero if the stay has no duration
     * @throws ParkingException if the exit time is null or earlier than the entry time
     */
    public int calculateChargedHours(LocalDateTime exitTime) {
        requireValidExit(exitTime);
        Duration stay = Duration.between(entryTime, exitTime);
        long wholeHours = stay.toHours();
        boolean hasPartialHour = !stay.minusHours(wholeHours).isZero();
        return (int) (hasPartialHour ? wholeHours + 1 : wholeHours);
    }

    /**
     * Closes the active ticket and calculates the corresponding amount.
     *
     * @param exitTime time at which the vehicle leaves the parking lot
     * @throws InvalidTicketStateException if the ticket is not active
     * @throws ParkingException if the exit time is null or earlier than the entry time
     */
    public void close(LocalDateTime exitTime) {
        if (status != TicketStatus.ACTIVE) {
            throw new InvalidTicketStateException("El ticket no está activo");
        }
        requireValidExit(exitTime);

        this.amount = vehicle.calculateFee(calculateChargedHours(exitTime));
        this.exitTime = exitTime;
        this.status = TicketStatus.CLOSED;
        space.release();
    }

    /**
     * Rejects an exit time that is missing or earlier than the entry time.
     *
     * @param exitTime exit time to validate
     * @throws ParkingException if the exit time is null or earlier than the entry time
     */
    private void requireValidExit(LocalDateTime exitTime) {
        if (exitTime == null || exitTime.isBefore(entryTime)) {
            throw new ParkingException("La salida no puede ser anterior a la entrada");
        }
    }

    /**
     * Marks the ticket as paid after it has been closed.
     *
     * @throws InvalidTicketStateException if the ticket is not in the closed state
     */
    public void markAsPaid() {
        if (status != TicketStatus.CLOSED) {
            throw new InvalidTicketStateException("Solo se puede pagar un ticket cerrado");
        }
        status = TicketStatus.PAID;
    }
}
