package ch.hsr.isf.serepo.markdown;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pegdown.ast.AbstractNode;
import org.pegdown.ast.AutoLinkNode;
import org.pegdown.ast.ExpLinkNode;
import org.pegdown.ast.Node;
import org.pegdown.ast.RefLinkNode;
import org.pegdown.ast.ReferenceNode;
import org.pegdown.ast.TextNode;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

/**
 * This class contains useful utils processing markdown documents.
 * 
 * @author Andreas
 *
 */
public class MarkdownUtils {

  private MarkdownUtils() {}

  public static Optional<String> getText(Node node) {
    StringBuilder text = new StringBuilder();
    textNodeCollector(text, node);
    if (text.length() > 0) {
      return Optional.of(text.toString());
    }
    return Optional.absent();
  }

  private static void textNodeCollector(StringBuilder sb, Node node) {
    if (TextNode.class.isInstance(node)) {
      sb.append(((TextNode) node).getText());
    }
    for (Node child : node.getChildren()) {
      textNodeCollector(sb, child);
    }
  }

  public static List<Link> getLinks(List<Node> nodes,
      Map<String, ReferenceNode> mapReferenceNodes) {

    Map<String, Link> mapLinks = new HashMap<>();

    List<Node> linkNodes = getNodesByTypes(
        Arrays.asList(ExpLinkNode.class, RefLinkNode.class, AutoLinkNode.class), nodes);
    for (Node linkNode : linkNodes) {
      if (ExpLinkNode.class.isInstance(linkNode)) {
        ExpLinkNode expLinkNode = (ExpLinkNode) linkNode;
        if (!mapLinks.containsKey(expLinkNode.url)) {
          mapLinks.put(expLinkNode.url, toLink(expLinkNode));
        }
      } else if (RefLinkNode.class.isInstance(linkNode)) {
        RefLinkNode refLinkNode = (RefLinkNode) linkNode;
        String refKey = getRefKey(refLinkNode);
        if (mapReferenceNodes.containsKey(refKey)) {
          ReferenceNode referenceNode = mapReferenceNodes.get(refKey);
          if (!mapLinks.containsKey(referenceNode.getUrl())) {
            mapLinks.put(referenceNode.getUrl(), toLink(refLinkNode, referenceNode));
          }
        }
      } else if (AutoLinkNode.class.isInstance(linkNode)) {
        AutoLinkNode autoLinkNode = (AutoLinkNode) linkNode;
        if (!mapLinks.containsKey(autoLinkNode.getText())) {
          mapLinks.put(autoLinkNode.getText(), toLink(autoLinkNode));
        }
      }
    }

    return ImmutableList.copyOf(mapLinks.values());

  }

  public static Link toLink(ExpLinkNode node) {
    return new Link(getText(node).or(""), node.title, node.url);
  }

  public static Link toLink(RefLinkNode refLinkNode, ReferenceNode referenceNode) {
    return new Link(getText(refLinkNode).or(""), referenceNode.getTitle(), referenceNode.getUrl());
  }

  public static Link toLink(AutoLinkNode node) {
    return new Link(null, null, node.getText());
  }

  public static String getRefKey(RefLinkNode refLinkNode) {
    String refKey = null;
    boolean onlyIdInText = refLinkNode.referenceKey == null;
    if (onlyIdInText) {
      refKey = getText(refLinkNode).or("");
    } else {
      refKey = getText(refLinkNode.referenceKey).or("");
    }
    return refKey;
  }

  public static List<Node> getNodesByTypes(List<Class<? extends AbstractNode>> nodeTypes,
      List<Node> nodes) {

    List<Node> list = new ArrayList<>();

    for (Node node : nodes) {
      if (nodeTypes.contains(node.getClass())) {
        list.add(node);
      }
      list.addAll(getNodesByTypes(nodeTypes, node.getChildren()));
    }

    return list;

  }

}
