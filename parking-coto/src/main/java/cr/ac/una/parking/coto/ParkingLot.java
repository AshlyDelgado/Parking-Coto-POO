package cr.ac.una.parking.coto;

import cr.ac.una.parking.coto.enums.PaymentType;
import cr.ac.una.parking.coto.enums.SpaceType;
import cr.ac.una.parking.coto.enums.TicketStatus;
import cr.ac.una.parking.coto.exception.ActiveTicketException;
import cr.ac.una.parking.coto.exception.DuplicateRecordException;
import cr.ac.una.parking.coto.exception.InvalidTicketStateException;
import cr.ac.una.parking.coto.exception.ParkingException;
import cr.ac.una.parking.coto.exception.RecordNotFoundException;
import cr.ac.una.parking.coto.exception.SpaceNotAvailableException;
import cr.ac.una.parking.coto.model.ParkingSpace;
import cr.ac.una.parking.coto.model.ParkingTicket;
import cr.ac.una.parking.coto.model.Payment;
import cr.ac.una.parking.coto.model.Vehicle;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParkingLot {

    private final Map<String, Vehicle> vehicles;
    private final Map<Integer, ParkingSpace> spaces;
    private final List<ParkingTicket> tickets;
    private final List<Payment> payments;
    private long nextTicketId;
    private long nextPaymentId;

    public ParkingLot() {
        this.vehicles = new HashMap<String, Vehicle>();
        this.spaces = new HashMap<Integer, ParkingSpace>();
        this.tickets = new ArrayList<ParkingTicket>();
        this.payments = new ArrayList<Payment>();
        this.nextTicketId = 1L;
        this.nextPaymentId = 1L;
    }

    public void registerVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            throw new ParkingException("El vehículo no puede ser nulo");
        }
        if (vehicles.containsKey(vehicle.getPlate())) {
            throw new DuplicateRecordException("Ya existe un vehículo con la placa " + vehicle.getPlate());
        }
        vehicles.put(vehicle.getPlate(), vehicle);
    }

    public void registerSpace(ParkingSpace space) {
        if (space == null) {
            throw new ParkingException("El espacio no puede ser nulo");
        }
        if (spaces.containsKey(space.getNumber())) {
            throw new DuplicateRecordException("Ya existe un espacio con el número " + space.getNumber());
        }
        spaces.put(space.getNumber(), space);
    }

    public List<ParkingSpace> getAvailableSpaces() {
        List<ParkingSpace> availableSpaces = new ArrayList<ParkingSpace>();
        for (ParkingSpace space : spaces.values()) {
            if (space.isAvailable()) {
                availableSpaces.add(space);
            }
        }
        return Collections.unmodifiableList(availableSpaces);
    }

    public ParkingTicket registerEntry(Vehicle vehicle, LocalDateTime entryTime) {
        if (vehicle == null) {
            throw new ParkingException("El vehículo no puede ser nulo");
        }
        if (!vehicles.containsKey(vehicle.getPlate())) {
            throw new RecordNotFoundException("El vehículo no está registrado");
        }
        if (findActiveTicket(vehicle) != null) {
            throw new ActiveTicketException("El vehículo ya tiene un ticket activo");
        }

        ParkingSpace compatibleSpace = findCompatibleSpace(vehicle);
        if (compatibleSpace == null) {
            throw new SpaceNotAvailableException("No hay espacios disponibles para el vehículo");
        }

        if (!compatibleSpace.occupy()) {
            throw new SpaceNotAvailableException("El espacio seleccionado no está disponible");
        }

        ParkingTicket ticket = new ParkingTicket(nextTicketId++, vehicle, compatibleSpace, entryTime);
        tickets.add(ticket);
        return ticket;
    }

    public ParkingTicket registerEntry(Vehicle vehicle, ParkingSpace space, LocalDateTime entryTime) {
        if (vehicle == null) {
            throw new ParkingException("El vehículo no puede ser nulo");
        }
        if (space == null) {
            throw new ParkingException("El espacio no puede ser nulo");
        }
        if (!vehicles.containsKey(vehicle.getPlate())) {
            throw new RecordNotFoundException("El vehículo no está registrado");
        }
        if (findActiveTicket(vehicle) != null) {
            throw new ActiveTicketException("El vehículo ya tiene un ticket activo");
        }

        ParkingSpace registeredSpace = spaces.get(space.getNumber());
        if (registeredSpace == null) {
            throw new RecordNotFoundException("El espacio no está registrado");
        }
        if (!registeredSpace.isCompatibleWith(vehicle)) {
            throw new SpaceNotAvailableException("El espacio no es compatible con el vehículo");
        }
        if (!registeredSpace.isAvailable()) {
            throw new SpaceNotAvailableException("El espacio no está disponible");
        }
        if (!registeredSpace.occupy()) {
            throw new SpaceNotAvailableException("No se pudo ocupar el espacio");
        }

        ParkingTicket ticket = new ParkingTicket(nextTicketId++, vehicle, registeredSpace, entryTime);
        tickets.add(ticket);
        return ticket;
    }

    public ParkingTicket registerExit(Vehicle vehicle, LocalDateTime exitTime) {
        if (vehicle == null) {
            throw new ParkingException("El vehículo no puede ser nulo");
        }
        ParkingTicket activeTicket = findActiveTicket(vehicle);
        if (activeTicket == null) {
            throw new ActiveTicketException("No existe un ticket activo para ese vehículo");
        }

        activeTicket.close(exitTime);
        return activeTicket;
    }

    public Payment registerPayment(ParkingTicket ticket, PaymentType paymentType, LocalDateTime paymentDateTime) {
        if (ticket == null) {
            throw new ParkingException("El ticket no puede ser nulo");
        }
        if (paymentType == null) {
            throw new ParkingException("El tipo de pago no puede ser nulo");
        }
        if (paymentDateTime == null) {
            throw new ParkingException("La fecha del pago no puede ser nula");
        }

        boolean exists = false;
        for (ParkingTicket current : tickets) {
            if (current.getId() == ticket.getId()) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            throw new RecordNotFoundException("El ticket no está registrado");
        }
        if (ticket.getStatus() == TicketStatus.ACTIVE) {
            throw new InvalidTicketStateException("No se puede pagar un ticket activo");
        }
        if (ticket.getStatus() == TicketStatus.PAID) {
            throw new InvalidTicketStateException("El ticket ya fue pagado");
        }

        ticket.markAsPaid();
        Payment payment = new Payment(nextPaymentId++, ticket, paymentDateTime, ticket.getAmount(), paymentType);
        payments.add(payment);
        return payment;
    }

    public List<ParkingTicket> getActiveTickets() {
        List<ParkingTicket> activeTickets = new ArrayList<ParkingTicket>();
        for (ParkingTicket ticket : tickets) {
            if (ticket.getStatus() == TicketStatus.ACTIVE) {
                activeTickets.add(ticket);
            }
        }
        return Collections.unmodifiableList(activeTickets);
    }

    public List<Vehicle> getVehiclesInside() {
        List<Vehicle> vehiclesInside = new ArrayList<Vehicle>();
        for (ParkingTicket ticket : tickets) {
            if (ticket.getStatus() == TicketStatus.ACTIVE || ticket.getStatus() == TicketStatus.CLOSED) {
                vehiclesInside.add(ticket.getVehicle());
            }
        }
        return Collections.unmodifiableList(vehiclesInside);
    }

    public double getTotalIncome() {
        double total = 0.0;
        for (Payment payment : payments) {
            total += payment.getAmount();
        }
        return total;
    }

    public Map<SpaceType, Integer> getOccupancyByType() {
        Map<SpaceType, Integer> occupancy = new HashMap<SpaceType, Integer>();
        for (ParkingSpace space : spaces.values()) {
            if (space.getStatus() == cr.ac.una.parking.coto.enums.SpaceStatus.OCCUPIED) {
                Integer actual = occupancy.get(space.getType());
                if (actual == null) {
                    occupancy.put(space.getType(), 1);
                } else {
                    occupancy.put(space.getType(), actual + 1);
                }
            }
        }
        return occupancy;
    }

    public int countUsableSpaces() {
        int count = 0;
        for (ParkingSpace space : spaces.values()) {
            if (space.isUsable()) {
                count++;
            }
        }
        return count;
    }

    private ParkingSpace findCompatibleSpace(Vehicle vehicle) {
        for (ParkingSpace space : spaces.values()) {
            if (space.isCompatibleWith(vehicle) && space.isAvailable()) {
                return space;
            }
        }
        return null;
    }

    private ParkingTicket findActiveTicket(Vehicle vehicle) {
        for (ParkingTicket ticket : tickets) {
            if (ticket.getVehicle().getPlate().equals(vehicle.getPlate())
                    && ticket.getStatus() == TicketStatus.ACTIVE) {
                return ticket;
            }
        }
        return null;
    }
}
