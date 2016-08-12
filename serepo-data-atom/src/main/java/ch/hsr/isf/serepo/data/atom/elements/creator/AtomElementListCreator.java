package ch.hsr.isf.serepo.data.atom.elements.creator;

import java.util.List;

public interface AtomElementListCreator<ATOM_ELEMENT> {

	List<ATOM_ELEMENT> create(Object object) throws Exception;
	
}
