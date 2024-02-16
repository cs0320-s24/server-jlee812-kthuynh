package edu.brown.cs.student.main.server.censusHandler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface CensusSourceInterface {

  public List<CensusResult> getBroadband(Location location)
      throws IOException, URISyntaxException, InterruptedException, LocationNotFoundException ;

}
