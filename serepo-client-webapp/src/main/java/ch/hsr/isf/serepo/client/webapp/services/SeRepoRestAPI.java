package ch.hsr.isf.serepo.client.webapp.services;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ch.hsr.isf.serepo.client.webapp.model.Settings;
import ch.hsr.isf.serepo.data.restinterface.commit.CommitContainer;
import ch.hsr.isf.serepo.data.restinterface.repository.RepositoryContainer;

public class SeRepoRestAPI {

  private SeRepoRestAPI() {};
  
  public static RepositoryContainer getRepositories() {
    String uri = String.format("%s/repos", Settings.getFromSession().getSerepoUrl());
    return request(RepositoryContainer.class, uri);
  }

  public static CommitContainer getCommitsForRepository(String repository) {
    String uri = String.format("%s/repos/%s/commits", Settings.getFromSession().getSerepoUrl(), repository);
    return request(CommitContainer.class, uri);
  }
  
  private static <T> T request(Class<T> type, String uri) {
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(uri);
    
    Response response = null;
    try {
      response = target.request()
                       .accept(MediaType.APPLICATION_JSON_TYPE)
                       .get();
      return response.readEntity(type);
    } finally {
      if (response != null) {
        response.close();
      }
    }
  }
  
}
