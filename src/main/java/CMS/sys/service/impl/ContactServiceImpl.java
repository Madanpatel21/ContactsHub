package CMS.sys.service.impl;

import CMS.sys.constant.ApiConstants;
import CMS.sys.dto.ContactRequestDTO;
import CMS.sys.dto.ContactResponseDTO;
import CMS.sys.entity.Contact;
import CMS.sys.entity.ContactInteraction;
import CMS.sys.event.ContactEventType;
import CMS.sys.exception.DuplicateResourceException;
import CMS.sys.exception.ResourceNotFoundException;
import CMS.sys.interaction.repository.ContactInteractionRepository;
import CMS.sys.kafka.ContactEventProducer;
import CMS.sys.mapper.ContactMapper;
import CMS.sys.repository.ContactRepository;
import CMS.sys.service.ContactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private static final String CONTACT_CACHE = "contacts";
    private static final String CONTACTS_CACHE = "contactsAll";

    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;
    private final ContactEventProducer contactEventProducer;
    private final MongoTemplate mongoTemplate;
    private final ContactInteractionRepository interactionRepository;

    @Override
    @Transactional
    public ContactResponseDTO createContact(ContactRequestDTO contactRequestDTO) {
        log.info("Creating new contact for email: {}", contactRequestDTO.getEmail());

        validateUniqueEmail(contactRequestDTO.getEmail());
        validateUniqueMobileNumber(contactRequestDTO.getMobileNumber());

        Contact contact = contactMapper.toEntity(contactRequestDTO);
        Contact savedContact = contactRepository.save(contact);

        log.info("Contact created successfully with ID: {}", savedContact.getId());

        contactEventProducer.publishContactEvent(ContactEventType.CREATED, savedContact.getId(), savedContact);

        return contactMapper.toResponseDTO(savedContact);
    }

    @Override
    @Transactional
    @CachePut(cacheNames = CONTACT_CACHE, key = "#result.id")
    public ContactResponseDTO updateContact(String id, ContactRequestDTO contactRequestDTO) {
        log.info("Updating contact with ID: {}", id);

        Contact existingContact = contactRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Contact not found with ID: {}", id);
                    return new ResourceNotFoundException(ApiConstants.CONTACT_NOT_FOUND + id);
                });

        if (contactRequestDTO.getEmail() != null &&
                contactRepository.existsByEmailAndIdNot(contactRequestDTO.getEmail(), id)) {
            log.warn("Duplicate email found during update: {}", contactRequestDTO.getEmail());
            throw new DuplicateResourceException(ApiConstants.DUPLICATE_EMAIL);
        }

        if (contactRequestDTO.getMobileNumber() != null &&
                contactRepository.existsByMobileNumberAndIdNot(contactRequestDTO.getMobileNumber(), id)) {
            log.warn("Duplicate mobile number found during update: {}", contactRequestDTO.getMobileNumber());
            throw new DuplicateResourceException(ApiConstants.DUPLICATE_MOBILE);
        }

        contactMapper.updateEntityFromDTO(contactRequestDTO, existingContact);
        Contact updatedContact = contactRepository.save(existingContact);

        log.info("Contact updated successfully with ID: {}", updatedContact.getId());

        contactEventProducer.publishContactEvent(ContactEventType.UPDATED, updatedContact.getId(), updatedContact);

        return contactMapper.toResponseDTO(updatedContact);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CONTACT_CACHE, key = "#id")
    public ContactResponseDTO getContactById(String id) {
        log.info("Retrieving contact with ID: {}", id);

        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Contact not found with ID: {}", id);
                    return new ResourceNotFoundException(ApiConstants.CONTACT_NOT_FOUND + id);
                });

        ContactResponseDTO response = contactMapper.toResponseDTO(contact);
        enrichWithInteractionSummary(id, response);
        log.info("Contact retrieved successfully with ID: {}", id);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CONTACTS_CACHE)
    public List<ContactResponseDTO> getAllContacts() {
        log.info("Retrieving all contacts");

        List<ContactResponseDTO> contacts = contactRepository.findAll()
                .stream()
                .map(contactMapper::toResponseDTO)
                .collect(Collectors.toList());

        for (ContactResponseDTO contact : contacts) {
            enrichWithInteractionSummary(contact.getId(), contact);
        }

        log.info("Retrieved {} contacts", contacts.size());
        return contacts;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContactResponseDTO> searchContacts(String query, Pageable pageable) {
        log.info("Searching contacts with query: {}", query);

        Query mongoQuery = new Query();
        if (query != null && !query.isBlank()) {
            String regex = "(?i).*" + query.replaceAll("\\s+", ".*") + ".*";
            Criteria criteria = new Criteria().orOperator(
                    Criteria.where("firstName").regex(regex),
                    Criteria.where("lastName").regex(regex),
                    Criteria.where("nickname").regex(regex),
                    Criteria.where("email").regex(regex),
                    Criteria.where("company").regex(regex),
                    Criteria.where("designation").regex(regex),
                    Criteria.where("city").regex(regex),
                    Criteria.where("country").regex(regex),
                    Criteria.where("notes").regex(regex)
            );
            mongoQuery.addCriteria(criteria);
        }

        long total = mongoTemplate.count(mongoQuery, Contact.class);
        mongoQuery.with(pageable);
        List<Contact> contacts = mongoTemplate.find(mongoQuery, Contact.class);

        List<ContactResponseDTO> dtos = contacts.stream()
                .map(contactMapper::toResponseDTO)
                .collect(Collectors.toList());

        log.info("Search returned {} of {} contacts", dtos.size(), total);
        return new org.springframework.data.domain.PageImpl<>(dtos, pageable, total);
    }

    @Override
    @Transactional
    @CacheEvict(value = { CONTACT_CACHE, CONTACTS_CACHE }, key = "#id")
    public void deleteContact(String id) {
        log.info("Deleting contact with ID: {}", id);

        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Contact not found for deletion with ID: {}", id);
                    return new ResourceNotFoundException(ApiConstants.CONTACT_NOT_FOUND + id);
                });

        contactRepository.deleteById(id);
        log.info("Contact deleted successfully with ID: {}", id);

        contactEventProducer.publishContactEvent(ContactEventType.DELETED, contact.getId(), contact);
    }

    @Override
    public List<ContactResponseDTO> findContactsNearby(double latitude, double longitude, double radiusKm) {
        log.info("Finding contacts near lat={}, lng={}, radius={}km", latitude, longitude, radiusKm);

        List<Contact> allContacts = contactRepository.findAll();

        List<ContactResponseDTO> nearby = allContacts.stream()
                .filter(contact -> contact.getLocation() != null)
                .filter(contact -> {
                    double distance = calculateDistance(
                            latitude,
                            longitude,
                            contact.getLocation().getY(),
                            contact.getLocation().getX()
                    );
                    return distance <= radiusKm;
                })
                .map(contactMapper::toResponseDTO)
                .toList();

        log.info("Found {} contacts nearby", nearby.size());
        return nearby;
    }

    private void enrichWithInteractionSummary(String contactId, ContactResponseDTO response) {
        List<ContactInteraction> interactions = interactionRepository.findByContactIdOrderByLastContactedDateDesc(contactId);
        if (!interactions.isEmpty()) {
            response.setLastContactedDate(interactions.get(0).getLastContactedDate());
            response.setInteractionCount((long) interactions.size());
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private void validateUniqueEmail(String email) {
        if (email != null && contactRepository.findByEmail(email).isPresent()) {
            log.warn("Duplicate email found during creation: {}", email);
            throw new DuplicateResourceException(ApiConstants.DUPLICATE_EMAIL);
        }
    }

    private void validateUniqueMobileNumber(String mobileNumber) {
        if (mobileNumber != null && contactRepository.findByMobileNumber(mobileNumber).isPresent()) {
            log.warn("Duplicate mobile number found during creation: {}", mobileNumber);
            throw new DuplicateResourceException(ApiConstants.DUPLICATE_MOBILE);
        }
    }
}