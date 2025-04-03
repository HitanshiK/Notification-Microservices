package src.repos;

import src.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
  // Additional query methods can be defined here

  @Query(value = "SELECT * FROM user WHERE id = :id", nativeQuery = true)
  User findUserById(@Param("id") Long id);

  @Query(value = " SELECT * FROM user WHERE emial =:email", nativeQuery = true )
  User findByEmail(@Param("email") String email);

}
