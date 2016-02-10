package org.fwb.xml.sax.delegate;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/** this would extend ForwardingObject but prefers to avoid the dependency */
public abstract class ForwardingContentHandler implements ContentHandler {
	public static class FixedForwardingContentHandler extends ForwardingContentHandler {
		private final ContentHandler delegate;
		public FixedForwardingContentHandler(ContentHandler delegate) {
			this.delegate = delegate;
		}
		@Override
		public ContentHandler delegate() {
			return delegate;
		}
	}
	
	protected abstract ContentHandler delegate();
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		delegate().characters(ch, start, length);
	}
	@Override
	public void endDocument() throws SAXException {
		delegate().endDocument();
	}
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		delegate().endElement(uri, localName, qName);
	}
	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
		delegate().endPrefixMapping(prefix);
	}
	@Override
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
		delegate().ignorableWhitespace(ch, start, length);
	}
	@Override
	public void processingInstruction(String target, String data) throws SAXException {
		delegate().processingInstruction(target, data);
	}
	@Override
	public void setDocumentLocator(Locator locator) {
		delegate().setDocumentLocator(locator);
	}
	@Override
	public void skippedEntity(String name) throws SAXException {
		delegate().skippedEntity(name);
	}
	@Override
	public void startDocument() throws SAXException {
		delegate().startDocument();
	}
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		delegate().startElement(uri, localName, qName, atts);
	}
	@Override
	public void startPrefixMapping(String prefix, String uri) throws SAXException {
		delegate().startPrefixMapping(prefix, uri);
	}
}
