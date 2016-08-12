package ch.hsr.isf.serepo.data.atom.elements.creator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.plugins.providers.atom.Content;

import ch.hsr.isf.serepo.data.atom.annotations.Annotations;
import ch.hsr.isf.serepo.data.atom.annotations.AtomEntry;
import ch.hsr.isf.serepo.data.atom.annotations.AtomEntry.AtomContent;

public class ContentElementCreator implements AtomElementCreator<Content> {

	public ContentElementCreator() {
	}

	@Override
	public Content create(Object object) throws Exception {
		Annotations.throwExceptionIfNotPresent(object, AtomEntry.AtomContent.class);
		return create(object, object.getClass().getDeclaredFields());
	}

	public Content create(Object object, Field field) throws Exception {
		return create(object, new Field[] { field });
	}

	private Content create(Object object, Field[] fields) throws Exception {

		Map<Class<? extends Annotation>, Boolean> mapAnnotationPresent = new HashMap<>();
		mapAnnotationPresent.put(AtomContent.MediaType.class, false);

		Content content = new Content();

		boolean srcUsed = false;
		boolean textUsed = false;
		for (Field field : fields) {
			field.setAccessible(true);
			Object fieldValue = field.get(object);
			if (field.isAnnotationPresent(AtomContent.Src.class)) {
				if (fieldValue != null) {
					srcUsed = true;
					URI uri = null;
					if (fieldValue.getClass() == URI.class) {
						uri = (URI) fieldValue;
					} else if (fieldValue.getClass() == String.class) {
						uri = new URI(fieldValue.toString());
					}
					content.setSrc(uri);
				}
			}
			if (field.isAnnotationPresent(AtomContent.MediaType.class)) {
				mapAnnotationPresent.put(AtomContent.MediaType.class, true);
				MediaType mediaType = null;
				if (field.getAnnotation(AtomContent.MediaType.class).value().isEmpty()) {
					if (fieldValue != null) {
						if (fieldValue.getClass() == MediaType.class) {
							mediaType = (MediaType) fieldValue;
						} else if (fieldValue.getClass() == String.class) {
							mediaType = MediaType.valueOf(fieldValue.toString());
						} else {
							String message = String.format(
									"The field '%s' with the annotation '%s' must be of type '%s' or '%s'!",
									field.getName(), AtomContent.MediaType.class, MediaType.class, String.class);
							throw new Exception(message);
						}
					} else {
						String message = String.format("The field '%s' must not be null!", field.getName());
						throw new Exception(message);
					}
				} else {
					mediaType = MediaType.valueOf(field.getAnnotation(AtomContent.MediaType.class).value());
				}
				content.setType(mediaType);
			}
			if (field.isAnnotationPresent(AtomContent.Text.class)) {
				if (fieldValue != null) {
					textUsed = true;
					content.setText(fieldValue.toString());
				}
			}
		}

		for (Map.Entry<Class<? extends Annotation>, Boolean> annotationEntry : mapAnnotationPresent.entrySet()) {
			if (!annotationEntry.getValue()) {
				String message = String.format("Annotation '%s' must be present on the class '%s'!",
						annotationEntry.getKey().getSimpleName(), object.getClass().getName());
				throw new RuntimeException(message);
			}
		}

		if (srcUsed && textUsed) {
			String message = String.format(
					"Both annotations '%s' and '%s' are used with non null values. One of the fields must be null.",
					AtomContent.Src.class, AtomContent.Text.class);
			throw new Exception(message);
		}

		return content;

	}

}
