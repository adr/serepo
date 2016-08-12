package ch.hsr.isf.serepo.relations;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RelationsFile {

	private Map<String, RelationDefinition> definitions = new TreeMap<>();

	public RelationsFile() {
	}

	/**
	 * Merges two {@link RelationsFile}s together and returns a new object
	 * instance.
	 * 
	 * @param relFile1
	 * @param relFile2
	 * @return A new object instance.
	 */
	@JsonIgnore
	public static RelationsFile merge(RelationsFile relFile1, RelationsFile relFile2) {
		RelationsFile relFile = new RelationsFile();
		return relFile.merge(relFile1).merge(relFile2);
	}

	/**
	 * Merges the given {@link RelationsFile} into this {@link RelationsFile}.
	 * The current object instance is returned.
	 * 
	 * @param relFile
	 * @return The current object instance.
	 */
	@JsonIgnore
	public RelationsFile merge(RelationsFile relFile) {
		for (RelationDefinition relDef : relFile.getDefinitions()) {
			this.addIfNew(relDef);
		}
		return this;
	}

	@JsonIgnore
	public Optional<RelationDefinition> getDefinition(String identifier) {
		return Optional.fromNullable(definitions.get(identifier));
	}

	@JsonIgnore
	public boolean hasDefinition(String identifier) {
		return definitions.containsKey(identifier);
	}

	@JsonIgnore
	public boolean addIfNew(RelationDefinition relDef) {
		boolean added = false;
		if (!definitions.containsKey(relDef.getIdentifier())) {
			definitions.put(relDef.getIdentifier(), relDef);
			added = true;
		}
		return added;
	}

	@JsonIgnore
	public void addOrReplace(RelationDefinition relDef) {
		definitions.put(relDef.getIdentifier(), relDef);
	}
	
	@JsonIgnore
	public void remove(RelationDefinition relDef) {
		remove(relDef.getIdentifier());
	}
	
	@JsonIgnore
	public void remove(String identifier) {
		definitions.remove(identifier);
	}

	public List<RelationDefinition> getDefinitions() {
		return ImmutableList.copyOf(definitions.values());
	}

	public void setDefinitions(List<RelationDefinition> definitions) {
		this.definitions.clear();
		for (RelationDefinition relDef : definitions) {
			addIfNew(relDef);
		}
	}

}
