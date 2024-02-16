package edu.brown.cs.student.main.server.censusHandler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/** An interface for the census source. */
public interface CensusSourceInterface {

  /**
   * The public method that the handler calls to get broadband results.
   *
   * @param location The location whose broadband usage is being list for.
   * @return A list of the census results.
   * @throws IOException               Thrown when there is trouble getting the JSON.
   * @throws URISyntaxException        Thrown when the link was not valid.
   * @throws InterruptedException      Thrown when the thread for connecting is interrupted.
   * @throws LocationNotFoundException Thrown when the location does not exist.
   */
  List<CensusResult> getBroadband(Location location) throws IOException, URISyntaxException, InterruptedException, LocationNotFoundException;
}

