package edu.brown.cs.student.main.server.csvHandlers;

import edu.brown.cs.student.main.server.datasource.CSVSource;
import edu.brown.cs.student.main.server.datasource.DataSuccessResponse;
import java.util.List;
import spark.Request;
import spark.Response;
import spark.Route;

public class CSVSearchHandler implements Route {

  private CSVSource source;

  public CSVSearchHandler(CSVSource source) {
    this.source = source;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String value = request.queryParams("value");
    String column = request.queryParams("column");

    List<List<String>> responseMap = this.source.search(value, column);

    return new DataSuccessResponse(responseMap).serialize();
  }
}
