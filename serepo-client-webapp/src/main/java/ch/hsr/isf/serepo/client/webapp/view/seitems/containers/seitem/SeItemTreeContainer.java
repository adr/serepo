package ch.hsr.isf.serepo.client.webapp.view.seitems.containers.seitem;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.eventbus.Subscribe;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.Resource;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.CollapseEvent;
import com.vaadin.ui.Tree.CollapseListener;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.Tree.ExpandListener;
import com.vaadin.ui.VerticalLayout;

import ch.hsr.isf.serepo.client.webapp.event.AppEvent;
import ch.hsr.isf.serepo.client.webapp.event.AppEventBus;
import ch.hsr.isf.serepo.data.restinterface.seitem.SeItem;

public class SeItemTreeContainer extends CustomComponent {

  private static final long serialVersionUID = -3629755455435817506L;

  public interface Listener {
    void seItemClicked(SeItemTreeItem seItemTreeItem);
  }

  private final Tree tree;
  private final HierarchicalContainer container = new HierarchicalContainer();
  private Map<String, SeItemTreeItem> pathToSeTreeItem = new HashMap<String, SeItemTreeItem>();
  private CheckBox chbxSeItemCanRepresentFolder = new CheckBox("Commit contains SE-Items which represents packages/folders", true);
  private Listener listener;

  private List<SeItem> seItems;

  public SeItemTreeContainer() {

    setSizeFull();

    container.addContainerProperty("icon", Resource.class, null);

    tree = new Tree(null, container);
    tree.setSizeUndefined();
    tree.setSelectable(true);
    tree.setNullSelectionAllowed(false);
    tree.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
    tree.setItemIconPropertyId("icon");
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
    tree.addCollapseListener(new CollapseListener() {
      private static final long serialVersionUID = 1635470441516221761L;

      @Override
      public void nodeCollapse(CollapseEvent event) {
        toggleFolderIcon(event.getItemId());
      }
    });
    tree.addExpandListener(new ExpandListener() {
      private static final long serialVersionUID = -6109682679971665988L;

      @Override
      public void nodeExpand(ExpandEvent event) {
        toggleFolderIcon(event.getItemId());
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

    VerticalLayout vlPanel = new VerticalLayout(panel, chbxSeItemCanRepresentFolder);
    vlPanel.setSizeFull();
    vlPanel.setSpacing(true);
    vlPanel.setExpandRatio(panel, 1f);
    vlPanel.setComponentAlignment(chbxSeItemCanRepresentFolder, Alignment.BOTTOM_LEFT);

    setCompositionRoot(vlPanel);

    chbxSeItemCanRepresentFolder.addValueChangeListener(new ValueChangeListener() {
      private static final long serialVersionUID = 7940276200234018080L;

      @Override
      public void valueChange(ValueChangeEvent event) {
        setSeItems(seItems);
      }
    });

  }
  
  @SuppressWarnings("unchecked")
  private void toggleFolderIcon(Object itemId) {
    if (itemId != null && TreeItem.class.isInstance(itemId)) {
      TreeItem treeItem = (TreeItem) itemId;
      if (treeItem.getIcon() == FolderTreeItem.ICON_CLOSED) {
        treeItem.setIcon(FolderTreeItem.ICON_OPEN);
      } else {
        treeItem.setIcon(FolderTreeItem.ICON_CLOSED);
      }
      container.getItem(itemId).getItemProperty("icon").setValue(treeItem.getIcon());
    }
  }

  public void setListener(Listener listener) {
    this.listener = listener;
  }

  @Override
  public void attach() {
    super.attach();
    AppEventBus.register(this);
  }

  @Override
  public void detach() {
    AppEventBus.unregister(this);
    super.detach();
  }

  @Subscribe
  public void selectSeItem(AppEvent.SelectSeItemInTree event) {
    for (Object itemId : container.getItemIds()) {
      if (((TreeItem) itemId).getId().toString().equals(event.getUriOfSeItem())) {
        tree.select(itemId);
        expandItemsRecursevlyUp(itemId);
      }
    }
  }
  
  private void expandItemsRecursevlyUp(Object itemId) {
    if (itemId != null) {
      tree.expandItem(itemId);
      expandItemsRecursevlyUp(container.getParent(itemId));
    }
  }

  public void setSeItems(List<SeItem> seItems) {
    this.seItems = seItems;
    container.removeAllItems();
    pathToSeTreeItem.clear();

    for (SeItem seItem : seItems) {
      LinkedList<TreeItem> folders = createFolders(seItem);

      SeItemTreeItem seItemTreeItem = new SeItemTreeItem(seItem);
      addItem(seItemTreeItem, folders.peekLast());
      container.setChildrenAllowed(seItemTreeItem, false);
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

      if (chbxSeItemCanRepresentFolder.getValue()) {
        if (!pathToSeTreeItem.containsKey(currentFolderPath.toString())) {
          // there is no SE-Item which represents current folder!
          FolderTreeItem folderTreeItem = createFolderTreeitem(currentParent, currentFolderPath, folder);
          currentParent = folderTreeItem;
          listFolderTreeItems.add(folderTreeItem);
        } else {
          // there is a SE-Item which represents current folder!
          // no folder will be created!
          SeItemTreeItem seItemTreeItem = pathToSeTreeItem.get(currentFolderPath.toString());
          configAsFolder(seItemTreeItem);
          currentParent = seItemTreeItem; // set SE-ItemTreeItem as parent!
          listFolderTreeItems.add(seItemTreeItem);
        }
      } else {
        FolderTreeItem folderTreeItem = createFolderTreeitem(currentParent, currentFolderPath, folder);
        currentParent = folderTreeItem;
        listFolderTreeItems.add(folderTreeItem);
      }

      if (folderDelimiter.isEmpty()) {
        folderDelimiter = "/";
      }
    }
    return listFolderTreeItems;
  }

  private FolderTreeItem createFolderTreeitem(TreeItem currentParent, StringBuilder currentFolderPath, String caption) {
    FolderTreeItem folderTreeItem = new FolderTreeItem(currentFolderPath.toString(), currentFolderPath.toString(), caption);
    addItem(folderTreeItem, currentParent);
    configAsFolder(folderTreeItem);
    return folderTreeItem;
  }
  
  @SuppressWarnings("unchecked")
  private void addItem(TreeItem treeItem, TreeItem parent) {
    if (!container.containsId(treeItem)) {
      Item item = container.addItem(treeItem);
      item.getItemProperty("icon").setValue(treeItem.getIcon());
      container.setParent(treeItem, parent);
    }
  }
  
  @SuppressWarnings("unchecked")
  private void configAsFolder(TreeItem item) {
    container.setChildrenAllowed(item, true);
    item.setIcon(FolderTreeItem.ICON_CLOSED);
    container.getItem(item).getItemProperty("icon").setValue(item.getIcon());
  }

}
