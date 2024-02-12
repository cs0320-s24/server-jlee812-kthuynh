package edu.brown.cs.student.main.server.censusHandler;

import spark.Request;
import spark.Response;
import spark.Route;

public class CensusHandler implements Route {
  private final CensusSource source;

  public CensusHandler(CensusSource source) {
    this.source = source;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String state = request.queryParams("state");
    String county = request.queryParams("county");

    Location location = new Location(state, county);
    String stateCode = this.source.getStateCode(location.state());
    String countyCode = this.source.getCountyCode(stateCode, location.county());
    return stateCode + " " + countyCode;
    // System.out.println(this.source.resolveAreaCodes(location.state(), location.county()));
    // return "Success!";
  }
}
