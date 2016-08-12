package ch.hsr.isf.serepo.data.atom.elements.creator;

import java.lang.reflect.Field;
import java.net.URI;

import org.jboss.resteasy.plugins.providers.atom.Link;

import ch.hsr.isf.serepo.data.atom.annotations.Annotations;
import ch.hsr.isf.serepo.data.atom.annotations.AtomLink;

public class LinkElementCreator extends AbstractAtomElementListCreator<Link> {

	@Override
	protected Link createElement(Object object) throws Exception {

		Annotations.throwExceptionIfNotPresent(object, AtomLink.class);
		
		Link link = new Link();
		
		for (Field field : object.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			if (field.isAnnotationPresent(AtomLink.Title.class)) {
				link.setTitle((String) field.get(object));
			} else if (field.isAnnotationPresent(AtomLink.Rel.class)) {
				link.setRel((String) field.get(object));
			} else if (field.isAnnotationPresent(AtomLink.Href.class)) {
				link.setHref(new URI((String) field.get(object)));
			}
		}
		
		return link;
		
	}

}
