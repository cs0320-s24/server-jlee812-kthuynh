package edu.brown.cs.student.main.server.csvHandlers.loadHandler;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.Map;

public record LoadSuccessResponse(String result, Map<String, Object> responseMap) {

  public LoadSuccessResponse(Map<String, Object> responseMap) {
    this("success", responseMap);
  }

  public String serialize() {
    try {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<LoadSuccessResponse> adapter = moshi.adapter(LoadSuccessResponse.class);
      return adapter.toJson(this);
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
}
