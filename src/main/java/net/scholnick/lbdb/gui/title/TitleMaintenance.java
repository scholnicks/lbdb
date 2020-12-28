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

import static java.util.stream.Collectors.toList;
import static javax.swing.BorderFactory.*;

@Component
public final class TitleMaintenance extends AbstractUpdateMaintenance {
    private static final Logger log = LoggerFactory.getLogger(TitleMaintenance.class);

    private final JTextField titleField;
    private final JTextField seriesField;
    private final JTextField publishedYearField;
    private final JTextField isbnField;
    private final JTextField numberOfPagesField;
    private final JCheckBox anthologyCheckBox;
    private final JComboBox<BookType> typeCombo;
    private final JComboBox<Media> mediaCombo;
    private final JTextArea commentsArea;

    private JLabel imageLabel;
    private JButton deleteButton;

    private JTextField authorsSelect;
    private final JPanel selectedAuthorsPanel;

    private Book book;

    private BookService       bookService;
    private AuthorService     authorService;
    private CoverPhotoService coverPhotoService;

    public TitleMaintenance() {
        super();

        titleField           = new TrimmedTextField(45, 255);
        seriesField          = new TrimmedTextField(45, 255);
        publishedYearField   = new TrimmedTextField(10, 4);
        isbnField            = new TrimmedTextField(30, 20);
        numberOfPagesField   = new TrimmedTextField(15, 20);
        anthologyCheckBox    = new JCheckBox();
        typeCombo            = new JComboBox<>(BookType.values());
        mediaCombo           = new JComboBox<>(Media.values());
        selectedAuthorsPanel = new JPanel(new FlowLayout());

        commentsArea = new JTextArea(5, 44);
        commentsArea.setLineWrap(true);

        buildGUI();
    }

