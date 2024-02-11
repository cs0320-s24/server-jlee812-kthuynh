package edu.brown.cs.student.main.server;

import java.util.HashMap;
import java.util.Map;

public class HandlerErrorBuilder {
  private final String errorType;
  private final String errorMessage;
  private final Map<String, String> details;

  public HandlerErrorBuilder(String errorType, String errorMessage, Map<String, String> details) {
    this.errorType = errorType;
    this.errorMessage = errorMessage;
    this.details = details;
  }

  public String serialize() {
    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put("errorType", this.errorType);
    responseMap.put("message", this.errorMessage);
    responseMap.put("details", this.details);

    return new ErrorResponse(responseMap).serialize();
  }
}
