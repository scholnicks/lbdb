package net.scholnick.lbdb.gui.title;

import net.scholnick.lbdb.domain.Author;

import javax.swing.*;
import javax.swing.table.*;
import java.util.*;

final class AuthorTable extends JTable {
    public AuthorTable() {
        super();
        setModel(new AuthorTableModel());
        this.initialize();
    }

    private void initialize() {
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setCellSelectionEnabled(false);
        setRowSelectionAllowed(true);
        getTableHeader().setReorderingAllowed(false);
        getTableHeader().setDefaultRenderer(new HeaderRenderer(this));
        TableColumnModel columnModel = getColumnModel();
//            columnModel.getColumn(0).setPreferredWidth(300);
//            columnModel.getColumn(1).setPreferredWidth(25);
    }


    private static final class AuthorTableModel extends AbstractTableModel {
        private final List<Author> dataRows;

        AuthorTableModel() {
            dataRows = new ArrayList<>();
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
            return String.class;
        }

        @Override
        public Object getValueAt(int row, int col) {
            return getTitleData(row);
        }

        public Author getTitleData(int row) {
            return dataRows.get(row);
        }

        public void clear() {
            dataRows.clear();
        }

        public void addRow(Author data) {
            dataRows.add(data);
        }

        private static final String[] columnNames = {"Name", "Function"};
    }
}
