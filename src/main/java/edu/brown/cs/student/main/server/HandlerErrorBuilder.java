package edu.brown.cs.student.main.server;

import java.util.HashMap;
import java.util.Map;

/** A class that creates the error response. */
public class HandlerErrorBuilder {
  private final String errorType;
  private final String errorMessage;
  private final Map<String, String> details;

  /**
   * The constructor for the error builder.
   *
   * @param errorType The type of error.
   * @param errorMessage The message of the error.
   * @param details Relevant details for the error.
   */
  public HandlerErrorBuilder(String errorType, String errorMessage, Map<String, String> details) {
    this.errorType = errorType;
    this.errorMessage = errorMessage;
    this.details = details;
  }

  /**
   * A method that serializes the response into a JSON.
   *
   * @return The response as a JSON.
   */
  public String serialize() {
    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put("errorType", this.errorType);
    responseMap.put("message", this.errorMessage);
    responseMap.put("details", this.details);

    return new ErrorResponse(responseMap).serialize();
  }
}
