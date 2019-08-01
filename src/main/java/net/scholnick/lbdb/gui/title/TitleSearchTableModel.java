package net.scholnick.lbdb.gui.title;


import net.scholnick.lbdb.domain.Book;
import net.scholnick.lbdb.domain.Media;
import net.scholnick.lbdb.util.NullSafe;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Steve Scholnick <steve@scholnick.net>
 */
public final class TitleSearchTableModel extends AbstractTableModel {
	private final List<Book> dataRows;
	private int              sortedColumn;
	private Direction[]      sortingDirections;

	/** Constructor */
	public TitleSearchTableModel() {
		dataRows = new ArrayList<Book>();
		setSortingDefaults();
	}

	private void setSortingDefaults() {
		sortedColumn = 0;

		sortingDirections = new Direction[columnNames.length];
		for (int i = 0; i < sortingDirections.length; i++) {
			sortingDirections[i] = Direction.ASCENDING;
		}
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
		switch (c) {
			case 2:
				return Media.class;
			case 4:
				return Boolean.class;
			default:
				return String.class;
		}
	}

	@Override
	public Object getValueAt(int row, int col) {
		Book data = getTitleData(row);

		switch (col) {
			case 0:
				return data.getTitle();
			case 1:
				return data.getAuthorNames();
			case 2:
				return data.getMedia();
			case 3:
				return data.getSeries();
			case 4:
				return data.isAnthology() ? "Yes" : "No";
		}

		return null;
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

	public void sort(int column) {
		if (column == sortedColumn) {
			sortingDirections[column] = sortingDirections[column] == Direction.ASCENDING ? Direction.DESCENDING : Direction.ASCENDING;
		}

		//LogManager.debug(getClass(), "Selected column = " + column + ",Old column = " + sortedColumn + ",Direction = " + sortingDirections[column]);

		sortedColumn = column;
		Collections.sort(dataRows, new DataSorter(column));
		fireTableDataChanged();
	}

	private static final String[] columnNames = { "Title", "Authors", "Media", "Series", "Anthology" };

	private enum Direction {
		ASCENDING, DESCENDING
	};

	private final class DataSorter implements Comparator<Book> {
		private final int column;

		DataSorter(int column) {
			this.column = column;
		}

		@Override
		public int compare(Book o1, Book o2) {
			int cmp = 0;

			switch (column) {
				case 0:
					cmp = o1.compareTo(o2);
					break;

				case 1:
					cmp = o1.getAuthorNames().compareTo(o2.getAuthorNames());
					break;

				case 2:
					cmp = NullSafe.compare(o1.getMedia(), o2.getMedia());
					break;

				case 3:
					cmp = NullSafe.compare(o1.getSeries(), o2.getSeries());
					break;

				case 4:
					cmp = NullSafe.compare(o1.isAnthology(), o2.isAnthology());
					break;
			}

			if (sortingDirections[column] == Direction.DESCENDING) {
				cmp *= -1;
			}

			if (cmp == 0) {
				cmp = o1.compareTo(o2);
			}

			return cmp;
		}
	}

	private static final long serialVersionUID = 6499478587310181950L;
}
