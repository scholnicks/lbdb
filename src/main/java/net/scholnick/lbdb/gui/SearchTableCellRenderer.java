package net.scholnick.lbdb.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public final class SearchTableCellRenderer extends DefaultTableCellRenderer {
	private static final SearchTableCellRenderer INSTANCE = new SearchTableCellRenderer();

	private static final TableCellRenderer FIRST_COLUMN_RENDERER = new DefaultTableCellRenderer();

	public static SearchTableCellRenderer getInstance() {
		return INSTANCE;
	}

	private SearchTableCellRenderer() {
		setHorizontalAlignment(SwingConstants.CENTER);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (column == 0) return FIRST_COLUMN_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
}
