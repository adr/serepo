package ch.hsr.isf.serepo.rest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class ExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<Exception> {

  private static final Logger logger = LoggerFactory.getLogger(ExceptionMapper.class);
  
  public ExceptionMapper() {
  }

  @Override
  public Response toResponse(Exception exception) {
    Response response = null;
    logger.debug(String.format("An internal exception occured."), exception);
    response = Response.status(Status.INTERNAL_SERVER_ERROR)
                       .entity(exception.getMessage())
                       .type(MediaType.TEXT_PLAIN_TYPE)
                       .build();
    return response;
  }

}
