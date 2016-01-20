package org.fwb.xml.sax.snax;

import java.util.Map;
import java.util.Map.Entry;

import javax.xml.XMLConstants;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

/**
 * extends the Attributes API, primarily for NameSpace-free simplicity and defaulting to CDATA type
 */
public class SimpleAttributes extends AttributesImpl {
	public static final String
		CDATA = "CDATA";
	
	/**
	 * bulk add
	 * @return {@code this} for convenience
	 */
	public SimpleAttributes addAttributes(Attributes a) {
		for (int i = 0; i < a.getLength(); ++i)
			addAttribute(a.getURI(i), a.getLocalName(i), a.getQName(i), a.getType(i), a.getValue(i));
		return this;
	}
	
	/** bulk add namespace-free text attributes */
	public void addAttributes(Map<String, ?> a) {
		for (Entry<String, ?> e : a.entrySet())
			addAttribute(e.getKey(), e.getValue());
	}
	
	/** namespace-free attribute */
	public void addAttribute(String name, String value, String type) {
		addAttribute(XMLConstants.NULL_NS_URI, name, name, type, value);
	}
	
	/** namespace-free, text attribute */
	public void addAttribute(String name, Object value) {
		String type = CDATA;
		// TODO
//		if (value instanceof Number)
//			type = "xs:decimal";
//		if (value instanceof Boolean)
//			type = "xs:boolean";
//		if (value instanceof Date)
//			type = "xs:dateTime";
		addAttribute(name, value == null ? "" : value.toString(), type);
	}
}
