package cr.ac.una.parking.coto.ui;

import cr.ac.una.parking.coto.ParkingLot;
import cr.ac.una.parking.coto.enums.SpaceType;
import cr.ac.una.parking.coto.model.ParkingSpace;
import cr.ac.una.parking.coto.model.Payment;
import cr.ac.una.parking.coto.model.Vehicle;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

/**
 * Controller of the queries and reports screen: total income, occupation by
 * type of space, vehicles inside, available spaces and payments.
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.0
 */
public class ReportsController implements Refreshable {

    /** Parking lot shared by every screen. */
    private final ParkingLot parkingLot;

    @FXML private Label incomeLabel;
    @FXML private Label occupancyLabel;
    @FXML private TableView<Vehicle> insideTable;
    @FXML private TableView<ParkingSpace> availableTable;
    @FXML private TableView<Payment> paymentsTable;

    /**
     * Creates the controller.
     *
     * @param parkingLot parking lot shared by every screen
     */
    public ReportsController(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
    }

    @FXML
    private void initialize() {
        UiSupport.addVehicleColumns(insideTable);
        UiSupport.addSpaceColumns(availableTable);
        UiSupport.addPaymentColumns(paymentsTable);
        refresh();
    }

    /** Reloads every query. */
    @Override
    public void refresh() {
        incomeLabel.setText(UiSupport.money(parkingLot.getTotalIncome()));
        occupancyLabel.setText(describeOccupancy());
        insideTable.getItems().setAll(parkingLot.getVehiclesInside());
        availableTable.getItems().setAll(parkingLot.getAvailableSpaces());
        paymentsTable.getItems().setAll(parkingLot.getPayments());
    }

    /** Builds the text "occupied/total" for each type of space. */
    private String describeOccupancy() {
        Map<SpaceType, Integer> occupied = parkingLot.getOccupancyByType();
        StringBuilder text = new StringBuilder();
        for (SpaceType type : SpaceType.values()) {
            int total = 0;
            for (ParkingSpace space : parkingLot.getSpaces()) {
                if (space.getType() == type) {
                    total++;
                }
            }
            Integer used = occupied.get(type);
            if (text.length() > 0) {
                text.append("     ");
            }
            text.append(type.getDisplayName()).append("  ").append(used == null ? 0 : used)
                    .append('/').append(total);
        }
        return text.toString();
    }
}
