package ch.hsr.isf.serepo.data.atom.elements.creator;

public interface AtomElementCreator<ATOM_ELEMENT> {

	ATOM_ELEMENT create(Object object) throws Exception;
	
}
