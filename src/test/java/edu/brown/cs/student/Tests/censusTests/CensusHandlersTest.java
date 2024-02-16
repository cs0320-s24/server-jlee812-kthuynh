package edu.brown.cs.student.Tests.censusTests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.server.DataSuccessResponse;
import edu.brown.cs.student.main.server.ErrorResponse;
import edu.brown.cs.student.main.server.censusHandler.CensusHandler;
import edu.brown.cs.student.main.server.censusHandler.CensusResult;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class CensusHandlersTest {
  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }

  private MockedCensusSource creator;

  @BeforeEach
  public void setup() {
    // Re-initialize state, etc. for _every_ test method run
    ArrayList<CensusResult> data = new ArrayList();
    data.add(new CensusResult("Cambridge","47"));
    this.creator = new MockedCensusSource(data);
    // In fact, restart the entire Spark server for every test!
    Spark.get("broadband", new CensusHandler(this.creator));
    Spark.awaitInitialization(); // don't continue until the server is listening
  }

  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints after each test
    Spark.unmap("broadband");
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
  private static HttpURLConnection tryRequest(String apiCall, String state, String county)
      throws IOException {
    // Configure the connection (but don't actually send the request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall
    + "?state=" + state + "&county=" + county);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    // The default method is "GET", which is what we're using here.
    // If we were using "POST", we'd need to say so.
    clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }

  private static HttpURLConnection tryRequest(String apiCall)
      throws IOException {
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
  public void testBroadbandGoodArg() throws IOException {
    HttpURLConnection clientConnection = tryRequest("broadband","massachusetts",
        "middlesex");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    // Now we need to see whether we've got the expected Json response.
    Moshi moshi = new Moshi.Builder().build();
    DataSuccessResponse response =
        moshi
            .adapter(DataSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    System.out.println(response);
    // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but
    // a real Json reply.
    assertEquals(response.responseMap().get("data").toString().trim(),
        "[{county=Cambridge, broadband=47}]");
    assertEquals(response.responseMap().get("county"),
        "middlesex");
    assertEquals(response.responseMap().get("state"),
        "massachusetts");
    clientConnection.disconnect();
  }

  @Test
  public void testBroadbandNoCounty() throws IOException {
    HttpURLConnection clientConnection = tryRequest("broadband","massachusetts",
        "");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    // Now we need to see whether we've got the expected Json response.
    Moshi moshi = new Moshi.Builder().build();
    DataSuccessResponse response =
        moshi
            .adapter(DataSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    System.out.println(response);
    // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but
    // a real Json reply.
    assertEquals(response.responseMap().get("data").toString().trim(),
        "[{county=Cambridge, broadband=47}]");
    assertEquals(response.responseMap().get("county"),
        "");
    assertEquals(response.responseMap().get("state"),
        "massachusetts");
    clientConnection.disconnect();
  }
  @Test
  public void testBroadbandNoState() throws IOException {
    HttpURLConnection clientConnection = tryRequest("broadband","",
        "");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    // Now we need to see whether we've got the expected Json response.
    Moshi moshi = new Moshi.Builder().build();
    DataSuccessResponse response =
        moshi
            .adapter(DataSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    System.out.println(response);
    // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but
    // a real Json reply.
    assertEquals(response.responseMap().get("data").toString().trim(),
        "[{county=Cambridge, broadband=47}]");
    assertEquals(response.responseMap().get("county"),
        "");
    assertEquals(response.responseMap().get("state"),
        "");
    clientConnection.disconnect();
  }
  @Test
  public void testBroadbandArg2Words() throws IOException {
    HttpURLConnection clientConnection = tryRequest("broadband","massachusetts",
        "gag_city");
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
    assertEquals("success", response.result());
    clientConnection.disconnect();
  }

  @Test
  public void testBadConnection() throws IOException {
    HttpURLConnection clientConnection = tryRequest("broadband");
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
    assertEquals("error", response.result());
    clientConnection.disconnect();
  }

}
