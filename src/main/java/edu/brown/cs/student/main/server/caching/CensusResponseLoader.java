package edu.brown.cs.student.main.server.caching;

import com.google.common.cache.CacheLoader;
import edu.brown.cs.student.main.server.censusHandler.CensusSource;
import edu.brown.cs.student.main.server.censusHandler.Location;
import edu.brown.cs.student.main.server.censusHandler.LocationNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CensusResponseLoader extends CacheLoader<Location, Map<String, Object>> {
  private final CensusSource source;

  public CensusResponseLoader(CensusSource source) {
    this.source = source;
  }

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
