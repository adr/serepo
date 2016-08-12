package ch.hsr.isf.serepo.data.restinterface.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ch.hsr.isf.serepo.data.atom.annotations.AtomLink;

@AtomLink
@JsonInclude(Include.NON_NULL)
public class Link {

	@AtomLink.Title
	private String title;

	@AtomLink.Rel
	private String rel;

	@AtomLink.Href
	private String href;

	public Link() {
	}

	public Link(String rel, String href) {
		this.rel = rel;
		this.href = href;
	}

	public Link(String title, String rel, String href) {
		this.title = title;
		this.rel = rel;
		this.href = href;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getRel() {
		return rel;
	}

	public void setRel(String rel) {
		this.rel = rel;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

}
