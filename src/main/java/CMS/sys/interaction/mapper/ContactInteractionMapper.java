package CMS.sys.interaction.mapper;

import CMS.sys.entity.ContactInteraction;
import CMS.sys.interaction.dto.ContactInteractionRequestDTO;
import CMS.sys.interaction.dto.ContactInteractionResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ContactInteractionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contactId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ContactInteraction toEntity(ContactInteractionRequestDTO requestDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contactId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ContactInteractionResponseDTO toResponseDTO(ContactInteraction interaction);

    void updateEntityFromDTO(ContactInteractionRequestDTO requestDTO, @MappingTarget ContactInteraction interaction);
}