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
import java.awt.event.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

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
            infoLabel = LabelFactory.createLabel(" ");    // dont use "", swing will hide the component
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
        return p;
    }

    private JPanel getCriteriaPanel() {
        JPanel cp = new JPanel(new GridLayout(1, 2));

        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBorder(GUIUtilities.EMPTY_BORDER);

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
        cp.add(leftPanel);

        JPanel rightPanel = new JPanel(new GridBagLayout());
        Insets shiftedInsets = new Insets(0, 20, 0, 0);
        gbc = GUIUtilities.getDefaultGridBagConstraints();
        rightPanel.add(LabelFactory.createLabel("Author Name"), gbc);
        gbc.gridx++;
        gbc.insets = shiftedInsets;
        rightPanel.add(getAuthorNameField(), gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        rightPanel.add(LabelFactory.createLabel("Media"), gbc);
        gbc.gridx++;
        rightPanel.add(getMediaCombo(), gbc);

        JPanel extra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        extra.add(rightPanel);
        cp.add(extra);

        return cp;
    }

    private JComboBox<MediaType> getMediaCombo() {
        if (mediaCombo == null) {
            Vector<MediaType> types = new Vector<>(Media.values().length + 1);
            types.add(new MediaType("", -1));
            types.addAll(Arrays.stream(Media.values()).map(m -> new MediaType(m.toString(), m.getId())).collect(toList()));
            mediaCombo = new JComboBox<>(types);
        }
        return mediaCombo;
    }

    private JButton getSearchButton() {
        if (searchButton == null) {
            searchButton = new JButton("Search");
            searchButton.addActionListener(searchAction);
        }
        return searchButton;
    }

    private void search() {
        Book b = new Book();
        b.setTitle(getTitleField().getText());
        b.setSeries(getSeriesField().getText());
        b.setMedia(Media.from(Objects.requireNonNull((MediaType) getMediaCombo().getSelectedItem()).getId()));

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
            clearButton = new JButton("Clear");
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

        if (authors.size() == 1) {
            return authors.get(0);
        }

        AuthorSelectionPopUp selection = new AuthorSelectionPopUp(b.getAuthors());
        selection.setVisible(true);
        return selection.isApproved() ? selection.getSelectedAuthor() : null;
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
            title = new TrimmedTextField(20, 100);
            title.addActionListener(searchAction);
        }
        return title;
    }

    private JTextField getSeriesField() {
        if (series == null) {
            series = new TrimmedTextField(20, 100);
            series.addActionListener(searchAction);
        }
        return series;
    }

    @Override
    protected JPanel getInputPanel() {
        return null;
    }

    private static final class MediaType {
        private final String display;
        private final Integer id;

        MediaType(String display, Integer id) {
            this.display = display;
            this.id = id;
        }

        @Override
        public String toString() {
            return display;
        }

        public Integer getId() {
            return id;
        }
    }

    private final class SearchAction extends AbstractAction implements Runnable {
        private Thread searchThread;

        @Override
        public void actionPerformed(ActionEvent e) {
            if (searchThread != null) {
                return;
            }

            searchThread = new Thread(this);
            searchThread.start();
        }

        @Override
        public void run() {
            search();
            searchThread = null;
        }
    }
}
