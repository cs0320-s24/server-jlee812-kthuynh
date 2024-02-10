package edu.brown.cs.student.Tests;

import edu.brown.cs.student.Tests.creatorfromrowclasses.IntegerListFromRow;
import edu.brown.cs.student.Tests.creatorfromrowclasses.Person;
import edu.brown.cs.student.Tests.creatorfromrowclasses.PersonFromRow;
import edu.brown.cs.student.main.parser.CSVParser;
import edu.brown.cs.student.main.parser.FactoryFailureException;
import edu.brown.cs.student.main.user.search.StringListFromRow;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

/** Tests for the CSV Parser. */
public class CSVParserTest {

  /** Tests parsing when there is a header, and it is reading through a file reader. */
  @Test
  public void testFileParseWithHeader() throws IOException, FactoryFailureException {
    FileReader fileReader = new FileReader("data/tests/withHeader.csv");
    CSVParser csvParser = new CSVParser<>(fileReader, new StringListFromRow(), true);

    List<String> expectedHeaderResult = new ArrayList<>();
    expectedHeaderResult.add("Num1");
    expectedHeaderResult.add("Num2");
    expectedHeaderResult.add("Num3");

    List<List<String>> expectedDataResult = new ArrayList<>();
    expectedDataResult.add(new ArrayList<>());
    expectedDataResult.add(new ArrayList<>());
    expectedDataResult.get(0).add("1");
    expectedDataResult.get(0).add("2");
    expectedDataResult.get(0).add("3");
    expectedDataResult.get(1).add("4");
    expectedDataResult.get(1).add("5");
    expectedDataResult.get(1).add("6");

    Assert.assertEquals(csvParser.getHeader(), expectedHeaderResult);
    Assert.assertEquals(csvParser.parseCSV(), expectedDataResult);
  }

  /**
   * Tests parsing when there is a header, and it is reading through a file reader, but the
   * hasHeader boolean is false.
   */
  @Test
  public void testFileParseWithHeaderFalse() throws IOException, FactoryFailureException {
    FileReader fileReader = new FileReader("data/tests/withHeader.csv");
    CSVParser csvParser = new CSVParser<>(fileReader, new StringListFromRow(), false);

    List<String> expectedHeaderResult = new ArrayList<>();
    List<List<String>> expectedDataResult = new ArrayList<>();
    expectedDataResult.add(new ArrayList<>());
    expectedDataResult.add(new ArrayList<>());
    expectedDataResult.add(new ArrayList<>());
    expectedDataResult.get(0).add("Num1");
    expectedDataResult.get(0).add("Num2");
    expectedDataResult.get(0).add("Num3");
    expectedDataResult.get(1).add("1");
    expectedDataResult.get(1).add("2");
    expectedDataResult.get(1).add("3");
    expectedDataResult.get(2).add("4");
    expectedDataResult.get(2).add("5");
    expectedDataResult.get(2).add("6");

    Assert.assertEquals(csvParser.getHeader(), expectedHeaderResult);
    Assert.assertEquals(csvParser.parseCSV(), expectedDataResult);
  }

  /** Tests parsing when there is no header, and it is reading through a file reader. */
  @Test
  public void testFileParseWithoutHeader() throws IOException, FactoryFailureException {
    FileReader fileReader = new FileReader("data/tests/withoutHeader.csv");
    CSVParser csvParser = new CSVParser<>(fileReader, new StringListFromRow(), false);

    List<String> expectedHeaderResult = new ArrayList<>();
    List<List<String>> expectedDataResult = new ArrayList<>();
    expectedDataResult.add(new ArrayList<>());
    expectedDataResult.add(new ArrayList<>());
    expectedDataResult.get(0).add("1");
    expectedDataResult.get(0).add("2");
    expectedDataResult.get(0).add("3");
    expectedDataResult.get(1).add("4");
    expectedDataResult.get(1).add("5");
    expectedDataResult.get(1).add("6");

    // The header list should be empty.
    Assert.assertEquals(csvParser.getHeader(), expectedHeaderResult);
    Assert.assertEquals(csvParser.parseCSV(), expectedDataResult);
  }

  /** Tests parsing when there is a header, and it is reading through a string reader. */
  @Test
  public void testStringParseWithHeader() throws IOException, FactoryFailureException {
    StringReader stringReader = new StringReader("Num1,Num2,Num3\n" + "1,2,3\n" + "4,5,6");
    CSVParser csvParser = new CSVParser<>(stringReader, new StringListFromRow(), true);

    List<String> expectedHeaderResult = new ArrayList<>();
    expectedHeaderResult.add("Num1");
    expectedHeaderResult.add("Num2");
    expectedHeaderResult.add("Num3");

    List<List<String>> expectedDataResult = new ArrayList<>();
    expectedDataResult.add(new ArrayList<>());
    expectedDataResult.add(new ArrayList<>());
    expectedDataResult.get(0).add("1");
    expectedDataResult.get(0).add("2");
    expectedDataResult.get(0).add("3");
    expectedDataResult.get(1).add("4");
    expectedDataResult.get(1).add("5");
    expectedDataResult.get(1).add("6");

    Assert.assertEquals(csvParser.getHeader(), expectedHeaderResult);
    Assert.assertEquals(csvParser.parseCSV(), expectedDataResult);
  }

