package edu.brown.cs.student.main.server.csvEndpoints.csvHandlers;

import edu.brown.cs.student.main.csv.parser.FactoryFailureException;
import edu.brown.cs.student.main.server.HandlerErrorBuilder;
import edu.brown.cs.student.main.server.DataSuccessResponse;
import edu.brown.cs.student.main.server.csvEndpoints.CSVSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** The handler for loading the CSV. */
public class CSVLoadHandler implements Route {
  private final CSVSource source;

  /**
   * The constructor for the CSV load handler.
   *
   * @param source The source of the CSV data.
   */
  public CSVLoadHandler(CSVSource source) {
    this.source = source;
  }

  /**
   * The handler method.
   *
   * @param request The requested endpoint.
   * @param response The JSON response.
   * @return A JSON response of relevant information after loading the CSV.
   */
  @Override
  public Object handle(Request request, Response response) {
    String hasHeader = request.queryParams("header");
    String fileName = request.queryParams("file");
    Map<String, Object> responseMap = new HashMap<>();

    // If there are missing parameters.
    if (hasHeader == null || fileName == null) {
      response.status(200);
      String errorType = "error_bad_request";
      String errorMessage = "The endpoint loadcsv is missing required queries";
      Map<String, String> details = new HashMap<>();
      details.put("header", hasHeader);
      details.put("file", fileName);
      details.put("error_arg", fileName == null ? "file" : "header");
      return new HandlerErrorBuilder(errorType, errorMessage, details).serialize();
    }

    // If the file is not in the protected directory
    if (!fileName.startsWith("data")) {
      response.status(400);
      String errorType = "error_bad_request";
      String errorMessage = "File must be in data folder";
      Map<String, String> details = new HashMap<>();
      details.put("error_arg", "file");
      details.put("file", fileName);
      return new HandlerErrorBuilder(errorType, errorMessage, details).serialize();
    }

    // If the header is not a boolean value
    if (!hasHeader.equalsIgnoreCase("true") && !hasHeader.equalsIgnoreCase("false")) {
      response.status(400);
      String errorType = "error_bad_request";
      String errorMessage = "Header must be true or false";
      Map<String, String> details = new HashMap<>();
      details.put("error_arg", "header");
      details.put("header", hasHeader);
      return new HandlerErrorBuilder(errorType, errorMessage, details).serialize();
    }

    boolean hasHeaderBool = Boolean.parseBoolean(hasHeader);

    // Send a response on success or failure.
    try {
      this.source.loadData(fileName, hasHeaderBool);
      responseMap.put("file", fileName);
      responseMap.put("header", hasHeader);
      return new DataSuccessResponse(responseMap).serialize();
    } catch (IOException e) {
      response.status(500);
      String errorType = "error_datasource";
      String errorMessage = e.getMessage();
      Map<String, String> details = new HashMap<>();
      details.put("error_arg", "file");
      details.put("file", fileName);
      return new HandlerErrorBuilder(errorType, errorMessage, details).serialize();
    } catch (FactoryFailureException e) {
      String errorType = "error_malformed_csv";
      String errorMessage = e.getMessage();
      Map<String, String> details = new HashMap<>();
      details.put("error_at_row", e.getRow().toString());
      return new HandlerErrorBuilder(errorType, errorMessage, details).serialize();
    }
  }
}
