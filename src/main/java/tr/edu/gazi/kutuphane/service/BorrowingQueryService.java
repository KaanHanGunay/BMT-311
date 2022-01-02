package tr.edu.gazi.kutuphane.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;
import tr.edu.gazi.kutuphane.domain.*; // for static metamodels
import tr.edu.gazi.kutuphane.domain.Borrowing;
import tr.edu.gazi.kutuphane.repository.BorrowingRepository;
import tr.edu.gazi.kutuphane.service.criteria.BorrowingCriteria;

/**
 * Service for executing complex queries for {@link Borrowing} entities in the database.
 * The main input is a {@link BorrowingCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Borrowing} or a {@link Page} of {@link Borrowing} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BorrowingQueryService extends QueryService<Borrowing> {

    private final Logger log = LoggerFactory.getLogger(BorrowingQueryService.class);

    private final BorrowingRepository borrowingRepository;

    public BorrowingQueryService(BorrowingRepository borrowingRepository) {
        this.borrowingRepository = borrowingRepository;
    }

    /**
     * Return a {@link List} of {@link Borrowing} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Borrowing> findByCriteria(BorrowingCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Borrowing> specification = createSpecification(criteria);
        return borrowingRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Borrowing} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Borrowing> findByCriteria(BorrowingCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Borrowing> specification = createSpecification(criteria);
        return borrowingRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BorrowingCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Borrowing> specification = createSpecification(criteria);
        return borrowingRepository.count(specification);
    }

    /**
     * Function to convert {@link BorrowingCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Borrowing> createSpecification(BorrowingCriteria criteria) {
        Specification<Borrowing> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Borrowing_.id));
            }
            if (criteria.getBorrowingDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getBorrowingDate(), Borrowing_.borrowingDate));
            }
            if (criteria.getDeliveryDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDeliveryDate(), Borrowing_.deliveryDate));
            }
            if (criteria.getUserId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserId(), root -> root.join(Borrowing_.user, JoinType.LEFT).get(User_.id))
                    );
            }
            if (criteria.getBookId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getBookId(), root -> root.join(Borrowing_.book, JoinType.LEFT).get(Book_.id))
                    );
            }
        }
        return specification;
    }
}
