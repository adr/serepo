package ch.hsr.isf.serepo.data.restinterface.consistency.relation;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ch.hsr.isf.serepo.data.atom.annotations.AtomEntry;
import ch.hsr.isf.serepo.data.atom.annotations.AtomEntry.AtomSummary;
import ch.hsr.isf.serepo.data.atom.annotations.AtomId;
import ch.hsr.isf.serepo.data.atom.annotations.AtomLink;
import ch.hsr.isf.serepo.data.atom.annotations.AtomPerson;
import ch.hsr.isf.serepo.data.atom.annotations.AtomTitle;
import ch.hsr.isf.serepo.data.atom.annotations.AtomUpdated;
import ch.hsr.isf.serepo.data.restinterface.common.Link;
import ch.hsr.isf.serepo.data.restinterface.common.User;

@AtomEntry
public class RelationInconsistency {

  @AtomId
  private URI id;

  @AtomTitle
  private String seItem;

  @AtomSummary
  private String inconsistency;

  @AtomUpdated
  @JsonIgnore
  private Date update;

  @AtomPerson
  @JsonIgnore
  private User author;

  @AtomLink
  private List<Link> links = new ArrayList<>();

  public RelationInconsistency() {}

  public URI getId() {
    return id;
  }

  public void setId(URI id) {
    this.id = id;
  }

  public String getSeItem() {
    return seItem;
  }

  public void setSeItem(String seItem) {
    this.seItem = seItem;
  }

  public String getInconsistency() {
    return inconsistency;
  }

  public void setInconsistency(String inconsistency) {
    this.inconsistency = inconsistency;
  }

  public Date getUpdate() {
    return update;
  }

  public void setUpdate(Date update) {
    this.update = update;
  }

  public User getAuthor() {
    return author;
  }

  public void setAuthor(User author) {
    this.author = author;
  }

  public List<Link> getLinks() {
    return links;
  }

  public void setLinks(List<Link> links) {
    this.links = links;
  }

}
