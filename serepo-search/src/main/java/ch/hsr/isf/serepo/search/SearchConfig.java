package ch.hsr.isf.serepo.search;

public interface SearchConfig {

  public interface Fields {
    
    String SEITEM_PREFIX = "seitem_";
    
    String REPOSITORY = SEITEM_PREFIX + "repository";
    String COMMIT_ID = SEITEM_PREFIX + "commitid";
    String SEITEM_ID = SEITEM_PREFIX + "id";
    String SEITEM_DOCUMENTTYPE = SEITEM_PREFIX + "documenttype";
    
  }

}
