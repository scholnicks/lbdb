package net.scholnick.lbdb.gui;


import net.scholnick.lbdb.domain.*;
import net.scholnick.lbdb.gui.author.*;
import net.scholnick.lbdb.gui.title.*;
import net.scholnick.lbdb.service.BookService;
import net.scholnick.lbdb.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Objects;
import java.util.Vector;

@Component
public class SearchPanel extends BasePanel {
	private JButton searchButton;
	private JButton clearButton;
	private JButton editBookButton;
	private JButton editAuthorButton;

	private JTextField authorFirstName;
	private JTextField authorLastName;
	private JTextField title;
	private JTextField series;
	private JComboBox<MediaType> mediaCombo;
	
	private JLabel infoLabel;

	private JTable dataTable;

	private Thread searchThread;

	private final SearchAction searchAction;
	
	private final BookService bookService;

	@Autowired
	public SearchPanel(BookService bookService) {
		super();
		this.bookService = bookService;
		this.searchAction = new SearchAction();
		setLayout(new BorderLayout(1, 1));
		makePanels();
	}

	@Override
	public void paintComponent(Graphics g) {
		getTitleField().requestFocus();
		super.paintComponent(g);
	}

	/** Builds the GUI */
	private void makePanels() {
		JPanel middle = new JPanel(new BorderLayout());
		middle.add(getTopPanel(), BorderLayout.NORTH);
		middle.add(new JScrollPane(getDataTable()), BorderLayout.CENTER);

		add(middle, BorderLayout.CENTER);
		add(getBottomPanel(), BorderLayout.SOUTH);
	}

	/** Returns the bottom panel */
	private JPanel getBottomPanel() {
		JPanel p = new JPanel(new BorderLayout(1, 1));
		p.add(getButtonPanel(), BorderLayout.CENTER);
		p.add(getInfoLabel(), BorderLayout.SOUTH);
		return p;
	}

