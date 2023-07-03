package net.scholnick.lbdb.gui.title;

import net.scholnick.lbdb.domain.*;
import net.scholnick.lbdb.util.*;

import javax.swing.*;
import javax.swing.table.*;

import java.awt.*;
import java.util.*;
import java.util.List;

import static net.scholnick.lbdb.gui.title.TitleMaintenance.TEXT_FIELD_SIZE;

final class AuthorPanel extends JPanel {
    private final String label;
    private final JTextField selectField;
    private final AuthorTable dataTable;

    public AuthorPanel(String label) {
        this.label = label;

        this.selectField = new JTextField(15);
        GUIUtilities.setSizes(selectField,TEXT_FIELD_SIZE);

        dataTable = new AuthorTable();
//        GUIUtilities.setSizes(this,new Dimension(400,100));

        buildGUI();
    }

    private void buildGUI() {
        setLayout(new BorderLayout());
        add(GUIUtilities.panel(new JLabel(label)),BorderLayout.WEST);

        JPanel center = new JPanel(new BorderLayout());
        center.add(selectField,BorderLayout.NORTH);
        center.add(new JScrollPane(dataTable),BorderLayout.CENTER);
        add(center,BorderLayout.CENTER);
    }

    private static final class AuthorTable extends JTable {
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
