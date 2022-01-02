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
import org.springframework.util.Base64Utils;
import tr.edu.gazi.kutuphane.IntegrationTest;
import tr.edu.gazi.kutuphane.domain.Book;
import tr.edu.gazi.kutuphane.domain.Borrowing;
import tr.edu.gazi.kutuphane.domain.User;
import tr.edu.gazi.kutuphane.repository.BorrowingRepository;
import tr.edu.gazi.kutuphane.service.criteria.BorrowingCriteria;

/**
 * Integration tests for the {@link BorrowingResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BorrowingResourceIT {

    private static final LocalDate DEFAULT_BORROWING_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BORROWING_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_BORROWING_DATE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_DELIVERY_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DELIVERY_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DELIVERY_DATE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/borrowings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BorrowingRepository borrowingRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBorrowingMockMvc;

    private Borrowing borrowing;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Borrowing createEntity(EntityManager em) {
        Borrowing borrowing = new Borrowing()
            .borrowingDate(DEFAULT_BORROWING_DATE)
            .deliveryDate(DEFAULT_DELIVERY_DATE)
            .comment(DEFAULT_COMMENT);
        return borrowing;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Borrowing createUpdatedEntity(EntityManager em) {
        Borrowing borrowing = new Borrowing()
            .borrowingDate(UPDATED_BORROWING_DATE)
            .deliveryDate(UPDATED_DELIVERY_DATE)
            .comment(UPDATED_COMMENT);
        return borrowing;
    }

    @BeforeEach
    public void initTest() {
        borrowing = createEntity(em);
    }

    @Test
    @Transactional
    void createBorrowing() throws Exception {
        int databaseSizeBeforeCreate = borrowingRepository.findAll().size();
        // Create the Borrowing
        restBorrowingMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(borrowing))
            )
            .andExpect(status().isCreated());

        // Validate the Borrowing in the database
        List<Borrowing> borrowingList = borrowingRepository.findAll();
        assertThat(borrowingList).hasSize(databaseSizeBeforeCreate + 1);
        Borrowing testBorrowing = borrowingList.get(borrowingList.size() - 1);
        assertThat(testBorrowing.getBorrowingDate()).isEqualTo(DEFAULT_BORROWING_DATE);
        assertThat(testBorrowing.getDeliveryDate()).isEqualTo(DEFAULT_DELIVERY_DATE);
        assertThat(testBorrowing.getComment()).isEqualTo(DEFAULT_COMMENT);
    }

    @Test
    @Transactional
    void createBorrowingWithExistingId() throws Exception {
        // Create the Borrowing with an existing ID
        borrowing.setId(1L);

        int databaseSizeBeforeCreate = borrowingRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBorrowingMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(borrowing))
            )
            .andExpect(status().isBadRequest());

        // Validate the Borrowing in the database
        List<Borrowing> borrowingList = borrowingRepository.findAll();
        assertThat(borrowingList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllBorrowings() throws Exception {
        // Initialize the database
        borrowingRepository.saveAndFlush(borrowing);

        // Get all the borrowingList
        restBorrowingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(borrowing.getId().intValue())))
            .andExpect(jsonPath("$.[*].borrowingDate").value(hasItem(DEFAULT_BORROWING_DATE.toString())))
            .andExpect(jsonPath("$.[*].deliveryDate").value(hasItem(DEFAULT_DELIVERY_DATE.toString())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())));
    }

    @Test
    @Transactional
    void getBorrowing() throws Exception {
        // Initialize the database
        borrowingRepository.saveAndFlush(borrowing);

        // Get the borrowing
        restBorrowingMockMvc
            .perform(get(ENTITY_API_URL_ID, borrowing.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(borrowing.getId().intValue()))
            .andExpect(jsonPath("$.borrowingDate").value(DEFAULT_BORROWING_DATE.toString()))
            .andExpect(jsonPath("$.deliveryDate").value(DEFAULT_DELIVERY_DATE.toString()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()));
    }

    @Test
    @Transactional
    void getBorrowingsByIdFiltering() throws Exception {
        // Initialize the database
        borrowingRepository.saveAndFlush(borrowing);

        Long id = borrowing.getId();

        defaultBorrowingShouldBeFound("id.equals=" + id);
        defaultBorrowingShouldNotBeFound("id.notEquals=" + id);

        defaultBorrowingShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultBorrowingShouldNotBeFound("id.greaterThan=" + id);

        defaultBorrowingShouldBeFound("id.lessThanOrEqual=" + id);
        defaultBorrowingShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBorrowingsByBorrowingDateIsEqualToSomething() throws Exception {
        // Initialize the database
        borrowingRepository.saveAndFlush(borrowing);

        // Get all the borrowingList where borrowingDate equals to DEFAULT_BORROWING_DATE
        defaultBorrowingShouldBeFound("borrowingDate.equals=" + DEFAULT_BORROWING_DATE);

        // Get all the borrowingList where borrowingDate equals to UPDATED_BORROWING_DATE
        defaultBorrowingShouldNotBeFound("borrowingDate.equals=" + UPDATED_BORROWING_DATE);
    }

    @Test
    @Transactional
    void getAllBorrowingsByBorrowingDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        borrowingRepository.saveAndFlush(borrowing);

        // Get all the borrowingList where borrowingDate not equals to DEFAULT_BORROWING_DATE
        defaultBorrowingShouldNotBeFound("borrowingDate.notEquals=" + DEFAULT_BORROWING_DATE);

        // Get all the borrowingList where borrowingDate not equals to UPDATED_BORROWING_DATE
        defaultBorrowingShouldBeFound("borrowingDate.notEquals=" + UPDATED_BORROWING_DATE);
    }

    @Test
    @Transactional
    void getAllBorrowingsByBorrowingDateIsInShouldWork() throws Exception {
        // Initialize the database
        borrowingRepository.saveAndFlush(borrowing);

        // Get all the borrowingList where borrowingDate in DEFAULT_BORROWING_DATE or UPDATED_BORROWING_DATE
        defaultBorrowingShouldBeFound("borrowingDate.in=" + DEFAULT_BORROWING_DATE + "," + UPDATED_BORROWING_DATE);

        // Get all the borrowingList where borrowingDate equals to UPDATED_BORROWING_DATE
        defaultBorrowingShouldNotBeFound("borrowingDate.in=" + UPDATED_BORROWING_DATE);
    }

    @Test
    @Transactional
    void getAllBorrowingsByBorrowingDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        borrowingRepository.saveAndFlush(borrowing);

        // Get all the borrowingList where borrowingDate is not null
        defaultBorrowingShouldBeFound("borrowingDate.specified=true");

        // Get all the borrowingList where borrowingDate is null
        defaultBorrowingShouldNotBeFound("borrowingDate.specified=false");
    }

    @Test
    @Transactional
    void getAllBorrowingsByBorrowingDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        borrowingRepository.saveAndFlush(borrowing);

        // Get all the borrowingList where borrowingDate is greater than or equal to DEFAULT_BORROWING_DATE
        defaultBorrowingShouldBeFound("borrowingDate.greaterThanOrEqual=" + DEFAULT_BORROWING_DATE);

        // Get all the borrowingList where borrowingDate is greater than or equal to UPDATED_BORROWING_DATE
        defaultBorrowingShouldNotBeFound("borrowingDate.greaterThanOrEqual=" + UPDATED_BORROWING_DATE);
    }

    @Test
    @Transactional
    void getAllBorrowingsByBorrowingDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        borrowingRepository.saveAndFlush(borrowing);

        // Get all the borrowingList where borrowingDate is less than or equal to DEFAULT_BORROWING_DATE
        defaultBorrowingShouldBeFound("borrowingDate.lessThanOrEqual=" + DEFAULT_BORROWING_DATE);

        // Get all the borrowingList where borrowingDate is less than or equal to SMALLER_BORROWING_DATE
        defaultBorrowingShouldNotBeFound("borrowingDate.lessThanOrEqual=" + SMALLER_BORROWING_DATE);
    }

    @Test
    @Transactional
    void getAllBorrowingsByBorrowingDateIsLessThanSomething() throws Exception {
        // Initialize the database
        borrowingRepository.saveAndFlush(borrowing);

        // Get all the borrowingList where borrowingDate is less than DEFAULT_BORROWING_DATE
        defaultBorrowingShouldNotBeFound("borrowingDate.lessThan=" + DEFAULT_BORROWING_DATE);

        // Get all the borrowingList where borrowingDate is less than UPDATED_BORROWING_DATE
        defaultBorrowingShouldBeFound("borrowingDate.lessThan=" + UPDATED_BORROWING_DATE);
    }

    @Test
    @Transactional
    void getAllBorrowingsByBorrowingDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        borrowingRepository.saveAndFlush(borrowing);

        // Get all the borrowingList where borrowingDate is greater than DEFAULT_BORROWING_DATE
        defaultBorrowingShouldNotBeFound("borrowingDate.greaterThan=" + DEFAULT_BORROWING_DATE);

        // Get all the borrowingList where borrowingDate is greater than SMALLER_BORROWING_DATE
        defaultBorrowingShouldBeFound("borrowingDate.greaterThan=" + SMALLER_BORROWING_DATE);
    }

    @Test
    @Transactional
    void getAllBorrowingsByDeliveryDateIsEqualToSomething() throws Exception {
        // Initialize the database
        borrowingRepository.saveAndFlush(borrowing);

        // Get all the borrowingList where deliveryDate equals to DEFAULT_DELIVERY_DATE
        defaultBorrowingShouldBeFound("deliveryDate.equals=" + DEFAULT_DELIVERY_DATE);

        // Get all the borrowingList where deliveryDate equals to UPDATED_DELIVERY_DATE
        defaultBorrowingShouldNotBeFound("deliveryDate.equals=" + UPDATED_DELIVERY_DATE);
    }

    @Test
    @Transactional
    void getAllBorrowingsByDeliveryDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        borrowingRepository.saveAndFlush(borrowing);

        // Get all the borrowingList where deliveryDate not equals to DEFAULT_DELIVERY_DATE
        defaultBorrowingShouldNotBeFound("deliveryDate.notEquals=" + DEFAULT_DELIVERY_DATE);

        // Get all the borrowingList where deliveryDate not equals to UPDATED_DELIVERY_DATE
        defaultBorrowingShouldBeFound("deliveryDate.notEquals=" + UPDATED_DELIVERY_DATE);
    }

    @Test
    @Transactional
    void getAllBorrowingsByDeliveryDateIsInShouldWork() throws Exception {
        // Initialize the database
        borrowingRepository.saveAndFlush(borrowing);

        // Get all the borrowingList where deliveryDate in DEFAULT_DELIVERY_DATE or UPDATED_DELIVERY_DATE
        defaultBorrowingShouldBeFound("deliveryDate.in=" + DEFAULT_DELIVERY_DATE + "," + UPDATED_DELIVERY_DATE);

        // Get all the borrowingList where deliveryDate equals to UPDATED_DELIVERY_DATE
        defaultBorrowingShouldNotBeFound("deliveryDate.in=" + UPDATED_DELIVERY_DATE);
    }

    @Test
    @Transactional
    void getAllBorrowingsByDeliveryDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        borrowingRepository.saveAndFlush(borrowing);

        // Get all the borrowingList where deliveryDate is not null
        defaultBorrowingShouldBeFound("deliveryDate.specified=true");

        // Get all the borrowingList where deliveryDate is null
        defaultBorrowingShouldNotBeFound("deliveryDate.specified=false");
    }

    @Test
    @Transactional
    void getAllBorrowingsByDeliveryDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        borrowingRepository.saveAndFlush(borrowing);

        // Get all the borrowingList where deliveryDate is greater than or equal to DEFAULT_DELIVERY_DATE
        defaultBorrowingShouldBeFound("deliveryDate.greaterThanOrEqual=" + DEFAULT_DELIVERY_DATE);

        // Get all the borrowingList where deliveryDate is greater than or equal to UPDATED_DELIVERY_DATE
        defaultBorrowingShouldNotBeFound("deliveryDate.greaterThanOrEqual=" + UPDATED_DELIVERY_DATE);
    }

    @Test
    @Transactional
    void getAllBorrowingsByDeliveryDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        borrowingRepository.saveAndFlush(borrowing);

        // Get all the borrowingList where deliveryDate is less than or equal to DEFAULT_DELIVERY_DATE
        defaultBorrowingShouldBeFound("deliveryDate.lessThanOrEqual=" + DEFAULT_DELIVERY_DATE);

        // Get all the borrowingList where deliveryDate is less than or equal to SMALLER_DELIVERY_DATE
        defaultBorrowingShouldNotBeFound("deliveryDate.lessThanOrEqual=" + SMALLER_DELIVERY_DATE);
    }

    @Test
    @Transactional
    void getAllBorrowingsByDeliveryDateIsLessThanSomething() throws Exception {
        // Initialize the database
        borrowingRepository.saveAndFlush(borrowing);

        // Get all the borrowingList where deliveryDate is less than DEFAULT_DELIVERY_DATE
        defaultBorrowingShouldNotBeFound("deliveryDate.lessThan=" + DEFAULT_DELIVERY_DATE);

        // Get all the borrowingList where deliveryDate is less than UPDATED_DELIVERY_DATE
        defaultBorrowingShouldBeFound("deliveryDate.lessThan=" + UPDATED_DELIVERY_DATE);
    }

    @Test
    @Transactional
    void getAllBorrowingsByDeliveryDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        borrowingRepository.saveAndFlush(borrowing);

        // Get all the borrowingList where deliveryDate is greater than DEFAULT_DELIVERY_DATE
        defaultBorrowingShouldNotBeFound("deliveryDate.greaterThan=" + DEFAULT_DELIVERY_DATE);

        // Get all the borrowingList where deliveryDate is greater than SMALLER_DELIVERY_DATE
        defaultBorrowingShouldBeFound("deliveryDate.greaterThan=" + SMALLER_DELIVERY_DATE);
    }

    @Test
    @Transactional
    void getAllBorrowingsByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        borrowingRepository.saveAndFlush(borrowing);
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            user = UserResourceIT.createEntity(em);
            em.persist(user);
            em.flush();
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        borrowing.setUser(user);
        borrowingRepository.saveAndFlush(borrowing);
        Long userId = user.getId();

        // Get all the borrowingList where user equals to userId
        defaultBorrowingShouldBeFound("userId.equals=" + userId);

        // Get all the borrowingList where user equals to (userId + 1)
        defaultBorrowingShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    @Test
    @Transactional
    void getAllBorrowingsByBookIsEqualToSomething() throws Exception {
        // Initialize the database
        borrowingRepository.saveAndFlush(borrowing);
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
        borrowing.setBook(book);
        borrowingRepository.saveAndFlush(borrowing);
        Long bookId = book.getId();

        // Get all the borrowingList where book equals to bookId
        defaultBorrowingShouldBeFound("bookId.equals=" + bookId);

        // Get all the borrowingList where book equals to (bookId + 1)
        defaultBorrowingShouldNotBeFound("bookId.equals=" + (bookId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBorrowingShouldBeFound(String filter) throws Exception {
        restBorrowingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(borrowing.getId().intValue())))
            .andExpect(jsonPath("$.[*].borrowingDate").value(hasItem(DEFAULT_BORROWING_DATE.toString())))
            .andExpect(jsonPath("$.[*].deliveryDate").value(hasItem(DEFAULT_DELIVERY_DATE.toString())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())));

        // Check, that the count call also returns 1
        restBorrowingMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBorrowingShouldNotBeFound(String filter) throws Exception {
        restBorrowingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBorrowingMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBorrowing() throws Exception {
        // Get the borrowing
        restBorrowingMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewBorrowing() throws Exception {
        // Initialize the database
        borrowingRepository.saveAndFlush(borrowing);

        int databaseSizeBeforeUpdate = borrowingRepository.findAll().size();

        // Update the borrowing
        Borrowing updatedBorrowing = borrowingRepository.findById(borrowing.getId()).get();
        // Disconnect from session so that the updates on updatedBorrowing are not directly saved in db
        em.detach(updatedBorrowing);
        updatedBorrowing.borrowingDate(UPDATED_BORROWING_DATE).deliveryDate(UPDATED_DELIVERY_DATE).comment(UPDATED_COMMENT);

        restBorrowingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBorrowing.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBorrowing))
            )
            .andExpect(status().isOk());

        // Validate the Borrowing in the database
        List<Borrowing> borrowingList = borrowingRepository.findAll();
        assertThat(borrowingList).hasSize(databaseSizeBeforeUpdate);
        Borrowing testBorrowing = borrowingList.get(borrowingList.size() - 1);
        assertThat(testBorrowing.getBorrowingDate()).isEqualTo(UPDATED_BORROWING_DATE);
        assertThat(testBorrowing.getDeliveryDate()).isEqualTo(UPDATED_DELIVERY_DATE);
        assertThat(testBorrowing.getComment()).isEqualTo(UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void putNonExistingBorrowing() throws Exception {
        int databaseSizeBeforeUpdate = borrowingRepository.findAll().size();
        borrowing.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBorrowingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, borrowing.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(borrowing))
            )
            .andExpect(status().isBadRequest());

        // Validate the Borrowing in the database
        List<Borrowing> borrowingList = borrowingRepository.findAll();
        assertThat(borrowingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBorrowing() throws Exception {
        int databaseSizeBeforeUpdate = borrowingRepository.findAll().size();
        borrowing.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBorrowingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(borrowing))
            )
            .andExpect(status().isBadRequest());

        // Validate the Borrowing in the database
        List<Borrowing> borrowingList = borrowingRepository.findAll();
        assertThat(borrowingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBorrowing() throws Exception {
        int databaseSizeBeforeUpdate = borrowingRepository.findAll().size();
        borrowing.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBorrowingMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(borrowing))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Borrowing in the database
        List<Borrowing> borrowingList = borrowingRepository.findAll();
        assertThat(borrowingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBorrowingWithPatch() throws Exception {
        // Initialize the database
        borrowingRepository.saveAndFlush(borrowing);

        int databaseSizeBeforeUpdate = borrowingRepository.findAll().size();

        // Update the borrowing using partial update
        Borrowing partialUpdatedBorrowing = new Borrowing();
        partialUpdatedBorrowing.setId(borrowing.getId());

        partialUpdatedBorrowing.deliveryDate(UPDATED_DELIVERY_DATE).comment(UPDATED_COMMENT);

        restBorrowingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBorrowing.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBorrowing))
            )
            .andExpect(status().isOk());

        // Validate the Borrowing in the database
        List<Borrowing> borrowingList = borrowingRepository.findAll();
        assertThat(borrowingList).hasSize(databaseSizeBeforeUpdate);
        Borrowing testBorrowing = borrowingList.get(borrowingList.size() - 1);
        assertThat(testBorrowing.getBorrowingDate()).isEqualTo(DEFAULT_BORROWING_DATE);
        assertThat(testBorrowing.getDeliveryDate()).isEqualTo(UPDATED_DELIVERY_DATE);
        assertThat(testBorrowing.getComment()).isEqualTo(UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void fullUpdateBorrowingWithPatch() throws Exception {
        // Initialize the database
        borrowingRepository.saveAndFlush(borrowing);

        int databaseSizeBeforeUpdate = borrowingRepository.findAll().size();

        // Update the borrowing using partial update
        Borrowing partialUpdatedBorrowing = new Borrowing();
        partialUpdatedBorrowing.setId(borrowing.getId());

        partialUpdatedBorrowing.borrowingDate(UPDATED_BORROWING_DATE).deliveryDate(UPDATED_DELIVERY_DATE).comment(UPDATED_COMMENT);

        restBorrowingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBorrowing.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBorrowing))
            )
            .andExpect(status().isOk());

        // Validate the Borrowing in the database
        List<Borrowing> borrowingList = borrowingRepository.findAll();
        assertThat(borrowingList).hasSize(databaseSizeBeforeUpdate);
        Borrowing testBorrowing = borrowingList.get(borrowingList.size() - 1);
        assertThat(testBorrowing.getBorrowingDate()).isEqualTo(UPDATED_BORROWING_DATE);
        assertThat(testBorrowing.getDeliveryDate()).isEqualTo(UPDATED_DELIVERY_DATE);
        assertThat(testBorrowing.getComment()).isEqualTo(UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void patchNonExistingBorrowing() throws Exception {
        int databaseSizeBeforeUpdate = borrowingRepository.findAll().size();
        borrowing.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBorrowingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, borrowing.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(borrowing))
            )
            .andExpect(status().isBadRequest());

        // Validate the Borrowing in the database
        List<Borrowing> borrowingList = borrowingRepository.findAll();
        assertThat(borrowingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBorrowing() throws Exception {
        int databaseSizeBeforeUpdate = borrowingRepository.findAll().size();
        borrowing.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBorrowingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(borrowing))
            )
            .andExpect(status().isBadRequest());

        // Validate the Borrowing in the database
        List<Borrowing> borrowingList = borrowingRepository.findAll();
        assertThat(borrowingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBorrowing() throws Exception {
        int databaseSizeBeforeUpdate = borrowingRepository.findAll().size();
        borrowing.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBorrowingMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(borrowing))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Borrowing in the database
        List<Borrowing> borrowingList = borrowingRepository.findAll();
        assertThat(borrowingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBorrowing() throws Exception {
        // Initialize the database
        borrowingRepository.saveAndFlush(borrowing);

        int databaseSizeBeforeDelete = borrowingRepository.findAll().size();

        // Delete the borrowing
        restBorrowingMockMvc
            .perform(delete(ENTITY_API_URL_ID, borrowing.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Borrowing> borrowingList = borrowingRepository.findAll();
        assertThat(borrowingList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
