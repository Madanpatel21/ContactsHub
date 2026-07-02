package CMS.sys.servicenow;

import CMS.sys.event.ContactEventType;
import CMS.sys.kafka.ContactEventProducer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
public class ServiceNowContactSyncService {

    private final ServiceNowClient serviceNowClient;
    private final ContactEventProducer contactEventProducer;

    public ServiceNowContactSyncService(ServiceNowClient serviceNowClient, ContactEventProducer contactEventProducer) {
        this.serviceNowClient = serviceNowClient;
        this.contactEventProducer = contactEventProducer;
    }

    public void handleContactEvent(String payload) {
        Map<String, Object> serviceNowPayload = new HashMap<>();
        serviceNowPayload.put("u_sys_id", extractField(payload, "contactId"));
        serviceNowPayload.put("u_first_name", extractField(payload, "firstName"));
        serviceNowPayload.put("u_last_name", extractField(payload, "lastName"));
        serviceNowPayload.put("u_email", extractField(payload, "email"));
        serviceNowPayload.put("u_mobile_number", extractField(payload, "mobileNumber"));
        serviceNowPayload.put("u_company", extractField(payload, "company"));
        serviceNowPayload.put("u_designation", extractField(payload, "designation"));

        String eventType = extractField(payload, "eventType");
        if (ContactEventType.CREATED.name().equalsIgnoreCase(eventType)) {
            serviceNowClient.createContactRecord(serviceNowPayload).subscribe();
        } else if (ContactEventType.UPDATED.name().equalsIgnoreCase(eventType)) {
            String sysId = extractField(payload, "contactId");
            serviceNowClient.updateContactRecord(sysId, serviceNowPayload).subscribe();
        }
    }

    private String extractField(String json, String field) {
        String marker = "\"" + field + "\"";
        int index = json.indexOf(marker);
        if (index == -1) {
            return null;
        }
        int colonIndex = json.indexOf(":", index + marker.length());
        if (colonIndex == -1) {
            return null;
        }
        int start = colonIndex + 1;
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
            start++;
        }
        if (start >= json.length()) {
            return null;
        }
        char quote = json.charAt(start);
        if (quote == '\"') {
            int end = json.indexOf('"', start + 1);
            if (end == -1) {
                return null;
            }
            return json.substring(start + 1, end);
        }
        int end = json.indexOf(',', start);
        if (end == -1) {
            end = json.indexOf('}', start);
        }
        if (end == -1) {
            return json.substring(start).trim();
        }
        return json.substring(start, end).trim();
    }
}
