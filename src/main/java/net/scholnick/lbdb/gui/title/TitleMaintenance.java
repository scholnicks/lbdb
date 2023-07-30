package net.scholnick.lbdb.gui.title;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import net.scholnick.lbdb.BooksDB;
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
import java.util.Objects;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;
import static javax.swing.BorderFactory.*;
import static javax.swing.JOptionPane.*;

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
    private final AuthorTable authorsTable;

    private JTextField editorsSelect;
    private final AuthorTable editorsTable;

    private Book book;

    private BookService       bookService;
    private AuthorService     authorService;
    private CoverPhotoService coverPhotoService;

    private static final int WIDTH = 128;
    private static final int HEIGHT = 198;

    static final Dimension TEXT_FIELD_SIZE = new Dimension(400,20);

    public TitleMaintenance() {
        super();

        titleField           = TrimmedTextField.create(45, 255,TEXT_FIELD_SIZE);
        seriesField          = TrimmedTextField.create(45, 255, TEXT_FIELD_SIZE);
        publishedYearField   = TrimmedTextField.create(10, 4, TEXT_FIELD_SIZE);
        isbnField            = TrimmedTextField.create(20, 20, TEXT_FIELD_SIZE);
        numberOfPagesField   = TrimmedTextField.create(15, 20, TEXT_FIELD_SIZE);
        anthologyCheckBox    = new JCheckBox();
        typeCombo            = new JComboBox<>(BookType.values());
        mediaCombo           = new JComboBox<>(Media.values());

        authorsTable = new AuthorTable();
        editorsTable = new AuthorTable();

        commentsArea = new JTextArea(5, 44);
        commentsArea.setLineWrap(true);
        GUIUtilities.setSizes(commentsArea,new Dimension(400,75));

        buildGUI();
    }

    private void addAuthor(Author a, AuthorTable dataTable, JTextField inputField) {
        if (a.getId() == null) {
            if (showConfirmDialog(this, a.getName() + " not found.","Add?",YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                return;
            }
        }
        dataTable.add(a);
        inputField.setText("");
    }

    private void popUpMenu(java.util.List<Author> data, AuthorTable dataPanel, JTextField inputField) {
        JPopupMenu popup = new JPopupMenu();
        for (Author d : data) {
            JMenuItem item = new JMenuItem(d.getName());
            item.addActionListener(l -> addAuthor(d,dataPanel,inputField));
            popup.add(item);
        }
        popup.pack();
        popup.show(inputField, 0, inputField.getHeight());
        popup.requestFocusInWindow();
    }

    private java.util.List<Author> searchForAuthors(JTextField inputField) {
        return authorService.search(inputField.getText()).stream().filter(Objects::nonNull).limit(20).collect(toList());
    }

    private JTextField getAuthorsSelect() {
        if (authorsSelect == null) {
            authorsSelect = new JTextField(30);
            GUIUtilities.setSizes(authorsSelect,TEXT_FIELD_SIZE);
            authorsSelect.addActionListener(l -> {
                var data = searchForAuthors(authorsSelect);
                if (data.isEmpty()) {
                    addAuthor(Author.of(authorsSelect.getText()),authorsTable,authorsSelect);
                }
                else {
                    popUpMenu(data,authorsTable,authorsSelect);
                }
            });
        }
        return authorsSelect;
    }

    private JTextField getEditorsSelect() {
        if (editorsSelect == null) {
            editorsSelect = new JTextField(30);
            GUIUtilities.setSizes(editorsSelect,TEXT_FIELD_SIZE);
            editorsSelect.addActionListener(l -> {
                var data = searchForAuthors(editorsSelect);
                if (data.isEmpty()) {
                    addAuthor(Author.of(editorsSelect.getText()),editorsTable,editorsSelect);
                }
                else {
                    popUpMenu(data,editorsTable,editorsSelect);
                }
            });
        }
        return editorsSelect;
    }

    private JButton getDeleteButton() {
        if (deleteButton == null) {
            deleteButton = GUIUtilities.createButton("Delete");
            deleteButton.addActionListener(e -> {
                if (showConfirmDialog(this, "Delete Book?") == JOptionPane.YES_OPTION) {
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

            Dimension d = new Dimension(WIDTH,HEIGHT);
            imageLabel.setPreferredSize(d);
            imageLabel.setSize(d);
            imageLabel.setMaximumSize(d);
            imageLabel.setMinimumSize(d);

            IconFontSwing.register(FontAwesome.getIconFont());
            imageLabel.setIcon(IconFontSwing.buildIcon(FontAwesome.BOOK, (float) WIDTH));
        }
        return imageLabel;
    }

    private void downloadCoverPhoto() {
        try {
            log.info("Downloading cover photo");

            String existingImageFile = coverPhotoService.getDownloadedImage(getBook());
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
            getImageLabel().setIcon(new ImageIcon(new ImageIcon(imagePath).getImage()));
            reload();
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

        authorsTable.clear();
        editorsTable.clear();

        IconFontSwing.register(FontAwesome.getIconFont());
        getImageLabel().setIcon(IconFontSwing.buildIcon(FontAwesome.BOOK, 48, Color.lightGray));

        book = null;
    }

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

        if (! NullSafe.isEmpty(numberOfPagesField.getText())) {
            b.setNumberOfPages(Integer.parseInt(numberOfPagesField.getText()));
        }

        b.clearAuthors();
        authorsTable.get().forEach(b::addAuthor);
        editorsTable.get().forEach(b::addEditor);

        return b;
    }

    private void clear(JPanel panel) {
        for (int i=panel.getComponentCount()-1; i >=0; i--) {
            panel.remove(i);
        }
    }

    private Book getBook() {
        return book;
    }

    public void setBook(Book b) {
        clear();
        this.book = b;
        loadData(b);
    }

    @Override
    protected void ok() {
        Book b = createBookFromFormData();
        bookService.save(b);
        sendMessage(b.getTitle() + " has been saved.");
        setBook(b);
    }

    private void loadData(Book b) {
        new SwingWorker<Object, Boolean>() {
            @Override protected Boolean doInBackground() {
                try {
                    getImageLabel().setIcon(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("images/loading.gif"))));
                    downloadCoverPhoto();
                    reload();
                }
                catch (Exception e) {
                    log.error("Unable to load the cover photo", e);
                }
                return Boolean.TRUE;
            }
        }.execute();

        titleField.setText(b.getTitle());
        seriesField.setText(b.getSeries());
        commentsArea.setText(b.getComments());
        anthologyCheckBox.setSelected(b.isAnthology());
        typeCombo.setSelectedItem(b.getType());
        isbnField.setText(b.getIsbn());
        publishedYearField.setText(b.getPublishedYear());
        mediaCombo.setSelectedItem(b.getMedia());

        if (b.getNumberOfPages() != null && b.getNumberOfPages() > 0) {
            numberOfPagesField.setText(String.valueOf(b.getNumberOfPages()));
        }

        getAddedDateLabel().setText(b.getAddedTimestamp());

        authorsTable.clear();
        b.getAuthors().stream().filter(not(Author::isEditor)).forEach(authorsTable::add);

        editorsTable.clear();
        b.getAuthors().stream().filter(Author::isEditor).forEach(editorsTable::add);

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
        p.setBorder(createCompoundBorder(createEtchedBorder(), createEmptyBorder(5, 5, 5, 5)));
        GUIUtilities.setSizes(p,new Dimension(BooksDB.WINDOW_SIZE.width - 20,BooksDB.WINDOW_SIZE.height-100));

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

        // Start of Authors

        IconFontSwing.register(FontAwesome.getIconFont());
        JLabel searchLabel = new JLabel(IconFontSwing.buildIcon(FontAwesome.SEARCH, 24, Color.GREEN));
        searchLabel.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                popUpMenu(searchForAuthors(getAuthorsSelect()), authorsTable, getAuthorsSelect());
            }
        });

        gbc.gridy++;
        gbc.weightx = labelWeight;
        gbc.gridx = 0;
        p.add(LabelFactory.createLabel("Authors"), gbc);
        gbc.gridx++;
        gbc.weightx = inputWeight;
        gbc.insets = new Insets(0, -5, 0, 0);
        JPanel authorSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        authorSearchPanel.setBorder(BorderFactory.createEmptyBorder());
        authorSearchPanel.add(getAuthorsSelect());
        authorSearchPanel.add(searchLabel);
        p.add(authorSearchPanel, gbc);

        gbc.gridy++;
        gbc.weightx = labelWeight;
        gbc.gridx = 0;
        p.add(LabelFactory.createLabel(""), gbc);
        gbc.gridx++;
        gbc.insets = new Insets(0,4,0,0);
        gbc.weightx = inputWeight;

        JScrollPane authorsScroll = new JScrollPane(authorsTable,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        GUIUtilities.setSizes(authorsScroll,new Dimension(AuthorTable.SIZE.width+30,AuthorTable.SIZE.height+30));
        authorsScroll.setForeground(BooksDB.FOREGROUND_COLOR);
        authorsScroll.setBackground(BooksDB.BACKGROUND_COLOR);
        p.add(authorsScroll, gbc);

        // End of Authors

        // Start of Editors

        IconFontSwing.register(FontAwesome.getIconFont());
        JLabel editorSearchLabel = new JLabel(IconFontSwing.buildIcon(FontAwesome.SEARCH, 24, Color.GREEN));
        editorSearchLabel.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                popUpMenu(searchForAuthors(getEditorsSelect()), editorsTable, getEditorsSelect());
            }
        });

        gbc.gridy++;
        gbc.weightx = labelWeight;
        gbc.gridx = 0;
        p.add(LabelFactory.createLabel("Editors"), gbc);
        gbc.gridx++;
        gbc.weightx = inputWeight;
        gbc.insets = new Insets(0, -5, 0, 0);
        JPanel editorSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        editorSearchPanel.add(getEditorsSelect());
        editorSearchPanel.add(editorSearchLabel);
        p.add(editorSearchPanel, gbc);

        gbc.gridy++;
        gbc.weightx = labelWeight;
        gbc.gridx = 0;
        p.add(LabelFactory.createLabel(""), gbc);
        gbc.gridx++;
        gbc.insets = new Insets(0,4,0,0);
        gbc.weightx = inputWeight;

        JScrollPane editorsScroll = new JScrollPane(editorsTable,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        GUIUtilities.setSizes(editorsScroll,new Dimension(AuthorTable.SIZE.width+30,AuthorTable.SIZE.height+30));
        editorsScroll.setForeground(BooksDB.FOREGROUND_COLOR);
        editorsScroll.setBackground(BooksDB.BACKGROUND_COLOR);
        p.add(editorsScroll, gbc);

        // End of Editors

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = labelWeight;
        p.add(LabelFactory.createLabel("Series"), gbc);
        gbc.gridx++;
        gbc.insets = indentInsets;
        gbc.weightx = inputWeight;
        p.add(seriesField, gbc);

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
        gbc.insets = new Insets(0, -5, 0, 0);
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

        return p;
    }
}
