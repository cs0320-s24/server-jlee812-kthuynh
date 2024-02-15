package edu.brown.cs.student.main.server.censusHandler;

public class LocationNotFoundException extends Exception {
  private final String locationType;

  public LocationNotFoundException(String message, String location) {
    super(message);
    this.locationType = location;
  }

  public String getLocationType() {
    return this.locationType;
  }
}
