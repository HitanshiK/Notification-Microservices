package src.Services.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.stereotype.Service;
import src.models.Notifications;

import java.util.HashMap;
import java.util.Map;

@Service
public class RetryProducer {

  private final RedisTemplate<String, String> redisTemplate;
  private static final String RETRY_STREAM = "retry_notification_stream";

  public RetryProducer(RedisTemplate<String, String> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public void sendToRetryStream(Notifications notification) {
    Map<String, String> message = new HashMap<>();
    message.put("id", String.valueOf(notification.getNotificationId()));
    message.put("content", notification.getContent());
    message.put("type", notification.getType());
    message.put("priority", notification.getPriority().toString());

    // Add message to retry stream
    RecordId recordId = redisTemplate.opsForStream().add(RETRY_STREAM, message);
    System.out.println("Retry message pushed into stream with ID: " + recordId.getValue());
  }
}

