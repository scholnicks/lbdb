package net.scholnick.lbdb.gui.title;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.Component;

@Deprecated
final class HeaderRenderer implements TableCellRenderer {
    private final DefaultTableCellRenderer renderer;

    HeaderRenderer(JTable table) {
        renderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
        renderer.setHorizontalAlignment(JLabel.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        return renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
    }
}
