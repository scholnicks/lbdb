package net.scholnick.lbdb.gui;

import net.scholnick.lbdb.domain.*;
import net.scholnick.lbdb.gui.author.*;
import net.scholnick.lbdb.gui.title.*;
import net.scholnick.lbdb.service.BookService;
import net.scholnick.lbdb.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

@Component
public class SearchPanel extends BasePanel {
    private JButton searchButton;
    private JButton clearButton;
    private JButton editBookButton;
    private JButton editAuthorButton;

    private JTextField authorName;
    private JTextField title;
    private JTextField series;
    private JComboBox<MediaType> mediaCombo;

    private JLabel infoLabel;

    private JTable dataTable;

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

    private void makePanels() {
        JPanel middle = new JPanel(new BorderLayout());
        middle.add(getTopPanel(), BorderLayout.NORTH);
        middle.add(new JScrollPane(getDataTable()), BorderLayout.CENTER);

        add(middle, BorderLayout.CENTER);
        add(getBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel getBottomPanel() {
        JPanel p = new JPanel(new BorderLayout(1, 1));
        p.add(getButtonPanel(), BorderLayout.CENTER);
        p.add(getInfoLabel(), BorderLayout.SOUTH);
        return p;
    }

    protected JPanel getButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(getEditBookButton());
        buttonPanel.add(getEditAuthorButton());
        return buttonPanel;
    }

    private JLabel getInfoLabel() {
        if (infoLabel == null) {
            infoLabel = LabelFactory.createLabel(" ");    // don't use "", swing will hide the component
            infoLabel.setVisible(true);
        }
        return infoLabel;
    }

    private JPanel getTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(getCriteriaPanel(), BorderLayout.CENTER);
        topPanel.add(getSearchButtonPanel(), BorderLayout.SOUTH);
        return topPanel;
    }

    private JPanel getSearchButtonPanel() {
        JPanel p = new JPanel();
        p.add(getSearchButton());
        p.add(getClearButton());
        p.setBorder(BorderFactory.createEmptyBorder(10,10,5,10));
        return p;
    }

