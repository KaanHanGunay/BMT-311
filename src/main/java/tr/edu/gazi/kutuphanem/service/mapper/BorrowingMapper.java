package tr.edu.gazi.kutuphanem.service.mapper;

import org.mapstruct.*;
import tr.edu.gazi.kutuphanem.domain.Borrowing;
import tr.edu.gazi.kutuphanem.service.dto.BorrowingDTO;

/**
 * Mapper for the entity {@link Borrowing} and its DTO {@link BorrowingDTO}.
 */
@Mapper(componentModel = "spring", uses = { UserMapper.class, BookMapper.class })
public interface BorrowingMapper extends EntityMapper<BorrowingDTO, Borrowing> {
    @Mapping(target = "user", source = "user", qualifiedByName = "login")
    @Mapping(target = "book", source = "book", qualifiedByName = "id")
    BorrowingDTO toDto(Borrowing s);
}
