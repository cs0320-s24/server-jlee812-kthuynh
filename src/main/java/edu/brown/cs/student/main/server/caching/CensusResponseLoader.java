package edu.brown.cs.student.main.server.caching;

import com.google.common.cache.CacheLoader;
import edu.brown.cs.student.main.server.censusHandler.CensusSourceInterface;
import edu.brown.cs.student.main.server.censusHandler.Location;
import edu.brown.cs.student.main.server.censusHandler.LocationNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** A cache loader for the response of the census. */
public class CensusResponseLoader extends CacheLoader<Location, Map<String, Object>> {
  private final CensusSourceInterface source;

  public CensusResponseLoader(CensusSourceInterface source) {
    this.source = source;
  }

  /**
   * The loading method for the cache loader, getting the data from the census datasource.
   *
   * @param key The location whose broadband usage is searched for.
   * @return A response map of relevant broadband usage information.
   * @throws IOException Thrown when there is trouble getting the JSON.
   * @throws URISyntaxException Thrown when the link was not valid.
   * @throws InterruptedException Thrown when the thread for connecting is interrupted.
   * @throws LocationNotFoundException Thrown when the location does not exist.
   */
  @Override
  public Map<String, Object> load(Location key)
      throws IOException, URISyntaxException, InterruptedException, LocationNotFoundException {
    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put("time", LocalTime.now().toString());
    responseMap.put("date", LocalDate.now().toString());
    responseMap.put("state", key.state());
    responseMap.put("county", key.county());
    responseMap.put("data", source.getBroadband(key));
    return Collections.unmodifiableMap(responseMap);
  }
}
