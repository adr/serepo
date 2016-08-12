package ch.hsr.isf.serepo.client.webapp.view.consistencies.relations;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;

import ch.hsr.isf.serepo.data.restinterface.consistency.relation.RelationInconsistency;

public class InconsistenciesTree extends CustomComponent {

  private static final long serialVersionUID = -8014481309832762934L;
  private static final String PROPERTY_CAPTION = "caption";
  private static final String PROPERTY_LIST = "list";
  
  private Tree tree;
  
  public interface Listener {
    
    void seItemClicked(List<RelationInconsistency> inconsistencies);
    
  }
  
  private Listener listener;

  public InconsistenciesTree() {

    setCaption("Inconsistent SE-Items");
    
    createTree();
    
    Panel pnlTree = new Panel(tree);
    pnlTree.setSizeFull();
    
    setCompositionRoot(pnlTree);
    setSizeFull();

  }

  public void setListener(Listener listener) {
    this.listener = listener;
  }

  private void createTree() {
    tree = new Tree();
    tree.addContainerProperty(PROPERTY_CAPTION, String.class, null);
    tree.addContainerProperty(PROPERTY_LIST, List.class, null);
    tree.setItemCaptionPropertyId(PROPERTY_CAPTION);
    tree.setSizeUndefined();
    tree.addItemClickListener(new ItemClickListener() {
      private static final long serialVersionUID = -35872619145166606L;
      @Override
      public void itemClick(ItemClickEvent event) {
        Object itemId = event.getItemId();
        if (tree.areChildrenAllowed(itemId)) {
          if (tree.isExpanded(itemId)) {
            tree.collapseItem(itemId);
          } else {
            tree.expandItem(itemId);
          }
        }
      }
    });
    tree.addValueChangeListener(new ValueChangeListener() {
      private static final long serialVersionUID = -5143946741533798488L;

      @Override
      @SuppressWarnings("unchecked")
      public void valueChange(ValueChangeEvent event) {
        Object itemId = event.getProperty()
                             .getValue();
        List<RelationInconsistency> inconsistencies = null;
        if (itemId != null) {
          inconsistencies = (List<RelationInconsistency>) tree.getItem(itemId)
                                                              .getItemProperty(PROPERTY_LIST)
                                                              .getValue();
        }
        if (inconsistencies == null) {
          inconsistencies = new ArrayList<>();
        }
        if (listener != null) {
          listener.seItemClicked(inconsistencies);
        }
      }
    });
  }

  @SuppressWarnings("unchecked")
  public void setInconsistencies(List<RelationInconsistency> inconsistencies) {
    tree.removeAllItems();
    for (RelationInconsistency inconsistency : inconsistencies) {
      String[] pathElements = inconsistency.getSeItem()
                                           .split("/");
      StringBuilder currentPath = new StringBuilder();
      String leaf = pathElements[pathElements.length - 1];
      String parent = null;
      for (String element : pathElements) {
        currentPath.append("/")
                   .append(element);
        Item item = tree.getItem(currentPath.toString());
        if (item == null) {
          item = tree.addItem(currentPath.toString());
          item.getItemProperty(PROPERTY_CAPTION)
              .setValue(element);
          tree.setParent(currentPath.toString(), parent);
          if (element == leaf) {
            item.getItemProperty(PROPERTY_LIST)
                .setValue(new ArrayList<>());
            tree.setChildrenAllowed(currentPath.toString(), false);
          }
        }
        if (element == leaf) {
          List<RelationInconsistency> inconsistenciesForSeItem =
              (List<RelationInconsistency>) tree.getItem(currentPath.toString())
                                                .getItemProperty(PROPERTY_LIST)
                                                .getValue();
          inconsistenciesForSeItem.add(inconsistency);
        } else {
          tree.expandItem(currentPath.toString());
        }
        parent = currentPath.toString();
      }
    }
  }

}
