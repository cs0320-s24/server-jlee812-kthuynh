package edu.brown.cs.student.Tests.censusTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import edu.brown.cs.student.main.server.censusHandler.CensusResult;
import edu.brown.cs.student.main.server.censusHandler.CensusSource;
import edu.brown.cs.student.main.server.censusHandler.Location;
import edu.brown.cs.student.main.server.censusHandler.LocationNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Testing file for online Census API */
public class CensusSourceTest {

  /**
   * Separate testing suite for testing census API to not send to many requests to the census API.
   * Tests that the connection works, and that we are parsing data correctly from it.
   *
   * @throws IOException Thrown when there is trouble getting the JSON.
   * @throws URISyntaxException Thrown when the link was not valid.
   * @throws InterruptedException Thrown when the thread for connecting is interrupted.
   * @throws LocationNotFoundException Thrown when the location does not exist.
   */
  @Test
  public void testCensusAPICanLoad_REAL()
      throws IOException, URISyntaxException, InterruptedException, LocationNotFoundException {
    CensusSource source = new CensusSource();

    Location loc = new Location("massachusetts", "barnstable county");
    List<CensusResult> results = source.getBroadband(loc);

    assertNotNull(results);

    assertEquals("91.6", results.get(0).broadband());
    assertEquals("Barnstable County, Massachusetts", results.get(0).county());

    Location loc2 = new Location("california", "orange county");
    List<CensusResult> results2 = source.getBroadband(loc2);
    assertEquals("93.0", results2.get(0).broadband());
    assertEquals("Orange County, California", results2.get(0).county());
  }

  /** Tests to make sure the census source can catch locations that don't exist */
  @Test
  public void testCensusAPINonexistantData_REAL() {
    CensusSource source = new CensusSource();

    Location loc = new Location("massachusetts", "hello county");
    Assert.assertThrows(LocationNotFoundException.class, () -> source.getBroadband(loc));
  }
}
