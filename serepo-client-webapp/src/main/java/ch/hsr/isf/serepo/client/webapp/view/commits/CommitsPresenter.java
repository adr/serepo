package ch.hsr.isf.serepo.client.webapp.view.commits;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import ch.hsr.isf.serepo.client.webapp.model.Settings;
import ch.hsr.isf.serepo.data.restinterface.commit.CommitContainer;

public class CommitsPresenter {

	private ICommitsView view;

	public CommitsPresenter(ICommitsView view) {
		this.view = view;
	}
	
	public void load(String repository) {
		String uri = String.format("%s/repos/%s/commits", Settings.getFromSession().getSerepoUrl(), repository);
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(uri);
        Response response = null;
        try {
          response = target.request().accept(MediaType.APPLICATION_JSON_TYPE).get();
          if (response.getStatus() == Status.OK.getStatusCode()) {
          	CommitContainer commitContainer = response.readEntity(CommitContainer.class);
          	view.setCommits(commitContainer.getCommits());
          } else {
          	Notification.show(String.format("Error while loading commits for repository '%s'", repository), response.getStatusInfo().getReasonPhrase(), Type.ERROR_MESSAGE);
          }
        } finally {
          if (response != null) {
            response.close();
          }
        }
	}
	
}
