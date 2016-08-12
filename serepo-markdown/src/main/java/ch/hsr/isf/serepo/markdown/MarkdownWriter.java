package ch.hsr.isf.serepo.markdown;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Strings;

import ch.hsr.isf.serepo.markdown.yamlfrontmatter.Metadata;

public class MarkdownWriter {

  private ObjectMapper yamlMapper;

  private Object yamlFrontmatter;
  private String content;
  private Map<String, List<Link>> mapSectionsToLinks = new TreeMap<>();

  public MarkdownWriter() {
    yamlMapper = new ObjectMapper(new YAMLFactory());
    yamlMapper.enable(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS);
    yamlMapper.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
  }

  public void setYamlFrontmatter(Metadata metadata) {
    this.yamlFrontmatter = metadata;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public void addLink(String section, Link link) {
    if (!mapSectionsToLinks.containsKey(section)) {
      mapSectionsToLinks.put(section, new ArrayList<Link>());
    }
    mapSectionsToLinks.get(section)
                      .add(link);
  }

  public void write(OutputStreamWriter outputStreamWriter)
      throws JsonProcessingException, IOException {
    outputStreamWriter.write(writeYamlFrontmatter());
    outputStreamWriter.write("---\n");
    outputStreamWriter.flush();
    writeContent(outputStreamWriter);
    writeSections(outputStreamWriter);
  }

  private String writeYamlFrontmatter() throws JsonProcessingException, IOException {
    return yamlMapper.writerWithDefaultPrettyPrinter()
                     .writeValueAsString(yamlFrontmatter);
  }

  private void writeContent(OutputStreamWriter outputStreamWriter) throws IOException {
    if (content != null) {
      outputStreamWriter.write(content);
      outputStreamWriter.flush();
    }
  }

  private void writeSections(OutputStreamWriter outputStreamWriter) throws IOException {
    for (Map.Entry<String, List<Link>> entry : mapSectionsToLinks.entrySet()) {
      writeNewLine(outputStreamWriter);
      outputStreamWriter.write(
          String.format("%s%s", MarkdownSettings.LINK_SECTION_LEVEL, entry.getKey()));
      writeNewLine(outputStreamWriter);
      writeLinks(outputStreamWriter, entry.getValue());
      outputStreamWriter.flush();
    }
  }

  private void writeLinks(OutputStreamWriter outputStreamWriter, List<Link> links)
      throws IOException {
    for (Link link : links) {
      String url = Strings.nullToEmpty(link.getUrl());
      String text;
      if (Strings.isNullOrEmpty(link.getText())) {
        text = url;
      } else {
        text = link.getText();
      }
      if (Strings.isNullOrEmpty(link.getTitle())) {
        writeLink(outputStreamWriter, text, url);
      } else {
        writeLink(outputStreamWriter, text, url, link.getTitle());
      }
      writeNewLine(outputStreamWriter);
    }
  }

  private void writeLink(OutputStreamWriter outputStreamWriter, String text, String url)
      throws IOException {
    outputStreamWriter.write(String.format("- [%s](%s)", text, url));
  }

  private void writeLink(OutputStreamWriter outputStreamWriter, String text, String url,
      String title) throws IOException {
    outputStreamWriter.write(String.format("- [%s](%s \"%s\")", text, url, title));
  }

  private void writeNewLine(OutputStreamWriter outputStreamWriter) throws IOException {
    outputStreamWriter.write("\n");
  }

}
