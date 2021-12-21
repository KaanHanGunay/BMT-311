package tr.edu.gazi.kutuphanem.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BorrowingMapperTest {

    private BorrowingMapper borrowingMapper;

    @BeforeEach
    public void setUp() {
        borrowingMapper = new BorrowingMapperImpl();
    }
}
