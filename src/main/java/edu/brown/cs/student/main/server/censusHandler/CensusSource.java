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
import java.util.List;

public class CensusSource {

  private String getJSONString(String endpoint)
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

  private List<List<String>> getJSONAsList(String jsonString) throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    Type listType =
        Types.newParameterizedType(
            List.class, Types.newParameterizedType(List.class, String.class));
    JsonAdapter<List<List<String>>> adapter = moshi.adapter(listType);
    List<List<String>> deserializedList = adapter.fromJson(jsonString);
    return deserializedList;
  }

  public String getStateCode(String state) {
    try {
      String jsonString = this.getJSONString("2010/dec/sf1?get=NAME&for=state:*");
      List<List<String>> deserializedStateCodes = this.getJSONAsList(jsonString);

      if (deserializedStateCodes != null) {
        for (List<String> row : deserializedStateCodes) {
          if (row.get(0).equalsIgnoreCase(state)) {
            return row.get(1);
          }
        }
      }

      return "Not found!";
    } catch (Exception e) {
      return "Error!";
    }
  }

  public String getCountyCode(String stateCode, String county) {
    try {
      String jsonString =
          this.getJSONString("2010/dec/sf1?get=NAME&for=county:*&in=state:" + stateCode);
      List<List<String>> deserializedCountyCodes = this.getJSONAsList(jsonString);

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
}
