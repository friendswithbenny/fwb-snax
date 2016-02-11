package org.fwb.xml.sax.snax;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.transform.Result;

import org.fwb.xml.sax.SaxUtil;
import org.fwb.xml.sax.delegate.ForwardingContentHandler.FixedForwardingContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * wraps/decorates a ContentHandler, offering additional "simplified" helper methods.
 * 
 * utilities:
 * 	static methods to create ContentHandler from various result types
 * 	intuitive sugar methods with some commonly-empty arguments removed:
 * 		namespace: defaults to null-prefix, {@link XMLConstants.NULL_NS_URI} ("")
 * 		attributes: defaults to empty-set of attributes
 * 		children: provides empty-element methods to replace two-call start+end events
 * 
 * @see http://en.wikipedia.org/wiki/Decorator_pattern
 * @see com.google.common.collect.ForwardingObject
 */
public class SimpleContentHandler extends FixedForwardingContentHandler {
	public static SimpleContentHandler of(ContentHandler ch) {
		return (ch instanceof SimpleContentHandler) ? (SimpleContentHandler) ch : new SimpleContentHandler(ch);
	}
	
	public SimpleContentHandler(ContentHandler delegate) {
		super(delegate);
	}
	
	public SimpleContentHandler(Result r) {
		this(SaxUtil.createContentHandler(r));
	}
	public SimpleContentHandler(File f) {
		this(SaxUtil.createContentHandler(f));
	}
	public SimpleContentHandler(String systemID) {
		this(SaxUtil.createContentHandler(systemID));
	}
	
	/* namespace-free element (namespace-free is the default, hereafter) */
	/** namespace-free element */
	public void startElement(String name, Attributes atts) throws SAXException {
		startElement(XMLConstants.NULL_NS_URI, name, name, atts);
	}
	/** namespace-free element */
	public void endElement(String name) throws SAXException {
		endElement(XMLConstants.NULL_NS_URI, name, name);
	}
	
	/* empty element */
	/** empty element, attribute-free */
	public void emptyElement(String name) throws SAXException {
		emptyElement(name, SaxUtil.EMPTY_ATTS);
	}
	/** empty element with attributes */
	public void emptyElement(String name, Attributes atts) throws SAXException {
		startElement(name, atts);
		endElement(name);
	}
	/** empty element with attributes and namespace */
	public void emptyElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		startElement(uri, localName, qName, atts);
		endElement(uri, localName, qName);
	}
	
	/* attribute-free element */
	/** attribute-free element */
	public void startElement(String name) throws SAXException {
		startElement(name, SaxUtil.EMPTY_ATTS);
	}
	/** attribute-free element with namespace */
	public void startElement(String uri, String localName, String qName) throws SAXException {
		startElement(uri, localName, qName, SaxUtil.EMPTY_ATTS);
	}
	
	/* simplified implementations */
	/** all characters of a string */
	public void characters(String s) throws SAXException {
		characters(s.toCharArray(), 0, s.length());
	}
	/** ignorableWhitespace of all characters of a string */
	public void ignorableWhitespace(String s) throws SAXException {
		ignorableWhitespace(s.toCharArray(), 0, s.length());
	}
}
