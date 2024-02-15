package edu.brown.cs.student.main.server.caching;

import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * A class that handles caching. To change/control how fast and when it caches, you can edit the
 * final variables maxsize and duration.
 */
public class CacheControl {
  private final LoadingCache<Location, Map<String, Object>> graphs;
  private final int maxsize = 500;
  private final int duration = 5;

  public CacheControl(CensusSource source) {
    this.graphs =
        CacheBuilder.newBuilder()
            .maximumSize(this.maxsize)
            .expireAfterWrite(this.duration, TimeUnit.MINUTES)
            .build(
                new CacheLoader<>() {
                  @Override
                  public Map<String, Object> load(Location key) throws Exception {
                    Map<String, Object> responseMap = new HashMap<>();
                    responseMap.put("time", LocalTime.now().toString());
                    responseMap.put("date", LocalDate.now().toString());
                    responseMap.put("state", key.state());
                    responseMap.put("county", key.county());
                    responseMap.put("data", source.getBroadband(key));
                    return responseMap;
                  }
                });
  }

  /**
   * Proxy method for Guava's cache get method
   *
   * @return
   */
  public Map<String, Object> get(Location location)
      throws LocationNotFoundException, URISyntaxException, IOException, InterruptedException {
    try {
      return Collections.unmodifiableMap(this.graphs.get(location));
    } catch (ExecutionException e) {
      Throwables.propagateIfPossible(e.getCause(), LocationNotFoundException.class);
      Throwables.propagateIfPossible(e.getCause(), URISyntaxException.class);
      Throwables.propagateIfPossible(e.getCause(), IOException.class);
      Throwables.propagateIfPossible(e.getCause(), InterruptedException.class);
      throw new IllegalStateException("Error during caching process!");
    }
  }
}
