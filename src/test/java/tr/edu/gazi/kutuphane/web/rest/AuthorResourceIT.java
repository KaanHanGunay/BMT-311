package tr.edu.gazi.kutuphane.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tr.edu.gazi.kutuphane.IntegrationTest;
import tr.edu.gazi.kutuphane.domain.Author;
import tr.edu.gazi.kutuphane.domain.Book;
import tr.edu.gazi.kutuphane.repository.AuthorRepository;
import tr.edu.gazi.kutuphane.service.criteria.AuthorCriteria;

/**
 * Integration tests for the {@link AuthorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AuthorResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SURNAME = "AAAAAAAAAA";
    private static final String UPDATED_SURNAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_BIRTHDAY = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BIRTHDAY = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_BIRTHDAY = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_DIED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DIED = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DIED = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_NATIONALITY = "AAAAAAAAAA";
    private static final String UPDATED_NATIONALITY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/authors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAuthorMockMvc;

    private Author author;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Author createEntity(EntityManager em) {
        Author author = new Author()
            .name(DEFAULT_NAME)
            .surname(DEFAULT_SURNAME)
            .birthday(DEFAULT_BIRTHDAY)
            .died(DEFAULT_DIED)
            .nationality(DEFAULT_NATIONALITY);
        return author;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Author createUpdatedEntity(EntityManager em) {
        Author author = new Author()
            .name(UPDATED_NAME)
            .surname(UPDATED_SURNAME)
            .birthday(UPDATED_BIRTHDAY)
            .died(UPDATED_DIED)
            .nationality(UPDATED_NATIONALITY);
        return author;
    }

    @BeforeEach
    public void initTest() {
        author = createEntity(em);
    }

    @Test
    @Transactional
    void createAuthor() throws Exception {
        int databaseSizeBeforeCreate = authorRepository.findAll().size();
        // Create the Author
        restAuthorMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(author))
            )
            .andExpect(status().isCreated());

        // Validate the Author in the database
        List<Author> authorList = authorRepository.findAll();
        assertThat(authorList).hasSize(databaseSizeBeforeCreate + 1);
        Author testAuthor = authorList.get(authorList.size() - 1);
        assertThat(testAuthor.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAuthor.getSurname()).isEqualTo(DEFAULT_SURNAME);
        assertThat(testAuthor.getBirthday()).isEqualTo(DEFAULT_BIRTHDAY);
        assertThat(testAuthor.getDied()).isEqualTo(DEFAULT_DIED);
        assertThat(testAuthor.getNationality()).isEqualTo(DEFAULT_NATIONALITY);
    }

    @Test
    @Transactional
    void createAuthorWithExistingId() throws Exception {
        // Create the Author with an existing ID
        author.setId(1L);

        int databaseSizeBeforeCreate = authorRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAuthorMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(author))
            )
            .andExpect(status().isBadRequest());

        // Validate the Author in the database
        List<Author> authorList = authorRepository.findAll();
        assertThat(authorList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = authorRepository.findAll().size();
        // set the field null
        author.setName(null);

        // Create the Author, which fails.

        restAuthorMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(author))
            )
            .andExpect(status().isBadRequest());

        List<Author> authorList = authorRepository.findAll();
        assertThat(authorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAuthors() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList
        restAuthorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(author.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].surname").value(hasItem(DEFAULT_SURNAME)))
            .andExpect(jsonPath("$.[*].birthday").value(hasItem(DEFAULT_BIRTHDAY.toString())))
            .andExpect(jsonPath("$.[*].died").value(hasItem(DEFAULT_DIED.toString())))
            .andExpect(jsonPath("$.[*].nationality").value(hasItem(DEFAULT_NATIONALITY)));
    }

    @Test
    @Transactional
    void getAuthor() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get the author
        restAuthorMockMvc
            .perform(get(ENTITY_API_URL_ID, author.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(author.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.surname").value(DEFAULT_SURNAME))
            .andExpect(jsonPath("$.birthday").value(DEFAULT_BIRTHDAY.toString()))
            .andExpect(jsonPath("$.died").value(DEFAULT_DIED.toString()))
            .andExpect(jsonPath("$.nationality").value(DEFAULT_NATIONALITY));
    }

    @Test
    @Transactional
    void getAuthorsByIdFiltering() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        Long id = author.getId();

        defaultAuthorShouldBeFound("id.equals=" + id);
        defaultAuthorShouldNotBeFound("id.notEquals=" + id);

        defaultAuthorShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultAuthorShouldNotBeFound("id.greaterThan=" + id);

        defaultAuthorShouldBeFound("id.lessThanOrEqual=" + id);
        defaultAuthorShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAuthorsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where name equals to DEFAULT_NAME
        defaultAuthorShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the authorList where name equals to UPDATED_NAME
        defaultAuthorShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllAuthorsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where name not equals to DEFAULT_NAME
        defaultAuthorShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the authorList where name not equals to UPDATED_NAME
        defaultAuthorShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllAuthorsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where name in DEFAULT_NAME or UPDATED_NAME
        defaultAuthorShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the authorList where name equals to UPDATED_NAME
        defaultAuthorShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllAuthorsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where name is not null
        defaultAuthorShouldBeFound("name.specified=true");

        // Get all the authorList where name is null
        defaultAuthorShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllAuthorsByNameContainsSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where name contains DEFAULT_NAME
        defaultAuthorShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the authorList where name contains UPDATED_NAME
        defaultAuthorShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllAuthorsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where name does not contain DEFAULT_NAME
        defaultAuthorShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the authorList where name does not contain UPDATED_NAME
        defaultAuthorShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllAuthorsBySurnameIsEqualToSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where surname equals to DEFAULT_SURNAME
        defaultAuthorShouldBeFound("surname.equals=" + DEFAULT_SURNAME);

        // Get all the authorList where surname equals to UPDATED_SURNAME
        defaultAuthorShouldNotBeFound("surname.equals=" + UPDATED_SURNAME);
    }

    @Test
    @Transactional
    void getAllAuthorsBySurnameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where surname not equals to DEFAULT_SURNAME
        defaultAuthorShouldNotBeFound("surname.notEquals=" + DEFAULT_SURNAME);

        // Get all the authorList where surname not equals to UPDATED_SURNAME
        defaultAuthorShouldBeFound("surname.notEquals=" + UPDATED_SURNAME);
    }

    @Test
    @Transactional
    void getAllAuthorsBySurnameIsInShouldWork() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where surname in DEFAULT_SURNAME or UPDATED_SURNAME
        defaultAuthorShouldBeFound("surname.in=" + DEFAULT_SURNAME + "," + UPDATED_SURNAME);

        // Get all the authorList where surname equals to UPDATED_SURNAME
        defaultAuthorShouldNotBeFound("surname.in=" + UPDATED_SURNAME);
    }

    @Test
    @Transactional
    void getAllAuthorsBySurnameIsNullOrNotNull() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where surname is not null
        defaultAuthorShouldBeFound("surname.specified=true");

        // Get all the authorList where surname is null
        defaultAuthorShouldNotBeFound("surname.specified=false");
    }

    @Test
    @Transactional
    void getAllAuthorsBySurnameContainsSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where surname contains DEFAULT_SURNAME
        defaultAuthorShouldBeFound("surname.contains=" + DEFAULT_SURNAME);

        // Get all the authorList where surname contains UPDATED_SURNAME
        defaultAuthorShouldNotBeFound("surname.contains=" + UPDATED_SURNAME);
    }

    @Test
    @Transactional
    void getAllAuthorsBySurnameNotContainsSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where surname does not contain DEFAULT_SURNAME
        defaultAuthorShouldNotBeFound("surname.doesNotContain=" + DEFAULT_SURNAME);

        // Get all the authorList where surname does not contain UPDATED_SURNAME
        defaultAuthorShouldBeFound("surname.doesNotContain=" + UPDATED_SURNAME);
    }

    @Test
    @Transactional
    void getAllAuthorsByBirthdayIsEqualToSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where birthday equals to DEFAULT_BIRTHDAY
        defaultAuthorShouldBeFound("birthday.equals=" + DEFAULT_BIRTHDAY);

        // Get all the authorList where birthday equals to UPDATED_BIRTHDAY
        defaultAuthorShouldNotBeFound("birthday.equals=" + UPDATED_BIRTHDAY);
    }

    @Test
    @Transactional
    void getAllAuthorsByBirthdayIsNotEqualToSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where birthday not equals to DEFAULT_BIRTHDAY
        defaultAuthorShouldNotBeFound("birthday.notEquals=" + DEFAULT_BIRTHDAY);

        // Get all the authorList where birthday not equals to UPDATED_BIRTHDAY
        defaultAuthorShouldBeFound("birthday.notEquals=" + UPDATED_BIRTHDAY);
    }

    @Test
    @Transactional
    void getAllAuthorsByBirthdayIsInShouldWork() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where birthday in DEFAULT_BIRTHDAY or UPDATED_BIRTHDAY
        defaultAuthorShouldBeFound("birthday.in=" + DEFAULT_BIRTHDAY + "," + UPDATED_BIRTHDAY);

        // Get all the authorList where birthday equals to UPDATED_BIRTHDAY
        defaultAuthorShouldNotBeFound("birthday.in=" + UPDATED_BIRTHDAY);
    }

    @Test
    @Transactional
    void getAllAuthorsByBirthdayIsNullOrNotNull() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where birthday is not null
        defaultAuthorShouldBeFound("birthday.specified=true");

        // Get all the authorList where birthday is null
        defaultAuthorShouldNotBeFound("birthday.specified=false");
    }

    @Test
    @Transactional
    void getAllAuthorsByBirthdayIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where birthday is greater than or equal to DEFAULT_BIRTHDAY
        defaultAuthorShouldBeFound("birthday.greaterThanOrEqual=" + DEFAULT_BIRTHDAY);

        // Get all the authorList where birthday is greater than or equal to UPDATED_BIRTHDAY
        defaultAuthorShouldNotBeFound("birthday.greaterThanOrEqual=" + UPDATED_BIRTHDAY);
    }

    @Test
    @Transactional
    void getAllAuthorsByBirthdayIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where birthday is less than or equal to DEFAULT_BIRTHDAY
        defaultAuthorShouldBeFound("birthday.lessThanOrEqual=" + DEFAULT_BIRTHDAY);

        // Get all the authorList where birthday is less than or equal to SMALLER_BIRTHDAY
        defaultAuthorShouldNotBeFound("birthday.lessThanOrEqual=" + SMALLER_BIRTHDAY);
    }

    @Test
    @Transactional
    void getAllAuthorsByBirthdayIsLessThanSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where birthday is less than DEFAULT_BIRTHDAY
        defaultAuthorShouldNotBeFound("birthday.lessThan=" + DEFAULT_BIRTHDAY);

        // Get all the authorList where birthday is less than UPDATED_BIRTHDAY
        defaultAuthorShouldBeFound("birthday.lessThan=" + UPDATED_BIRTHDAY);
    }

    @Test
    @Transactional
    void getAllAuthorsByBirthdayIsGreaterThanSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where birthday is greater than DEFAULT_BIRTHDAY
        defaultAuthorShouldNotBeFound("birthday.greaterThan=" + DEFAULT_BIRTHDAY);

        // Get all the authorList where birthday is greater than SMALLER_BIRTHDAY
        defaultAuthorShouldBeFound("birthday.greaterThan=" + SMALLER_BIRTHDAY);
    }

    @Test
    @Transactional
    void getAllAuthorsByDiedIsEqualToSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where died equals to DEFAULT_DIED
        defaultAuthorShouldBeFound("died.equals=" + DEFAULT_DIED);

        // Get all the authorList where died equals to UPDATED_DIED
        defaultAuthorShouldNotBeFound("died.equals=" + UPDATED_DIED);
    }

    @Test
    @Transactional
    void getAllAuthorsByDiedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where died not equals to DEFAULT_DIED
        defaultAuthorShouldNotBeFound("died.notEquals=" + DEFAULT_DIED);

        // Get all the authorList where died not equals to UPDATED_DIED
        defaultAuthorShouldBeFound("died.notEquals=" + UPDATED_DIED);
    }

    @Test
    @Transactional
    void getAllAuthorsByDiedIsInShouldWork() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where died in DEFAULT_DIED or UPDATED_DIED
        defaultAuthorShouldBeFound("died.in=" + DEFAULT_DIED + "," + UPDATED_DIED);

        // Get all the authorList where died equals to UPDATED_DIED
        defaultAuthorShouldNotBeFound("died.in=" + UPDATED_DIED);
    }

    @Test
    @Transactional
    void getAllAuthorsByDiedIsNullOrNotNull() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where died is not null
        defaultAuthorShouldBeFound("died.specified=true");

        // Get all the authorList where died is null
        defaultAuthorShouldNotBeFound("died.specified=false");
    }

    @Test
    @Transactional
    void getAllAuthorsByDiedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where died is greater than or equal to DEFAULT_DIED
        defaultAuthorShouldBeFound("died.greaterThanOrEqual=" + DEFAULT_DIED);

        // Get all the authorList where died is greater than or equal to UPDATED_DIED
        defaultAuthorShouldNotBeFound("died.greaterThanOrEqual=" + UPDATED_DIED);
    }

    @Test
    @Transactional
    void getAllAuthorsByDiedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where died is less than or equal to DEFAULT_DIED
        defaultAuthorShouldBeFound("died.lessThanOrEqual=" + DEFAULT_DIED);

        // Get all the authorList where died is less than or equal to SMALLER_DIED
        defaultAuthorShouldNotBeFound("died.lessThanOrEqual=" + SMALLER_DIED);
    }

    @Test
    @Transactional
    void getAllAuthorsByDiedIsLessThanSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where died is less than DEFAULT_DIED
        defaultAuthorShouldNotBeFound("died.lessThan=" + DEFAULT_DIED);

        // Get all the authorList where died is less than UPDATED_DIED
        defaultAuthorShouldBeFound("died.lessThan=" + UPDATED_DIED);
    }

    @Test
    @Transactional
    void getAllAuthorsByDiedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where died is greater than DEFAULT_DIED
        defaultAuthorShouldNotBeFound("died.greaterThan=" + DEFAULT_DIED);

        // Get all the authorList where died is greater than SMALLER_DIED
        defaultAuthorShouldBeFound("died.greaterThan=" + SMALLER_DIED);
    }

    @Test
    @Transactional
    void getAllAuthorsByNationalityIsEqualToSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where nationality equals to DEFAULT_NATIONALITY
        defaultAuthorShouldBeFound("nationality.equals=" + DEFAULT_NATIONALITY);

        // Get all the authorList where nationality equals to UPDATED_NATIONALITY
        defaultAuthorShouldNotBeFound("nationality.equals=" + UPDATED_NATIONALITY);
    }

    @Test
    @Transactional
    void getAllAuthorsByNationalityIsNotEqualToSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where nationality not equals to DEFAULT_NATIONALITY
        defaultAuthorShouldNotBeFound("nationality.notEquals=" + DEFAULT_NATIONALITY);

        // Get all the authorList where nationality not equals to UPDATED_NATIONALITY
        defaultAuthorShouldBeFound("nationality.notEquals=" + UPDATED_NATIONALITY);
    }

    @Test
    @Transactional
    void getAllAuthorsByNationalityIsInShouldWork() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where nationality in DEFAULT_NATIONALITY or UPDATED_NATIONALITY
        defaultAuthorShouldBeFound("nationality.in=" + DEFAULT_NATIONALITY + "," + UPDATED_NATIONALITY);

        // Get all the authorList where nationality equals to UPDATED_NATIONALITY
        defaultAuthorShouldNotBeFound("nationality.in=" + UPDATED_NATIONALITY);
    }

    @Test
    @Transactional
    void getAllAuthorsByNationalityIsNullOrNotNull() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where nationality is not null
        defaultAuthorShouldBeFound("nationality.specified=true");

        // Get all the authorList where nationality is null
        defaultAuthorShouldNotBeFound("nationality.specified=false");
    }

    @Test
    @Transactional
    void getAllAuthorsByNationalityContainsSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where nationality contains DEFAULT_NATIONALITY
        defaultAuthorShouldBeFound("nationality.contains=" + DEFAULT_NATIONALITY);

        // Get all the authorList where nationality contains UPDATED_NATIONALITY
        defaultAuthorShouldNotBeFound("nationality.contains=" + UPDATED_NATIONALITY);
    }

    @Test
    @Transactional
    void getAllAuthorsByNationalityNotContainsSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where nationality does not contain DEFAULT_NATIONALITY
        defaultAuthorShouldNotBeFound("nationality.doesNotContain=" + DEFAULT_NATIONALITY);

        // Get all the authorList where nationality does not contain UPDATED_NATIONALITY
        defaultAuthorShouldBeFound("nationality.doesNotContain=" + UPDATED_NATIONALITY);
    }

    @Test
    @Transactional
    void getAllAuthorsByBookIsEqualToSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);
        Book book;
        if (TestUtil.findAll(em, Book.class).isEmpty()) {
            book = BookResourceIT.createEntity(em);
            em.persist(book);
            em.flush();
        } else {
            book = TestUtil.findAll(em, Book.class).get(0);
        }
        em.persist(book);
        em.flush();
        author.addBook(book);
        authorRepository.saveAndFlush(author);
        Long bookId = book.getId();

        // Get all the authorList where book equals to bookId
        defaultAuthorShouldBeFound("bookId.equals=" + bookId);

        // Get all the authorList where book equals to (bookId + 1)
        defaultAuthorShouldNotBeFound("bookId.equals=" + (bookId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAuthorShouldBeFound(String filter) throws Exception {
        restAuthorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(author.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].surname").value(hasItem(DEFAULT_SURNAME)))
            .andExpect(jsonPath("$.[*].birthday").value(hasItem(DEFAULT_BIRTHDAY.toString())))
            .andExpect(jsonPath("$.[*].died").value(hasItem(DEFAULT_DIED.toString())))
            .andExpect(jsonPath("$.[*].nationality").value(hasItem(DEFAULT_NATIONALITY)));

        // Check, that the count call also returns 1
        restAuthorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAuthorShouldNotBeFound(String filter) throws Exception {
        restAuthorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAuthorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAuthor() throws Exception {
        // Get the author
        restAuthorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAuthor() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        int databaseSizeBeforeUpdate = authorRepository.findAll().size();

        // Update the author
        Author updatedAuthor = authorRepository.findById(author.getId()).get();
        // Disconnect from session so that the updates on updatedAuthor are not directly saved in db
        em.detach(updatedAuthor);
        updatedAuthor
            .name(UPDATED_NAME)
            .surname(UPDATED_SURNAME)
            .birthday(UPDATED_BIRTHDAY)
            .died(UPDATED_DIED)
            .nationality(UPDATED_NATIONALITY);

        restAuthorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAuthor.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAuthor))
            )
            .andExpect(status().isOk());

        // Validate the Author in the database
        List<Author> authorList = authorRepository.findAll();
        assertThat(authorList).hasSize(databaseSizeBeforeUpdate);
        Author testAuthor = authorList.get(authorList.size() - 1);
        assertThat(testAuthor.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAuthor.getSurname()).isEqualTo(UPDATED_SURNAME);
        assertThat(testAuthor.getBirthday()).isEqualTo(UPDATED_BIRTHDAY);
        assertThat(testAuthor.getDied()).isEqualTo(UPDATED_DIED);
        assertThat(testAuthor.getNationality()).isEqualTo(UPDATED_NATIONALITY);
    }

    @Test
    @Transactional
    void putNonExistingAuthor() throws Exception {
        int databaseSizeBeforeUpdate = authorRepository.findAll().size();
        author.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAuthorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, author.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(author))
            )
            .andExpect(status().isBadRequest());

        // Validate the Author in the database
        List<Author> authorList = authorRepository.findAll();
        assertThat(authorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAuthor() throws Exception {
        int databaseSizeBeforeUpdate = authorRepository.findAll().size();
        author.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuthorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(author))
            )
            .andExpect(status().isBadRequest());

        // Validate the Author in the database
        List<Author> authorList = authorRepository.findAll();
        assertThat(authorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAuthor() throws Exception {
        int databaseSizeBeforeUpdate = authorRepository.findAll().size();
        author.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuthorMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(author))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Author in the database
        List<Author> authorList = authorRepository.findAll();
        assertThat(authorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAuthorWithPatch() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        int databaseSizeBeforeUpdate = authorRepository.findAll().size();

        // Update the author using partial update
        Author partialUpdatedAuthor = new Author();
        partialUpdatedAuthor.setId(author.getId());

        partialUpdatedAuthor.surname(UPDATED_SURNAME);

        restAuthorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAuthor.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAuthor))
            )
            .andExpect(status().isOk());

        // Validate the Author in the database
        List<Author> authorList = authorRepository.findAll();
        assertThat(authorList).hasSize(databaseSizeBeforeUpdate);
        Author testAuthor = authorList.get(authorList.size() - 1);
        assertThat(testAuthor.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAuthor.getSurname()).isEqualTo(UPDATED_SURNAME);
        assertThat(testAuthor.getBirthday()).isEqualTo(DEFAULT_BIRTHDAY);
        assertThat(testAuthor.getDied()).isEqualTo(DEFAULT_DIED);
        assertThat(testAuthor.getNationality()).isEqualTo(DEFAULT_NATIONALITY);
    }

    @Test
    @Transactional
    void fullUpdateAuthorWithPatch() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        int databaseSizeBeforeUpdate = authorRepository.findAll().size();

        // Update the author using partial update
        Author partialUpdatedAuthor = new Author();
        partialUpdatedAuthor.setId(author.getId());

        partialUpdatedAuthor
            .name(UPDATED_NAME)
            .surname(UPDATED_SURNAME)
            .birthday(UPDATED_BIRTHDAY)
            .died(UPDATED_DIED)
            .nationality(UPDATED_NATIONALITY);

        restAuthorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAuthor.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAuthor))
            )
            .andExpect(status().isOk());

        // Validate the Author in the database
        List<Author> authorList = authorRepository.findAll();
        assertThat(authorList).hasSize(databaseSizeBeforeUpdate);
        Author testAuthor = authorList.get(authorList.size() - 1);
        assertThat(testAuthor.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAuthor.getSurname()).isEqualTo(UPDATED_SURNAME);
        assertThat(testAuthor.getBirthday()).isEqualTo(UPDATED_BIRTHDAY);
        assertThat(testAuthor.getDied()).isEqualTo(UPDATED_DIED);
        assertThat(testAuthor.getNationality()).isEqualTo(UPDATED_NATIONALITY);
    }

    @Test
    @Transactional
    void patchNonExistingAuthor() throws Exception {
        int databaseSizeBeforeUpdate = authorRepository.findAll().size();
        author.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAuthorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, author.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(author))
            )
            .andExpect(status().isBadRequest());

        // Validate the Author in the database
        List<Author> authorList = authorRepository.findAll();
        assertThat(authorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAuthor() throws Exception {
        int databaseSizeBeforeUpdate = authorRepository.findAll().size();
        author.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuthorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(author))
            )
            .andExpect(status().isBadRequest());

        // Validate the Author in the database
        List<Author> authorList = authorRepository.findAll();
        assertThat(authorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAuthor() throws Exception {
        int databaseSizeBeforeUpdate = authorRepository.findAll().size();
        author.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuthorMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(author))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Author in the database
        List<Author> authorList = authorRepository.findAll();
        assertThat(authorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAuthor() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        int databaseSizeBeforeDelete = authorRepository.findAll().size();

        // Delete the author
        restAuthorMockMvc
            .perform(delete(ENTITY_API_URL_ID, author.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Author> authorList = authorRepository.findAll();
        assertThat(authorList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
