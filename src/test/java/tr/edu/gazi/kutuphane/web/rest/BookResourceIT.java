package tr.edu.gazi.kutuphane.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import tr.edu.gazi.kutuphane.domain.Borrowing;
import tr.edu.gazi.kutuphane.repository.BookRepository;
import tr.edu.gazi.kutuphane.service.criteria.BookCriteria;

/**
 * Integration tests for the {@link BookResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BookResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final Integer DEFAULT_NUM_OF_PAGE = 1;
    private static final Integer UPDATED_NUM_OF_PAGE = 2;
    private static final Integer SMALLER_NUM_OF_PAGE = 1 - 1;

    private static final String DEFAULT_COVER_URL = "AAAAAAAAAA";
    private static final String UPDATED_COVER_URL = "BBBBBBBBBB";

    private static final String DEFAULT_PUBLISHER = "AAAAAAAAAA";
    private static final String UPDATED_PUBLISHER = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/books";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBookMockMvc;

    private Book book;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Book createEntity(EntityManager em) {
        Book book = new Book().title(DEFAULT_TITLE).numOfPage(DEFAULT_NUM_OF_PAGE).coverUrl(DEFAULT_COVER_URL).publisher(DEFAULT_PUBLISHER);
        return book;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Book createUpdatedEntity(EntityManager em) {
        Book book = new Book().title(UPDATED_TITLE).numOfPage(UPDATED_NUM_OF_PAGE).coverUrl(UPDATED_COVER_URL).publisher(UPDATED_PUBLISHER);
        return book;
    }

    @BeforeEach
    public void initTest() {
        book = createEntity(em);
    }

    @Test
    @Transactional
    void createBook() throws Exception {
        int databaseSizeBeforeCreate = bookRepository.findAll().size();
        // Create the Book
        restBookMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(book))
            )
            .andExpect(status().isCreated());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeCreate + 1);
        Book testBook = bookList.get(bookList.size() - 1);
        assertThat(testBook.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testBook.getNumOfPage()).isEqualTo(DEFAULT_NUM_OF_PAGE);
        assertThat(testBook.getCoverUrl()).isEqualTo(DEFAULT_COVER_URL);
        assertThat(testBook.getPublisher()).isEqualTo(DEFAULT_PUBLISHER);
    }

    @Test
    @Transactional
    void createBookWithExistingId() throws Exception {
        // Create the Book with an existing ID
        book.setId(1L);

        int databaseSizeBeforeCreate = bookRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(book))
            )
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = bookRepository.findAll().size();
        // set the field null
        book.setTitle(null);

        // Create the Book, which fails.

        restBookMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(book))
            )
            .andExpect(status().isBadRequest());

        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBooks() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList
        restBookMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(book.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].numOfPage").value(hasItem(DEFAULT_NUM_OF_PAGE)))
            .andExpect(jsonPath("$.[*].coverUrl").value(hasItem(DEFAULT_COVER_URL)))
            .andExpect(jsonPath("$.[*].publisher").value(hasItem(DEFAULT_PUBLISHER)));
    }

    @Test
    @Transactional
    void getBook() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get the book
        restBookMockMvc
            .perform(get(ENTITY_API_URL_ID, book.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(book.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.numOfPage").value(DEFAULT_NUM_OF_PAGE))
            .andExpect(jsonPath("$.coverUrl").value(DEFAULT_COVER_URL))
            .andExpect(jsonPath("$.publisher").value(DEFAULT_PUBLISHER));
    }

    @Test
    @Transactional
    void getBooksByIdFiltering() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        Long id = book.getId();

        defaultBookShouldBeFound("id.equals=" + id);
        defaultBookShouldNotBeFound("id.notEquals=" + id);

        defaultBookShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultBookShouldNotBeFound("id.greaterThan=" + id);

        defaultBookShouldBeFound("id.lessThanOrEqual=" + id);
        defaultBookShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBooksByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where title equals to DEFAULT_TITLE
        defaultBookShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the bookList where title equals to UPDATED_TITLE
        defaultBookShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllBooksByTitleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where title not equals to DEFAULT_TITLE
        defaultBookShouldNotBeFound("title.notEquals=" + DEFAULT_TITLE);

        // Get all the bookList where title not equals to UPDATED_TITLE
        defaultBookShouldBeFound("title.notEquals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllBooksByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultBookShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the bookList where title equals to UPDATED_TITLE
        defaultBookShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllBooksByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where title is not null
        defaultBookShouldBeFound("title.specified=true");

        // Get all the bookList where title is null
        defaultBookShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    void getAllBooksByTitleContainsSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where title contains DEFAULT_TITLE
        defaultBookShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the bookList where title contains UPDATED_TITLE
        defaultBookShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllBooksByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where title does not contain DEFAULT_TITLE
        defaultBookShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the bookList where title does not contain UPDATED_TITLE
        defaultBookShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllBooksByNumOfPageIsEqualToSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where numOfPage equals to DEFAULT_NUM_OF_PAGE
        defaultBookShouldBeFound("numOfPage.equals=" + DEFAULT_NUM_OF_PAGE);

        // Get all the bookList where numOfPage equals to UPDATED_NUM_OF_PAGE
        defaultBookShouldNotBeFound("numOfPage.equals=" + UPDATED_NUM_OF_PAGE);
    }

    @Test
    @Transactional
    void getAllBooksByNumOfPageIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where numOfPage not equals to DEFAULT_NUM_OF_PAGE
        defaultBookShouldNotBeFound("numOfPage.notEquals=" + DEFAULT_NUM_OF_PAGE);

        // Get all the bookList where numOfPage not equals to UPDATED_NUM_OF_PAGE
        defaultBookShouldBeFound("numOfPage.notEquals=" + UPDATED_NUM_OF_PAGE);
    }

    @Test
    @Transactional
    void getAllBooksByNumOfPageIsInShouldWork() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where numOfPage in DEFAULT_NUM_OF_PAGE or UPDATED_NUM_OF_PAGE
        defaultBookShouldBeFound("numOfPage.in=" + DEFAULT_NUM_OF_PAGE + "," + UPDATED_NUM_OF_PAGE);

        // Get all the bookList where numOfPage equals to UPDATED_NUM_OF_PAGE
        defaultBookShouldNotBeFound("numOfPage.in=" + UPDATED_NUM_OF_PAGE);
    }

    @Test
    @Transactional
    void getAllBooksByNumOfPageIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where numOfPage is not null
        defaultBookShouldBeFound("numOfPage.specified=true");

        // Get all the bookList where numOfPage is null
        defaultBookShouldNotBeFound("numOfPage.specified=false");
    }

    @Test
    @Transactional
    void getAllBooksByNumOfPageIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where numOfPage is greater than or equal to DEFAULT_NUM_OF_PAGE
        defaultBookShouldBeFound("numOfPage.greaterThanOrEqual=" + DEFAULT_NUM_OF_PAGE);

        // Get all the bookList where numOfPage is greater than or equal to UPDATED_NUM_OF_PAGE
        defaultBookShouldNotBeFound("numOfPage.greaterThanOrEqual=" + UPDATED_NUM_OF_PAGE);
    }

    @Test
    @Transactional
    void getAllBooksByNumOfPageIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where numOfPage is less than or equal to DEFAULT_NUM_OF_PAGE
        defaultBookShouldBeFound("numOfPage.lessThanOrEqual=" + DEFAULT_NUM_OF_PAGE);

        // Get all the bookList where numOfPage is less than or equal to SMALLER_NUM_OF_PAGE
        defaultBookShouldNotBeFound("numOfPage.lessThanOrEqual=" + SMALLER_NUM_OF_PAGE);
    }

    @Test
    @Transactional
    void getAllBooksByNumOfPageIsLessThanSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where numOfPage is less than DEFAULT_NUM_OF_PAGE
        defaultBookShouldNotBeFound("numOfPage.lessThan=" + DEFAULT_NUM_OF_PAGE);

        // Get all the bookList where numOfPage is less than UPDATED_NUM_OF_PAGE
        defaultBookShouldBeFound("numOfPage.lessThan=" + UPDATED_NUM_OF_PAGE);
    }

    @Test
    @Transactional
    void getAllBooksByNumOfPageIsGreaterThanSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where numOfPage is greater than DEFAULT_NUM_OF_PAGE
        defaultBookShouldNotBeFound("numOfPage.greaterThan=" + DEFAULT_NUM_OF_PAGE);

        // Get all the bookList where numOfPage is greater than SMALLER_NUM_OF_PAGE
        defaultBookShouldBeFound("numOfPage.greaterThan=" + SMALLER_NUM_OF_PAGE);
    }

    @Test
    @Transactional
    void getAllBooksByCoverUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where coverUrl equals to DEFAULT_COVER_URL
        defaultBookShouldBeFound("coverUrl.equals=" + DEFAULT_COVER_URL);

        // Get all the bookList where coverUrl equals to UPDATED_COVER_URL
        defaultBookShouldNotBeFound("coverUrl.equals=" + UPDATED_COVER_URL);
    }

    @Test
    @Transactional
    void getAllBooksByCoverUrlIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where coverUrl not equals to DEFAULT_COVER_URL
        defaultBookShouldNotBeFound("coverUrl.notEquals=" + DEFAULT_COVER_URL);

        // Get all the bookList where coverUrl not equals to UPDATED_COVER_URL
        defaultBookShouldBeFound("coverUrl.notEquals=" + UPDATED_COVER_URL);
    }

    @Test
    @Transactional
    void getAllBooksByCoverUrlIsInShouldWork() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where coverUrl in DEFAULT_COVER_URL or UPDATED_COVER_URL
        defaultBookShouldBeFound("coverUrl.in=" + DEFAULT_COVER_URL + "," + UPDATED_COVER_URL);

        // Get all the bookList where coverUrl equals to UPDATED_COVER_URL
        defaultBookShouldNotBeFound("coverUrl.in=" + UPDATED_COVER_URL);
    }

    @Test
    @Transactional
    void getAllBooksByCoverUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where coverUrl is not null
        defaultBookShouldBeFound("coverUrl.specified=true");

        // Get all the bookList where coverUrl is null
        defaultBookShouldNotBeFound("coverUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllBooksByCoverUrlContainsSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where coverUrl contains DEFAULT_COVER_URL
        defaultBookShouldBeFound("coverUrl.contains=" + DEFAULT_COVER_URL);

        // Get all the bookList where coverUrl contains UPDATED_COVER_URL
        defaultBookShouldNotBeFound("coverUrl.contains=" + UPDATED_COVER_URL);
    }

    @Test
    @Transactional
    void getAllBooksByCoverUrlNotContainsSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where coverUrl does not contain DEFAULT_COVER_URL
        defaultBookShouldNotBeFound("coverUrl.doesNotContain=" + DEFAULT_COVER_URL);

        // Get all the bookList where coverUrl does not contain UPDATED_COVER_URL
        defaultBookShouldBeFound("coverUrl.doesNotContain=" + UPDATED_COVER_URL);
    }

    @Test
    @Transactional
    void getAllBooksByPublisherIsEqualToSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where publisher equals to DEFAULT_PUBLISHER
        defaultBookShouldBeFound("publisher.equals=" + DEFAULT_PUBLISHER);

        // Get all the bookList where publisher equals to UPDATED_PUBLISHER
        defaultBookShouldNotBeFound("publisher.equals=" + UPDATED_PUBLISHER);
    }

    @Test
    @Transactional
    void getAllBooksByPublisherIsNotEqualToSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where publisher not equals to DEFAULT_PUBLISHER
        defaultBookShouldNotBeFound("publisher.notEquals=" + DEFAULT_PUBLISHER);

        // Get all the bookList where publisher not equals to UPDATED_PUBLISHER
        defaultBookShouldBeFound("publisher.notEquals=" + UPDATED_PUBLISHER);
    }

    @Test
    @Transactional
    void getAllBooksByPublisherIsInShouldWork() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where publisher in DEFAULT_PUBLISHER or UPDATED_PUBLISHER
        defaultBookShouldBeFound("publisher.in=" + DEFAULT_PUBLISHER + "," + UPDATED_PUBLISHER);

        // Get all the bookList where publisher equals to UPDATED_PUBLISHER
        defaultBookShouldNotBeFound("publisher.in=" + UPDATED_PUBLISHER);
    }

    @Test
    @Transactional
    void getAllBooksByPublisherIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where publisher is not null
        defaultBookShouldBeFound("publisher.specified=true");

        // Get all the bookList where publisher is null
        defaultBookShouldNotBeFound("publisher.specified=false");
    }

    @Test
    @Transactional
    void getAllBooksByPublisherContainsSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where publisher contains DEFAULT_PUBLISHER
        defaultBookShouldBeFound("publisher.contains=" + DEFAULT_PUBLISHER);

        // Get all the bookList where publisher contains UPDATED_PUBLISHER
        defaultBookShouldNotBeFound("publisher.contains=" + UPDATED_PUBLISHER);
    }

    @Test
    @Transactional
    void getAllBooksByPublisherNotContainsSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList where publisher does not contain DEFAULT_PUBLISHER
        defaultBookShouldNotBeFound("publisher.doesNotContain=" + DEFAULT_PUBLISHER);

        // Get all the bookList where publisher does not contain UPDATED_PUBLISHER
        defaultBookShouldBeFound("publisher.doesNotContain=" + UPDATED_PUBLISHER);
    }

    @Test
    @Transactional
    void getAllBooksByAuthorIsEqualToSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);
        Author author;
        if (TestUtil.findAll(em, Author.class).isEmpty()) {
            author = AuthorResourceIT.createEntity(em);
            em.persist(author);
            em.flush();
        } else {
            author = TestUtil.findAll(em, Author.class).get(0);
        }
        em.persist(author);
        em.flush();
        book.setAuthor(author);
        bookRepository.saveAndFlush(book);
        Long authorId = author.getId();

        // Get all the bookList where author equals to authorId
        defaultBookShouldBeFound("authorId.equals=" + authorId);

        // Get all the bookList where author equals to (authorId + 1)
        defaultBookShouldNotBeFound("authorId.equals=" + (authorId + 1));
    }

    @Test
    @Transactional
    void getAllBooksByBorrowingIsEqualToSomething() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);
        Borrowing borrowing;
        if (TestUtil.findAll(em, Borrowing.class).isEmpty()) {
            borrowing = BorrowingResourceIT.createEntity(em);
            em.persist(borrowing);
            em.flush();
        } else {
            borrowing = TestUtil.findAll(em, Borrowing.class).get(0);
        }
        em.persist(borrowing);
        em.flush();
        book.addBorrowing(borrowing);
        bookRepository.saveAndFlush(book);
        Long borrowingId = borrowing.getId();

        // Get all the bookList where borrowing equals to borrowingId
        defaultBookShouldBeFound("borrowingId.equals=" + borrowingId);

        // Get all the bookList where borrowing equals to (borrowingId + 1)
        defaultBookShouldNotBeFound("borrowingId.equals=" + (borrowingId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBookShouldBeFound(String filter) throws Exception {
        restBookMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(book.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].numOfPage").value(hasItem(DEFAULT_NUM_OF_PAGE)))
            .andExpect(jsonPath("$.[*].coverUrl").value(hasItem(DEFAULT_COVER_URL)))
            .andExpect(jsonPath("$.[*].publisher").value(hasItem(DEFAULT_PUBLISHER)));

        // Check, that the count call also returns 1
        restBookMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBookShouldNotBeFound(String filter) throws Exception {
        restBookMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBookMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBook() throws Exception {
        // Get the book
        restBookMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewBook() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        int databaseSizeBeforeUpdate = bookRepository.findAll().size();

        // Update the book
        Book updatedBook = bookRepository.findById(book.getId()).get();
        // Disconnect from session so that the updates on updatedBook are not directly saved in db
        em.detach(updatedBook);
        updatedBook.title(UPDATED_TITLE).numOfPage(UPDATED_NUM_OF_PAGE).coverUrl(UPDATED_COVER_URL).publisher(UPDATED_PUBLISHER);

        restBookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBook.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBook))
            )
            .andExpect(status().isOk());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
        Book testBook = bookList.get(bookList.size() - 1);
        assertThat(testBook.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testBook.getNumOfPage()).isEqualTo(UPDATED_NUM_OF_PAGE);
        assertThat(testBook.getCoverUrl()).isEqualTo(UPDATED_COVER_URL);
        assertThat(testBook.getPublisher()).isEqualTo(UPDATED_PUBLISHER);
    }

    @Test
    @Transactional
    void putNonExistingBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().size();
        book.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, book.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(book))
            )
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().size();
        book.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(book))
            )
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().size();
        book.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(book))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBookWithPatch() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        int databaseSizeBeforeUpdate = bookRepository.findAll().size();

        // Update the book using partial update
        Book partialUpdatedBook = new Book();
        partialUpdatedBook.setId(book.getId());

        restBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBook.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBook))
            )
            .andExpect(status().isOk());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
        Book testBook = bookList.get(bookList.size() - 1);
        assertThat(testBook.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testBook.getNumOfPage()).isEqualTo(DEFAULT_NUM_OF_PAGE);
        assertThat(testBook.getCoverUrl()).isEqualTo(DEFAULT_COVER_URL);
        assertThat(testBook.getPublisher()).isEqualTo(DEFAULT_PUBLISHER);
    }

    @Test
    @Transactional
    void fullUpdateBookWithPatch() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        int databaseSizeBeforeUpdate = bookRepository.findAll().size();

        // Update the book using partial update
        Book partialUpdatedBook = new Book();
        partialUpdatedBook.setId(book.getId());

        partialUpdatedBook.title(UPDATED_TITLE).numOfPage(UPDATED_NUM_OF_PAGE).coverUrl(UPDATED_COVER_URL).publisher(UPDATED_PUBLISHER);

        restBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBook.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBook))
            )
            .andExpect(status().isOk());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
        Book testBook = bookList.get(bookList.size() - 1);
        assertThat(testBook.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testBook.getNumOfPage()).isEqualTo(UPDATED_NUM_OF_PAGE);
        assertThat(testBook.getCoverUrl()).isEqualTo(UPDATED_COVER_URL);
        assertThat(testBook.getPublisher()).isEqualTo(UPDATED_PUBLISHER);
    }

    @Test
    @Transactional
    void patchNonExistingBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().size();
        book.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, book.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(book))
            )
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().size();
        book.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(book))
            )
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().size();
        book.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(book))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBook() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        int databaseSizeBeforeDelete = bookRepository.findAll().size();

        // Delete the book
        restBookMockMvc
            .perform(delete(ENTITY_API_URL_ID, book.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
