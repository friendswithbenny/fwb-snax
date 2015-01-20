package org.fwb.xml.sax.snax;

import org.xml.sax.Attributes;

public class EmptyAttributes implements Attributes {
	/** high-performance immutable+empty attributes singleton */
	public static final Attributes EMPTY_ATTRIBUTES =
//		new AttributesImpl();	// alternative free approach
		new EmptyAttributes();
	
	/** @see singleton {@link #EMPTY_ATTRIBUTES} */
	private EmptyAttributes() {}
	
	public int getIndex(String s, String s1) {
		return -1;
	}
	public int getIndex(String s) {
		return -1;
	}
	public int getLength() {
		return 0;
	}
	public String getLocalName(int i) {
		return null;
	}
	public String getQName(int i) {
		return null;
	}
	public String getType(int i) {
		return null;
	}
	public String getType(String s, String s1) {
		return null;
	}
	public String getType(String s) {
		return null;
	}
	public String getURI(int i) {
		return null;
	}
	public String getValue(int i) {
		return null;
	}
	public String getValue(String s, String s1) {
		return null;
	}
	public String getValue(String s) {
		return null;
	}
}
