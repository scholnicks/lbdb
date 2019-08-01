package net.scholnick.lbdb.service;


import net.scholnick.lbdb.domain.Author;
import net.scholnick.lbdb.domain.Book;

import java.util.List;

public interface AuthorService {
	List<Author> get(Book b);
	List<Author> search(Author a);
	
	Author get(Long id);
	void delete(Author a);
	Author save(Author a, boolean createOnly);
	
	Long count();
}
