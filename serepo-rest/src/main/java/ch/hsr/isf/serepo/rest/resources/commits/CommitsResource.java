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
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import ch.hsr.isf.serepo.data.restinterface.commit.Commit;
import ch.hsr.isf.serepo.data.restinterface.commit.CommitContainer;
import ch.hsr.isf.serepo.git.error.GitCommandException;
import ch.hsr.isf.serepo.rest.resources.Resource;
import ch.hsr.isf.serepo.rest.resources.repository.RepositoryResource;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api
@Path(CommitsResource.PATH)
public class CommitsResource extends Resource {

  public static final String PATH = RepositoryResource.PATH + "/commits";
  public static final String PARAM_COMMIT_ID = "commitId";

  @ApiOperation(value = "Get all commits from a repository.", notes = "Use this call to get all commits of a specific repository.", nickname = "getCommits")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Returns a commit container object.", response = CommitContainer.class)
      , @ApiResponse(code = 404, message = "If the given repository does not exist.")
  })
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

  @ApiOperation(value = "Get a specific commit from a repository.", notes = "Use this call to get one specific commit of a specific repository.", nickname = "getCommit")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Returns a commit object.", response = Commit.class)
      , @ApiResponse(code = 404, message = "If the given repository or commit id does not exist.")
  })
  @GET
  @Path("{" + PARAM_COMMIT_ID + "}")
  public Response get(@PathParam(RepositoryResource.PARAM_REPOSITORY) String repositoryName,
      @PathParam(PARAM_COMMIT_ID) String commitId)
      throws URISyntaxException, IOException, GitCommandException {
    return Response.ok(Commits.commit(getUriInfo().getBaseUri(), getApp().getRepositoriesDir(),
        repositoryName, commitId))
                   .build();
  }

  @ApiOperation(value = "Add a new commit to a repository.", nickname = "createCommit")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "If nothing can be committed, repository is up-to-date.")
      , @ApiResponse(code = 201, message = "If the commit was successfully created.")
      , @ ApiResponse(code = 400, message = "If the post request is invalid or the JSON is missing.")
  })
  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.TEXT_PLAIN)
  public Response post(@PathParam(RepositoryResource.PARAM_REPOSITORY) String repositoryName,
      MultipartFormDataInput multipart)
      throws IOException, GitCommandException, URISyntaxException {

    NewCommit newCommit =
        new NewCommit(getApp().getRepositoriesDir(), getApp().getRepositoriesTempWorkingDir(), getApp().getSolrUrl());
    return newCommit.create(repositoryName, multipart);

  }

}
