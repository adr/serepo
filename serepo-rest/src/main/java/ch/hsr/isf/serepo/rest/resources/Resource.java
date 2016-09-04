package ch.hsr.isf.serepo.rest.resources;

import java.nio.charset.StandardCharsets;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import ch.hsr.isf.serepo.rest.SeRepoRestApplication;

@Consumes(MediaType.APPLICATION_JSON)
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_ATOM_XML})
public abstract class Resource {

  public static final String HATEOAS_PREFIX = "https://www.ifs.hsr.ch/serepo/api/rels/";
  
  @Context
  private UriInfo uriInfo;
  @Context
  private Application application;

  public Resource() {}

  protected UriInfo getUriInfo() {
    return uriInfo;
  }

  protected SeRepoRestApplication getApp() {
    return (SeRepoRestApplication) application;
  }

  protected Response internalServerError(String message) {
    return Response.status(Status.INTERNAL_SERVER_ERROR)
                   .entity(message)
                   .type(MediaType.TEXT_PLAIN_TYPE)
                   .encoding(StandardCharsets.UTF_8.name())
                   .build();
  }

}
