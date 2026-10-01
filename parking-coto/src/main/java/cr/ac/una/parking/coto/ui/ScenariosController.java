package cr.ac.una.parking.coto.ui;

import cr.ac.una.parking.coto.scenario.ScenarioResult;
import cr.ac.una.parking.coto.scenario.ScenarioRunner;
import java.util.List;
import java.util.function.Function;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

/**
 * Controller of the test-cases screen. It runs the scenarios of the
 * assignment and shows, for each one, what the rules require and what the
 * system produced.
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.0
 */
public class ScenariosController implements Refreshable {

    /**
     * Creates the controller of the test-cases screen.
     */
    public ScenariosController() {
    }

    @FXML private TableView<ScenarioResult> resultsTable;
    @FXML private Label summaryLabel;

    @FXML
    private void initialize() {
        resultsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        addColumn("N.º", 40, false, result -> String.valueOf(result.getNumber()));
        addColumn("Tipo", 100, false, ScenarioResult::getCategory);
        addColumn("Caso", 170, true, ScenarioResult::getName);
        addColumn("Entrada", 215, true, ScenarioResult::getInput);
        addColumn("Resultado esperado", 200, true, ScenarioResult::getExpected);
        addColumn("Resultado obtenido", 225, true, ScenarioResult::getObtained);
        addColumn("Veredicto", 105, false, result -> result.isPassed() ? "✔ Correcto" : "✘ Falla");
    }

    /** This screen has nothing to reload: the cases always run on new data. */
    @Override
    public void refresh() {
        // intentionally empty
    }

    @FXML
    private void onRunScenarios() {
        List<ScenarioResult> results = new ScenarioRunner().runAll();
        resultsTable.getItems().setAll(results);
        long passed = results.stream().filter(ScenarioResult::isPassed).count();
        summaryLabel.setText(passed + " de " + results.size() + " casos correctos");
        summaryLabel.getStyleClass().removeAll("result-ok", "result-fail");
        summaryLabel.getStyleClass().add(passed == results.size() ? "result-ok" : "result-fail");
    }

    /** Adds a column; long texts can wrap onto several lines. */
    private void addColumn(String title, double width, boolean wrap, Function<ScenarioResult, String> text) {
        TableColumn<ScenarioResult, String> column = new TableColumn<ScenarioResult, String>(title);
        column.setPrefWidth(width);
        column.setMinWidth(wrap ? 110 : width);
        column.setSortable(false);
        column.setCellValueFactory(cell -> new SimpleStringProperty(text.apply(cell.getValue())));
        if (wrap) {
            column.setCellFactory(tableColumn -> {
                TableCell<ScenarioResult, String> cell = new TableCell<ScenarioResult, String>();
                Text label = new Text();
                cell.setGraphic(label);
                cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
                label.wrappingWidthProperty().bind(column.widthProperty().subtract(12));
                label.textProperty().bind(cell.itemProperty());
                return cell;
            });
        } else if ("Veredicto".equals(title)) {
            column.setCellFactory(tableColumn -> new TableCell<ScenarioResult, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    getStyleClass().removeAll("result-ok", "result-fail");
                    setText(empty ? null : item);
                    if (!empty) {
                        getStyleClass().add(item.startsWith("✔") ? "result-ok" : "result-fail");
                    }
                }
            });
        }
        resultsTable.getColumns().add(column);
    }
}
