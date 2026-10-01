package cr.ac.una.parking.coto;

import cr.ac.una.parking.coto.enums.PaymentType;
import cr.ac.una.parking.coto.enums.SpaceStatus;
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
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Coordinates the parking lot operations and acts as the main orchestrator of
 * the business flow.
 *
 * <p>This class manages the collections of registered vehicles, parking spaces,
 * tickets, and payments, and validates the rules that define a valid entry,
 * exit, and billing process. It centralizes the operational logic of the
 * parking system without storing the business rules inside the UI or the
 * vehicle classes themselves.</p>
 *
 * <p>The instance keeps the system state in memory using maps and lists, and it
 * is responsible for enforcing the business invariants of the project, such as
 * duplicate registrations, incompatible spaces, active-ticket checks, and total
 * income accounting.</p>
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.0
 */
public class ParkingLot {

    /** Stores vehicles by plate for unique registration validation. */
    private final Map<String, Vehicle> vehicles;
    /** Stores parking spaces by their numeric identifier. */
    private final Map<Integer, ParkingSpace> spaces;
    /** Tracks the ticket history and active ticket state. */
    private final List<ParkingTicket> tickets;
    /** Records each completed payment. */
    private final List<Payment> payments;
    /** Sequential identifier for parking tickets. */
    private long nextTicketId;
    /** Sequential identifier for payments. */
    private long nextPaymentId;

    /**
     * Creates a new empty parking lot with empty registries.
     */
    public ParkingLot() {
        this.vehicles = new LinkedHashMap<String, Vehicle>();
        this.spaces = new TreeMap<Integer, ParkingSpace>();
        this.tickets = new ArrayList<ParkingTicket>();
        this.payments = new ArrayList<Payment>();
        this.nextTicketId = 1L;
        this.nextPaymentId = 1L;
    }

