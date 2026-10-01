package cr.ac.una.parking.coto.ui;

import cr.ac.una.parking.coto.ParkingLot;
import cr.ac.una.parking.coto.enums.SpaceType;
import cr.ac.una.parking.coto.exception.ParkingException;
import cr.ac.una.parking.coto.model.ParkingSpace;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * Controller of the spaces screen: registers spaces, lists them and puts them
 * out of service or back in service.
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 2.0
 */
public class SpacesController implements Refreshable {

    /** Parking lot shared by every screen. */
    private final ParkingLot parkingLot;
    /** Activity log shared by every screen. */
    private final ActivityLog activityLog;
    /** Message shown after each operation. */
    private FeedbackBanner feedback;

    @FXML private TextField spaceNumberField;
    @FXML private ComboBox<SpaceType> spaceTypeCombo;
    @FXML private TableView<ParkingSpace> spacesTable;
    @FXML private Label feedbackLabel;
    @FXML private Label countLabel;

    /**
     * Creates the controller.
     *
     * @param parkingLot parking lot shared by every screen
     * @param activityLog activity log shared by every screen
     */
    public SpacesController(ParkingLot parkingLot, ActivityLog activityLog) {
        this.parkingLot = parkingLot;
        this.activityLog = activityLog;
    }

    @FXML
    private void initialize() {
        feedback = new FeedbackBanner(feedbackLabel);
        spaceTypeCombo.getItems().setAll(SpaceType.values());
        spaceTypeCombo.setConverter(UiSupport.converter(SpaceType::getDisplayName));
        UiSupport.addSpaceColumns(spacesTable);
        refresh();
    }

    /** Reloads the table of spaces. */
    @Override
    public void refresh() {
        ParkingSpace selected = spacesTable.getSelectionModel().getSelectedItem();
        spacesTable.getItems().setAll(parkingLot.getSpaces());
        if (selected != null) {
            spacesTable.getSelectionModel().select(selected);
        }
        countLabel.setText(parkingLot.getSpaces().size() + " registrados · "
                + parkingLot.getAvailableSpaces().size() + " disponibles");
    }

    @FXML
    private void onRegisterSpace() {
        try {
            SpaceType type = spaceTypeCombo.getValue();
            if (type == null) {
                throw new IllegalArgumentException("Seleccione el tipo de espacio");
            }
            ParkingSpace space = new ParkingSpace(readSpaceNumber(), type);
            parkingLot.registerSpace(space);
            spaceNumberField.clear();
            feedback.success("Espacio " + space.getNumber() + " registrado para " + type.getDisplayName().toLowerCase());
            activityLog.add("✔ Espacio registrado · " + UiSupport.describe(space));
            refresh();
        } catch (ParkingException | IllegalArgumentException e) {
            feedback.error(e);
            activityLog.rejected("Registro de espacio", e);
        }
    }

    @FXML
    private void onPutOutOfService() {
        changeService(true);
    }

    @FXML
    private void onRestoreService() {
        changeService(false);
    }

    /** Puts the selected space out of service or restores it. */
    private void changeService(boolean outOfService) {
        String operation = outOfService ? "Poner fuera de servicio" : "Restaurar servicio";
        try {
            ParkingSpace selected = spacesTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                throw new IllegalArgumentException("Seleccione un espacio de la tabla");
            }
            if (outOfService) {
                parkingLot.putSpaceOutOfService(selected.getNumber());
            } else {
                parkingLot.restoreSpaceService(selected.getNumber());
            }
            feedback.success("Espacio " + selected.getNumber() + ": " + selected.getStatus().getDisplayName());
            activityLog.add("✔ " + operation + " · espacio " + selected.getNumber());
            refresh();
        } catch (ParkingException | IllegalArgumentException e) {
            feedback.error(e);
            activityLog.rejected(operation, e);
        }
    }

    /** Reads the space number typed by the user. */
    private int readSpaceNumber() {
        try {
            return Integer.parseInt(spaceNumberField.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El número del espacio debe ser un entero, por ejemplo 8");
        }
    }
}
