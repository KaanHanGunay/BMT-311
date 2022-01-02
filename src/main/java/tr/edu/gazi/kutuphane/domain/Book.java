package tr.edu.gazi.kutuphane.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Book.
 */
@Entity
@Table(name = "book")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "num_of_page")
    private Integer numOfPage;

    @Column(name = "cover_url")
    private String coverUrl;

    @Column(name = "publisher")
    private String publisher;

    @ManyToOne
    @JsonIgnoreProperties(value = { "books" }, allowSetters = true)
    private Author author;

    @OneToMany(mappedBy = "book")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "user", "book" }, allowSetters = true)
    private Set<Borrowing> borrowings = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Book id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Book title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getNumOfPage() {
        return this.numOfPage;
    }

    public Book numOfPage(Integer numOfPage) {
        this.setNumOfPage(numOfPage);
        return this;
    }

    public void setNumOfPage(Integer numOfPage) {
        this.numOfPage = numOfPage;
    }

    public String getCoverUrl() {
        return this.coverUrl;
    }

    public Book coverUrl(String coverUrl) {
        this.setCoverUrl(coverUrl);
        return this;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getPublisher() {
        return this.publisher;
    }

    public Book publisher(String publisher) {
        this.setPublisher(publisher);
        return this;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Author getAuthor() {
        return this.author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Book author(Author author) {
        this.setAuthor(author);
        return this;
    }

    public Set<Borrowing> getBorrowings() {
        return this.borrowings;
    }

    public void setBorrowings(Set<Borrowing> borrowings) {
        if (this.borrowings != null) {
            this.borrowings.forEach(i -> i.setBook(null));
        }
        if (borrowings != null) {
            borrowings.forEach(i -> i.setBook(this));
        }
        this.borrowings = borrowings;
    }

    public Book borrowings(Set<Borrowing> borrowings) {
        this.setBorrowings(borrowings);
        return this;
    }

    public Book addBorrowing(Borrowing borrowing) {
        this.borrowings.add(borrowing);
        borrowing.setBook(this);
        return this;
    }

    public Book removeBorrowing(Borrowing borrowing) {
        this.borrowings.remove(borrowing);
        borrowing.setBook(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Book)) {
            return false;
        }
        return id != null && id.equals(((Book) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Book{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", numOfPage=" + getNumOfPage() +
            ", coverUrl='" + getCoverUrl() + "'" +
            ", publisher='" + getPublisher() + "'" +
            "}";
    }
}
