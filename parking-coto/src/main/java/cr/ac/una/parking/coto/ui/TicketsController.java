package cr.ac.una.parking.coto.ui;

import cr.ac.una.parking.coto.ParkingLot;
import cr.ac.una.parking.coto.model.ParkingTicket;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

/**
 * Controller of the tickets screen: the tickets that are active and the full
 * history, including closed and paid ones.
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.0
 */
public class TicketsController implements Refreshable {

    /** Parking lot shared by every screen. */
    private final ParkingLot parkingLot;

    @FXML private TableView<ParkingTicket> activeTable;
    @FXML private TableView<ParkingTicket> historyTable;
    @FXML private Label countLabel;

    /**
     * Creates the controller.
     *
     * @param parkingLot parking lot shared by every screen
     */
    public TicketsController(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
    }

    @FXML
    private void initialize() {
        UiSupport.addTicketColumns(activeTable);
        UiSupport.addTicketColumns(historyTable);
        refresh();
    }

    /** Reloads both tables. */
    @Override
    public void refresh() {
        activeTable.getItems().setAll(parkingLot.getActiveTickets());
        historyTable.getItems().setAll(parkingLot.getTickets());
        countLabel.setText(parkingLot.getActiveTickets().size() + " activos de "
                + parkingLot.getTickets().size() + " generados");
    }
}
