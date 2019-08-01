package net.scholnick.lbdb.gui;

import net.scholnick.lbdb.util.*;

import javax.swing.*;



public abstract class AbstractUpdateMaintenance extends BasePanel {
	private JButton saveButton;
	private JButton clearButton;
	private JLabel addedDateLabel;
	private MessageListener messageListener;

	@Override
	protected JPanel getButtonPanel() {
		JPanel p = new JPanel();
		p.add(getSaveButton());
		p.add(getClearButton());

		return p;
	}

	protected final void sendMessage(String text) {
	    if (messageListener != null && ! NullSafe.isEmpty(text)) {
	        messageListener.send(text);
        }
    }

	protected abstract void clear();

	protected abstract void ok();

	protected abstract void resetFocus();

	protected final JButton getClearButton() {
		if (clearButton == null) {
			clearButton = new JButton("Clear");
			clearButton.addActionListener(e -> clear());
		}
		return clearButton;
	}

	protected final JButton getSaveButton() {
		if (saveButton == null) {
			saveButton = new JButton("Save");
			saveButton.addActionListener(e -> save());
        }
		return saveButton;
	}

	public final void save() {
		ok();
		resetFocus();
		repaint();
	}

	protected final JLabel getAddedDateLabel() {
		if (addedDateLabel == null)
			addedDateLabel = LabelFactory.createDataLabel();

		return addedDateLabel;
	}

    public MessageListener getMessageListener() {
        return messageListener;
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    private static final long serialVersionUID = 7366488641110084605L;
}
