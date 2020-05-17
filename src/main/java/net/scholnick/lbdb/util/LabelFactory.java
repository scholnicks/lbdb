package net.scholnick.lbdb.util;

import javax.swing.*;
import java.awt.*;

public final class LabelFactory {
    private LabelFactory() {}

    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.black);
        return label;
    }

    public static JLabel createDataLabel() {
        JLabel label = new JLabel();
        label.setForeground(Color.blue);
        return label;
    }
}