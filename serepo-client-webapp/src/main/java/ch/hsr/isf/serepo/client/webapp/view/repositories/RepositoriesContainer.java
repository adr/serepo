package ch.hsr.isf.serepo.client.webapp.view.repositories;

import java.util.List;
import java.util.Map;

import com.google.common.base.Optional;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Table;

import ch.hsr.isf.serepo.client.webapp.event.AppEvent;
import ch.hsr.isf.serepo.client.webapp.event.AppEventBus;
import ch.hsr.isf.serepo.client.webapp.view.UserConverter;
import ch.hsr.isf.serepo.data.restinterface.repository.Repository;

public class RepositoriesContainer extends CustomComponent {

  private static final long serialVersionUID = -2885424912611441094L;

  private final Table table;
  private final BeanItemContainer<Repository> container;

  public RepositoriesContainer() {

    setSizeFull();

    table = new Table(null, container = new BeanItemContainer<>(Repository.class));
    table.setSelectable(true);
    table.setSizeFull();
    table.setVisibleColumns("name", "description", "updated", "lastUpdateUser");
    table.setColumnHeaders("Name", "Description", "Last commit at", "Last commit by");
    table.setConverter("lastUpdateUser", new UserConverter());
    table.setNullSelectionAllowed(false);
    table.addItemClickListener(new ItemClickListener() {
      private static final long serialVersionUID = 3127940423276726881L;

      @Override
      public void itemClick(ItemClickEvent event) {
        Map<Class<?>, Object> map = (Map<Class<?>, Object>) VaadinSession.getCurrent().getAttribute("table-selections");
        map.put(RepositoriesContainer.class, event.getItemId());
        if (event.isDoubleClick()) {
          AppEventBus.post(new AppEvent.ItemDoubleClickevent<Repository>(container.getItem(table.getValue()).getBean()));
        }
      }
    });

    setCompositionRoot(table);

  }
  
  public void selectLastSelectedRepository() {
    Map<Class<?>, Object> map = (Map<Class<?>, Object>) VaadinSession.getCurrent().getAttribute("table-selections");
    selectRepository((Repository) map.get(RepositoriesContainer.class));
  }
  
  public void selectRepository(Repository repoToSelect) {
    table.select(null);
    if (repoToSelect != null) {
      for (Repository repo : container.getItemIds()) {
        if (repo.getId().equals(repoToSelect.getId())) {
          table.select(repo);
          table.setCurrentPageFirstItemId(repo);
          return;
        }
      }
    }
  }

  public void setRepositories(List<Repository> repositories) {
    container.removeAllItems();
    container.addAll(repositories);
  }

  public Optional<Repository> getSelectedRepository() {
    return Optional.fromNullable((Repository) table.getValue());
  }

}
