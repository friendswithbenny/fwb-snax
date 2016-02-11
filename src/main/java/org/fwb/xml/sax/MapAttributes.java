package org.fwb.xml.sax;

import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;

import org.fwb.collection.ListIndex.IndexedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;

import com.google.common.base.Preconditions;

/**
 * exposes a collections {@link Map} as sax {@link Attributes} of datatype 'CDATA'.
 * TODO a version with Object values which infers a more precise datatype?
 * 
 * @deprecated see {@link ListAttributes#asMap}
 */
class MapAttributes implements Attributes {
	static final Logger LOG = LoggerFactory.getLogger(MapAttributes.class);
	static final String
		TYPE = SaxUtil.CDATA,
		NS = XMLConstants.NULL_NS_URI;
	
	final Map<String, String> MAP;
	/** immutable, indexed copy */
	final List<String> HEADER;
	/**
	 * @param map values may change, but the keys must be (1) non-null and (2) fixed-consistent order
	 */
	MapAttributes(Map<String, String> map) {
		MAP = map;
		// for (unfortunate?) historical reasons, this interface prefers integer-indexing
		HEADER = new IndexedList<String>(map.keySet());
	}
	
	public Map<String, String> getMap() {
		return MAP;
	}
	
	@Override
	public int getLength() {
		return getMap().size();
	}
	
	/* name-to-index conversion */
	@Override
	public int getIndex(String qName) {
		int retVal = HEADER.indexOf(qName);
		
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
		return (null != getURI(index)) ? HEADER.get(index) : null;
	}
	@Override
	public String getQName(int index) {
		return getLocalName(index);
	}
	
	
	@Override
	public String getValue(int index) {
		return MAP.get(getQName(index));
	}
	@Override
	public String getValue(String qName) {
		// at the cost of an extra lookup, force a warning
		return (0 <= getIndex(qName)) ? MAP.get(qName) : null;
	}
	@Override
	public String getValue(String uri, String localName) {
		return (0 <= getIndex(uri, localName)) ? MAP.get(localName) : null;
	}
	
	@Override
	public String getType(int index) {
		return (null != getURI(index)) ? TYPE : null;
	}
	@Override
	public String getType(String qName) {
		return (0 <= getIndex(qName)) ? TYPE : null;
	}
	@Override
	public String getType(String uri, String localName) {
		return (0 <= getIndex(uri, localName)) ? TYPE : null;
	}
}
