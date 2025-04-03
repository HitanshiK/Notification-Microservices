package src;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
@ComponentScan
public class NotificationMicroserviceApplication  {

  public static void main(String[] args) {
    try{
      SpringApplication.run(NotificationMicroserviceApplication.class, args);
      System.out.println("Application running...");
    }catch (Exception e){
      System.out.println("Appication run failed");
      throw new RuntimeException(e.getCause());
    }
  }
}
