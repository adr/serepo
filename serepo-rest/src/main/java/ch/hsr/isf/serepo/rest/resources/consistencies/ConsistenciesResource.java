package ch.hsr.isf.serepo.rest.resources.consistencies;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ch.hsr.isf.serepo.data.restinterface.common.Link;
import ch.hsr.isf.serepo.data.restinterface.common.User;
import ch.hsr.isf.serepo.data.restinterface.consistency.relation.RelationInconsistency;
import ch.hsr.isf.serepo.data.restinterface.consistency.relation.RelationStatus;
import ch.hsr.isf.serepo.git.error.GitCommandException;
import ch.hsr.isf.serepo.relations.RelationsFile;
import ch.hsr.isf.serepo.relations.RelationsFileReader;
import ch.hsr.isf.serepo.relations.check.CheckResult;
import ch.hsr.isf.serepo.relations.check.CheckResult.Inconsistency;
import ch.hsr.isf.serepo.relations.check.RelationConsistencyChecker;
import ch.hsr.isf.serepo.rest.resources.Resource;
import ch.hsr.isf.serepo.rest.resources.commits.CommitsResource;
import ch.hsr.isf.serepo.rest.resources.repository.RepositoryResource;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api
@Path(ConsistenciesResource.PATH)
public class ConsistenciesResource extends Resource {

  public static final String PATH = CommitsResource.PATH + "/{" + CommitsResource.PARAM_COMMIT_ID + "}/consistencies";
  
  @ApiOperation(value = "Check relations of a specific commit.", notes = "Use this call to check the relations of a specific commit within a specific repository.", nickname = "checkRelations")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "If the repository was successfully deleted.", response = RelationStatus.class)
      , @ApiResponse(code = 404, message = "If the given repository or the given commitId does not exist.")
  })
  @GET
  @Path("/relations")
  public Response relations(@PathParam(RepositoryResource.PARAM_REPOSITORY) String repositoryName, @PathParam(CommitsResource.PARAM_COMMIT_ID) String commitId) throws IOException, GitCommandException {
    
    File repositoryDir = Paths.get(getApp().getRepositoriesDir().getAbsolutePath(), repositoryName + ".git").toFile();
    if (!repositoryDir.isDirectory()) {
      String msg = String.format("The repository '%s' could not be found!", repositoryName);
      return Response.status(Status.NOT_FOUND)
                     .entity(msg)
                     .type(MediaType.TEXT_PLAIN_TYPE)
                     .build();
    }
    
    RelationsFileReader relationsFileReader = new RelationsFileReader();
    RelationsFile relationsFile = relationsFileReader.read(getApp().getGlobalRelationDefinitionFile());
    
    RelationConsistencyChecker relationConsistencyChecker = new RelationConsistencyChecker();
    CheckResult checkResult = relationConsistencyChecker.check(repositoryDir, commitId, relationsFile);
    
    RelationStatus relationStatus = new RelationStatus();
    relationStatus.setId(getUriInfo().getAbsolutePath());
    relationStatus.setNumberOfInconsistencies(checkResult.getInconsistencies().size());
    relationStatus.getLinks().add(new Link("self", getUriInfo().getAbsolutePath().toString()));
    
    Date lastUpdate = null;
    for (Inconsistency inconsistency : checkResult.getInconsistencies()) {
      final String message = "The relation \"%s - '%s'\" within the SE-Item '%s' is inconsistent.\nReason:\n%s";
      final String inconsistencyText = String.format(message, inconsistency.getType(), inconsistency.getUrl(), inconsistency.getFile(), inconsistency.getInconsistencyReason());
      
      RelationInconsistency relationInconsistency = new RelationInconsistency();
      relationInconsistency.setId(getUriInfo().getAbsolutePath());
      User author = new User(inconsistency.getCommitLog().getAuthor().getName(), inconsistency.getCommitLog().getAuthor().getEmail());
      relationInconsistency.setAuthor(author);
      relationInconsistency.setUpdate(inconsistency.getCommitLog().getWhen());
      relationInconsistency.setInconsistency(inconsistencyText);
      relationInconsistency.setSeItem(inconsistency.getFile());
      relationStatus.getInconsistencies().add(relationInconsistency);
      
      if (lastUpdate == null || lastUpdate.before(inconsistency.getCommitLog().getWhen())) {
        lastUpdate = inconsistency.getCommitLog().getWhen();
      }
      
    }
    relationStatus.setUpdated(lastUpdate);
    
    return Response.ok(relationStatus).build();
    
  }
  
}
