package net.scholnick.lbdb.gui.title;

import net.scholnick.lbdb.domain.Author;

import javax.swing.table.AbstractTableModel;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

/**
 * AuthorTableModel is the TableModel for authors in the Title edit dialog.
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */
final class AuthorTableModel extends AbstractTableModel {
    private final List<Author> dataRows;

    AuthorTableModel() {
        dataRows = new LinkedList<>();
    }

    /** Get the set of authors marked as editors. */
    Set<Author> getEditors() {
        return dataRows.stream().filter(Author::isEditor).collect(toSet());
    }

    /** Check if the model contains the given author. */
    boolean contains(Author a) {
        return dataRows.contains(a);
    }

    @Override
    public int getRowCount() {
        return dataRows.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public String getColumnName(int col) {
        return switch (col) {
            case 0 -> "Name";
            case 1 -> "Editor?";
            default -> null;
        };
    }

    @Override
    public Class<?> getColumnClass(int c) {
        return (c != 1) ? String.class : Boolean.class;
    }

    @Override
    public Object getValueAt(int row, int col) {
        Author data = dataRows.get(row);
        if (col == 0) {
            return data.getName();
        }
        else if (col == 1) {
            return data.isEditor();
        }
        return null;
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (columnIndex == 1) {
            dataRows.get(rowIndex).setEditor((Boolean) value);
        }
        fireTableDataChanged();
    }

    /** Delete the author at the given row. */
    public void delete(int row) {
        dataRows.remove(row);
        fireTableDataChanged();
    }

    /** Add the given author. */
    public void add(Author a) {
        dataRows.add(a);
        fireTableDataChanged();
    }

    /** Get the author at the given row. */
    public Author get(int row) {
        return dataRows.get(row);
    }

    /** Get the number of authors in the model. */
    public int size() {
        return dataRows.size();
    }

    /** Get a stream of the authors in the model. */
    public Stream<Author> stream() {
        return dataRows.stream();
    }

    /** Clear all authors from the model. */
    public void clear() {
        dataRows.clear();
        fireTableDataChanged();
    }
}