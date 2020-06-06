package net.scholnick.lbdb.gui.title;

import net.scholnick.lbdb.coverphoto.GoogleService;
import net.scholnick.lbdb.domain.Author;
import net.scholnick.lbdb.domain.Book;
import net.scholnick.lbdb.domain.BookType;
import net.scholnick.lbdb.domain.Media;
import net.scholnick.lbdb.gui.AbstractUpdateMaintenance;
import net.scholnick.lbdb.gui.TrimmedTextField;
import net.scholnick.lbdb.gui.author.AuthorQuickSearch;
import net.scholnick.lbdb.gui.author.MultipleAuthorsDialog;
import net.scholnick.lbdb.service.BookService;
import net.scholnick.lbdb.util.FileUtils;
import net.scholnick.lbdb.util.GUIUtilities;
import net.scholnick.lbdb.util.LabelFactory;
import net.scholnick.lbdb.util.NullSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static javax.swing.BorderFactory.*;
import static net.scholnick.lbdb.util.GUIUtilities.showMessageDialog;

@Component
public class TitleMaintenance extends AbstractUpdateMaintenance {
    private static final Logger log = LoggerFactory.getLogger(TitleMaintenance.class);

    private JTextField titleField;
    private JTextField seriesField;
    private JTextField publishedYearField;
    private JTextField isbnField;
    private JTextField numberOfPagesField;
    private JCheckBox anthologyCheckBox;
    private JTextArea commentsArea;
    private JLabel imageLabel;
    private JButton deleteButton;
    private JTable authorsList;

    private JComboBox<BookType> typeCombo;
    private JComboBox<Media> mediaCombo;

    private Book book;

    private BookService bookService;
    private GoogleService googleService;
    private AuthorQuickSearch authorQuickSearch;
    private MultipleAuthorsDialog multipleAuthorDialog;

    private static final Dimension AUTHOR_PANEL_SIZE = new Dimension(542, 100);

    public TitleMaintenance() {
        super();
        buildGUI();
    }

    @Override
    protected void buildGUI() {
        setLayout(new BorderLayout());
        add(getImagePanel(), BorderLayout.WEST);
        add(getInputPanel(), BorderLayout.CENTER);
        add(getButtonPanel(), BorderLayout.SOUTH);
    }

    @Override
    protected JPanel getButtonPanel() {
        JPanel p = new JPanel();
        p.add(getSaveButton());
        p.add(getDeleteButton());
        p.add(getClearButton());
        return p;
    }

    private JButton getDeleteButton() {
        if (deleteButton == null) {
            deleteButton = new JButton("Delete");
            deleteButton.addActionListener(e -> removeTitle());
        }
        return deleteButton;
    }

    private void removeTitle() {
        if (JOptionPane.showConfirmDialog(this, "Delete Book?") == JOptionPane.YES_OPTION) {
            bookService.delete(book);
            sendMessage(book + " deleted");
            clear();
        }
    }

    protected JPanel getInputPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weighty = 1.00;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;

        Insets indentInsets = new Insets(0, 0, 0, 0);

        double labelWeight = .10;
        double inputWeight = .90;

