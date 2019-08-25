package net.scholnick.lbdb.gui.author;

import net.scholnick.lbdb.domain.Author;
import net.scholnick.lbdb.gui.BaseDialog;
import net.scholnick.lbdb.gui.TrimmedTextField;
import net.scholnick.lbdb.service.AuthorService;
import net.scholnick.lbdb.util.LabelFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static javax.swing.JOptionPane.*;
import static net.scholnick.lbdb.util.GUIUtilities.center;

@Component
@Scope("prototype")
public class MultipleAuthorsDialog extends BaseDialog {
	private JTextField nameField;
	private JTable     resultsTable;
	private JTable     selectedTable;

	private final AuthorService authorService;

	private static final Dimension TABLE_DIMENSION = new Dimension(260,120);

	@Autowired
	public MultipleAuthorsDialog(AuthorService authorService) {
		super();
		this.authorService = authorService;

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
		getContentPane().add(getNameField(), BorderLayout.NORTH);
		getContentPane().add(getCenterPanels(), BorderLayout.CENTER);
		getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);
	}

	private JPanel getCenterPanels() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(5, 5, 0, 0);

		gbc.insets = new Insets(5, 100, 0, 0);
		panel.add(LabelFactory.createLabel("Results"), gbc);

		gbc.gridy++;
		gbc.insets = new Insets(5, 5, 0, 0);
		JScrollPane pane = new JScrollPane(getResultsTable());
		pane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		pane.setMinimumSize(TABLE_DIMENSION);
		panel.add(pane, gbc);

		gbc.gridy++;
		gbc.insets = new Insets(5, 100, 0, 0);
		panel.add(LabelFactory.createLabel("Selected"), gbc);

		gbc.gridy++;
		gbc.insets = new Insets(5, 5, 0, 0);
		JScrollPane selectedPane = new JScrollPane(getSelectedTable());
		selectedPane.setMinimumSize(TABLE_DIMENSION);
		selectedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		panel.add(selectedPane, gbc);

		return panel;
	}

	private JTextField getNameField() {
		if (nameField == null) {
			nameField = new TrimmedTextField(20,100);
			nameField.addActionListener(e -> search(false));
			nameField.addKeyListener(new KeyAdapter() {
				@Override public void keyReleased(KeyEvent e) {
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
			resultsTable.getTableHeader().setReorderingAllowed(false);
			resultsTable.getTableHeader().setUI(null);

			resultsTable.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent event) {
					if (event.getClickCount() == 2) {
                        moveChosenAuthorToSelectedTable();
					}
				}
			});
		}
		return resultsTable;
	}

	private void moveChosenAuthorToSelectedTable() {
        AuthorTableModel resultesModel = (AuthorTableModel) getResultsTable().getModel();

        ((AuthorTableModel) getSelectedTable().getModel()).add(resultesModel.get(getResultsTable().getSelectedRow()));

        clear();
        getNameField().requestFocus();
        repaintScreen();
    }


	private JTable getSelectedTable() {
		if (selectedTable == null) {
			selectedTable = new JTable(new AuthorTableModel());
			selectedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			selectedTable.setCellSelectionEnabled(false);
			selectedTable.setRowSelectionAllowed(false);
			selectedTable.getTableHeader().setReorderingAllowed(false);
			selectedTable.getTableHeader().setUI(null);
		}
		return selectedTable;
	}

	private void search(boolean existingOnly) {
		AuthorTableModel model = (AuthorTableModel) getResultsTable().getModel();
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

			int choice = showConfirmDialog(this, "Author ( " + fullName + " ) not found.  Add?", "Add Author?", YES_NO_OPTION);
			if (choice == YES_OPTION) {
                ((AuthorTableModel) getSelectedTable().getModel()).add(Author.of(fullName));
                clear();
                getNameField().requestFocus();
                repaintScreen();
			}
		}
	}

	public java.util.List<Author> getAuthors() {
		java.util.List<Author> data = ((AuthorTableModel) getSelectedTable().getModel()).getAllAuthors();
		return data.stream().distinct().sorted().collect(toList());
	}
}
