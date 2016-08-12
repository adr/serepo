package ch.hsr.isf.serepo.relations;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * Can read a YAML formatted content to create a {@link RelationsFile}. The
 * input content must at least start with "---". Null or empty content is not
 * allowed and will result in an exception. It is always guaranteed to return an
 * non-null object instance.
 * 
 */
public class RelationsFileReader {

	private ObjectMapper mapper;

	public RelationsFileReader() {
		mapper = new ObjectMapper(new YAMLFactory());
	}

	public RelationsFile read(String content) throws JsonProcessingException, IOException {
		return returnNonNull(mapper.readValue(content, RelationsFile.class));
	}

	public RelationsFile read(byte[] content) throws JsonProcessingException, IOException {
		return returnNonNull(mapper.readValue(content, RelationsFile.class));
	}

	public RelationsFile read(InputStream src) throws JsonProcessingException, IOException {
		return returnNonNull(mapper.readValue(src, RelationsFile.class));
	}

	public RelationsFile read(File src) throws JsonProcessingException, IOException {
		return returnNonNull(mapper.readValue(src, RelationsFile.class));
	}

	private RelationsFile returnNonNull(RelationsFile relFile) {
		if (relFile == null) {
			relFile = new RelationsFile();
		}
		return relFile;
	}

}
