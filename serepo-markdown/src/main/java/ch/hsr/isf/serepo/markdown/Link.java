package ch.hsr.isf.serepo.markdown;

import com.google.common.base.MoreObjects;

/**
 * This class represents a link how it can appear in a markdown document.
 * 
 * @author Andreas
 *
 */
public class Link {

  private String text;
  private String title;
  private String url;

  public Link(String text, String title, String url) {
    this.text = text;
    this.title = title;
    this.url = url;
  }

  public String getText() {
    return text;
  }

  public String getTitle() {
    return title;
  }

  public String getUrl() {
    return url;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
                      .add("Text", text)
                      .add("Title", title)
                      .add("URL", url)
                      .toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((url == null) ? 0 : url.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Link other = (Link) obj;
    if (url == null) {
      if (other.url != null)
        return false;
    } else if (!url.equals(other.url))
      return false;
    return true;
  }

}
