package net.scholnick.lbdb.gui.title;

import net.scholnick.lbdb.domain.Author;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

final class AuthorTable extends JTable {
    static final Dimension SIZE = new Dimension(325,55);

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
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        getTableHeader().setDefaultRenderer(new AuthorHeaderRenderer(this));
        getColumnModel().getColumn(0).setPreferredWidth(300);
        getColumnModel().getColumn(1).setPreferredWidth(25);
        setPreferredScrollableViewportSize(SIZE);
        getTableHeader().setPreferredSize(new Dimension(SIZE.width,30));

        getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public int getHorizontalAlignment() {
                return JLabel.LEFT;
            }
        });

        getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public int getHorizontalAlignment() {
                return JLabel.CENTER;
            }
        });
    }

    void add(Author a) {
        ((AuthorTableModel) getModel()).addRow(a);
        ((AuthorTableModel) getModel()).fireTableDataChanged();
    }

    List<Author> get() {
        return List.copyOf(((AuthorTableModel) getModel()).dataRows);
    }

    void clear() {
        ((AuthorTableModel) getModel()).clear();
        ((AuthorTableModel) getModel()).fireTableDataChanged();
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
            return col == 0 ? dataRows.get(row).getName() : "x";
        }

        public void clear() {
            dataRows.clear();
        }

        public void addRow(Author data) {
            dataRows.add(data);
        }

        private static final String[] columnNames = {"Name", ""};
    }

    static final class AuthorHeaderRenderer implements TableCellRenderer {
        private final DefaultTableCellRenderer renderer;

        AuthorHeaderRenderer(JTable table) {
            renderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
            renderer.setHorizontalAlignment(JLabel.LEFT);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            return renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        }
    }

}
