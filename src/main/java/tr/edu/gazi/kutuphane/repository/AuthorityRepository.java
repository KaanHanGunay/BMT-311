package tr.edu.gazi.kutuphane.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tr.edu.gazi.kutuphane.domain.Authority;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {}
