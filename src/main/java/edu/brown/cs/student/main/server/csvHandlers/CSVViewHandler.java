package edu.brown.cs.student.main.server.csvHandlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class CSVViewHandler implements Route {
  private CSVCreator creator;

  public CSVViewHandler(CSVCreator creator) {
    this.creator = creator;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    List<Map<String, String>> responseMap = new ArrayList<>();
    List<List<String>> data = this.creator.getData();
    List<String> header = this.creator.getHeader();

    for (List<String> row : data) {
      Map<String, String> map = new HashMap<>();
      for (int i = 0; i < row.size(); i++) {
        if (header.isEmpty()) {
          map.put("col" + i, row.get(i));
        } else {
          map.put(header.get(i), row.get(i));
        }
      }
      responseMap.add(map);
    }

    return new DataSuccessResponse(responseMap).serialize();
  }

  public record DataSuccessResponse(String response_type, List<Map<String, String>> responseMap) {
    public DataSuccessResponse(List<Map<String, String>> responseMap) {
      this("success", responseMap);
    }

    String serialize() {
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
}
