package net.scholnick.lbdb.coverphoto;

import net.scholnick.lbdb.domain.Book;

import java.io.IOException;

@FunctionalInterface
public interface GoogleService {
	void setCoverPhoto(Book book) throws IOException;
}