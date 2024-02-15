package edu.brown.cs.student.main.server.caching;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * A class that handles caching. To change/control how fast and when it caches, you can edit the
 * final variables maxsize and duration.
 */
public class CacheControl<K, V> {
  private final LoadingCache<K, V> graphs;
  private final CacheLoader<K, V> cacheLoader;
  private final boolean useCache;

  /**
   * The constructor for the cache control.
   *
   * @param cacheLoader The loading cache.
   * @param useCache A boolean for whether to use the cache.
   * @param maxSize The max size of the cache.
   * @param durationMinutes The length to keep the cache.
   */
  public CacheControl(
      CacheLoader<K, V> cacheLoader, boolean useCache, int maxSize, int durationMinutes) {
    CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
    this.cacheLoader = cacheLoader;
    this.useCache = useCache;

    if (durationMinutes >= 0) {
      builder.expireAfterWrite(durationMinutes, TimeUnit.MINUTES);
    }

    this.graphs = builder.maximumSize(maxSize).build(this.cacheLoader);
  }

  /**
   * Proxy method for Guava's cache get method.
   *
   * @return The value from a given key.
   */
  public V get(K key) throws Throwable {
    try {
      if (this.useCache) {
        return this.graphs.get(key);
      } else {
        return this.cacheLoader.load(key);
      }
    } catch (ExecutionException e) {
      throw (e.getCause());
    }
  }
}
