package src.Services.redis;

import io.lettuce.core.StreamMessage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import src.Services.FcmService;
import src.Services.NotificationService;
import src.enums.Priority;
import src.models.Events;
import src.models.Notifications;

import java.time.Duration;
import java.util.*;

@Service
public class NotificationConsumer {

  private final RedisTemplate<String, String> redisTemplate;
  private static final String STREAM_NAME = "notification_stream";
  private final NotificationService notificationService;

  public NotificationConsumer(RedisTemplate<String, String> redisTemplate, NotificationService notificationService) {
    this.redisTemplate = redisTemplate;
    this.notificationService = notificationService;
  }

  @Autowired
  FcmService fcmService;

  @PostConstruct
  public void consumeEvents() {
    new Thread(() -> {
      while (true) {
        try {
          // Using Redis commands directly instead of MapRecord.class as not comapatible with java 8
          List<Map<String, String>> messages = redisTemplate.execute((RedisConnection connection) -> {
            List<Map<String, String>> result = new ArrayList<>();

            // Reading from the stream
            Consumer consumer = Consumer.from("notification_group", "consumer-1");
            StreamReadOptions options = StreamReadOptions.empty().count(10).block(Duration.ofMillis(1000));

            List<MapRecord<String, Object, Object>> streamMessages = redisTemplate.opsForStream()
              .read(consumer, options, StreamOffset.create(STREAM_NAME, ReadOffset.lastConsumed()));

            if (streamMessages != null) {
              for (MapRecord<String, Object, Object> message : streamMessages) {
                Map<Object, Object> messageMap = (Map<Object, Object>) message.getValue();  // Use getValue() instead of getBody()
                Map<String, String> data = Collections.singletonMap("message", messageMap.toString());
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
              processEvent(message);  // Process the event
            }
          }

        } catch (Exception e) {
          System.err.println("Error reading from Redis stream: " + e.getMessage());
          e.printStackTrace();
        }
      }
    }).start();
  }


  private void processEvent(Map<String, String>  message) {
    Notifications notification = mapToNotification(message);
    fcmService.sendNotification(notification);
    System.out.printf("🔥 Consumed Notification: %s → Email: %s → Message: %s%n",
      notification.getType(), notification.getUser().getEmail(), notification.getContent());
  }

  private Notifications mapToNotification(Map<String, String> message) {
    return notificationService.createNotificationForEvents(message.get("email"),
      message.get("type"), message.get("content"),message.get("priority"));
  }

}
