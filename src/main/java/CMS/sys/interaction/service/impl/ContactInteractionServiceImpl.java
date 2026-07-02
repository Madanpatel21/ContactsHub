package CMS.sys.interaction.service.impl;

import CMS.sys.entity.ContactInteraction;
import CMS.sys.exception.ResourceNotFoundException;
import CMS.sys.interaction.dto.ContactInteractionRequestDTO;
import CMS.sys.interaction.dto.ContactInteractionResponseDTO;
import CMS.sys.interaction.mapper.ContactInteractionMapper;
import CMS.sys.interaction.repository.ContactInteractionRepository;
import CMS.sys.interaction.service.ContactInteractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactInteractionServiceImpl implements ContactInteractionService {

    private final ContactInteractionRepository interactionRepository;
    private final ContactInteractionMapper interactionMapper;

    @Override
    @Transactional
    public ContactInteractionResponseDTO createInteraction(String contactId, ContactInteractionRequestDTO requestDTO) {
        log.info("Creating interaction for contactId: {}, type: {}", contactId, requestDTO.getType());

        ContactInteraction interaction = interactionMapper.toEntity(requestDTO);
        interaction.setContactId(contactId);
        if (interaction.getAttachments() == null) {
            interaction.setAttachments(new java.util.ArrayList<>());
        }

        ContactInteraction saved = interactionRepository.save(interaction);
        log.info("Interaction created successfully with ID: {}", saved.getId());
        return interactionMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public ContactInteractionResponseDTO updateInteraction(String interactionId, ContactInteractionRequestDTO requestDTO) {
        log.info("Updating interaction with ID: {}", interactionId);

        ContactInteraction existing = interactionRepository.findById(interactionId)
                .orElseThrow(() -> {
                    log.warn("Interaction not found with ID: {}", interactionId);
                    return new ResourceNotFoundException("Interaction not found with id: " + interactionId);
                });

        interactionMapper.updateEntityFromDTO(requestDTO, existing);
        ContactInteraction updated = interactionRepository.save(existing);

        log.info("Interaction updated successfully with ID: {}", updated.getId());
        return interactionMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional
    public void deleteInteraction(String interactionId) {
        log.info("Deleting interaction with ID: {}", interactionId);

        if (!interactionRepository.existsById(interactionId)) {
            log.warn("Interaction not found for deletion with ID: {}", interactionId);
            throw new ResourceNotFoundException("Interaction not found with id: " + interactionId);
        }

        interactionRepository.deleteById(interactionId);
        log.info("Interaction deleted successfully with ID: {}", interactionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactInteractionResponseDTO> getInteractionsByContactId(String contactId) {
        log.info("Retrieving interactions for contactId: {}", contactId);

        List<ContactInteractionResponseDTO> interactions = interactionRepository
                .findByContactIdOrderByLastContactedDateDesc(contactId)
                .stream()
                .map(interactionMapper::toResponseDTO)
                .collect(Collectors.toList());

        log.info("Retrieved {} interactions for contactId: {}", interactions.size(), contactId);
        return interactions;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactInteractionResponseDTO> getInteractionsByContactIdAndType(String contactId, ContactInteraction.InteractionType type) {
        log.info("Retrieving {} interactions for contactId: {}", type, contactId);

        List<ContactInteractionResponseDTO> interactions = interactionRepository
                .findByContactIdAndTypeOrderByLastContactedDateDesc(contactId, type)
                .stream()
                .map(interactionMapper::toResponseDTO)
                .collect(Collectors.toList());

        log.info("Retrieved {} {} interactions for contactId: {}", interactions.size(), type, contactId);
        return interactions;
    }

    @Override
    @Transactional(readOnly = true)
    public ContactInteractionResponseDTO getInteractionById(String interactionId) {
        log.info("Retrieving interaction with ID: {}", interactionId);

        ContactInteraction interaction = interactionRepository.findById(interactionId)
                .orElseThrow(() -> {
                    log.warn("Interaction not found with ID: {}", interactionId);
                    return new ResourceNotFoundException("Interaction not found with id: " + interactionId);
                });

        return interactionMapper.toResponseDTO(interaction);
    }
}