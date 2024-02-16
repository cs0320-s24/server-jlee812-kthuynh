# Project Details
- Project Name: Server
- Total Estimated Time: 20 hours
- Team Member(s): kthuynh, Jlee812
- I USED INFORMATION FROM THE FOLLOWING SOURCES:
    - https://stackoverflow.com/questions/57388440/how-to-remove-everything-after-last-hyphen-using-regex-java
- Repo Link: https://github.com/cs0320-s24/server-jlee812-kthuynh

# Design Choices
Our Server consists of the following classes:
- Server: Our main class, which starts server and listens on relevant endpoints.
- DataSuccessResponse: A record for a successful JSON response.
- ErrorResponse: A record for a failed JSON response.
- HandlerErrorBuilder: A class for building out an error response.

## CSV Handler Classes
These are classes related to listening on endpoints related to the CSV API.
- CSVSource: This is a shared state for the handlers, containing important information like
the data stored in a CSV, as well as methods for viewing and searching it.
- CSVLoadHandler: This is a handler for loading the CSV with the file path and whether it
has a header, returning a JSON response.
- CSVSearchHandler: This is a handler for searching the CSV with the value and the column to narrow
by, returning a JSON response.
- CSVViewHandler: This is handler for viewing the CSV.

## Census API Handler Classes
- CensusSource: This is the data source for the handler, making requests to convert state and
county names to their associated codes, as well as calling for broadcast information.
- CensusHandler: A handler for returning the census API results to the user as a JSON object.
- CacheControl: A class responsible for handling caching of results, taking in a generic
LoadingCache.
- CensusResponseLoader: A class that extends the LoadingCache, and gets information from the 
CensusSource.
- CensusResult: A record for the relevant results of a broadcast query.
- Location: A record for the request a user sends, with state and county names.
- EncodedLocation: A record for the encoding of a requested location.
- CensusSourceInterface: a shared interface for the census source and mocked census source classes
to ensure tests that don't slow down the online API

## CSV Classes
- CSVParser: The CSVParser class is responsible for parsing through the CSV. It takes in a reader,
  a class that implements the CreatorFromRow interface, and a boolean for whether the CSV has a header.
  It then uses this information to process this data and a return a list of objects that represents
  the CSV information, these objects representing rows as determined by the CreatorFromRow object. In
  the instance something goes wrong reading a file or creating an object, it throws an IOException or
  a FactoryFailureException.
- CSVSearcher: The CSVSearcher class is responsible for searching through the CSV. It takes in a
  CSVParser, using it to get the data. It then has a search method and various helper methods
  to support that functionality. In order to search, it requires a desired value to look for, as well
  as a column to narrow by. If a column is not being narrowed on, it is treated as searching through
  the entire row, instead of a specific column. In the event searching fails because of a missing
  header value or an exceeded column index, or in the parsing, it will throw the errors that
  CSVParser does as well as a HeaderValueException or an IndexOutOfBoundsException.
- CreatorFromRow: The CreatorFromRow interface is a blueprint for classes to turn a row in a
  CSV into a desired object.
- StringListFromRow: The StringFromRow class is an implementation of the CreatorFromRow interface,
  and it takes a row and returns it as a list of strings. A list of strings is used to provide
  ease of printing and searching for the CSVSearcher's purposes.

# Errors/Bugs
- N/A

# Tests
- CensusHandlersTest: testing suite with mocked census data. Unit tests ensuring that the census
  handlers are behaving as expected.
- MockedCensusSource: implements the CensusSourceInterface, and returns a generic/constant piece
of data to not overwhelm the online census API with requests.
- CensusSourceTest: tests that the connection to online census API works and behaves correctly
- CSVHandlersTest: tests that connections to load, view, and search handlers all behave correctly
- CSVTests: unit tests that ensure that our CSV parser is behaving correctly

# How to
## Run tests:
1. Run 'mvn package' in the terminal.
2. Go to src/test/java/edu.brown.cs.student/Tests to see the test files.
3. Go to each test file and press the run button to run the tests.

## Run program:
1. Run 'mvn package' in the terminal.
2. Go to src/main/java/edu.brown.cs.student.main/server to find the Server class.
3. Run the program with the play button or ./run, which opens up the server.
4. Go to the website http://localhost:3232/
5. Attatch the following endpoints for desired usage:
   - To use the CSV API:
   1. Add on the query "/loadcsv" with the parameters of "filePath" and "header". "filePath"
   should be the CSV file path and "header" should be a true or false value depending on
   if the CSV file has a header.
   2. To view the CSV, use the query "/viewcsv".
   3. To search the CSV, use the query "/searchcsv". It requires the parameter "value", which
   is the value it searches on, and the optional "column", for the column value to search by.
   - To use the Census API:
   1. Add on the query "/broadcast" with the required parameter of "state", which should be
   the name of the state you're searching for, and the optional parameter of "county", which
   is the name of the county you are searching for in the format "[COUNTY NAME] County".