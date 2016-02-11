package org.fwb.xml.sax.snax;

import java.io.File;
import java.util.LinkedList;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * this extension of SimpleContentHandler provides extremely intuitive high-level simplicity at the expense of some tradeoffs
 * 
 * simplicity is achieved by allowing serial event-driven approach to attributes, just as SAX allows for elements
 * this is to say, addAttribute can be called *at this (ContentHandler) level* to add attributes to an Element event
 * obviously, such calls add attributes to the "current context element," which must have been set with an immediately prior call to startElement
 * this is lexically intuitive, in that a programmer accustomed to writing XML Strings would of course build them with TagName followed by Attribute definitions
 * 
 * one minor cost of this implementation is speed. SAX events will be slower on objects of this class. asymptotically there is no change: O(1) additional time overhead
 * 
 * the major cost of this implementation is one of exact lexical semantics
 * a programmer expects, after a call to startElement, that whatever underlying event handling mechanism exists has been notified of the event
 * this implementation explicitly breaks that paradigm, and a call to startElement is *NOT* propagated until the next lexical event is processed
 * for instance, an XMLSerializer to an OutputStream will write "<x />" as soon as startElement(... "x" ...) is called
 * this will *not* happen in this case, and that writing will *only* occur after the next (lexical) event
 * 
 * in most cases, this difference should be meaningless, as any startElement event must AT LEAST be followed by an endElement at some point
 * however, the programmer should be very careful to avoid use of this class in applications with complicated/intricate handling of events
 * for instance, a ContentHandler might await the startElement event, and upon that event, itself call the endElement event
 * such an example would never complete!
 * a simpler example would be an application which awaits startElement for some stateChange, and the next SAX event actually uses that changed state in its data:
 * startElement(... "x" ...)
 * characters("new state: " + getCurrentDataState());
 * in this case, getCurrentDataState() will run BEFORE the underlying startElement is ever run
 * 
 * in all cases, a call to <code>flush()</code> will always force any pending events to be sent
 * 
 * @deprecated prefer {@link SimpleContentHandler} to this ultra-stateful sugar
 */
@Deprecated
public class SmartContentHandler extends SimpleContentHandler {
	public static SmartContentHandler of(ContentHandler ch) {
		return (ch instanceof SmartContentHandler) ? (SmartContentHandler) ch : new SmartContentHandler(ch);
	}
	
	/** serialization token for a QName, surprisingly absent from the QName api */
	static String toName(QName qName) {
		if (null == qName)
			return null;
		if (null == qName.getPrefix())
			throw new IllegalArgumentException("unexpected null prefix (try XMLConstants#DEFAULT_NS_PREFIX?): " + qName);
		return (XMLConstants.DEFAULT_NS_PREFIX.equals(qName.getPrefix()) ? "" : qName.getPrefix() + ":")
				+ qName.getLocalPart();
	}
	static String toPrefix(String qName) {
		if (qName == null)
			throw new IllegalArgumentException("can't get prefix of null");
		int colon = qName.indexOf(":");
		return colon < 0 ? XMLConstants.DEFAULT_NS_PREFIX : qName.substring(0, colon);
	}
	
	private final LinkedList<QName> STACK = new LinkedList<QName>();
	
	private QName currentElement = null;
	private SimpleAttributes currentAttributes = null;
	
	public SmartContentHandler(File f) {
		super(f);
	}
	public SmartContentHandler(ContentHandler ch) {
		super(ch);
	}
	public SmartContentHandler(String systemID) {
		super(systemID);
	}
	
	/**
	 * this method should be called to finalize any calls to startElement,
	 * if finalization is needed before another lexical event occurs
	 */
	public final void flush() throws SAXException {
		if (currentElement != null) {
			super.startElement(
					currentElement.getNamespaceURI(), currentElement.getLocalPart(), toName(currentElement),
					currentAttributes);
			STACK.push(currentElement);
			currentElement = null;
			currentAttributes = null;
		}
	}
	
	public void endElement() throws SAXException {
		flush();
		QName e = STACK.peek();
		endElement(e.getNamespaceURI(), e.getLocalPart(), toName(e));
	}
	
	/**
	 * Closes all open elements and the document.
	 * @deprecated use of this method is not recommended; it is usually best-practice to self-balance
	 */
	@Deprecated
	void endAll() throws SAXException {
		while (! STACK.isEmpty()) {
			endElement();
		}
		endDocument();
	}
	
	/* convenience methods, N.B. these do NOT apply XSD-typing in any way. */
	public void addAttribute(String name, Number value) {
		currentAttributes.addAttribute(name, "" + value);
	}
	public void addAttribute(String name, Boolean value) {
		currentAttributes.addAttribute(name, "" + value);
	}
	// TODO Date!?
	
	public void addAttribute(String name, String value) {
		currentAttributes.addAttribute(name, value);
	}
	public void addAttribute(String uri, String localName, String qName, String type, String value) {
		currentAttributes.addAttribute(uri, localName, qName, type, value);
	}
	
	/* flush before certain events */
	
//	@Override
//	public void setDocumentLocator(Locator locator) {
//		super.setDocumentLocator(locator);
//	}
//	@Override
//	public void startDocument() throws SAXException {
//		flush();
//		super.startDocument();
//	}
//	@Override
//	public void endDocument() throws SAXException {
//		flush();
//		super.endDocument();
//	}
	
//	/** at ~the same level as addAttribute */
//	@Override
//	public void startPrefixMapping(String prefix, String uri) throws SAXException {
//		flush();
//		super.startPrefixMapping(prefix, uri);
//	}
//	/** guaranteed to be called "immediately after the corresponding endElement event */
//	@Override
//	public void endPrefixMapping(String prefix) throws SAXException {
//		flush();
//		super.endPrefixMapping(prefix);
//	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		flush();
		super.characters(ch, start, length);
	}
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		flush();
		super.endElement(uri, localName, qName);
		STACK.pop();
	}
	@Override
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
		flush();
		super.ignorableWhitespace(ch, start, length);
	}
	@Override
	public void processingInstruction(String target, String data) throws SAXException {
		flush();
		super.processingInstruction(target, data);
	}
	@Override
	public void skippedEntity(String name) throws SAXException {
		flush();
		super.skippedEntity(name);
	}
	/** now a delayed implementation */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		flush();
		currentElement = new QName(uri, localName, toPrefix(qName));
		currentAttributes = new SimpleAttributes();
		currentAttributes.addAttributes(atts);
	}
}
