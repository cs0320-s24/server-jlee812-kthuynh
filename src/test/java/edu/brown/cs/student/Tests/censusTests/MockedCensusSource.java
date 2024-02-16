package edu.brown.cs.student.Tests.censusTests;

import edu.brown.cs.student.main.server.censusHandler.CensusResult;
import edu.brown.cs.student.main.server.censusHandler.CensusSourceInterface;
import edu.brown.cs.student.main.server.censusHandler.Location;
import edu.brown.cs.student.main.server.censusHandler.LocationNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class MockedCensusSource implements CensusSourceInterface {

  private List<CensusResult> data;

  public MockedCensusSource(List<CensusResult> data) {
    this.data = data;
  }

  @Override
  public List<CensusResult> getBroadband(Location location)
      throws IOException, URISyntaxException, InterruptedException, LocationNotFoundException {
    return this.data;
  }
}
