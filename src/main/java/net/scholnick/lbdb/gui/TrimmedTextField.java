package net.scholnick.lbdb.gui;

import net.scholnick.lbdb.util.LimitedStyledDocument;
import net.scholnick.lbdb.util.NullSafe;

import javax.swing.*;

public final class TrimmedTextField extends JTextField {
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
