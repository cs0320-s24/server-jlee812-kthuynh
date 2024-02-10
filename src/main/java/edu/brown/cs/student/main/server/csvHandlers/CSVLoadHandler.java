package edu.brown.cs.student.main.server.csvHandlers;

import edu.brown.cs.student.main.server.datasource.CSVSource;
import edu.brown.cs.student.main.server.datasource.DataErrorResponse;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class CSVLoadHandler implements Route {
  private CSVSource source;

  public CSVLoadHandler(CSVSource creator) {
    this.source = creator;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String hasHeader = request.queryParams("header");
    String fileName = request.queryParams("file");
    Map<String, Object> responseMap = new HashMap<>();
    if (hasHeader == null || fileName == null) {
      response.status(400); // Set HTTP status code for Bad Request
      responseMap.put("hasHeader", hasHeader);
      responseMap.put("fileName", fileName);
      responseMap.put("type_error", "missing_parameter");
      responseMap.put("error_arg", fileName == null ? "hasHeader" : "fileName");
      return new DataErrorResponse(responseMap);
    }
    boolean hasHeaderBool = Boolean.parseBoolean(hasHeader);
    this.source.loadData(fileName, hasHeaderBool);
    return "Loaded!";
  }
}
