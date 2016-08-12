package ch.hsr.isf.serepo.data.atom.extension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Adapter to map a {@link Map} to a XML-Element called "metadata". The
 * {@link XmlMetadataAdapter.Metadata} holds a list with
 * {@link XmlMetadataAdapter.Metadata.Entry} entries.
 * 
 */
public class XmlMetadataAdapter extends XmlAdapter<XmlMetadataAdapter.Metadata, Map<String, Object>> {

	/**
	 * Class which holds all metadata entries {@link Metadata.Entry}.
	 *
	 */
	@XmlRootElement(namespace = Namespace.URI)
	public static class Metadata {

		@XmlElement(name = "entry", namespace = Namespace.URI)
		public List<Entry> entries = new ArrayList<>();
		
		/**
		 * Class which represents a metadata entry.
		 *
		 */
		@XmlRootElement(namespace = Namespace.URI)
		@XmlType(propOrder = { "key", "value", "entries" })
		public static class Entry {

			@XmlAttribute(required = true)
			public String key;
			@XmlAttribute(required = false)
			public String value;
			
			@XmlElement(name = "entry", namespace = Namespace.URI)
			public List<Entry> entries = new ArrayList<>();
			
			public Entry() {
			}

			public Entry(String key) {
				this.key = key;
			}
			
			public Entry(String key, String value) {
				this.key = key;
				this.value = value;
			}

		}

	}

	public XmlMetadataAdapter() {
	}

	@Override
	public Map<String, Object> unmarshal(Metadata metadata) throws Exception {
		Map<String, Object> map = new TreeMap<>();
		for (Metadata.Entry entry : metadata.entries) {
			map.put(entry.key, entry.value);
		}
		return map;
	}
	
	@Override
	public Metadata marshal(Map<String, Object> v) throws Exception {
		Metadata metadata = new Metadata();
		metadata.entries.addAll(marshalMap(v));
		return metadata;
	}
	
	@SuppressWarnings("unchecked")
	private List<Metadata.Entry> marshalMap(Map<String, Object> map) throws Exception {
		List<Metadata.Entry> entries = new ArrayList<>();
		for (java.util.Map.Entry<String, Object> mapEntry : map.entrySet()) {
			if (Map.class.isInstance(mapEntry.getValue())) {
				Metadata.Entry entry = new Metadata.Entry(mapEntry.getKey());
				entries.add(entry);
				entry.entries.addAll(marshalMap((Map<String, Object>) mapEntry.getValue()));
			} else if (Collection.class.isInstance(mapEntry.getValue())) {
				// TODO
				throw new Exception(String.format("Converting a list is currently not supported. Tag: '%s'", mapEntry.getKey()));
			} else {
				entries.add(new Metadata.Entry(mapEntry.getKey(), mapEntry.getValue().toString()));
			}
		}
		return entries;
	}

}
