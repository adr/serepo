package ch.hsr.isf.serepo.client.webapp.view.repositories;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import ch.hsr.isf.serepo.client.webapp.model.Settings;
import ch.hsr.isf.serepo.client.webapp.services.SeRepoRestAPI;
import ch.hsr.isf.serepo.data.restinterface.repository.RepositoryContainer;

public class RepositoryPresenter {

  private IRepositoriesView view;

  public RepositoryPresenter(IRepositoriesView view) {
    this.view = view;
    load();
  }

  public void load() {
    RepositoryContainer repositoryContainer = SeRepoRestAPI.getRepositories();
    view.setRepositories(repositoryContainer.getRepositories());
  }

  public void deleteRepository(String name) {

    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(String.format("%s/repos/%s", Settings.getFromSession()
                                                                          .getSerepoUrl(),
        name));
    Response response = null;

    try {
      response = target.request()
                       .delete();

      switch (Status.fromStatusCode(response.getStatus())) {
        case NO_CONTENT:
          Notification.show(String.format("Repository '%s' deleted.", name));
          load();
          break;
        default:
          Notification.show(response.getStatusInfo()
                                    .getReasonPhrase(),
              response.readEntity(String.class), Type.ERROR_MESSAGE);
          break;
      }
      
      // quietly delete search index.
      target = client.target(String.format("%s/search?repository=%s", Settings.getFromSession()
                                                                     .getSerepoUrl(),
          name));
      target.request().delete();
      
    } finally {
      if (response != null) {
        response.close();
      }
    }

  }

}
