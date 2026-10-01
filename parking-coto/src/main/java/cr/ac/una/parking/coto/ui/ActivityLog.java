package cr.ac.una.parking.coto.ui;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Recent activity shared by every screen: each operation that works or is
 * rejected leaves one line, newest first.
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.0
 */
final class ActivityLog {

    /** Maximum number of lines kept. */
    private static final int MAX_ENTRIES = 40;
    /** Format of the clock shown before each line. */
    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm:ss");

    /** Lines of the log, newest first. */
    private final ObservableList<String> entries = FXCollections.observableArrayList();

    /**
     * Adds a line at the top of the log.
     *
     * @param text description of what happened
     */
    void add(String text) {
        entries.add(0, "[" + CLOCK.format(LocalTime.now()) + "]  " + text);
        if (entries.size() > MAX_ENTRIES) {
            entries.remove(entries.size() - 1);
        }
    }

    /**
     * Adds a line for an operation that a business rule rejected.
     *
     * @param operation name of the operation
     * @param error exception that rejected it
     */
    void rejected(String operation, RuntimeException error) {
        add("✘ " + operation + " rechazado · " + FeedbackBanner.ruleOf(error));
    }

    /**
     * Returns the lines of the log.
     *
     * @return observable list, newest line first
     */
    ObservableList<String> getEntries() {
        return entries;
    }
}
