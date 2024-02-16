package edu.brown.cs.student.main.server.csvEndpoints.csvHandlers;

import edu.brown.cs.student.main.csv.search.HeaderValueException;
import edu.brown.cs.student.main.server.HandlerErrorBuilder;
import edu.brown.cs.student.main.server.DataSuccessResponse;
import edu.brown.cs.student.main.server.csvEndpoints.CSVSource;
import edu.brown.cs.student.main.server.csvEndpoints.UnloadedCSVException;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * A handler for searching the CSV.
 */
public class CSVSearchHandler implements Route {

  private final CSVSource source;

  /**
   * The constructor for the CSV search handler.
   * @param source The CSV datasource.
   */
  public CSVSearchHandler(CSVSource source) {
    this.source = source;
  }

  /**
   * The handler method.
   *
   * @param request The requested endpoint.
   * @param response The JSON response.
   * @return A JSON response of relevant information after searching the CSV.
   */
  @Override
  public Object handle(Request request, Response response) {
    String value = request.queryParams("value");
    String column = request.queryParams("column");

    // Handles an error if the value parameter was not given.
    if (value == null) {
      response.status(200);
      String errorType = "error_bad_request";
      String errorMessage = "The endpoint searchcsv is missing required queries";
      Map<String, String> details = new HashMap<>();
      details.put("value", null);
      details.put("column", column);
      details.put("error_arg", "value");
      return new HandlerErrorBuilder(errorType, errorMessage, details).serialize();
    }

    // Sends a response based on success or failure.
    try {
      Map<String, Object> responseMap = new HashMap<>();
      responseMap.put("value", value);
      responseMap.put("column", column);
      responseMap.put("results", this.source.search(value, column));
      return new DataSuccessResponse(responseMap).serialize();
    } catch (UnloadedCSVException e) {
      response.status(200);
      String errorType = "error_unloaded_csv";
      String errorMessage = e.getMessage();
      Map<String, String> details = new HashMap<>();
      return new HandlerErrorBuilder(errorType, errorMessage, details).serialize();
    } catch (HeaderValueException e) {
      response.status(200);
      String errorType = "error_bad_header_value";
      String errorMessage = e.getMessage();
      Map<String, String> details = new HashMap<>();
      details.put("column", column);
      details.put("error_arg", "column");
      details.put("valid_columns", this.source.getHeader().toString());
      return new HandlerErrorBuilder(errorType, errorMessage, details).serialize();
    }
  }
}
