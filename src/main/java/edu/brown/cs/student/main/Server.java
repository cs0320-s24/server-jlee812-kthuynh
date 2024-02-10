package edu.brown.cs.student.main;

import static spark.Spark.after;

import java.util.ArrayList;
import java.util.List;
import spark.Spark;

public class Server {
  public static void main(String[] args) {
    int port = 3232;
    Spark.port(port);

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    // Setting up the handler for the GET /order and /activity endpoints
    //Spark.get("order", new OrderHandler(menu));
    //Spark.get("activity", new ActivityHandler());
    Spark.init();
    Spark.awaitInitialization();

    System.out.println("Server started at http://localhost:" + port);
  }
}
