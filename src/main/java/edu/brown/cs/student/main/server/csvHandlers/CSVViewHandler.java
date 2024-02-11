package edu.brown.cs.student.main.server.csvHandlers;

import edu.brown.cs.student.main.server.HandlerErrorBuilder;
import edu.brown.cs.student.main.server.datasource.CSVSource;
import edu.brown.cs.student.main.server.datasource.DataSuccessResponse;
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
  public Object handle(Request request, Response response) throws Exception {
    List<List<String>> responseMap = new ArrayList<>();
    if (!this.source.getIsLoaded()) {
      response.status(200);
      String errorType = "missing_file";
      String errorMessage = "A file hasn't been loaded to the CSV parser";
      Map<String, String> details = new HashMap<>();
      details.put("missing_file", this.source.getData().toString());
//      details.put("error_arg", this.source.getData() == null ? "missing_file");
      return new HandlerErrorBuilder(errorType, errorMessage, details).serialize();
    }
    if (!this.source.getHeader().isEmpty()) {
      responseMap.add(this.source.getHeader());
    }
    responseMap.addAll(this.source.getData());
    return new DataSuccessResponse(responseMap).serialize();
  }
}
