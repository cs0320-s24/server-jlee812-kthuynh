package edu.brown.cs.student.Tests.censusTests;

import edu.brown.cs.student.main.server.censusHandler.CensusResult;
import edu.brown.cs.student.main.server.censusHandler.CensusSourceInterface;
import edu.brown.cs.student.main.server.censusHandler.Location;
import edu.brown.cs.student.main.server.censusHandler.LocationNotFoundException;
import java.util.ArrayList;
import java.util.List;

/** A mocked census source to avoid querying the API. */
public class MockedCensusSource implements CensusSourceInterface {

  private final List<CensusResult> data;

  public MockedCensusSource(List<CensusResult> data) {
    this.data = data;
  }

  /**
   * A method to help emulate when a state does not exist.
   *
   * @param desiredState The desired state.
   * @return A boolean for if it exists.
   */
  private Boolean stateExistsInData(String desiredState) {
    for (CensusResult censusResult : this.data) {
      String[] county_and_state = censusResult.county().split(", ");
      String state = county_and_state[1];
      if (state.equalsIgnoreCase(desiredState)) {
        return true;
      }
    }
    return false;
  }

  /**
   * A method to help emulate when a county does not exist.
   *
   * @param desiredCounty The desired county.
   * @return A boolean for if it exists.
   */
  private Boolean countyExistsInData(String desiredCounty) {
    for (CensusResult censusResult : this.data) {
      String[] county_and_state = censusResult.county().split(", ");
      String county = county_and_state[0];
      if (county.equalsIgnoreCase(desiredCounty)) {
        return true;
      }
    }
    return false;
  }

  /**
   * The public method that the handler calls to get broadband results.
   *
   * @param location The location whose broadband usage is being list for.
   * @return A list of the census results.
   */
  @Override
  public List<CensusResult> getBroadband(Location location) throws LocationNotFoundException {
    if (!stateExistsInData(location.state())) {
      throw new LocationNotFoundException("State not found: " + location.state(), "state");
    }

    if (!countyExistsInData(location.county()) && !location.county().equalsIgnoreCase("*")) {
      throw new LocationNotFoundException("County not found: " + location.county(), "county");
    }

    List<CensusResult> results = new ArrayList<>();
    for (CensusResult censusResult : this.data) {
      String[] county_and_state = censusResult.county().split(", ");
      String county = county_and_state[0];
      String state = county_and_state[1];
      if (state.equalsIgnoreCase(location.state())) {
        if (county.equalsIgnoreCase(location.county()) || location.county().equalsIgnoreCase("*")) {
          results.add(censusResult);
        }
      }
    }
    return new ArrayList<>(results);
  }
}
