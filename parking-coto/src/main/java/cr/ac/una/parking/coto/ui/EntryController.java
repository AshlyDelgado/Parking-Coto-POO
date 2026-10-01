package cr.ac.una.parking.coto.ui;

import cr.ac.una.parking.coto.ParkingLot;
import cr.ac.una.parking.coto.exception.ParkingException;
import cr.ac.una.parking.coto.model.ParkingSpace;
import cr.ac.una.parking.coto.model.ParkingTicket;
import cr.ac.una.parking.coto.model.Vehicle;
import java.time.LocalDateTime;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller of the entry screen. The entry time is typed by the user, and
 * the space can be assigned automatically or chosen by hand, which is how the
 * rejections for occupied, out-of-service or incompatible spaces are shown.
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 2.0
 */
public class EntryController implements Refreshable {

    /** Parking lot shared by every screen. */
    private final ParkingLot parkingLot;
    /** Activity log shared by every screen. */
    private final ActivityLog activityLog;
    /** Date and time of the entry. */
    private DateTimeFields entryTime;
    /** Message shown after each operation. */
    private FeedbackBanner feedback;

    @FXML private ComboBox<Vehicle> vehicleCombo;
    @FXML private CheckBox manualSpaceCheck;
    @FXML private ComboBox<ParkingSpace> spaceCombo;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;
    @FXML private Label feedbackLabel;

    /**
     * Creates the controller.
     *
     * @param parkingLot parking lot shared by every screen
     * @param activityLog activity log shared by every screen
     */
    public EntryController(ParkingLot parkingLot, ActivityLog activityLog) {
        this.parkingLot = parkingLot;
        this.activityLog = activityLog;
    }

    @FXML
    private void initialize() {
        feedback = new FeedbackBanner(feedbackLabel);
        entryTime = new DateTimeFields(datePicker, timeField);
        vehicleCombo.setConverter(UiSupport.converter(UiSupport::describe));
        spaceCombo.setConverter(UiSupport.converter(UiSupport::describe));
        refresh();
    }

    /** Reloads the lists of vehicles and spaces. */
    @Override
    public void refresh() {
        UiSupport.replaceItems(vehicleCombo, parkingLot.getVehicles());
        UiSupport.replaceItems(spaceCombo, parkingLot.getSpaces());
    }

    @FXML
    private void onManualSpaceToggled() {
        spaceCombo.setDisable(!manualSpaceCheck.isSelected());
    }

    @FXML
    private void onNow() {
        entryTime.setNow();
    }

    @FXML
    private void onRegisterEntry() {
        try {
            Vehicle vehicle = vehicleCombo.getValue();
            if (vehicle == null) {
                throw new IllegalArgumentException("Seleccione un vehículo");
            }
            LocalDateTime time = entryTime.getValue();
            ParkingTicket ticket;
            if (manualSpaceCheck.isSelected()) {
                ParkingSpace space = spaceCombo.getValue();
                if (space == null) {
                    throw new IllegalArgumentException("Seleccione un espacio");
                }
                ticket = parkingLot.registerEntry(vehicle, space, time);
            } else {
                ticket = parkingLot.registerEntry(vehicle, time);
            }
            String summary = "Ticket #" + ticket.getId() + " · " + vehicle.getPlate() + " · espacio "
                    + ticket.getSpace().getNumber() + " (" + ticket.getSpace().getType().getDisplayName()
                    + ") · " + UiSupport.dateTime(time);
            feedback.success("Ingreso registrado. " + summary);
            activityLog.add("✔ Ingreso · " + summary);
            refresh();
        } catch (ParkingException | IllegalArgumentException e) {
            feedback.error(e);
            activityLog.rejected("Ingreso", e);
        }
    }
}
