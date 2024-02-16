package edu.brown.cs.student.main.server.csvEndpoints;

/**
 * An exception for when the CSV has not been loaded for relevant handlers.
 */
public class UnloadedCSVException extends Exception {

  public UnloadedCSVException(String message) {
    super(message);
  }
}
