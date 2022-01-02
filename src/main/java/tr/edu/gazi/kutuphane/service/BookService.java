package tr.edu.gazi.kutuphane.service;

import java.util.List;
import java.util.Optional;
import tr.edu.gazi.kutuphane.domain.Book;
import tr.edu.gazi.kutuphane.domain.request.BookSearch;

/**
 * Service Interface for managing {@link Book}.
 */
public interface BookService {
    /**
     * Save a book.
     *
     * @param book the entity to save.
     * @return the persisted entity.
     */
    Book save(Book book);

    /**
     * Partially updates a book.
     *
     * @param book the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Book> partialUpdate(Book book);

    /**
     * Get all the books.
     *
     * @return the list of entities.
     */
    List<Book> findAll();

    /**
     * Get the "id" book.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Book> findOne(Long id);

    /**
     * Delete the "id" book.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    List<Book> searchBook(BookSearch params);
}