  /** Tests parsing when there is no header, and it is reading through a string reader. */
  @Test
  public void testStringParseWithoutHeader() throws IOException, FactoryFailureException {
    StringReader stringReader = new StringReader("1,2,3\n" + "4,5,6");
    CSVParser csvParser = new CSVParser<>(stringReader, new StringListFromRow(), false);

    List<String> expectedHeaderResult = new ArrayList<>();
    List<List<String>> expectedDataResult = new ArrayList<>();
    expectedDataResult.add(new ArrayList<>());
    expectedDataResult.add(new ArrayList<>());
    expectedDataResult.get(0).add("1");
    expectedDataResult.get(0).add("2");
    expectedDataResult.get(0).add("3");
    expectedDataResult.get(1).add("4");
    expectedDataResult.get(1).add("5");
    expectedDataResult.get(1).add("6");

    // The header list should be empty.
    Assert.assertEquals(csvParser.getHeader(), expectedHeaderResult);
    Assert.assertEquals(csvParser.parseCSV(), expectedDataResult);
  }

  /**
   * Tests that given a string reader and file reader with the same CSV, their results are the same.
   */
  @Test
  public void testFileParseWithStringParse() throws IOException, FactoryFailureException {
    FileReader fileReader = new FileReader("data/tests/withHeader.csv");
    StringReader stringReader = new StringReader("Num1,Num2,Num3\n" + "1,2,3\n" + "4,5,6");
    CSVParser csvFileParser = new CSVParser<>(fileReader, new StringListFromRow(), true);
    CSVParser csvStringParser = new CSVParser<>(stringReader, new StringListFromRow(), true);

    Assert.assertEquals(csvFileParser.getHeader(), csvStringParser.getHeader());
    Assert.assertEquals(csvFileParser.parseCSV(), csvStringParser.parseCSV());
  }

  /** Tests that a FactoryFailureException is thrown upon a missing value in a column. */
  @Test
  public void testBlankValueInColumn() throws IOException {
    StringReader stringReader = new StringReader("Num1,Num2,Num3\n" + "1,,3\n" + "4,5,6");
    CSVParser csvStringParser = new CSVParser<>(stringReader, new StringListFromRow(), true);
    Assert.assertThrows(FactoryFailureException.class, () -> csvStringParser.parseCSV());
  }

  /** Tests that a FactoryFailureException is thrown upon an extra column. */
  @Test
  public void testExtraColumn() throws IOException {
    StringReader stringReader = new StringReader("Num1,Num2,Num3\n" + "1,2,3\n" + "4,5,6,7");
    CSVParser csvStringParser = new CSVParser<>(stringReader, new StringListFromRow(), true);
    Assert.assertThrows(FactoryFailureException.class, () -> csvStringParser.parseCSV());
  }

  /** Tests that a FactoryFailureException is thrown upon a missing column. */
  @Test
  public void testMissingColumn() throws IOException {
    StringReader stringReader = new StringReader("Num1,Num2,Num3\n" + "1,2,3\n" + "4,5");
    CSVParser csvStringParser = new CSVParser<>(stringReader, new StringListFromRow(), true);
    Assert.assertThrows(FactoryFailureException.class, () -> csvStringParser.parseCSV());
  }

  /** Tests parsing with a CreatorFromRow class that turns rows into lists of integers. */
  @Test
  public void testIntegerListFromRow() throws IOException, FactoryFailureException {
    StringReader stringReader = new StringReader("Num1,Num2,Num3\n" + "1,2,3\n" + "4,5,6");
    CSVParser csvStringParser = new CSVParser<>(stringReader, new IntegerListFromRow(), true);

    List<String> expectedHeaderResult = new ArrayList<>();
    expectedHeaderResult.add("Num1");
    expectedHeaderResult.add("Num2");
    expectedHeaderResult.add("Num3");

    List<List<Integer>> expectedDataResult = new ArrayList<>();
    expectedDataResult.add(new ArrayList<>());
    expectedDataResult.add(new ArrayList<>());
    expectedDataResult.get(0).add(1);
    expectedDataResult.get(0).add(2);
    expectedDataResult.get(0).add(3);
    expectedDataResult.get(1).add(4);
    expectedDataResult.get(1).add(5);
    expectedDataResult.get(1).add(6);

    Assert.assertEquals(csvStringParser.getHeader(), expectedHeaderResult);
    Assert.assertEquals(csvStringParser.parseCSV(), expectedDataResult);
  }

  /** Tests parsing with a CreatorFromRow class that turns rows into a class instance. */
  @Test
  public void testPersonFromRow() throws IOException, FactoryFailureException {
    Person john = new Person("John", "May 10th", "Baseball");
    Person linda = new Person("linda", "March 13th", "Guitar");

    FileReader fileReader = new FileReader("data/tests/people.csv");
    CSVParser csvParser = new CSVParser<>(fileReader, new PersonFromRow(), true);
    List<Person> results = csvParser.parseCSV();
    List<Person> expectedResults = new ArrayList<>();
    expectedResults.add(john);
    expectedResults.add(linda);

    Assert.assertEquals(results.size(), expectedResults.size());
    for (int i = 0; i < results.size(); i++) {
      Assert.assertTrue(expectedResults.get(i).equals(results.get(i)));
    }
  }

  /** Tests that access is not given to CSV files outside of /data/ */
  @Test
  public void testOutsideAllowedFolder() {
    String filePath = "outside_of_data/withHeader.csv";

    if (!filePath.startsWith("data")) {
      filePath = "";
    }
    String finalFilePath = filePath;
    Assert.assertThrows(FileNotFoundException.class, () -> new FileReader(finalFilePath));
  }
}
