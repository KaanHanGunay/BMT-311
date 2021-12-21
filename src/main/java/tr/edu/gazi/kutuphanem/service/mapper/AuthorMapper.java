package tr.edu.gazi.kutuphanem.service.mapper;

import org.mapstruct.*;
import tr.edu.gazi.kutuphanem.domain.Author;
import tr.edu.gazi.kutuphanem.service.dto.AuthorDTO;

/**
 * Mapper for the entity {@link Author} and its DTO {@link AuthorDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface AuthorMapper extends EntityMapper<AuthorDTO, Author> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AuthorDTO toDtoId(Author author);
}
