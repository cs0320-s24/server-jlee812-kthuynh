package edu.brown.cs.student.main.server.censusHandler;

import edu.brown.cs.student.main.server.DataSuccessResponse;
import edu.brown.cs.student.main.server.HandlerErrorBuilder;
import edu.brown.cs.student.main.server.caching.CacheControl;
import edu.brown.cs.student.main.server.caching.CensusResponseLoader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** The handler for the census API */
public class CensusHandler implements Route {
  private final CacheControl<Location, Map<String, Object>> cacher;

  /**
   * The constructor for the handler.
   *
   * @param source The data source for the handler.
   */
  public CensusHandler(CensusSourceInterface source) {
    this.cacher = new CacheControl<>(new CensusResponseLoader(source), true, 500, 5);
  }

  /**
   * The handling method to send the response.
   *
   * @param request The endpoint requested.
   * @param response The JSON sent as a response.
   * @return A JSON representing either a success or failure.
   */
  @Override
  public Object handle(Request request, Response response) {
    String state = request.queryParams("state");
    String county = request.queryParams("county");

    // If the state query was not included, send an error.
    if (state == null) {
      response.status(200);
      String errorType = "error_bad_request";
      String errorMessage = "The endpoint broadband is missing required queries";
      Map<String, String> details = new HashMap<>();
      details.put("state", null);
      details.put("error_arg", "state");
      return new HandlerErrorBuilder(errorType, errorMessage, details).serialize();
    }

    // If no county was given, search for all of them in a given state.
    if (county == null) {
      county = "*";
    }

    try {
      Location location = new Location(state, county);
      // Get the response map from the cache.
      Map<String, Object> responseMap = this.cacher.get(location);
      return new DataSuccessResponse(responseMap).serialize();
    } catch (LocationNotFoundException e) {
      response.status(200);
      String errorType = "error_bad_location";
      String errorMessage = e.getMessage();
      Map<String, String> details = new HashMap<>();
      details.put("state", state);
      details.put("county", county);
      details.put("error_arg", e.getLocationType());
      return new HandlerErrorBuilder(errorType, errorMessage, details).serialize();
    } catch (URISyntaxException e) {
      response.status(200);
      String errorType = "error_bad_uri";
      String errorMessage = e.getMessage();
      Map<String, String> details = new HashMap<>();
      details.put("state", state);
      details.put("county", county);
      return new HandlerErrorBuilder(errorType, errorMessage, details).serialize();
    } catch (IOException e) {
      response.status(200);
      String errorType = "error_bad_json";
      String errorMessage = "The JSON result could not be retrieved";
      Map<String, String> details = new HashMap<>();
      details.put("state", state);
      details.put("county", county);
      return new HandlerErrorBuilder(errorType, errorMessage, details).serialize();
    } catch (InterruptedException e) {
      response.status(200);
      String errorType = "error_interrupted_thread";
      String errorMessage = "The thread was interrupted";
      Map<String, String> details = new HashMap<>();
      details.put("state", state);
      details.put("county", county);
      return new HandlerErrorBuilder(errorType, errorMessage, details).serialize();
    } catch (Throwable e) {
      response.status(200);
      String errorType = "error_caching";
      String errorMessage = "The data could not be cached";
      Map<String, String> details = new HashMap<>();
      details.put("state", state);
      details.put("county", county);
      return new HandlerErrorBuilder(errorType, errorMessage, details).serialize();
    }
  }
}
