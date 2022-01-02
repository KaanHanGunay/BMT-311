package tr.edu.gazi.kutuphane.service;

import java.util.List;
import java.util.Optional;
import tr.edu.gazi.kutuphane.domain.Borrowing;

/**
 * Service Interface for managing {@link Borrowing}.
 */
public interface BorrowingService {
    /**
     * Save a borrowing.
     *
     * @param borrowing the entity to save.
     * @return the persisted entity.
     */
    Borrowing save(Borrowing borrowing);

    /**
     * Partially updates a borrowing.
     *
     * @param borrowing the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Borrowing> partialUpdate(Borrowing borrowing);

    /**
     * Get all the borrowings.
     *
     * @return the list of entities.
     */
    List<Borrowing> findAll();

    /**
     * Get the "id" borrowing.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Borrowing> findOne(Long id);

    /**
     * Delete the "id" borrowing.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    Borrowing borrow(Long bookId);

    List<Borrowing> getBorrowings();
}
