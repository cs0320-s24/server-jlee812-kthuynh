package edu.brown.cs.student.main.server.csvHandlers;

import edu.brown.cs.student.main.server.datasource.CSVSource;
import edu.brown.cs.student.main.server.datasource.DataSuccessResponse;
import java.util.ArrayList;
import java.util.List;
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
    if (!this.source.getHeader().isEmpty()) {
      responseMap.add(this.source.getHeader());
    }
    responseMap.addAll(this.source.getData());
    return new DataSuccessResponse(responseMap).serialize();
  }
}
