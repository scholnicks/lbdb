package net.scholnick.lbdb.gui;

import net.scholnick.lbdb.gui.author.*;
import net.scholnick.lbdb.gui.title.*;

import javax.swing.*;
import java.awt.*;

/**
 * BasePanel provides a base class for all GUI panels.
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */
public abstract class BasePanel extends JPanel {
    /** Builds the GUI components. */
    protected void buildGUI() {
        setLayout(new BorderLayout());
        add(getInputPanel(), BorderLayout.CENTER);
        add(getButtonPanel(), BorderLayout.SOUTH);
    }

    /** Returns the input panel. */
    protected abstract JPanel getInputPanel();

    /** Returns the button panel. */
    protected abstract JPanel getButtonPanel();

    /** Reload the panel data. */
    protected void reload() {
        validate();
        repaint();
    }

    /** Listener list for event handling. */
    public final void addTitleSelectionListener(TitleSelectionListener listener) {
        listenerList.add(TitleSelectionListener.class, listener);
    }

    /** Fire a title selection event. */
    final void fireTitleSelection(TitleSelectionEvent event) {
        Object[] listeners = listenerList.getListenerList();

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TitleSelectionListener.class) {
                ((TitleSelectionListener) listeners[i + 1]).select(event);
            }
        }
    }

    /** Listener list for event handling. */
    public final void addAuthorSelectionListener(AuthorSelectionListener listener) {
        listenerList.add(AuthorSelectionListener.class, listener);
    }

    /** Fire an author selection event. */
    final void fireAuthorSelection(AuthorSelectionEvent event) {
        Object[] listeners = listenerList.getListenerList();

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == AuthorSelectionListener.class) {
                ((AuthorSelectionListener) listeners[i + 1]).select(event);
            }
        }
    }

}
