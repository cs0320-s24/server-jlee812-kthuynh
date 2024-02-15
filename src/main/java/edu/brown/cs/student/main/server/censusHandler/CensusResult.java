package edu.brown.cs.student.main.server.censusHandler;

/**
 * A record for the results given by the census.
 *
 * @param county The county.
 * @param broadband The broadband usage in the given county.
 */
public record CensusResult(String county, String broadband) {}
