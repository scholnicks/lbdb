package net.scholnick.lbdb.gui.author;


import net.scholnick.lbdb.domain.Author;
import net.scholnick.lbdb.gui.AbstractUpdateMaintenance;
import net.scholnick.lbdb.gui.TrimmedTextField;
import net.scholnick.lbdb.service.AuthorService;
import net.scholnick.lbdb.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@Component
public class AuthorMaintenance extends AbstractUpdateMaintenance {
	private JTextField lastNameField;
	private JTextField firstNameField;
	private JTextField webSiteField;
	private JButton deleteButton;

	private Author author;

	@Autowired
	private AuthorService authorService;
	
	public AuthorMaintenance() {
		buildGUI();
	}

	protected void resetFocus() {
		getFirstNameField().requestFocus();
	}

	@Override
	protected void clear() {
		getLastNameField().setText("");
		getFirstNameField().setText("");
		getWebSiteField().setText("");
		getAddedDateLabel().setText("");
		author = null;
	}

	private JButton getDeleteButton() {
		if (deleteButton == null) {
			deleteButton = new JButton("Delete");
			deleteButton.addActionListener(e -> removeAuthor());
		}

		return deleteButton;
	}

	private void removeAuthor() {
		if (JOptionPane.showConfirmDialog(this, "Delete Author?") == JOptionPane.YES_OPTION) {
			try {
				authorService.delete(getAuthor());
				GUIUtilities.showMessageDialog(author + " deleted");
				clear();
			}
			catch (Exception e) {
				GUIUtilities.showMessageDialog("Unable to delete author");
			}
		}
	}

	@Override
	protected JPanel getButtonPanel() {
		JPanel p = new JPanel();
		p.add(getSaveButton());
		p.add(getDeleteButton());
		p.add(getClearButton());

		return p;
	}

	protected JPanel getInputPanel() {
		JPanel p = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(5, 5, 0, 0);

		p.add(LabelFactory.createLabel("First Name"), gbc);
		gbc.gridx++;
		p.add(getFirstNameField(), gbc);

		gbc.gridy++;
		gbc.gridx = 0;
		p.add(LabelFactory.createLabel("Last Name"), gbc);
		gbc.gridx++;
		p.add(getLastNameField(), gbc);

		gbc.gridy++;
		gbc.gridx = 0;
		p.add(LabelFactory.createLabel("Website"), gbc);
		gbc.gridx++;
		p.add(getWebSiteField(), gbc);
		gbc.gridx++;
		p.add(getOpenWebSiteIcon(), gbc);

		gbc.gridy++;
		gbc.gridx = 0;
		p.add(LabelFactory.createLabel("Added Date"), gbc);
		gbc.gridx++;
		p.add(getAddedDateLabel(), gbc);

		return p;
	}

	protected JComponent getInitialFocusComponent() {
		return getFirstNameField();
	}

	private JTextField getFirstNameField() {
		if (firstNameField == null) {
			firstNameField = new TrimmedTextField(10);
			firstNameField.setDocument(new LimitedStyledDocument(100));
		}

		return firstNameField;
	}

	private JTextField getLastNameField() {
		if (lastNameField == null) {
			lastNameField = new TrimmedTextField(20);
			lastNameField.setDocument(new LimitedStyledDocument(100));
		}

		return lastNameField;
	}

	private JTextField getWebSiteField() {
		if (webSiteField == null) {
			webSiteField = new TrimmedTextField(30);
			webSiteField.setDocument(new LimitedStyledDocument(100));
		}

		return webSiteField;
	}

	private JLabel getOpenWebSiteIcon() {
		JLabel openSiteLabel = new JLabel(new ImageIcon(getClass().getResource("/images/WebComponent24.gif")));
		openSiteLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				openWebSite();
			}
		});
		return openSiteLabel;
	}

	private void openWebSite() {
		GUIUtilities.openWebSite(getWebSiteField().getText());
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author a) {
		author = a;

		if (author != null) {
			loadData();
		}
	}

	private void loadData() {
		getFirstNameField().setText(author.getFirstName());
		getLastNameField().setText(author.getLastName());
		getWebSiteField().setText(author.getWebSite());

		getAddedDateLabel().setText(TextFormatter.toText(author.getAddedDate()));

		validate();
		repaint();
	}

	protected void ok() {
		try {
			loadDataFromForm();

			String errors = getAuthor().validate();

			if (NullSafe.isEmpty(errors)) {
				databaseUpdate();
				GUIUtilities.showMessageDialog(author.getName() + " saved");
			}
			else {
				GUIUtilities.showMessageDialog(errors);
			}
		}
		catch (Exception e) {
			//LogManager.error(getClass(), e);
		}
	}

	private void databaseUpdate() {
		try {
			authorService.save(getAuthor(),false);
		}
		catch (Exception e) {
			//LogManager.error(getClass(), e);
			throw new RuntimeException(e);
		}
	}

	private void loadDataFromForm() {
		if (author == null) {
			author = new Author();
		}

		author.setLastName(getLastNameField().getText());
		author.setFirstName(getFirstNameField().getText());
		author.setWebSite(getWebSiteField().getText());

		setAuthor(author);
	}

	private static final long serialVersionUID = 8287206725962042277L;
}
