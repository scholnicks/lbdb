package net.scholnick.lbdb.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * BaseDialog for modal dialogs with OK and Cancel buttons.
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */
public abstract class BaseDialog extends JDialog {
    private JButton okButton;
    private JButton cancelButton;
    private boolean approved;

    public BaseDialog() {
        super();
        setModal(true);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                approved = false;
            }
        });
    }

    @Override
    protected final JRootPane createRootPane() {
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        JRootPane rootPane = new JRootPane();
        rootPane.registerKeyboardAction(l -> {
            approved = false;
            setVisible(false);
        }, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        return rootPane;
    }

    /** Build the GUI components of the dialog. */
    protected void buildGUI() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(getInputPanel(), BorderLayout.CENTER);
        getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);
    }

    /** Create the panel containing the OK and Cancel buttons. */
    protected JPanel getButtonPanel() {
        JPanel p = new JPanel();
        p.add(getOKButton());
        p.add(getCancelButton());
        return p;
    }

    /** Get the OK button, creating it if necessary. */
    protected final JButton getOKButton() {
        if (okButton == null) {
            okButton = new JButton("Ok");
            okButton.addActionListener(l -> ok());
        }
        return okButton;
    }

    /** Get the Cancel button, creating it if necessary. */
    private JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(l -> cancel());
        }
        return cancelButton;
    }

    /** Handle the OK button being pressed. */
    protected void ok() {
        close(true);
    }

    /** Handle the Cancel button being pressed. */
    private void cancel() {
        close(false);
    }

    /** Close the dialog, setting the approved flag. */
    private void close(boolean approvedIn) {
        approved = approvedIn;
        setVisible(false);
    }

    /** Get the panel containing the input components. Override to provide input fields. */
    protected JPanel getInputPanel() {
        return null;
    }

    /** Get the component that should receive initial focus when the dialog is shown. */
    protected abstract JComponent getInitialFocusComponent();

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        getInitialFocusComponent().requestFocus();
    }

    /** Repaint and validate the dialog screen. */
    protected final void repaintScreen() {
        validate();
        repaint();
    }

    /** Check if the dialog was approved (OK pressed). */
    public boolean isApproved() {
        return approved;
    }
}