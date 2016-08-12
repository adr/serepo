package ch.hsr.isf.serepo.rest.resources.repos;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import ch.hsr.isf.serepo.data.restinterface.repository.CreateRepository;
import ch.hsr.isf.serepo.data.restinterface.repository.RepositoryContainer;
import ch.hsr.isf.serepo.git.error.GitCommandException;
import ch.hsr.isf.serepo.rest.resources.Resource;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api
@Path(ReposResource.PATH)
public class ReposResource extends Resource {

  public static final String PATH = "/repos";

  @ApiOperation(value = "Get all repositories.", notes = "Use this call to get all repositories within SE-Repo.", nickname = "getRepositories")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Returns a repository container object.", response = RepositoryContainer.class)
  })
  @GET
  public Response get() throws GitCommandException, IOException, URISyntaxException {
    RepositoryContainer repositoryContainer =
        Repositories.container(getUriInfo().getBaseUri(), getApp().getRepositoriesDir());
    return Response.ok(repositoryContainer)
                   .build();
  }

  @ApiOperation(value = "Add a new repository to SE-Repo.", nickname = "createRepository")
  @ApiResponses(value = {
      @ApiResponse(code = 201, message = "If the repository was successfully created.")
      , @ ApiResponse(code = 409, message = "If a repository with the same name already exists.")
  })
  @POST
  public Response post(@ApiParam(value = "Repository object to create a new repository.", required = true) CreateRepository repository) throws IOException, GitCommandException {
    return Repositories.create(repository, getApp().getRepositoriesDir());
  }

}
