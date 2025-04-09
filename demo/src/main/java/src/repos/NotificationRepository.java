package src.repos;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import src.models.Notifications;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import src.models.User;

@Repository
public interface NotificationRepository extends CrudRepository<Notifications, Long> {
  // Additional query methods can be defined here
  @Query(value = "SELECT * FROM notifications WHERE id = :id", nativeQuery = true)
  Notifications findNotificationById(@Param("id") Long id);
}
