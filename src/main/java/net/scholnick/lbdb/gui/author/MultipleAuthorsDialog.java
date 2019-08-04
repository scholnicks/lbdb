package net.scholnick.lbdb.gui.author;

import net.scholnick.lbdb.domain.Author;
import net.scholnick.lbdb.gui.BaseDialog;
import net.scholnick.lbdb.service.AuthorService;
import net.scholnick.lbdb.util.LabelFactory;
import net.scholnick.lbdb.util.LimitedStyledDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import static net.scholnick.lbdb.util.GUIUtilities.center;
import static net.scholnick.lbdb.util.GUIUtilities.showMessageDialog;
import static net.scholnick.lbdb.util.NullSafe.isEmpty;

@Component("multipleAuthorsDialog")
@Scope("prototype")
public class MultipleAuthorsDialog extends BaseDialog {
	private JTextField lastNameField;
	private JTextField firstNameField;
	private JButton searchButton;
	private JTable resultsTable;
	private AuthorService authorService;
	
	public MultipleAuthorsDialog() {
		super();
		setTitle("Add Multiple Authors");
		setModal(true);
		setSize(300, 400);
		center(this);
		buildGUI();

		getOKButton().setName("Done");
	}

	@Override
	public void setVisible(boolean b) {
		if (b) {
			clear();
		}
		super.setVisible(b);
	}
	
	private void clear() {
		getLastNameField().setText("");
		getFirstNameField().setText("");
		((AuthorTableModel) getResultsTable().getModel()).clear();
	}
	
	@Override
	protected void buildGUI() {
		getContentPane().setLayout(new BorderLayout());

		getContentPane().add(getInputPanel(), BorderLayout.NORTH);

		JScrollPane pane = new JScrollPane(getResultsTable());
		pane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		getContentPane().add(pane, BorderLayout.CENTER);
		getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);
	}

    /** {@inheritDoc} */
	@Override
	protected JPanel getInputPanel() {
		JPanel p = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = gbc.gridy = 0;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.weightx = 1.00;
		gbc.weighty = .50;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(5, 5, 0, 0);

		p.add(LabelFactory.createLabel("Last Name:"), gbc);
		gbc.gridx++;
		p.add(getLastNameField(), gbc);

		gbc.gridy++;
		gbc.gridx = 0;
		p.add(LabelFactory.createLabel("First Name:"), gbc);
		gbc.gridx++;
		p.add(getFirstNameField(), gbc);

		gbc.gridy++;
		gbc.gridx = 0;
		gbc.insets = new Insets(5, 0, 0, 0);
		p.add(getSearchButton(), gbc);

		return p;
	}

	private JTextField getFirstNameField() {
		if (firstNameField == null) {
			firstNameField = new JTextField(10);
			firstNameField.setDocument(new LimitedStyledDocument(100));
			firstNameField.setToolTipText("First name");
			firstNameField.addActionListener(l -> search());
		}
		return firstNameField;
	}

	private JTextField getLastNameField() {
		if (lastNameField == null) {
			lastNameField = new JTextField(10);
			lastNameField.setDocument(new LimitedStyledDocument(100));
			lastNameField.setToolTipText("Last name");
			lastNameField.addActionListener(l -> search());
		}
		return lastNameField;
	}

	/** returns the search button */
	private JButton getSearchButton() {
		if (searchButton == null) {
			searchButton = new JButton("Search");
			searchButton.addActionListener(l -> search());
		}
		return searchButton;
	}

	@Override
	protected JComponent getInitialFocusComponent() {
		return getLastNameField();
	}

	private JTable getResultsTable() {
		if (resultsTable == null) {
			resultsTable = new JTable(new AuthorTableModel());
			resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			resultsTable.setCellSelectionEnabled(false);
			resultsTable.setRowSelectionAllowed(false);
			resultsTable.getTableHeader().setReorderingAllowed(false);
		}
		return resultsTable;
	}

	private void search() {
		try {
			String lastName = getLastNameField().getText();
			String firstName = getFirstNameField().getText();

			if (isEmpty(lastName) && isEmpty(firstName)) {
				showMessageDialog("No search criteria specified.");
				return;
			}

			AuthorTableModel model = (AuthorTableModel) getResultsTable().getModel();

			java.util.List<Author> foundAuthors = authorService.search( new Author(lastName,firstName) );

			if (foundAuthors.isEmpty()) {
				String message = "Author ( " + lastName + "," + firstName + " ) not found.  Add?";
				int choice = showConfirmDialog(this, message, "Add Author?", YES_NO_OPTION);

				if (choice == JOptionPane.OK_OPTION) {
					addAuthor(lastName, firstName);
				}
			}
			else {
				if (foundAuthors.size() > 1) {
					showMessageDialog("Multiple Matches for search criteria. Narrow your search");
				}
				else {
					Author a = foundAuthors.get(0);
					if (model.getAllAuthors().contains(a)) {
						showMessageDialog(a.getName() + " has already been added. Skipping.");
					}
					else {
						model.add(foundAuthors.get(0));
					}
				}
			}

			getFirstNameField().setText("");
			getLastNameField().setText("");

			validate();
			repaint();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void addAuthor(String lastName, String firstName) {
		Author a = authorService.save(new Author(lastName,firstName),false);
		((AuthorTableModel) getResultsTable().getModel()).add(a);
	}

	public java.util.List<Author> getAuthors() {
		return ((AuthorTableModel) getResultsTable().getModel()).getAllAuthors();
	}

	@Autowired
	public void setAuthorService(AuthorService authorService) {
		this.authorService = authorService;
	}
}
