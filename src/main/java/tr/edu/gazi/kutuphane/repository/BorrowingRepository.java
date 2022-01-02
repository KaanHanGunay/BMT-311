package tr.edu.gazi.kutuphane.repository;

import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import tr.edu.gazi.kutuphane.domain.Borrowing;

/**
 * Spring Data SQL repository for the Borrowing entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, Long>, JpaSpecificationExecutor<Borrowing> {
    @Query("select borrowing from Borrowing borrowing where borrowing.user.login = ?#{principal.username}")
    List<Borrowing> findByUserIsCurrentUser();
}
