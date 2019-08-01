package net.scholnick.lbdb.gui;

import net.scholnick.lbdb.util.NullSafe;

import javax.swing.*;
import javax.swing.text.Document;



public final class TrimmedTextField extends JTextField {
    public TrimmedTextField() {
        super();
    }

    public TrimmedTextField(Document doc, String text, int columns) {
        super(doc, text, columns);
    }

    public TrimmedTextField(int columns) {
        super(columns);
    }

    public TrimmedTextField(String text, int columns) {
        super(text, columns);
    }

    public TrimmedTextField(String text) {
        super(text);
    }

    @Override
    public String getText() {
        return NullSafe.trim(super.getText());
    }

    @Override
    public String toString() {
        return getText();
    }

    private static final long serialVersionUID = -4481801440944080319L;

}
