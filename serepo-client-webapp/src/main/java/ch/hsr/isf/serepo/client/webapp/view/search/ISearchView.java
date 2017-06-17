package ch.hsr.isf.serepo.client.webapp.view.search;

import java.util.List;

import ch.hsr.isf.serepo.client.webapp.view.search.SearchComponent.CommitInfo;
import ch.hsr.isf.serepo.data.restinterface.search.SearchContainer;

public interface ISearchView {

  void setSearchResult(SearchContainer searchContainer);

  void setSeItem(String seItemUri);
  
  void setRepositories(List<String> repositories);

  void setCommits(List<CommitInfo> commits);

}
