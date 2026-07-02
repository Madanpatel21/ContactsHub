package CMS.sys.kafka.config;

/**
 * Centralized Kafka topic names for the Contact Management System.
 * Keeping topic names in one place ensures consistency across producers and consumers.
 *
 * @author Contact Management System
 * @version 1.0
 */
public final class KafkaTopics {

    private KafkaTopics() {
    }

    public static final String CONTACT_EVENTS = "contact-events";
}