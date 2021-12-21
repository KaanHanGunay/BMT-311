package tr.edu.gazi.kutuphanem.service.mapper;

import org.mapstruct.*;
import tr.edu.gazi.kutuphanem.domain.Book;
import tr.edu.gazi.kutuphanem.service.dto.BookDTO;

/**
 * Mapper for the entity {@link Book} and its DTO {@link BookDTO}.
 */
@Mapper(componentModel = "spring", uses = { AuthorMapper.class, PublisherMapper.class })
public interface BookMapper extends EntityMapper<BookDTO, Book> {
    @Mapping(target = "author", source = "author", qualifiedByName = "id")
    @Mapping(target = "publisher", source = "publisher", qualifiedByName = "id")
    BookDTO toDto(Book s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BookDTO toDtoId(Book book);
}
