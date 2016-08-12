package ch.hsr.isf.serepo.data.restinterface.search;

import java.util.ArrayList;
import java.util.List;

public class SearchContainer {

	private List<SearchResult> searchResult = new ArrayList<>();

	public SearchContainer() {
	}

	public List<SearchResult> getSearchResult() {
		return searchResult;
	}

	public void setSearchResult(List<SearchResult> searchResult) {
		this.searchResult = searchResult;
	}

}
