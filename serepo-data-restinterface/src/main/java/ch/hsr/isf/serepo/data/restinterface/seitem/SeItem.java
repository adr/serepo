package ch.hsr.isf.serepo.data.restinterface.seitem;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.hsr.isf.serepo.data.atom.annotations.AtomEntry;
import ch.hsr.isf.serepo.data.atom.annotations.AtomEntry.AtomContent;
import ch.hsr.isf.serepo.data.atom.annotations.AtomId;
import ch.hsr.isf.serepo.data.atom.annotations.AtomLink;
import ch.hsr.isf.serepo.data.atom.annotations.AtomPerson;
import ch.hsr.isf.serepo.data.atom.annotations.AtomTitle;
import ch.hsr.isf.serepo.data.atom.annotations.AtomUpdated;
import ch.hsr.isf.serepo.data.restinterface.common.Link;
import ch.hsr.isf.serepo.data.restinterface.common.User;

@AtomEntry
public class SeItem {

	@AtomId
	private URI id;
	
	@AtomTitle
	private String name;
	
	// TODO as atom-entry xml element
	private String folder;
	
	@AtomUpdated
	private Date updated;
	
	@AtomPerson
	private User author;
	
	@AtomLink
	private List<Link> links = new ArrayList<>();
	
	@AtomContent
	private Content content;
	
	@AtomContent
	public static class Content {
		
		@AtomContent.MediaType
		private String mimetype;
		
		@AtomContent.Src
		private String url;
		
		public Content() {
		}

		public String getMimetype() {
			return mimetype;
		}

		public void setMimetype(String mimetype) {
			this.mimetype = mimetype;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
		
	}
	
	public SeItem() {
	}

	public URI getId() {
		return id;
	}

	public void setId(URI id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
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

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

}
