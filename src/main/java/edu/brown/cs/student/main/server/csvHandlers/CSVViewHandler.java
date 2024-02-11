package edu.brown.cs.student.main.server.csvHandlers;

import edu.brown.cs.student.main.server.HandlerErrorBuilder;
import edu.brown.cs.student.main.server.datasource.CSVSource;
import edu.brown.cs.student.main.server.datasource.DataSuccessResponse;
import edu.brown.cs.student.main.server.datasource.UnloadedCSVException;
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
    List<List<String>> responseMap = new ArrayList<>();
    try {
      if (!this.source.getHeader().isEmpty()) {
        responseMap.add(this.source.getHeader());
      }
      responseMap.addAll(this.source.getData());
      return new DataSuccessResponse(responseMap).serialize();
    } catch (UnloadedCSVException e) {
      response.status(200);
      String errorType = "unloaded_csv";
      String errorMessage = e.getMessage();
      Map<String, String> details = new HashMap<>();
      return new HandlerErrorBuilder(errorType, errorMessage, details).serialize();
    }
  }
}
