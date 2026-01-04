package net.scholnick.lbdb.gui;

import net.scholnick.lbdb.util.*;

import javax.swing.*;

/**
 * AbstractUpdateMaintenance provides a base class for maintenance panels.
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */
public abstract class AbstractUpdateMaintenance extends BasePanel {
    private JButton saveButton;
    private JButton clearButton;
    private JLabel addedDateLabel;
    private MessageListener messageListener;

    /** Set the message listener for sending messages. */
    @Override
    protected JPanel getButtonPanel() {
        JPanel p = new JPanel();
        p.add(getSaveButton());
        p.add(getClearButton());
        return p;
    }

    /** Set the message listener for sending messages. */
    protected final void sendMessage(String text) {
        if (messageListener != null) {
            messageListener.send(text == null ? "" : text.trim());
        }
    }

    /** Clear the current data. */
    protected abstract void clear();

    /** Save the current data. */
    protected abstract void ok();

    /** Reset the focus to the appropriate field. */
    protected abstract void resetFocus();

    /** Get the Clear button. */
    protected final JButton getClearButton() {
        if (clearButton == null) {
            clearButton = GUIUtilities.createButton("Clear");
            clearButton.addActionListener(_ -> { clear();sendMessage(""); });
        }
        return clearButton;
    }

    /** Get the Save button. */
    protected final JButton getSaveButton() {
        if (saveButton == null) {
            saveButton = GUIUtilities.createButton("Save");
            saveButton.addActionListener(_ -> save());
        }
        return saveButton;
    }

    /** Save the current data. */
    public final void save() {
        ok();
        resetFocus();
        repaint();
    }

    /** Get the Added Date label. */
    protected final JLabel getAddedDateLabel() {
        if (addedDateLabel == null) addedDateLabel = LabelFactory.createDataLabel();
        return addedDateLabel;
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }
}
