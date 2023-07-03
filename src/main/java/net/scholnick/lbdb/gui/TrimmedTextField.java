package net.scholnick.lbdb.gui;

import net.scholnick.lbdb.util.*;

import javax.swing.JTextField;
import java.awt.Dimension;

public final class TrimmedTextField extends JTextField {
    public static TrimmedTextField create(int columns, int maxChars, Dimension size) {
        TrimmedTextField t = new TrimmedTextField(columns,maxChars);
        GUIUtilities.setSizes(t,size);
        return t;
    }

    public TrimmedTextField(int columns, int maxChars) {
        super(columns);
        setDocument(new LimitedStyledDocument(maxChars));
    }

    @Override
    public String getText() {
        return NullSafe.trim(super.getText());
    }

    @Override
    public String toString() {
        return getText();
    }
}
