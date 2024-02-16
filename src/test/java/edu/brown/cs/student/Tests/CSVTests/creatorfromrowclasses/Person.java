package edu.brown.cs.student.Tests.CSVTests.creatorfromrowclasses;

public class Person {
  private String name;
  private String birthday;
  private String hobby;

  public Person(String name, String birthday, String hobby) {
    this.name = name;
    this.birthday = birthday;
    this.hobby = hobby;
  }

  public String getName() {
    return this.name;
  }

  public String getBirthday() {
    return this.birthday;
  }

  public String getHobby() {
    return this.hobby;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Person person = (Person) o;
    return this.name.equalsIgnoreCase(person.getName())
        && this.birthday.equalsIgnoreCase(person.getBirthday())
        && this.hobby.equalsIgnoreCase(person.getHobby());
  }
}
