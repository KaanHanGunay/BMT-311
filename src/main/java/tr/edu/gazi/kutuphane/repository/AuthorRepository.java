package tr.edu.gazi.kutuphane.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import tr.edu.gazi.kutuphane.domain.Author;

/**
 * Spring Data SQL repository for the Author entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AuthorRepository extends JpaRepository<Author, Long>, JpaSpecificationExecutor<Author> {}
