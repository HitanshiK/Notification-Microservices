package src.repos;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import src.models.Device;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import src.models.User;

import java.util.List;

@Repository
public interface DeviceRepository extends CrudRepository<Device, Long> {
  // Additional query methods can be defined here

  @Query(value = "SELECT * FROM device WHERE userId = :user_id", nativeQuery = true)
  List<Device> find(@Param("user_id") int userId);
}
