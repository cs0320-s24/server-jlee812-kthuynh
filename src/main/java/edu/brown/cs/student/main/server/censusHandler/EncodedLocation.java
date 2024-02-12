package edu.brown.cs.student.main.server.censusHandler;

public record EncodedLocation(String state, String county) {

  public String getJSONParams() {
    return "for=county:" + this.county + "&in=state:" + this.state;
  }
}
