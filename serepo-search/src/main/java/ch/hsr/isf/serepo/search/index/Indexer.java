package ch.hsr.isf.serepo.search.index;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.common.util.ContentStreamBase;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import ch.hsr.isf.serepo.search.Search;

public class Indexer implements AutoCloseable {

  private static final Logger logger = LoggerFactory.getLogger(Indexer.class);

  private HttpSolrClient solr;
  private Set<String> fieldsInSolr;

  private String repository, commitId;

  public Indexer(String solrUrl, String repository, String commitId) {
    solr = new HttpSolrClient(solrUrl);
    this.repository = repository;
    this.commitId = commitId;
    fieldsInSolr = getFieldsInSolr();
  }

  public void index(final String seItemId, final String seItemName, final Map<String, Object> metadata, final byte[] content) throws IndexException {
    ContentStreamUpdateRequest streamUpdate = new ContentStreamUpdateRequest("/update/extract");
    try {
      // metadata
      streamUpdate.setParam(toParam(Search.Fields.REPOSITORY), repository);
      streamUpdate.setParam(toParam(Search.Fields.COMMITID), commitId);
      streamUpdate.setParam(toParam(Search.Fields.SEITEM_PATH), seItemId);
      streamUpdate.setParam(toParam(Search.Fields.SEITEM_NAME), seItemName);
      addMetadata(streamUpdate, metadata);
      // content
      streamUpdate.addContentStream(new ContentStreamBase.ByteArrayStream(content, seItemId));
      // send to solr
      solr.request(streamUpdate);
    } catch (SolrServerException | IOException e) {
      String message = String.format("There was an error while indexing content of SE-Item '%s'.", seItemId);
      logger.error(message, e);
      throw new IndexException(message, e);
    }
  }
  
  @SuppressWarnings("unchecked")
  private void addMetadata(ContentStreamUpdateRequest streamUpdate, Map<String, Object> metadata) throws SolrServerException, IOException {
    for (Map.Entry<String, Object> entry : metadata.entrySet()) {
      checkAndAddFieldToSolr(entry.getKey());
      if (entry.getValue() == null) {
        streamUpdate.setParam(toParam(entry.getKey()), "");
      } else if (Map.class.isInstance(entry.getValue())) {
        addMetadata(streamUpdate, (Map<String, Object>) entry.getValue());
      } else if (Collection.class.isInstance(entry.getValue())) {
          String list = Joiner.on(',').skipNulls().join((Collection<Object>) entry.getValue());
          // TODO a single value could be a collection or map!
          streamUpdate.setParam(toParam(entry.getKey()), list);
      } else {
        streamUpdate.setParam(toParam(entry.getKey()), entry.getValue().toString());
      }
    }
  }

  public void commit() throws IndexException {
    try {
      solr.commit();
    } catch (SolrServerException | IOException e) {
      String message = "An error occured while committing to Solr.";
      logger.error(message, e);
      throw new IndexException(message, e);
    }
  }

  @Override
  public void close() {
    try {
      solr.commit();
    } catch (SolrServerException | IOException e) {
      logger.error("An error occured while committing pending documents in the disconnect process.",
          e);
    } finally {
      try {
        solr.close();
      } catch (IOException e) {
        logger.error("An error occured while closing the connection to Solr.", e);
      }
    }
  }
  
  private Set<String> getFieldsInSolr() {
    Set<String> fields = new HashSet<>();
    try {
      @SuppressWarnings("unchecked")
      List<SimpleOrderedMap<Object>> fieldsList = (List<SimpleOrderedMap<Object>>) solr.request(new SchemaRequest.Fields()).findRecursive("fields");
      for (SimpleOrderedMap<Object> som : fieldsList) {
        fields.add((String) som.get("name"));
      }
    } catch (SolrServerException | IOException e) {
      logger.error("There was an error while loading the field definitions from Solr", e);
    }
    return fields;
  }

  private void checkAndAddFieldToSolr(String fieldName) throws SolrServerException, IOException {
    if (!fieldsInSolr.contains(toFieldName(fieldName))) {
      addFieldToSolr(fieldName);
    }
  }

  private void addFieldToSolr(String name) throws SolrServerException, IOException {
    name = toFieldName(name);
    Map<String, Object> map = new HashMap<>();
    map.put("name", name);
    map.put("type", "text_general");
    map.put("indexed", true);
    map.put("stored", true);
    logger.debug(String.format("Add field into Solr: %s", name));
    solr.request(new SchemaRequest.AddField(map));
    fieldsInSolr.add(name);
  }

  private String toParam(String input) {
    return "literal." + toFieldName(input);
  }

  private String toFieldName(String fieldName) {
    return fieldName.trim().replace(" ", "_").toLowerCase();
  }
  
}
