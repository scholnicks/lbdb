package net.scholnick.lbdb.gui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;

public abstract class BaseDialog extends JDialog {
	private static final long serialVersionUID = 9061414244058378906L;

	private JButton okButton;
	private JButton cancelButton;
	private boolean dataChanged;
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

	protected final JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(l -> cancel());
		}
		return cancelButton;
	}

	protected void ok() {
		approved = true;
		close(approved);
	}

	protected void cancel() {
		approved = false;
		close(approved);
	}

	protected final void close(boolean approvedIn) {
		approved = approvedIn;
		setVisible(false);
	}

	protected abstract JPanel getInputPanel();

	protected abstract JComponent getInitialFocusComponent();

	public void paint(Graphics g) {
		super.paint(g);
		getInitialFocusComponent().requestFocus();
	}

	public boolean isApproved() {
		return approved;
	}

	public boolean isDataChanged() {
		return dataChanged;
	}

	public void setDataChanged(boolean b) {
		dataChanged = b;
	}

	protected void loadData(JTextComponent field, String value) {
		if (value != null) {
			field.setText(value);
		}
	}

	/** @param approved The approved to set. */
	protected void setApproved(boolean approved) {
		this.approved = approved;
	}
}