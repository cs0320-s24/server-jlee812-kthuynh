package edu.brown.cs.student.main.user.search;

import edu.brown.cs.student.main.parser.FactoryFailureException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** A searcher for the CSV */
public class CSVSearcher {
  private List<List<String>> data;
  private List<String> header;

  /**
   * The constructor for the CSV Searcher
   *
   * @param header
   * @param data
   * @throws IOException Throws an IOException if something goes wrong reading the file.
   * @throws FactoryFailureException Throws a FactoryFailureException if there is trouble processing
   *     data
   */
  public CSVSearcher(List<String> header, List<List<String>> data)
      throws IOException, FactoryFailureException {
    this.header = header;
    this.data = data;
  }

  /**
   * Searches through the data for matching values.
   *
   * @param value The desired value being searched for.
   * @param columnValue The column condition which the search is narrowed on.
   * @return A list of rows that match the value.
   * @throws IndexOutOfBoundsException Thrown if the searched column value exceeds the CSV's
   *     dimensions.
   */
  public List<List<String>> search(String value, String columnValue)
      throws IndexOutOfBoundsException, HeaderValueException {
    List<List<String>> result = new ArrayList<>();
    // Calculates what to narrow the search by.
    int columnIndex = this.calculateColumnIndex(columnValue);

    // Searches through the rows of data.
    for (List<String> row : this.data) {
      if (this.hasValue(row, value, columnIndex)) {
        result.add(row);
      }
    }

    return result;
  }

  /**
   * Checks if a row contains a value.
   *
   * @param row The row being searched through.
   * @param value The value being searched for.
   * @param columnIndex The column index to narrow on.
   * @return A boolean for if the row contains the value.
   */
  private boolean hasValue(List<String> row, String value, int columnIndex) {
    // If there is a condition to narrow by, looks at this specific column.
    if (columnIndex > 0) {
      try {
        return row.get(columnIndex).equalsIgnoreCase(value);
      } catch (IndexOutOfBoundsException e) {
        throw new IndexOutOfBoundsException("Column value exceeded row length at row: " + row);
      }
    }
    // Otherwise, search through every column of the row.
    else {
      for (String columnValue : row) {
        if (columnValue.equalsIgnoreCase(value)) {
          return true;
        }
      }
      return false;
    }
  }

  /**
   * Calculates the column index to narrow by.
   *
   * @param columnValue What to narrow the search by.
   * @return
   */
  private int calculateColumnIndex(String columnValue) throws HeaderValueException {
    // First, see if the value is an integer.
    try {
      return Integer.parseInt(columnValue);
    }
    // If the value is not an integer, search for the header column that this
    // value applies to.
    catch (NumberFormatException e) {
      // Searches if there is a header, otherwise, default to searching by every column.
      if (!this.header.isEmpty() && columnValue != null) {
        for (String column : this.header) {
          if (column.equalsIgnoreCase(columnValue)) {
            return this.header.indexOf(column);
          }
        }
        // If the value has not been found in the header, throw an error.
        throw new HeaderValueException(columnValue + " not found in header!");
      }
    }
    return -1;
  }
}
