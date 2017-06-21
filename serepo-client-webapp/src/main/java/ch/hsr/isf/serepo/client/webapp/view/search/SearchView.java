package ch.hsr.isf.serepo.client.webapp.view.search;

import com.google.common.eventbus.Subscribe;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import ch.hsr.isf.serepo.client.webapp.AppNavigator;
import ch.hsr.isf.serepo.client.webapp.event.AppEvent;
import ch.hsr.isf.serepo.client.webapp.event.AppEvent.TitleChangeEvent;
import ch.hsr.isf.serepo.client.webapp.event.AppEventBus;
import ch.hsr.isf.serepo.client.webapp.view.AppViewType;
import ch.hsr.isf.serepo.client.webapp.view.search.SearchComponent.SearchResultListener;
import ch.hsr.isf.serepo.client.webapp.view.seitems.SeItemComponent;
import ch.hsr.isf.serepo.data.restinterface.search.SearchContainer;
import ch.hsr.isf.serepo.data.restinterface.search.SearchResult;

public class SearchView extends VerticalLayout implements View {
  private static final long serialVersionUID = -1994290342312994302L;

  private SearchComponent searchComponent = new SearchComponent();
  private SearchResultContainer searchResultContainer = new SearchResultContainer();
  private SeItemComponent seItemComponent = new SeItemComponent();

  public SearchView() {

    setSizeFull();
    setSpacing(true);

    searchComponent.setWidth("100%");
    searchResultContainer.setWidth("100%");
    searchResultContainer.setHeight(15, Unit.EM);
    
    Panel panel = new Panel(seItemComponent);
    panel.setSizeFull();
    panel.setCaption("SE-Item");
    panel.setIcon(FontAwesome.FILE_O);
    
    
    addComponent(searchComponent);
    addComponent(searchResultContainer);
    addComponent(panel);
    setExpandRatio(panel, 1);

    searchComponent.setListener(new SearchResultListener() {
      
      @Override
      public void searchResult(SearchContainer searchContainer) {
        searchResultContainer.setSearchResult(searchContainer.getSearchResult());
      }
    });
    searchResultContainer.setListener(new SearchResultContainer.Listener() {
      
      @Override
      public void searchResultClicked(SearchResult searchResult) {
        seItemComponent.setSeItem(searchResult.getSeItemUri());
      }
    });

  }
  
  @Subscribe
  private void jumpToSeItem(AppEvent.SelectSeItemInTree selectSeItemInTree) {
    SearchResult searchResult = searchResultContainer.getSelectedSearchResult();
    AppNavigator.navigateTo(AppViewType.SEITEMS, searchResult.getRepository(), searchResult.getCommitId());
    AppEventBus.post(selectSeItemInTree);
  }

  @Override
  public void attach() {
    super.attach();
    AppEventBus.register(this);
  }

  @Override
  public void detach() {
    AppEventBus.unregister(this);
    AppEventBus.post(new AppEvent.GlobalSearchField.Visible(true));
    super.detach();
  }

  @Override
  public void enter(ViewChangeEvent event) {
    AppEventBus.post(new TitleChangeEvent("Search"));
    AppEventBus.post(new AppEvent.GlobalSearchField.Visible(false));
    if (event.getParameters() != null) {
      String query = event.getParameters();
      if (!query.isEmpty()) {
        if (query.endsWith("/")) {
          query = query.substring(0, query.length() - 1);
        }
        searchComponent.executeQuery(query);
      }
    }
  }

}
