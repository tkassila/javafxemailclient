package com.metait.javafxemailclient.imap.gmail;

import javafx.scene.layout.*;
import javafx.scene.shape.StrokeType;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * This class static method returns a new Border instance, which has rectangle lines around it
 * in the case:
 * <source>
 *     Pane pane = ...
 *     Border border = BorderRectangle.getBorderRectangleInstance(Color.GRAY, 1, 2);
 *     pane.setBorder(border);
 * </source>
 *
 * Actual code is taken from Jenkow's javafx tutorial web page.
 */
public class BorderRectangle {
    public static Border getBorderRectangleInstance(Color color, int iBorderWidths, int iCornerRadii)
    {
        StrokeType     strokeType     = StrokeType.INSIDE;
        StrokeLineJoin strokeLineJoin = StrokeLineJoin.MITER;
        StrokeLineCap  strokeLineCap  = StrokeLineCap.BUTT;
        double         miterLimit     = 10;
        double         dashOffset     = 0;
        List<Double> dashArray      = null;

        BorderStrokeStyle borderStrokeStyle =
                new BorderStrokeStyle(
                        strokeType,
                        strokeLineJoin,
                        strokeLineCap,
                        miterLimit,
                        dashOffset,
                        dashArray
                );

        BorderStroke borderStroke =
                new BorderStroke(
                        Color.valueOf(color.toString() /* "08ff80"*/),
                        borderStrokeStyle,
                        new CornerRadii(iCornerRadii),
                        new BorderWidths(iBorderWidths)
                );

        Border border = new Border(borderStroke);
        return border;
    }
}
