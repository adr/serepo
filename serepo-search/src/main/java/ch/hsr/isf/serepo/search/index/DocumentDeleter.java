package ch.hsr.isf.serepo.search.index;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.hsr.isf.serepo.search.SearchConfig;
import ch.hsr.isf.serepo.search.request.FilterQueries;

public class DocumentDeleter implements AutoCloseable {

  private static final Logger logger = LoggerFactory.getLogger(DocumentDeleter.class);

  private HttpSolrClient solr;

  public DocumentDeleter(String solrUrl) {
    solr = new HttpSolrClient(solrUrl);
  }

  public void deleteForRepository(String repositoryName) throws DeleteException {

    String deleteQuery = FilterQueries.create(SearchConfig.Fields.REPOSITORY, repositoryName);

    UpdateRequest updateRequest = new UpdateRequest();
    updateRequest.deleteByQuery(deleteQuery);

    try {
      solr.request(updateRequest);
      solr.commit();
    } catch (SolrServerException | IOException e) {
      String message = String.format(
          "An error occured while deleting the index for repository '%s'.", repositoryName);
      logger.error(message, e);
      throw new DeleteException(message, e);
    }

  }

  @Override
  public void close() {
    try {
      solr.close();
    } catch (IOException e) {
      logger.error("An error occured while closing the connection to Solr.", e);
    }
  }
  
}
