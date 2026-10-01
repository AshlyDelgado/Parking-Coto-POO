package cr.ac.una.parking.coto.ui;

import cr.ac.una.parking.coto.ParkingLot;
import cr.ac.una.parking.coto.enums.VehicleType;
import cr.ac.una.parking.coto.exception.ParkingException;
import cr.ac.una.parking.coto.model.Car;
import cr.ac.una.parking.coto.model.FreightVehicle;
import cr.ac.una.parking.coto.model.Motorcycle;
import cr.ac.una.parking.coto.model.Vehicle;
import cr.ac.una.parking.coto.scenario.SampleData;
import java.util.EnumMap;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * Controller of the vehicles screen: registers vehicles and lists them.
 *
 * <p>It only reads what the user typed and calls {@link ParkingLot}; the
 * validations live in the domain classes.</p>
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 2.0
 */
public class VehiclesController implements Refreshable {

    /** Builds a vehicle of one category from the data typed by the user. */
    private interface VehicleCreator {
        /**
         * Creates the vehicle.
         *
         * @param plate vehicle plate
         * @param brand vehicle brand
         * @param model vehicle model
         * @param color vehicle color
         * @return the new vehicle
         */
        Vehicle create(String plate, String brand, String model, String color);
    }

    /** Parking lot shared by every screen. */
    private final ParkingLot parkingLot;
    /** Activity log shared by every screen. */
    private final ActivityLog activityLog;
    /** Constructor of the concrete vehicle class for each category. */
    private final Map<VehicleType, VehicleCreator> creators = new EnumMap<VehicleType, VehicleCreator>(VehicleType.class);
    /** Message shown after each operation. */
    private FeedbackBanner feedback;

    @FXML private ComboBox<VehicleType> vehicleTypeCombo;
    @FXML private TextField plateField;
    @FXML private TextField brandField;
    @FXML private TextField modelField;
    @FXML private TextField colorField;
    @FXML private TableView<Vehicle> vehiclesTable;
    @FXML private Label feedbackLabel;
    @FXML private Label countLabel;

    /**
     * Creates the controller.
     *
     * @param parkingLot parking lot shared by every screen
     * @param activityLog activity log shared by every screen
     */
    public VehiclesController(ParkingLot parkingLot, ActivityLog activityLog) {
        this.parkingLot = parkingLot;
        this.activityLog = activityLog;
        creators.put(VehicleType.MOTORCYCLE, Motorcycle::new);
        creators.put(VehicleType.CAR, Car::new);
        creators.put(VehicleType.FREIGHT, FreightVehicle::new);
    }

    @FXML
    private void initialize() {
        feedback = new FeedbackBanner(feedbackLabel);
        vehicleTypeCombo.getItems().setAll(VehicleType.values());
        vehicleTypeCombo.setConverter(UiSupport.converter(VehicleType::getDisplayName));
        UiSupport.addVehicleColumns(vehiclesTable);
        refresh();
    }

    /** Reloads the table of vehicles. */
    @Override
    public void refresh() {
        vehiclesTable.getItems().setAll(parkingLot.getVehicles());
        countLabel.setText(parkingLot.getVehicles().size() + " registrados");
    }

    @FXML
    private void onRegisterVehicle() {
        try {
            VehicleType type = vehicleTypeCombo.getValue();
            if (type == null) {
                throw new IllegalArgumentException("Seleccione el tipo de vehículo");
            }
            Vehicle vehicle = creators.get(type).create(plateField.getText(), brandField.getText(),
                    modelField.getText(), colorField.getText());
            parkingLot.registerVehicle(vehicle);
            plateField.clear();
            brandField.clear();
            modelField.clear();
            colorField.clear();
            feedback.success("Vehículo " + vehicle.getPlate() + " registrado como " + type.getDisplayName().toLowerCase());
            activityLog.add("✔ Vehículo registrado · " + UiSupport.describe(vehicle));
            refresh();
        } catch (ParkingException | IllegalArgumentException e) {
            feedback.error(e);
            activityLog.rejected("Registro de vehículo", e);
        }
    }

    @FXML
    private void onLoadSampleData() {
        try {
            SampleData.load(parkingLot);
            feedback.success("Datos de ejemplo cargados: 7 espacios y 7 vehículos");
            activityLog.add("✔ Datos de ejemplo cargados");
            refresh();
        } catch (ParkingException e) {
            feedback.error(e);
            activityLog.rejected("Carga de datos de ejemplo", e);
        }
    }
}
