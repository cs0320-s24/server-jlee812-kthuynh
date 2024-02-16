package edu.brown.cs.student.Tests.censusTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.server.DataSuccessResponse;
import edu.brown.cs.student.main.server.ErrorResponse;
import edu.brown.cs.student.main.server.HandlerErrorBuilder;
import edu.brown.cs.student.main.server.caching.CacheControl;
import edu.brown.cs.student.main.server.caching.CensusResponseLoader;
import edu.brown.cs.student.main.server.censusHandler.CensusHandler;
import edu.brown.cs.student.main.server.censusHandler.CensusResult;
import edu.brown.cs.student.main.server.censusHandler.Location;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import spark.Spark;

/** Class with mocked data source that tests that connecting to census handlers works */
public class CensusHandlersTest {

  /** Begins port */
  @BeforeClass
  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }

  /** Begins listener for broadband */
  @BeforeEach
  public void setup() {
    // Re-initialize state, etc. for _every_ test method run
    ArrayList<CensusResult> data = new ArrayList();
    data.add(new CensusResult("Middlesex County, Massachusetts", "47"));
    data.add(new CensusResult("Ventura County, California", "98"));
    MockedCensusSource mockedCensusSource = new MockedCensusSource(data);
    CacheControl<Location, Map<String, Object>> cacheControl =
        new CacheControl<>(new CensusResponseLoader(mockedCensusSource), true, 1, 1);

    // In fact, restart the entire Spark server for every test!
    Spark.get("broadband", new CensusHandler(cacheControl));
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
    HttpURLConnection clientConnection =
        tryRequest("broadband", "massachusetts", "middlesex%20county");
    // Get an OK response (the *connection* worked, the *API* provides a response)
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
        response.responseMap().get("data").toString().trim(),
        "[{county=Middlesex County, Massachusetts, broadband=47}]");
    assertEquals(response.responseMap().get("county"), "middlesex county");
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
    HttpURLConnection clientConnection = tryRequest("broadband?state=massachusetts");
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
        response.responseMap().get("data").toString().trim(),
        "[{county=Middlesex County, Massachusetts, broadband=47}]");
    assertEquals(response.responseMap().get("county"), "*");
    assertEquals(response.responseMap().get("state"), "massachusetts");
    clientConnection.disconnect();
  }

  /**
<<<<<<< HEAD
   * Broadband connection doesn't crash when there's no state given.
=======
   * Broadband connection doesn't crash when there's no state given
>>>>>>> 9e8233dab01a1cb70cbbd287288178125403649d
   *
   * @throws IOException
   */
  @Test
  public void testBroadbandNoState() throws IOException {
    HttpURLConnection clientConnection = tryRequest("broadband", "", "Middlesex%20County");
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

    String errorType = "error_bad_location";
    String errorMessage = "State not found: ";
    Map<String, String> details = new HashMap<>();
    details.put("state", "");
    details.put("county", "Middlesex County");
    details.put("error_arg", "state");
    String expectedResponse = new HandlerErrorBuilder(errorType, errorMessage, details).serialize();

    assertEquals(expectedResponse, response.serialize());
    clientConnection.disconnect();
  }

  /**
<<<<<<< HEAD
   * Broadband doesn't crash when there's a county that doesn't exist.
=======
   * Broadband doesn't crash when there's a 2 word arg
>>>>>>> 9e8233dab01a1cb70cbbd287288178125403649d
   *
   * @throws IOException
   */
  @Test
  public void testBroadbandCountyNotFound() throws IOException {
    HttpURLConnection clientConnection = tryRequest("broadband", "massachusetts", "");
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

    String errorType = "error_bad_location";
    String errorMessage = "County not found: ";
    Map<String, String> details = new HashMap<>();
    details.put("state", "massachusetts");
    details.put("county", "");
    details.put("error_arg", "county");
    String expectedResponse = new HandlerErrorBuilder(errorType, errorMessage, details).serialize();

    Assert.assertEquals(expectedResponse, response.serialize());
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

  /** Tests caching by looking at timestamps.*/
  @Test
  public void testCache() throws IOException, InterruptedException {
    HttpURLConnection clientConnection =
        tryRequest("broadband", "massachusetts", "middlesex%20county");
    // Get an OK response (the *connection* worked, the *API* provides a response)
    assertEquals(200, clientConnection.getResponseCode());

    HttpURLConnection clientConnection2 =
        tryRequest("broadband", "massachusetts", "middlesex%20county");
    // Get an OK response (the *connection* worked, the *API* provides a response)
    assertEquals(200, clientConnection.getResponseCode());

    // Now we need to see whether we've got the expected Json response.
    Moshi moshi = new Moshi.Builder().build();
    DataSuccessResponse response =
        moshi
            .adapter(DataSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    // Waits 5 seconds
    Thread.sleep(5000);

    // Now we need to see whether we've got the expected Json response.
    Moshi moshi2 = new Moshi.Builder().build();
    DataSuccessResponse response2 =
        moshi2
            .adapter(DataSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));

    // Makes sure the result was gotten from the query made previously.
    assertEquals(response.responseMap().get("time"), response2.responseMap().get("time"));

    clientConnection.disconnect();
    clientConnection2.disconnect();
  }

  /** Tests caching after expiration */
  @Test
  public void testCacheAfterExpiration() throws IOException, InterruptedException {
    // Search for a value.
    HttpURLConnection clientConnection =
        tryRequest("broadband", "massachusetts", "middlesex%20county");
    // Get an OK response (the *connection* worked, the *API* provides a response)
    assertEquals(200, clientConnection.getResponseCode());

    // Search for a new value, expiring the cache because of size.
    HttpURLConnection clientConnection2 = tryRequest("broadband", "california", "ventura%20county");
    // Get an OK response (the *connection* worked, the *API* provides a response)
    assertEquals(200, clientConnection.getResponseCode());

    // Search for the old value.
    HttpURLConnection clientConnection3 =
        tryRequest("broadband", "massachusetts", "middlesex%20county");
    // Get an OK response (the *connection* worked, the *API* provides a response)
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    DataSuccessResponse response =
        moshi
            .adapter(DataSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    Moshi moshi2 = new Moshi.Builder().build();
    moshi2
        .adapter(DataSuccessResponse.class)
        .fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));

    Moshi moshi3 = new Moshi.Builder().build();
    DataSuccessResponse response3 =
        moshi3
            .adapter(DataSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection3.getInputStream()));

    // Makes sure the result was gotten from the query made previously.
    Assert.assertNotEquals(response.responseMap().get("time"), response3.responseMap().get("time"));

    clientConnection.disconnect();
    clientConnection2.disconnect();
    clientConnection3.disconnect();
  }
}
