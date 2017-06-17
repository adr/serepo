package ch.hsr.isf.serepo.client.webapp.view.search;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.common.base.Strings;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import ch.hsr.isf.serepo.client.webapp.model.Settings;
import ch.hsr.isf.serepo.client.webapp.view.search.SearchComponent.CommitInfo;
import ch.hsr.isf.serepo.data.restinterface.commit.Commit;
import ch.hsr.isf.serepo.data.restinterface.commit.CommitContainer;
import ch.hsr.isf.serepo.data.restinterface.repository.Repository;
import ch.hsr.isf.serepo.data.restinterface.repository.RepositoryContainer;
import ch.hsr.isf.serepo.data.restinterface.search.SearchContainer;
import ch.hsr.isf.serepo.data.restinterface.search.SearchResult;

public class SearchPresenter {

  private ISearchView view;

  public SearchPresenter(ISearchView view) {
    this.view = view;
    loadRepositories();
  }

  public void loadRepositories() {

    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(String.format("%s/repos", Settings.getFromSession()
                                                                       .getSerepoUrl()));
    RepositoryContainer repositoryContainer = target.request(MediaType.APPLICATION_JSON_TYPE)
                                                    .get(new GenericType<RepositoryContainer>(
                                                        RepositoryContainer.class));

    List<String> repositories = new ArrayList<>();
    for (Repository repository : repositoryContainer.getRepositories()) {
      repositories.add(repository.getName());
    }
    view.setRepositories(repositories);

  }

  public void loadCommitsForRepository(String repository) {

    if (Strings.isNullOrEmpty(repository)) {
      view.setCommits(new ArrayList<CommitInfo>());
      return;
    }

    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(String.format("%s/repos/%s/commits", Settings.getFromSession()
                                                                                  .getSerepoUrl(),
        repository));
    CommitContainer commitContainer = target.request(MediaType.APPLICATION_JSON_TYPE)
                                            .get(new GenericType<CommitContainer>(
                                                CommitContainer.class));

    List<SearchComponent.CommitInfo> commits = new ArrayList<>();
    for (Commit commit : commitContainer.getCommits()) {
      String[] pathElements = commit.getId()
                                    .getPath()
                                    .split("/");
      String commitId = pathElements[pathElements.length - 1];
      commits.add(new SearchComponent.CommitInfo(commitId, commit.getShortMessage()));
    }
    view.setCommits(commits);

  }

  public void search(String query) {

    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(String.format("%s/search", Settings.getFromSession()
                                                                        .getSerepoUrl()));
    target = target.queryParam("q", query);

    Response response = null;
    try {
      response = target.request(MediaType.APPLICATION_JSON_TYPE)
                       .get();
      if (response.getStatus() == Status.OK.getStatusCode()) {
        SearchContainer searchContainer = response.readEntity(SearchContainer.class);
        view.setSearchResult(searchContainer);
      } else {
        String messageFromServer = null;
        try {
          messageFromServer = "\n" + response.readEntity(String.class);
        } catch (IllegalStateException e) {
          messageFromServer = "";
        }
        Notification.show(String.format("Error while searching!"), response.getStatusInfo()
                                                                           .getReasonPhrase()
            + messageFromServer, Type.ERROR_MESSAGE);
      }
    } finally {
      if (response != null) {
        response.close();
      }
    }

  }

  public void searchResultClicked(SearchResult searchResult) {
    view.setSeItem(searchResult.getSeItemUri());
  }

}
