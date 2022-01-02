package tr.edu.gazi.kutuphane.service;

import java.util.List;
import java.util.Optional;
import tr.edu.gazi.kutuphane.domain.Author;

/**
 * Service Interface for managing {@link Author}.
 */
public interface AuthorService {
    /**
     * Save a author.
     *
     * @param author the entity to save.
     * @return the persisted entity.
     */
    Author save(Author author);

    /**
     * Partially updates a author.
     *
     * @param author the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Author> partialUpdate(Author author);

    /**
     * Get all the authors.
     *
     * @return the list of entities.
     */
    List<Author> findAll();

    /**
     * Get the "id" author.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Author> findOne(Long id);

    /**
     * Delete the "id" author.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
