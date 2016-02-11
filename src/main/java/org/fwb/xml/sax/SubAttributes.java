package org.fwb.xml.sax;

import org.xml.sax.Attributes;

import com.google.common.base.Preconditions;

/**
 * A contiguous (by list-order) sub-set of (a set of) XML {@link Attributes}.
 */
public class SubAttributes implements Attributes {
	final Attributes A;
	final int BEGIN, LENGTH;
	
	public SubAttributes(Attributes a, int beginIndex) {
		this(a, beginIndex, a.getLength() - beginIndex);
	}
	/**
	 * @param a the attributes to sub-set
	 * @param beginIndex the first index, inclusive
	 * @param length the size of the intended sub-set
	 */
	public SubAttributes(Attributes a, int beginIndex, int length) {
		A = a;
		BEGIN = beginIndex;
		LENGTH = length;
		
		Preconditions.checkPositionIndex(BEGIN + LENGTH, A.getLength());
		// TODO more validations
	}
	
	/* index mapping */
	int external(int internal) {
		return /*internal < 0 ? internal :*/ internal - BEGIN;
	}
	int internal(int external) {
		return /*external < 0 ? external :*/ external + BEGIN;
	}
	
	@Override
	public int getLength() {
		return LENGTH;
	}
	
	@Override
	public int getIndex(String qName) {
		return external(A.getIndex(qName));
	}
	@Override
	public int getIndex(String uri, String localName) {
		return external(A.getIndex(uri, localName));
	}
	
	/* by-index adjusted */
	@Override
	public String getLocalName(int index) {
		return A.getLocalName(internal(index));
	}
	@Override
	public String getQName(int index) {
		return A.getQName(internal(index));
	}
	@Override
	public String getType(int index) {
		return A.getType(internal(index));
	}
	@Override
	public String getURI(int index) {
		return A.getURI(internal(index));
	}
	@Override
	public String getValue(int index) {
		return A.getValue(internal(index));
	}
	
	/* by-name unchanged */
	@Override
	public String getType(String qName) {
		return A.getType(qName);
	}
	@Override
	public String getType(String uri, String localName) {
		return A.getType(uri, localName);
	}
	@Override
	public String getValue(String qName) {
		return A.getValue(qName);
	}
	@Override
	public String getValue(String uri, String localName) {
		return A.getValue(uri, localName);
	}
}
