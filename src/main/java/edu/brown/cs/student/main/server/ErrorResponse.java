package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.Map;

public record ErrorResponse(String result, Map<String, Object> responseMap) {

  public ErrorResponse(Map<String, Object> responseMap) {
    this("error", responseMap);
  }

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
