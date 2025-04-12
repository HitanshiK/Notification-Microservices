package src.Services;

import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import src.Services.redis.NotificationProducer;
import src.data.PushNotificationRequest;
import src.enums.Priority;
import src.models.Events;
import src.models.Notifications;
import org.springframework.beans.factory.annotation.Autowired;
import src.repos.NotificationRepository;

@Service
public class NotificationService {

  @Autowired
  NotificationRepository notificationRepository;
  @Autowired
  @Lazy
  UserServices userServices;
  @Autowired
  DeviceServices deviceServices;
  @Autowired
  NotificationProducer producer;

  public void save(Notifications notification) {
    notificationRepository.save(notification);
  }

  public void deleteById(Notifications notifications) {
    if (notificationRepository.existsById((long) notifications.getNotificationId())) {
      notificationRepository.deleteById((long) notifications.getNotificationId());
    }
  }

  public ResponseEntity<Events> createNotificationEvents(PushNotificationRequest request) {
    try {
      if (userServices.validateUser(request.getEmail())) {
        Events event = new Events();
        event.setContent(request.getContent());
        event.setType(request.getType());
        event.setEmail(request.getEmail());
        if(request.getScheduledTime()!=null){
             // Scheduled notifications
          createScheduledNotifiications(event);
        }
        producer.sendNotification(event);
        return ResponseEntity.ok(event);
      } else {
        throw new RuntimeException("User not found");
      }
    } catch (RuntimeException e) {
      throw new RuntimeException(e.getCause());
    }
  }

  public Notifications createNotificationForEvents(String email, String content, String type,String priority) {
    try {
      if (userServices.validateUser(email)) {

        Notifications notifications = new Notifications();
        notifications.setUser(userServices.findUser(email));
        notifications.setContent(content);
        notifications.setDevice(deviceServices.findAll(notifications.getUser().getId()));
        notifications.setType(type);
        notifications.setPriority(Priority.valueOf(priority));
        save(notifications);
        return notifications;
      } else {
        throw new RuntimeException("User not found");
      }
    } catch (RuntimeException e) {
      throw new RuntimeException(e.getCause());
    }
  }

  public Events createEvents(String email, String content, String type) {
    try {
      Priority priority = determinePriorityFromEventType(type);

      Events events = new Events();
      events.setContent(content);
      events.setContent(type);
      events.setEmail(email);
      events.setPriority(priority);

      return events;
    } catch (Exception e) {
      throw new RuntimeException(e.getLocalizedMessage());
    }
  }

  public String getTitleByNotificationType(String notificationType) {
    return switch (notificationType) {
      case "MESSAGE" -> "New Message Received!";
      case "ORDER_CONFIRMED" -> "Your Order is Confirmed!";
      case "PAYMENT_SUCCESS" -> "Payment Successful ";
      case "ALERT" -> "Important Alert!";
      default -> "New Notification"; // Default title if type is unknown
    };
  }

  public ResponseEntity<Notifications> update(Notifications notifications) {
    if (notificationRepository.existsById((long) notifications.getNotificationId())) {
      notificationRepository.save(notifications);
      return ResponseEntity.ok(notifications);
    } else {
      throw new RuntimeException("Notification not found");
    }
  }

  public Priority determinePriorityFromEventType(String eventType) {
    return switch (eventType.toUpperCase()) {
      case "TRANSACTION_ALERT", "PASSWORD_CHANGED" -> Priority.HIGH;
      case "REMINDER", "SUMMARY" -> Priority.MEDIUM;
      case "PROMOTION", "FEEDBACK" -> Priority.LOW;
      default -> Priority.MEDIUM;
    };
  }

  public Notifications createScheduledNotifiications(Events events){
    try {
      if (userServices.validateUser(events.getEmail())) {

        Notifications notifications = new Notifications();
        notifications.setUser(userServices.findUser(events.getEmail()));
        notifications.setContent(events.getContent());
        notifications.setDevice(deviceServices.findAll(notifications.getUser().getId()));
        notifications.setType(events.getType());
        notifications.setPriority(events.getPriority());
        notifications.setIsScheduled(true);
        notifications.setScheduledTime(events.getScheduledTime());
        save(notifications);
        return notifications;
      } else {
        throw new RuntimeException("User not found");
      }
    } catch (RuntimeException e) {
      throw new RuntimeException(e.getCause());
    }
  }
}
