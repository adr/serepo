package ch.hsr.isf.serepo.markdown;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;
import org.pegdown.ast.HeaderNode;
import org.pegdown.ast.Node;
import org.pegdown.ast.ReferenceNode;
import org.pegdown.ast.RootNode;
import org.pegdown.plugins.PegDownPlugins;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import ch.hsr.isf.serepo.markdown.yamlfrontmatter.plugin.YamlFrontmatterNode;
import ch.hsr.isf.serepo.markdown.yamlfrontmatter.plugin.YamlFrontmatterParser;

public class MarkdownReader {

  private RootNode rootNode;
  private Optional<YamlFrontmatterNode> yamlFrontmatterNode;
  private Map<String, List<Node>> mapHeaders;
  private Map<String, ReferenceNode> mapReferenceNodes;

  public MarkdownReader(byte[] content) {
    parse(content);
    index();
  }

  private void parse(byte[] content) {

    char[] mdFileChars = new String(content, StandardCharsets.UTF_8).toCharArray();

    PegDownPlugins plugins = new PegDownPlugins.Builder().withPlugin(YamlFrontmatterParser.class)
                                                         .build();
    PegDownProcessor pegDownProcessor =
        new PegDownProcessor(Extensions.ALL, MarkdownSettings.MAX_PARSING_TIME, plugins);

    rootNode = pegDownProcessor.parseMarkdown(mdFileChars);

  }

  private void index() {

    mapHeaders = new HashMap<>();
    mapHeaders.put("", new ArrayList<Node>()); // for all nodes which belong nowhere.
    mapReferenceNodes = new HashMap<>();
    yamlFrontmatterNode = Optional.absent();

    String lastHeader = "";
    for (Node node : rootNode.getChildren()) {

      if (YamlFrontmatterNode.class.isInstance(node) && rootNode.getChildren()
                                                                .get(0) == node) {
        yamlFrontmatterNode = Optional.of((YamlFrontmatterNode) node);
      } else if (HeaderNode.class.isInstance(node)) {
        lastHeader = MarkdownUtils.getText(node)
                                  .or("");
        mapHeaders.put(lastHeader, new ArrayList<Node>()); // Note: if n > 1 exact same headers
                                                           // exist, only the last one is saved.
      } else if (ReferenceNode.class.isInstance(node)) {
        ReferenceNode refNode = (ReferenceNode) node;
        Optional<String> id = MarkdownUtils.getText(refNode);
        if (id.isPresent()) {
          mapReferenceNodes.put(id.get(), refNode);
        }
      } else { // add node to the current section / header it belongs to.
        mapHeaders.get(lastHeader)
                  .add(node);
      }

    }

  }

  public List<SectionLink> getLinks(List<String> forSections) {
    Builder<SectionLink> builder = new ImmutableList.Builder<SectionLink>();
    for (String sectionName : forSections) {
      Optional<Section> section = getSection(sectionName);
      if (section.isPresent()) {
        for (Link link : MarkdownUtils.getLinks(section.get()
                                                       .getNodes(),
            mapReferenceNodes)) {
          builder.add(new SectionLink(section.get()
                                             .getName(),
              link));
        }
      }
    }
    return builder.build();
  }

  private Optional<Section> getSection(String sectionName) {
    Section section = null;
    if (mapHeaders.containsKey(sectionName)) {
      List<Node> list = mapHeaders.get(sectionName);
      section = new Section(sectionName, ImmutableList.copyOf(list));
    }
    return Optional.fromNullable(section);
  }

  public Optional<String> getYamlFrontmatter() {
    return Optional.fromNullable(yamlFrontmatterNode.or(new YamlFrontmatterNode(null))
                                                    .getContent());
  }

  public static Optional<String> readYamlFrontmatter(InputStream content) throws IOException {
    final StringBuilder yamlContent = new StringBuilder();
    try (BufferedReader bufferedReader =
        new BufferedReader(new InputStreamReader(content, StandardCharsets.UTF_8))) {
      String line = bufferedReader.readLine();
      if (line != null) {
        if (line.equals("---")) {
          // YAML-Frontmatter startmark found
          yamlContent.append(line)
                     .append("\n");
          while ((line = bufferedReader.readLine()) != null && !line.equals("---")) {
            yamlContent.append(line)
                       .append("\n");
          }
        }
      }
    }
    String yamlContentTrimmed = yamlContent.toString()
                                           .trim();
    if (yamlContentTrimmed.isEmpty()) {
      yamlContentTrimmed = null;
    }
    return Optional.fromNullable(yamlContentTrimmed);
  }

  public static String readContent(InputStream content, List<String> listOfHeadersToStop)
      throws IOException {
    final StringBuilder shortedContent = new StringBuilder();
    try (BufferedReader bufferedReader =
        new BufferedReader(new InputStreamReader(content, StandardCharsets.UTF_8))) {
      String line = bufferedReader.readLine();
      if (line != null) {
        if (line.equals("---")) {
          // YAML-Frontmatter startmark found
          while ((line = bufferedReader.readLine()) != null && !line.equals("---")) {
            // YAML-Frontmatter content skipped
          }
          // YAML-Frontmatter endmark found, read next line
          line = bufferedReader.readLine();
        }
        while (line != null) {
          if (line.startsWith(MarkdownSettings.LINK_SECTION_LEVEL)) { // Header
            if (listOfHeadersToStop.contains(line.substring(3))) {
              break;
            }
          }
          shortedContent.append(line)
                        .append("\n");
          line = bufferedReader.readLine();
        }
      }
    }
    return shortedContent.toString()
                         .trim();
  }

}
