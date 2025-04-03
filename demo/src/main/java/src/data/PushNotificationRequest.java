package src.data;

import lombok.Data;

@Data
public class PushNotificationRequest {
  private String email;
  private String content;
  private String type;
}
