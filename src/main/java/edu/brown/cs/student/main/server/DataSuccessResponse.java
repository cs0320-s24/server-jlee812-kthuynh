package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.Map;

public record DataSuccessResponse(String response_type, Map<String, Object> responseMap) {

  public DataSuccessResponse(Map<String, Object> responseMap) {
    this("success", responseMap);
  }

  public String serialize() {
    try {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<DataSuccessResponse> adapter = moshi.adapter(DataSuccessResponse.class);
      return adapter.toJson(this);
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
}
