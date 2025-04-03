package src.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import src.Services.NotificationService;
import src.data.PushNotificationRequest;
import src.models.Events;
import src.models.Notifications;

@RestController
@RequestMapping("/apis/notifications")
public class NotificationController {

  @Autowired
  NotificationService notificationService;

  @PostMapping("/push")
  public ResponseEntity<Events> push(@RequestBody PushNotificationRequest requests) {
    return notificationService.createNotificationEvents(requests);
  }

}