    private void createAuthorLabel(Author a, boolean reload) {
        JLabel label = DataLabel.of(a);
        label.setBorder(BorderFactory.createEtchedBorder());
        label.setForeground(Color.white);

        if (a.getId() == null) {
            label.setBackground(Color.green);
        }
        else {
            label.setBackground(Color.black);
        }

        label.setOpaque(true);
        label.setHorizontalTextPosition(JLabel.LEFT);
        label.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                selectedAuthorsPanel.remove((JLabel) e.getSource());
            }
        });
        selectedAuthorsPanel.add(label);

        if (reload) {
            getAuthorsSelect().setText("");
            reload();
        }
    }

    private void popUpMenu(java.util.List<Author> data) {
        JPopupMenu popup = new JPopupMenu();
        for (Author d : data) {
            JMenuItem item = new JMenuItem(d.getName());
            item.addActionListener(l -> createAuthorLabel(d,true));
            popup.add(item);
        }
        popup.pack();
        popup.show(getAuthorsSelect(), 0, getAuthorsSelect().getHeight());
        popup.requestFocusInWindow();
    }

    private java.util.List<Author> searchForAuthors() {
        return authorService.search(authorsSelect.getText()).stream().filter(Objects::nonNull).limit(20).collect(toList());
    }

    private JTextField getAuthorsSelect() {
        if (authorsSelect == null) {
            authorsSelect = new JTextField(15);
            authorsSelect.addActionListener(l -> {
                var data = searchForAuthors();
                if (data.isEmpty()) {
                    createAuthorLabel(Author.of(getAuthorsSelect().getText()),true);
                }
                else {
                    popUpMenu(data);
                }
            });
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

    @Override
    protected void resetFocus() {
        titleField.requestFocus();
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
                isbnField.setText(getBook().getIsbn());

                if (getBook().getNumberOfPages() != null) {
                    numberOfPagesField.setText(getBook().getNumberOfPages().toString());
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

    public void clear() {
        anthologyCheckBox.setSelected(false);
        commentsArea.setText("");
        seriesField.setText("");
        titleField.setText("");
        typeCombo.setSelectedIndex(0);
        isbnField.setText("");
        publishedYearField.setText("");
        getAddedDateLabel().setText("");
        numberOfPagesField.setText("");
        clear(selectedAuthorsPanel);
        getImageLabel().setIcon(null);

        book = null;
    }

    protected void ok() {
        Book b = createBookFromFormData();
        if (databaseUpdate(b)) {
            setBook(b);
        }
    }

    @SuppressWarnings("unchecked")
    private Book createBookFromFormData() {
        Book b = getBook() == null ? new Book() : getBook();

        b.setTitle(titleField.getText());
        b.setSeries(seriesField.getText());
        b.setComments(commentsArea.getText());
        b.setAnthology(anthologyCheckBox.isSelected());
        b.setPublishedYear(publishedYearField.getText());
        b.setType((BookType) typeCombo.getSelectedItem());
        b.setMedia((Media) mediaCombo.getSelectedItem());

        if (! NullSafe.isEmpty(isbnField.getText())) {
            b.setIsbn(isbnField.getText().replace("-",""));
        }

        if (!NullSafe.isEmpty(numberOfPagesField.getText())) {
            b.setNumberOfPages(Integer.parseInt(numberOfPagesField.getText()));
        }

        b.clearAuthors();
        for (int i=0,n=selectedAuthorsPanel.getComponentCount(); i < n; i++) {
            DataLabel<Author> dataLabel = (DataLabel<Author>) selectedAuthorsPanel.getComponent(i);
            b.addAuthor(dataLabel.getData());
        }

        //        b.setEditors(getAuthorTableModel().getEditors());

        return b;
    }

    private void clear(JPanel panel) {
        for (int i=panel.getComponentCount()-1; i >=0; i--) {
            panel.remove(i);
        }
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
        titleField.setText(getBook().getTitle());
        seriesField.setText(getBook().getSeries());
        commentsArea.setText(getBook().getComments());
        anthologyCheckBox.setSelected(getBook().isAnthology());
        typeCombo.setSelectedItem(getBook().getType());
        isbnField.setText(getBook().getIsbn());
        publishedYearField.setText(getBook().getPublishedYear());
        mediaCombo.setSelectedItem(getBook().getMedia());

        if (getBook().getNumberOfPages() != null && getBook().getNumberOfPages() > 0) {
            numberOfPagesField.setText(String.valueOf(getBook().getNumberOfPages()));
        }

        getAddedDateLabel().setText(book.getAddedTimestamp());

        clear(selectedAuthorsPanel);
        getBook().getAuthors().stream().sorted().forEach(a -> createAuthorLabel(a,false));

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

    // GUI related methods

    @Override
    protected void buildGUI() {
        setLayout(new BorderLayout());
        JPanel p = new JPanel();
        p.add(getImageLabel());
        add(p, BorderLayout.WEST);
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

    @Override
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
        p.add(titleField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = labelWeight;
        p.add(LabelFactory.createLabel("Series"), gbc);
        gbc.gridx++;
        gbc.insets = indentInsets;
        gbc.weightx = inputWeight;
        p.add(seriesField, gbc);

        // Start of Authors

        IconFontSwing.register(FontAwesome.getIconFont());
        JLabel searchLabel = new JLabel(IconFontSwing.buildIcon(FontAwesome.SEARCH, 24, Color.GREEN));
        searchLabel.addMouseListener(new MouseAdapter() {@Override public void mouseClicked(MouseEvent e) { popUpMenu(searchForAuthors()); }});

        gbc.gridy++;
        gbc.weightx = labelWeight;
        gbc.gridx = 0;
        p.add(LabelFactory.createLabel("Authors"), gbc);
        gbc.gridx++;
        gbc.weightx = inputWeight;
        JPanel authorSearchPanel = new JPanel(new FlowLayout());
        authorSearchPanel.add(getAuthorsSelect());
        authorSearchPanel.add(searchLabel);
        p.add(authorSearchPanel, gbc);

        gbc.gridy++;
        gbc.weightx = labelWeight;
        gbc.gridx = 0;
        p.add(LabelFactory.createLabel(""), gbc);
        gbc.gridx++;
        gbc.weightx = inputWeight;
        p.add(selectedAuthorsPanel, gbc);

        // End of Authors

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = labelWeight;
        p.add(LabelFactory.createLabel("Type"), gbc);
        gbc.gridx++;
        gbc.insets = indentInsets;
        gbc.weightx = inputWeight;
        p.add(typeCombo, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = labelWeight;
        p.add(LabelFactory.createLabel("Media"), gbc);
        gbc.gridx++;
        gbc.insets = indentInsets;
        gbc.weightx = inputWeight;
        p.add(mediaCombo, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = labelWeight;
        p.add(LabelFactory.createLabel("Year Published"), gbc);
        gbc.gridx++;
        gbc.insets = indentInsets;
        gbc.weightx = inputWeight;
        p.add(publishedYearField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = labelWeight;
        p.add(LabelFactory.createLabel("ISBN"), gbc);
        gbc.gridx++;
        gbc.insets = indentInsets;
        gbc.weightx = inputWeight;
        p.add(isbnField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = labelWeight;
        p.add(LabelFactory.createLabel("Number of Pages"), gbc);
        gbc.gridx++;
        gbc.insets = indentInsets;
        gbc.weightx = inputWeight;
        p.add(numberOfPagesField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = labelWeight;
        p.add(LabelFactory.createLabel("Anthology"), gbc);
        gbc.gridx++;
        gbc.insets = indentInsets;
        gbc.weightx = inputWeight;
        p.add(anthologyCheckBox, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = labelWeight;
        p.add(LabelFactory.createLabel("Comments"), gbc);
        gbc.gridx++;
        gbc.insets = new Insets(0, 5, 0, 0);
        gbc.weightx = inputWeight;
        p.add(new JScrollPane(commentsArea), gbc);

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

}
