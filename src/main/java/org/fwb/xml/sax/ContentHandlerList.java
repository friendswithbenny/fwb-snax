package org.fwb.xml.sax;

import java.util.*;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * forwards ContentHandler events to a Collection of delegate ContentHandlers,
 * sometimes called a "Tee" operation but with an arbitrary number of outputs.
 * 
 * TODO consider renaming this "ContentHandlers"
 * TODO see Apache Tika "TeeContentHandler"
 */
public class ContentHandlerList implements ContentHandler {
	final Collection<ContentHandler> C;
	public ContentHandlerList(Collection<ContentHandler> c) {
		C = c;
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		for (ContentHandler h : C)
			h.characters(ch, start, length);
	}
	@Override
	public void endDocument() throws SAXException {
		for (ContentHandler h : C)
			h.endDocument();
	}
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		for (ContentHandler h : C)
			h.endElement(uri, localName, qName);
	}
	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
		for (ContentHandler h : C)
			h.endPrefixMapping(prefix);
	}
	@Override
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
		for (ContentHandler h : C)
			h.ignorableWhitespace(ch, start, length);
	}
	@Override
	public void processingInstruction(String target, String data) throws SAXException {
		for (ContentHandler h : C)
			h.processingInstruction(target, data);
	}
	@Override
	public void setDocumentLocator(Locator locator) {
		for (ContentHandler h : C)
			h.setDocumentLocator(locator);
	}
	@Override
	public void skippedEntity(String name) throws SAXException {
		for (ContentHandler h : C)
			h.skippedEntity(name);
	}
	@Override
	public void startDocument() throws SAXException {
		for (ContentHandler h : C)
			h.startDocument();
	}
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		for (ContentHandler h : C)
			h.startElement(uri, localName, qName, atts);
	}
	@Override
	public void startPrefixMapping(String prefix, String uri) throws SAXException {
		for (ContentHandler h : C)
			h.startPrefixMapping(prefix, uri);
	}
}
