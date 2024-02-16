package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.Map;

/**
 * A record representing an unsuccessful response.
 * @param response_type The type of response.
 * @param responseMap The response map.
 */
public record ErrorResponse(String response_type, Map<String, Object> responseMap) {

  /**
   * The constructor for the response.
   * @param responseMap The response map.
   */
  public ErrorResponse(Map<String, Object> responseMap) {
    this("error", responseMap);
  }

  /**
   * A method that serializes the response into a JSON.
   * @return The response as a JSON.
   */
  public String serialize() {
    try {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<ErrorResponse> adapter = moshi.adapter(ErrorResponse.class);
      return adapter.toJson(this);
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
}
