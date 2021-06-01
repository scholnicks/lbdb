package net.scholnick.lbdb.gui.title;

import net.scholnick.lbdb.domain.*;
import net.scholnick.lbdb.util.NullSafe;

import javax.swing.table.AbstractTableModel;
import java.util.*;

public final class TitleSearchTableModel extends AbstractTableModel {
    private final List<Book> dataRows;
    private int sortedColumn;
    private Direction[] sortingDirections;

    TitleSearchTableModel() {
        dataRows = new ArrayList<>();
        setSortingDefaults();
    }

    private void setSortingDefaults() {
        sortedColumn = 0;
        sortingDirections = new Direction[columnNames.length];
        Arrays.fill(sortingDirections, Direction.ASCENDING);
    }

    @Override
    public int getRowCount() {
        return dataRows.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int c) {
        return columnNames[c];
    }

    @Override
    public Class<?> getColumnClass(int c) {
        return switch (c) {
            case 2 -> Media.class;
            case 4 -> Boolean.class;
            default -> String.class;
        };
    }

    @Override
    public Object getValueAt(int row, int col) {
        Book data = getTitleData(row);

        return switch (col) {
            case 0 -> data.getTitle();
            case 1 -> data.getAuthorNames();
            case 2 -> data.getMedia();
            case 3 -> data.getSeries();
            case 4 -> data.isAnthology() ? "Yes" : "No";
            default -> null;
        };
    }

    public Book getTitleData(int row) {
        return dataRows.get(row);
    }

    public void clear() {
        dataRows.clear();
        setSortingDefaults();
        fireTableDataChanged();
    }

    public void addRow(Book data) {
        dataRows.add(data);
        fireTableDataChanged();
    }

    void sort(int column) {
        if (column == sortedColumn) {
            sortingDirections[column] = sortingDirections[column] == Direction.ASCENDING ? Direction.DESCENDING : Direction.ASCENDING;
        }

        sortedColumn = column;
        dataRows.sort(new DataSorter(column));
        fireTableDataChanged();
    }

    private static final String[] columnNames = {"Title", "Authors", "Media", "Series", "Anthology"};

    private enum Direction {
        ASCENDING, DESCENDING
    }

    private final class DataSorter implements Comparator<Book> {
        private final int column;

        DataSorter(int column) {
            this.column = column;
        }

        @Override
        public int compare(Book o1, Book o2) {
            int cmp = switch (column) {
                case 0 -> o1.compareTo(o2);
                case 1 -> o1.getAuthorNames().compareTo(o2.getAuthorNames());
                case 2 -> NullSafe.compare(o1.getMedia(), o2.getMedia());
                case 3 -> NullSafe.compare(o1.getSeries(), o2.getSeries());
                case 4 -> NullSafe.compare(o1.isAnthology(), o2.isAnthology());
                default -> 0;
            };

            if (sortingDirections[column] == Direction.DESCENDING) {
                cmp *= -1;
            }

            if (cmp == 0) {
                cmp = o1.compareTo(o2);
            }

            return cmp;
        }
    }
}
