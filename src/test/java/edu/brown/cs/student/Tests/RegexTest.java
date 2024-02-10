package edu.brown.cs.student.Tests;

import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

/** Tests for the regex. */
public class RegexTest {

  private static final Pattern regexSplitCSVRow =
      Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");

  /** Tests splitting regex on a normal CSV row. */
  @Test
  public void testRegexNormalCSVRow() {
    String string = "1,2,3";
    String[] expectedResult = new String[3];
    expectedResult[0] = "1";
    expectedResult[1] = "2";
    expectedResult[2] = "3";
    Assert.assertEquals(expectedResult, regexSplitCSVRow.split(string));
  }

  /** Tests splitting regex behavior with spaces. */
  @Test
  public void testRegexWhitespace() {
    String string = "1 ,2 ,3 ";
    String[] expectedResult = new String[3];
    expectedResult[0] = "1 ";
    expectedResult[1] = "2 ";
    expectedResult[2] = "3 ";

    // Notice how it leaves in trailing spaces. Therefore, SPACES MATTER when parsing a CSV
    Assert.assertEquals(expectedResult, regexSplitCSVRow.split(string));

    // This becomes problematic when parsing something like " $1,000 ".
    String stringProblematic = "\" $1,000 \"";
    String[] expectedResultProblematic = new String[1];
    expectedResultProblematic[0] = "\" $1,000 \"";

    // Spaces are left exactly as they are.
    Assert.assertEquals(expectedResultProblematic, regexSplitCSVRow.split(stringProblematic));

    // Thus, to fix this, I trim each trailing space in a CSV column to make it as friendly
    // as possible to parse.
  }

  /** Tests splitting regex behavior with empty columns. */
  @Test
  public void testRegexEmptyColumn() {
    String string = "1,,3";
    String[] expectedResult = new String[3];
    expectedResult[0] = "1";
    expectedResult[1] = "";
    expectedResult[2] = "3";

    // Makes sure that empty characters remain in array.
    Assert.assertEquals(expectedResult, regexSplitCSVRow.split(string));
  }

  /** Tests splitting regex behavior when a phrase has a space */
  @Test
  public void testWordsWithSpaces() {
    String string = "1,\"2 3\"";
    String[] expectedResult = new String[2];
    expectedResult[0] = "1";
    expectedResult[1] = "\"2 3\"";

    Assert.assertEquals(expectedResult, regexSplitCSVRow.split(string));
  }

  /** Tests splitting regex behavior when a phrase has a comma */
  @Test
  public void testWordsWithCommas() {
    String string = "1,\"2, 3\"";
    String[] expectedResult = new String[2];
    expectedResult[0] = "1";
    expectedResult[1] = "\"2, 3\"";

    Assert.assertEquals(expectedResult, regexSplitCSVRow.split(string));
  }

  /** Tests splitting regex behavior when a phrase has a backslash */
  @Test
  public void testWordsWithBackslash() {
    String string = "1 \2,3";
    String[] expectedResult = new String[2];
    expectedResult[0] = "1 \2";
    expectedResult[1] = "3";

    Assert.assertEquals(expectedResult, regexSplitCSVRow.split(string));
  }
}
