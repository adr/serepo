package ch.hsr.isf.serepo.data.atom.elements.creator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jboss.resteasy.plugins.providers.atom.Content;
import org.jboss.resteasy.plugins.providers.atom.Entry;

import ch.hsr.isf.serepo.data.atom.annotations.Annotations;
import ch.hsr.isf.serepo.data.atom.annotations.AtomEntry;
import ch.hsr.isf.serepo.data.atom.annotations.AtomId;
import ch.hsr.isf.serepo.data.atom.annotations.AtomLink;
import ch.hsr.isf.serepo.data.atom.annotations.AtomPerson;
import ch.hsr.isf.serepo.data.atom.annotations.AtomTitle;
import ch.hsr.isf.serepo.data.atom.annotations.AtomUpdated;
import ch.hsr.isf.serepo.data.atom.annotations.AtomEntry.AtomContent;

public class EntryElementCreator extends AbstractAtomElementListCreator<Entry> {

	private LinkElementCreator linkCreator = new LinkElementCreator();
	private PersonElementCreator personCreator = new PersonElementCreator();
	private ContentElementCreator contentCreator = new ContentElementCreator();

	private Map<Class<? extends Annotation>, Boolean> mapAnnotationPresent = new HashMap<>();

	public EntryElementCreator() {
	}

	@Override
	protected Entry createElement(Object object) throws Exception {

		Annotations.throwExceptionIfNotPresent(object, AtomEntry.class);

		mapAnnotationPresent.clear();
		mapAnnotationPresent.put(AtomId.class, false);
		mapAnnotationPresent.put(AtomUpdated.class, false);
		mapAnnotationPresent.put(AtomTitle.class, false);
		mapAnnotationPresent.put(AtomLink.class, false);
		mapAnnotationPresent.put(AtomPerson.class, false);

		Entry entry = new Entry();

		// create a list with all superclasses of this class
		Class<?> currentClass = object.getClass();
		List<Class<?>> inheritanceList = new ArrayList<>();
		while (currentClass != null) {
			inheritanceList.add(currentClass);
			currentClass = currentClass.getSuperclass();
		}
		Collections.reverse(inheritanceList);
		for (Class<?> clazz : inheritanceList) {
			fillEntry(entry, clazz, object);
		}

		for (Map.Entry<Class<? extends Annotation>, Boolean> annotationEntry : mapAnnotationPresent.entrySet()) {
			if (!annotationEntry.getValue()) {
				String message = String.format("Annotation '%s' must be present on the class '%s'!",
						annotationEntry.getKey().getSimpleName(), object.getClass().getName());
				throw new RuntimeException(message);
			}
		}

		return entry;

	}

	@SuppressWarnings("unchecked")
	private void fillEntry(Entry entry, Class<?> currentClassForFields, Object object) throws Exception {
		for (Field field : currentClassForFields.getDeclaredFields()) {
			field.setAccessible(true);
			if (field.isAnnotationPresent(AtomId.class)) {
				mapAnnotationPresent.put(AtomId.class, true);
				Object id = field.get(object);
				URI uri = null;
				if (String.class == id.getClass()) {
					uri = new URI((String) id);
				} else if (URI.class == id.getClass()) {
					uri = (URI) id;
				}
				entry.setId(uri);
			} else if (field.isAnnotationPresent(AtomTitle.class)) {
				mapAnnotationPresent.put(AtomTitle.class, true);
				AtomTitle atomTitle = field.getAnnotation(AtomTitle.class);
				String title = String.format("%s%s%s", atomTitle.prefix(), field.get(object).toString(),
						atomTitle.suffix());
				entry.setTitle(title);
			} else if (field.isAnnotationPresent(AtomUpdated.class)) {
				mapAnnotationPresent.put(AtomUpdated.class, true);
				entry.setUpdated((Date) field.get(object));
			} else if (field.isAnnotationPresent(AtomEntry.AtomSummary.class)) {
				mapAnnotationPresent.put(AtomEntry.AtomSummary.class, true);
				entry.setSummary((String) field.get(object));
			} else if (field.isAnnotationPresent(AtomEntry.AtomContent.class)) {
				mapAnnotationPresent.put(AtomEntry.AtomContent.class, true);
				Content content = null;
				if (field.isAnnotationPresent(AtomContent.Src.class)) {
					content = new Content();
					URI src = null;
					if (field.get(object).getClass() == URI.class) {
						src = (URI) field.get(object);
					} else if (field.get(object) != null) {
						src = new URI(field.get(object).toString());
					} else {
						String message = String.format("The field '%s' must not be null", field.getName());
						throw new Exception(message);
					}
					content.setSrc(src);
					if (field.isAnnotationPresent(AtomContent.MediaType.class)) {
						content.setType(MediaType.valueOf(field.getAnnotation(AtomContent.MediaType.class).value()));
					} else {
						String message = String.format("The annotation '%s' is not present at the field '%s'",
								AtomContent.MediaType.class.getName(), field.getName());
						throw new Exception(message);
					}
				} else if (field.isAnnotationPresent(AtomContent.Text.class) && field.isAnnotationPresent(AtomContent.MediaType.class)) {
					content = contentCreator.create(object, field);
				} else {
					content = contentCreator.create(field.get(object));
				}
				entry.setContent(content);
			} else if (field.isAnnotationPresent(AtomPerson.class)) {
				mapAnnotationPresent.put(AtomPerson.class, true);
				entry.getAuthors().addAll(personCreator.create(field.get(object)));
			} else if (field.isAnnotationPresent(AtomLink.class)) {
				mapAnnotationPresent.put(AtomLink.class, true);
				entry.getLinks().addAll(linkCreator.create(field.get(object)));
			} else if (field.isAnnotationPresent(AtomEntry.AtomJAXBContent.class)) {
				mapAnnotationPresent.put(AtomEntry.AtomJAXBContent.class, true);
				Object jaxbObject = null;
				if (field.isAnnotationPresent(XmlJavaTypeAdapter.class)) {
					jaxbObject = field.getAnnotation(XmlJavaTypeAdapter.class).value().newInstance()
							.marshal(field.get(object));
				} else {
					jaxbObject = field.get(object);
				}
				entry.setAnyOtherJAXBObject(jaxbObject);
			}
		}
	}

}
