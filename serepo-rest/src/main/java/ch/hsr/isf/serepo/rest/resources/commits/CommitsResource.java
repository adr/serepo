package ch.hsr.isf.serepo.rest.resources.commits;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import ch.hsr.isf.serepo.git.error.GitCommandException;
import ch.hsr.isf.serepo.rest.resources.Resource;
import ch.hsr.isf.serepo.rest.resources.repository.RepositoryResource;

@Path(CommitsResource.PATH)
public class CommitsResource extends Resource {

  public static final String PATH = RepositoryResource.PATH + "/commits";
  public static final String PARAM_COMMIT_ID = "commitId";

  @GET
  public Response get(@PathParam(RepositoryResource.PARAM_REPOSITORY) String repositoryName)
      throws IOException, GitCommandException, URISyntaxException {

    File repositoryDir = Paths.get(getApp().getRepositoriesDir()
                                           .getAbsolutePath(),
        repositoryName + ".git")
                              .toFile();
    if (!repositoryDir.isDirectory()) {
      return Response.status(Status.NOT_FOUND)
                     .build();
    }

    return Response.ok(
        Commits.container(getUriInfo().getBaseUri(), getApp().getRepositoriesDir(), repositoryName))
                   .build();

  }

  @GET
  @Path("{" + PARAM_COMMIT_ID + "}")
  public Response get(@PathParam(RepositoryResource.PARAM_REPOSITORY) String repositoryName,
      @PathParam(PARAM_COMMIT_ID) String commitId)
      throws URISyntaxException, IOException, GitCommandException {
    return Response.ok(Commits.commit(getUriInfo().getBaseUri(), getApp().getRepositoriesDir(),
        repositoryName, commitId))
                   .build();
  }

  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response post(@PathParam(RepositoryResource.PARAM_REPOSITORY) String repositoryName,
      MultipartFormDataInput multipart)
      throws IOException, GitCommandException, URISyntaxException {

    NewCommit newCommit =
        new NewCommit(getApp().getRepositoriesDir(), getApp().getRepositoriesTempWorkingDir(), getApp().getSolrUrl());
    return newCommit.create(repositoryName, multipart);

  }

}
