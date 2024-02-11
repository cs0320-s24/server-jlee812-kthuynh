package edu.brown.cs.student.Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.server.ErrorResponse;
import edu.brown.cs.student.main.server.csvHandlers.CSVSearchHandler;
import edu.brown.cs.student.main.server.csvHandlers.CSVViewHandler;
import edu.brown.cs.student.main.server.csvHandlers.loadHandler.CSVLoadHandler;
import edu.brown.cs.student.main.server.datasource.CSVSource;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class CSVHandlersTest {
  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }
  private CSVSource creator;
  @BeforeEach
  public void setup() {
    // Re-initialize state, etc. for _every_ test method run

    this.creator = new CSVSource();
    // In fact, restart the entire Spark server for every test!
    Spark.get("loadcsv", new CSVLoadHandler(this.creator));
    Spark.get("viewcsv", new CSVViewHandler(this.creator));
    Spark.get("searchcsv", new CSVSearchHandler(this.creator));
    Spark.awaitInitialization(); // don't continue until the server is listening
  }

  @AfterEach
  public void teardown() {
    this.creator.setIsLoaded();
    // Gracefully stop Spark listening on both endpoints after each test
    Spark.unmap("loadcsv");
    Spark.unmap("viewcsv");
    Spark.unmap("searchcsv");
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  /**
   * Helper to start a connection to a specific API endpoint/params
   *
   * @param apiCall the call string, including endpoint (NOTE: this would be better if it had more
   *     structure!)
   * @return the connection for the given URL, just after connecting
   * @throws IOException if the connection fails for some reason
   */
  private static HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection (but don't actually send the request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    // The default method is "GET", which is what we're using here.
    // If we were using "POST", we'd need to say so.
    clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }

  @Test
  public void testLoadHandlerNoFile() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    // Now we need to see whether we've got the expected Json response.
    Moshi moshi = new Moshi.Builder().build();
    ErrorResponse response =
        moshi
            .adapter(ErrorResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    System.out.println(response);
    // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but
    // a real Json reply.

    clientConnection.disconnect();
  }
  @Test
  public void testSearchHandlerNoValueAndColumn() throws IOException {
    HttpURLConnection clientConnection = tryRequest("searchcsv");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    // Now we need to see whether we've got the expected Json response.
    Moshi moshi = new Moshi.Builder().build();
    ErrorResponse response =
        moshi
            .adapter(ErrorResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    System.out.println(response);
    // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but
    // a real Json reply.

    clientConnection.disconnect();
  }
  @Test
  public void testViewHandlerNoFile() throws IOException {
    HttpURLConnection clientConnection = tryRequest("viewcsv");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    // Now we need to see whether we've got the expected Json response.
    Moshi moshi = new Moshi.Builder().build();
    ErrorResponse response =
        moshi
            .adapter(ErrorResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    System.out.println(response);
    // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but
    // a real Json reply.

    clientConnection.disconnect();
  }
}
