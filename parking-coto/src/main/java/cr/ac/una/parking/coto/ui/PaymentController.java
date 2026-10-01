package cr.ac.una.parking.coto.ui;

import cr.ac.una.parking.coto.ParkingLot;
import cr.ac.una.parking.coto.enums.PaymentType;
import cr.ac.una.parking.coto.enums.TicketStatus;
import cr.ac.una.parking.coto.exception.ParkingException;
import cr.ac.una.parking.coto.model.ParkingTicket;
import cr.ac.una.parking.coto.model.Payment;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller of the payment screen. The list includes tickets that are still
 * active so the rule that forbids paying them can be shown.
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 2.0
 */
public class PaymentController implements Refreshable {

    /** Parking lot shared by every screen. */
    private final ParkingLot parkingLot;
    /** Activity log shared by every screen. */
    private final ActivityLog activityLog;
    /** Date and time of the payment. */
    private DateTimeFields paymentTime;
    /** Message shown after each operation. */
    private FeedbackBanner feedback;

    @FXML private ComboBox<ParkingTicket> ticketCombo;
    @FXML private ComboBox<PaymentType> paymentTypeCombo;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;
    @FXML private Label feedbackLabel;

    /**
     * Creates the controller.
     *
     * @param parkingLot parking lot shared by every screen
     * @param activityLog activity log shared by every screen
     */
    public PaymentController(ParkingLot parkingLot, ActivityLog activityLog) {
        this.parkingLot = parkingLot;
        this.activityLog = activityLog;
    }

    @FXML
    private void initialize() {
        feedback = new FeedbackBanner(feedbackLabel);
        paymentTime = new DateTimeFields(datePicker, timeField);
        ticketCombo.setConverter(UiSupport.converter(this::describePayable));
        paymentTypeCombo.getItems().setAll(PaymentType.values());
        paymentTypeCombo.setConverter(UiSupport.converter(PaymentType::getDisplayName));
        paymentTypeCombo.setValue(PaymentType.CASH);
        refresh();
    }

    /** Reloads the list of tickets that have not been paid. */
    @Override
    public void refresh() {
        List<ParkingTicket> unpaid = new ArrayList<ParkingTicket>();
        for (ParkingTicket ticket : parkingLot.getTickets()) {
            if (ticket.getStatus() != TicketStatus.PAID) {
                unpaid.add(ticket);
            }
        }
        UiSupport.replaceItems(ticketCombo, unpaid);
    }

    @FXML
    private void onNow() {
        paymentTime.setNow();
    }

    @FXML
    private void onRegisterPayment() {
        try {
            ParkingTicket ticket = ticketCombo.getValue();
            if (ticket == null) {
                throw new IllegalArgumentException("Seleccione un ticket");
            }
            PaymentType type = paymentTypeCombo.getValue();
            if (type == null) {
                throw new IllegalArgumentException("Seleccione el tipo de pago");
            }
            Payment payment = parkingLot.registerPayment(ticket, type, paymentTime.getValue());
            String summary = "Pago #" + payment.getId() + " · Ticket #" + ticket.getId() + " · "
                    + UiSupport.money(payment.getAmount()) + " · " + type.getDisplayName();
            feedback.success("Pago registrado. " + summary + ". Ingresos totales: "
                    + UiSupport.money(parkingLot.getTotalIncome()));
            activityLog.add("✔ " + summary);
            refresh();
        } catch (ParkingException | IllegalArgumentException e) {
            feedback.error(e);
            activityLog.rejected("Pago", e);
        }
    }

    /** Describes a ticket together with what is owed. */
    private String describePayable(ParkingTicket ticket) {
        String amount = ticket.getStatus() == TicketStatus.ACTIVE ? "" : " · " + UiSupport.money(ticket.getAmount());
        return UiSupport.describe(ticket) + amount;
    }
}
