package net.scholnick.lbdb.gui.title;

import net.scholnick.lbdb.domain.Author;

import javax.swing.table.AbstractTableModel;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

final class AuthorTableModel extends AbstractTableModel {
	private final List<Author> dataRows;

	AuthorTableModel() {
		dataRows = new LinkedList<>();
	}

	Set<Author> getEditors() {
		return dataRows.stream().filter(Author::isEditor).collect(toSet());
	}
	
	boolean contains(Author a) {
		return dataRows.contains(a);
	}
	
	@Override
	public int getRowCount() {
		return dataRows.size();
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 2;
	}

	@Override
	public String getColumnName(int col) {
		switch (col) {
			case 0:
				return "Last";
			case 1:
				return "First";
			case 2:
				return "Editor?";
		}
		return null;
	}

	@Override
	public Class<?> getColumnClass(int c) {
		return (c < 2) ? String.class : Boolean.class;
	}

	@Override
	public Object getValueAt(int row, int col) {
		Author data = dataRows.get(row);
		switch (col) {
			case 0:
				return data.getLastName();
			case 1:
				return data.getFirstName();
			case 2:
				return data.isEditor();
		}
		return null;
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if (columnIndex == 2) {
			dataRows.get(rowIndex).setEditor((Boolean) value);
		}
		fireTableDataChanged();
	}

	public void delete(int row) {
		dataRows.remove(row);
		fireTableDataChanged();
	}

	public void add(Author a) {
		dataRows.add(a);
		fireTableDataChanged();
	}

	public Author get(int row) {
		return dataRows.get(row);
	}

	int size() {
		return dataRows.size();
	}

	public void clear() {
		dataRows.clear();
		fireTableDataChanged();
	}
}