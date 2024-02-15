package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.server.censusHandler.CensusHandler;
import edu.brown.cs.student.main.server.censusHandler.CensusSource;
import edu.brown.cs.student.main.server.csvEndpoints.csvHandlers.CSVSearchHandler;
import edu.brown.cs.student.main.server.csvEndpoints.csvHandlers.CSVViewHandler;
import edu.brown.cs.student.main.server.csvEndpoints.csvHandlers.CSVLoadHandler;
import edu.brown.cs.student.main.server.csvEndpoints.CSVSource;
import spark.Spark;

public class Server {
  public static void main(String[] args) {
    CSVSource creator = new CSVSource();
    CensusSource source = new CensusSource();

    int port = 3232;
    Spark.port(port);

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    // Setting up the handler for the GET /order and /activity endpoints
    // Spark.get("order", new OrderHandler(menu));
    // Spark.get("activity", new ActivityHandler());
    Spark.get("loadcsv", new CSVLoadHandler(creator));
    Spark.get("viewcsv", new CSVViewHandler(creator));
    Spark.get("searchcsv", new CSVSearchHandler(creator));
    Spark.get("broadband", new CensusHandler(source));

    Spark.init();
    Spark.awaitInitialization();

    System.out.println("Server started at http://localhost:" + port);
  }
}
