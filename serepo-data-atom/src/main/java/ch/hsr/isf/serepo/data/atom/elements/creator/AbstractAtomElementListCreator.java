package ch.hsr.isf.serepo.data.atom.elements.creator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractAtomElementListCreator<ATOM_ELEMENT> implements AtomElementListCreator<ATOM_ELEMENT> {

	public AbstractAtomElementListCreator() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ATOM_ELEMENT> create(Object object) throws Exception {
		
		List<ATOM_ELEMENT> elementList = new ArrayList<>();
		
		if (Collection.class.isAssignableFrom(object.getClass())) {
			for (Object objectOfList : (Collection<Object>) object) {
				elementList.add(createElement(objectOfList));
			}
		} else {
			elementList.add(createElement(object));
		}
		
		return elementList;
		
	}

	protected abstract ATOM_ELEMENT createElement(Object object) throws Exception;
	
}
