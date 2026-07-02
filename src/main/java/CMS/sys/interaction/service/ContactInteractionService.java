package CMS.sys.interaction.service;

import CMS.sys.entity.ContactInteraction;
import CMS.sys.interaction.dto.ContactInteractionRequestDTO;
import CMS.sys.interaction.dto.ContactInteractionResponseDTO;

import java.util.List;

public interface ContactInteractionService {

    ContactInteractionResponseDTO createInteraction(String contactId, ContactInteractionRequestDTO requestDTO);

    ContactInteractionResponseDTO updateInteraction(String interactionId, ContactInteractionRequestDTO requestDTO);

    void deleteInteraction(String interactionId);

    List<ContactInteractionResponseDTO> getInteractionsByContactId(String contactId);

    List<ContactInteractionResponseDTO> getInteractionsByContactIdAndType(String contactId, ContactInteraction.InteractionType type);

    ContactInteractionResponseDTO getInteractionById(String interactionId);
}