        gbc.weightx = labelWeight;
        p.add(LabelFactory.createLabel("Title"), gbc);
        gbc.weightx = inputWeight;
        gbc.gridx++;
        gbc.insets = indentInsets;
        p.add(getTitleField(), gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = labelWeight;
        p.add(LabelFactory.createLabel("Series"), gbc);
        gbc.gridx++;
        gbc.insets = indentInsets;
        gbc.weightx = inputWeight;
        p.add(getSeriesField(), gbc);

        gbc.gridy++;
        gbc.weightx = labelWeight;
        gbc.gridx = 0;
        p.add(LabelFactory.createLabel("Authors"), gbc);
        gbc.gridx++;
        gbc.weightx = inputWeight;
        gbc.insets = new Insets(0, 5, 0, 0);
        p.add(getAuthorPanel(), gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = labelWeight;
        p.add(LabelFactory.createLabel("Type"), gbc);
        gbc.gridx++;
        gbc.insets = indentInsets;
        gbc.weightx = inputWeight;
        p.add(getTypeCombo(), gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = labelWeight;
        p.add(LabelFactory.createLabel("Media"), gbc);
        gbc.gridx++;
        gbc.insets = indentInsets;
        gbc.weightx = inputWeight;
        p.add(getMediaCombo(), gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = labelWeight;
        p.add(LabelFactory.createLabel("Year Published"), gbc);
        gbc.gridx++;
        gbc.insets = indentInsets;
        gbc.weightx = inputWeight;
        p.add(getPublishedYearField(), gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = labelWeight;
        p.add(LabelFactory.createLabel("ISBN"), gbc);
        gbc.gridx++;
        gbc.insets = indentInsets;
        gbc.weightx = inputWeight;
        p.add(getISBNField(), gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = labelWeight;
        p.add(LabelFactory.createLabel("Number of Pages"), gbc);
        gbc.gridx++;
        gbc.insets = indentInsets;
        gbc.weightx = inputWeight;
        p.add(getNumberOfPagesField(), gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = labelWeight;
        p.add(LabelFactory.createLabel("Anthology"), gbc);
        gbc.gridx++;
        gbc.insets = indentInsets;
        gbc.weightx = inputWeight;
        p.add(getAnthologyCheckBox(), gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = labelWeight;
        p.add(LabelFactory.createLabel("Comments"), gbc);
        gbc.gridx++;
        gbc.insets = new Insets(0, 5, 0, 0);
        gbc.weightx = inputWeight;
        p.add(new JScrollPane(getCommentsArea()), gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = labelWeight;
        p.add(LabelFactory.createLabel("Added Date"), gbc);
        gbc.gridx++;
        gbc.insets = indentInsets;
        gbc.weightx = inputWeight;
        p.add(getAddedDateLabel(), gbc);

        p.setBorder(createCompoundBorder(createEtchedBorder(), createEmptyBorder(5, 5, 5, 5)));

        return p;
    }

    @Override
    protected void resetFocus() {
        getTitleField().requestFocus();
    }

    private JPanel getAuthorPanel() {
        JPanel p = new JPanel(new BorderLayout(5, 0));
        JScrollPane scrollPane = new JScrollPane(getAuthorsList(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(AUTHOR_PANEL_SIZE);
        scrollPane.setSize(AUTHOR_PANEL_SIZE);
        scrollPane.setMinimumSize(AUTHOR_PANEL_SIZE);
        scrollPane.setMaximumSize(AUTHOR_PANEL_SIZE);
        p.add(scrollPane, BorderLayout.CENTER);

        Dimension buttonSize = new Dimension(30, 30);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setPreferredSize(buttonSize);
        buttonPanel.setSize(buttonSize);
        buttonPanel.setMinimumSize(buttonSize);
        buttonPanel.setMaximumSize(buttonSize);
        buttonPanel.setSize(buttonSize);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;

        buttonPanel.add(getAddAuthorsLabel(), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        buttonPanel.add(getAddMultipleAuthorsLabel(), gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 3, 0, 0);
        buttonPanel.add(getRemoveAuthorsLabel(), gbc);

        p.add(buttonPanel, BorderLayout.EAST);

        return p;
    }

    private JTable getAuthorsList() {
        if (authorsList == null) {
            authorsList = new JTable(new AuthorTableModel());
            authorsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            authorsList.getTableHeader().setDefaultRenderer(new HeaderRenderer(authorsList));
            GUIUtilities.setCellsAlignment(authorsList, SwingConstants.CENTER);
        }
        return authorsList;
    }

    private AuthorTableModel getAuthorTableModel() {
        return (AuthorTableModel) getAuthorsList().getModel();
    }

    private JLabel getAddAuthorsLabel() {
        JLabel addAuthorLabel = new JLabel("+");
        addAuthorLabel.setFont(new Font("Helvetica", Font.BOLD, 24));
        addAuthorLabel.setForeground(Color.green);
        addAuthorLabel.setAlignmentX(SwingConstants.LEFT);
        addAuthorLabel.setHorizontalAlignment(SwingConstants.LEFT);
        addAuthorLabel.setToolTipText("Add");

        addAuthorLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                performQuickSearch();
            }
        });
        return addAuthorLabel;
    }

    private JLabel getAddMultipleAuthorsLabel() {
        JLabel addAuthorLabel = new JLabel("++");
        addAuthorLabel.setFont(new Font("Helvetica", Font.BOLD, 24));
        addAuthorLabel.setForeground(Color.green);
        addAuthorLabel.setAlignmentX(SwingConstants.LEFT);
        addAuthorLabel.setHorizontalAlignment(SwingConstants.LEFT);
        addAuthorLabel.setToolTipText("Add Multiple");

        addAuthorLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                addMultipleAuthors();
            }
        });
        return addAuthorLabel;
    }

    private JLabel getRemoveAuthorsLabel() {
        JLabel removeAuthorLabel = new JLabel("-");
        removeAuthorLabel.setFont(new Font("Helvetica", Font.BOLD, 24));
        removeAuthorLabel.setForeground(Color.red);
        removeAuthorLabel.setAlignmentX(SwingConstants.LEFT);
        removeAuthorLabel.setHorizontalAlignment(SwingConstants.LEFT);
        removeAuthorLabel.setToolTipText("Remove");

        removeAuthorLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                removeAuthor();
            }
        });
        return removeAuthorLabel;
    }

    private JLabel getImageLabel() {
        if (imageLabel == null) {
            imageLabel = new JLabel();
            imageLabel.setIconTextGap(0);
            imageLabel.setBorder(null);
            imageLabel.setText(null);
            imageLabel.setOpaque(false);
        }
        return imageLabel;
    }

    private void downloadCoverPhoto() {
        try {
            log.info("Downloading cover photo");

            String existingImageFile = getDownloadedImage();
            if (existingImageFile != null) {
                loadImage(existingImageFile);
                return;
            }

            googleService.setCoverPhoto(getBook());
            if (getBook().getCoverPhotoPath() != null) {
                loadImage(getBook().getCoverPhotoPath().toAbsolutePath().toString());
                getISBNField().setText(getBook().getIsbn());

                if (getBook().getNumberOfPages() != null) {
                    getNumberOfPagesField().setText(getBook().getNumberOfPages().toString());
                }

                bookService.save(getBook());
            }
            else {
                getImageLabel().setIcon(null);
                reload();
            }
        }
        catch (Exception e) {
            log.error("Unable to download cover", e);
            getImageLabel().setIcon(null);
            reload();
        }
    }

    private void loadImage(String imagePath) {
        if (imagePath != null) {
            Image img = new ImageIcon(imagePath).getImage();
            getImageLabel().setIcon(new ImageIcon(img));
            reload();
        }
    }

    private String getDownloadedImage() throws IOException {
        File imageFilePath = new File(FileUtils.getDestinationDirectory(), getBook().getId() + ".jpg");

        if (imageFilePath.exists() && imageFilePath.canRead()) {
            return imageFilePath.getCanonicalPath();
        }
        else {
            return null;
        }
    }

    private void removeAuthor() {
        int row = getAuthorsList().getSelectedRow();
        getAuthorTableModel().delete(row);
        reload();
    }

    private JTextField getTitleField() {
        if (titleField == null) titleField = new TrimmedTextField(45, 255);
        return titleField;
    }

    private JTextField getSeriesField() {
        if (seriesField == null) seriesField = new TrimmedTextField(45, 255);
        return seriesField;
    }

    private JTextField getPublishedYearField() {
        if (publishedYearField == null) publishedYearField = new TrimmedTextField(10, 4);
        return publishedYearField;
    }

    private JPanel getImagePanel() {
        JPanel p = new JPanel();
        p.add(getImageLabel());
        return p;
    }

    private JTextField getISBNField() {
        if (isbnField == null) isbnField = new TrimmedTextField(30, 20);
        return isbnField;
    }

    private JTextField getNumberOfPagesField() {
        if (numberOfPagesField == null) numberOfPagesField = new TrimmedTextField(15, 20);
        return numberOfPagesField;
    }

    private JCheckBox getAnthologyCheckBox() {
        if (anthologyCheckBox == null) anthologyCheckBox = new JCheckBox();
        return anthologyCheckBox;
    }

    private JTextArea getCommentsArea() {
        if (commentsArea == null) {
            commentsArea = new JTextArea(5, 44);
            commentsArea.setLineWrap(true);
        }
        return commentsArea;
    }

    private JComboBox<BookType> getTypeCombo() {
        if (typeCombo == null) typeCombo = new JComboBox<>(BookType.values());
        return typeCombo;
    }

    private JComboBox<Media> getMediaCombo() {
        if (mediaCombo == null) mediaCombo = new JComboBox<>(Media.values());
        return mediaCombo;
    }

    public void performQuickSearch() {
        authorQuickSearch.initialize();
        authorQuickSearch.setVisible(true);

        if (authorQuickSearch.isApproved()) {
            Author a = authorQuickSearch.getSelectedAuthor();

            if (getAuthorTableModel().contains(a)) {
                showMessageDialog(a.getName() + " has already been added. Skipping.");
            }
            else {
                getAuthorTableModel().add(authorQuickSearch.getSelectedAuthor());
            }
            reload();
        }
    }

    private void addMultipleAuthors() {
        multipleAuthorDialog.setVisible(true);
        if (multipleAuthorDialog.isApproved()) {
            multipleAuthorDialog.getAuthors().forEach(a -> getAuthorTableModel().add(a));
            reload();
        }
    }

    public void clear() {
        getAnthologyCheckBox().setSelected(false);
        getCommentsArea().setText("");
        getSeriesField().setText("");
        getTitleField().setText("");
        getTypeCombo().setSelectedIndex(0);
        getISBNField().setText("");
        getPublishedYearField().setText("");
        getAddedDateLabel().setText("");
        getNumberOfPagesField().setText("");
        getAuthorTableModel().clear();
        getImageLabel().setIcon(null);

        book = null;
    }

    protected void ok() {
        Book b = createBookFromFormData();
        if (databaseUpdate(b)) {
            setBook(b);
        }
    }

    private Book createBookFromFormData() {
        Book b = getBook() == null ? new Book() : getBook();

        b.setTitle(getTitleField().getText());
        b.setSeries(getSeriesField().getText());
        b.setComments(getCommentsArea().getText());
        b.setAnthology(getAnthologyCheckBox().isSelected());
        b.setIsbn(getISBNField().getText());
        b.setPublishedYear(getPublishedYearField().getText());
        b.setType((BookType) getTypeCombo().getSelectedItem());
        b.setMedia((Media) getMediaCombo().getSelectedItem());

        if (!NullSafe.isEmpty(getNumberOfPagesField().getText())) {
            b.setNumberOfPages(Integer.parseInt(getNumberOfPagesField().getText()));
        }

        b.clearAuthors();
        getAuthorTableModel().stream().forEach(b::addAuthor);
        b.setEditors(getAuthorTableModel().getEditors());

        return b;
    }

    private boolean databaseUpdate(Book b) {
        bookService.save(b);
        sendMessage(b + " has been saved.");
        return true;
    }

    private Book getBook() {
        return book;
    }

    public void setBook(Book b) {
        clear();

        this.book = b;

        if (book != null) {
            loadAllData();
        }
    }

    private void loadAllData() {
        loadData();

        log.info("Load all data");


        new SwingWorker<Object, Boolean>() {
            @Override
            protected Boolean doInBackground() {
                loadCoverPhoto();
                return Boolean.TRUE;
            }
        }.execute();
    }

    private void loadCoverPhoto() {
        try {
            getImageLabel().setIcon(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("images/loading.gif"))));
            downloadCoverPhoto();
        }
        catch (Exception e) {
            log.error("Unable to load the covder photo", e);
        }
    }

