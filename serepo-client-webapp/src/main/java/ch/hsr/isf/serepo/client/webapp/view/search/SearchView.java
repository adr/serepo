package ch.hsr.isf.serepo.client.webapp.view.search;

import java.util.List;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import ch.hsr.isf.serepo.client.webapp.event.AppEvent.TitleChangeEvent;
import ch.hsr.isf.serepo.client.webapp.event.AppEventBus;
import ch.hsr.isf.serepo.client.webapp.view.search.SearchComponent.CommitInfo;
import ch.hsr.isf.serepo.client.webapp.view.seitems.SeItemComponent;
import ch.hsr.isf.serepo.data.restinterface.search.SearchContainer;
import ch.hsr.isf.serepo.data.restinterface.search.SearchResult;

public class SearchView extends VerticalLayout implements View, ISearchView {
  private static final long serialVersionUID = -1994290342312994302L;

  private SearchPresenter presenter;
  private SearchComponent searchComponent = new SearchComponent();

  private SearchResultContainer searchResultContainer = new SearchResultContainer();
  private SeItemComponent seItemComponent = new SeItemComponent();


  public SearchView() {

    setSizeFull();

    setListeners();
    searchComponent.setSizeFull();

    VerticalLayout vlRight = new VerticalLayout(searchResultContainer, seItemComponent);
    vlRight.setSizeFull();
    vlRight.setSpacing(true);

    HorizontalLayout hl = new HorizontalLayout(searchComponent, vlRight);
    hl.setSizeFull();
    hl.setSpacing(true);
    addComponent(hl);

  }

  private void setListeners() {
    searchComponent.setListener(new SearchComponent.Listener() {

      @Override
      public void searchClicked(String query) {
        presenter.search(query);
      }

      @Override
      public void repositoryChanged(String repository) {
        presenter.loadCommitsForRepository(repository);
      }
    });

    searchResultContainer.setListener(new SearchResultContainer.Listener() {

      @Override
      public void searchResultClicked(SearchResult searchResult) {
        presenter.searchResultClicked(searchResult);
      }
    });
  }

  @Override
  public void setSearchResult(SearchContainer searchContainer) {
    searchResultContainer.setSearchResult(searchContainer.getSearchResult());
  }
  
  @Override
  public void setSeItem(String seItemUri) {
    seItemComponent.setSeItem(seItemUri);
  }

  @Override
  public void attach() {
    super.attach();
    presenter = new SearchPresenter(this);
  }

  @Override
  public void setRepositories(List<String> repositories) {
    searchComponent.setRepositories(repositories);
  }

  @Override
  public void setCommits(List<CommitInfo> commits) {
    searchComponent.setCommits(commits);
  }

  @Override
  public void enter(ViewChangeEvent event) {
    AppEventBus.post(new TitleChangeEvent("Search"));
  }

}
