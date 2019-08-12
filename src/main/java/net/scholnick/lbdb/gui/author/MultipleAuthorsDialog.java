package net.scholnick.lbdb.gui.author;

import net.scholnick.lbdb.domain.Author;
import net.scholnick.lbdb.gui.BaseDialog;
import net.scholnick.lbdb.gui.TrimmedTextField;
import net.scholnick.lbdb.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

import static javax.swing.JOptionPane.showConfirmDialog;
import static net.scholnick.lbdb.util.GUIUtilities.center;
import static net.scholnick.lbdb.util.GUIUtilities.showMessageDialog;

@Component
@Scope("prototype")
public class MultipleAuthorsDialog extends BaseDialog {
	private JTextField nameField;
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
		if (b) clear();
		super.setVisible(b);
	}
	
	private void clear() {
		getNameField().setText("");
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

	@Override
	protected JPanel getInputPanel() {
		JPanel p = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = gbc.gridy = 0;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.weightx = 1.00;
		gbc.weighty = 1.00;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(5, 5, 0, 0);

		gbc.gridy++;
		p.add(getNameField(), gbc);

		return p;
	}

	private JTextField getNameField() {
		if (nameField == null) {
			nameField = new TrimmedTextField(20,100);
			nameField.addActionListener(e -> search());
		}
		return nameField;
	}

	@Override
	protected JComponent getInitialFocusComponent() {
		return getNameField();
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
		AuthorTableModel model = (AuthorTableModel) getResultsTable().getModel();

		java.util.List<Author> foundAuthors = authorService.search(getNameField().getText());

		if (foundAuthors.isEmpty()) {
			String fullName = getNameField().getText();

			String message = "Author ( " + fullName + " ) not found.  Add?";
			int choice = showConfirmDialog(this, message, "Add Author?", JOptionPane.YES_NO_OPTION);
			if (choice == JOptionPane.YES_OPTION) {
				model.add(Author.of(fullName));
				getNameField().setText("");
				getNameField().requestFocus();
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

				getNameField().setText("");
				getNameField().requestFocus();
			}
		}

		repaintScreen();
	}

	public java.util.List<Author> getAuthors() {
		return ((AuthorTableModel) getResultsTable().getModel()).getAllAuthors();
	}

	@Autowired
	public void setAuthorService(AuthorService authorService) {
		this.authorService = authorService;
	}
}
