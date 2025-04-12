package src.models;

import src.enums.NotificationStatus;
import jakarta.persistence.*;
import lombok.Data;
import src.enums.Priority;

import java.sql.Timestamp;
import java.util.List;

@Data
@Entity
@Table(name = "notifications")
public class Notifications {
  @Id
  private int notificationId;  // Unique Notification ID

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;  // Associated User

  private List<Device> device;

  private String type;  // Type of Notification (e.g., otp,PROMO)

  private String content;  // Notification message content

  private NotificationStatus status = NotificationStatus.PENDING;  // Status (PENDING, SENT, FAILED)

  private Integer attempts = 0;

  private Priority priority = Priority.MEDIUM;

  private String errorMessage;

  private Long createdAt;

  private Boolean isScheduled;

  private Long scheduledTime;

  public Notifications() {}

  public Notifications(User user, List<Device> device, String type, String content) {
    this.user = user;
    this.device = device;
    this.type = type;
    this.content = content;
  }
}


