package CMS.sys.service.impl;

import CMS.sys.constant.ApiConstants;
import CMS.sys.dto.ContactRequestDTO;
import CMS.sys.dto.ContactResponseDTO;
import CMS.sys.entity.Contact;
import CMS.sys.entity.ContactInteraction;
import CMS.sys.event.ContactEventType;
import CMS.sys.exception.DuplicateResourceException;
import CMS.sys.exception.ResourceNotFoundException;
import CMS.sys.kafka.ContactEventProducer;
import CMS.sys.mapper.ContactMapper;
import CMS.sys.repository.ContactRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceImplTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private ContactMapper contactMapper;

    @Mock
    private ContactEventProducer contactEventProducer;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private ContactInteractionRepository interactionRepository;

    @InjectMocks
    private ContactServiceImpl contactService;

    private ContactRequestDTO contactRequestDTO;
    private Contact contact;
    private ContactResponseDTO contactResponseDTO;

    @BeforeEach
    void setUp() {
        contactRequestDTO = ContactRequestDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .nickname("Johnny")
                .mobileNumber("+1234567890")
                .email("john.doe@example.com")
                .company("Acme Corp")
                .designation("Engineer")
                .address("123 Main St")
                .city("New York")
                .state("NY")
                .country("USA")
                .notes("Test contact")
                .favorite(false)
                .build();

        contact = Contact.builder()
                .id("507f1f77bcf86cd799439011")
                .firstName("John")
                .lastName("Doe")
                .nickname("Johnny")
                .mobileNumber("+1234567890")
                .email("john.doe@example.com")
                .company("Acme Corp")
                .designation("Engineer")
                .address("123 Main St")
                .city("New York")
                .state("NY")
                .country("USA")
                .notes("Test contact")
                .favorite(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        contactResponseDTO = ContactResponseDTO.builder()
                .id("507f1f77bcf86cd799439011")
                .firstName("John")
                .lastName("Doe")
                .nickname("Johnny")
                .mobileNumber("+1234567890")
                .email("john.doe@example.com")
                .company("Acme Corp")
                .designation("Engineer")
                .address("123 Main St")
                .city("New York")
                .state("NY")
                .country("USA")
                .notes("Test contact")
                .favorite(false)
                .build();
    }

    @Test
    @DisplayName("Should create contact successfully when email and mobile are unique")
    void createContact_ShouldCreateContact_WhenValidData() {
        when(contactRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(contactRepository.findByMobileNumber(any())).thenReturn(Optional.empty());
        when(contactMapper.toEntity(any(ContactRequestDTO.class))).thenReturn(contact);
        when(contactRepository.save(any(Contact.class))).thenReturn(contact);
        when(contactMapper.toResponseDTO(any(Contact.class))).thenReturn(contactResponseDTO);

        ContactResponseDTO result = contactService.createContact(contactRequestDTO);

        assertNotNull(result);
        assertEquals(contactResponseDTO.getId(), result.getId());
        verify(contactRepository, times(1)).save(any(Contact.class));
        verify(contactEventProducer, times(1)).publishContactEvent(eq(ContactEventType.CREATED), eq(contact.getId()), any(Contact.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when email already exists")
    void createContact_ShouldThrowException_WhenEmailExists() {
        when(contactRepository.findByEmail(any())).thenReturn(Optional.of(contact));

        assertThrows(DuplicateResourceException.class,
                () -> contactService.createContact(contactRequestDTO));

        verify(contactRepository, never()).save(any(Contact.class));
        verify(contactEventProducer, never()).publishContactEvent(any(ContactEventType.class), any(String.class), any(Contact.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when mobile number already exists")
    void createContact_ShouldThrowException_WhenMobileExists() {
        when(contactRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(contactRepository.findByMobileNumber(any())).thenReturn(Optional.of(contact));

        assertThrows(DuplicateResourceException.class,
                () -> contactService.createContact(contactRequestDTO));

        verify(contactRepository, never()).save(any(Contact.class));
        verify(contactEventProducer, never()).publishContactEvent(any(ContactEventType.class), any(String.class), any(Contact.class));
    }

    @Test
    @DisplayName("Should update contact successfully when contact exists")
    void updateContact_ShouldUpdateContact_WhenContactExists() {
        Contact updatedContact = Contact.builder()
                .id("507f1f77bcf86cd799439011")
                .firstName("Jane")
                .lastName("Doe")
                .build();
        ContactResponseDTO updatedResponse = ContactResponseDTO.builder()
                .id("507f1f77bcf86cd799439011")
                .firstName("Jane")
                .lastName("Doe")
                .build();

        when(contactRepository.findById("507f1f77bcf86cd799439011")).thenReturn(Optional.of(contact));
        when(contactRepository.existsByEmailAndIdNot(contactRequestDTO.getEmail(), contact.getId())).thenReturn(false);
        when(contactRepository.existsByMobileNumberAndIdNot(contactRequestDTO.getMobileNumber(), contact.getId())).thenReturn(false);
        when(contactMapper.toResponseDTO(any(Contact.class))).thenReturn(updatedResponse);
        when(contactRepository.save(any(Contact.class))).thenReturn(updatedContact);

        ContactResponseDTO result = contactService.updateContact("507f1f77bcf86cd799439011", contactRequestDTO);

        assertNotNull(result);
        verify(contactMapper, times(1)).updateEntityFromDTO(eq(contactRequestDTO), eq(contact));
        verify(contactRepository, times(1)).save(contact);
        verify(contactEventProducer, times(1)).publishContactEvent(eq(ContactEventType.UPDATED), eq(contact.getId()), any(Contact.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when updating with existing email")
    void updateContact_ShouldThrowException_WhenEmailExists() {
        when(contactRepository.findById("507f1f77bcf86cd799439011")).thenReturn(Optional.of(contact));
        when(contactRepository.existsByEmailAndIdNot(contactRequestDTO.getEmail(), contact.getId())).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> contactService.updateContact(contact.getId(), contactRequestDTO));
        verify(contactRepository, never()).save(any(Contact.class));
        verify(contactEventProducer, never()).publishContactEvent(any(ContactEventType.class), any(String.class), any(Contact.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when updating with existing mobile number")
    void updateContact_ShouldThrowException_WhenMobileExists() {
        when(contactRepository.findById("507f1f77bcf86cd799439011")).thenReturn(Optional.of(contact));
        when(contactRepository.existsByMobileNumberAndIdNot(contactRequestDTO.getMobileNumber(), contact.getId())).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> contactService.updateContact(contact.getId(), contactRequestDTO));
        verify(contactRepository, never()).save(any(Contact.class));
        verify(contactEventProducer, never()).publishContactEvent(any(ContactEventType.class), any(String.class), any(Contact.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when contact not found during update")
    void updateContact_ShouldThrowException_WhenContactNotFound() {
        when(contactRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> contactService.updateContact("invalid-id", contactRequestDTO));
        verify(contactRepository, never()).save(any(Contact.class));
        verify(contactEventProducer, never()).publishContactEvent(any(ContactEventType.class), any(String.class), any(Contact.class));
    }

    @Test
    @DisplayName("Should get contact by ID when contact exists")
    void getContactById_ShouldReturnContact_WhenContactExists() {
        when(contactRepository.findById("507f1f77bcf86cd799439011")).thenReturn(Optional.of(contact));
        when(contactMapper.toResponseDTO(contact)).thenReturn(contactResponseDTO);

        ContactResponseDTO result = contactService.getContactById("507f1f77bcf86cd799439011");

        assertNotNull(result);
        verify(contactRepository, times(1)).findById("507f1f77bcf86cd799439011");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when contact not found during get")
    void getContactById_ShouldThrowException_WhenContactNotFound() {
        when(contactRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> contactService.getContactById("invalid-id"));
    }

    @Test
    @DisplayName("Should return all contacts")
    void getAllContacts_ShouldReturnAllContacts() {
        when(contactRepository.findAll()).thenReturn(List.of(contact));
        when(contactMapper.toResponseDTO(any(Contact.class))).thenReturn(contactResponseDTO);

        List<ContactResponseDTO> result = contactService.getAllContacts();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(contactRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should search contacts with pagination")
    void searchContacts_ShouldReturnPaginatedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Contact> contactPage = new PageImpl<>(List.of(contact), pageable, 1);

        when(contactRepository.findAll()).thenReturn(List.of(contact));
        when(contactMapper.toResponseDTO(any(Contact.class))).thenReturn(contactResponseDTO);

        Page<ContactResponseDTO> result = contactService.searchContacts("John", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when contact not found during delete")
    void deleteContact_ShouldThrowException_WhenContactNotFound() {
        when(contactRepository.existsById(any())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> contactService.deleteContact("invalid-id"));

        verify(contactRepository, never()).deleteById(any());
        verify(contactEventProducer, never()).publishContactEvent(any(ContactEventType.class), any(String.class), any(Contact.class));
    }

    @Test
    @DisplayName("Should delete contact successfully when contact exists")
    void deleteContact_ShouldDeleteContact_WhenContactExists() {
        when(contactRepository.existsById("507f1f77bcf86cd799439011")).thenReturn(true);
        doNothing().when(contactRepository).deleteById("507f1f77bcf86cd799439011");

        contactService.deleteContact("507f1f77bcf86cd799439011");

        verify(contactRepository, times(1)).deleteById("507f1f77bcf86cd799439011");
        verify(contactEventProducer, times(1)).publishContactEvent(eq(ContactEventType.DELETED), eq(contact.getId()), any(Contact.class));
    }

}