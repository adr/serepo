package ch.hsr.isf.serepo.client.webapp.view.seitems.containers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.themes.ValoTheme;

import ch.hsr.isf.serepo.client.webapp.event.AppEvent;
import ch.hsr.isf.serepo.client.webapp.event.AppEventBus;
import ch.hsr.isf.serepo.client.webapp.view.seitems.ShowSeItemWindow;
import ch.hsr.isf.serepo.data.restinterface.common.Link;

public class RelationsContainer extends CustomComponent {

  private static final long serialVersionUID = 6136634101557738647L;

  private final Table table;
  private final BeanItemContainer<Link> container;

  public RelationsContainer() {

    setSizeFull();
    setCaption("Relations");
    setIcon(FontAwesome.SHARE_ALT);

    table = new Table(null, container = new BeanItemContainer<>(Link.class));
    table.addStyleName(ValoTheme.TABLE_SMALL);
    table.setSizeFull();
    table.setSelectable(false);
    table.setNullSelectionAllowed(false);
    table.setVisibleColumns("title");
    table.addGeneratedColumn("actions", new ColumnGenerator() {
      private static final long serialVersionUID = -4513240938829501255L;

      @Override
      public Object generateCell(Table source, Object itemId, Object columnId) {
        final Link link = (Link) itemId;
        
        Button btnShowSeItem = createActionButton("show SE-Item", new ClickListener() {
          private static final long serialVersionUID = 3560526349123313597L;

          @Override
          public void buttonClick(ClickEvent event) {
            new ShowSeItemWindow(link.getHref());
          }
        });
        Button btnJumpToSeItem = createActionButton("jump to SE-Item", new ClickListener() {
          private static final long serialVersionUID = 8061250855056493068L;

          @Override
          public void buttonClick(ClickEvent event) {
            AppEventBus.post(new AppEvent.SelectSeItemInTree(link.getHref()));
          }
        });
        
        HorizontalLayout hlActionButtons = new HorizontalLayout(btnShowSeItem, btnJumpToSeItem);
        hlActionButtons.setSpacing(true);
        return hlActionButtons;
      }
    });
    table.setColumnHeaders("Type", "Actions");
    setCompositionRoot(table);

  }
  
  private Button createActionButton(String caption, ClickListener clickListener) {
    Button btn = new Button(caption, clickListener);
    btn.addStyleName(ValoTheme.BUTTON_LINK);
    btn.addStyleName(ValoTheme.BUTTON_SMALL);
    return btn;
  }

  public void setRelations(List<Link> relations) {
    List<Link> relationsSorted = new ArrayList<Link>(relations);
    Collections.sort(relationsSorted, new Comparator<Link>() {

      @Override
      public int compare(Link o1, Link o2) {
        return o1.getTitle().compareTo(o2.getTitle());
      }

    });
    container.removeAllItems();
    container.addAll(relationsSorted);
  }

}
