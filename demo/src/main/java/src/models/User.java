package src.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Data;


@Data
@Entity
@Table(name = "users")
public class User  {

  @Id
  private int id;  // Unique user ID

  private String name;

  @Column(unique = true)
  private String email;

  @Column(unique = true)
  private String phone;

  private Long createdAt = System.currentTimeMillis() ;

  public User() {}

  public User(String name, String email, String phone) {
    this.name = name;

    this.email = email;
    this.phone = phone;
  }
}
