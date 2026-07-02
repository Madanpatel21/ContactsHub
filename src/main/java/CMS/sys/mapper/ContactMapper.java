package CMS.sys.mapper;

import CMS.sys.dto.ContactRequestDTO;
import CMS.sys.dto.ContactResponseDTO;
import CMS.sys.entity.Contact;
import CMS.sys.entity.EmailAddress;
import CMS.sys.entity.PhoneNumber;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.geo.Point;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ContactMapper {

    Contact toEntity(ContactRequestDTO requestDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ContactResponseDTO toResponseDTO(Contact contact);

    void updateEntityFromDTO(ContactRequestDTO requestDTO, @MappingTarget Contact contact);

    default Point map(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return null;
        }
        return new Point(longitude, latitude);
    }

    @org.mapstruct.AfterMapping
    default void afterMapping(@org.mapstruct.MappingTarget Contact contact, ContactRequestDTO requestDTO) {
        if (requestDTO.getLatitude() != null && requestDTO.getLongitude() != null) {
            contact.setLocation(map(requestDTO.getLatitude(), requestDTO.getLongitude()));
        }
    }

    default PhoneNumber mapPhoneNumber(PhoneNumber source) {
        if (source == null) return null;
        return PhoneNumber.builder()
                .number(source.getNumber())
                .type(source.getType())
                .build();
    }

    default List<PhoneNumber> mapPhoneNumbers(List<PhoneNumber> source) {
        if (source == null) return null;
        return source.stream().map(this::mapPhoneNumber).toList();
    }

    default EmailAddress mapEmailAddress(EmailAddress source) {
        if (source == null) return null;
        return EmailAddress.builder()
                .email(source.getEmail())
                .type(source.getType())
                .build();
    }

    default List<EmailAddress> mapEmailAddresses(List<EmailAddress> source) {
        if (source == null) return null;
        return source.stream().map(this::mapEmailAddress).toList();
    }
}