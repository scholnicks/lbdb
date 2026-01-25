package net.scholnick.lbdb;

import net.scholnick.lbdb.domain.*;
import net.scholnick.lbdb.gui.SearchPanel;
import net.scholnick.lbdb.gui.author.AuthorMaintenance;
import net.scholnick.lbdb.gui.title.TitleMaintenance;
import net.scholnick.lbdb.service.*;
import net.scholnick.lbdb.util.GUIUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

/**
 *  BooksDB is the main application window.
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */
@Component
public final class BooksDB extends JFrame {
    private JTabbedPane       tabbedPane;
    private SearchPanel       searchPanel;
    private TitleMaintenance  titleMaintenance;
    private AuthorMaintenance authorMaintenance;
    private final JLabel      notificationLabel;

    private BookService   bookService;
    private AuthorService authorService;

    public static final Dimension WINDOW_SIZE = new Dimension(1000, 850);
    private static final String       VERSION = "Version 8.2.2";

    public static final Color BACKGROUND_COLOR = new Color(4,106,56);
    public static final Color FOREGROUND_COLOR = Color.white;

    public BooksDB() {
        super("Laurel's Books Database");
        notificationLabel = new JLabel("",JLabel.CENTER);
    }

    /** Initialize the main window. */
    void init() {
        setSize(WINDOW_SIZE);
        GUIUtilities.center(this);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        JPanel content = (JPanel) getContentPane();
        content.setLayout(new BorderLayout());
        content.setBackground(Color.white);

        loadInfoInBackground();
        buildMenus();
        loadSearchPanel();
        getContentPane().add(getTabbedPane(), BorderLayout.CENTER);

        JPanel p = new JPanel();
        notificationLabel.setForeground(FOREGROUND_COLOR);
        p.add(notificationLabel);
        p.setForeground(FOREGROUND_COLOR);
        p.setBackground(BACKGROUND_COLOR);
        getContentPane().add(p, BorderLayout.SOUTH);

        setVisible(true);
    }

    /** Load information in the background. */
    private void loadInfoInBackground() {
        SwingUtilities.invokeLater(() -> {
            setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/bookcase.gif"))).getImage());
            updateCounts();
        });

        // load the author cache in a separate short-lived thread
        new Thread(authorService::loadCache).start();
    }

    /** Get the tabbed pane. */
    private JTabbedPane getTabbedPane() {
        if (tabbedPane == null) {
            tabbedPane = new JTabbedPane();
            tabbedPane.setTabPlacement(SwingConstants.TOP);
            tabbedPane.setForeground(FOREGROUND_COLOR);
            tabbedPane.setBackground(BACKGROUND_COLOR);
            tabbedPane.setOpaque(true);
            tabbedPane.addTab("Search", searchPanel);
            tabbedPane.addTab("Title", titleMaintenance);
            tabbedPane.addTab("Author", authorMaintenance);

            tabbedPane.addChangeListener(e -> {
                JTabbedPane tp = (JTabbedPane) e.getSource();
                if (tp.getSelectedIndex() == 0) { // back to the search tab, so update the title
                    updateCounts();
                }
                notificationLabel.setText("");
            });
        }
        return tabbedPane;
    }

    /** Update the counts in the notification label. */
    private void updateCounts() {
        notificationLabel.setText(bookService.count() + " books / " + authorService.count() + " authors");
    }

    /** Build the application menus. */
    private void buildMenus() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(getTitleMenu());
        menuBar.add(getGoMenu());
        menuBar.add(getEditMenu());
        setJMenuBar(menuBar);

        Desktop.getDesktop().setAboutHandler(_ -> GUIUtilities.showMessageDialog(null,
            VERSION + "\nMode: " + System.getProperty("lbdb.environment","dev"),
            "About"
        ));
    }

    /** Build the Edit menu. */
    private JMenu getEditMenu() {
        JMenu editMenu = new JMenu("Edit");

        JMenuItem cutItem = new JMenuItem(new DefaultEditorKit.CutAction());
        cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.META_DOWN_MASK));
        cutItem.setText("Cut");
        editMenu.add(cutItem);

        JMenuItem copyItem = new JMenuItem(new DefaultEditorKit.CopyAction());
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.META_DOWN_MASK));
        copyItem.setText("Copy");
        editMenu.add(copyItem);

        JMenuItem pasteItem = new JMenuItem(new DefaultEditorKit.PasteAction());
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.META_DOWN_MASK));
        pasteItem.setText("Paste");
        editMenu.add(pasteItem);

        return editMenu;
    }

    /** Build the Title menu. */
    private JMenu getTitleMenu() {
        JMenu fileMenu = new JMenu("Title");

        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.META_DOWN_MASK));
        saveItem.setText("Save");
        saveItem.addActionListener(_ -> titleMaintenance.save());
        fileMenu.add(saveItem);

        JMenuItem clearItem = new JMenuItem("Clear");
        clearItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.META_DOWN_MASK));
        clearItem.setText("Clear");
        clearItem.addActionListener(_ -> titleMaintenance.clear());
        fileMenu.add(clearItem);

        JMenuItem exportItem = new JMenuItem("Export");
        exportItem.setText("Export");
        exportItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.META_DOWN_MASK));

        return fileMenu;
    }

    /** Build the Go menu. */
    private JMenu getGoMenu() {
        JMenu goMenu = new JMenu("Go");

        JMenuItem searchItem = new JMenuItem("Search");
        searchItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.META_DOWN_MASK));
        searchItem.setText("Search");
        searchItem.addActionListener(_ -> getTabbedPane().setSelectedIndex(0));
        goMenu.add(searchItem);

        JMenuItem titleItem = new JMenuItem("Title");
        titleItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.META_DOWN_MASK));
        titleItem.setText("Title");
        titleItem.addActionListener(_ -> getTabbedPane().setSelectedIndex(1));
        goMenu.add(titleItem);

        JMenuItem authorItem = new JMenuItem("Author");
        authorItem.setText("Author");
        authorItem.addActionListener(_ -> getTabbedPane().setSelectedIndex(2));
        goMenu.add(authorItem);

        return goMenu;
    }

    /** Load the search panel listeners. */
    private void loadSearchPanel() {
        searchPanel.addTitleSelectionListener(event -> {
            titleMaintenance.setBook((Book) event.getSource());
            getTabbedPane().setSelectedIndex(1);
        });

        searchPanel.addAuthorSelectionListener(event -> {
            authorMaintenance.setAuthor((Author) event.getSource());
            getTabbedPane().setSelectedIndex(2);
        });
    }

    @Override
    public Dimension getPreferredSize() {
        return WINDOW_SIZE;
    }

    @Override
    public Dimension getMinimumSize() {
        return WINDOW_SIZE;
    }

    @Override
    public Dimension getMaximumSize() {
        return WINDOW_SIZE;
    }

    @Autowired
    public void setSearchPanel(SearchPanel searchPanel) {
        this.searchPanel = searchPanel;
    }

    @Autowired
    public void setTitleMaintenance(TitleMaintenance titleMaintenance) {
        this.titleMaintenance = titleMaintenance;
        this.titleMaintenance.setMessageListener(notificationLabel::setText);
    }

    @Autowired
    public void setAuthorMaintenance(AuthorMaintenance authorMaintenance) {
        this.authorMaintenance = authorMaintenance;
        this.authorMaintenance.setMessageListener(notificationLabel::setText);
    }

    @Autowired
    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }

    @Autowired
    public void setAuthorService(AuthorService authorService) {
        this.authorService = authorService;
    }
}
