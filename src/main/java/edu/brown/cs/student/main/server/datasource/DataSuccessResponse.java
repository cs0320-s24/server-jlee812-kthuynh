package edu.brown.cs.student.main.server.datasource;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.List;

public record DataSuccessResponse(String response_type, List<List<String>> responseMap) {

  public DataSuccessResponse(List<List<String>> responseMap) {
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
