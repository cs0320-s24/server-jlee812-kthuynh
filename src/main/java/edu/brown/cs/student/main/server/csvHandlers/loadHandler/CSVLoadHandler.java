package edu.brown.cs.student.main.server.csvHandlers.loadHandler;

import edu.brown.cs.student.main.csv.parser.FactoryFailureException;
import edu.brown.cs.student.main.server.HandlerErrorBuilder;
import edu.brown.cs.student.main.server.datasource.CSVSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class CSVLoadHandler implements Route {
  private final CSVSource source;

  public CSVLoadHandler(CSVSource creator) {
    this.source = creator;
  }

  @Override
  public Object handle(Request request, Response response) {
    String hasHeader = request.queryParams("header");
    String fileName = request.queryParams("file");
    Map<String, Object> responseMap = new HashMap<>();

    // If there are missing parameters.
    if (hasHeader == null || fileName == null) {
      response.status(400);
      String errorType = "missing_parameter";
      String errorMessage = "The endpoint loadcsv is missing requires queries";
      Map<String, String> details = new HashMap<>();
      details.put("header", hasHeader);
      details.put("file", fileName);
      details.put("error_arg", fileName == null ? "file" : "header");
      return new HandlerErrorBuilder(errorType, errorMessage, details).serialize();
    }

    // If the file is not in the protected directory
    if (!fileName.startsWith("data")) {
      response.status(400);
      String errorType = "invalid_parameter";
      String errorMessage = "File must be in data folder";
      Map<String, String> details = new HashMap<>();
      details.put("error_arg", "file");
      details.put("file", fileName);
      return new HandlerErrorBuilder(errorType, errorMessage, details).serialize();
    }

    // If the header is not a boolean value
    if (!hasHeader.equalsIgnoreCase("true") && !hasHeader.equalsIgnoreCase("false")) {
      response.status(400);
      String errorType = "invalid_parameter";
      String errorMessage = "Header must be true or false";
      Map<String, String> details = new HashMap<>();
      details.put("error_arg", "header");
      details.put("header", hasHeader);
      return new HandlerErrorBuilder(errorType, errorMessage, details).serialize();
    }

    boolean hasHeaderBool = Boolean.parseBoolean(hasHeader);

    try {
      this.source.loadData(fileName, hasHeaderBool);
      responseMap.put("file", fileName);
      responseMap.put("header", hasHeader);
      return new LoadSuccessResponse(responseMap).serialize();
    } catch (IOException e) {
      response.status(500);
      String errorType = "file_reading_error";
      String errorMessage = e.getMessage();
      Map<String, String> details = new HashMap<>();
      details.put("error_arg", "file");
      details.put("file", fileName);
      return new HandlerErrorBuilder(errorType, errorMessage, details).serialize();
    } catch (FactoryFailureException e) {
      String errorType = "malformed_csv";
      String errorMessage = e.getMessage();
      Map<String, String> details = new HashMap<>();
      details.put("error_at_row", e.getRow().toString());
      return new HandlerErrorBuilder(errorType, errorMessage, details).serialize();
    }
  }
}
