package src.Services;

import com.google.firebase.messaging.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import src.enums.NotificationStatus;
import src.models.Notifications;
import src.models.User;
import src.repos.DeviceRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FcmService {

    @Autowired
    DeviceServices deviceService;
    @Autowired
    NotificationService notificationService;
    @Autowired
    DeviceRepository deviceRepository;

    public void sendNotification(Notifications notification) {

        User user = notification.getUser();

        String title = notificationService.getTitleByNotificationType(notification.getType());

        if(user!=null) {
            List<String> tokens =deviceService.fetchFcmTokens(user.getId());

            if (!tokens.isEmpty()) {
              List<Message> messages = tokens.stream()
                .map(token -> Message.builder()
                  .setToken(token)
                  .setNotification(Notification.builder()
                    .setTitle(title)
                    .setBody(notification.getContent())
                    .build())
                  .build())
                .collect(Collectors.toList());

                try {
                    BatchResponse response = FirebaseMessaging.getInstance().sendEach(messages);
                    System.out.println(" Notifications sent: " + response.getSuccessCount());

                    if(response.getSuccessCount()>0){
                        notification.setStatus(NotificationStatus.SENT);
                        notificationService.update(notification);
                    }else{
                      if(notification.getAttempts()==3){
                        notification.setStatus(NotificationStatus.FAILED);
                        notification.setErrorMessage(response.getResponses().get(0).getException().getMessage());
                        notificationService.update(notification);
                      }else{
                        //retry Mechanism


                      }
                    }
                    List<SendResponse>responses = response.getResponses();
                    List<Token> failedTokens = new ArrayList<>();

                    for(int i=0 ; i<responses.size();i++){
                        SendResponse res = responses.get(i);
                        if(!res.isSuccessful()){
                            Token token = new Token();
                            token.setFcmToken(tokens.get(i));
                            token.setException(res.getException());
                            failedTokens.add(token);
                        }
                    }
                    handleInvalidFcmTokens(failedTokens);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw  new RuntimeException( " Failed to send notifications.");
                }
            } else {
                throw new RuntimeException("no Registered device for this user");
            }
        }else{
            throw  new RuntimeException("user not found");
        }
    }

    void handleInvalidFcmTokens(List<Token> tokens){
        try{
            List<String> invalidTokens = new ArrayList<>();

            for (Token token: tokens) {
                if (token.getException() != null) {
                    FirebaseMessagingException fcmException = token.getException();
                    if (fcmException.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT ||
                            fcmException.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                        invalidTokens.add(token.getFcmToken()); // Collect invalid tokens
                    }
                }
            }

            if (!invalidTokens.isEmpty()) {
              deviceService.deleteByFcmToken(invalidTokens);
                System.out.println("Removed invalid FCM tokens: " + invalidTokens.size());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getCause());
        }
    }

    @Data
    private class Token{
        private String fcmToken;
        private FirebaseMessagingException exception;
    }
}

