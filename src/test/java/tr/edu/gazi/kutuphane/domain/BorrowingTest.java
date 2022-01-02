package tr.edu.gazi.kutuphane.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import tr.edu.gazi.kutuphane.web.rest.TestUtil;

class BorrowingTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Borrowing.class);
        Borrowing borrowing1 = new Borrowing();
        borrowing1.setId(1L);
        Borrowing borrowing2 = new Borrowing();
        borrowing2.setId(borrowing1.getId());
        assertThat(borrowing1).isEqualTo(borrowing2);
        borrowing2.setId(2L);
        assertThat(borrowing1).isNotEqualTo(borrowing2);
        borrowing1.setId(null);
        assertThat(borrowing1).isNotEqualTo(borrowing2);
    }
}
