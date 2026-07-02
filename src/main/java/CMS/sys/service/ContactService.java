package CMS.sys.service;

import CMS.sys.dto.ContactRequestDTO;
import CMS.sys.dto.ContactResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ContactService {

    ContactResponseDTO createContact(ContactRequestDTO contactRequestDTO);

    ContactResponseDTO updateContact(String id, ContactRequestDTO contactRequestDTO);

    ContactResponseDTO getContactById(String id);

    List<ContactResponseDTO> getAllContacts();

    Page<ContactResponseDTO> searchContacts(String query, Pageable pageable);

    void deleteContact(String id);

    List<ContactResponseDTO> findContactsNearby(double latitude, double longitude, double radiusKm);
}