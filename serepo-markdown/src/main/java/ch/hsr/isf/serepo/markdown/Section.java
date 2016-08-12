package ch.hsr.isf.serepo.markdown;

import java.util.List;

import org.pegdown.ast.Node;

import com.google.common.collect.ImmutableList;

/**
 * This class represents a section of a markdown document. It contains the section name as well as
 * all nodes which belong to the section.
 * 
 * @author Andreas
 *
 */
public class Section {

  private String name;
  private ImmutableList<Node> nodes;

  public Section(String name) {
    this.name = name;
    nodes = ImmutableList.of();
  }

  public Section(String name, ImmutableList<Node> nodes) {
    this.name = name;
    this.nodes = nodes;
  }

  public String getName() {
    return name;
  }

  public List<Node> getNodes() {
    return nodes.asList();
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
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
    Section other = (Section) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }

}
