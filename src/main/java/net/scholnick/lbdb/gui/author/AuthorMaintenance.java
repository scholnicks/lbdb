package net.scholnick.lbdb.gui.author;


import net.scholnick.lbdb.domain.Author;
import net.scholnick.lbdb.gui.AbstractUpdateMaintenance;
import net.scholnick.lbdb.gui.TrimmedTextField;
import net.scholnick.lbdb.service.AuthorService;
import net.scholnick.lbdb.util.GUIUtilities;
import net.scholnick.lbdb.util.LabelFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
		if (JOptionPane.showConfirmDialog(this, "Delete Author?") == JOptionPane.YES_OPTION) {
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

		p.add(LabelFactory.createLabel("First Name"), gbc);
		gbc.gridx++;
		p.add(getNameField(), gbc);

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

	private JTextField getNameField() {
		if (nameField == null) nameField = new TrimmedTextField(10,100);
		return nameField;
	}

	private JTextField getWebSiteField() {
		if (webSiteField == null) webSiteField = new TrimmedTextField(30,100);
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
		getNameField().setText(author.getName());
		getWebSiteField().setText(author.getWebSite());
		getAddedDateLabel().setText(author.getAddedTimestamp());
		validate();
		repaint();
	}

	protected void ok() {
		authorService.save(getAuthor(),false);
	}

//	private void loadDataFromForm() {
//		if (author == null) {
//			author = new Author();
//		}
//
//		author.setName(getNameField().getText());
//		author.setWebSite(getWebSiteField().getText());
//
//		setAuthor(author);
//	}
}
