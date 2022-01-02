package tr.edu.gazi.kutuphane.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link tr.edu.gazi.kutuphane.domain.Book} entity. This class is used
 * in {@link tr.edu.gazi.kutuphane.web.rest.BookResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /books?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class BookCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private IntegerFilter numOfPage;

    private StringFilter coverUrl;

    private StringFilter publisher;

    private LongFilter authorId;

    private LongFilter borrowingId;

    private Boolean distinct;

    public BookCriteria() {}

    public BookCriteria(BookCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.numOfPage = other.numOfPage == null ? null : other.numOfPage.copy();
        this.coverUrl = other.coverUrl == null ? null : other.coverUrl.copy();
        this.publisher = other.publisher == null ? null : other.publisher.copy();
        this.authorId = other.authorId == null ? null : other.authorId.copy();
        this.borrowingId = other.borrowingId == null ? null : other.borrowingId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public BookCriteria copy() {
        return new BookCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getTitle() {
        return title;
    }

    public StringFilter title() {
        if (title == null) {
            title = new StringFilter();
        }
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public IntegerFilter getNumOfPage() {
        return numOfPage;
    }

    public IntegerFilter numOfPage() {
        if (numOfPage == null) {
            numOfPage = new IntegerFilter();
        }
        return numOfPage;
    }

    public void setNumOfPage(IntegerFilter numOfPage) {
        this.numOfPage = numOfPage;
    }

    public StringFilter getCoverUrl() {
        return coverUrl;
    }

    public StringFilter coverUrl() {
        if (coverUrl == null) {
            coverUrl = new StringFilter();
        }
        return coverUrl;
    }

    public void setCoverUrl(StringFilter coverUrl) {
        this.coverUrl = coverUrl;
    }

    public StringFilter getPublisher() {
        return publisher;
    }

    public StringFilter publisher() {
        if (publisher == null) {
            publisher = new StringFilter();
        }
        return publisher;
    }

    public void setPublisher(StringFilter publisher) {
        this.publisher = publisher;
    }

    public LongFilter getAuthorId() {
        return authorId;
    }

    public LongFilter authorId() {
        if (authorId == null) {
            authorId = new LongFilter();
        }
        return authorId;
    }

    public void setAuthorId(LongFilter authorId) {
        this.authorId = authorId;
    }

    public LongFilter getBorrowingId() {
        return borrowingId;
    }

    public LongFilter borrowingId() {
        if (borrowingId == null) {
            borrowingId = new LongFilter();
        }
        return borrowingId;
    }

    public void setBorrowingId(LongFilter borrowingId) {
        this.borrowingId = borrowingId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BookCriteria that = (BookCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(numOfPage, that.numOfPage) &&
            Objects.equals(coverUrl, that.coverUrl) &&
            Objects.equals(publisher, that.publisher) &&
            Objects.equals(authorId, that.authorId) &&
            Objects.equals(borrowingId, that.borrowingId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, numOfPage, coverUrl, publisher, authorId, borrowingId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (title != null ? "title=" + title + ", " : "") +
            (numOfPage != null ? "numOfPage=" + numOfPage + ", " : "") +
            (coverUrl != null ? "coverUrl=" + coverUrl + ", " : "") +
            (publisher != null ? "publisher=" + publisher + ", " : "") +
            (authorId != null ? "authorId=" + authorId + ", " : "") +
            (borrowingId != null ? "borrowingId=" + borrowingId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
