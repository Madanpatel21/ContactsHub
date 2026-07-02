package CMS.sys.kafka;

import CMS.sys.event.ContactEvent;
import CMS.sys.kafka.config.KafkaTopics;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

/**
 * Kafka consumer for processing contact lifecycle events.
 * Demonstrates async event handling for future integrations like
 * notifications, search indexing, audit logging, or external sync.
 *
 * @author Contact Management System
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContactEventConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaTopics.CONTACT_EVENTS, groupId = "contact-manager-group")
    public void consumeContactEvent(String payload,
                                    @Header(KafkaHeaders.RECEIVED_KEY) String key,
                                    @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                    @Header(KafkaHeaders.OFFSET) long offset) {
        try {
            ContactEvent event = objectMapper.readValue(payload, ContactEvent.class);
            log.info("Consumed Kafka event: type={}, contactId={}, key={}, partition={}, offset={}",
                    event.getEventType(), event.getContactId(), key, partition, offset);

            switch (event.getEventType()) {
                case "CREATED" -> handleContactCreated(event);
                case "UPDATED" -> handleContactUpdated(event);
                case "DELETED" -> handleContactDeleted(event);
                default -> log.warn("Unhandled contact event type: {}", event.getEventType());
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize contact event payload: {}", payload, e);
        }
    }

    private void handleContactCreated(ContactEvent event) {
        log.info("Async processing: contact created - ID: {}", event.getContactId());
        // Future: Send welcome email, sync to external systems, update search index
    }

    private void handleContactUpdated(ContactEvent event) {
        log.info("Async processing: contact updated - ID: {}", event.getContactId());
        // Future: Invalidate related caches, notify dependent services
    }

    private void handleContactDeleted(ContactEvent event) {
        log.info("Async processing: contact deleted - ID: {}", event.getContactId());
        // Future: Clean up related resources, audit log, notify user
    }
}