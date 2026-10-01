package cr.ac.una.parking.coto.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

/**
 * Groups a date picker and a time field so the user can type any date and
 * time, which is what allows demonstrating stays of one minute or eleven
 * hours without waiting.
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.0
 */
final class DateTimeFields {

    /** Format of the date typed by the user. */
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    /** Format of the time typed by the user. */
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    /** Control used to choose the date. */
    private final DatePicker datePicker;
    /** Control used to type the time. */
    private final TextField timeField;

    /**
     * Wraps the two controls and fills them with the current minute.
     *
     * @param datePicker date control
     * @param timeField time control
     */
    DateTimeFields(DatePicker datePicker, TextField timeField) {
        this.datePicker = datePicker;
        this.timeField = timeField;
        this.datePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return date == null ? "" : DATE_FORMAT.format(date);
            }

            @Override
            public LocalDate fromString(String text) {
                return text == null || text.trim().isEmpty() ? null : LocalDate.parse(text.trim(), DATE_FORMAT);
            }
        });
        this.datePicker.setPromptText("dd/MM/aaaa");
        setNow();
    }

    /** Fills the controls with the current date and minute. */
    void setNow() {
        setValue(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
    }

    /**
     * Fills the controls with a date and time.
     *
     * @param value date and time to show
     */
    void setValue(LocalDateTime value) {
        datePicker.setValue(value.toLocalDate());
        timeField.setText(TIME_FORMAT.format(value));
    }

    /**
     * Reads the date and time typed by the user.
     *
     * @return the date and time
     * @throws IllegalArgumentException if the date or the time are missing or invalid
     */
    LocalDateTime getValue() {
        LocalDate date;
        try {
            String typed = datePicker.getEditor().getText();
            date = typed == null || typed.trim().isEmpty() ? null : LocalDate.parse(typed.trim(), DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("La fecha debe tener el formato dd/MM/aaaa, por ejemplo 01/10/2026");
        }
        if (date == null) {
            throw new IllegalArgumentException("Seleccione la fecha");
        }
        datePicker.setValue(date);
        try {
            LocalTime time = LocalTime.parse(timeField.getText().trim(), TIME_FORMAT);
            return date.atTime(time);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("La hora debe tener el formato HH:mm, por ejemplo 08:30");
        }
    }
}
