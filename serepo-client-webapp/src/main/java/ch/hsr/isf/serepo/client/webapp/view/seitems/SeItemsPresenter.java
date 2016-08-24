package ch.hsr.isf.serepo.client.webapp.view.seitems;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import ch.hsr.isf.serepo.client.webapp.model.Settings;
import ch.hsr.isf.serepo.client.webapp.services.SeItemDataLoader;
import ch.hsr.isf.serepo.data.restinterface.common.Link;
import ch.hsr.isf.serepo.data.restinterface.seitem.SeItem;
import ch.hsr.isf.serepo.data.restinterface.seitem.SeItemContainer;

public class SeItemsPresenter {

  private ISeItemsView view;

  public SeItemsPresenter(ISeItemsView view) {
    this.view = view;
  }

  public void load(String repository, String commitId) {

    String uri = String.format("%s/repos/%s/commits/%s/seitems", Settings.getFromSession()
                                                                         .getSerepoUrl(),
        repository, commitId);

    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(uri);
    Response response = null;
    try {
      response = target.request()
                       .accept(MediaType.APPLICATION_JSON_TYPE)
                       .get();
      if (response.getStatus() == Status.OK.getStatusCode()) {
        SeItemContainer seItemContainer = response.readEntity(SeItemContainer.class);
        view.setSeItems(seItemContainer.getSeItems());
      } else {
        Notification.show(
            String.format("Error while loading commits for repository '%s'", repository),
            response.getStatusInfo()
                    .getReasonPhrase(),
            Type.ERROR_MESSAGE);
      }
    } finally {
      if (response != null) {
        response.close();
      }
    }

  }

  public void seItemClicked(SeItem seItem) {
    try {
      view.setSeItemContent(seItem.getName(), new URL(seItem.getContent()
                                                            .getUrl()));
      view.setSeItemMetadata(seItem.getName(), getMetadata(seItem));
      view.setSeItemRelations(seItem.getName(), getRelations(seItem));
    } catch (MalformedURLException e) {
      Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
    }
  }

  private Map<String, Object> getMetadata(SeItem seItem) {
    return SeItemDataLoader.loadMetadata(seItem.getId()
                                               .toString());
  }

  private List<Link> getRelations(SeItem seItem) {
    return SeItemDataLoader.loadRelations(seItem.getId()
                                                .toString());
  }

}
