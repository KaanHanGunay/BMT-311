package tr.edu.gazi.kutuphanem.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import tr.edu.gazi.kutuphanem.web.rest.TestUtil;

class BorrowingDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BorrowingDTO.class);
        BorrowingDTO borrowingDTO1 = new BorrowingDTO();
        borrowingDTO1.setId(1L);
        BorrowingDTO borrowingDTO2 = new BorrowingDTO();
        assertThat(borrowingDTO1).isNotEqualTo(borrowingDTO2);
        borrowingDTO2.setId(borrowingDTO1.getId());
        assertThat(borrowingDTO1).isEqualTo(borrowingDTO2);
        borrowingDTO2.setId(2L);
        assertThat(borrowingDTO1).isNotEqualTo(borrowingDTO2);
        borrowingDTO1.setId(null);
        assertThat(borrowingDTO1).isNotEqualTo(borrowingDTO2);
    }
}
