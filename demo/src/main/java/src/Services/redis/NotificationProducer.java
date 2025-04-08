package src.Services.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import src.models.Events;
import src.models.Notifications;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationProducer {

  private final StringRedisTemplate redisTemplate;
  private static final String STREAM_NAME = "notification-stream";

  public NotificationProducer(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public void sendNotification(Events event) {
    Map<String, String> message ;
    {
      message =new HashMap<>();
      message.put("email",event.getEmail());
      message.put("content",event.getContent());
      message.put("type",event.getType());
      message.put("priority", String.valueOf(event.getPriority()));
    }

    // Add message to Redis Stream
    redisTemplate.opsForStream().add(STREAM_NAME, message);
    System.out.println("Message pushed into the stream: " + message);
  }
}
