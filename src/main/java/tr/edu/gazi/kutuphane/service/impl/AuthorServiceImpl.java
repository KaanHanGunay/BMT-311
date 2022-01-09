package tr.edu.gazi.kutuphane.service.impl;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.edu.gazi.kutuphane.domain.Author;
import tr.edu.gazi.kutuphane.repository.AuthorRepository;
import tr.edu.gazi.kutuphane.service.AuthorService;

/**
 * Service Implementation for managing {@link Author}.
 */
@Service
@Transactional
public class AuthorServiceImpl implements AuthorService {

    private final Logger log = LoggerFactory.getLogger(AuthorServiceImpl.class);

    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public Author save(Author author) {
        log.debug("Request to save Author : {}", author);
        return authorRepository.CREATE_AUTHOR(
            author.getName(),
            author.getSurname(),
            author.getBirthday(),
            author.getDied(),
            author.getNationality()
        );
    }

    @Override
    public Optional<Author> partialUpdate(Author author) {
        log.debug("Request to partially update Author : {}", author);

        return authorRepository
            .findById(author.getId())
            .map(existingAuthor -> {
                if (author.getName() != null) {
                    existingAuthor.setName(author.getName());
                }
                if (author.getSurname() != null) {
                    existingAuthor.setSurname(author.getSurname());
                }
                if (author.getBirthday() != null) {
                    existingAuthor.setBirthday(author.getBirthday());
                }
                if (author.getDied() != null) {
                    existingAuthor.setDied(author.getDied());
                }
                if (author.getNationality() != null) {
                    existingAuthor.setNationality(author.getNationality());
                }

                return existingAuthor;
            })
            .map(authorRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Author> findAll() {
        log.debug("Request to get all Authors");
        return authorRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Author> findOne(Long id) {
        log.debug("Request to get Author : {}", id);
        return authorRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Author : {}", id);
        authorRepository.deleteById(id);
    }
}
