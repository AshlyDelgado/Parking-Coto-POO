package cr.ac.una.parking.coto.ui;

import cr.ac.una.parking.coto.ParkingLot;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX application of Parking Coto.
 *
 * <p>It creates the single {@link ParkingLot} of the program, loads the FXML
 * screens designed with Scene Builder and hands the parking lot to every
 * controller. The screens never create domain objects of their own.</p>
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 2.0
 */
public class ParkingApp extends Application {

    /**
     * Creates the application; JavaFX instantiates it when the program starts.
     */
    public ParkingApp() {
    }

    /**
     * Builds the main window.
     *
     * @param stage primary window supplied by JavaFX
     * @throws IOException if an FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> Platform.runLater(() -> UiSupport.showUnexpected(error)));
        Parent root = load(new ParkingLot());
        Scene scene = new Scene(root, 1280, 690);
        scene.getStylesheets().add(ParkingApp.class.getResource("parking.css").toExternalForm());
        stage.setTitle("ParkingCoto · Sistema de gestión de parqueos");
        stage.setMinWidth(1260);
        stage.setMinHeight(640);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Loads the main FXML file connecting every controller to one parking lot.
     *
     * @param parkingLot parking lot shared by the controllers
     * @return root node of the main window
     * @throws IOException if an FXML file cannot be loaded
     */
    static Parent load(ParkingLot parkingLot) throws IOException {
        FXMLLoader loader = new FXMLLoader(ParkingApp.class.getResource("MainView.fxml"));
        loader.setControllerFactory(new ControllerFactory(parkingLot, new ActivityLog()));
        return loader.load();
    }
}
