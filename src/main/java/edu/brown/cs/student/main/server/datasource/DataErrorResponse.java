package edu.brown.cs.student.main.server.datasource;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.Map;

public record DataErrorResponse(String response_type, Map<String, Object> responseMap) {

  public DataErrorResponse(Map<String, Object> responseMap) {
    this("error", responseMap);
  }

  public String serialize() {
    try {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<DataErrorResponse> adapter = moshi.adapter(DataErrorResponse.class);
      return adapter.toJson(this);
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
}
