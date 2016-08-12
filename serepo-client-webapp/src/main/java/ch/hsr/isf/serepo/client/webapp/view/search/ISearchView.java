package ch.hsr.isf.serepo.client.webapp.view.search;

import java.util.List;
import java.util.Map;

import ch.hsr.isf.serepo.client.webapp.view.search.SearchComponent.CommitInfo;
import ch.hsr.isf.serepo.data.restinterface.search.SearchContainer;

public interface ISearchView {

  void setSearchResult(SearchContainer searchContainer);

  void setSeItemContent(String url);

  void setSeItemMetadata(Map<String, Object> metadata);

  void setRepositories(List<String> repositories);

  void setCommits(List<CommitInfo> commits);

}
