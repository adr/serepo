package ch.hsr.isf.serepo.client.webapp.view.seitems.containers;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.themes.ValoTheme;

public class MetadataContainer extends CustomComponent {

	private static final long serialVersionUID = -958470993263723872L;
	
	private final TreeTable treeTable;
	private final HierarchicalContainer container;

	public MetadataContainer() {
		
		setSizeFull();
		
		treeTable = new TreeTable(null, container = new HierarchicalContainer());
		treeTable.setSizeFull();
		treeTable.addStyleName(ValoTheme.TREETABLE_SMALL);
		treeTable.addContainerProperty("key", String.class, "", "Key", null, Align.LEFT);
		treeTable.addContainerProperty("value", String.class, "", "Value", null, Align.LEFT);
		
		setCompositionRoot(treeTable);
		
	}

	public void setMetatadata(Map<String, Object> metadata) {
		container.removeAllItems();
		fill(null, metadata);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void fill(Object parentId, Map<String, Object> metadata) {
		
		for (Entry<String, Object> entry : metadata.entrySet()) {
			Object itemId = null;
			if (parentId != null) {
				itemId = String.format("%s.%s", parentId, entry.getKey());
			} else {
				itemId = entry.getKey();
			}
			Item item = container.addItem(itemId);
			item.getItemProperty("key").setValue(entry.getKey());
			if (parentId != null) {
				container.setParent(itemId, parentId);
				treeTable.setCollapsed(parentId, false);
			}
			if (entry.getValue() != null) {
    			if (Map.class.isAssignableFrom(entry.getValue().getClass())) {
    				fill(itemId, (Map) entry.getValue());
    			} else if (Collection.class.isAssignableFrom(entry.getValue().getClass())) {
    				fill(itemId, (Collection) entry.getValue());
    			} else {
    				item.getItemProperty("value").setValue(entry.getValue().toString());
    				container.setChildrenAllowed(itemId, false);
    			}
			}
		}
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void fill(Object parentId, Collection<Object> list) {
		for (Object object : list) {
			Object itemId = null;
			if (parentId != null) {
				itemId = String.format("%s.%s", parentId, object);
			} else {
				itemId = object;
			}
			Item item = container.addItem(itemId);
			container.setParent(itemId, parentId);
			
			if (Map.class.isAssignableFrom(object.getClass())) {
				fill(itemId, (Map) object);
			} else if (Collection.class.isAssignableFrom(object.getClass())) {
				fill(itemId, (Collection) object);
			} else {
				item.getItemProperty("value").setValue(object);
			}
		}
	}
	
}
