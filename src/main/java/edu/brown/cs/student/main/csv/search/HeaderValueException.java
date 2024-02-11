package edu.brown.cs.student.main.csv.search;

/** An exception for if a value is not found in the header. */
public class HeaderValueException extends Exception {

  public HeaderValueException(String message) {
    super(message);
  }
}
