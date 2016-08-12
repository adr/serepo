package ch.hsr.isf.serepo.rest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.hsr.isf.serepo.git.error.GitCommandException;

@Provider
public class GitExceptionMapper implements ExceptionMapper<GitCommandException> {

  private static final Logger logger = LoggerFactory.getLogger(GitExceptionMapper.class);

  public GitExceptionMapper() {}

  @Override
  public Response toResponse(GitCommandException exception) {
    Response response = null;
    logger.debug(String.format("Error while processing repository"), exception);
    response = Response.status(Status.INTERNAL_SERVER_ERROR)
                       .entity(exception.getMessage())
                       .type(MediaType.TEXT_PLAIN_TYPE)
                       .build();
    return response;
  }

}
