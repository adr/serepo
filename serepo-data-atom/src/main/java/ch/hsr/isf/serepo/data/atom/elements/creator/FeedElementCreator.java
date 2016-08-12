package ch.hsr.isf.serepo.data.atom.elements.creator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jboss.resteasy.plugins.providers.atom.Feed;

import ch.hsr.isf.serepo.data.atom.annotations.Annotations;
import ch.hsr.isf.serepo.data.atom.annotations.AtomEntry;
import ch.hsr.isf.serepo.data.atom.annotations.AtomFeed;
import ch.hsr.isf.serepo.data.atom.annotations.AtomId;
import ch.hsr.isf.serepo.data.atom.annotations.AtomLink;
import ch.hsr.isf.serepo.data.atom.annotations.AtomPerson;
import ch.hsr.isf.serepo.data.atom.annotations.AtomTitle;
import ch.hsr.isf.serepo.data.atom.annotations.AtomUpdated;

public class FeedElementCreator implements AtomElementCreator<Feed> {

	private EntryElementCreator entryCreator = new EntryElementCreator();
	private LinkElementCreator linkCreator = new LinkElementCreator();
	private PersonElementCreator personCreator = new PersonElementCreator();

	public FeedElementCreator() {
	}

	@Override
	public Feed create(Object object) throws Exception {

		Feed feed = createElement(object);
		if (object.getClass().isAnnotationPresent(AtomEntry.class)) {
			// create entry from the object itself
			feed.getEntries().addAll(entryCreator.create(object));
		} else {
			// create entry from annotated property field
			for (Field field : object.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				if (field.isAnnotationPresent(AtomEntry.class)) {
					feed.getEntries().addAll(entryCreator.create(field.get(object)));
					break;
				}
			}
		}

		return feed;

	}

	private Feed createElement(Object object) throws Exception {

		Annotations.throwExceptionIfNotPresent(object, AtomFeed.class);

		Map<Class<? extends Annotation>, Boolean> mapAnnotationPresent = new HashMap<>();
		mapAnnotationPresent.put(AtomId.class, false);
		mapAnnotationPresent.put(AtomUpdated.class, false);
		mapAnnotationPresent.put(AtomTitle.class, false);
		mapAnnotationPresent.put(AtomLink.class, false);

		Feed feed = new Feed();
		for (Field field : object.getClass().getDeclaredFields()) {
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
				feed.setId(uri);
			} else if (field.isAnnotationPresent(AtomTitle.class)) {
				mapAnnotationPresent.put(AtomTitle.class, true);
				AtomTitle atomTitle = field.getAnnotation(AtomTitle.class);
				String title = String.format("%s%s%s", atomTitle.prefix(), field.get(object).toString(),
						atomTitle.suffix());
				feed.setTitle(title);
			} else if (field.isAnnotationPresent(AtomUpdated.class)) {
				mapAnnotationPresent.put(AtomUpdated.class, true);
				feed.setUpdated((Date) field.get(object));
			} else if (field.isAnnotationPresent(AtomLink.class)) {
				mapAnnotationPresent.put(AtomLink.class, true);
				feed.getLinks().addAll(linkCreator.create(field.get(object)));
			} else if (field.isAnnotationPresent(AtomPerson.class)) {
                mapAnnotationPresent.put(AtomPerson.class, true);
                feed.getAuthors().addAll(personCreator.create(field.get(object)));
            }
		}

		for (Map.Entry<Class<? extends Annotation>, Boolean> entry : mapAnnotationPresent.entrySet()) {
			if (!entry.getValue()) {
				String message = String.format("Annotation '%s' must be present on the class '%s'!",
						entry.getKey().getSimpleName(), object.getClass().getName());
				throw new RuntimeException(message);
			}
		}

		return feed;

	}

}
