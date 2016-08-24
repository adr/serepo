package ch.hsr.isf.serepo.client.webapp.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import ch.hsr.isf.serepo.data.restinterface.common.Link;
import ch.hsr.isf.serepo.data.restinterface.metadata.MetadataContainer;
import ch.hsr.isf.serepo.data.restinterface.seitem.RelationContainer;

public class SeItemDataLoader {

	private SeItemDataLoader() {
	}
	
	public static List<Link> loadRelations(String seItemUrl) {
		String uri = String.format("%s?relations", seItemUrl);
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(uri);
        Response response = null;
        try {
          response = target.request().accept(MediaType.APPLICATION_JSON_TYPE).get();
          if (response.getStatus() == Status.OK.getStatusCode()) {
            RelationContainer relationContainer = response.readEntity(new GenericType<RelationContainer>(RelationContainer.class));
            return relationContainer.getEntry().getLinks();
          } else {
          	Notification.show(String.format("Error while loading metadata for SE-Item."), response.getStatusInfo().getReasonPhrase(), Type.ERROR_MESSAGE);
          }
        } finally {
          if (response != null) {
            response.close();
          }
        }
        return new ArrayList<>();
	}
	
	public static Map<String,Object> loadMetadata(String seItemUrl) {
		String uri = String.format("%s?metadata", seItemUrl);
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(uri);
        Response response = null;
        try {
          response = target.request().accept(MediaType.APPLICATION_JSON_TYPE).get();
          if (response.getStatus() == Status.OK.getStatusCode()) {
            MetadataContainer metadataContainer = response.readEntity(new GenericType<MetadataContainer>(MetadataContainer.class));
            return metadataContainer.getMetadata().getMap();
          } else {
          	Notification.show(String.format("Error while loading metadata for SE-Item."), response.getStatusInfo().getReasonPhrase(), Type.ERROR_MESSAGE);
          }
        } finally {
          if (response != null) {
            response.close();
          }
        }
        return new TreeMap<>();
	}
	
}
