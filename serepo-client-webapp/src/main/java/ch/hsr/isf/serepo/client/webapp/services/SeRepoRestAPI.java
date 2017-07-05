package ch.hsr.isf.serepo.client.webapp.services;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ch.hsr.isf.serepo.client.webapp.model.Settings;
import ch.hsr.isf.serepo.data.restinterface.commit.CommitContainer;
import ch.hsr.isf.serepo.data.restinterface.repository.RepositoryContainer;
import ch.hsr.isf.serepo.data.restinterface.search.SearchContainer;

public class SeRepoRestAPI {
  
  public static class RestfulApiException extends RuntimeException {
    private static final long serialVersionUID = 7815841713880704891L;

    public RestfulApiException(String message) {
      super(message);
    }

    public RestfulApiException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  private SeRepoRestAPI() {};
  
  public static RepositoryContainer getRepositories() {
    String uri = String.format("%s/repos", Settings.getFromSession().getSerepoUrl());
    return readEntity(RepositoryContainer.class, uri);
  }

  public static CommitContainer getCommitsForRepository(String repository) {
    String uri = String.format("%s/repos/%s/commits", Settings.getFromSession().getSerepoUrl(), repository);
    return readEntity(CommitContainer.class, uri);
  }
  
  public static SearchContainer search(String query) {
    String uri = String.format("%s/search", Settings.getFromSession().getSerepoUrl());
    WebTarget target = createTarget(uri).queryParam("q", query);
    return readEntity(SearchContainer.class, target);
  }
  
  private static <T> T readEntity(Class<T> type, String uri) {
    return readEntity(type, createTarget(uri));
  }

  private static <T> T readEntity(Class<T> type, WebTarget target) {
    Response respone = getResponse(target);
    try {
      if (respone.getStatus() == Status.OK.getStatusCode()) {
        return respone.readEntity(type);
      } else {
        String msg;
        try {
          msg = respone.readEntity(String.class);
        } catch (Throwable e) {
          msg = "RESTful HTTP API error!";
        }
        throw new RestfulApiException(msg);
      }
    } finally {
      respone.close();
    }
  }
  
  private static Response getResponse(WebTarget target) {
    return target.request()
                 .accept(MediaType.APPLICATION_JSON_TYPE)
                 .get();
  }

  private static WebTarget createTarget(String uri) {
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(uri);
    return target;
  }
  
}
