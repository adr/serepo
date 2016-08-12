package ch.hsr.isf.serepo.client.webapp.view.seitems.containers.seitem;

import java.util.List;

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
	private final HierarchicalContainer container;
	private Listener listener;
	
	public SeItemTreeContainer() {

		setSizeFull();
		
		tree = new Tree(null, container = new HierarchicalContainer());
		tree.setSizeUndefined();
		tree.setSelectable(true);
		tree.setNullSelectionAllowed(false);
		tree.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
		tree.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = -7738562408316085806L;

			@Override
			public void itemClick(ItemClickEvent event) {
				Object itemId = event.getItemId();
				if (FolderTreeItem.class == itemId.getClass()) {
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
				if (SeItemTreeItem.class == event.getProperty().getValue().getClass()) {
					if (listener != null) {
						listener.seItemClicked(((SeItemTreeItem) event.getProperty().getValue()));
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
		for (SeItem seItem : seItems) {
			String[] folders = seItem.getFolder().split("/");
			if (folders[0].isEmpty()) {
				SeItemTreeItem seItemTreeItem = new SeItemTreeItem(seItem);
				container.addItem(seItemTreeItem);
				container.setChildrenAllowed(seItemTreeItem, false);
			} else {
				StringBuilder sbFolders = new StringBuilder();
				sbFolders.append(folders[0]).append("/");
				
				FolderTreeItem rootFolder = new FolderTreeItem(sbFolders.toString(), folders[0], sbFolders.toString());
				container.addItem(rootFolder);
				tree.setItemIcon(rootFolder, FontAwesome.FOLDER_O);

				Object parentId = rootFolder;
				for (int i = 1; i < folders.length; i++) {
					sbFolders.append(folders[i]).append("/");
					FolderTreeItem folder = new FolderTreeItem(sbFolders.toString(), folders[i], sbFolders.toString());
					container.addItem(folder);
					container.setParent(folder, parentId);
					tree.setItemIcon(folder, FontAwesome.FOLDER_O);
					parentId = folder;
				}
				SeItemTreeItem seItemTreeItem = new SeItemTreeItem(seItem);
				container.addItem(seItemTreeItem);
				container.setParent(seItemTreeItem, parentId);
				container.setChildrenAllowed(seItemTreeItem, false);
				tree.setItemIcon(seItemTreeItem, FontAwesome.FILE_O);
			}
		}
	}
	
}
