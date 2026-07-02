package CMS.sys.kafka;

import CMS.sys.event.ContactEvent;
import CMS.sys.event.ContactEventType;
import CMS.sys.kafka.config.KafkaTopics;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Publishes contact lifecycle events to Kafka topics.
 * Decouples event production from business logic using Spring's KafkaTemplate.
 *
 * @author Contact Management System
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContactEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishContactEvent(ContactEventType eventType, String contactId, Object payload) {
        try {
            ContactEvent event = new ContactEvent(eventType.name(), contactId, null);
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(KafkaTopics.CONTACT_EVENTS, contactId, json);
            log.info("Published event to topic '{}' for contact ID: {}, type: {}", KafkaTopics.CONTACT_EVENTS, contactId, eventType);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize Kafka event for contact ID: {}", contactId, e);
        }
    }
}