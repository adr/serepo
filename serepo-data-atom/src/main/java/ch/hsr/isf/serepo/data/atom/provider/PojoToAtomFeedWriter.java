package ch.hsr.isf.serepo.data.atom.provider;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

import org.jboss.resteasy.plugins.providers.atom.Feed;

import ch.hsr.isf.serepo.data.atom.elements.creator.FeedElementCreator;

/**
 * This class creates an Atom feed from an old plain java object (POJO). The
 * POJO class type as well some of its properties needs to be annotated with
 * several different atom annotations.
 *
 */
@Provider
@Produces("application/atom+*")
public class PojoToAtomFeedWriter implements MessageBodyWriter<Object> {

	@Context
	protected Providers providers;

	public PojoToAtomFeedWriter() {
	}

	@Override
	public long getSize(Object object, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return true;
	}

	@Override
	public void writeTo(Object object, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
			throws IOException, WebApplicationException {

		try {
			Feed feed = new FeedElementCreator().create(object);
			providers.getMessageBodyWriter(Feed.class, genericType, annotations, mediaType).writeTo(feed, type,
					genericType, annotations, mediaType, httpHeaders, entityStream);
		} catch (Exception e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}

	}

}
