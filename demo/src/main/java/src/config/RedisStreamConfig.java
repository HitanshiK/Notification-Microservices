package src.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Collections;
import java.util.Map;

@Configuration
public class RedisStreamConfig {

  private static final String STREAM_NAME = "notification-stream";

  @Bean
  public ApplicationRunner streamInitializer(StringRedisTemplate redisTemplate) {
    return args -> {
      // Check if stream already exists
      Boolean exists = redisTemplate.hasKey(STREAM_NAME);
      System.out.println("Redis stream running .....");

      if (exists == null || !exists) {
        System.out.println("Creating Redis Stream: " + STREAM_NAME);
        Map<String, String> message = Collections.singletonMap("init", "stream");
        redisTemplate.opsForStream().add(STREAM_NAME, message);
      }
    };
  }
}

