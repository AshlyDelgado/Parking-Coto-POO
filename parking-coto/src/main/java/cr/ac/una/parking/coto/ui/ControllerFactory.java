package cr.ac.una.parking.coto.ui;

import cr.ac.una.parking.coto.ParkingLot;
import javafx.util.Callback;

/**
 * Creates the controllers declared in the FXML files and gives all of them the
 * same {@link ParkingLot} and the same {@link ActivityLog}, so every screen
 * works on the same data.
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.1
 */
final class ControllerFactory implements Callback<Class<?>, Object> {

    /** Parking lot shared by every controller. */
    private final ParkingLot parkingLot;
    /** Activity log shared by every controller. */
    private final ActivityLog activityLog;

    /**
     * Creates a factory bound to one parking lot.
     *
     * @param parkingLot parking lot given to the controllers
     * @param activityLog activity log given to the controllers
     */
    ControllerFactory(ParkingLot parkingLot, ActivityLog activityLog) {
        this.parkingLot = parkingLot;
        this.activityLog = activityLog;
    }

    /**
     * Instantiates a controller with the most complete constructor it offers:
     * parking lot and log, only the parking lot, or none.
     *
     * @param type controller class named in the FXML file
     * @return the new controller
     */
    @Override
    public Object call(Class<?> type) {
        try {
            try {
                return type.getConstructor(ParkingLot.class, ActivityLog.class).newInstance(parkingLot, activityLog);
            } catch (NoSuchMethodException withLog) {
                try {
                    return type.getConstructor(ParkingLot.class).newInstance(parkingLot);
                } catch (NoSuchMethodException withLot) {
                    return type.getConstructor().newInstance();
                }
            }
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("No se pudo crear el controlador " + type.getName(), e);
        }
    }
}
