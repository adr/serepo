package ch.hsr.isf.serepo.search.request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient.RemoteSolrException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.hsr.isf.serepo.search.Search;

public class Searcher {

  private static final Logger logger = LoggerFactory.getLogger(Searcher.class);

  private final String solrUrl;

  public Searcher(String solrUrl) {
    this.solrUrl = solrUrl;
  }

  public List<SearchResult> search(String query) throws SearchException {

    List<SearchResult> searchResults = new ArrayList<>();

    try (HttpSolrClient solr = new HttpSolrClient(solrUrl)) {

      SolrQuery q = new SolrQuery();
      q.setQuery(query);
      q.setRows(100000); // TODO
      q.setFields(Search.Fields.REPOSITORY, Search.Fields.COMMITID, Search.Fields.SEITEM_ID, Search.Fields.SEITEM_NAME);

      QueryResponse queryResponse = solr.query(q);
      for (SolrDocument doc : queryResponse.getResults()) {
        SearchResult searchResult = new SearchResult();
        searchResult.setRepository((String) doc.getFirstValue(Search.Fields.REPOSITORY));
        searchResult.setCommitid((String) doc.getFirstValue(Search.Fields.COMMITID));
        searchResult.setSeItemId((String) doc.getFirstValue(Search.Fields.SEITEM_ID));
        searchResult.setSeItemName((String) doc.getFirstValue(Search.Fields.SEITEM_NAME));
        searchResults.add(searchResult);
      }

    } catch (SolrServerException | RemoteSolrException | IOException e) {
      String message = String.format("The search '%s' is invalid and caused an error.", query);
      logger.error(message, e);
      throw new SearchException(message, e);
    }

    return searchResults;

  }

}
