package cr.ac.una.parking.coto.ui;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/**
 * Controller of the main window. It only coordinates the screens: the side
 * menu shows one screen at a time, and the screen reloads its data from the
 * shared parking lot every time it is shown.
 *
 * <p>If the files {@code logo.png} or {@code banner.png} are placed next to
 * the FXML files, they replace the built-in drawings of the side menu and of
 * the banner.</p>
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 2.0
 */
public class MainController {

    /**
     * Creates the controller of the main window.
     */
    public MainController() {
    }

    /** Format of the date shown in the banner. */
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-CR"));

    /** One screen of the side menu: what is shown and what reloads it. */
    private static final class Screen {
        /** Root node of the screen. */
        private final Node view;
        /** Controller that reloads the screen. */
        private final Refreshable controller;

        Screen(Node view, Refreshable controller) {
            this.view = view;
            this.controller = controller;
        }
    }

    /** Screens of the side menu, by the button that opens them. */
    private final Map<Toggle, Screen> screens = new LinkedHashMap<Toggle, Screen>();

    @FXML private ToggleGroup navigation;
    @FXML private StackPane logoHolder;
    @FXML private StackPane bannerArt;
    @FXML private Label dateLabel;
    @FXML private ToggleButton dashboardButton;
    @FXML private ToggleButton vehiclesButton;
    @FXML private ToggleButton spacesButton;
    @FXML private ToggleButton entryButton;
    @FXML private ToggleButton exitButton;
    @FXML private ToggleButton paymentButton;
    @FXML private ToggleButton ticketsButton;
    @FXML private ToggleButton reportsButton;
    @FXML private ToggleButton scenariosButton;
    @FXML private Node dashboardView;
    @FXML private Node vehiclesView;
    @FXML private Node spacesView;
    @FXML private Node entryView;
    @FXML private Node exitView;
    @FXML private Node paymentView;
    @FXML private Node ticketsView;
    @FXML private Node reportsView;
    @FXML private Node scenariosView;
    @FXML private DashboardController dashboardViewController;
    @FXML private VehiclesController vehiclesViewController;
    @FXML private SpacesController spacesViewController;
    @FXML private EntryController entryViewController;
    @FXML private ExitController exitViewController;
    @FXML private PaymentController paymentViewController;
    @FXML private TicketsController ticketsViewController;
    @FXML private ReportsController reportsViewController;
    @FXML private ScenariosController scenariosViewController;

    @FXML
    private void initialize() {
        register(dashboardButton, "dashboard", dashboardView, dashboardViewController);
        register(vehiclesButton, "car", vehiclesView, vehiclesViewController);
        register(spacesButton, "parking", spacesView, spacesViewController);
        register(entryButton, "login", entryView, entryViewController);
        register(exitButton, "logout", exitView, exitViewController);
        register(paymentButton, "money", paymentView, paymentViewController);
        register(ticketsButton, "ticket", ticketsView, ticketsViewController);
        register(reportsButton, "reports", reportsView, reportsViewController);
        register(scenariosButton, "check", scenariosView, scenariosViewController);

        navigation.selectedToggleProperty().addListener((observable, previous, current) -> {
            if (current == null) {
                previous.setSelected(true);
            } else {
                show(current);
            }
        });
        show(dashboardButton);

        String today = LocalDate.now().format(DATE_FORMAT);
        dateLabel.setText(Character.toUpperCase(today.charAt(0)) + today.substring(1));
        drawLogo();
        drawBannerArt();
    }

    /** Connects a menu button with its icon and its screen. */
    private void register(ToggleButton button, String icon, Node view, Refreshable controller) {
        button.setGraphic(Icons.of(icon, 20, "white"));
        screens.put(button, new Screen(view, controller));
    }

    /** Shows the screen of the selected button and reloads its data. */
    private void show(Toggle selected) {
        for (Map.Entry<Toggle, Screen> entry : screens.entrySet()) {
            boolean visible = entry.getKey() == selected;
            entry.getValue().view.setVisible(visible);
            entry.getValue().view.setManaged(visible);
        }
        screens.get(selected).controller.refresh();
    }

    /** Puts the logo in the side menu: the image file if it exists, otherwise a parking sign. */
    private void drawLogo() {
        Node logo = loadImage("logo.png", 84, 84);
        logoHolder.getChildren().setAll(logo == null ? parkingSign("logo-sign") : logo);
    }

    /** Puts the art in the banner: the image file if it exists, otherwise a parking sign. */
    private void drawBannerArt() {
        Node art = loadImage("banner.png", 420, 110);
        bannerArt.getChildren().setAll(art == null ? parkingSign("banner-sign") : art);
    }

    /** Loads an optional image placed next to the FXML files. */
    private Node loadImage(String name, double width, double height) {
        URL url = MainController.class.getResource(name);
        if (url == null) {
            return null;
        }
        ImageView view = new ImageView(new Image(url.toExternalForm(), width, height, true, true));
        view.setPreserveRatio(true);
        return view;
    }

    /** Builds a blue square with a white P, like a parking sign. */
    private Node parkingSign(String styleClass) {
        Label sign = new Label("P");
        sign.getStyleClass().addAll("parking-sign", styleClass);
        return sign;
    }
}
