package ch.hsr.isf.serepo.markdown;

import com.google.common.base.MoreObjects;

/**
 * This class represents a link how it can appear in a markdown document. Furthermore it contains
 * the section the link belongs to.
 * 
 * @author Andreas
 *
 */
public class SectionLink extends Link {

  private String section;

  public SectionLink(String section, String text, String title, String url) {
    super(text, title, url);
    this.section = section;
  }

  public SectionLink(String section, Link link) {
    this(section, link.getText(), link.getTitle(), link.getUrl());
  }

  public String getSection() {
    return section;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
                      .add("Section", getSection())
                      .add("Text", getText())
                      .add("Title", getTitle())
                      .add("URL", getUrl())
                      .toString();
  }

}
