package ch.hsr.isf.serepo.data.restinterface.commit;

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
public class CommitContainer {

	@AtomId
	private URI id;

	@AtomTitle(prefix = "Commits of '", suffix = "'")
	private String repository;

	@AtomUpdated
	private Date updated;

	@AtomLink
	private List<Link> links = new ArrayList<>();

	@AtomEntry
	private List<Commit> commits = new ArrayList<>();

	public CommitContainer() {
	}

	public URI getId() {
		return id;
	}

	public void setId(URI id) {
		this.id = id;
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
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

	public List<Commit> getCommits() {
		return commits;
	}

	public void setCommits(List<Commit> commits) {
		this.commits = commits;
	}

}