    private JPanel getCriteriaPanel() {
        JPanel cp = new JPanel(new GridBagLayout());
        cp.setBorder(BorderFactory.createEmptyBorder(10,10,5,10));

        GridBagConstraints gbc = GUIUtilities.getDefaultGridBagConstraints();
        gbc.weighty = .25;

        cp.add(LabelFactory.createLabel("Title"), gbc);
        gbc.gridx++;
        cp.add(getTitleField(), gbc);

        gbc.gridx++;
        cp.add(LabelFactory.createLabel("Author Name"), gbc);

        gbc.gridx++;
        cp.add(getAuthorNameField(), gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        cp.add(LabelFactory.createLabel("Series"), gbc);
        gbc.gridx++;
        cp.add(getSeriesField(), gbc);

        gbc.gridx++;
        cp.add(LabelFactory.createLabel("Media"), gbc);

        gbc.gridx++;
        gbc.insets = new Insets(0,-7,0,0); // shift combo back to the left. no idea why it does not line up.
        cp.add(getMediaCombo(), gbc);

        return cp;
    }

    private JComboBox<MediaType> getMediaCombo() {
        if (mediaCombo == null) {
            Vector<MediaType> types = new Vector<>(Media.values().length + 1);
            types.add(new MediaType("", -1));
            types.addAll(Arrays.stream(Media.values()).map(m -> new MediaType(m.toString(), m.getId())).toList());
            mediaCombo = new JComboBox<>(types);
            mediaCombo.setBorder(BorderFactory.createEmptyBorder());
            mediaCombo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        return mediaCombo;
    }

    private JButton getSearchButton() {
        if (searchButton == null) {
            searchButton = GUIUtilities.createButton("Search");
            searchButton.addActionListener(searchAction);
        }
        return searchButton;
    }

    private void search() {
        Book b = new Book();
        b.setTitle(getTitleField().getText());
        b.setSeries(getSeriesField().getText());
        b.setMedia(Media.from(Objects.requireNonNull((MediaType) getMediaCombo().getSelectedItem()).id()));

        if (!NullSafe.isEmpty(getAuthorNameField().getText())) {
            b.setAuthors(java.util.List.of(Author.of(getAuthorNameField().getText())));
        }

        new SwingWorker<Object, Boolean>() {
            @Override
            protected Boolean doInBackground() {
                fillTable(bookService.search(b));
                return Boolean.TRUE;
            }
        }.execute();

        SoundPlayer.playAhh();
    }

    private JButton getClearButton() {
        if (clearButton == null) {
            clearButton = GUIUtilities.createButton("Clear");
            clearButton.addActionListener(l -> clear());
        }
        return clearButton;
    }

    private void clear() {
        getTitleField().setText("");
        getAuthorNameField().setText("");
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
        getInfoLabel().setText("");
    }

    private void fillTable(java.util.List<Book> data) {
        clearTableData();

        TitleSearchTableModel model = (TitleSearchTableModel) getDataTable().getModel();

        data.forEach(model::addRow);
        getInfoLabel().setText(data.size() + " book" + (data.size() != 1 ? "s" : "") + " found");
        validate();
        repaint();
    }

    private JTextField getAuthorNameField() {
        if (authorName == null) {
            authorName = new TrimmedTextField(20, 100);
            authorName.setBorder(BorderFactory.createLineBorder(Color.white));
            authorName.addActionListener(searchAction);
        }
        return authorName;
    }

    private JTable getDataTable() {
        if (dataTable == null) {
            dataTable = new TitleSearchTable();

            dataTable.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent event) {
                    if (getEditBookButton().isEnabled() && event.getClickCount() == 2) {
                        editTitle();
                    }
                }
            });
        }
        return dataTable;
    }

    private void editTitle() {
        int row = getDataTable().getSelectedRow();

        TitleSearchTableModel model = (TitleSearchTableModel) getDataTable().getModel();
        Book data = bookService.get(model.getTitleData(row).getId());

        getInfoLabel().setText("");
        fireTitleSelection(new TitleSelectionEvent(data));
    }

    private void editAuthor() {
        int row = getDataTable().getSelectedRow();

        TitleSearchTableModel model = (TitleSearchTableModel) getDataTable().getModel();
        Book data = model.getTitleData(row);

        Author a = getSelectedAuthor(data);

        if (a != null) {
            getInfoLabel().setText("");
            fireAuthorSelection(new AuthorSelectionEvent(a));
        }
    }

    private Author getSelectedAuthor(Book b) {
        java.util.List<Author> authors = b.getAuthors();

        if (authors.size() == 1) return authors.getFirst();

        AuthorSelectionPopUp selection = new AuthorSelectionPopUp(b.getAuthors());
        selection.setVisible(true);
        return selection.isApproved() ? selection.getSelectedAuthor() : null;
    }

    private JButton getEditBookButton() {
        if (editBookButton == null) {
            editBookButton = GUIUtilities.createButton("Edit Book");
            editBookButton.addActionListener(l -> editTitle());
        }
        return editBookButton;
    }

    private JButton getEditAuthorButton() {
        if (editAuthorButton == null) {
            editAuthorButton = GUIUtilities.createButton("Edit Author");
            editAuthorButton.addActionListener(l -> editAuthor());
        }
        return editAuthorButton;
    }

    private JTextField getTitleField() {
        if (title == null) {
            title = new TrimmedTextField(20, 100);
            title.setBorder(BorderFactory.createLineBorder(Color.white));
            title.addActionListener(searchAction);
        }
        return title;
    }

    private JTextField getSeriesField() {
        if (series == null) {
            series = new TrimmedTextField(20, 100);
            series.setBorder(BorderFactory.createLineBorder(Color.white));
            series.addActionListener(searchAction);
        }
        return series;
    }

    @Override
    protected JPanel getInputPanel() {
        return null;
    }

    private record MediaType(String display, Integer id) {
        @Override public String toString() {
                return display;
            }
    }

    private final class SearchAction extends AbstractAction implements Runnable {
        private Thread searchThread;

        @Override public void actionPerformed(ActionEvent e) {
            if (searchThread != null) {
                return;
            }

            searchThread = new Thread(this);
            searchThread.start();
        }

        @Override public void run() {
            search();
            searchThread = null;
        }
    }

    public static final class SearchTableCellRenderer extends DefaultTableCellRenderer {
        private static final SearchTableCellRenderer INSTANCE = new SearchTableCellRenderer();

        private static final TableCellRenderer FIRST_COLUMN_RENDERER = new DefaultTableCellRenderer();

        public static SearchTableCellRenderer getInstance() {
            return INSTANCE;
        }

        private SearchTableCellRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (column == 0) return FIRST_COLUMN_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
}
