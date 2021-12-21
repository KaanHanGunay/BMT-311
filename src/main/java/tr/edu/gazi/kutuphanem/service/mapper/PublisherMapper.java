package tr.edu.gazi.kutuphanem.service.mapper;

import org.mapstruct.*;
import tr.edu.gazi.kutuphanem.domain.Publisher;
import tr.edu.gazi.kutuphanem.service.dto.PublisherDTO;

/**
 * Mapper for the entity {@link Publisher} and its DTO {@link PublisherDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface PublisherMapper extends EntityMapper<PublisherDTO, Publisher> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PublisherDTO toDtoId(Publisher publisher);
}
