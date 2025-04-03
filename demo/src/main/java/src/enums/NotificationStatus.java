package src.enums;

import io.ebean.annotation.DbEnumValue;
import lombok.AllArgsConstructor;


public enum NotificationStatus {

  SENT("SENT"),
  PENDING("PENDING"),
  FAILED("FAILED");

  private final String value;

    NotificationStatus(String value) {
      this.value = value;
    }

    @DbEnumValue
  public String getValue() {
    return value;
  }
}
