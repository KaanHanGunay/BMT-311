package tr.edu.gazi.kutuphanem.service;

import java.util.List;
import java.util.Optional;
import tr.edu.gazi.kutuphanem.service.dto.PublisherDTO;

/**
 * Service Interface for managing {@link tr.edu.gazi.kutuphanem.domain.Publisher}.
 */
public interface PublisherService {
    /**
     * Save a publisher.
     *
     * @param publisherDTO the entity to save.
     * @return the persisted entity.
     */
    PublisherDTO save(PublisherDTO publisherDTO);

    /**
     * Partially updates a publisher.
     *
     * @param publisherDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PublisherDTO> partialUpdate(PublisherDTO publisherDTO);

    /**
     * Get all the publishers.
     *
     * @return the list of entities.
     */
    List<PublisherDTO> findAll();

    /**
     * Get the "id" publisher.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PublisherDTO> findOne(Long id);

    /**
     * Delete the "id" publisher.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
