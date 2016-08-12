package ch.hsr.isf.serepo.client.webapp.view.search;

import java.util.List;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Table;

import ch.hsr.isf.serepo.data.restinterface.search.SearchResult;

public class SearchResultContainer extends CustomComponent {

  private static final long serialVersionUID = -3703860774748923396L;

  private final Table table;
  private final BeanItemContainer<SearchResult> container;

  private Listener listener = null;

  public interface Listener {
    void searchResultClicked(SearchResult searchResult);
  }

  public SearchResultContainer() {

    setSizeFull();

    table = new Table(null, container = new BeanItemContainer<>(SearchResult.class));
    table.setSizeFull();
    table.setVisibleColumns("repository", "commitId", "seItemUri");
    table.setColumnHeaders("Repository", "CommitId", "SE-Item Uri");
    table.setColumnExpandRatio("seItemUri", 1.0f);
    table.setNullSelectionAllowed(false);
    table.setSelectable(true);
    table.addValueChangeListener(new ValueChangeListener() {
      private static final long serialVersionUID = -6496257516898733678L;

      @Override
      public void valueChange(ValueChangeEvent event) {
        if (table.getValue() != null && listener != null) {
          listener.searchResultClicked((SearchResult) table.getValue());
        }
      }
    });

    setCompositionRoot(table);

  }

  public void setListener(Listener listener) {
    this.listener = listener;
  }

  public void setSearchResult(List<SearchResult> searchResults) {
    container.removeAllItems();
    container.addAll(searchResults);
  }

}
