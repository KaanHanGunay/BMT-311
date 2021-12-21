package tr.edu.gazi.kutuphanem.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Borrowing.
 */
@Entity
@Table(name = "borrowing")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Borrowing implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "borrowing_date")
    private LocalDate borrowingDate;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @Lob
    @Column(name = "jhi_comment")
    private String comment;

    @ManyToOne
    private User user;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "author", "publisher" }, allowSetters = true)
    private Book book;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Borrowing id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getBorrowingDate() {
        return this.borrowingDate;
    }

    public Borrowing borrowingDate(LocalDate borrowingDate) {
        this.setBorrowingDate(borrowingDate);
        return this;
    }

    public void setBorrowingDate(LocalDate borrowingDate) {
        this.borrowingDate = borrowingDate;
    }

    public LocalDate getDeliveryDate() {
        return this.deliveryDate;
    }

    public Borrowing deliveryDate(LocalDate deliveryDate) {
        this.setDeliveryDate(deliveryDate);
        return this;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getComment() {
        return this.comment;
    }

    public Borrowing comment(String comment) {
        this.setComment(comment);
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Borrowing user(User user) {
        this.setUser(user);
        return this;
    }

    public Book getBook() {
        return this.book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Borrowing book(Book book) {
        this.setBook(book);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Borrowing)) {
            return false;
        }
        return id != null && id.equals(((Borrowing) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Borrowing{" +
            "id=" + getId() +
            ", borrowingDate='" + getBorrowingDate() + "'" +
            ", deliveryDate='" + getDeliveryDate() + "'" +
            ", comment='" + getComment() + "'" +
            "}";
    }
}
