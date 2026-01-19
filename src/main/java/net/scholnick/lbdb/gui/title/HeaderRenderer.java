package net.scholnick.lbdb.gui.title;

import net.scholnick.lbdb.BooksDB;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.Component;

/**
 * HeaderRenderer for JTable headers in the Title List table.
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */
final class HeaderRenderer implements TableCellRenderer {
    private final DefaultTableCellRenderer renderer;

    HeaderRenderer(JTable table) {
        renderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
        renderer.setHorizontalAlignment(JLabel.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        Component c = renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        c.setForeground(BooksDB.FOREGROUND_COLOR);
        c.setBackground(BooksDB.BACKGROUND_COLOR);
        return c;
    }
}
