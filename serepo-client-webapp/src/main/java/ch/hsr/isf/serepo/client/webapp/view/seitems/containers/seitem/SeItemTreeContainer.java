package ch.hsr.isf.serepo.client.webapp.view.seitems.containers.seitem;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;

import ch.hsr.isf.serepo.data.restinterface.seitem.SeItem;

public class SeItemTreeContainer extends CustomComponent {
	
	private static final long serialVersionUID = -3629755455435817506L;

	public interface Listener {
		void seItemClicked(SeItemTreeItem seItemTreeItem);
	}

	private final Tree tree;
	private final HierarchicalContainer container = new HierarchicalContainer();
	private Map<String, SeItemTreeItem> pathToSeTreeItem = new HashMap<String, SeItemTreeItem>();
	private Listener listener;
	
	public SeItemTreeContainer() {

		setSizeFull();
		
		tree = new Tree(null, container);
		tree.setSizeUndefined();
		tree.setSelectable(true);
		tree.setNullSelectionAllowed(false);
		tree.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
		tree.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = -7738562408316085806L;

			@Override
			public void itemClick(ItemClickEvent event) {
				Object itemId = event.getItemId();
				if (tree.hasChildren(itemId)) {
					if (tree.isExpanded(itemId)) {
						tree.collapseItem(itemId);
					} else {
						tree.expandItem(itemId);
					}
				}
			}
		});
		tree.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = -5912075834762514167L;

			@Override
			public void valueChange(ValueChangeEvent event) {
			  if (event.getProperty().getValue() != null) {
			    if (SeItemTreeItem.class == event.getProperty().getValue().getClass()) {
			      if (listener != null) {
			        listener.seItemClicked(((SeItemTreeItem) event.getProperty().getValue()));
			      }
			    }
			  }
			}
		});
		
		Panel panel = new Panel(tree);
		panel.setSizeFull();

		setCompositionRoot(panel);
		
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	public void setSeItems(List<SeItem> seItems) {
		container.removeAllItems();
		pathToSeTreeItem.clear();

		for (SeItem seItem : seItems) {
		  LinkedList<TreeItem> folders = createFolders(seItem);
		  
		  SeItemTreeItem seItemTreeItem = new SeItemTreeItem(seItem);
		  container.addItem(seItemTreeItem);
		  container.setParent(seItemTreeItem, folders.peekLast());
		  container.setChildrenAllowed(seItemTreeItem, false);
		  tree.setItemIcon(seItemTreeItem, FontAwesome.FILE_O);
		  pathToSeTreeItem.put(seItem.getFolder() + seItem.getName(), seItemTreeItem);
		  
		}
		
	}
	
	private LinkedList<TreeItem> createFolders(SeItem seItem) {
	  LinkedList<TreeItem> listFolderTreeItems = new LinkedList<>();
	  TreeItem currentParent = null;
	  String[] folders = seItem.getFolder().split("/");
	  StringBuilder currentFolderPath = new StringBuilder();

	  String folderDelimiter = "";
      for (String folder : folders) {
        currentFolderPath.append(folderDelimiter).append(folder);
        
        if (!pathToSeTreeItem.containsKey(currentFolderPath.toString())) {
          // there is no SE-Item which represents current folder!
          FolderTreeItem folderTreeItem = new FolderTreeItem(currentFolderPath.toString(), folder, currentFolderPath.toString());
          container.addItem(folderTreeItem);
          container.setParent(folderTreeItem, currentParent);
          tree.setItemIcon(folderTreeItem, FontAwesome.FOLDER_O);
          currentParent = folderTreeItem;
          listFolderTreeItems.add(folderTreeItem);
        } else {
          // there is a SE-Item which represents current folder!
          // no folder will be created!
          SeItemTreeItem seItemTreeItem = pathToSeTreeItem.get(currentFolderPath.toString());
          container.setChildrenAllowed(seItemTreeItem, true);
          tree.setItemIcon(seItemTreeItem, FontAwesome.FOLDER_O);
          currentParent = seItemTreeItem; // set SE-ItemTreeItem as parent!
          listFolderTreeItems.add(seItemTreeItem);
        }
        
        if (folderDelimiter.isEmpty()) {
          folderDelimiter = "/";
        }
      }
      return listFolderTreeItems;
	}
	
}
