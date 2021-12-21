package tr.edu.gazi.kutuphanem.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;
import tr.edu.gazi.kutuphanem.repository.BorrowingRepository;
import tr.edu.gazi.kutuphanem.service.BorrowingQueryService;
import tr.edu.gazi.kutuphanem.service.BorrowingService;
import tr.edu.gazi.kutuphanem.service.criteria.BorrowingCriteria;
import tr.edu.gazi.kutuphanem.service.dto.BorrowingDTO;
import tr.edu.gazi.kutuphanem.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link tr.edu.gazi.kutuphanem.domain.Borrowing}.
 */
@RestController
@RequestMapping("/api")
public class BorrowingResource {

    private final Logger log = LoggerFactory.getLogger(BorrowingResource.class);

    private static final String ENTITY_NAME = "borrowing";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BorrowingService borrowingService;

    private final BorrowingRepository borrowingRepository;

    private final BorrowingQueryService borrowingQueryService;

    public BorrowingResource(
        BorrowingService borrowingService,
        BorrowingRepository borrowingRepository,
        BorrowingQueryService borrowingQueryService
    ) {
        this.borrowingService = borrowingService;
        this.borrowingRepository = borrowingRepository;
        this.borrowingQueryService = borrowingQueryService;
    }

    /**
     * {@code POST  /borrowings} : Create a new borrowing.
     *
     * @param borrowingDTO the borrowingDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new borrowingDTO, or with status {@code 400 (Bad Request)} if the borrowing has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/borrowings")
    public ResponseEntity<BorrowingDTO> createBorrowing(@Valid @RequestBody BorrowingDTO borrowingDTO) throws URISyntaxException {
        log.debug("REST request to save Borrowing : {}", borrowingDTO);
        if (borrowingDTO.getId() != null) {
            throw new BadRequestAlertException("A new borrowing cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BorrowingDTO result = borrowingService.save(borrowingDTO);
        return ResponseEntity
            .created(new URI("/api/borrowings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /borrowings/:id} : Updates an existing borrowing.
     *
     * @param id the id of the borrowingDTO to save.
     * @param borrowingDTO the borrowingDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated borrowingDTO,
     * or with status {@code 400 (Bad Request)} if the borrowingDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the borrowingDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/borrowings/{id}")
    public ResponseEntity<BorrowingDTO> updateBorrowing(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BorrowingDTO borrowingDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Borrowing : {}, {}", id, borrowingDTO);
        if (borrowingDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, borrowingDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!borrowingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        BorrowingDTO result = borrowingService.save(borrowingDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, borrowingDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /borrowings/:id} : Partial updates given fields of an existing borrowing, field will ignore if it is null
     *
     * @param id the id of the borrowingDTO to save.
     * @param borrowingDTO the borrowingDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated borrowingDTO,
     * or with status {@code 400 (Bad Request)} if the borrowingDTO is not valid,
     * or with status {@code 404 (Not Found)} if the borrowingDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the borrowingDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/borrowings/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BorrowingDTO> partialUpdateBorrowing(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BorrowingDTO borrowingDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Borrowing partially : {}, {}", id, borrowingDTO);
        if (borrowingDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, borrowingDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!borrowingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BorrowingDTO> result = borrowingService.partialUpdate(borrowingDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, borrowingDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /borrowings} : get all the borrowings.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of borrowings in body.
     */
    @GetMapping("/borrowings")
    public ResponseEntity<List<BorrowingDTO>> getAllBorrowings(BorrowingCriteria criteria) {
        log.debug("REST request to get Borrowings by criteria: {}", criteria);
        List<BorrowingDTO> entityList = borrowingQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /borrowings/count} : count all the borrowings.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/borrowings/count")
    public ResponseEntity<Long> countBorrowings(BorrowingCriteria criteria) {
        log.debug("REST request to count Borrowings by criteria: {}", criteria);
        return ResponseEntity.ok().body(borrowingQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /borrowings/:id} : get the "id" borrowing.
     *
     * @param id the id of the borrowingDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the borrowingDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/borrowings/{id}")
    public ResponseEntity<BorrowingDTO> getBorrowing(@PathVariable Long id) {
        log.debug("REST request to get Borrowing : {}", id);
        Optional<BorrowingDTO> borrowingDTO = borrowingService.findOne(id);
        return ResponseUtil.wrapOrNotFound(borrowingDTO);
    }

    /**
     * {@code DELETE  /borrowings/:id} : delete the "id" borrowing.
     *
     * @param id the id of the borrowingDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/borrowings/{id}")
    public ResponseEntity<Void> deleteBorrowing(@PathVariable Long id) {
        log.debug("REST request to delete Borrowing : {}", id);
        borrowingService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