	/** Returns the button panel */
	protected JPanel getButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(getEditBookButton());
		buttonPanel.add(getEditAuthorButton());
		return buttonPanel;
	}

	/** Returns the laziy iniated info label */
	private JLabel getInfoLabel() {
		if (infoLabel == null) {
			infoLabel = LabelFactory.createLabel(" ");	// dont use "", swing will hide the component
			infoLabel.setVisible(true);
		}

		return infoLabel;
	}

	/** returns the top search panel */
	private JPanel getTopPanel() {
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(getCriteriaPanel(), BorderLayout.CENTER);
		topPanel.add(getSearchButtonPanel(), BorderLayout.SOUTH);
		return topPanel;
	}

	/** returns the search button panel */
	private JPanel getSearchButtonPanel() {
		JPanel p = new JPanel();
		p.add(getSearchButton());
		p.add(getClearButton());
		return p;
	}

	/** returns the input criteria panel */
	private JPanel getCriteriaPanel() {
		JPanel cp = new JPanel(new GridLayout(1,2));
		
		JPanel leftPanel = new JPanel(new GridBagLayout());
		leftPanel.setBorder( GUIUtilities.EMPTY_BORDER );
		
		GridBagConstraints gbc = GUIUtilities.getDefaultGridBagConstraints();

		leftPanel.add(LabelFactory.createLabel("Title"), gbc);
		gbc.gridx++;
		leftPanel.add(getTitleField(), gbc);
		cp.add(leftPanel);
		
		gbc.gridy++;
		gbc.gridx = 0;
		leftPanel.add(LabelFactory.createLabel("Series"), gbc);
		gbc.gridx++;
		leftPanel.add(getSeriesField(), gbc);

		gbc.gridy++;
		gbc.gridx = 0;
		leftPanel.add(LabelFactory.createLabel("Media"), gbc);
		gbc.gridx++;
		leftPanel.add(getMediaCombo(), gbc);
		cp.add(leftPanel);

		
		JPanel rightPanel = new JPanel(new GridBagLayout());
		Insets shiftedInsets = new Insets(0,20,0,0);
		gbc = GUIUtilities.getDefaultGridBagConstraints();
		rightPanel.add(LabelFactory.createLabel("Author Last Name"), gbc);
		gbc.gridx++;
		gbc.insets = shiftedInsets;
		rightPanel.add(getAuthorLastNameField(), gbc);

		gbc.gridy++;
		gbc.gridx = 0;
		gbc.insets = GUIUtilities.EMPTY_INSETS;
		rightPanel.add(LabelFactory.createLabel("Author First Name"), gbc);
		gbc.gridx++;
		gbc.insets = shiftedInsets;
		rightPanel.add(getAuthorFirstNameField(), gbc);
		
		JPanel extra = new JPanel(new FlowLayout(FlowLayout.LEFT));
		extra.add(rightPanel);
		cp.add(extra);
		
		return cp;
	}

	private JComboBox<MediaType> getMediaCombo() {
		if (mediaCombo == null) {
			Vector<MediaType> types = new Vector<>(Media.values().length);
			types.add( new MediaType("",-1) );
			for (Media m : Media.values()) {
				types.add( new MediaType(m.toString(),m.getId()) );
			}
			
			mediaCombo = new JComboBox<>(types);
		}
		return mediaCombo;
	}
	
	private static class MediaType {
		private final String display;
		private final Integer id;
		
		MediaType(String display, Integer id) { this.display = display; this.id = id; }
		@Override public String toString() { return display; }
		public Integer getId() { return id; }
	}

	/** returns the search button */
	private JButton getSearchButton() {
		if (searchButton == null) {
			searchButton = new JButton("Search");
			searchButton.addActionListener(searchAction);
		}
		return searchButton;
	}

	/** search method */
	public void search() {
		try {
			Book b = new Book();
			b.setTitle( getTitleField().getText() );
			b.setSeries( getSeriesField().getText() );
			b.setMedia( Media.from( Objects.requireNonNull((MediaType) getMediaCombo().getSelectedItem()).getId()) );
			
			String firstName = getAuthorFirstNameField().getText();
			String lastName = getAuthorLastNameField().getText();
			
			if (! NullSafe.isEmpty(firstName) || ! NullSafe.isEmpty(lastName)) {
				Author a = new Author();
				a.setFirstName(firstName);
				a.setLastName(lastName);
				b.setAuthors( Collections.singletonList(a) );
			}
			
			final java.util.List<Book> books = bookService.search(b);

			new SwingWorker<Object,Boolean>() {
				@Override protected Boolean doInBackground() {
					fillTable(books);
					return Boolean.TRUE;
				}
			}.execute();
			
			SoundPlayer.playAhh();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private JButton getClearButton() {
		if (clearButton == null) {
			clearButton = new JButton("Clear");
			clearButton.addActionListener(l -> clear());
		}
		return clearButton;
	}

	private void clear() {
		getTitleField().setText("");
		getAuthorFirstNameField().setText("");
		getAuthorLastNameField().setText("");
		getSeriesField().setText("");
		getMediaCombo().setSelectedIndex(0);
		getInfoLabel().setText(" ");

		clearTableData();

		getTitleField().requestFocus();
		repaint();
	}

	private void clearTableData() {
		getDataTable().clearSelection();
		((TitleSearchTableModel) getDataTable().getModel()).clear();
	}

	private void fillTable(java.util.List<Book> data) {
		clearTableData();

		TitleSearchTableModel model = (TitleSearchTableModel) getDataTable().getModel();

		try {
			for (Book b : data) {
				model.addRow(b);
			}

			getInfoLabel().setText(data.size() + " book" + (data.size() != 1 ? "s" : "")  + " found");
		}
		finally {
			validate();
			repaint();
		}
	}

	private JTextField getAuthorFirstNameField() {
		if (authorFirstName == null) {
			authorFirstName = new TrimmedTextField(20);
			authorFirstName.setDocument(new LimitedStyledDocument(100));
			authorFirstName.setToolTipText("First name");
			authorFirstName.addActionListener(searchAction);
		}
		return authorFirstName;
	}

	private JTextField getAuthorLastNameField() {
		if (authorLastName == null) {
			authorLastName = new TrimmedTextField(20);
			authorLastName.setDocument(new LimitedStyledDocument(100));
			authorLastName.setToolTipText("Last name");
			authorLastName.addActionListener(searchAction);
		}
		return authorLastName;
	}

	private JTable getDataTable() {
		if (dataTable == null) {
			dataTable = new TitleSearchTable();

			dataTable.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent event) {
					if (getEditBookButton().isEnabled() && event.getClickCount() == 2)
						editTitle();
				}
			});
		}
		return dataTable;
	}

	private void editTitle() {
		int row = getDataTable().getSelectedRow();

		if (row < 0) {
			GUIUtilities.showMessageDialog("Must select a row");
			return;
		}

		TitleSearchTableModel model = (TitleSearchTableModel) getDataTable().getModel();
		Book data = bookService.get(model.getTitleData(row).getId());

		fireTitleSelection(new TitleSelectionEvent(data));
	}

	private void editAuthor() {
		int row = getDataTable().getSelectedRow();

		if (row < 0) {
			GUIUtilities.showMessageDialog("Must select a row");
			return;
		}

		TitleSearchTableModel model = (TitleSearchTableModel) getDataTable().getModel();
		Book data = model.getTitleData(row);

		Author a = getSelectedAuthor(data);

		if (a != null) {
			fireAuthorSelection(new AuthorSelectionEvent(a));
		}
	}

	private Author getSelectedAuthor(Book b) {
		java.util.List<Author> authors = b.getAuthors();

		if (authors.size() == 1) {
			return authors.get(0);
		}

		AuthorSelectionPopUp selection = new AuthorSelectionPopUp(b.getAuthors());
		selection.setVisible(true);

		if (selection.isApproved()) {
			return selection.getSelectedAuthor();
		}
		else {
			return null;
		}
	}

	private JButton getEditBookButton() {
		if (editBookButton == null) {
			editBookButton = new JButton("Edit Book");
			editBookButton.addActionListener(l -> editTitle());
		}
		return editBookButton;
	}

	private JButton getEditAuthorButton() {
		if (editAuthorButton == null) {
			editAuthorButton = new JButton("Edit Author");
			editAuthorButton.addActionListener(l -> editAuthor());
		}
		return editAuthorButton;
	}

	private JTextField getTitleField() {
		if (title == null) {
			title = new JTextField(20);
			title.setDocument(new LimitedStyledDocument(100));
			title.addActionListener(searchAction);
		}
		return title;
	}

	private JTextField getSeriesField() {
		if (series == null) {
			series = new JTextField(20);
			series.setDocument(new LimitedStyledDocument(100));
			series.addActionListener(searchAction);
		}
		return series;
	}

	@Override
	protected JPanel getInputPanel() {
		return null;
	}

	private static final long serialVersionUID = 6835258910942672966L;

    /** SearchAction is initiated when the user clicks the Search button */
    private class SearchAction extends AbstractAction implements Runnable {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEmpty()) {
                GUIUtilities.showMessageDialog("No search criteria specified.");
                return;
            }

            if (searchThread != null) {
                return;
            }

            searchThread = new Thread(this);
            searchThread.start();
        }

        private boolean isEmpty() {
            return NullSafe.isEmpty(getTitleField().getText()) &&
                    NullSafe.isEmpty(getAuthorFirstNameField().getText()) &&
                    NullSafe.isEmpty(getAuthorLastNameField().getText()) &&
                    NullSafe.isEmpty(getSeriesField().getText()) &&
                    getMediaCombo().getSelectedIndex() == 0
                    ;
        }

        @Override
        public void run() {
            search();
            searchThread = null;
        }

        private static final long serialVersionUID = 7505766496341980946L;
    }
}
