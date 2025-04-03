package src.Services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import src.Services.redis.NotificationProducer;
import src.models.Events;
import src.models.Notifications;

@Service
public class EventTriggerService {

  private final NotificationProducer producer;
  private final NotificationService notificationService;

  public EventTriggerService(NotificationProducer producer, NotificationService notificationService) {
    this.producer = producer;
    this.notificationService = notificationService;
  }
  // some generic event triggers

  public void  triggerUserRegistered(String email) {
    Events notification = notificationService.createEvents(email,
      "Welcome! Your account has been successfully created.",
      "USER_REGISTERED");
    producer.sendNotification(notification);
  }

  public void triggerPaymentSuccess(String email, double amount) {
   Events notification =notificationService.createEvents(email,
     "Your payment of ₹" + amount + " has been processed successfully.",
     "PAYMENT_SUCCESS");
    producer.sendNotification(notification);
  }

  public void triggerServerError(String errorMessage) {
    Events notification = notificationService.createEvents(
      "admin@company.com",
      "Critical error: " + errorMessage,
      "SERVER_ERROR"
    );
    producer.sendNotification(notification);
  }

  //more different events can be added here
}
