package net.scholnick.lbdb.gui.author;

import net.scholnick.lbdb.domain.Author;
import net.scholnick.lbdb.gui.BaseDialog;
import net.scholnick.lbdb.gui.TrimmedTextField;
import net.scholnick.lbdb.service.AuthorService;
import net.scholnick.lbdb.util.GUIUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import static javax.swing.JOptionPane.showConfirmDialog;

@Component
@Scope("prototype")
public class AuthorQuickSearch extends BaseDialog {
	private JTextField nameField;
	private JTable resultsTable;
	private AuthorService authorService;
	
	public AuthorQuickSearch() {
		setTitle("Author Search");
		setModal(true);
		setSize(300, 400);
		buildGUI();
		getOKButton().setName("Done");
	}
	
	public void initialize() {
		getNameField().setText("");
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
		gbc.weighty = 1.00;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(5, 5, 0, 0);

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
		AuthorTableModel model = (AuthorTableModel) getResultsTable().getModel();

		getResultsTable().clearSelection();
		model.clear();

		List<Author> foundAuthors = authorService.search(getNameField().getText());

		if (! foundAuthors.isEmpty()) {
			for (Author a : foundAuthors) {
				model.add(a);
			}
		}
		else {
			String fullName = getNameField().getText();

			String message = "Author ( " + fullName + " ) not found.  Add?";
			int choice = showConfirmDialog(this, message, "Add Author?", JOptionPane.YES_NO_OPTION);
			if (choice == JOptionPane.YES_OPTION) {
				model.add(Author.parse(fullName));
			}
		}

		validate();
		repaint();
	}

//	private void addAuthor(String lastName, String firstName) {
//		Author a = new Author();
//		a.setLastName(lastName);
//		a.setFirstName(firstName);
//		authorService.save(a,true);
//
//		((AuthorTableModel) getResultsTable().getModel()).add(a);
//		getResultsTable().setRowSelectionInterval(0, 0);
//		ok();
//	}

	public Author getSelectedAuthor() {
		int row = getResultsTable().getSelectedRow();
		if (row == -1) return null;
		return ((AuthorTableModel) getResultsTable().getModel()).get(row);
	}

	@Autowired
	public void setAuthorService(AuthorService authorService) {
		this.authorService = authorService;
	}
}
