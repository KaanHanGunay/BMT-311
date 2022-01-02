package tr.edu.gazi.kutuphane.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.edu.gazi.kutuphane.domain.Book;
import tr.edu.gazi.kutuphane.domain.request.BookSearch;
import tr.edu.gazi.kutuphane.repository.BookRepository;
import tr.edu.gazi.kutuphane.service.BookService;

/**
 * Service Implementation for managing {@link Book}.
 */
@Service
@Transactional
public class BookServiceImpl implements BookService {

    private final Logger log = LoggerFactory.getLogger(BookServiceImpl.class);

    private final BookRepository bookRepository;

    private final EntityManager entityManager;

    public BookServiceImpl(BookRepository bookRepository, EntityManager entityManager) {
        this.bookRepository = bookRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Book save(Book book) {
        log.debug("Request to save Book : {}", book);
        return bookRepository.save(book);
    }

    @Override
    public Optional<Book> partialUpdate(Book book) {
        log.debug("Request to partially update Book : {}", book);

        return bookRepository
            .findById(book.getId())
            .map(existingBook -> {
                if (book.getTitle() != null) {
                    existingBook.setTitle(book.getTitle());
                }
                if (book.getNumOfPage() != null) {
                    existingBook.setNumOfPage(book.getNumOfPage());
                }
                if (book.getCoverUrl() != null) {
                    existingBook.setCoverUrl(book.getCoverUrl());
                }
                if (book.getPublisher() != null) {
                    existingBook.setPublisher(book.getPublisher());
                }

                return existingBook;
            })
            .map(bookRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findAll() {
        log.debug("Request to get all Books");
        return bookRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> findOne(Long id) {
        log.debug("Request to get Book : {}", id);
        return bookRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Book : {}", id);
        bookRepository.deleteById(id);
    }

    @Override
    public List<Book> searchBook(BookSearch params) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> from = cq.from(Book.class);

        from.fetch("author");

        List<Predicate> predicateList = new ArrayList<>();

        if (params.getBookTitle() != null) {
            predicateList.add(cb.like(from.get("title"), "%" + params.getBookTitle() + "%"));
        }

        if (params.getAuthorName() != null) {
            predicateList.add(cb.like(from.get("author").get("name"), "%" + params.getAuthorName() + "%"));
        }

        if (params.getAuthorSurname() != null) {
            predicateList.add(cb.like(from.get("author").get("surname"), "%" + params.getAuthorSurname() + "%"));
        }

        cq.where(predicateList.toArray(new Predicate[] {}));
        TypedQuery<Book> query = entityManager.createQuery(cq);

        return query.getResultList();
    }
}
