package tr.edu.gazi.kutuphane.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.edu.gazi.kutuphane.domain.Borrowing;
import tr.edu.gazi.kutuphane.domain.User;
import tr.edu.gazi.kutuphane.repository.BookRepository;
import tr.edu.gazi.kutuphane.repository.BorrowingRepository;
import tr.edu.gazi.kutuphane.repository.UserRepository;
import tr.edu.gazi.kutuphane.service.BorrowingService;

/**
 * Service Implementation for managing {@link Borrowing}.
 */
@Service
@Transactional
public class BorrowingServiceImpl implements BorrowingService {

    private final Logger log = LoggerFactory.getLogger(BorrowingServiceImpl.class);

    private final BorrowingRepository borrowingRepository;

    private final BookRepository bookRepository;

    private final UserRepository userRepository;

    public BorrowingServiceImpl(BorrowingRepository borrowingRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.borrowingRepository = borrowingRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Borrowing save(Borrowing borrowing) {
        log.debug("Request to save Borrowing : {}", borrowing);
        return borrowingRepository.save(borrowing);
    }

    @Override
    public Optional<Borrowing> partialUpdate(Borrowing borrowing) {
        log.debug("Request to partially update Borrowing : {}", borrowing);

        return borrowingRepository
            .findById(borrowing.getId())
            .map(existingBorrowing -> {
                if (borrowing.getBorrowingDate() != null) {
                    existingBorrowing.setBorrowingDate(borrowing.getBorrowingDate());
                }
                if (borrowing.getDeliveryDate() != null) {
                    existingBorrowing.setDeliveryDate(borrowing.getDeliveryDate());
                }
                if (borrowing.getComment() != null) {
                    existingBorrowing.setComment(borrowing.getComment());
                }

                return existingBorrowing;
            })
            .map(borrowingRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Borrowing> findAll() {
        log.debug("Request to get all Borrowings");
        return borrowingRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Borrowing> findOne(Long id) {
        log.debug("Request to get Borrowing : {}", id);
        return borrowingRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Borrowing : {}", id);
        borrowingRepository.deleteById(id);
    }

    @Override
    public Borrowing borrow(Long bookId) {
        Borrowing borrowing = new Borrowing();
        borrowing.setBorrowingDate(LocalDate.now());
        Optional<User> user = userRepository.findOneByLogin(SecurityContextHolder.getContext().getAuthentication().getName());

        if (user.isEmpty()) {
            throw new RuntimeException("Kullanıcı bulunamadı");
        }

        user.ifPresent(u -> {
            borrowing.setBook(bookRepository.getById(bookId));
            borrowing.setUser(u);
        });

        return borrowingRepository.save(borrowing);
    }

    @Override
    public List<Borrowing> getBorrowings() {
        return borrowingRepository.findByUserIsCurrentUser();
    }
}
