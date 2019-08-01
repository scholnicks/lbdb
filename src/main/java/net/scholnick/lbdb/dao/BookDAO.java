package net.scholnick.lbdb.dao;

import net.scholnick.lbdb.domain.Book;

import java.util.List;

public interface BookDAO {
	List<Book> search(Book b);
	Long create(Book b);
	void update(Book b);
	int delete(Book b);
	int removeJoinRecords(Book b);
	void addJoinRecords(Book b);
	Book get(Long id);
	Long count();
}
