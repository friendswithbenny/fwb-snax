package org.fwb.xml.sax.snax;

import javax.xml.XMLConstants;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

/**
 * extends the Attributes API, primarily for NameSpace-free simplicity and defaulting to CDATA type
 */
public class SimpleAttributes extends AttributesImpl {
	public static final String
		CDATA = "CDATA";
	
	/** bulk add */
	public void addAttributes(Attributes a) {
		for (int i = 0; i < a.getLength(); ++i)
			addAttribute(a.getURI(i), a.getLocalName(i), a.getQName(i), a.getType(i), a.getValue(i));
	}
	
	/** namespace-free attribute */
	public void addAttribute(String name, String value, String type) {
		addAttribute(XMLConstants.NULL_NS_URI, name, name, type, value);
	}
	
	/** namespace-free, text attributes */
	public void addAttribute(String name, Object value) {
		String type = CDATA;
		// TODO(benny)
//		if (value instanceof Number)
//			type = "xs:decimal";
//		if (value instanceof Boolean)
//			type = "xs:boolean";
//		if (value instanceof Date)
//			type = "xs:dateTime";
		addAttribute(name, value == null ? "" : value.toString(), type);
	}
}
