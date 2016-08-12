package ch.hsr.isf.serepo.data.restinterface.metadata;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ch.hsr.isf.serepo.data.atom.annotations.AtomEntry;
import ch.hsr.isf.serepo.data.atom.annotations.AtomFeed;
import ch.hsr.isf.serepo.data.atom.annotations.AtomId;
import ch.hsr.isf.serepo.data.atom.annotations.AtomLink;
import ch.hsr.isf.serepo.data.atom.annotations.AtomTitle;
import ch.hsr.isf.serepo.data.atom.annotations.AtomUpdated;
import ch.hsr.isf.serepo.data.restinterface.common.Link;

@AtomFeed
public class MetadataContainer {

  @AtomId
  private URI id;

  @AtomTitle
  @JsonIgnore
  private final static String TITLE = "Metadata";

  @AtomUpdated
  private Date updated;

  @AtomLink
  private List<Link> links = new ArrayList<>();

  @AtomEntry
  private MetadataEntry metadata;

  public MetadataContainer() {}

  public URI getId() {
    return id;
  }

  public void setId(URI id) {
    this.id = id;
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

  public MetadataEntry getMetadata() {
    return metadata;
  }

  public void setMetadata(MetadataEntry metadata) {
    this.metadata = metadata;
  }

}
