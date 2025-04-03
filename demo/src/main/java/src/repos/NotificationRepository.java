package src.repos;

import src.models.Notifications;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends CrudRepository<Notifications, Long> {
  // Additional query methods can be defined here
}
