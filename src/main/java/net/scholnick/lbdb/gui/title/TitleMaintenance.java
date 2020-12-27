package net.scholnick.lbdb.gui.title;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import net.scholnick.lbdb.coverphoto.CoverPhotoService;
import net.scholnick.lbdb.domain.*;
import net.scholnick.lbdb.gui.*;
import net.scholnick.lbdb.service.*;
import net.scholnick.lbdb.util.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Objects;

import static javax.swing.BorderFactory.*;

@Component
public final class TitleMaintenance extends AbstractUpdateMaintenance {
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

    private JPanel selectedAuthorsPanel;

    private JTextField authorsSelect;

    private JComboBox<BookType> typeCombo;
    private JComboBox<Media> mediaCombo;

    private Book book;

    private BookService bookService;
    private AuthorService         authorService;
    private CoverPhotoService coverPhotoService;

    private final Icon icon;

    public TitleMaintenance() {
        super();
        buildGUI();
        IconFontSwing.register(FontAwesome.getIconFont());
        icon = IconFontSwing.buildIcon(FontAwesome.BAN, 10, Color.red);
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

    private JPanel selectedAuthorsPanel() {
        if (selectedAuthorsPanel == null) selectedAuthorsPanel = new JPanel(new FlowLayout());
        return selectedAuthorsPanel;
    }

    private void createAuthorLabel(Author a) {
        JLabel label = new JLabel(a.getName());
        label.setBorder(BorderFactory.createEtchedBorder());
        label.setForeground(Color.white);
        label.setBackground(Color.black);
        label.setOpaque(true);
        label.setIcon(icon);
        label.setHorizontalTextPosition(JLabel.LEFT);
        label.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                selectedAuthorsPanel().remove((JLabel) e.getSource());
            }
        });
        selectedAuthorsPanel().add(label);
        getAuthorsSelect().setText("");
        reload();
    }

    private JTextField getAuthorsSelect() {
        if (authorsSelect == null) {
            authorsSelect = SelectTextField.of(
              45,
              3,
               () -> authorService.search(authorsSelect.getText()),
               this::createAuthorLabel
           );
        }
        return authorsSelect;
    }

    private JButton getDeleteButton() {
        if (deleteButton == null) {
            deleteButton = new JButton("Delete");
            deleteButton.addActionListener(e -> {
                if (JOptionPane.showConfirmDialog(this, "Delete Book?") == JOptionPane.YES_OPTION) {
                    bookService.delete(book);
                    sendMessage(book + " deleted");
                    clear();
                }
            });
        }
        return deleteButton;
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
        p.add(getAuthorsSelect(), gbc);

        gbc.gridy++;
        gbc.weightx = labelWeight;
        gbc.gridx = 0;
        p.add(LabelFactory.createLabel(""), gbc);
        gbc.gridx++;
        gbc.weightx = inputWeight;
        p.add(selectedAuthorsPanel(), gbc);

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

            coverPhotoService.setCoverPhoto(getBook());
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

//    private void removeAuthor() {
//        int row = getAuthorsList().getSelectedRow();
//        getAuthorTableModel().delete(row);
//        reload();
//    }

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
//        getAuthorTableModel().clear();
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
        b.setPublishedYear(getPublishedYearField().getText());
        b.setType((BookType) getTypeCombo().getSelectedItem());
        b.setMedia((Media) getMediaCombo().getSelectedItem());

        if (! NullSafe.isEmpty(getISBNField().getText())) {
            b.setIsbn(getISBNField().getText().replace("-",""));
        }

        if (!NullSafe.isEmpty(getNumberOfPagesField().getText())) {
            b.setNumberOfPages(Integer.parseInt(getNumberOfPagesField().getText()));
        }

        b.clearAuthors();
//        getAuthorTableModel().stream().forEach(b::addAuthor);
//        b.setEditors(getAuthorTableModel().getEditors());

        return b;
    }

    private boolean databaseUpdate(Book b) {
        bookService.save(b);
        sendMessage(b.getTitle() + " has been saved.");
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
            log.error("Unable to load the cover photo", e);
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

//        getAuthorTableModel().clear();
//        getBook().getAuthors().stream().sorted().forEach(a -> getAuthorTableModel().add(a));
        reload();
    }

    @Autowired
    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }

    @Autowired
    public void setCoverPhotoService(CoverPhotoService coverPhotoService) {
        this.coverPhotoService = coverPhotoService;
    }

    @Autowired
    public void setAuthorService(AuthorService authorService) {
        this.authorService = authorService;
    }
}
