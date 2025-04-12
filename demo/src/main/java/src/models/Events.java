package src.models;

import lombok.Data;
import src.enums.Priority;

@Data
public class Events {

  private String email;

  private String content;

  private String type;

  private Priority priority;

  private Long scheduledTime;

}
