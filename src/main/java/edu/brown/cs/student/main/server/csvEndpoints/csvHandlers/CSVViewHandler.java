package edu.brown.cs.student.main.server.csvEndpoints.csvHandlers;

import edu.brown.cs.student.main.server.HandlerErrorBuilder;
import edu.brown.cs.student.main.server.DataSuccessResponse;
import edu.brown.cs.student.main.server.csvEndpoints.CSVSource;
import edu.brown.cs.student.main.server.csvEndpoints.UnloadedCSVException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class CSVViewHandler implements Route {
  private final CSVSource source;

  public CSVViewHandler(CSVSource source) {
    this.source = source;
  }

  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();
    List<List<String>> data = new ArrayList<>();
    try {
      if (!this.source.getHeader().isEmpty()) {
        data.add(this.source.getHeader());
      }
      data.addAll(this.source.getData());
      responseMap.put("results", data);
      return new DataSuccessResponse(responseMap).serialize();
    } catch (UnloadedCSVException e) {
      response.status(200);
      String errorType = "error_unloaded_csv";
      String errorMessage = e.getMessage();
      Map<String, String> details = new HashMap<>();
      return new HandlerErrorBuilder(errorType, errorMessage, details).serialize();
    }
  }
}
