package edu.brown.cs.student.main.server.csvHandlers;

import edu.brown.cs.student.main.csv.search.HeaderValueException;
import edu.brown.cs.student.main.server.HandlerErrorBuilder;
import edu.brown.cs.student.main.server.datasource.CSVSource;
import edu.brown.cs.student.main.server.datasource.DataSuccessResponse;
import edu.brown.cs.student.main.server.datasource.UnloadedCSVException;
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
    try {
      if (value == null) {
        response.status(200);
        String errorType = "missing_parameter";
        String errorMessage = "The endpoint searchcsv is missing required queries";
        Map<String, String> details = new HashMap<>();
        details.put("value", value);
        details.put("column", column);
        details.put("error_arg", "value");
        return new HandlerErrorBuilder(errorType, errorMessage, details).serialize();
      }

      List<List<String>> responseMap = this.source.search(value, column);
      return new DataSuccessResponse(responseMap).serialize();
    } catch (HeaderValueException e) {
      response.status(200);
      String errorType = "missing_header_value";
      String errorMessage = e.getMessage();
      Map<String, String> details = new HashMap<>();
      details.put("column", column);
      details.put("error_arg", "column");
      details.put("valid_header_values", this.source.getHeader().toString());
      return new HandlerErrorBuilder(errorType, errorMessage, details).serialize();
    } catch (UnloadedCSVException e) {
      response.status(200);
      String errorType = "unloaded_csv";
      String errorMessage = e.getMessage();
      Map<String, String> details = new HashMap<>();
      return new HandlerErrorBuilder(errorType, errorMessage, details).serialize();
    }
  }
}
