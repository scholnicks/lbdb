package net.scholnick.lbdb;

import net.scholnick.lbdb.domain.Author;
import net.scholnick.lbdb.domain.Book;
import net.scholnick.lbdb.gui.SearchPanel;
import net.scholnick.lbdb.gui.author.AuthorMaintenance;
import net.scholnick.lbdb.gui.title.TitleMaintenance;
import net.scholnick.lbdb.service.AuthorService;
import net.scholnick.lbdb.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static net.scholnick.lbdb.util.GUIUtilities.center;
import static net.scholnick.lbdb.util.GUIUtilities.showMessageDialog;

@Component
public final class BooksDB extends JFrame {
	private JTabbedPane tabbedPane;

	private final SearchPanel       searchPanel;
	private final TitleMaintenance  titleMaintenance;
	private final AuthorMaintenance authorMaintenance;
	private final BookService       bookService;
	private final AuthorService     authorService;

	private static final Dimension WINDOW_SIZE = new Dimension(900, 600);

	private static final String VERSION = "Version 4.5.0";

	private JLabel notificationLabel;

    @Autowired
	public BooksDB(SearchPanel searchPanel, TitleMaintenance titleMaintenance, AuthorMaintenance authorMaintenance, BookService bookService, AuthorService authorService) {
		super("Laurel's Books Database");
		this.searchPanel       = searchPanel;
		this.titleMaintenance  = titleMaintenance;
		this.authorMaintenance = authorMaintenance;
		this.bookService       = bookService;
		this.authorService     = authorService;

		this.titleMaintenance.setMessageListener(text -> getNotificationLabel().setText(text));
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
		p.add(getNotificationLabel());
		getContentPane().add(p, BorderLayout.SOUTH);
		setVisible(true);
	}

    private JLabel getNotificationLabel() {
        if (notificationLabel == null) {
            notificationLabel = new JLabel("Laurel's Book Database");
            notificationLabel.setHorizontalAlignment(JLabel.CENTER);
        }
        return notificationLabel;
    }

    private void loadInfoInBackground() {
		new SwingWorker<Object,Boolean>() {
			@Override protected Boolean doInBackground() {
			setIconImage(new ImageIcon(getClass().getResource("/images/bookcase.gif")).getImage());
			updateTitle();
			return Boolean.TRUE;
			}
		}.execute();
	}

	private JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane();
			tabbedPane.setTabPlacement(SwingConstants.TOP);
			tabbedPane.addTab("Search",searchPanel);
			tabbedPane.addTab("Title", titleMaintenance);
			tabbedPane.addTab("Author", authorMaintenance);

			tabbedPane.addChangeListener(e -> {
				JTabbedPane tp = (JTabbedPane) e.getSource();
				if (tp.getSelectedIndex() == 0) { // back to the search tab, so update the title
					updateTitle();
				}
				getNotificationLabel().setText("");
			});
		}
		return tabbedPane;
	}

	private void updateTitle() {
		setTitle("Laurel's Book Database : " + bookService.count() + " books / " + authorService.count() + " authors");
	}

	/** creates the menus */
	private void buildMenus() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(getTitleMenu());
        menuBar.add(getGoMenu());
		menuBar.add(getEditMenu());
		setJMenuBar(menuBar);

		Desktop.getDesktop().setAboutHandler(e -> showMessageDialog(VERSION,"About"));
	}

	/** returns the standard edit menu */
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

	/** returns the title menu */
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

		JMenuItem addAuthorItem = new JMenuItem("Add Author");
		addAuthorItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.META_DOWN_MASK));
		addAuthorItem.setText("Add Author");
		addAuthorItem.addActionListener(l -> titleMaintenance.performQuickSearch());
		fileMenu.add(addAuthorItem);

		return fileMenu;
	}

	/** returns the go menu */
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
}
