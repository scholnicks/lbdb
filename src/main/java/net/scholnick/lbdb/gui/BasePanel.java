package net.scholnick.lbdb.gui;

import net.scholnick.lbdb.gui.author.AuthorSelectionEvent;
import net.scholnick.lbdb.gui.author.AuthorSelectionListener;
import net.scholnick.lbdb.gui.title.TitleSelectionEvent;
import net.scholnick.lbdb.gui.title.TitleSelectionListener;

import javax.swing.*;
import java.awt.*;

public abstract class BasePanel extends JPanel {
	protected void buildGUI() {
		setLayout(new BorderLayout());
		add(getInputPanel(), BorderLayout.CENTER);
		add(getButtonPanel(), BorderLayout.SOUTH);
	}

	protected abstract JPanel getInputPanel();

	protected abstract JPanel getButtonPanel();

	protected void reload() {
		validate();
		repaint();
	}
	
	public final void addTitleSelectionListener(TitleSelectionListener listener) {
		listenerList.add(TitleSelectionListener.class, listener);
	}

	final void fireTitleSelection(TitleSelectionEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TitleSelectionListener.class) {
				((TitleSelectionListener) listeners[i + 1]).select(event);
			}
		}
	}

	public final void addAuthorSelectionListener(AuthorSelectionListener listener) {
		listenerList.add(AuthorSelectionListener.class, listener);
	}

	final void fireAuthorSelection(AuthorSelectionEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == AuthorSelectionListener.class) {
				((AuthorSelectionListener) listeners[i + 1]).select(event);
			}
		}
	}

}
