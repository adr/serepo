package ch.hsr.isf.serepo.data.restinterface.seitem;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.hsr.isf.serepo.data.atom.annotations.AtomEntry;
import ch.hsr.isf.serepo.data.atom.annotations.AtomFeed;
import ch.hsr.isf.serepo.data.atom.annotations.AtomId;
import ch.hsr.isf.serepo.data.atom.annotations.AtomLink;
import ch.hsr.isf.serepo.data.atom.annotations.AtomTitle;
import ch.hsr.isf.serepo.data.atom.annotations.AtomUpdated;
import ch.hsr.isf.serepo.data.restinterface.common.Link;

@AtomFeed
public class SeItemContainer {

	@AtomId
	private URI id;
	
	@AtomTitle(prefix = "SE-Items of commit '", suffix = "'")
	private String commitId;
	
	@AtomUpdated
	private Date updated;
	
	@AtomLink
	private List<Link> links = new ArrayList<>();

	@AtomEntry
	private List<SeItem> seItems = new ArrayList<>();
	
	public SeItemContainer() {
	}

	public URI getId() {
		return id;
	}

	public void setId(URI id) {
		this.id = id;
	}

	public String getCommitId() {
		return commitId;
	}

	public void setCommitId(String commitId) {
		this.commitId = commitId;
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

	public List<SeItem> getSeItems() {
		return seItems;
	}

	public void setSeItems(List<SeItem> seItems) {
		this.seItems = seItems;
	}

}
