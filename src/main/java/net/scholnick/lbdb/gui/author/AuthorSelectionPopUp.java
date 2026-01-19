package net.scholnick.lbdb.gui.author;

import net.scholnick.lbdb.domain.Author;
import net.scholnick.lbdb.gui.BaseDialog;
import net.scholnick.lbdb.util.GUIUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * AuthorSelectionPopUp is a dialog that allows the user to select an author from a list.
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */
public final class AuthorSelectionPopUp extends BaseDialog {
    private JTable listingTable;

    public AuthorSelectionPopUp(List<Author> authors) {
        super();
        setTitle("Author Selection");
        setModal(true);
        setSize(300, 300);
        GUIUtilities.center(this);
        buildGUI();

        getOKButton().setEnabled(true);
        getOKButton().setName("Select");

        addAuthors(authors);
    }

    @Override
    protected void buildGUI() {
        getContentPane().setLayout(new BorderLayout());

        JScrollPane pane = new JScrollPane(getListingTable());
        pane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        getContentPane().add(pane, BorderLayout.CENTER);
        getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);
    }

    /** Lazy initialization of the listing table. */
    private JTable getListingTable() {
        if (listingTable == null) {
            listingTable = new JTable(new AuthorTableModel());

            listingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            listingTable.setCellSelectionEnabled(false);
            listingTable.setRowSelectionAllowed(true);
            listingTable.getTableHeader().setReorderingAllowed(false);
            listingTable.getTableHeader().setUI(null);

            listingTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    if (event.getClickCount() == 1)
                        getOKButton().setEnabled(true);
                    else if (event.getClickCount() == 2)
                        ok();
                }
            });
        }
        return listingTable;
    }

    /** Populate the listing table with authors. */
    private void addAuthors(List<Author> authors) {
        getListingTable().clearSelection();

        AuthorTableModel model = (AuthorTableModel) getListingTable().getModel();

        model.clear();
        authors.forEach(model::add);
        validate();
        repaint();
    }

    /** Get the selected author from the listing table. */
    public Author getSelectedAuthor() {
        int row = getListingTable().getSelectedRow();

        if (row == -1) {
            return null;
        }

        return ((AuthorTableModel) getListingTable().getModel()).get(row);
    }

    @Override
    protected JComponent getInitialFocusComponent() {
        return getListingTable();
    }

    @Override
    protected JPanel getInputPanel() {
        return new JPanel();
    }
}