    private void loadData() {
        getTitleField().setText(getBook().getTitle());
        getSeriesField().setText(getBook().getSeries());
        getCommentsArea().setText(getBook().getComments());
        getAnthologyCheckBox().setSelected(getBook().isAnthology());
        getTypeCombo().setSelectedItem(getBook().getType());
        getISBNField().setText(getBook().getIsbn());
        getPublishedYearField().setText(getBook().getPublishedYear());
        getMediaCombo().setSelectedItem(getBook().getMedia());

        if (getBook().getNumberOfPages() != null && getBook().getNumberOfPages() > 0) {
            getNumberOfPagesField().setText(String.valueOf(getBook().getNumberOfPages()));
        }

        getAddedDateLabel().setText(book.getAddedTimestamp());

        getAuthorTableModel().clear();
        getBook().getAuthors().stream().sorted().forEach(a -> getAuthorTableModel().add(a));
        reload();
    }

    @Autowired
    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }

    @Autowired
    public void setGoogleService(GoogleService googleService) {
        this.googleService = googleService;
    }

    @Autowired
    public void setAuthorQuickSearch(AuthorQuickSearch authorQuickSearch) {
        this.authorQuickSearch = authorQuickSearch;
    }

    @Autowired
    public void setMultipleAuthorDialog(MultipleAuthorsDialog multipleAuthorDialog) {
        this.multipleAuthorDialog = multipleAuthorDialog;
    }
}
