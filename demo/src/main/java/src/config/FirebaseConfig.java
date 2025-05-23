package src.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

  @PostConstruct
  public void init() {
    try {
      InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-service-account.json");

      FirebaseOptions options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .build();

      if (FirebaseApp.getApps().isEmpty()) {
        FirebaseApp.initializeApp(options);
      }

      System.out.println("Firebase Initialized Successfully!");

    } catch (Exception e) {
      System.err.println(" Firebase initialization failed: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
