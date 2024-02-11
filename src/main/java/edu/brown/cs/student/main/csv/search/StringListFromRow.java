package edu.brown.cs.student.main.csv.search;

import edu.brown.cs.student.main.csv.parser.CreatorFromRow;
import edu.brown.cs.student.main.csv.parser.FactoryFailureException;
import java.util.List;

public class StringListFromRow implements CreatorFromRow<List<String>> {

  @Override
  public List<String> create(List<String> row) throws FactoryFailureException {
    for (String value : row) {
      if (value.length() == 0) {
        throw new FactoryFailureException("Missing column value in row : " + row, row);
      }
    }

    return row;
  }
}