package ch.hsr.isf.serepo.relations;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class RelationsFileWriter {

	private ObjectMapper mapper;

	public RelationsFileWriter() {
		this (new YAMLFactory());
	}
	
	public RelationsFileWriter(JsonFactory factory) {
		mapper = new ObjectMapper(factory);
	}

	public String writeAsString(RelationsFile relFile) throws JsonProcessingException {
		return mapper.writeValueAsString(relFile);
	}

	public byte[] writeAsByteArray(RelationsFile relFile) throws JsonProcessingException {
		return mapper.writeValueAsBytes(relFile);
	}

	public void writeToFile(RelationsFile relFile, File file) throws JsonProcessingException, IOException {
		try (FileWriter fw = new FileWriter(file)) {
			mapper.writeValue(fw, relFile);
		}
	}

	/**
	 * Writes the {@link RelationsFile} to the {@link OutputStream}. The stream
	 * is not closed by this method!
	 * 
	 * @param relFile
	 * @param out
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public void writeToOutputStream(RelationsFile relFile, OutputStream out)
			throws JsonProcessingException, IOException {
		mapper.writeValue(out, relFile);
	}

}
