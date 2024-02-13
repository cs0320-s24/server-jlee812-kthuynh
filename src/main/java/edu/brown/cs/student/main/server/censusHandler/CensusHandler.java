package edu.brown.cs.student.main.server.censusHandler;

import edu.brown.cs.student.main.server.caching.CacheControl;
import edu.brown.cs.student.main.server.datasource.DataSuccessResponse;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class CensusHandler implements Route {
  private final CensusSource source;
  private CacheControl cacher;

  public CensusHandler(CensusSource source) {
    this.source = source;
    this.cacher = new CacheControl(this.source);
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String state = request.queryParams("state");
    String county = request.queryParams("county");

    Location location = new Location(state, county);
    Map<String, Object> responseMap = this.cacher.get(location);
    //    Map<String, Object> responseMap = new HashMap<>();
    //    responseMap.put("time", LocalTime.now().toString());
    //    responseMap.put("state", state);
    //    responseMap.put("county", county);
    //    responseMap.put("data", this.source.getBroadband(location));
    return new DataSuccessResponse(responseMap).serialize();
  }
}
