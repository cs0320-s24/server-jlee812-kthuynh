package edu.brown.cs.student.main.server.censusHandler;

/**
 * A record for the encoded location.
 *
 * @param state The state code.
 * @param county The county code.
 */
public record EncodedLocation(String state, String county) {

  /**
   * Gets the parameter format for the county and state.
   *
   * @return The parameter format.
   */
  public String getJSONParams() {
    return "for=county:" + this.county + "&in=state:" + this.state;
  }
}
