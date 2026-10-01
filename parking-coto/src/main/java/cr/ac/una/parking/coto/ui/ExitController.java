package cr.ac.una.parking.coto.ui;

import cr.ac.una.parking.coto.ParkingLot;
import cr.ac.una.parking.coto.exception.ParkingException;
import cr.ac.una.parking.coto.model.ParkingTicket;
import cr.ac.una.parking.coto.model.Vehicle;
import java.time.LocalDateTime;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller of the exit screen. The exit time is typed by the user, and
 * shortcut buttons set it a known time after the entry, so stays of one
 * minute or eleven hours can be shown without waiting.
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 2.0
 */
public class ExitController implements Refreshable {

    /** Parking lot shared by every screen. */
    private final ParkingLot parkingLot;
    /** Activity log shared by every screen. */
    private final ActivityLog activityLog;
    /** Date and time of the exit. */
    private DateTimeFields exitTime;
    /** Message shown after each operation. */
    private FeedbackBanner feedback;

    @FXML private ComboBox<Vehicle> vehicleCombo;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;
    @FXML private Label entryInfoLabel;
    @FXML private Label feedbackLabel;

    /**
     * Creates the controller.
     *
     * @param parkingLot parking lot shared by every screen
     * @param activityLog activity log shared by every screen
     */
    public ExitController(ParkingLot parkingLot, ActivityLog activityLog) {
        this.parkingLot = parkingLot;
        this.activityLog = activityLog;
    }

    @FXML
    private void initialize() {
        feedback = new FeedbackBanner(feedbackLabel);
        exitTime = new DateTimeFields(datePicker, timeField);
        vehicleCombo.setConverter(UiSupport.converter(UiSupport::describe));
        vehicleCombo.valueProperty().addListener((observable, previous, current) -> showEntryInfo());
        refresh();
    }

    /** Reloads the list of vehicles. */
    @Override
    public void refresh() {
        UiSupport.replaceItems(vehicleCombo, parkingLot.getVehicles());
        showEntryInfo();
    }

    @FXML
    private void onNow() {
        exitTime.setNow();
    }

    @FXML
    private void onQuickExit(ActionEvent event) {
        try {
            Vehicle vehicle = vehicleCombo.getValue();
            if (vehicle == null) {
                throw new IllegalArgumentException("Seleccione un vehículo");
            }
            long minutes = Long.parseLong((String) ((Button) event.getSource()).getUserData());
            ParkingTicket active = findActiveTicket(vehicle);
            if (active == null) {
                throw new IllegalArgumentException(
                        "El vehículo no tiene un ticket activo, por eso no hay una hora de entrada de referencia");
            }
            exitTime.setValue(active.getEntryTime().plusMinutes(minutes));
            feedback.hide();
        } catch (IllegalArgumentException e) {
            feedback.error(e);
        }
    }

    @FXML
    private void onRegisterExit() {
        try {
            Vehicle vehicle = vehicleCombo.getValue();
            if (vehicle == null) {
                throw new IllegalArgumentException("Seleccione un vehículo");
            }
            LocalDateTime time = exitTime.getValue();
            ParkingTicket ticket = parkingLot.registerExit(vehicle, time);
            String summary = "Ticket #" + ticket.getId() + " · " + vehicle.getPlate() + " · permanencia "
                    + ticket.calculateStayMinutes(time) + " min · horas cobradas "
                    + ticket.calculateChargedHours(time) + " · monto " + UiSupport.money(ticket.getAmount());
            feedback.success("Salida registrada. " + summary + " · espacio " + ticket.getSpace().getNumber()
                    + " " + ticket.getSpace().getStatus().getDisplayName().toLowerCase()
                    + ". Falta registrar el pago.");
            activityLog.add("✔ Salida · " + summary);
            refresh();
        } catch (ParkingException | IllegalArgumentException e) {
            feedback.error(e);
            activityLog.rejected("Salida", e);
        }
    }

    /** Shows when the selected vehicle entered, if it is inside. */
    private void showEntryInfo() {
        Vehicle vehicle = vehicleCombo.getValue();
        ParkingTicket active = vehicle == null ? null : findActiveTicket(vehicle);
        if (vehicle == null) {
            entryInfoLabel.setText("Seleccione un vehículo para ver su ticket activo.");
        } else if (active == null) {
            entryInfoLabel.setText("El vehículo no tiene un ticket activo: no está dentro del parqueo.");
        } else {
            entryInfoLabel.setText("Ticket #" + active.getId() + " · entró el " + UiSupport.dateTime(active.getEntryTime())
                    + " · espacio " + active.getSpace().getNumber());
        }
    }

    /** Finds the active ticket of a vehicle, or null if it is not inside. */
    private ParkingTicket findActiveTicket(Vehicle vehicle) {
        for (ParkingTicket ticket : parkingLot.getActiveTickets()) {
            if (ticket.getVehicle().getPlate().equals(vehicle.getPlate())) {
                return ticket;
            }
        }
        return null;
    }
}
