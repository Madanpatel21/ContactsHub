package CMS.sys.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.geo.Point;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "contact_interactions")
public class ContactInteraction {

    @Id
    private String id;

    @Indexed
    private String contactId;

    private InteractionType type;

    private String subject;
    private String notes;
    private String outcome;

    private java.util.List<String> attachments;

    @Indexed
    private LocalDateTime lastContactedDate;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum InteractionType {
        CALL,
        MEETING,
        EMAIL,
        CHAT,
        FOLLOW_UP
    }
}