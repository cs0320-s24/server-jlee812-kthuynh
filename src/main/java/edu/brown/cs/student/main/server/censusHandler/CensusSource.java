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
  private static Map<String, Map<String, String>> countyCodes = new HashMap<>();

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
    System.out.println(countyCodes);
  }

  private static String getStateCode(String state) throws Exception {
    if (stateCodes.isEmpty()) {
      getStateCodes();
    }

    if (stateCodes.containsKey(state.toLowerCase())) {
      return stateCodes.get(state.toLowerCase());
    } else {
      throw new Exception("State not found!");
    }
  }

  private static String getCountyCode(String stateCode, String county) {
    try {
      String jsonString = getJSONString("2010/dec/sf1?get=NAME&for=county:*&in=state:" + stateCode);
      List<List<String>> deserializedCountyCodes = getJSONAsList(jsonString);

      if (deserializedCountyCodes != null) {
        for (List<String> row : deserializedCountyCodes) {
          if (row.get(0).toLowerCase().contains(county.toLowerCase())) {
            return row.get(2);
          }
        }
      }

      return "Not found!";
    } catch (Exception e) {
      return "Error!";
    }
  }

  public List<CensusResult> getBroadband(Location location) throws Exception {
    return getBroadband(location.state(), location.county());
  }

  private static List<CensusResult> getBroadband(String stateCode, String countyCode)
      throws Exception {
    String encodedState = getStateCode(stateCode);
    String encodedCounty = countyCode;

    System.out.println(countyCode);
    if (!countyCode.equalsIgnoreCase("*")) {
      getCountyCodes(encodedState);
      encodedCounty = getCountyCode(encodedState, countyCode);
      // if (countyCodes.get(encodedState).containsKey(countyCode)) {
      // encodedCounty = countyCodes.get(encodedState).get(countyCode.toLowerCase());
      // } else {
      // throw new Exception("County not found!");
      // }
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
