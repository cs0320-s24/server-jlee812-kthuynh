package edu.brown.cs.student.main.server.censusHandler;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CensusSource {
  private static Map<String, String> stateCodes = new HashMap<>();
  private static final Map<String, Map<String, String>> countyCodes = new HashMap<>();

  /**
   * A method that gets the JSON as a string.
   *
   * @param endpoint The endpoint from which the JSON is pulled.
   * @return The JSON as a string
   * @throws IOException Thrown when there is trouble opening the file.
   * @throws InterruptedException Thrown when the thread is interrupted.
   * @throws URISyntaxException Thrown when the uri does not exist.
   */
  private static String getJSONString(String endpoint)
      throws IOException, InterruptedException, URISyntaxException {
    HttpRequest buildCensusApiRequest =
        HttpRequest.newBuilder()
            .uri(new URI("https://api.census.gov/data/" + endpoint))
            .GET()
            .build();

    HttpResponse<String> sentCensusApiResponse =
        HttpClient.newBuilder()
            .build()
            .send(buildCensusApiRequest, HttpResponse.BodyHandlers.ofString());

    return sentCensusApiResponse.body();
  }

  /**
   * A method that turns the JSON string into a list of a list of strings.
   *
   * @param jsonString The JSON string.
   * @return A list of a list of strings representing the parsed census JSON.
   * @throws IOException Thrown when there is trouble getting the JSON.
   */
  private static List<List<String>> getJSONAsList(String jsonString) throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    Type listType =
        Types.newParameterizedType(
            List.class, Types.newParameterizedType(List.class, String.class));
    JsonAdapter<List<List<String>>> adapter = moshi.adapter(listType);
    return adapter.fromJson(jsonString);
  }

  private static void getStateCodes() throws IOException, URISyntaxException, InterruptedException {
    String jsonString = getJSONString("2010/dec/sf1?get=NAME&for=state:*");
    List<List<String>> deserializedStateCodes = getJSONAsList(jsonString);

    Map<String, String> mappedStateCodes = new HashMap<>();
    for (List<String> row : deserializedStateCodes) {
      mappedStateCodes.put(row.get(0).toLowerCase(), row.get(1));
    }

    stateCodes = mappedStateCodes;
  }

  private static void getCountyCodes(String stateCode)
      throws IOException, URISyntaxException, InterruptedException {
    String jsonString = getJSONString("2010/dec/sf1?get=NAME&for=county:*&in=state:" + stateCode);
    List<List<String>> deserializedCountyCodes = getJSONAsList(jsonString);

    Map<String, String> mappedCountyCodes = new HashMap<>();
    for (List<String> row : deserializedCountyCodes) {
      String countyName = row.get(0);
      if (countyName.lastIndexOf(',') != -1) {
        countyName = countyName.substring(0, countyName.lastIndexOf(',')).toLowerCase();
      }
      mappedCountyCodes.put(countyName, row.get(2));
    }

    countyCodes.put(stateCode, mappedCountyCodes);
  }

  private static String getStateCode(String state)
      throws IOException, URISyntaxException, InterruptedException, LocationNotFoundException {
    if (stateCodes.isEmpty()) {
      getStateCodes();
    }

    if (stateCodes.containsKey(state.toLowerCase())) {
      return stateCodes.get(state.toLowerCase());
    } else {
      throw new LocationNotFoundException("State not found: " + state, "state");
    }
  }

  public List<CensusResult> getBroadband(Location location) throws Exception {
    return getBroadband(location.state(), location.county());
  }

  private static List<CensusResult> getBroadband(String state, String county)
      throws IOException, URISyntaxException, InterruptedException, LocationNotFoundException {
    String encodedState = getStateCode(state);
    String encodedCounty = county;

    if (!county.equalsIgnoreCase("*")) {
      getCountyCodes(encodedState);
      if (countyCodes.get(encodedState).containsKey(county)) {
        encodedCounty = countyCodes.get(encodedState).get(county.toLowerCase());
      } else {
        throw new LocationNotFoundException("County not found: " + county, "county");
      }
    }

    EncodedLocation encodedLocation = new EncodedLocation(encodedState, encodedCounty);
    String jsonString =
        getJSONString(
            "2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&"
                + encodedLocation.getJSONParams());

    List<List<String>> deserializedBroadbandUsage = getJSONAsList(jsonString);
    List<CensusResult> results = new ArrayList<>();
    for (int i = 1; i < deserializedBroadbandUsage.size(); i++) {
      List<String> countyData = deserializedBroadbandUsage.get(i);
      results.add(new CensusResult(countyData.get(0), countyData.get(1)));
    }
    return results;
  }
}
