package cr.ac.una.parking.coto.model;

import cr.ac.una.parking.coto.enums.TicketStatus;
import cr.ac.una.parking.coto.exception.InvalidTicketStateException;
import cr.ac.una.parking.coto.exception.ParkingException;
import java.time.Duration;
import java.time.LocalDateTime;

public class ParkingTicket {

    private final long id;
    private final Vehicle vehicle;
    private final ParkingSpace space;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private TicketStatus status;
    private double amount;

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

    public long getId() {
        return id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ParkingSpace getSpace() {
        return space;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public double getAmount() {
        return amount;
    }

    public void close(LocalDateTime exitTime) {
        if (status != TicketStatus.ACTIVE) {
            throw new InvalidTicketStateException("El ticket no está activo");
        }
        if (exitTime == null || exitTime.isBefore(entryTime)) {
            throw new ParkingException("La salida no puede ser anterior a la entrada");
        }

        long minutes = Duration.between(entryTime, exitTime).toMinutes();
        int chargedHours = minutes <= 0 ? 0 : (int) Math.ceil(minutes / 60.0);
        this.amount = vehicle.calculateFee(chargedHours);
        this.exitTime = exitTime;
        this.status = TicketStatus.CLOSED;
        space.release();
    }

    public void markAsPaid() {
        if (status != TicketStatus.CLOSED) {
            throw new InvalidTicketStateException("Solo se puede pagar un ticket cerrado");
        }
        status = TicketStatus.PAID;
    }
}
