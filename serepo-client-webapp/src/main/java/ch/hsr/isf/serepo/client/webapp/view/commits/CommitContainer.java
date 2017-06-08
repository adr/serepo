package ch.hsr.isf.serepo.client.webapp.view.commits;

import java.util.List;

import com.google.common.base.Optional;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Table;

import ch.hsr.isf.serepo.client.webapp.event.AppEvent;
import ch.hsr.isf.serepo.client.webapp.event.AppEventBus;
import ch.hsr.isf.serepo.client.webapp.view.UserConverter;
import ch.hsr.isf.serepo.data.restinterface.commit.Commit;

public class CommitContainer extends CustomComponent {

  private static final long serialVersionUID = 526120782025670697L;
  
  private Table table;
  private BeanItemContainer<Commit> container;

  public CommitContainer() {
    
    table = new Table(null, container = new BeanItemContainer<>(Commit.class));
    table.setSelectable(true);
    table.setSizeFull();
    table.setVisibleColumns("shortMessage", "author", "when"); // TODO show commitId?
    table.setColumnHeaders("Description", "Author", "Date");
    table.setConverter("author", new UserConverter());
    table.setCaptionAsHtml(true);
    table.setNullSelectionAllowed(false);
    table.addItemClickListener(new ItemClickListener() {
      private static final long serialVersionUID = 684301669055888L;

      @Override
      public void itemClick(ItemClickEvent event) {
        if (event.isDoubleClick()) {
          AppEventBus.post(new AppEvent.ItemDoubleClickevent<Commit>(container.getItem(table.getValue()).getBean()));
        }
      }
    });
  
    setSizeFull();
    setCompositionRoot(table);
    
  }
  
  public void setCommits(List<Commit> commits) {
    container.removeAllItems();
    container.addAll(commits);
  }

  public Optional<Commit> getSelectedCommit() {
    return Optional.fromNullable((Commit) table.getValue());
  }
  
}
