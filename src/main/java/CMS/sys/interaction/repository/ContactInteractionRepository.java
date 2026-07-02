package CMS.sys.interaction.repository;

import CMS.sys.entity.ContactInteraction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactInteractionRepository extends MongoRepository<ContactInteraction, String> {

    List<ContactInteraction> findByContactIdOrderByLastContactedDateDesc(String contactId);

    List<ContactInteraction> findByContactIdAndTypeOrderByLastContactedDateDesc(String contactId, ContactInteraction.InteractionType type);
}