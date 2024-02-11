package edu.brown.cs.student.main.server.csvHandlers;

import edu.brown.cs.student.main.server.HandlerErrorBuilder;
import edu.brown.cs.student.main.server.datasource.CSVSource;
import edu.brown.cs.student.main.server.datasource.DataSuccessResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class CSVSearchHandler implements Route {

  private final CSVSource source;

  public CSVSearchHandler(CSVSource source) {
    this.source = source;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String value = request.queryParams("value");
    String column = request.queryParams("column");
    if (value == null && column == null) {
      response.status(200);
      String errorType = "missing_parameter";
      String errorMessage = "The endpoint searchcsv is missing requires queries";
      Map<String, String> details = new HashMap<>();
      details.put("value", value);
      details.put("column", column);
      details.put("error_arg", value == null ? "value" : "column");
      return new HandlerErrorBuilder(errorType, errorMessage, details).serialize();
    }
    List<List<String>> responseMap = this.source.search(value, column);

    return new DataSuccessResponse(responseMap).serialize();
  }
}
