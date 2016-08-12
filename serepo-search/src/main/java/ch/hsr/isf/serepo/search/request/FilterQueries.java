package ch.hsr.isf.serepo.search.request;

public class FilterQueries {

  private FilterQueries() {
  }

  public static String create(String field, String value) {
    return String.format("%s:%s", field, value);
  }
  
}
