package ch.hsr.isf.serepo.data.restinterface.metadata;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ch.hsr.isf.serepo.data.atom.annotations.AtomEntry;
import ch.hsr.isf.serepo.data.atom.annotations.AtomId;
import ch.hsr.isf.serepo.data.atom.annotations.AtomLink;
import ch.hsr.isf.serepo.data.atom.annotations.AtomPerson;
import ch.hsr.isf.serepo.data.atom.annotations.AtomTitle;
import ch.hsr.isf.serepo.data.atom.annotations.AtomUpdated;
import ch.hsr.isf.serepo.data.atom.annotations.AtomEntry.AtomJAXBContent;
import ch.hsr.isf.serepo.data.atom.annotations.AtomEntry.AtomSummary;
import ch.hsr.isf.serepo.data.atom.extension.XmlMetadataAdapter;
import ch.hsr.isf.serepo.data.restinterface.common.Link;
import ch.hsr.isf.serepo.data.restinterface.common.User;

@AtomEntry
public class MetadataEntry {
 
  @AtomId
  private URI id;
  
  @AtomTitle
  @JsonIgnore
  private static final String TITLE = "Metadata";
  
  @AtomPerson
  private User author;
  
  @AtomUpdated
  private Date updated;
  
  @AtomLink
  private List<Link> links = new ArrayList<>();
  
  @AtomJAXBContent
  @XmlJavaTypeAdapter(XmlMetadataAdapter.class)
  @JsonIgnore
  private Map<String, Object> metadataForAtom = new TreeMap<>();
  
  private Map<String, Object> map = new TreeMap<>();
  
  @AtomSummary
  @JsonIgnore
  private static final String SUMMARY = "Metadata";
  
  public MetadataEntry() {
  }

  public URI getId() {
    return id;
  }

  public void setId(URI id) {
    this.id = id;
  }

  public User getAuthor() {
    return author;
  }

  public void setAuthor(User author) {
    this.author = author;
  }

  public Date getUpdated() {
    return updated;
  }

  public void setUpdated(Date updated) {
    this.updated = updated;
  }

  public List<Link> getLinks() {
    return links;
  }

  public void setLinks(List<Link> links) {
    this.links = links;
  }

  public Map<String, Object> getMap() {
    return map;
  }

  public void setMap(Map<String, Object> metadata) {
    this.map = this.metadataForAtom = metadata;
  }

}
