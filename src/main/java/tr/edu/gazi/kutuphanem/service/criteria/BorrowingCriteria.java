package tr.edu.gazi.kutuphanem.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LocalDateFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link tr.edu.gazi.kutuphanem.domain.Borrowing} entity. This class is used
 * in {@link tr.edu.gazi.kutuphanem.web.rest.BorrowingResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /borrowings?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class BorrowingCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LocalDateFilter borrowingDate;

    private LocalDateFilter deliveryDate;

    private LongFilter userId;

    private LongFilter bookId;

    private Boolean distinct;

    public BorrowingCriteria() {}

    public BorrowingCriteria(BorrowingCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.borrowingDate = other.borrowingDate == null ? null : other.borrowingDate.copy();
        this.deliveryDate = other.deliveryDate == null ? null : other.deliveryDate.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.bookId = other.bookId == null ? null : other.bookId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public BorrowingCriteria copy() {
        return new BorrowingCriteria(this);
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

    public LocalDateFilter getBorrowingDate() {
        return borrowingDate;
    }

    public LocalDateFilter borrowingDate() {
        if (borrowingDate == null) {
            borrowingDate = new LocalDateFilter();
        }
        return borrowingDate;
    }

    public void setBorrowingDate(LocalDateFilter borrowingDate) {
        this.borrowingDate = borrowingDate;
    }

    public LocalDateFilter getDeliveryDate() {
        return deliveryDate;
    }

    public LocalDateFilter deliveryDate() {
        if (deliveryDate == null) {
            deliveryDate = new LocalDateFilter();
        }
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDateFilter deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public LongFilter userId() {
        if (userId == null) {
            userId = new LongFilter();
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public LongFilter getBookId() {
        return bookId;
    }

    public LongFilter bookId() {
        if (bookId == null) {
            bookId = new LongFilter();
        }
        return bookId;
    }

    public void setBookId(LongFilter bookId) {
        this.bookId = bookId;
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
        final BorrowingCriteria that = (BorrowingCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(borrowingDate, that.borrowingDate) &&
            Objects.equals(deliveryDate, that.deliveryDate) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(bookId, that.bookId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, borrowingDate, deliveryDate, userId, bookId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BorrowingCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (borrowingDate != null ? "borrowingDate=" + borrowingDate + ", " : "") +
            (deliveryDate != null ? "deliveryDate=" + deliveryDate + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (bookId != null ? "bookId=" + bookId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
