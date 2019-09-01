package net.scholnick.lbdb.gui.author;


import net.scholnick.lbdb.domain.Author;
import net.scholnick.lbdb.gui.AbstractUpdateMaintenance;
import net.scholnick.lbdb.gui.TrimmedTextField;
import net.scholnick.lbdb.service.AuthorService;
import net.scholnick.lbdb.util.LabelFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

import static javax.swing.JOptionPane.YES_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import static net.scholnick.lbdb.util.GUIUtilities.showMessageDialog;

@Component
public class AuthorMaintenance extends AbstractUpdateMaintenance {
	private JTextField nameField;
	private JTextField webSiteField;
	private JButton    deleteButton;

	private Author author;

	private final AuthorService authorService;

	@Autowired
	public AuthorMaintenance(AuthorService authorService) {
		super();
		this.authorService = authorService;
		buildGUI();
	}

	protected void resetFocus() {
		getNameField().requestFocus();
	}

	@Override
	protected void clear() {
		getNameField().setText("");
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
		if (showConfirmDialog(this, "Delete Author?") == YES_OPTION) {
			authorService.delete(getAuthor());
			showMessageDialog(author + " deleted");
			clear();
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

		p.add(LabelFactory.createLabel("Name"), gbc);
		gbc.gridx++;
		p.add(getNameField(), gbc);

		gbc.gridy++;
		gbc.gridx = 0;
		p.add(LabelFactory.createLabel("Website"), gbc);
		gbc.gridx++;
		p.add(getWebSiteField(), gbc);

		gbc.gridy++;
		gbc.gridx = 0;
		p.add(LabelFactory.createLabel("Added Date"), gbc);
		gbc.gridx++;
		p.add(getAddedDateLabel(), gbc);

		return p;
	}

	private JTextField getNameField() {
		if (nameField == null) nameField = new TrimmedTextField(30,255);
		return nameField;
	}

	private JTextField getWebSiteField() {
		if (webSiteField == null) webSiteField = new TrimmedTextField(30,100);
		return webSiteField;
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
		getNameField().setText(author.getName());
		getWebSiteField().setText(author.getWebSite());
		getAddedDateLabel().setText(author.getAddedTimestamp());
		reload();
	}

	protected void ok() {
		author.setName(getNameField().getText());
		author.setWebSite(getWebSiteField().getText());
		authorService.save(author,false);
		sendMessage(author.getName() + " has been saved");
	}
}
