package ch.hsr.isf.serepo.data.restinterface.search;

public class SearchResult {

  private String repository;
  private String commitId;
  private String seItemUri;
  private String seItemName;

  public SearchResult() {}

  public String getRepository() {
    return repository;
  }

  public void setRepository(String repository) {
    this.repository = repository;
  }

  public String getCommitId() {
    return commitId;
  }

  public void setCommitId(String commitId) {
    this.commitId = commitId;
  }

  public String getSeItemUri() {
    return seItemUri;
  }

  public void setSeItemUri(String seItemUri) {
    this.seItemUri = seItemUri;
  }

  public String getSeItemName() {
    return seItemName;
  }

  public void setSeItemName(String seItemName) {
    this.seItemName = seItemName;
  }

}
