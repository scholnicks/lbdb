package net.scholnick.lbdb.gui.author;


import net.scholnick.lbdb.domain.Author;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

class AuthorTableModel extends AbstractTableModel {
	private final List<Author> dataRows;

	AuthorTableModel() {
		dataRows = new ArrayList<Author>();
	}

	public List<Author> getAllAuthors() {
		return dataRows;
	}

	/** {@inheritDoc} */
	@Override
	public int getRowCount() {
		return dataRows.size();
	}

	/** {@inheritDoc} */
	@Override
	public int getColumnCount() {
		return 2;
	}

	/** {@inheritDoc} */
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

	/** {@inheritDoc} */
	@Override
	public Class<?> getColumnClass(int c) {
		return String.class;
	}

	/** {@inheritDoc} */
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

	private static final long serialVersionUID = -8251950494914559534L;
}