package edu.brown.cs.student.main.server.censusHandler;

/** An exception for when a location could not be found in the census data. */
public class LocationNotFoundException extends Exception {
  private final String locationType;

  /**
   * The constructor for the exception.
   *
   * @param message The message.
   * @param locationType The type of location that could not be found.
   */
  public LocationNotFoundException(String message, String locationType) {
    super(message);
    this.locationType = locationType;
  }

  public String getLocationType() {
    return this.locationType;
  }
}
