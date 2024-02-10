package edu.brown.cs.student.main.server.csvHandlers;

import edu.brown.cs.student.main.user.search.CSVSearcher;
import java.util.List;

public class CSVCreator {
  private CSVSearcher searcher;
  private List<String> header;
  private List<List<String>> data;
  private Boolean isLoaded;

  public CSVCreator() {
    //new searcher
  }

  public void isLoaded() {
    this.isLoaded = true;
  }

  public List<String> getHeader() {
    return this.header;
  }

  public void setHeader(List<String> newHeader) {
    this.header = newHeader;
  }

  public void setData(List<List<String>> newData) {
    this.data = newData;
  }

  public List<List<String>> getData(){
    return this.data;
  }

  public void search() {
    //this.searcher.search();
  }

}
