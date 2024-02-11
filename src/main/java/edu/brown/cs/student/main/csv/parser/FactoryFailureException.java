package edu.brown.cs.student.main.csv.parser;

import java.util.List;

/**
 * This is an error provided to catch any error that may occur when you create an object from a row.
 * Feel free to expand or supplement or use it for other purposes.
 */
public class FactoryFailureException extends Exception {
  private final List<String> row;

  public FactoryFailureException(String message, List<String> row) {
    super(message);
    this.row = row;
  }

  public List<String> getRow() {
    return this.row;
  }
}
