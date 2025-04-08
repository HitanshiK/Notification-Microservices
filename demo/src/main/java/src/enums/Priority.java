package src.enums;

import io.ebean.annotation.DbEnumValue;

public enum Priority {

 HIGH("HIGH"),
 MEDIUM("MEDIUM"),
 LOW("LOW");

  private final String value;

  Priority(String value) {
    this.value = value;
  }

  @DbEnumValue
  public String getValue() {
    return value;
  }
}
