package src.data;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class PushNotificationRequest {
  private String email;
  private String content;
  private String type;
  private Long scheduledTime; //only if it is a Scheduled notiofication
}
