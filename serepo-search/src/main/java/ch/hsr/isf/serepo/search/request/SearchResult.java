package ch.hsr.isf.serepo.search.request;

public class SearchResult {

  private String repository;
  private String commitid;
  private String seItemId;
  private String seItemName;

  public SearchResult() {}

  public String getRepository() {
    return repository;
  }

  public void setRepository(String repository) {
    this.repository = repository;
  }

  public String getCommitid() {
    return commitid;
  }

  public void setCommitid(String commitid) {
    this.commitid = commitid;
  }
  
  public String getSeItemId() {
    return seItemId;
  }
  
  public void setSeItemId(String seItemId) {
    this.seItemId = seItemId;
  }

  public String getSeItemName() {
    return seItemName;
  }

  public void setSeItemName(String seItemName) {
    this.seItemName = seItemName;
  }

}
