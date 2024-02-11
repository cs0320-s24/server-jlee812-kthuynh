package edu.brown.cs.student.Tests.creatorfromrowclasses;

import edu.brown.cs.student.main.csv.parser.CreatorFromRow;
import edu.brown.cs.student.main.csv.parser.FactoryFailureException;
import java.util.List;

public class PersonFromRow implements CreatorFromRow<Person> {

  @Override
  public Person create(List<String> row) throws FactoryFailureException {
    if (row.size() != 3) {
      throw new FactoryFailureException("Could not make Person from row!", row);
    } else {
      return new Person(row.get(0), row.get(1), row.get(2));
    }
  }
}
