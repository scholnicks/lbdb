package net.scholnick.lbdb.service;

import net.scholnick.lbdb.domain.Book;

import java.util.List;


public interface BookService {
	List<Book> search(Book a);
	
	Book get(Long id);
	void delete(Book a);
	Book save(Book a);
	
	Long count();
}