    /**
     * Registers a new vehicle in the parking lot if the plate is unique.
     *
     * @param vehicle vehicle to register
     * @throws ParkingException if the vehicle reference is null
     * @throws DuplicateRecordException if a vehicle with the same plate exists
     */
    public void registerVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            throw new ParkingException("El vehículo no puede ser nulo");
        }
        if (vehicles.containsKey(vehicle.getPlate())) {
            throw new DuplicateRecordException("Ya existe un vehículo con la placa " + vehicle.getPlate());
        }
        vehicles.put(vehicle.getPlate(), vehicle);
    }

    /**
     * Registers a parking space if its identifier is unique.
     *
     * @param space parking space to register
     * @throws ParkingException if the space reference is null
     * @throws DuplicateRecordException if the same numeric identifier already exists
     */
    public void registerSpace(ParkingSpace space) {
        if (space == null) {
            throw new ParkingException("El espacio no puede ser nulo");
        }
        if (spaces.containsKey(space.getNumber())) {
            throw new DuplicateRecordException("Ya existe un espacio con el número " + space.getNumber());
        }
        spaces.put(space.getNumber(), space);
    }

    /**
     * Returns the set of currently available parking spaces.
     *
     * @return read-only list of free spaces
     */
    public List<ParkingSpace> getAvailableSpaces() {
        List<ParkingSpace> availableSpaces = new ArrayList<ParkingSpace>();
        for (ParkingSpace space : spaces.values()) {
            if (space.isAvailable()) {
                availableSpaces.add(space);
            }
        }
        return Collections.unmodifiableList(availableSpaces);
    }

    /**
     * Returns every registered parking space ordered by its number.
     *
     * @return read-only list with all the spaces
     */
    public List<ParkingSpace> getSpaces() {
        return Collections.unmodifiableList(new ArrayList<ParkingSpace>(spaces.values()));
    }

    /**
     * Returns every registered vehicle in registration order.
     *
     * @return read-only list with all the vehicles
     */
    public List<Vehicle> getVehicles() {
        return Collections.unmodifiableList(new ArrayList<Vehicle>(vehicles.values()));
    }

    /**
     * Returns the complete ticket history, including closed and paid tickets.
     *
     * @return read-only list with every ticket generated so far
     */
    public List<ParkingTicket> getTickets() {
        return Collections.unmodifiableList(new ArrayList<ParkingTicket>(tickets));
    }

    /**
     * Returns the payments registered so far.
     *
     * @return read-only list with every payment
     */
    public List<Payment> getPayments() {
        return Collections.unmodifiableList(new ArrayList<Payment>(payments));
    }

    /**
     * Puts an available parking space out of service so it cannot be assigned.
     *
     * @param number identifier of the space
     * @throws RecordNotFoundException if the space is not registered
     * @throws SpaceNotAvailableException if the space is not currently available
     */
    public void putSpaceOutOfService(int number) {
        ParkingSpace space = findSpace(number);
        if (!space.putOutOfService()) {
            throw new SpaceNotAvailableException("Solo se puede poner fuera de servicio un espacio disponible");
        }
    }

    /**
     * Returns an out-of-service parking space to the available state.
     *
     * @param number identifier of the space
     * @throws RecordNotFoundException if the space is not registered
     * @throws SpaceNotAvailableException if the space is not out of service
     */
    public void restoreSpaceService(int number) {
        ParkingSpace space = findSpace(number);
        if (!space.restoreService()) {
            throw new SpaceNotAvailableException("El espacio no está fuera de servicio");
        }
    }

    /**
     * Registers a vehicle entry by automatically assigning a compatible empty
     * space and creating an active ticket.
     *
     * @param vehicle registered vehicle entering the parking lot
     * @param entryTime entry timestamp
     * @return the generated ticket for the entry
     * @throws ParkingException if the vehicle is null or invalid
     * @throws RecordNotFoundException if the vehicle is not registered
     * @throws ActiveTicketException if the vehicle already has an active ticket
     * @throws SpaceNotAvailableException if no compatible space is available
     */
    public ParkingTicket registerEntry(Vehicle vehicle, LocalDateTime entryTime) {
        if (vehicle == null) {
            throw new ParkingException("El vehículo no puede ser nulo");
        }
        if (entryTime == null) {
            throw new ParkingException("La fecha de entrada no puede ser nula");
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

    /**
     * Registers a vehicle entry in a specific parking space selected by the
     * caller.
     *
     * @param vehicle vehicle entering the parking lot
     * @param space requested parking space
     * @param entryTime entry timestamp
     * @return generated ticket for the entry
     * @throws ParkingException if any required argument is null
     * @throws RecordNotFoundException if the vehicle or space is not registered
     * @throws ActiveTicketException if the vehicle already has an active ticket
     * @throws SpaceNotAvailableException if the space is occupied, incompatible,
     *         or not usable
     */
    public ParkingTicket registerEntry(Vehicle vehicle, ParkingSpace space, LocalDateTime entryTime) {
        if (vehicle == null) {
            throw new ParkingException("El vehículo no puede ser nulo");
        }
        if (space == null) {
            throw new ParkingException("El espacio no puede ser nulo");
        }
        if (entryTime == null) {
            throw new ParkingException("La fecha de entrada no puede ser nula");
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

    /**
     * Closes the active ticket for a vehicle and releases the assigned space.
     *
     * @param vehicle vehicle leaving the parking lot
     * @param exitTime exit timestamp
     * @return the closed parking ticket
     * @throws ParkingException if the vehicle reference is null
     * @throws RecordNotFoundException if there is no active ticket for the vehicle
     */
    public ParkingTicket registerExit(Vehicle vehicle, LocalDateTime exitTime) {
        if (vehicle == null) {
            throw new ParkingException("El vehículo no puede ser nulo");
        }
        ParkingTicket activeTicket = findActiveTicket(vehicle);
        if (activeTicket == null) {
            throw new RecordNotFoundException("No existe un ticket activo para ese vehículo");
        }

        activeTicket.close(exitTime);
        return activeTicket;
    }

    /**
     * Records the payment for a ticket that is already closed and not paid.
     *
     * @param ticket ticket to be paid
     * @param paymentType form of payment used by the customer
     * @param paymentDateTime date and time when the payment was made
     * @return generated payment record
     * @throws ParkingException if required arguments are null
     * @throws RecordNotFoundException if the ticket is not registered in the lot
     * @throws InvalidTicketStateException if the ticket is active or already paid
     */
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
        if (ticket.isActive()) {
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

    /**
     * Returns the list of tickets that are still active.
     *
     * @return read-only list with the active tickets
     */
    public List<ParkingTicket> getActiveTickets() {
        List<ParkingTicket> activeTickets = new ArrayList<ParkingTicket>();
        for (ParkingTicket ticket : tickets) {
            if (ticket.isActive()) {
                activeTickets.add(ticket);
            }
        }
        return Collections.unmodifiableList(activeTickets);
    }

    /**
     * Returns the vehicles that are currently inside the parking lot.
     *
     * @return read-only list of vehicles that still have an active ticket
     */
    public List<Vehicle> getVehiclesInside() {
        List<Vehicle> vehiclesInside = new ArrayList<Vehicle>();
        for (ParkingTicket ticket : tickets) {
            if (ticket.isActive()) {
                vehiclesInside.add(ticket.getVehicle());
            }
        }
        return Collections.unmodifiableList(vehiclesInside);
    }

    /**
     * Calculates the total income received from all payment records.
     *
     * @return accumulated revenue for completed payments
     */
    public double getTotalIncome() {
        double total = 0.0;
        for (Payment payment : payments) {
            total += payment.getAmount();
        }
        return total;
    }

    /**
     * Counts how many spaces are occupied by each space type.
     *
     * @return map with the quantity of occupied spaces per space type
     */
    public Map<SpaceType, Integer> getOccupancyByType() {
        Map<SpaceType, Integer> occupancy = new EnumMap<SpaceType, Integer>(SpaceType.class);
        for (ParkingSpace space : spaces.values()) {
            if (space.getStatus() == SpaceStatus.OCCUPIED) {
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

    /**
     * Counts all non-out-of-service spaces in the parking lot.
     *
     * @return number of usable spaces
     */
    public int countUsableSpaces() {
        int count = 0;
        for (ParkingSpace space : spaces.values()) {
            if (space.isUsable()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Finds a registered space by its number.
     *
     * @param number identifier of the space
     * @return the registered space
     * @throws RecordNotFoundException if the space is not registered
     */
    private ParkingSpace findSpace(int number) {
        ParkingSpace space = spaces.get(number);
        if (space == null) {
            throw new RecordNotFoundException("El espacio no está registrado");
        }
        return space;
    }

    /**
     * Finds the first compatible space that is currently available.
     *
     * @param vehicle vehicle requiring an available compatible space
     * @return a compatible free space, or {@code null} if no match exists
     */
    private ParkingSpace findCompatibleSpace(Vehicle vehicle) {
        for (ParkingSpace space : spaces.values()) {
            if (space.isCompatibleWith(vehicle) && space.isAvailable()) {
                return space;
            }
        }
        return null;
    }

    /**
     * Finds an active ticket for the specified vehicle.
     *
     * @param vehicle vehicle being checked
     * @return the active ticket if one exists; otherwise {@code null}
     */
    private ParkingTicket findActiveTicket(Vehicle vehicle) {
        for (ParkingTicket ticket : tickets) {
            if (ticket.getVehicle().getPlate().equals(vehicle.getPlate())
                    && ticket.isActive()) {
                return ticket;
            }
        }
        return null;
    }
}
