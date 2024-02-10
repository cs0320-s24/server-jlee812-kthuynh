package edu.brown.cs.student.main.user;

import edu.brown.cs.student.main.parser.CSVParser;
import edu.brown.cs.student.main.parser.FactoryFailureException;
import edu.brown.cs.student.main.user.search.CSVSearcher;
import edu.brown.cs.student.main.user.search.HeaderValueException;
import edu.brown.cs.student.main.user.search.StringListFromRow;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/** A class that launches a REPL, taking in input from the user. */
public class CSVREPL {

  // A regex for separating arguments.
  private static final Pattern regexSplitArgs = Pattern.compile(" (?=([^']*'[^']*')*(?![^']*'))");

  /** A constructor for the CSV Utility class that launches the REPL. */
  public CSVREPL() {
    this.launchREPL();
  }

  /** A method that launches the REPL. */
  private void launchREPL() {
    Scanner scanner = new Scanner(System.in);
    System.out.println(
        "Welcome to the CSV Searcher REPL! Type 'help' for usage info and 'exit' to quit!");
    while (true) {
      // Gets the user input and stores it as an array of arguments.
      System.out.print("> ");
      String input = scanner.nextLine().trim();
      String[] args = regexSplitArgs.split(input);

      // Handles the possible commands.
      if (input.equalsIgnoreCase("exit")) {
        break;
      } else if (input.equalsIgnoreCase("help")) {
        System.out.println(
            "The REPL search function takes in 3 to 4 arguments:\n"
                + "-CSV File Path (MUST BE IN DATA FOLDER): <file path>\n"
                + "-Value (USE '' TO DENOTE MULTI-WORDED PHRASES): "
                + "'<The value being searched for>'\n"
                + "-Column Restriction (!OPTIONAL!): "
                + "<Column index (0-indexed in integer form) / Column header>\n"
                + "-CSV Header: <true / false>");
      } else {
        // If the arguments are in the right form, search the CSV.
        if (validArgs(args)) {
          search(args);
        }
      }
    }

    scanner.close();
  }

  /**
   * A method that instantiates a CSV Parser and CSV Searcher to look for desired values.
   *
   * @param args The arguments passed into the parser and searcher.
   */
  private void search(String[] args) {
    // Checks the file path, and if the file is not in the data folder, clear the file path.
    String filePath = args[0];
    if (!filePath.startsWith("data")) {
      System.err.println("ERROR: Cannot access files outside of data folder! Clearing input!");
      filePath = "";
    }

    // Pulls values and arguments from the rest of the array.
    String value = args[1].replaceAll("^'|'$", "");
    String columnValue = null;
    boolean hasHeader;
    if (args.length == 3) {
      hasHeader = Boolean.parseBoolean(args[2]);
    } else if (args.length == 4) {
      columnValue = args[2];
      hasHeader = Boolean.parseBoolean(args[3]);
    } else {
      System.err.println("ERROR: HasHeader is not a valid boolean!");
      return;
    }

    // Creates instances of Parser and Searcher to look through the CSV.
    try (FileReader fileReader = new FileReader(filePath)) {
      CSVParser<List<String>> csvParser =
          new CSVParser<>(fileReader, new StringListFromRow(), hasHeader);
      CSVSearcher csvSearcher = new CSVSearcher(csvParser);

      // Calls the searcher to search, and return the result.
      List<List<String>> results = csvSearcher.search(value, columnValue);

      // Prints out the results.
      if (results.isEmpty()) {
        System.out.println("No rows were found containing the value: " + value);
      } else {
        for (List<String> row : results) {
          System.out.println(row);
        }
      }
      // Catches errors and prints them to the terminal.
    } catch (IOException
        | FactoryFailureException
        | HeaderValueException
        | IndexOutOfBoundsException e) {
      System.err.println("ERROR: " + e.getMessage());
    }
  }

  /**
   * A method to determine the validity of the arguments passed in.
   *
   * @param args
   * @return
   */
  private boolean validArgs(String[] args) {
    // If the number of arguments is not either 3 and 4, or the hasHeader argument is not
    // true or false, print out an error.
    if ((3 != args.length && args.length != 4)
        || ((args.length == 3 && !this.checkBoolean(args[2]))
            || (args.length == 4 && !this.checkBoolean(args[3])))) {
      System.err.println(
          "ERROR: The REPL search function takes in 3 to 4 arguments:\n"
              + "-CSV File Path (MUST BE IN DATA FOLDER): <file path>\n"
              + "-Value (USE '' TO DENOTE MULTI-WORDED PHRASES): "
              + "'<The value being searched for>'\n"
              + "-Column Restriction (!OPTIONAL!): "
              + "<Column index (0-indexed in integer form) / Column header>\n"
              + "-CSV Header: <true / false>");
      return false;
    } else {
      return true;
    }
  }

  /**
   * A helper method for checking the boolean argument passed into main.
   *
   * @return A boolean for whether the argument is "true" or "false".
   */
  private boolean checkBoolean(String arg) {
    return arg.equalsIgnoreCase("true") || arg.equalsIgnoreCase("false");
  }
}
