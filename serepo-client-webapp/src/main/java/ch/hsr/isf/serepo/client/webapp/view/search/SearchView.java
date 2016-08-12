package ch.hsr.isf.serepo.client.webapp.view.search;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;

import ch.hsr.isf.serepo.client.webapp.event.AppEvent.TitleChangeEvent;
import ch.hsr.isf.serepo.client.webapp.event.AppEventBus;
import ch.hsr.isf.serepo.client.webapp.view.search.SearchComponent.CommitInfo;
import ch.hsr.isf.serepo.client.webapp.view.seitems.containers.ContentContainer;
import ch.hsr.isf.serepo.client.webapp.view.seitems.containers.MetadataContainer;
import ch.hsr.isf.serepo.data.restinterface.search.SearchContainer;
import ch.hsr.isf.serepo.data.restinterface.search.SearchResult;

public class SearchView extends VerticalLayout implements View, ISearchView {
  private static final long serialVersionUID = -1994290342312994302L;

  private SearchPresenter presenter;
  private SearchComponent searchComponent = new SearchComponent();

  private ContentContainer contentContainer;
  private MetadataContainer metadataContainer;

  private SearchResultContainer searchResultContainer;

  public SearchView() {

    setSizeFull();

    searchComponent.setListener(new SearchComponent.Listener() {

      @Override
      public void searchClicked(String repository, String commitId, String searchIn, String query) {
        presenter.search(repository, commitId, searchIn, query);
      }

      @Override
      public void repositoryChanged(String repository) {
        presenter.loadCommitsForRepository(repository);
      }
    });

    searchResultContainer = new SearchResultContainer();
    searchResultContainer.setListener(new SearchResultContainer.Listener() {

      @Override
      public void searchResultClicked(SearchResult searchResult) {
        presenter.searchResultClicked(searchResult);
      }
    });
    searchComponent.setSizeFull();
    searchResultContainer.setCaption("Search result");

    contentContainer = new ContentContainer();
    contentContainer.setCaption("Content of selected SE-Item");
    metadataContainer = new MetadataContainer();
    metadataContainer.setCaption("Metadata of selected SE-Item");

    VerticalLayout vlRight = new VerticalLayout(searchResultContainer, contentContainer, metadataContainer);
    vlRight.setSizeFull();
    vlRight.setSpacing(true);

    HorizontalLayout hl = new HorizontalLayout(searchComponent, vlRight);
    hl.setSizeFull();
    hl.setSpacing(true);
    addComponent(hl);

  }

  @Override
  public void setSearchResult(SearchContainer searchContainer) {
    searchResultContainer.setSearchResult(searchContainer.getSearchResult());
    contentContainer.clearContent();
    setSeItemMetadata(new TreeMap<String, Object>());
  }

  @Override
  public void setSeItemContent(String url) {
    try {
      contentContainer.setContent(new URL(url));
    } catch (MalformedURLException e) {
      Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
    }
  }

  @Override
  public void setSeItemMetadata(Map<String, Object> metadata) {
    metadataContainer.setMetatadata(metadata);
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
