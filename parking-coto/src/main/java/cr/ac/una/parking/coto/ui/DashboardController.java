package cr.ac.una.parking.coto.ui;

import cr.ac.una.parking.coto.ParkingLot;
import cr.ac.una.parking.coto.enums.SpaceType;
import cr.ac.una.parking.coto.model.ParkingSpace;
import java.util.EnumMap;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Controller of the dashboard: totals, occupation by type of space, active
 * tickets and the latest activity.
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.0
 */
public class DashboardController implements Refreshable {

    /** Order in which the types of space are shown. */
    private static final SpaceType[] DISPLAY_ORDER = {SpaceType.CAR, SpaceType.MOTORCYCLE, SpaceType.FREIGHT};

    /** Parking lot shared by every screen. */
    private final ParkingLot parkingLot;
    /** Activity log shared by every screen. */
    private final ActivityLog activityLog;
    /** Icon name of each type of space. */
    private final Map<SpaceType, String> typeIcons = new EnumMap<SpaceType, String>(SpaceType.class);
    /** Color of the occupation bar of each type of space. */
    private final Map<SpaceType, String> typeColors = new EnumMap<SpaceType, String>(SpaceType.class);
    /** Occupation bar of each type of space. */
    private final Map<SpaceType, ProgressBar> bars = new EnumMap<SpaceType, ProgressBar>(SpaceType.class);
    /** Text "occupied of total" of each type of space. */
    private final Map<SpaceType, Label> counts = new EnumMap<SpaceType, Label>(SpaceType.class);

    @FXML private StackPane totalIcon;
    @FXML private StackPane occupiedIcon;
    @FXML private StackPane availableIcon;
    @FXML private StackPane incomeIcon;
    @FXML private StackPane activeIcon;
    @FXML private Label totalLabel;
    @FXML private Label occupiedLabel;
    @FXML private Label availableLabel;
    @FXML private Label incomeLabel;
    @FXML private Label activeCountLabel;
    @FXML private VBox occupancyBox;
    @FXML private ListView<String> activityList;

    /**
     * Creates the controller.
     *
     * @param parkingLot parking lot shared by every screen
     * @param activityLog activity log shared by every screen
     */
    public DashboardController(ParkingLot parkingLot, ActivityLog activityLog) {
        this.parkingLot = parkingLot;
        this.activityLog = activityLog;
        typeIcons.put(SpaceType.CAR, "car");
        typeIcons.put(SpaceType.MOTORCYCLE, "motorcycle");
        typeIcons.put(SpaceType.FREIGHT, "truck");
        typeColors.put(SpaceType.CAR, "#1b5fc1");
        typeColors.put(SpaceType.MOTORCYCLE, "#e37400");
        typeColors.put(SpaceType.FREIGHT, "#0b8a7a");
    }

    @FXML
    private void initialize() {
        totalIcon.getChildren().add(Icons.of("parking", 28, "#1b5fc1"));
        occupiedIcon.getChildren().add(Icons.of("block", 28, "#d93025"));
        availableIcon.getChildren().add(Icons.of("available", 28, "#1e8e3e"));
        incomeIcon.getChildren().add(Icons.of("money", 28, "#0b8a7a"));
        activeIcon.getChildren().add(Icons.of("ticket", 64, "white"));
        for (SpaceType type : DISPLAY_ORDER) {
            occupancyBox.getChildren().add(buildOccupancyRow(type));
        }
        activityList.setItems(activityLog.getEntries());
        activityList.setPlaceholder(new Label("Todavía no hay actividad. Registra un ingreso para empezar."));
        refresh();
    }

    /** Reloads every number of the dashboard. */
    @Override
    public void refresh() {
        Map<SpaceType, Integer> occupied = parkingLot.getOccupancyByType();
        int occupiedTotal = 0;
        for (Integer count : occupied.values()) {
            occupiedTotal += count;
        }
        totalLabel.setText(String.valueOf(parkingLot.getSpaces().size()));
        occupiedLabel.setText(String.valueOf(occupiedTotal));
        availableLabel.setText(String.valueOf(parkingLot.getAvailableSpaces().size()));
        incomeLabel.setText(UiSupport.money(parkingLot.getTotalIncome()));
        activeCountLabel.setText(String.valueOf(parkingLot.getActiveTickets().size()));
        for (SpaceType type : DISPLAY_ORDER) {
            int total = 0;
            for (ParkingSpace space : parkingLot.getSpaces()) {
                if (space.getType() == type) {
                    total++;
                }
            }
            Integer used = occupied.get(type);
            int usedCount = used == null ? 0 : used;
            bars.get(type).setProgress(total == 0 ? 0 : (double) usedCount / total);
            counts.get(type).setText(usedCount + " de " + total);
        }
    }

    /** Builds the row of one type of space: icon, name, bar and count. */
    private HBox buildOccupancyRow(SpaceType type) {
        StackPane icon = new StackPane(Icons.of(typeIcons.get(type), 24, typeColors.get(type)));
        icon.setMinWidth(34);
        Label name = new Label(type.getDisplayName());
        name.getStyleClass().add("occupancy-name");
        name.setMinWidth(110);
        ProgressBar bar = new ProgressBar(0);
        bar.setStyle("-fx-accent: " + typeColors.get(type) + ";");
        bar.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(bar, Priority.ALWAYS);
        Label count = new Label("0 de 0");
        count.getStyleClass().add("muted");
        count.setMinWidth(60);
        bars.put(type, bar);
        counts.put(type, count);
        HBox row = new HBox(12, icon, name, bar, count);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(2, 0, 2, 0));
        return row;
    }
}
