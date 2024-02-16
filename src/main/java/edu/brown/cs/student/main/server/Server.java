package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.server.caching.CacheControl;
import edu.brown.cs.student.main.server.caching.CensusResponseLoader;
import edu.brown.cs.student.main.server.censusHandler.CensusHandler;
import edu.brown.cs.student.main.server.censusHandler.CensusSource;
import edu.brown.cs.student.main.server.censusHandler.Location;
import edu.brown.cs.student.main.server.csvEndpoints.CSVSource;
import edu.brown.cs.student.main.server.csvEndpoints.csvHandlers.CSVLoadHandler;
import edu.brown.cs.student.main.server.csvEndpoints.csvHandlers.CSVSearchHandler;
import edu.brown.cs.student.main.server.csvEndpoints.csvHandlers.CSVViewHandler;
import java.util.Map;
import spark.Spark;

/** The server for getting API requests. */
public class Server {
  public static void main(String[] args) {
    CSVSource creator = new CSVSource();
    CensusSource source = new CensusSource();
    CacheControl<Location, Map<String, Object>> cacheControl =
        new CacheControl<>(new CensusResponseLoader(source), true, 500, 5);

    int port = 3232;
    Spark.port(port);

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    // Setting up the handler for the csv and census endpoints.
    Spark.get("loadcsv", new CSVLoadHandler(creator));
    Spark.get("viewcsv", new CSVViewHandler(creator));
    Spark.get("searchcsv", new CSVSearchHandler(creator));
    Spark.get("broadband", new CensusHandler(cacheControl));

    Spark.init();
    Spark.awaitInitialization();

    System.out.println("Server started at http://localhost:" + port);
  }
}
