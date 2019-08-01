package net.scholnick.lbdb.gui.author;


import net.scholnick.lbdb.domain.Author;
import net.scholnick.lbdb.gui.BaseDialog;
import net.scholnick.lbdb.service.AuthorService;
import net.scholnick.lbdb.util.GUIUtilities;
import net.scholnick.lbdb.util.LabelFactory;
import net.scholnick.lbdb.util.LimitedStyledDocument;
import net.scholnick.lbdb.util.NullSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

@Component("authorQuickSearch")
@Scope("prototype")
public class AuthorQuickSearch extends BaseDialog {
	private final Logger log = LoggerFactory.getLogger(AuthorQuickSearch.class);

	private JTextField lastNameField;
	private JTextField firstNameField;
	private JCheckBox  exactNameOnlyCheckbox;
	private JButton    searchButton;

	private JTable resultsTable;

	@Autowired private AuthorService authorService;
	
	public AuthorQuickSearch() {
		setTitle("Author Search");
		setModal(true);
		setSize(300, 400);
		buildGUI();
		getOKButton().setName("Done");
	}
	
	public void initialize() {
		getLastNameField().setText("");
		getFirstNameField().setText("");
		((AuthorTableModel) getResultsTable().getModel()).clear();
		
		GUIUtilities.center(this);
		getOKButton().setEnabled(false);
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
		p.add(LabelFactory.createLabel("Exact Name Only:"), gbc);
		gbc.gridx++;
		p.add(getExactNameOnlyCheckbox(), gbc);

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
			firstNameField.addActionListener(searchAction);
		}
		return firstNameField;
	}

	private JTextField getLastNameField() {
		if (lastNameField == null) {
			lastNameField = new JTextField(10);
			lastNameField.setDocument(new LimitedStyledDocument(100));
			lastNameField.setToolTipText("Last name");
			lastNameField.addActionListener(searchAction);
		}
		return lastNameField;
	}

	private JCheckBox getExactNameOnlyCheckbox() {
		if (exactNameOnlyCheckbox == null) {
			exactNameOnlyCheckbox = new JCheckBox();
		}
		return exactNameOnlyCheckbox;
	}

	/** returns the search button */
	private JButton getSearchButton() {
		if (searchButton == null) {
			searchButton = new JButton("Search");
			searchButton.addActionListener(searchAction);
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
			resultsTable.setRowSelectionAllowed(true);
			resultsTable.getTableHeader().setReorderingAllowed(false);

			resultsTable.addMouseListener(new MouseAdapter() {
				@Override public void mouseClicked(MouseEvent event) {
					if (event.getClickCount() == 1) {
						getOKButton().setEnabled(true);
					}
					else if (event.getClickCount() == 2) {
						ok();
					}
				}
			});
		}
		return resultsTable;
	}

	private void search() {
		try {
			String lastName = getLastNameField().getText();
			String firstName = getFirstNameField().getText();

			if (NullSafe.isEmpty(lastName) && NullSafe.isEmpty(firstName)) {
				GUIUtilities.showMessageDialog("No search criteria specified.");
				return;
			}

			AuthorTableModel model = (AuthorTableModel) getResultsTable().getModel();

			getResultsTable().clearSelection();
			model.clear();

			Author searcher = new Author();
			searcher.setFirstName(getFirstNameField().getText());
			searcher.setLastName(getLastNameField().getText());
			List<Author> foundAuthors =  authorService.search(searcher);

			if (!foundAuthors.isEmpty()) {
				for (Author a : foundAuthors) {
					model.add(a);
				}
			}
			else {
				String message = "Author ( " + lastName + "," + firstName + " ) not found.  Add?";
				int choice = JOptionPane.showConfirmDialog(this, message, "Add Author?", JOptionPane.YES_NO_OPTION);

				if (choice == JOptionPane.YES_OPTION) {
					addAuthor(lastName, firstName);
				}
			}

			validate();
			repaint();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void addAuthor(String lastName, String firstName) {
		try {
			Author a = new Author();
			a.setLastName(lastName);
			a.setFirstName(firstName);
			authorService.save(a,true);

			//GUIUtilities.showMessageDialog("Author added");

			((AuthorTableModel) getResultsTable().getModel()).add(a);
			getResultsTable().setRowSelectionInterval(0, 0);
			ok();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Author getSelectedAuthor() {
		int row = getResultsTable().getSelectedRow();

		if (row == -1) {
			return null;
		}

		return ((AuthorTableModel) getResultsTable().getModel()).get(row);
	}

	private final ActionListener searchAction = new ActionListener() {
		public void actionPerformed(ActionEvent event) {
			search();
		}
	};

	private static final long serialVersionUID = -7523868692681805068L;
}
