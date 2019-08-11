package net.scholnick.lbdb.dao;

import net.scholnick.lbdb.domain.Author;
import net.scholnick.lbdb.domain.Book;

import java.util.List;
import java.util.Set;

public interface AuthorDAO {
	Long create(Author a);
	void delete(Author a);
	void update(Author a);
	List<Author> get(Book b);
	Set<Long> getEditors(Book b);
	List<Author> search(String criteria);
	Author get(Long id);
	Long count();
}
