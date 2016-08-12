package ch.hsr.isf.serepo.client.webapp.view.consistencies.relations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import ch.hsr.isf.serepo.client.webapp.model.Settings;
import ch.hsr.isf.serepo.data.restinterface.consistency.relation.RelationInconsistency;
import ch.hsr.isf.serepo.data.restinterface.consistency.relation.RelationStatus;

public class RelationConsistencyPresenter {

  private IRelationConsistencyView view;

  public RelationConsistencyPresenter(IRelationConsistencyView view) {
    this.view = view;
  }

  public byte[] exportAsCsv(List<RelationInconsistency> inconsistencies) throws IOException {
    
    CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader("SE-Item", "Inconsistency").withTrim();
    StringBuilder csv = new StringBuilder();
    try (CSVPrinter printer = new CSVPrinter(csv, csvFormat)) {
      for (RelationInconsistency inconsistency : inconsistencies) {
        printer.printRecord(inconsistency.getSeItem(), inconsistency.getInconsistency());
      }
    }
    return csv.toString().getBytes();
    
  }

  public void loadInconsistencies(String repository, String commitId) {
    view.setInconsistencies(getInconsistencies(repository, commitId));
  }

  private List<RelationInconsistency> getInconsistencies(String repository, String commitId) {
    List<RelationInconsistency> list = null;
    String uri =
        String.format("%s/repos/%s/commits/%s/consistencies/relations", Settings.getFromSession()
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
        RelationStatus relationStatus = response.readEntity(RelationStatus.class);
        list = relationStatus.getInconsistencies();
      } else {
        list = new ArrayList<>();
        Notification.show(
            String.format("Error while checking consistency for repository '%s' and commit '%s'",
                repository, commitId),
            response.getStatusInfo()
                    .getReasonPhrase(),
            Type.ERROR_MESSAGE);
      }
    } finally {
      if (response != null) {
        response.close();
      }
    }
    return list;
  }

}
