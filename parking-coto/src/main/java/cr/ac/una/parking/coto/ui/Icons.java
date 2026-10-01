package cr.ac.una.parking.coto.ui;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

/**
 * Vector icons drawn in code, so the interface needs no image files.
 *
 * <p>Every icon is described in a 24 by 24 box and scaled to the requested
 * size and color.</p>
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.0
 */
final class Icons {

    /** Filled shapes of each icon, in a 24 by 24 box. */
    private static final Map<String, String> FILLS = new HashMap<String, String>();
    /** Outlined shapes of each icon, in a 24 by 24 box. */
    private static final Map<String, String> STROKES = new HashMap<String, String>();

    static {
        FILLS.put("dashboard", "M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z");
        FILLS.put("car", "M18.92 6.01C18.72 5.42 18.16 5 17.5 5h-11c-.66 0-1.21.42-1.42 1.01L3 12v8c0 .55.45 1 1 1h1"
                + "c.55 0 1-.45 1-1v-1h12v1c0 .55.45 1 1 1h1c.55 0 1-.45 1-1v-8l-2.08-5.99zM6.5 16c-.83 0-1.5-.67-1.5-1.5"
                + "S5.67 13 6.5 13s1.5.67 1.5 1.5S7.33 16 6.5 16zm11 0c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5"
                + "-.67 1.5-1.5 1.5zM5 11l1.5-4.5h11L19 11H5z");
        FILLS.put("truck", "M20 8h-3V4H3c-1.1 0-2 .9-2 2v11h2c0 1.66 1.34 3 3 3s3-1.34 3-3h6c0 1.66 1.34 3 3 3s3-1.34 3-3h2"
                + "v-5l-3-4zM6 18.5c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5zm13.5-9l1.96 2.5H17"
                + "V9.5h2.5zm-1.5 9c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5z");
        FILLS.put("motorcycle", "M5 12.5a4 4 0 1 0 0 8 4 4 0 1 0 0-8zM5 14.2a2.3 2.3 0 1 1 0 4.6 2.3 2.3 0 1 1 0-4.6z"
                + "M19 12.5a4 4 0 1 0 0 8 4 4 0 1 0 0-8zM19 14.2a2.3 2.3 0 1 1 0 4.6 2.3 2.3 0 1 1 0-4.6z");
        STROKES.put("motorcycle", "M5 16.5L9.2 9.5H13.6L19 16.5M9.2 9.5L7.8 6.5H5.2M13.6 9.5L14.8 6H17.6");
        FILLS.put("parking", "M13 3H6v18h4v-6h3c3.31 0 6-2.69 6-6s-2.69-6-6-6zm.2 8H10V7h3.2c1.1 0 2 .9 2 2s-.9 2-2 2z");
        FILLS.put("block", "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zM4 12c0-4.42 3.58-8 8-8 1.85 0 3.55"
                + ".63 4.9 1.69L5.69 16.9C4.63 15.55 4 13.85 4 12zm8 8c-1.85 0-3.55-.63-4.9-1.69L18.31 7.1C19.37 8.45 20 10.15"
                + " 20 12c0 4.42-3.58 8-8 8z");
        FILLS.put("available", "M16.53 11.06L15.47 10l-4.88 4.88-2.12-2.12-1.06 1.06L10.59 17l5.94-5.94zM19 3h-1V1h-2v2H8V1H6v2H5"
                + "c-1.11 0-1.99.9-1.99 2L3 19c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V8h14v11z");
        FILLS.put("money", "M19 14V6c0-1.1-.9-2-2-2H3c-1.1 0-2 .9-2 2v8c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2zm-9-1c-1.66 0-3-1.34-3-3"
                + "s1.34-3 3-3 3 1.34 3 3-1.34 3-3 3zm13-6v11c0 1.1-.9 2-2 2H4v-2h17V7h2z");
        FILLS.put("login", "M11 7L9.6 8.4l2.6 2.6H2v2h10.2l-2.6 2.6L11 17l5-5-5-5zm9 12h-8v2h8c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2h-8v2h8v14z");
        FILLS.put("logout", "M17 7l-1.41 1.41L18.17 11H8v2h10.17l-2.58 2.58L17 17l5-5zM4 5h8V3H4c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h8"
                + "v-2H4V5z");
        FILLS.put("ticket", "M22 10V6c0-1.11-.9-2-2-2H4c-1.1 0-1.99.89-1.99 2v4c1.1 0 1.99.9 1.99 2s-.89 2-2 2v4c0 1.1.9 2 2 2h16"
                + "c1.1 0 2-.9 2-2v-4c-1.1 0-2-.9-2-2s.9-2 2-2zm-9 7.5h-2v-2h2v2zm0-4.5h-2v-2h2v2zm0-4.5h-2v-2h2v2z");
        FILLS.put("reports", "M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM9 17H7v-7h2v7zm4 0h-2V7h2"
                + "v10zm4 0h-2v-4h2v4z");
        FILLS.put("check", "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59"
                + "L19 8l-9 9z");
    }

    /** Utility class: it is never instantiated. */
    private Icons() {
    }

    /**
     * Builds an icon.
     *
     * @param name icon name, such as {@code car} or {@code ticket}
     * @param size width and height in pixels
     * @param color color in CSS notation, such as {@code #1b5fc1} or {@code white}
     * @return a node ready to be used as a graphic
     * @throws IllegalArgumentException if the icon does not exist
     */
    static Node of(String name, double size, String color) {
        if (!FILLS.containsKey(name)) {
            throw new IllegalArgumentException("No existe el icono " + name);
        }
        Color paint = Color.web(color);
        double scale = size / 24.0;
        Group icon = new Group();

        SVGPath filled = new SVGPath();
        filled.setContent(FILLS.get(name));
        filled.setFillRule(FillRule.EVEN_ODD);
        filled.setFill(paint);
        filled.setScaleX(scale);
        filled.setScaleY(scale);
        icon.getChildren().add(filled);

        String outline = STROKES.get(name);
        if (outline != null) {
            SVGPath stroked = new SVGPath();
            stroked.setContent(outline);
            stroked.setFill(null);
            stroked.setStroke(paint);
            stroked.setStrokeWidth(1.8);
            stroked.setStrokeLineCap(StrokeLineCap.ROUND);
            stroked.setStrokeLineJoin(StrokeLineJoin.ROUND);
            stroked.setScaleX(scale);
            stroked.setScaleY(scale);
            icon.getChildren().add(stroked);
        }
        return icon;
    }
}
