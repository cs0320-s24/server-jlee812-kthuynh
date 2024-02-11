package edu.brown.cs.student.main.csv.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A parser for a CSV.
 *
 * @param <T> The type of object the parser turns rows into.
 */
public class CSVParser<T> {

  // Private variable declarations.
  private BufferedReader bufferedReader;
  private CreatorFromRow<T> creatorFromRow;
  private List<String> header;

  // The regex for splitting up rows.
  private static final Pattern regexSplitCSVRow =
      Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");

  /**
   * A constructor for the CSV Parser.
   *
   * @param reader The Reader that CSV Parser uses to read through the CSV.
   * @param creatorFromRow A class that implements CreatorFromRow to create a list out of rows.
   * @param hasHeader A boolean for whether the CSV contains a header.
   * @throws IOException An IOException in the case that an error occurs during reading.
   */
  public CSVParser(Reader reader, CreatorFromRow<T> creatorFromRow, boolean hasHeader)
      throws IOException {
    this.bufferedReader = new BufferedReader(reader);
    this.creatorFromRow = creatorFromRow;

    if (hasHeader) {
      this.header = Arrays.asList(regexSplitCSVRow.split(this.bufferedReader.readLine()));
      this.trimValues(this.header);
    } else {
      this.header = new ArrayList<>();
    }
  }

  /**
   * Parses the CSV.
   *
   * @return A list containing elements of type T.
   */
  public List<T> parseCSV() throws IOException, FactoryFailureException {
    List<T> stores = new ArrayList<>();

    // Calculates the expected number of columns.
    int expectedColumns = -1;
    if (!this.header.isEmpty()) {
      expectedColumns = this.header.size();
    }

    // Reads through the document.
    String line = this.bufferedReader.readLine();
    while (line != null) {
      List<String> parsedLine = Arrays.asList(regexSplitCSVRow.split(line));

      // Ensures that the CSV has a valid format.
      if (expectedColumns < 0) {
        // If there's no header, then the expected column number is based on the
        // first row.
        expectedColumns = parsedLine.size();
      } else {
        if (parsedLine.size() != expectedColumns) {
          // If the expected columns doesn't match what was expected, throw an
          // exception.
          throw new FactoryFailureException(
              "Row did not match expected length of [" + expectedColumns + "]: " + parsedLine,
              parsedLine);
        }
      }

      // Adds the object created to the store.
      this.trimValues(parsedLine);
      stores.add(this.creatorFromRow.create(parsedLine));
      line = this.bufferedReader.readLine();
    }

    // Returns the result.
    return stores;
  }

  /**
   * Gets the header
   *
   * @return A string array containing the values of the header.
   */
  public List<String> getHeader() {
    return this.header;
  }

  /**
   * A method that trims trailing spaces from the CSV, as the regex fails to account for spaces.
   *
   * @param row The row whose values are trimmed.
   */
  private void trimValues(List<String> row) {
    for (int i = 0; i < row.size(); i++) {
      row.set(i, row.get(i).trim());
    }
  }
}
