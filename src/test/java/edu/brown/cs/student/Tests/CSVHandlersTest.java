package edu.brown.cs.student.Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.csv.parser.CSVParser;
import edu.brown.cs.student.main.csv.parser.FactoryFailureException;
import edu.brown.cs.student.main.csv.search.StringListFromRow;
import edu.brown.cs.student.main.server.DataSuccessResponse;
import edu.brown.cs.student.main.server.ErrorResponse;
import edu.brown.cs.student.main.server.HandlerErrorBuilder;
import edu.brown.cs.student.main.server.csvEndpoints.CSVSource;
import edu.brown.cs.student.main.server.csvEndpoints.csvHandlers.CSVLoadHandler;
import edu.brown.cs.student.main.server.csvEndpoints.csvHandlers.CSVSearchHandler;
import edu.brown.cs.student.main.server.csvEndpoints.csvHandlers.CSVViewHandler;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.Assert;
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

  /**
   * tryRequest for load handler
   *
   * @param apiCall
   * @param file
   * @return
   * @throws IOException
   */
  private static HttpURLConnection tryLoadRequest(String apiCall, String header, String file)
      throws IOException {
    // Configure the connection (but don't actually send the request yet)
    URL requestURL =
        new URL(
            "http://localhost:"
                + Spark.port()
                + "/"
                + apiCall
                + "?header="
                + header
                + "&filePath="
                + file);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    // The default method is "GET", which is what we're using here.
    // If we were using "POST", we'd need to say so.
    clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }

  /**
   * tryRequest for CSVSearchHandler
   *
   * @param apiCall
   * @param value
   * @param col
   * @return
   * @throws IOException
   */
  private static HttpURLConnection trySearchRequest(String apiCall, String value, String col)
      throws IOException {
    // Configure the connection (but don't actually send the request yet)
    URL requestURL =
        new URL(
            "http://localhost:"
                + Spark.port()
                + "/"
                + apiCall
                + "?value="
                + value
                + "&column="
                + col);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    // The default method is "GET", which is what we're using here.
    // If we were using "POST", we'd need to say so.
    clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }

  /**
   * Alternative tryRequest for search, without col specified
   *
   * @param apiCall
   * @param value
   * @return
   * @throws IOException
   */
  private static HttpURLConnection trySearchRequest(String apiCall, String value)
      throws IOException {
    // Configure the connection (but don't actually send the request yet)
    URL requestURL =
        new URL("http://localhost:" + Spark.port() + "/" + apiCall + "?value=" + value);
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

  @Test
  public void testLoadValidFile() throws IOException {
    HttpURLConnection clientConnection =
        tryLoadRequest("loadcsv", "true", "data/census/dol_ri_earnings_disparity.csv");
    // Get an OK response (the *connection* worked, the *API* provides a success response)
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

    HashMap<String, Object> responseMap = new HashMap<>();
    responseMap.put("filePath", "data/census/dol_ri_earnings_disparity.csv");
    responseMap.put("header", "true");
    String expectedResponse = new DataSuccessResponse(responseMap).serialize();

    Assert.assertEquals(expectedResponse, response.serialize());

    clientConnection.disconnect();
  }

  @Test
  public void testLoadTwice() throws IOException {
    HttpURLConnection clientConnection1 =
        tryLoadRequest("loadcsv", "true", "data/stars/stardata.csv");
    // Get an OK response (the *connection* worked, the *API* provides a success response)
    assertEquals(200, clientConnection1.getResponseCode());
    HttpURLConnection clientConnection =
        tryLoadRequest("loadcsv", "true", "data/census/dol_ri_earnings_disparity.csv");
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

    HashMap<String, Object> responseMap = new HashMap<>();
    responseMap.put("filePath", "data/census/dol_ri_earnings_disparity.csv");
    responseMap.put("header", "true");
    String expectedResponse = new DataSuccessResponse(responseMap).serialize();

    Assert.assertEquals(expectedResponse, response.serialize());;

    clientConnection.disconnect();
    clientConnection1.disconnect();
  }

  @Test
  public void testSearchValidCol() throws IOException, FactoryFailureException {
    HttpURLConnection clientConnection =
        tryLoadRequest("loadcsv", "true", "data/census/dol_ri_" + "earnings_disparity.csv");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    HttpURLConnection clientConnection2 = trySearchRequest("searchcsv", "white", "1");
    // Now we need to see whether we've got the expected Json response.
    assertEquals(200, clientConnection2.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    DataSuccessResponse response =
        moshi
            .adapter(DataSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));

    // Create the expected response.
    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put("column", "1");
    responseMap.put("value", "white");
    StringReader stringReader =
        new StringReader("RI,White,\" $1,058.47 \",395773.6521, $1.00 ,75%");
    CSVParser csvParser = new CSVParser<>(stringReader, new StringListFromRow(), false);
    responseMap.put("results", csvParser.parseCSV());
    String expectedResponse = new DataSuccessResponse(responseMap).serialize();

    assertEquals(expectedResponse, response.serialize());

    clientConnection.disconnect();
    clientConnection2.disconnect();
  }

  @Test
  public void testSearchValidColNoHeader() throws IOException, FactoryFailureException {
    HttpURLConnection clientConnection =
        tryLoadRequest("loadcsv", "false", "data/census/ri_income_us_census_2021.csv");
    assertEquals(200, clientConnection.getResponseCode());

    HttpURLConnection clientConnection2 = trySearchRequest("searchcsv", "CRANSTON");
    assertEquals(200, clientConnection2.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    DataSuccessResponse response =
        moshi
            .adapter(DataSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));

    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put("column", null);
    responseMap.put("value", "CRANSTON");
    StringReader stringReader =
        new StringReader("Cranston,\"77,145.00\",\"95,763.00\",\"38,269.00\"");
    CSVParser csvParser = new CSVParser<>(stringReader, new StringListFromRow(), false);
    responseMap.put("results", csvParser.parseCSV());
    String expectedResponse = new DataSuccessResponse(responseMap).serialize();

    assertEquals(expectedResponse, response.serialize());

    clientConnection.disconnect();
    clientConnection2.disconnect();
  }

  /**
   * Tests that a bad search input doesn't crash the program. Instead it is caught by the program,
   * and displays an empty responseMap
   *
   * @throws IOException
   */
  @Test
  public void testSearchBadInput() throws IOException {
    HttpURLConnection clientConnection =
        tryLoadRequest("loadcsv", "false", "data/census/ri_income_us_census_2021.csv");
    assertEquals(200, clientConnection.getResponseCode());

    HttpURLConnection clientConnection2 = trySearchRequest("searchcsv", "hi");
    assertEquals(200, clientConnection2.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    DataSuccessResponse response =
        moshi
            .adapter(DataSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));

    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put("value", "hi");
    responseMap.put("results", new ArrayList<>());
    String expectedResponse = new DataSuccessResponse(responseMap).serialize();

    assertEquals(expectedResponse, response.serialize());

    clientConnection.disconnect();
    clientConnection2.disconnect();
  }

  /**
   * Tests when a search request includes an invalid header.
   *
   * @throws IOException
   */
  @Test
  public void testSearchBadHeader() throws IOException {
    HttpURLConnection clientConnection =
        tryLoadRequest("loadcsv", "true", "data/census/ri_income_us_census_2021.csv");
    assertEquals(200, clientConnection.getResponseCode());

    HttpURLConnection clientConnection2 = trySearchRequest("searchcsv", "hi", "no");
    assertEquals(200, clientConnection2.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    ErrorResponse response =
        moshi
            .adapter(ErrorResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));

    String errorType = "error_bad_header_value";
    String errorMessage = "no not found in header!";
    Map<String, String> details = new HashMap<>();
    details.put("column", "no");
    details.put("error_arg", "column");
    details.put(
        "valid_columns",
        "[City/Town, Median Household Income, Median Family Income, Per Capita Income]");
    String expectedResponse = new HandlerErrorBuilder(errorType, errorMessage, details).serialize();

    assertEquals(expectedResponse, response.serialize());

    clientConnection.disconnect();
    clientConnection2.disconnect();
  }

  /**
   * Tests that in the case where 2 columns have a value, both rows are included.
   *
   * @throws IOException
   */
  @Test
  public void testSearchMultipleCols() throws IOException, FactoryFailureException {
    HttpURLConnection clientConnection =
        tryLoadRequest("loadcsv", "true", "data/census/dol_ri_earnings_disparity2.csv");
    assertEquals(200, clientConnection.getResponseCode());

    HttpURLConnection clientConnection2 = trySearchRequest("searchcsv", "Multiracial");
    assertEquals(200, clientConnection2.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    DataSuccessResponse response =
        moshi
            .adapter(DataSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));

    // Create the expected response.
    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put("value", "Multiracial");
    StringReader stringReader =
        new StringReader("RI,Asian-Pacific Islander,\" $1,080.09 \",18956.71657,$1.02,4%,Multiracial \n"
            + "RI,Multiracial,$971.89,8883.049171,$0.92,2%,Hispanic/Latino");
    CSVParser csvParser = new CSVParser<>(stringReader, new StringListFromRow(), false);
    responseMap.put("results", csvParser.parseCSV());
    String expectedResponse = new DataSuccessResponse(responseMap).serialize();

    assertEquals(expectedResponse, response.serialize());

    clientConnection.disconnect();
    clientConnection2.disconnect();
  }

  /**
   * Tests that after loading a file, and loading a second one, search works correctly.
   * @throws IOException
   * @throws FactoryFailureException
   */
  @Test
  public void testSearchAfterLoadingTwoFiles() throws IOException, FactoryFailureException {
    HttpURLConnection clientConnection =
        tryLoadRequest("loadcsv", "true", "data/census/dol_ri_"
            + "earnings_disparity.csv");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    HttpURLConnection clientConnection2 = trySearchRequest("searchcsv", "white", "1");
    // Now we need to see whether we've got the expected Json response.
    assertEquals(200, clientConnection2.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    DataSuccessResponse response =
        moshi
            .adapter(DataSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));

    // Create the expected response.
    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put("column", "1");
    responseMap.put("value", "white");
    StringReader stringReader =
        new StringReader("RI,White,\" $1,058.47 \",395773.6521, $1.00 ,75%");
    CSVParser csvParser = new CSVParser<>(stringReader, new StringListFromRow(), false);
    responseMap.put("results", csvParser.parseCSV());
    String expectedResponse = new DataSuccessResponse(responseMap).serialize();

    assertEquals(expectedResponse, response.serialize());

    // test loading a second file and searching again works

    HttpURLConnection clientConnection3 =
        tryLoadRequest("loadcsv", "true", "data/census/ri_income_us_census_2021.csv");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection3.getResponseCode());

    HttpURLConnection clientConnection4 = trySearchRequest("searchcsv", "Charlestown",
        "0");
    // Now we need to see whether we've got the expected Json response.
    assertEquals(200, clientConnection4.getResponseCode());
    DataSuccessResponse response2 =
        moshi
            .adapter(DataSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection4.getInputStream()));

    // Create the expected response.
    Map<String, Object> responseMap2 = new HashMap<>();
    responseMap2.put("column", "0");
    responseMap2.put("value", "Charlestown");
    StringReader stringReader2 =
        new StringReader("Charlestown,\"86,023.00\",\"102,325.00\",\"50,086.00\"");
    csvParser = new CSVParser<>(stringReader2, new StringListFromRow(), false);
    responseMap2.put("results", csvParser.parseCSV());
    String expectedResponse2 = new DataSuccessResponse(responseMap2).serialize();

    assertEquals(expectedResponse2, response2.serialize());

    clientConnection.disconnect();
    clientConnection2.disconnect();
    clientConnection3.disconnect();
    clientConnection4.disconnect();
  }
}
