package edu.brown.cs.student.main.server.datasource;

import edu.brown.cs.student.main.csv.parser.CSVParser;
import edu.brown.cs.student.main.csv.parser.FactoryFailureException;
import edu.brown.cs.student.main.csv.search.CSVSearcher;
import edu.brown.cs.student.main.csv.search.HeaderValueException;
import edu.brown.cs.student.main.csv.search.StringListFromRow;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVSource {
  private CSVSearcher searcher;
  private List<String> header;
  private List<List<String>> data;
  private Boolean isLoaded;

  public List<String> getHeader() {
    return new ArrayList<>(this.header);
  }

  public void loadData(String fileName, boolean hasHeader)
      throws IOException, FactoryFailureException {
    FileReader reader = new FileReader(fileName);
    CSVParser<List<String>> parser = new CSVParser<>(reader, new StringListFromRow(), hasHeader);
    this.header = parser.getHeader();
    this.data = parser.parseCSV();
    this.searcher = new CSVSearcher(this.header, this.data);
    this.isLoaded = true;
  }

  public List<List<String>> getData() {
    return new ArrayList<>(this.data);
  }

  public List<List<String>> search(String value, String columnValue) throws HeaderValueException {
    if (this.isLoaded) {
      return this.searcher.search(value, columnValue);
    } else {
      // throw error
    }

    return null;
  }
}
