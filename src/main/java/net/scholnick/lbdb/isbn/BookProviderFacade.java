package net.scholnick.lbdb.isbn;

import net.scholnick.lbdb.domain.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class BookProviderFacade implements BookProvider {
    private final GoogleBooks googleBooks;

    @Autowired
    public BookProviderFacade(GoogleBooks googleBooks) {
        this.googleBooks = googleBooks;
    }

    @Override
    public Book search(String isbn) {
        return googleBooks.search(isbn);
    }
}
