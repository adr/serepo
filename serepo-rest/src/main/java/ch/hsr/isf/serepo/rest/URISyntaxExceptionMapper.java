package ch.hsr.isf.serepo.rest;

import java.net.URISyntaxException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class URISyntaxExceptionMapper implements ExceptionMapper<URISyntaxException> {

	public URISyntaxExceptionMapper() {
	}

	private static final Logger logger = LoggerFactory.getLogger(URISyntaxExceptionMapper.class);
	
	@Override
	public Response toResponse(URISyntaxException exception) {
		logger.debug("Malformed URI.", exception);
		return Response.status(Status.INTERNAL_SERVER_ERROR).build();
	}

}
