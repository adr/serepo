package ch.hsr.isf.serepo.data.restinterface.seitem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class CreateSeItem {

	private String name;
	
	private String folder;
	
	private Map<String, Object> metadata = new TreeMap<>();

	private List<Relation> relations = new ArrayList<>();
	
	public CreateSeItem() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}

	@SuppressWarnings("unchecked")
	public void setMetadata(TreeMap<String, Object> metadata) {
		// TODO find a better/cleaner way
		for (Entry<String, Object> entry : metadata.entrySet()) {
			if (HashMap.class.isAssignableFrom(entry.getValue().getClass())) {
				metadata.put(entry.getKey(), new TreeMap<String, Object>((Map<String, Object>) entry.getValue()));
			}
		}
		this.metadata = metadata;
	}

	public List<Relation> getRelations() {
		return relations;
	}

	public void setRelations(List<Relation> relations) {
		this.relations = relations;
	}

}
