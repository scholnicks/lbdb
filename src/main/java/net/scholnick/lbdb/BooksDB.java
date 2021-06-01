package net.scholnick.lbdb;

import net.scholnick.lbdb.domain.*;
import net.scholnick.lbdb.gui.SearchPanel;
import net.scholnick.lbdb.gui.author.AuthorMaintenance;
import net.scholnick.lbdb.gui.title.TitleMaintenance;
import net.scholnick.lbdb.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

import static net.scholnick.lbdb.util.GUIUtilities.*;

@Component
public final class BooksDB extends JFrame {
    private JTabbedPane       tabbedPane;
    private SearchPanel       searchPanel;
    private TitleMaintenance  titleMaintenance;
    private AuthorMaintenance authorMaintenance;
    private final JLabel      notificationLabel;

    private BookService   bookService;
    private AuthorService authorService;
    private ExportService exportService;

    public static final Dimension WINDOW_SIZE = new Dimension(900, 700);
    private static final String       VERSION = "Version 6.0.2";

    public BooksDB() {
        super("Laurel's Books Database");
        notificationLabel = new JLabel("",JLabel.CENTER);
    }

    void init() {
        setSize(WINDOW_SIZE);
        center(this);

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
        p.add(notificationLabel);
        getContentPane().add(p, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void loadInfoInBackground() {
        new SwingWorker<Object,Boolean>() {
            @Override protected Boolean doInBackground() {
                setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/bookcase.gif"))).getImage());
                updateCounts();
                return Boolean.TRUE;
            }
        }.execute();

        // load the author cache in a separate short lived thread
        new Thread(authorService::loadCache).start();
    }

    private JTabbedPane getTabbedPane() {
        if (tabbedPane == null) {
            tabbedPane = new JTabbedPane();
            tabbedPane.setTabPlacement(SwingConstants.TOP);
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

    private void updateCounts() {
        notificationLabel.setText(bookService.count() + " books / " + authorService.count() + " authors");
    }

    private void buildMenus() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(getTitleMenu());
        menuBar.add(getGoMenu());
        menuBar.add(getEditMenu());
        setJMenuBar(menuBar);

        Desktop.getDesktop().setAboutHandler(e -> showMessageDialog(
            VERSION + "\nMode: " + System.getProperty("lbdb.environment","dev"),
            "About"
        ));
    }

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

    private JMenu getTitleMenu() {
        JMenu fileMenu = new JMenu("Title");

        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.META_DOWN_MASK));
        saveItem.setText("Save");
        saveItem.addActionListener(l -> titleMaintenance.save());
        fileMenu.add(saveItem);

        JMenuItem clearItem = new JMenuItem("Clear");
        clearItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.META_DOWN_MASK));
        clearItem.setText("Clear");
        clearItem.addActionListener(l -> titleMaintenance.clear());
        fileMenu.add(clearItem);

        JMenuItem exportItem = new JMenuItem("Export");
        exportItem.setText("Export");
        exportItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.META_DOWN_MASK));
        exportItem.addActionListener(l -> exportService.export());
        fileMenu.add(exportItem);

        return fileMenu;
    }

    private JMenu getGoMenu() {
        JMenu goMenu = new JMenu("Go");

        JMenuItem searchItem = new JMenuItem("Search");
        searchItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.META_DOWN_MASK));
        searchItem.setText("Search");
        searchItem.addActionListener(l -> getTabbedPane().setSelectedIndex(0));
        goMenu.add(searchItem);

        JMenuItem titleItem = new JMenuItem("Title");
        titleItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.META_DOWN_MASK));
        titleItem.setText("Title");
        titleItem.addActionListener(l -> getTabbedPane().setSelectedIndex(1));
        goMenu.add(titleItem);

        JMenuItem authorItem = new JMenuItem("Author");
        authorItem.setText("Author");
        authorItem.addActionListener(l -> getTabbedPane().setSelectedIndex(2));
        goMenu.add(authorItem);

        return goMenu;
    }

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
    public void setExportService(ExportService exportService) {
        this.exportService = exportService;
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
