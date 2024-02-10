package edu.brown.cs.student.Tests;

import edu.brown.cs.student.main.parser.CSVParser;
import edu.brown.cs.student.main.parser.FactoryFailureException;
import edu.brown.cs.student.main.user.search.CSVSearcher;
import edu.brown.cs.student.main.user.search.HeaderValueException;
import edu.brown.cs.student.main.user.search.StringListFromRow;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

/** Tests for the CSV Searcher. */
public class CSVSearcherTest {

  /** Tests for searching when a desired value is in the CSV, and not specifying a column. */
  @Test
  public void testSearchValueInCSV()
      throws IOException, FactoryFailureException, HeaderValueException {
    FileReader fileReader = new FileReader("data/tests/withHeader.csv");
    CSVParser csvParser = new CSVParser<>(fileReader, new StringListFromRow(), true);
    CSVSearcher csvSearcher = new CSVSearcher(csvParser);
    List<List<String>> result = csvSearcher.search("1", null);

    List<List<String>> expectedResult = new ArrayList<>();
    expectedResult.add(new ArrayList<>());
    expectedResult.get(0).add("1");
    expectedResult.get(0).add("2");
    expectedResult.get(0).add("3");

    Assert.assertEquals(result, expectedResult);
  }

  /** Tests for searching when a desired value has spaces and is in the CSV */
  @Test
  public void testSearchValueHasSpacesInCSV()
      throws IOException, FactoryFailureException, HeaderValueException {
    StringReader stringReader = new StringReader("Num1,Num2,Num3\n" + "Number 1,2,3\n" + "4,1,6");
    CSVParser csvParser = new CSVParser<>(stringReader, new StringListFromRow(), true);
    CSVSearcher csvSearcher = new CSVSearcher(csvParser);
    List<List<String>> result = csvSearcher.search("Number 1", null);

    List<List<String>> expectedResult = new ArrayList<>();
    expectedResult.add(new ArrayList<>());
    expectedResult.get(0).add("Number 1");
    expectedResult.get(0).add("2");
    expectedResult.get(0).add("3");

    Assert.assertEquals(result, expectedResult);
  }

  /** Tests for searching when a desired value has commas and is in the CSV */
  @Test
  public void testSearchValueHasCommasInCSV()
      throws IOException, FactoryFailureException, HeaderValueException {
    StringReader stringReader = new StringReader("Num1,Num2,Num3\n" + "\"1,4\",2,3\n" + "4,1,6");
    CSVParser csvParser = new CSVParser<>(stringReader, new StringListFromRow(), true);
    CSVSearcher csvSearcher = new CSVSearcher(csvParser);
    List<List<String>> result = csvSearcher.search("\"1,4\"", null);

    List<List<String>> expectedResult = new ArrayList<>();
    expectedResult.add(new ArrayList<>());
    expectedResult.get(0).add("\"1,4\"");
    expectedResult.get(0).add("2");
    expectedResult.get(0).add("3");

    Assert.assertEquals(result, expectedResult);
  }

  /** Tests for searching when a desired value is not in the CSV, and not specifying a column. */
  @Test
  public void testSearchValueNotInCSV()
      throws IOException, FactoryFailureException, HeaderValueException {
    FileReader fileReader = new FileReader("data/tests/withHeader.csv");
    CSVParser csvParser = new CSVParser<>(fileReader, new StringListFromRow(), true);
    CSVSearcher csvSearcher = new CSVSearcher(csvParser);
    List<List<String>> result = csvSearcher.search("10", null);
    List<List<String>> expectedResult = new ArrayList<>();

    Assert.assertEquals(result, expectedResult);
  }

  /** Tests for searching when a desired value is in the CSV, and searching by header value. */
  @Test
  public void testSearchValueInCSVWrongColumnBy()
      throws IOException, FactoryFailureException, HeaderValueException {
    StringReader stringReader = new StringReader("Num1,Num2,Num3\n" + "1,2,3\n" + "4,1,6");
    CSVParser csvParser = new CSVParser<>(stringReader, new StringListFromRow(), true);
    CSVSearcher csvSearcher = new CSVSearcher(csvParser);
    List<List<String>> result = csvSearcher.search("1", "Num2");
    List<List<String>> expectedResult = new ArrayList<>();
    expectedResult.add(new ArrayList<>());
    expectedResult.get(0).add("4");
    expectedResult.get(0).add("1");
    expectedResult.get(0).add("6");

    // Should not have the row ["1", "2", "3"]!
    Assert.assertEquals(result.size(), 1);
    Assert.assertEquals(result, expectedResult);
  }

