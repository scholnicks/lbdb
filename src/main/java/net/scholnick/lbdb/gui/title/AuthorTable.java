package net.scholnick.lbdb.gui.title;

import net.scholnick.lbdb.domain.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * AuthorTable is a JTable that displays a list of Authors with the ability to add and remove them.
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */
final class AuthorTable extends JTable {
    static final Dimension SIZE = new Dimension(325,55);

    public AuthorTable() {
        super();
        setModel(new AuthorTableModel());
        this.initialize();
    }

    /** Initializes the table with custom settings, including selection mode, cell renderers, and mouse listeners. */
    private void initialize() {
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setCellSelectionEnabled(false);
        setRowSelectionAllowed(true);
        setBorder(BorderFactory.createLineBorder(Color.white));

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
            @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setFont(new Font(label.getFont().getName(),Font.BOLD,label.getFont().getSize()+2));
                label.setForeground(Color.RED);
                label.setToolTipText("Remove author");
                return label;
            }
        });

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                ((AuthorTableModel) getModel()).remove(getSelectedRow());
            }
        });
    }

    /** Adds an Author to the table. */
    void add(Author a) {
        ((AuthorTableModel) getModel()).add(a);
    }

    /** Retrieves a list of Authors currently in the table. */
    List<Author> get() {
        return List.copyOf(((AuthorTableModel) getModel()).dataRows);
    }

    /** Clears all Authors from the table. */
    void clear() {
        ((AuthorTableModel) getModel()).clear();
    }

    /** Custom TableModel for managing Author data in the table. */
    private static final class AuthorTableModel extends AbstractTableModel {
        private final List<Author> dataRows;

        AuthorTableModel() {
            dataRows = new LinkedList<>();
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

        /** Clears all data from the table model. */
        void clear() {
            dataRows.clear();
            fireTableDataChanged();
        }

        /** Adds an Author to the table model. */
        void add(Author data) {
            dataRows.add(data);
            fireTableDataChanged();
        }

        /** Removes an Author from the table model at the specified row. */
        void remove (int row) {
            dataRows.remove(row);
            fireTableDataChanged();
        }

        private static final String[] columnNames = {"Name", ""};
    }

    /** Custom renderer for the table header to align text to the left. */
    private static final class AuthorHeaderRenderer implements TableCellRenderer {
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
