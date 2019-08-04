package net.scholnick.lbdb.gui.author;

import net.scholnick.lbdb.domain.Author;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

class AuthorTableModel extends AbstractTableModel {
	private final List<Author> dataRows;

	AuthorTableModel() {
		dataRows = new ArrayList<>();
	}

	List<Author> getAllAuthors() {
		return dataRows;
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
	public String getColumnName(int col) {
		switch (col) {
			case 0:
				return "First";
			case 1:
				return "Last";
		}
		return null;
	}

	@Override
	public Class<?> getColumnClass(int c) {
		return String.class;
	}

	@Override
	public Object getValueAt(int row, int col) {
		Author data = get(row);
		switch (col) {
			case 0:
				return data.getFirstName();
			case 1:
				return data.getLastName();
		}
		return null;
	}

	public void add(Author data) {
		dataRows.add(data);
		fireTableDataChanged();
	}

	public Author get(int row) {
		return dataRows.get(row);
	}

	public void clear() {
		dataRows.clear();
		fireTableDataChanged();
	}
}