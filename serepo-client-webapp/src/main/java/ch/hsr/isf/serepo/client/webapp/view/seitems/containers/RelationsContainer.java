package ch.hsr.isf.serepo.client.webapp.view.seitems.containers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.themes.ValoTheme;

import ch.hsr.isf.serepo.data.restinterface.seitem.Relation;

public class RelationsContainer extends CustomComponent {

  private static final long serialVersionUID = 6136634101557738647L;

  private final Table table;
  private final BeanItemContainer<Relation> container;

  public RelationsContainer() {

    setSizeFull();

    table = new Table(null, container = new BeanItemContainer<>(Relation.class));
    table.addStyleName(ValoTheme.TABLE_SMALL);
    table.setSizeFull();
    table.setSelectable(false);
    table.setNullSelectionAllowed(false);
    table.setVisibleColumns("type");
    table.addGeneratedColumn("actions", new ColumnGenerator() {
      private static final long serialVersionUID = -4513240938829501255L;

      @Override
      public Object generateCell(Table source, Object itemId, Object columnId) {
        final Relation rel = (Relation) itemId;
        Button btnShowTarget = new Button("show target", new ClickListener() {
          private static final long serialVersionUID = 3560526349123313597L;
          @Override
          public void buttonClick(ClickEvent event) {
            new RelationTargetContentBrowser(rel.getType(), rel.getTarget());
          }
        });
        btnShowTarget.addStyleName(ValoTheme.BUTTON_LINK);
        btnShowTarget.addStyleName(ValoTheme.BUTTON_SMALL);
        return btnShowTarget;
      }
    });
    table.setColumnHeaders("Type", "Actions");
    setCompositionRoot(table);

  }

  public void setRelations(List<Relation> relations) {
    List<Relation> relationsSorted = new ArrayList<Relation>(relations);
    Collections.sort(relationsSorted, new Comparator<Relation>() {

      @Override
      public int compare(Relation o1, Relation o2) {
        return o1.getType()
                 .compareTo(o2.getType());
      }

    });
    container.removeAllItems();
    container.addAll(relationsSorted);
  }

}
