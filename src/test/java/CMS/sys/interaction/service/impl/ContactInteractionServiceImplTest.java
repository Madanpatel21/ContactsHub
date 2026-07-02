package CMS.sys.interaction.service.impl;

import CMS.sys.entity.ContactInteraction;
import CMS.sys.exception.ResourceNotFoundException;
import CMS.sys.interaction.dto.ContactInteractionRequestDTO;
import CMS.sys.interaction.dto.ContactInteractionResponseDTO;
import CMS.sys.interaction.mapper.ContactInteractionMapper;
import CMS.sys.interaction.repository.ContactInteractionRepository;
import CMS.sys.interaction.service.ContactInteractionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactInteractionServiceImplTest {

    @Mock
    private ContactInteractionRepository interactionRepository;

    @Mock
    private ContactInteractionMapper interactionMapper;

    @InjectMocks
    private ContactInteractionServiceImpl interactionService;

    private ContactInteractionRequestDTO requestDTO;
    private ContactInteraction interaction;
    private ContactInteractionResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = ContactInteractionRequestDTO.builder()
                .type(ContactInteraction.InteractionType.MEETING)
                .subject("Quarterly review")
                .notes("Discussed Q1 goals")
                .outcome("Agreed to follow up")
                .lastContactedDate(java.time.LocalDateTime.now())
                .build();

        interaction = ContactInteraction.builder()
                .id("interaction-1")
                .contactId("contact-1")
                .type(ContactInteraction.InteractionType.MEETING)
                .subject("Quarterly review")
                .notes("Discussed Q1 goals")
                .outcome("Agreed to follow up")
                .lastContactedDate(java.time.LocalDateTime.now())
                .build();

        responseDTO = ContactInteractionResponseDTO.builder()
                .id("interaction-1")
                .contactId("contact-1")
                .type(ContactInteraction.InteractionType.MEETING)
                .subject("Quarterly review")
                .notes("Discussed Q1 goals")
                .outcome("Agreed to follow up")
                .lastContactedDate(java.time.LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should create interaction successfully")
    void createInteraction_ShouldCreateInteraction_WhenValidData() {
        when(interactionMapper.toEntity(any(ContactInteractionRequestDTO.class))).thenReturn(interaction);
        when(interactionRepository.save(any(ContactInteraction.class))).thenReturn(interaction);
        when(interactionMapper.toResponseDTO(any(ContactInteraction.class))).thenReturn(responseDTO);

        CMS.sys.interaction.dto.ContactInteractionResponseDTO result = interactionService.createInteraction("contact-1", requestDTO);

        assertNotNull(result);
        assertEquals("interaction-1", result.getId());
        verify(interactionRepository, times(1)).save(any(ContactInteraction.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existing interaction")
    void updateInteraction_ShouldThrowException_WhenInteractionNotFound() {
        when(interactionRepository.findById("non-existing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> interactionService.updateInteraction("non-existing", requestDTO));
    }

    @Test
    @DisplayName("Should update interaction successfully when interaction exists")
    void updateInteraction_ShouldUpdateInteraction_WhenInteractionExists() {
        when(interactionRepository.findById("interaction-1")).thenReturn(Optional.of(interaction));
        when(interactionRepository.save(any(ContactInteraction.class))).thenReturn(interaction);
        when(interactionMapper.toResponseDTO(any(ContactInteraction.class))).thenReturn(responseDTO);

        var result = interactionService.updateInteraction("interaction-1", requestDTO);

        assertNotNull(result);
        verify(interactionMapper, times(1)).updateEntityFromDTO(eq(requestDTO), eq(interaction));
        verify(interactionRepository, times(1)).save(interaction);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existing interaction")
    void deleteInteraction_ShouldThrowException_WhenInteractionNotFound() {
        when(interactionRepository.existsById("non-existing")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> interactionService.deleteInteraction("non-existing"));
    }

    @Test
    @DisplayName("Should delete interaction successfully when interaction exists")
    void deleteInteraction_ShouldDeleteInteraction_WhenInteractionExists() {
        when(interactionRepository.existsById("interaction-1")).thenReturn(true);
        doNothing().when(interactionRepository).deleteById("interaction-1");

        interactionService.deleteInteraction("interaction-1");

        verify(interactionRepository, times(1)).deleteById("interaction-1");
    }

    @Test
    @DisplayName("Should retrieve interactions by contact ID")
    void getInteractionsByContactId_ShouldReturnInteractions_WhenContactHasInteractions() {
        when(interactionRepository.findByContactIdOrderByLastContactedDateDesc("contact-1"))
                .thenReturn(List.of(interaction));
        when(interactionMapper.toResponseDTO(any(ContactInteraction.class))).thenReturn(responseDTO);

        List<CMS.sys.interaction.dto.ContactInteractionResponseDTO> result = interactionService.getInteractionsByContactId("contact-1");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(interactionRepository, times(1)).findByContactIdOrderByLastContactedDateDesc("contact-1");
    }

    @Test
    @DisplayName("Should return empty list when contact has no interactions")
    void getInteractionsByContactId_ShouldReturnEmptyList_WhenNoInteractions() {
        when(interactionRepository.findByContactIdOrderByLastContactedDateDesc("contact-1"))
                .thenReturn(Collections.emptyList());

        List<CMS.sys.interaction.dto.ContactInteractionResponseDTO> result = interactionService.getInteractionsByContactId("contact-1");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should retrieve interactions by contact ID and type")
    void getInteractionsByContactIdAndType_ShouldReturnFilteredInteractions() {
        when(interactionRepository.findByContactIdAndTypeOrderByLastContactedDateDesc("contact-1", ContactInteraction.InteractionType.MEETING))
                .thenReturn(List.of(interaction));
        when(interactionMapper.toResponseDTO(any(ContactInteraction.class))).thenReturn(responseDTO);

        List<CMS.sys.interaction.dto.ContactInteractionResponseDTO> result = interactionService.getInteractionsByContactIdAndType("contact-1", ContactInteraction.InteractionType.MEETING);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(interactionRepository, times(1)).findByContactIdAndTypeOrderByLastContactedDateDesc("contact-1", ContactInteraction.InteractionType.MEETING);
    }

    @Test
    @DisplayName("Should retrieve interaction by ID")
    void getInteractionById_ShouldReturnInteraction_WhenInteractionExists() {
        when(interactionRepository.findById("interaction-1")).thenReturn(Optional.of(interaction));
        when(interactionMapper.toResponseDTO(any(ContactInteraction.class))).thenReturn(responseDTO);

        CMS.sys.interaction.dto.ContactInteractionResponseDTO result = interactionService.getInteractionById("interaction-1");

        assertNotNull(result);
        assertEquals("interaction-1", result.getId());
        verify(interactionRepository, times(1)).findById("interaction-1");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when interaction not found by ID")
    void getInteractionById_ShouldThrowException_WhenInteractionNotFound() {
        when(interactionRepository.findById("non-existing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> interactionService.getInteractionById("non-existing"));
    }
}