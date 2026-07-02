package CMS.sys.repository;

import CMS.sys.entity.Contact;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends MongoRepository<Contact, String> {

    Optional<Contact> findByEmail(String email);

    Optional<Contact> findByMobileNumber(String mobileNumber);

    boolean existsByEmailAndIdNot(String email, String id);

    boolean existsByMobileNumberAndIdNot(String mobileNumber, String id);

    List<Contact> findByLocationNear(Point point, Distance distance);
}