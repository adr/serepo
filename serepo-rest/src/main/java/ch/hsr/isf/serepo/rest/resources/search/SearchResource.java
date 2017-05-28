package ch.hsr.isf.serepo.rest.resources.search;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import ch.hsr.isf.serepo.commons.Uri;
import ch.hsr.isf.serepo.data.restinterface.search.SearchContainer;
import ch.hsr.isf.serepo.rest.resources.Resource;
import ch.hsr.isf.serepo.search.SearchConfig;
import ch.hsr.isf.serepo.search.index.DeleteException;
import ch.hsr.isf.serepo.search.index.DocumentDeleter;
import ch.hsr.isf.serepo.search.request.FilterQueries;
import ch.hsr.isf.serepo.search.request.SearchException;
import ch.hsr.isf.serepo.search.request.SearchResult;
import ch.hsr.isf.serepo.search.request.Searcher;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api
@Path(SearchResource.PATH)
public class SearchResource extends Resource {

  public static final String PATH = "/search";

  private static final Logger logger = LoggerFactory.getLogger(SearchResource.class);

  @ApiOperation(value = "Search within SE-Repo.", notes = "Use this call to do easy and complex search & filters within SE-Repo.", nickname = "search")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Returns a search container object.", response = SearchContainer.class)
      , @ApiResponse(code = 400, message = "If a required parameter is missing.")
      , @ApiResponse(code = 500, message = "If the query cannot be understood or if something is wrong with the underlying search engine.")
  })
  @GET
  public Response get(@QueryParam("repository") String repository,
      @QueryParam("commitId") String commitId,
      @ApiParam(value = "To specify the documenttype (metadata, content)", allowableValues = "metadata, content",
          required = false) @QueryParam("in") String in,
      @ApiParam(value = "Query", required = true) @QueryParam("q") String q)
      throws URISyntaxException {

    Response response = null;

    Searcher searchRequest = new Searcher(getApp().getSolrUrl());

    List<String> filterQueries = new ArrayList<>();

    if (repository != null) {
      filterQueries.add(
          FilterQueries.create(SearchConfig.Fields.REPOSITORY, emptyToWildcard(repository)));
    }
    if (commitId != null) {
      filterQueries.add(
          FilterQueries.create(SearchConfig.Fields.COMMIT_ID, emptyToWildcard(commitId)));
    }
    if (in != null) {
      filterQueries.add(
          FilterQueries.create(SearchConfig.Fields.SEITEM_DOCUMENTTYPE, emptyToWildcard(in.toUpperCase())));
    }

    if (q == null) {
      return Response.status(Status.BAD_REQUEST)
                     .entity("q (Query) parameter is missing!")
                     .type(MediaType.TEXT_PLAIN_TYPE)
                     .build();
    }

    try {
      List<SearchResult> searchResults =
          searchRequest.search(q, filterQueries.toArray(new String[filterQueries.size()]));
      SearchContainer searchContainer = new SearchContainer();
      for (SearchResult searchResult : searchResults) {
        URI uri = Uri.of(getUriInfo().getBaseUri(), "repos", searchResult.getRepository(),
            "commits", searchResult.getCommitId(), "seitems", searchResult.getId());
        ch.hsr.isf.serepo.data.restinterface.search.SearchResult searchResultApi =
            new ch.hsr.isf.serepo.data.restinterface.search.SearchResult();
        searchResultApi.setRepository(searchResult.getRepository());
        searchResultApi.setCommitId(searchResult.getCommitId());
        searchResultApi.setSeItemUri(uri.toString());
        searchContainer.getSearchResult()
                       .add(searchResultApi);
      }
      response = Response.status(Status.OK)
                         .entity(searchContainer)
                         .build();
    } catch (SearchException e) {
      logger.error("An error occured while searching.", e);
      String reason = null;
      if (e.getCause() != null && !Strings.isNullOrEmpty(e.getCause()
                                                          .getMessage())) {
        reason = String.format("\nReason: %s", e.getCause()
                                                .getMessage());
      } else {
        reason = "";
      }
      String message = String.format("%s%s", e.getMessage(), reason);
      response = Response.status(Status.INTERNAL_SERVER_ERROR)
                         .entity(message)
                         .type(MediaType.TEXT_PLAIN_TYPE)
                         .build();
    }

    return response;

  }

  @ApiOperation(value = "Delete search index.", notes = "Use this call to delete the search index for a specific repository.", nickname = "deleteSearch")
  @ApiResponses(value = {
      @ApiResponse(code = 204, message = "If the search index was successfully deleted.")
      , @ApiResponse(code = 400, message = "If required parameters are missing.")
  })
  @DELETE
  public Response delete(@ApiParam(required = true) @QueryParam("repository") String repository) {

    Response response = null;
    if (!Strings.isNullOrEmpty(repository)) {
      try (DocumentDeleter deleter = new DocumentDeleter(getApp().getSolrUrl())) {
        deleter.deleteForRepository(repository);
        response = Response.status(Status.NO_CONTENT)
                           .build();
      } catch (DeleteException e) {
        logger.error("An error occured while deleting search index.", e);
        response = Response.status(Status.INTERNAL_SERVER_ERROR)
                           .build();
      }
    } else {
      response = Response.status(Status.BAD_REQUEST)
                         .entity("No repository defined!")
                         .type(MediaType.TEXT_PLAIN_TYPE)
                         .build();
    }

    return response;

  }

  private String emptyToWildcard(String value) {
    if (value.isEmpty()) {
      return "*";
    }
    return value;
  }

}
