package CMS.sys.event;

import CMS.sys.entity.Contact;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Kafka event model for contact lifecycle events.
 * Carries contact state changes for async downstream processing.
 *
 * @author Contact Management System
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactEvent {

    private String eventType;
    private String contactId;
    private Contact contact;
}