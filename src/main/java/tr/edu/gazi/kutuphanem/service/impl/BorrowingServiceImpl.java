package tr.edu.gazi.kutuphanem.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.edu.gazi.kutuphanem.domain.Borrowing;
import tr.edu.gazi.kutuphanem.repository.BorrowingRepository;
import tr.edu.gazi.kutuphanem.service.BorrowingService;
import tr.edu.gazi.kutuphanem.service.dto.BorrowingDTO;
import tr.edu.gazi.kutuphanem.service.mapper.BorrowingMapper;

/**
 * Service Implementation for managing {@link Borrowing}.
 */
@Service
@Transactional
public class BorrowingServiceImpl implements BorrowingService {

    private final Logger log = LoggerFactory.getLogger(BorrowingServiceImpl.class);

    private final BorrowingRepository borrowingRepository;

    private final BorrowingMapper borrowingMapper;

    public BorrowingServiceImpl(BorrowingRepository borrowingRepository, BorrowingMapper borrowingMapper) {
        this.borrowingRepository = borrowingRepository;
        this.borrowingMapper = borrowingMapper;
    }

    @Override
    public BorrowingDTO save(BorrowingDTO borrowingDTO) {
        log.debug("Request to save Borrowing : {}", borrowingDTO);
        Borrowing borrowing = borrowingMapper.toEntity(borrowingDTO);
        borrowing = borrowingRepository.save(borrowing);
        return borrowingMapper.toDto(borrowing);
    }

    @Override
    public Optional<BorrowingDTO> partialUpdate(BorrowingDTO borrowingDTO) {
        log.debug("Request to partially update Borrowing : {}", borrowingDTO);

        return borrowingRepository
            .findById(borrowingDTO.getId())
            .map(existingBorrowing -> {
                borrowingMapper.partialUpdate(existingBorrowing, borrowingDTO);

                return existingBorrowing;
            })
            .map(borrowingRepository::save)
            .map(borrowingMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowingDTO> findAll() {
        log.debug("Request to get all Borrowings");
        return borrowingRepository.findAll().stream().map(borrowingMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BorrowingDTO> findOne(Long id) {
        log.debug("Request to get Borrowing : {}", id);
        return borrowingRepository.findById(id).map(borrowingMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Borrowing : {}", id);
        borrowingRepository.deleteById(id);
    }
}
