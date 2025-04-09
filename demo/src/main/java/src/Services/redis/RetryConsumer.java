package src.Services.redis;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.RedisTemplate;
import src.Services.FcmService;
import src.Services.NotificationService;
import src.enums.NotificationStatus;
import src.enums.Priority;
import src.models.Notifications;
import src.repos.NotificationRepository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RetryConsumer {

  private final RedisTemplate<String, String> redisTemplate;
  private static final String STREAM_NAME = "notification_retry_stream";

  public RetryConsumer(RedisTemplate<String, String> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  @Autowired
  NotificationRepository notificationRepository;
  @Autowired
  NotificationService notificationService;
  @Autowired
  FcmService fcmService;

  @PostConstruct
  public void consumeEvents() {
    new Thread(() -> {
      while (true) {
        try {
          List<Map<String, String>> messages = redisTemplate.execute((RedisConnection connection) -> {
            List<Map<String, String>> result = new ArrayList<>();

            // Reading from the stream
            StreamReadOptions options = StreamReadOptions.empty().count(10).block(Duration.ofMillis(1000));

            List<ObjectRecord<String, String>> streamMessages = redisTemplate.opsForStream()
              .read(String.class, options, StreamOffset.create(STREAM_NAME, ReadOffset.lastConsumed()));

            if (streamMessages != null) {
              for (ObjectRecord<String, String> message : streamMessages) {
                String value = message.getValue();  // Use getValue() instead of getBody()
                Map<String, String> data = Collections.singletonMap("message", value);
                result.add(data);

                // Acknowledge message
                redisTemplate.opsForStream().acknowledge(STREAM_NAME, "notification_group", message.getId());
              }
            }
            return result;
          });

          if (messages != null && !messages.isEmpty()) {             // Sort based on priority

            messages.sort((m1, m2) -> {
              Priority p1 = Priority.valueOf(m1.getOrDefault("priority", "MEDIUM").toUpperCase());
              Priority p2 = Priority.valueOf(m2.getOrDefault("priority", "MEDIUM").toUpperCase());
              return p2.ordinal() - p1.ordinal(); // Higher priority first
            });


            for (Map<String, String> message : messages) {
              System.out.println("Received message: " + message);
                retryNotification(message);
            }
          }

        } catch (Exception e) {
          System.err.println("Error reading from retry Redis stream: " + e.getMessage());
          e.printStackTrace();
        }
      }
    }).start();
  }

  public void retryNotification(Map<String,String> message){
    long id = Long.parseLong(message.get("id"));
    Notifications notification = notificationRepository.findNotificationById(id);
    if(notification.getAttempts()==3){
      System.out.println("Notification failed");
      notification.setStatus(NotificationStatus.FAILED);
      notificationService.update(notification);
    }else{
      int attempts =notification.getAttempts();

      /*** Adding Backoff delay ***/
      long delay = (long) Math.pow(2, attempts) * 1000;
      try {
        System.out.printf("Backing off for %dms before retrying ID %s (retry #%d)%n", delay, notification.getNotificationId(), attempts);
        Thread.sleep(delay);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      if(notification.getStatus()!=NotificationStatus.SENT){
        fcmService.sendNotification(notification);
      }

      if(notification.getStatus()!=NotificationStatus.SENT){
        notification.setAttempts(attempts+1);
        notificationService.update(notification);
      }

      System.out.println("Retry notification sent to fcm");

    }
  }
}
