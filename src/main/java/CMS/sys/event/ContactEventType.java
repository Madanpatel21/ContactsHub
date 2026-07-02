package CMS.sys.event;

/**
 * Enumeration of contact lifecycle event types.
 * Used to classify events published to Kafka.
 *
 * @author Contact Management System
 * @version 1.0
 */
public enum ContactEventType {
    CREATED,
    UPDATED,
    DELETED
}