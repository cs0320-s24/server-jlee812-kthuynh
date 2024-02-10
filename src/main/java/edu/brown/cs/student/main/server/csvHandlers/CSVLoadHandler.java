package edu.brown.cs.student.main.server.csvHandlers;

import edu.brown.cs.student.main.parser.CSVParser;
import edu.brown.cs.student.main.user.search.StringListFromRow;
import java.io.FileReader;
import java.util.List;
import spark.Request;
import spark.Response;
import spark.Route;

public class CSVLoadHandler implements Route {
  private CSVCreator creator;

  public CSVLoadHandler(CSVCreator creator) {
    this.creator = creator;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String hasHeader = request.queryParams("header");
    String fileName = request.queryParams("file");

    boolean hasHeaderBool = Boolean.parseBoolean(hasHeader);

    FileReader reader = new FileReader(fileName);
    CSVParser<List<String>> parser =
        new CSVParser<>(reader, new StringListFromRow(), hasHeaderBool);
    this.creator.setHeader(parser.getHeader());
    this.creator.setData(parser.parseCSV());

    for (List<String> row : this.creator.getData()) {
      System.out.println(row);
    }

    return "Loaded!";
  }
}
