package edu.brown.cs.student.main.server.csvHandlers;

import edu.brown.cs.student.main.server.datasource.CSVSource;
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
    boolean hasHeaderBool = Boolean.parseBoolean(hasHeader);
    this.source.loadData(fileName, hasHeaderBool);
    return "Loaded!";
  }
}
