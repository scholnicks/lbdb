package net.scholnick.lbdb.service;

import net.scholnick.lbdb.dao.AuthorDAO;
import net.scholnick.lbdb.domain.Author;
import net.scholnick.lbdb.domain.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthorServiceImpl implements AuthorService {
    private static final Logger log = LoggerFactory.getLogger(AuthorServiceImpl.class);
    
	private final AuthorDAO authorDAO;

	@Autowired
	public AuthorServiceImpl(AuthorDAO authorDAO) {
		this.authorDAO = authorDAO;
	}

	@Override
	@Transactional(readOnly=true)
	public Author get(Long id) {
		return authorDAO.get(id);
	}

	@Override
	@Transactional(readOnly=true)
	public Long count() {
		return authorDAO.count();
	}

	@Override
	@Transactional
	public void delete(Author a) {
		log.info("Deleting author : " + a);
		authorDAO.delete(a);
	}

	@Override
	@Transactional(readOnly=true)
	public List<Author> get(Book b) {
		log.debug("Returning authors for book " + b);
		return authorDAO.get(b);
	}

	@Override
	@Transactional(readOnly=true)
	public List<Author> search(String criteria) {
		return authorDAO.search(criteria);
	}

	@Override
	@Transactional
	public Author save(Author a, boolean createOnly) {
		log.debug("Saving author information " + a);
		return a.isNew() ? create(a) : createOnly ? a : update(a);
	}
	
	private Author create(Author a) {
		log.info("Creating new author : " + a);
		Long id = authorDAO.create(a);
		a.setId(id);
		
		return a;
	}

	private Author update(Author a) {
		log.info("Updating existsing author : " + a);
		authorDAO.update(a);
		return get(a.getId());
	}
}