  /** Tests for searching when a desired value is in the CSV, and searching by column index. */
  @Test
  public void testSearchValueInCSVByColumnIndex()
      throws IOException, FactoryFailureException, HeaderValueException {
    StringReader stringReader = new StringReader("Num1,Num2,Num3\n" + "1,2,3\n" + "4,1,6");
    CSVParser csvParser = new CSVParser<>(stringReader, new StringListFromRow(), true);
    CSVSearcher csvSearcher = new CSVSearcher(csvParser);
    List<List<String>> result = csvSearcher.search("1", "1");
    List<List<String>> expectedResult = new ArrayList<>();
    expectedResult.add(new ArrayList<>());
    expectedResult.get(0).add("4");
    expectedResult.get(0).add("1");
    expectedResult.get(0).add("6");

    // Should not have the row ["1", "2", "3"]!
    Assert.assertEquals(result.size(), 1);
    Assert.assertEquals(result, expectedResult);
  }

  /**
   * Tests for searching when a desired value is in the CSV, and searching by header value that is
   * not in the header.
   */
  @Test
  public void testSearchValueInCSVByColumnNotInHeader()
      throws IOException, FactoryFailureException, HeaderValueException {
    StringReader stringReader = new StringReader("Num1,Num2,Num3\n" + "1,2,3\n" + "4,1,6");
    CSVParser csvParser = new CSVParser<>(stringReader, new StringListFromRow(), true);
    CSVSearcher csvSearcher = new CSVSearcher(csvParser);

    Assert.assertThrows(HeaderValueException.class, () -> csvSearcher.search("1", "Num4"));
  }

  /** Tests for searching when a desired value is in the CSV, and not searching by column. */
  @Test
  public void testSearchValueInCSVNoColumnNarrow()
      throws IOException, FactoryFailureException, HeaderValueException {
    StringReader stringReader = new StringReader("Num1,Num2,Num3\n" + "1,2,3\n" + "4,1,6");
    CSVParser csvParser = new CSVParser<>(stringReader, new StringListFromRow(), true);
    CSVSearcher csvSearcher = new CSVSearcher(csvParser);
    List<List<String>> result = csvSearcher.search("1", null);
    List<List<String>> expectedResult = new ArrayList<>();
    expectedResult.add(new ArrayList<>());
    expectedResult.add(new ArrayList<>());
    expectedResult.get(0).add("1");
    expectedResult.get(0).add("2");
    expectedResult.get(0).add("3");
    expectedResult.get(1).add("4");
    expectedResult.get(1).add("1");
    expectedResult.get(1).add("6");

    // Should have the row ["1", "2", "3"]!
    Assert.assertEquals(result.size(), 2);
    Assert.assertEquals(result, expectedResult);
  }

  /**
   * Tests for searching when a desired value is in the CSV, but the column that is searched by
   * exceeds the dimensions of the CSV.
   */
  @Test
  public void testSearchValueInCSVColumnExceedsRowLength()
      throws IOException, FactoryFailureException {
    StringReader stringReader = new StringReader("Num1,Num2,Num3\n" + "1,2,3\n" + "4,1,6");
    CSVParser csvParser = new CSVParser<>(stringReader, new StringListFromRow(), true);
    CSVSearcher csvSearcher = new CSVSearcher(csvParser);

    Assert.assertThrows(IndexOutOfBoundsException.class, () -> csvSearcher.search("1", "9"));
  }

  /** Tests for searching when a desired value is in the CSV, but not in the right column. */
  @Test
  public void testSearchValueInCSVNotRightColumn()
      throws IOException, FactoryFailureException, HeaderValueException {
    StringReader stringReader = new StringReader("Num1,Num2,Num3\n" + "1,2,3\n" + "4,5,6");
    CSVParser csvParser = new CSVParser<>(stringReader, new StringListFromRow(), true);
    CSVSearcher csvSearcher = new CSVSearcher(csvParser);
    List<List<String>> result = csvSearcher.search("1", "2");
    List<List<String>> expectedResult = new ArrayList<>();

    // Should not have the row ["1", "2", "3"]!
    Assert.assertEquals(result.size(), 0);
    Assert.assertEquals(result, expectedResult);
  }
}
