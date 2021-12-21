package tr.edu.gazi.kutuphanem.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.edu.gazi.kutuphanem.domain.Publisher;
import tr.edu.gazi.kutuphanem.repository.PublisherRepository;
import tr.edu.gazi.kutuphanem.service.PublisherService;
import tr.edu.gazi.kutuphanem.service.dto.PublisherDTO;
import tr.edu.gazi.kutuphanem.service.mapper.PublisherMapper;

/**
 * Service Implementation for managing {@link Publisher}.
 */
@Service
@Transactional
public class PublisherServiceImpl implements PublisherService {

    private final Logger log = LoggerFactory.getLogger(PublisherServiceImpl.class);

    private final PublisherRepository publisherRepository;

    private final PublisherMapper publisherMapper;

    public PublisherServiceImpl(PublisherRepository publisherRepository, PublisherMapper publisherMapper) {
        this.publisherRepository = publisherRepository;
        this.publisherMapper = publisherMapper;
    }

    @Override
    public PublisherDTO save(PublisherDTO publisherDTO) {
        log.debug("Request to save Publisher : {}", publisherDTO);
        Publisher publisher = publisherMapper.toEntity(publisherDTO);
        publisher = publisherRepository.save(publisher);
        return publisherMapper.toDto(publisher);
    }

    @Override
    public Optional<PublisherDTO> partialUpdate(PublisherDTO publisherDTO) {
        log.debug("Request to partially update Publisher : {}", publisherDTO);

        return publisherRepository
            .findById(publisherDTO.getId())
            .map(existingPublisher -> {
                publisherMapper.partialUpdate(existingPublisher, publisherDTO);

                return existingPublisher;
            })
            .map(publisherRepository::save)
            .map(publisherMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublisherDTO> findAll() {
        log.debug("Request to get all Publishers");
        return publisherRepository.findAll().stream().map(publisherMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PublisherDTO> findOne(Long id) {
        log.debug("Request to get Publisher : {}", id);
        return publisherRepository.findById(id).map(publisherMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Publisher : {}", id);
        publisherRepository.deleteById(id);
    }
}
