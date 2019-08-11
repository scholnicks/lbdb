package net.scholnick.lbdb.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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
		rootPane.registerKeyboardAction(l -> { approved = false; setVisible(false); }, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
		return rootPane;
	}

	protected void buildGUI() {
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(getInputPanel(), BorderLayout.CENTER);
		getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);
	}

	protected JPanel getButtonPanel() {
		JPanel p = new JPanel();
		p.add(getOKButton());
		p.add(getCancelButton());
		return p;
	}

	protected final JButton getOKButton() {
		if (okButton == null) {
			okButton = new JButton("Ok");
			okButton.addActionListener(l -> ok());
		}
		return okButton;
	}

	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(l -> cancel());
		}
		return cancelButton;
	}

	protected void ok() {
		close(true);
	}

	private void cancel() {
		close(false);
	}

	private void close(boolean approvedIn) {
		approved = approvedIn;
		setVisible(false);
	}

	protected abstract JPanel getInputPanel();

	protected abstract JComponent getInitialFocusComponent();

	public void paint(Graphics g) {
		super.paint(g);
		getInitialFocusComponent().requestFocus();
	}

	public void repaintScreen() {
		validate();
		repaint();
	}

	public boolean isApproved() {
		return approved;
	}
}