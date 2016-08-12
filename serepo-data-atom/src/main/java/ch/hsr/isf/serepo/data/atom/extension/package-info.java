/**
 * This package-info is to define the xmlns for the extension.
 */
@XmlSchema(elementFormDefault = XmlNsForm.QUALIFIED, xmlns = {
		@XmlNs(prefix = Namespace.PREFIX, namespaceURI = Namespace.URI) })
package ch.hsr.isf.serepo.data.atom.extension;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
