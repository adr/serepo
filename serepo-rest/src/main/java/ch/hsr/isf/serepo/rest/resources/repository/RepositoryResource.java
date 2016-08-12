package ch.hsr.isf.serepo.rest.resources.repository;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ch.hsr.isf.serepo.commons.FileUtils;
import ch.hsr.isf.serepo.data.restinterface.repository.Repository;
import ch.hsr.isf.serepo.git.error.GitCommandException;
import ch.hsr.isf.serepo.rest.resources.Resource;
import ch.hsr.isf.serepo.rest.resources.repos.ReposResource;
import ch.hsr.isf.serepo.rest.resources.repos.Repositories;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api
@Path(RepositoryResource.PATH)
public class RepositoryResource extends Resource {

  public static final String PATH = ReposResource.PATH + "/{" + RepositoryResource.PARAM_REPOSITORY + "}";
  public static final String PARAM_REPOSITORY = "repository";

  @ApiOperation(value = "Get specific repository.", notes = "Use this call to get information about one specific repository.", nickname = "getRepository")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Returns a repository object.", response = Repository.class)
      , @ApiResponse(code = 404, message = "If the repository does not exist.")
  })
  @GET
  public Response get(@PathParam(PARAM_REPOSITORY) String repositoryName)
      throws GitCommandException, IOException, URISyntaxException {

    File repositoryDir = getRepository(repositoryName);
    if (repositoryDir.isDirectory()) {
      Repository repository = Repositories.repository(getUriInfo().getBaseUri(), repositoryDir);
      return Response.ok(repository)
                     .build();
    } else {
      return Response.status(Status.NOT_FOUND)
                     .build();
    }

  }

  @ApiOperation(value = "Delete a specific repository.", notes = "Use this call to delete one specific repository.", nickname = "deleteRepository")
  @ApiResponses(value = {
      @ApiResponse(code = 201, message = "If the repository was successfully deleted.")
      , @ApiResponse(code = 404, message = "If the given repository does not exist.")
  })
  @DELETE
  public Response delete(@PathParam(PARAM_REPOSITORY) String repositoryName) throws IOException {
    File repositoryDir = getRepository(repositoryName);
    if (repositoryDir.isDirectory()) {
      FileUtils.delete(repositoryDir);
      return Response.noContent()
                     .build();
    } else {
      return Response.status(Status.NOT_FOUND)
                     .build();
    }
  }

  private File getRepository(String repositoryName) {
    return Paths.get(getApp().getRepositoriesDir()
                             .getAbsolutePath(),
        repositoryName + ".git")
                .toFile();
  }

}
