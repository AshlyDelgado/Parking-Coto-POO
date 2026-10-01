package cr.ac.una.parking.coto.ui;

import cr.ac.una.parking.coto.enums.TicketStatus;
import cr.ac.una.parking.coto.model.ParkingSpace;
import cr.ac.una.parking.coto.model.ParkingTicket;
import cr.ac.una.parking.coto.model.Payment;
import cr.ac.una.parking.coto.model.Vehicle;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;

/**
 * Small helpers shared by the controllers: text formatting, dialogs, table
 * columns and combo boxes.
 *
 * <p>It only presents information. Every business rule stays in the domain
 * classes.</p>
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.0
 */
final class UiSupport {

    /** Format used to display dates and times. */
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** Utility class: it is never instantiated. */
    private UiSupport() {
    }

    /**
     * Formats an amount like the assignment does, for example ₡1 800.
     *
     * @param amount amount to format
     * @return formatted amount
     */
    static String money(double amount) {
        return "₡" + String.format(Locale.ROOT, "%,.0f", amount).replace(',', ' ');
    }

    /**
     * Formats a date and time for display.
     *
     * @param value date and time, may be null
     * @return text such as 01/10/2026 08:00, or an empty text if null
     */
    static String dateTime(LocalDateTime value) {
        return value == null ? "" : DATE_TIME.format(value);
    }

    /**
     * Builds a converter that shows each item with the given text function.
     *
     * @param <T> type of the displayed item
     * @param text function that produces the text of an item
     * @return converter for combo boxes
     */
    static <T> StringConverter<T> converter(Function<T, String> text) {
        return new StringConverter<T>() {
            @Override
            public String toString(T value) {
                return value == null ? "" : text.apply(value);
            }

            @Override
            public T fromString(String string) {
                return null;
            }
        };
    }

    /**
     * Describes a vehicle in one line.
     *
     * @param vehicle vehicle to describe
     * @return plate and category
     */
    static String describe(Vehicle vehicle) {
        return vehicle.getPlate() + " · " + vehicle.getType().getDisplayName();
    }

    /**
     * Describes a space in one line.
     *
     * @param space space to describe
     * @return number, type and status
     */
    static String describe(ParkingSpace space) {
        return "Espacio " + space.getNumber() + " · " + space.getType().getDisplayName()
                + " · " + space.getStatus().getDisplayName();
    }

    /**
     * Describes a ticket in one line.
     *
     * @param ticket ticket to describe
     * @return number, plate and status
     */
    static String describe(ParkingTicket ticket) {
        return "Ticket #" + ticket.getId() + " · " + ticket.getVehicle().getPlate()
                + " · " + ticket.getStatus().getDisplayName();
    }

    /**
     * Shows an error that no screen expected, so the program never fails
     * silently. Business-rule rejections do not come here: each screen shows
     * them in its own {@link FeedbackBanner}.
     *
     * @param error the unexpected error
     */
    static void showUnexpected(Throwable error) {
        error.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error inesperado");
        alert.setHeaderText(error.getClass().getSimpleName());
        alert.setContentText(String.valueOf(error.getMessage()));
        alert.show();
    }

    /**
     * Adds a text column to a table.
     *
     * @param <S> type of the rows
     * @param table table receiving the column
     * @param title column header
     * @param width preferred width
     * @param text function that produces the cell text of a row
     */
    static <S> void addColumn(TableView<S> table, String title, double width, Function<S, String> text) {
        TableColumn<S, String> column = new TableColumn<S, String>(title);
        column.setPrefWidth(width);
        column.setCellValueFactory(cell -> new SimpleStringProperty(text.apply(cell.getValue())));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getColumns().add(column);
    }

    /**
     * Adds a text column that never gets narrower than a minimum width, so
     * long texts such as "Vehículo de carga" are not cut.
     *
     * @param <S> type of the rows
     * @param table table receiving the column
     * @param title column header
     * @param width preferred width
     * @param minWidth narrowest width the column may take
     * @param text function that produces the cell text of a row
     */
    static <S> void addColumn(TableView<S> table, String title, double width, double minWidth,
                              Function<S, String> text) {
        addColumn(table, title, width, text);
        table.getColumns().get(table.getColumns().size() - 1).setMinWidth(minWidth);
    }

    /**
     * Adds the vehicle columns to a table.
     *
     * @param table table of vehicles
     */
    static void addVehicleColumns(TableView<Vehicle> table) {
        addColumn(table, "Placa", 90, Vehicle::getPlate);
        addColumn(table, "Tipo", 150, 140, vehicle -> vehicle.getType().getDisplayName());
        addColumn(table, "Marca", 100, Vehicle::getBrand);
        addColumn(table, "Modelo", 100, Vehicle::getModel);
        addColumn(table, "Color", 80, Vehicle::getColor);
    }

    /**
     * Adds the space columns to a table.
     *
     * @param table table of spaces
     */
    static void addSpaceColumns(TableView<ParkingSpace> table) {
        addColumn(table, "Número", 70, space -> String.valueOf(space.getNumber()));
        addColumn(table, "Tipo", 120, space -> space.getType().getDisplayName());
        addColumn(table, "Estado", 130, space -> space.getStatus().getDisplayName());
    }

    /**
     * Adds the ticket columns to a table.
     *
     * @param table table of tickets
     */
    static void addTicketColumns(TableView<ParkingTicket> table) {
        addColumn(table, "Ticket", 60, ticket -> String.valueOf(ticket.getId()));
        addColumn(table, "Placa", 90, ticket -> ticket.getVehicle().getPlate());
        addColumn(table, "Tipo", 150, 140, ticket -> ticket.getVehicle().getType().getDisplayName());
        addColumn(table, "Espacio", 70, ticket -> String.valueOf(ticket.getSpace().getNumber()));
        addColumn(table, "Entrada", 130, ticket -> dateTime(ticket.getEntryTime()));
        addColumn(table, "Salida", 130, ticket -> dateTime(ticket.getExitTime()));
        addColumn(table, "Estado", 90, ticket -> ticket.getStatus().getDisplayName());
        addColumn(table, "Monto", 90, ticket -> ticket.getStatus() == TicketStatus.ACTIVE
                ? "—" : money(ticket.getAmount()));
    }

    /**
     * Adds the payment columns to a table.
     *
     * @param table table of payments
     */
    static void addPaymentColumns(TableView<Payment> table) {
        addColumn(table, "Pago", 60, payment -> String.valueOf(payment.getId()));
        addColumn(table, "Ticket", 70, payment -> "#" + payment.getTicket().getId());
        addColumn(table, "Placa", 90, payment -> payment.getTicket().getVehicle().getPlate());
        addColumn(table, "Fecha y hora", 140, payment -> dateTime(payment.getPaymentDateTime()));
        addColumn(table, "Monto", 90, payment -> money(payment.getAmount()));
        addColumn(table, "Tipo de pago", 120, payment -> payment.getType().getDisplayName());
    }

    /**
     * Replaces the items of a combo box, keeping the selection if it is still valid.
     *
     * @param <T> type of the items
     * @param combo combo box to update
     * @param items new items
     */
    static <T> void replaceItems(ComboBox<T> combo, List<T> items) {
        T selected = combo.getValue();
        combo.getItems().setAll(items);
        if (selected != null && items.contains(selected)) {
            combo.setValue(selected);
        }
    }
}
