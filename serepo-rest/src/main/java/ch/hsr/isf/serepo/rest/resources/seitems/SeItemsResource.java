package ch.hsr.isf.serepo.rest.resources.seitems;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import ch.hsr.isf.serepo.data.restinterface.metadata.MetadataContainer;
import ch.hsr.isf.serepo.data.restinterface.seitem.SeItemContainer;
import ch.hsr.isf.serepo.git.error.GitCommandException;
import ch.hsr.isf.serepo.relations.RelationsFile;
import ch.hsr.isf.serepo.relations.RelationsFileReader;
import ch.hsr.isf.serepo.rest.resources.Resource;
import ch.hsr.isf.serepo.rest.resources.commits.CommitsResource;
import ch.hsr.isf.serepo.rest.resources.repository.RepositoryResource;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api
@Path(SeItemsResource.PATH)
public class SeItemsResource extends Resource {

  public static final String PATH =
      CommitsResource.PATH + "/{" + CommitsResource.PARAM_COMMIT_ID + "}/seitems";

  @ApiOperation(value = "Get all SE-Items.", notes = "Use this call to get all SE-Items of a specific commitId within a repository.", nickname = "getSeItems")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Returns a SE-Item container object.", response = SeItemContainer.class)
      , @ApiResponse(code = 404, message = "If the given repository or commitId does not exist.")
  })
  @GET
  public Response get(@PathParam(RepositoryResource.PARAM_REPOSITORY) String repositoryName,
      @PathParam(CommitsResource.PARAM_COMMIT_ID) String commitId) throws Throwable {
    return SeItems.createResponse(getApp().getRepositoriesDir(), repositoryName, commitId,
        getUriInfo().getBaseUri());
  }

  @ApiOperation(value = "Get a specific SE-Item.", notes = "Use this call to get the content of one specific SE-Item.", nickname = "getSeItemContent")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Returns the content of the SE-Item")
      , @ApiResponse(code = 307, message = "If the content of the SE-Item is external, the new target URL is returned.")
      , @ApiResponse(code = 404, message = "If the given SE-Item, repository or commitId does not exist.")
  })
  @GET
  @Path("/{seitem:.*}")
  public Response get(@PathParam(RepositoryResource.PARAM_REPOSITORY) String repositoryName,
      @PathParam(CommitsResource.PARAM_COMMIT_ID) String commitId,
      @PathParam("seitem") String seitem) throws IOException, GitCommandException {
    return SeItems.getContent(getApp().getRepositoriesDir(), repositoryName, commitId, seitem, getApp().getGlobalRelationDefinitionFile());
  }

  @ApiOperation(value = "Get a metadata/relations of a SE-Item.", notes = "Use this call to get metadata or relations of one specific SE-Item.", nickname = "getSeItemMetadataRelations")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Returns metadata about the SE-Item", response = MetadataContainer.class)
//      , @ApiResponse(code = 200, message = "Returns relations of the SE-Item", response = RelationContainer.class) // swagger does currently not support multiple response types with the same code
      , @ApiResponse(code = 404, message = "If the given SE-Item, repository or commitId does not exist.")
  })
  @GET
  @Path("/{seitem:.*}")
  public Response getData(@PathParam(RepositoryResource.PARAM_REPOSITORY) String repositoryName,
      @PathParam(CommitsResource.PARAM_COMMIT_ID) String commitId,
      @PathParam("seitem") String seitem, @QueryParam("metadata") String queryMetadata,
      @QueryParam("relations") String queryRelations)
      throws IOException, GitCommandException, URISyntaxException {

    if (queryMetadata != null) {
      return SeItems.getMetadata(getUriInfo().getAbsolutePath(), getApp().getRepositoriesDir(),
          repositoryName, commitId, seitem);
    } else if (queryRelations != null) {
      RelationsFileReader relationsFileReader = new RelationsFileReader();
      RelationsFile relationsFile = relationsFileReader.read(getApp().getGlobalRelationDefinitionFile());
      return SeItems.getRelations(getUriInfo().getAbsolutePath(), getApp().getRepositoriesDir(),
          repositoryName, commitId, seitem, relationsFile);
    } else {
      return get(repositoryName, commitId, seitem);
    }

  }

}
