package tr.edu.gazi.kutuphanem.service;

import java.util.List;
import java.util.Optional;
import tr.edu.gazi.kutuphanem.service.dto.BorrowingDTO;

/**
 * Service Interface for managing {@link tr.edu.gazi.kutuphanem.domain.Borrowing}.
 */
public interface BorrowingService {
    /**
     * Save a borrowing.
     *
     * @param borrowingDTO the entity to save.
     * @return the persisted entity.
     */
    BorrowingDTO save(BorrowingDTO borrowingDTO);

    /**
     * Partially updates a borrowing.
     *
     * @param borrowingDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<BorrowingDTO> partialUpdate(BorrowingDTO borrowingDTO);

    /**
     * Get all the borrowings.
     *
     * @return the list of entities.
     */
    List<BorrowingDTO> findAll();

    /**
     * Get the "id" borrowing.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<BorrowingDTO> findOne(Long id);

    /**
     * Delete the "id" borrowing.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
