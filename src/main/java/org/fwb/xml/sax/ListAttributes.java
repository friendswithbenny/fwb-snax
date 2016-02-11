package org.fwb.xml.sax;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;

import org.fwb.collection.ListIndex.IndexedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;

import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * exposes a collections {@link List} (of values, along with a header list)
 * as sax {@link Attributes} of datatype 'CDATA'.
 */
public class ListAttributes implements Attributes {
	static final Logger LOG = LoggerFactory.getLogger(ListAttributes.class);
	
	/**
	 * the fancy replacement for deprecated {@link MapAttributes}
	 */
	public static ListAttributes forMap(Map<String, String> record) {
		IndexedList<String> header = new IndexedList<String>(record.keySet());
		return new ListAttributes(header,
				Lists.transform(header, Functions.forMap(record)));
	}
	
	static final String
		TYPE = SaxUtil.CDATA,
		NS = XMLConstants.NULL_NS_URI;
	
	final IndexedList<String> NAMES;
	final List<String> VALUES;
	
	/**
	 * @param names the list of attribute names (an immutable+indexed copy is made)
	 * @parma values results are undefined if this is not the same size as {@code header}
	 */
	public ListAttributes(Collection<String> names, List<String> values) {
		this(
				names instanceof IndexedList ? (IndexedList<String>) names : new IndexedList<String>(names),
				Collections.unmodifiableList(values));
	}
	private ListAttributes(IndexedList<String> header, List<String> values) {
		NAMES = header;
		VALUES = Collections.unmodifiableList(values);
	}
	
	@Override
	public int getLength() {
		return NAMES.size();
	}
	
	/* name-to-index conversion */
	@Override
	public int getIndex(String qName) {
		int retVal = NAMES.indexOf(qName);
		
		// warning, at DEBUG level
		if (0 > retVal) {
			String warning = String.format("warning: qName %s out of range (%s)", qName, this);
			LOG.debug(warning, new IndexOutOfBoundsException(warning));
		}
		
		return retVal;
	}
	@Override
	public int getIndex(String uri, String localName) {
		// TODO is this clean, or just brittle/dangerous?
		Preconditions.checkNotNull(uri);
		
		if (NS.equals(uri))
			return getIndex(localName);
		
		// warning, at DEBUG level
		String warning = String.format("warning: expanded-qName {%s}%s out of range (%s)", uri, localName, this);
		LOG.debug(warning, new IndexOutOfBoundsException(warning));
		return -1;
	}
	
	/* index-to-name conversion */
	/**
	 * @return {@link #NS} iff {@code index} is within bounds, otherwise {@code null}
	 */
	@Override
	public String getURI(int index) {
		if (0 <= index && index < getLength())
			return NS;
		else {
			// warning, at DEBUG level
			String warning = String.format("warning: index %s out of range (%s)", index, this);
			LOG.debug(warning, new IndexOutOfBoundsException(warning));
			return null;
		}
	}
	@Override
	public String getLocalName(int index) {
		return (null != getURI(index)) ? NAMES.get(index) : null;
	}
	@Override
	public String getQName(int index) {
		return getLocalName(index);
	}
	
	
	@Override
	public String getValue(int index) {
		return (null != getURI(index)) ? VALUES.get(index) : null;
	}
	@Override
	public String getValue(String qName) {
		return getValue(getIndex(qName));
	}
	@Override
	public String getValue(String uri, String localName) {
		return getValue(getIndex(uri, localName));
	}
	
	@Override
	public String getType(int index) {
		return (null != getURI(index)) ? TYPE : null;
	}
	@Override
	public String getType(String qName) {
		return getType(getIndex(qName));
	}
	@Override
	public String getType(String uri, String localName) {
		return getType(getIndex(uri, localName));
	}
}
