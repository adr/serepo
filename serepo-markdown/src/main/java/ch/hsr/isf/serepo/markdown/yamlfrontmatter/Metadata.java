package ch.hsr.isf.serepo.markdown.yamlfrontmatter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class Metadata extends TreeMap<String, Object> {

  private static final long serialVersionUID = -578424369885535668L;
  
  public interface ReservedKeys {
    /** This metadata key is used to reference an external content of an SE-Item. **/
    String CONTENT_URL = "content_url";
  }

  public enum InputType {
    JSON, YAML
  };

  public Metadata() {
    super();
  }
  
  public Metadata(Map<String, Object> map) {
    super(map);
  }

  public Metadata(String content, InputType inputType) throws IOException {
    super(create(content, inputType));
  }

  public Optional<List<String>> getAsStringList(Object key) {
    return getList(key, String.class);
  }

  @SuppressWarnings("unchecked")
  public Optional<Map<String, Object>> getAsMap(Object key) {
    if (Map.class.isInstance(this.get(key))) {
      return Optional.of((Map<String, Object>) this.get(key));
    } else {
      return Optional.absent();
    }
  }

  public String toYaml() throws JsonProcessingException {
    return to(new YAMLFactory(), false);
  }

  @SuppressWarnings("unchecked")
  private <T> Optional<List<T>> getList(Object key, Class<T> type) {
    if (this.containsKey(key)) {
      Builder<T> builder = ImmutableList.builder();
      Object value = this.get(key);
      for (T valueInList : (List<T>) value) {
        builder.add(valueInList);
      }
      return Optional.of((List<T>) builder.build());
    } else {
      return Optional.absent();
    }
  }

  private static Map<String, Object> create(String content, InputType inputType)
      throws IOException {
    switch (inputType) {
      case YAML:
        return create(content, new YAMLFactory());
      case JSON:
      default:
        return create(content, new JsonFactory());
    }
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Object> create(String content, JsonFactory factory)
      throws IOException {
    ObjectMapper mapper = new ObjectMapper(factory);
    return mapper.readValue(content, TreeMap.class);
  }

  private String to(JsonFactory factory, boolean prettyPrint) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper(factory);
    if (prettyPrint) {
      return mapper.writerWithDefaultPrettyPrinter()
                   .writeValueAsString(this);
    }
    return mapper.writeValueAsString(this);
  }

}
