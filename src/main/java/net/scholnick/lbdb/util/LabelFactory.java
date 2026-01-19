package net.scholnick.lbdb.util;

import javax.swing.*;
import java.awt.*;

/**
 * LabelFactory is a factory for creating JLabels with standard styling.
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */
public final class LabelFactory {
    private LabelFactory() {}

    /** Creates a JLabel with standard label styling. */
    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.black);
        return label;
    }

    /** Creates a JLabel with standard data label styling. */
    public static JLabel createDataLabel() {
        JLabel label = new JLabel();
        label.setForeground(Color.blue);
        return label;
    }
}