package tr.edu.gazi.kutuphanem.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import javax.persistence.Lob;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link tr.edu.gazi.kutuphanem.domain.Borrowing} entity.
 */
public class BorrowingDTO implements Serializable {

    private Long id;

    private LocalDate borrowingDate;

    private LocalDate deliveryDate;

    @Lob
    private String comment;

    private UserDTO user;

    private BookDTO book;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getBorrowingDate() {
        return borrowingDate;
    }

    public void setBorrowingDate(LocalDate borrowingDate) {
        this.borrowingDate = borrowingDate;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public BookDTO getBook() {
        return book;
    }

    public void setBook(BookDTO book) {
        this.book = book;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BorrowingDTO)) {
            return false;
        }

        BorrowingDTO borrowingDTO = (BorrowingDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, borrowingDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BorrowingDTO{" +
            "id=" + getId() +
            ", borrowingDate='" + getBorrowingDate() + "'" +
            ", deliveryDate='" + getDeliveryDate() + "'" +
            ", comment='" + getComment() + "'" +
            ", user=" + getUser() +
            ", book=" + getBook() +
            "}";
    }
}
