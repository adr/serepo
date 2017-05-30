package ch.hsr.isf.serepo.search.index;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.ContentStreamBase;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.hsr.isf.serepo.search.SeItemDocumentType;
import ch.hsr.isf.serepo.search.SearchConfig;

public class Indexer implements AutoCloseable {

  private static final Logger logger = LoggerFactory.getLogger(Indexer.class);

  private HttpSolrClient solr;
  private Set<String> definedFieldsInSolr;

  private String repository, commitId;

  public Indexer(String solrUrl, String repository, String commitId) {
    solr = new HttpSolrClient(solrUrl);
    this.repository = repository;
    this.commitId = commitId;
  }

  public void metadata(final String seItemId, final Map<String, Object> metadata)
      throws IndexException {
    SolrInputDocument inputDocument = createInputDocument(repository, commitId, seItemId);
    try {
      definedFieldsInSolr = getFieldsInSolr();
      addMetadataFieldsToDoc(inputDocument, metadata);
      solr.add(inputDocument);
    } catch (SolrServerException | IOException e) {
      String message =
          String.format("There was an error while indexing metadata of SE-Item '%s'.", seItemId);
      logger.error(message, e);
      throw new IndexException(message, e);
    }
  }

  public void content(final String seItemId, final byte[] content) throws IndexException {
    ContentStreamUpdateRequest streamUpdate = new ContentStreamUpdateRequest("/update/extract");
    streamUpdate.addContentStream(new ContentStreamBase.ByteArrayStream(content, seItemId));
    streamUpdate.setParam("literal." + SearchConfig.Fields.REPOSITORY, repository);
    streamUpdate.setParam("literal." + SearchConfig.Fields.COMMIT_ID, commitId);
    streamUpdate.setParam("literal." + SearchConfig.Fields.SEITEM_ID, seItemId);
    streamUpdate.setParam("literal." + SearchConfig.Fields.SEITEM_DOCUMENTTYPE,
        SeItemDocumentType.CONTENT.toString());
    try {
      solr.request(streamUpdate);
    } catch (SolrServerException | IOException e) {
      String message =
          String.format("There was an error while indexing content of SE-Item '%s'.", seItemId);
      logger.error(message, e);
      throw new IndexException(message, e);
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

  private Set<String> getFieldsInSolr() throws SolrServerException, IOException {
    @SuppressWarnings("unchecked")
    List<SimpleOrderedMap<Object>> fieldsList =
        (List<SimpleOrderedMap<Object>>) solr.request(new SchemaRequest.Fields())
                                             .findRecursive("fields");
    Set<String> fields = new HashSet<>();
    for (SimpleOrderedMap<Object> som : fieldsList) {
      fields.add((String) som.get("name"));
    }
    return fields;
  }

  private void checkAndAddFieldToSolr(String fieldname) throws SolrServerException, IOException {
    if (!definedFieldsInSolr.contains(fieldname)) {
      addFieldToSolr(fieldname);
    }
  }

  private void addFieldToSolr(final String name) throws SolrServerException, IOException {
    Map<String, Object> map = new HashMap<>();
    map.put("name", name);
    map.put("type", "text_general");
    map.put("indexed", true);
    map.put("stored", true);
    logger.info(String.format("Add field solr: %s", name));
    solr.request(new SchemaRequest.AddField(map));
  }

  /**
   * Cleans a field name so that it is accepted in Solr. Field names in Solr must not contain
   * spaces!
   * 
   * @param name
   * @return
   */
  private String cleanFieldName(final String name) {
    String cleanedName = name;
    cleanedName = name.trim()
                      .replace(" ", "_")
                      .toLowerCase();
    return cleanedName;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private void addMetadataFieldsToDoc(SolrInputDocument doc, Map<String, Object> metadata)
      throws SolrServerException, IOException {
    for (Entry<String, Object> entry : metadata.entrySet()) {
      if (Map.class.isInstance(entry.getValue())) {
        addMetadataFieldsToDoc(doc, (Map) entry.getValue());
      } else if (Collection.class.isInstance(entry.getValue())) {
        Iterator iterator = ((Collection) entry.getValue()).iterator();
        if (iterator.hasNext()) {
          Object firstEntry = iterator.next();
          if (Map.class.isInstance(firstEntry)) {
            addMetadataFieldsToDoc(doc, (Map) firstEntry);
            while (iterator.hasNext()) {
              addMetadataFieldsToDoc(doc, (Map) iterator.next());
            }
          }
        }
      } else {
        String metadataKey = cleanFieldName(entry.getKey());
        checkAndAddFieldToSolr(metadataKey);
        doc.addField(metadataKey, entry.getValue());
      }
    }
  }

  private SolrInputDocument createInputDocument(String repository, String commitId,
      String seItemId) {
    SolrInputDocument inputDoc = new SolrInputDocument();
    inputDoc.addField(SearchConfig.Fields.REPOSITORY, repository);
    inputDoc.addField(SearchConfig.Fields.COMMIT_ID, commitId);
    inputDoc.addField(SearchConfig.Fields.SEITEM_ID, seItemId);
    inputDoc.addField(SearchConfig.Fields.SEITEM_DOCUMENTTYPE,
        SeItemDocumentType.METADATA.toString());
    return inputDoc;
  }

}
