package ch.hsr.isf.serepo.rest;

import java.io.IOException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class IOExceptionMapper implements ExceptionMapper<IOException> {

	public IOExceptionMapper() {
	}

	private static Logger logger = LoggerFactory.getLogger(IOExceptionMapper.class);
	
	@Override
	public Response toResponse(IOException exception) {
		logger.debug("An IO error occured.", exception);
		return Response.status(Status.INTERNAL_SERVER_ERROR).build();
	}

}
