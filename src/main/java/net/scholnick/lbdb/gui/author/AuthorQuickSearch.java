package net.scholnick.lbdb.gui.author;

import net.scholnick.lbdb.domain.Author;
import net.scholnick.lbdb.gui.BaseDialog;
import net.scholnick.lbdb.gui.TrimmedTextField;
import net.scholnick.lbdb.service.AuthorService;
import net.scholnick.lbdb.util.GUIUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import static javax.swing.JOptionPane.showConfirmDialog;

@Component
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
		getContentPane().add(getNameField(), BorderLayout.NORTH);

		JScrollPane pane = new JScrollPane(getResultsTable());
		pane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		getContentPane().add(pane, BorderLayout.CENTER);
		getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);
	}

	private JTextField getNameField() {
		if (nameField == null) {
			nameField = new TrimmedTextField(20,100);
			nameField.addActionListener(e -> search(false));
			nameField.addKeyListener(new KeyAdapter() {
				@Override public void keyPressed(KeyEvent e) {
					if (nameField.getText() != null && nameField.getText().length() > 3) {
						search(true);
					}
				}
			});
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
			resultsTable.getTableHeader().setUI(null);
			GUIUtilities.setCellsAlignment(resultsTable,SwingConstants.CENTER);

			resultsTable.addMouseListener(new MouseAdapter() {
				@Override public void mouseClicked(MouseEvent event) {
					if (event.getClickCount() == 1) getOKButton().setEnabled(true);
					else if (event.getClickCount() == 2) ok();
				}
			});
		}
		return resultsTable;
	}

	private void search(boolean existingOnly) {
		AuthorTableModel model = (AuthorTableModel) getResultsTable().getModel();

		getResultsTable().clearSelection();
		model.clear();

		List<Author> foundAuthors = authorService.search(getNameField().getText());

		if (! foundAuthors.isEmpty()) {
			foundAuthors.forEach(model::add);
			repaintScreen();
		}
		else {
			if (existingOnly) {
				repaintScreen();
				return;
			}

			String fullName = getNameField().getText();

			String message = "Author ( " + fullName + " ) not found.  Add?";
			int choice = showConfirmDialog(this, message, "Add Author?", JOptionPane.YES_NO_OPTION);
			if (choice == JOptionPane.YES_OPTION) {
				model.add(Author.of(fullName));
				getResultsTable().setRowSelectionInterval(0,0);
				ok();
			}
		}
	}

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
