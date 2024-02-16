package edu.brown.cs.student.Tests.CSVTests.creatorfromrowclasses;

import edu.brown.cs.student.main.csv.parser.CreatorFromRow;
import edu.brown.cs.student.main.csv.parser.FactoryFailureException;
import java.util.ArrayList;
import java.util.List;

/** A CreatorFromRow class that converts rows into integers */
public class IntegerListFromRow implements CreatorFromRow<List<Integer>> {

  @Override
  public List<Integer> create(List<String> row) throws FactoryFailureException {
    List<Integer> integerList = new ArrayList<>();

    for (String value : row) {
      try {
        integerList.add(Integer.parseInt(value));
      } catch (NumberFormatException e) {
        throw new FactoryFailureException("Could not convert value to integer!", row);
      }
    }

    return integerList;
  }
}
