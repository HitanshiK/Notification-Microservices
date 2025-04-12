package src.crons;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import src.models.Notifications;
import src.repos.NotificationRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scheduler {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String STREAM_NAME = "notification_stream";

    @Autowired
    NotificationRepository notificationRepository;

    public Scheduler(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Scheduled(fixedRate = 60000)
    public void processScheduledNotifications() {
        List<Notifications> dueNotifications = notificationRepository.findDueScheduledNotifications(System.currentTimeMillis());

        for (Notifications n : dueNotifications) {
            // Build Redis stream message
            Map<String, String> message = new HashMap<>();
            message.put("id", String.valueOf(n.getNotificationId()));
            message.put("content", n.getContent());
            message.put("type", n.getType());
            message.put("priority", String.valueOf(n.getPriority()));

            redisTemplate.opsForStream().add(STREAM_NAME, message);
            notificationRepository.save(n);

            System.out.println(" Scheduled notification pushed to Redis stream: " + n.getNotificationId());
        }
    }
}


