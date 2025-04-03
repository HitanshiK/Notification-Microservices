package src.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "devices")
public class Device {
  @Id
  private int id;  // Unique Device ID

  @ManyToOne
  @JoinColumn(name = "userId")
  private User user;

  @Column( unique = true)
  private String fcmToken;  // FCM Token for Push Notifications

  private Long createdAt  ;
  private Long lastUsedAt;  // Last time device was active

  public Device(){

  }

  public Device(User user, String fcmToken) {
    this.user = user;
    this.fcmToken = fcmToken;
  }

}

