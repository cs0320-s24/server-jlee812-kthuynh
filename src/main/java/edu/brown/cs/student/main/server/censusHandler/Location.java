package edu.brown.cs.student.main.server.censusHandler;

/**
 * A record for the location being searched on.
 *
 * @param state The state's name.
 * @param county The county's name.
 */
public record Location(String state, String county) {}
