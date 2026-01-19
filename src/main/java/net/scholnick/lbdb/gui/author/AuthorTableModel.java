package net.scholnick.lbdb.gui.author;

import net.scholnick.lbdb.domain.Author;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * AuthorTableModel for displaying authors in a JTable.
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */
final class AuthorTableModel extends AbstractTableModel {
    private final List<Author> dataRows;

    AuthorTableModel() {
        dataRows = new ArrayList<>();
    }

    /** Get all authors in the table model. */
    List<Author> getAllAuthors() {
        return dataRows;
    }

    @Override
    public int getRowCount() {
        return dataRows.size();
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public String getColumnName(int col) {
        return "Name";
    }

    @Override
    public Class<?> getColumnClass(int c) {
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int col) {
        return get(row).getName();
    }

    /** Add an author to the table model. */
    public void add(Author data) {
        dataRows.add(data);
        fireTableDataChanged();
    }

    /** Get the author at the specified row. */
    public Author get(int row) {
        return dataRows.get(row);
    }

    /** Clear all authors from the table model. */
    public void clear() {
        dataRows.clear();
        fireTableDataChanged();
    }
}