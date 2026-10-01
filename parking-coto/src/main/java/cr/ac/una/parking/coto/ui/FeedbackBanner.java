package cr.ac.una.parking.coto.ui;

import cr.ac.una.parking.coto.exception.ParkingException;
import javafx.scene.control.Label;

/**
 * Message shown inside a screen after each operation: green when the
 * operation worked, red when a business rule rejected it.
 *
 * <p>For a rejected operation it shows the name of the exception thrown by
 * the domain, so it is clear which rule was applied.</p>
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.0
 */
final class FeedbackBanner {

    /** Label that displays the message. */
    private final Label label;

    /**
     * Wraps a label and hides it until there is something to say.
     *
     * @param label label placed in the screen
     */
    FeedbackBanner(Label label) {
        this.label = label;
        this.label.setWrapText(true);
        hide();
    }

    /**
     * Shows a message for an operation that worked.
     *
     * @param message text to show
     */
    void success(String message) {
        show("✔  " + message, "feedback-ok");
    }

    /**
     * Shows the reason why an operation was rejected.
     *
     * @param error exception thrown by the domain or by the validation of the form
     */
    void error(RuntimeException error) {
        show("✘  " + ruleOf(error) + " · " + error.getMessage(), "feedback-error");
    }

    /**
     * Names the rule that rejected an operation.
     *
     * @param error exception thrown by the domain or by the validation of the form
     * @return the exception class name for business rules, or a generic title for bad input
     */
    static String ruleOf(RuntimeException error) {
        return error instanceof ParkingException ? error.getClass().getSimpleName() : "Datos incorrectos";
    }

    /** Hides the message. */
    void hide() {
        label.setVisible(false);
        label.setManaged(false);
    }

    /** Applies the text and the style to the label and makes it visible. */
    private void show(String text, String styleClass) {
        label.getStyleClass().removeAll("feedback-ok", "feedback-error");
        label.getStyleClass().add(styleClass);
        label.setText(text);
        label.setVisible(true);
        label.setManaged(true);
    }
}
