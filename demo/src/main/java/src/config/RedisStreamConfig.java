package src.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Collections;
import java.util.Map;

@Configuration
public class RedisStreamConfig {

  private static final String STREAM_NAME = "notification_stream";
  private static final String CONSUMER_GROUP = "notification_group";

  @Bean
  public ApplicationRunner streamInitializer(StringRedisTemplate redisTemplate) {
    return args -> {
      System.out.println(" Redis stream setup starting...");

      //  Create stream if it doesn't exist
      Boolean exists = redisTemplate.hasKey(STREAM_NAME);
      if (exists == null || !exists) {
        System.out.println(" Creating Redis stream: " + STREAM_NAME);
        Map<String, String> message = Collections.singletonMap("init", "stream");
        redisTemplate.opsForStream().add(STREAM_NAME, message);
      }

      //  Create consumer group if it doesn't exist
      try {
        redisTemplate.opsForStream()
          .createGroup(STREAM_NAME, ReadOffset.latest(), CONSUMER_GROUP);
        System.out.println(" Consumer group '" + CONSUMER_GROUP + "' created.");
      } catch (Exception e) {
        if (e.getMessage() != null && e.getMessage().contains("BUSYGROUP")) {
          System.out.println(" Consumer group '" + CONSUMER_GROUP + "' already exists.");
        } else {
          System.err.println(" Failed to create consumer group: " + e.getMessage());
        }
      }
    };
  }
}
