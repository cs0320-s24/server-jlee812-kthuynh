package edu.brown.cs.student.main.server.csvEndpoints;

import edu.brown.cs.student.main.csv.parser.CSVParser;
import edu.brown.cs.student.main.csv.parser.FactoryFailureException;
import edu.brown.cs.student.main.csv.search.CSVSearcher;
import edu.brown.cs.student.main.csv.search.HeaderValueException;
import edu.brown.cs.student.main.csv.search.StringListFromRow;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** The data source that CSV handlers use. */
public class CSVSource {
  private CSVSearcher searcher;
  private List<String> header;
  private List<List<String>> data;
  private Boolean isLoaded = false;

  /**
   * Loads the CSV, parsing through it and assigning relevant variables.
   *
   * @param fileName The file being parsed.
   * @param hasHeader Whether the CSV file has a header.
   * @throws IOException Thrown when there is an error reading the file.
   * @throws FactoryFailureException Thrown when there is an error creating an object from the CSV
   *     rows.
   */
  public void loadData(String fileName, boolean hasHeader)
      throws IOException, FactoryFailureException {
    FileReader reader = new FileReader(fileName);
    CSVParser<List<String>> parser = new CSVParser<>(reader, new StringListFromRow(), hasHeader);
    this.header = parser.getHeader();
    this.data = parser.parseCSV();
    this.searcher = new CSVSearcher(this.header, this.data);
    this.isLoaded = true;
  }

  /**
   * Searches the CSV.
   *
   * @param value The value being searched for.
   * @param columnValue The column value being narrowed by.
   * @return A list of rows which contain the value.
   * @throws HeaderValueException Thrown when the header value being searched does not exist in the
   *     header.
   * @throws UnloadedCSVException Thrown when the CSV has not loaded.
   */
  public List<List<String>> search(String value, String columnValue)
      throws HeaderValueException, UnloadedCSVException {
    if (this.isLoaded) {
      return new ArrayList<>(this.searcher.search(value, columnValue));
    } else {
      throw new UnloadedCSVException("Searching requires a loaded CSV!");
    }
  }

  /**
   * Gets the header of the CSV.
   *
   * @return A list of strings representing each header value.
   */
  public List<String> getHeader() {
    if (this.isLoaded) {
      return new ArrayList<>(this.header);
    } else {
      return new ArrayList<>();
    }
  }

  /**
   * Gets the data of the CSV.
   *
   * @return A list of a list of strings representing the rows.
   * @throws UnloadedCSVException Thrown when the CSV has not been loaded.
   */
  public List<List<String>> getData() throws UnloadedCSVException {
    if (this.isLoaded) {
      return new ArrayList<>(this.data);
    } else {
      throw new UnloadedCSVException("Viewing requires a loaded CSV!");
    }
  }

  /** Setter method for testing handlers when there's no file, To reset CSV variables */
  public void setIsLoaded() {
    this.isLoaded = false;
  }
}
