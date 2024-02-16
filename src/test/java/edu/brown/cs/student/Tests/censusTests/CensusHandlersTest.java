package edu.brown.cs.student.Tests.censusTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

/** Class with mocked data source that tests that connecting to census handlers works */
public class CensusHandlersTest {

  /** Begins port */
  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }

  private MockedCensusSource creator;

  /** Begins listener for broadband */
  @BeforeEach
  public void setup() {
    // Re-initialize state, etc. for _every_ test method run
    ArrayList<CensusResult> data = new ArrayList();
    data.add(new CensusResult("Cambridge", "47"));
    this.creator = new MockedCensusSource(data);
    // In fact, restart the entire Spark server for every test!
    Spark.get("broadband", new CensusHandler(this.creator));
    Spark.awaitInitialization(); // don't continue until the server is listening
  }

  /** Closes listener for broadband */
  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints after each test
    Spark.unmap("broadband");
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  /**
   * Helper to start a connection to a specific API endpoint/params
   *
   * @return the connection for the given URL, just after connecting
   * @throws IOException if the connection fails for some reason
   */
  private static HttpURLConnection tryRequest(String apiCall, String state, String county)
      throws IOException {
    // Configure the connection (but don't actually send the request yet)
    URL requestURL =
        new URL(
            "http://localhost:"
                + Spark.port()
                + "/"
                + apiCall
                + "?state="
                + state
                + "&county="
                + county);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    // The default method is "GET", which is what we're using here.
    // If we were using "POST", we'd need to say so.
    clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }

  /**
   * General version of tryRequest to take in one argument
   *
   * @param apiCall
   * @return
   * @throws IOException
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

  /**
   * Test for broadband working in valid arg
   *
   * @throws IOException
   */
  @Test
  public void testBroadbandGoodArg() throws IOException {
    HttpURLConnection clientConnection = tryRequest("broadband", "massachusetts", "middlesex");
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
    assertEquals(
        response.responseMap().get("data").toString().trim(), "[{county=Cambridge, broadband=47}]");
    assertEquals(response.responseMap().get("county"), "middlesex");
    assertEquals(response.responseMap().get("state"), "massachusetts");
    clientConnection.disconnect();
  }

  /**
   * Broadband connection doesn't crash when no county given
   *
   * @throws IOException
   */
  @Test
  public void testBroadbandNoCounty() throws IOException {
    HttpURLConnection clientConnection = tryRequest("broadband", "massachusetts", "");
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
    assertEquals(
        response.responseMap().get("data").toString().trim(), "[{county=Cambridge, broadband=47}]");
    assertEquals(response.responseMap().get("county"), "");
    assertEquals(response.responseMap().get("state"), "massachusetts");
    clientConnection.disconnect();
  }

  /**
   * Broadband connection doesn't crash when there's no state given
   *
   * @throws IOException
   */
  @Test
  public void testBroadbandNoState() throws IOException {
    HttpURLConnection clientConnection = tryRequest("broadband", "", "");
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
    assertEquals(
        response.responseMap().get("data").toString().trim(), "[{county=Cambridge, broadband=47}]");
    assertEquals(response.responseMap().get("county"), "");
    assertEquals(response.responseMap().get("state"), "");
    clientConnection.disconnect();
  }

  /**
   * Broadband doesn't crash when there's a 2 word arg
   *
   * @throws IOException
   */
  @Test
  public void testBroadbandArg2Words() throws IOException {
    HttpURLConnection clientConnection = tryRequest("broadband", "massachusetts", "gag%20city");
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
    assertEquals("gag city", response.responseMap().get("county"));
    clientConnection.disconnect();
  }

  /**
   * Broadband gives error when not enough args
   *
   * @throws IOException
   */
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

  /**
   * Tests that the timestamp in broadband exists, and is valid
   * @throws IOException
   */
  @Test
  public void testBroadbandTimestamp() throws IOException {
    HttpURLConnection clientConnection = tryRequest("broadband", "maine", "portland");
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
    String myDateString = (String) response.responseMap().get("time");
    String[] split = myDateString.split(":");
    int hour = Integer.valueOf(split[0]);
    float minute = Integer.valueOf(split[1]);
    assertTrue(hour >= 0 && hour <= 24);
    assertTrue(minute >= 0 && minute < 60);
    clientConnection.disconnect();
  }
}
