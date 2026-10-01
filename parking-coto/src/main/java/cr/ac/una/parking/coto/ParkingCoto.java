package cr.ac.una.parking.coto;

import cr.ac.una.parking.coto.scenario.ScenarioResult;
import cr.ac.una.parking.coto.scenario.ScenarioRunner;
import cr.ac.una.parking.coto.ui.ParkingApp;
import java.util.Arrays;
import java.util.List;
import javafx.application.Application;

/**
 * Main entry point for the Parking Coto parking management system.
 *
 * <p>Without arguments it opens the JavaFX application. With {@code --console}
 * it runs the demonstration scenarios and prints them as text, and with
 * {@code --markdown} it prints them as a Markdown table, which is how the
 * test table of the report is produced. The text mode needs no graphics, so it
 * also works as a fallback where JavaFX is not available.</p>
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 2.0
 */
public class ParkingCoto {

    /**
     * Creates the application entry point.
     */
    public ParkingCoto() {
    }

    /**
     * Starts the program.
     *
     * @param args {@code --console} prints the scenarios as text, {@code --markdown}
     *             prints them as a Markdown table, no arguments opens the window
     */
    public static void main(String[] args) {
        List<String> options = Arrays.asList(args);
        if (options.contains("--markdown")) {
            printMarkdown(new ScenarioRunner().runAll());
        } else if (options.contains("--console")) {
            printText(new ScenarioRunner().runAll());
        } else {
            Application.launch(ParkingApp.class, args);
        }
    }

    /**
     * Prints every scenario as plain text.
     *
     * @param results scenario results to print
     */
    private static void printText(List<ScenarioResult> results) {
        int passed = 0;
        for (ScenarioResult result : results) {
            System.out.println(result.getNumber() + ". [" + result.getCategory() + "] " + result.getName());
            System.out.println("   Entrada:  " + result.getInput());
            System.out.println("   Esperado: " + result.getExpected());
            System.out.println("   Obtenido: " + result.getObtained());
            System.out.println("   " + (result.isPassed() ? "CORRECTO" : "FALLA"));
            System.out.println();
            if (result.isPassed()) {
                passed++;
            }
        }
        System.out.println(passed + " de " + results.size() + " casos correctos");
    }

    /**
     * Prints every scenario as a Markdown table.
     *
     * @param results scenario results to print
     */
    private static void printMarkdown(List<ScenarioResult> results) {
        System.out.println("| # | Tipo | Caso | Entrada | Resultado esperado | Resultado obtenido | Veredicto |");
        System.out.println("|---|---|---|---|---|---|---|");
        for (ScenarioResult result : results) {
            System.out.println("| " + result.getNumber() + " | " + result.getCategory() + " | " + result.getName()
                    + " | " + result.getInput() + " | " + result.getExpected() + " | " + result.getObtained()
                    + " | " + (result.isPassed() ? "Correcto" : "Falla") + " |");
        }
    }
}
