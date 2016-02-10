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
	
	/** bulk add */
	public void addAttributes(Attributes a) {
		for (int i = 0; i < a.getLength(); ++i)
			addAttribute(a.getURI(i), a.getLocalName(i), a.getQName(i), a.getType(i), a.getValue(i));
	}
	
	/**
	 * bulk add namespace-free text attributes
	 * @return {@code this} for convenience
	 */
	public SimpleAttributes addAttributes(Map<String, ?> a) {
		for (Entry<String, ?> e : a.entrySet())
			addAttribute(e.getKey(), e.getValue());
		return this;
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
	
	/** OCD. an immutable singleton of the empty-set of Attributes */
	public static final Attributes EMPTY_ATTS = new Attributes() {
		@Override
		public int getLength() {
			return 0;
		}
		@Override
		public String getURI(int index) {
			throw new ArrayIndexOutOfBoundsException(index);
		}
		@Override
		public String getLocalName(int index) {
			throw new ArrayIndexOutOfBoundsException(index);
		}
		@Override
		public String getQName(int index) {
			throw new ArrayIndexOutOfBoundsException(index);
		}
		@Override
		public String getType(int index) {
			throw new ArrayIndexOutOfBoundsException(index);
		}
		@Override
		public String getValue(int index) {
			throw new ArrayIndexOutOfBoundsException(index);
		}
		@Override
		public int getIndex(String uri, String localName) {
			return -1;
		}
		@Override
		public int getIndex(String qName) {
			return -1;
		}
		@Override
		public String getType(String uri, String localName) {
			return null;
		}
		@Override
		public String getType(String qName) {
			return null;
		}
		@Override
		public String getValue(String uri, String localName) {
			return null;
		}
		@Override
		public String getValue(String qName) {
			return null;
		}
	};
}
