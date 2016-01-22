package org.fwb.xml.sax.snax;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collection;
import java.util.Map;

import javax.xml.transform.sax.SAXSource;

import org.fwb.xml.sax.LogErrorHandler;
import org.fwb.xml.sax.snax.SnaxAdapter.SnaxAble;
import org.fwb.xml.sax.snax.SnaxAdapter.SnaxElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

/** @deprecated fail */
@Deprecated
// TODO this cycle is the problem
class SnaxAdapter /*implements SnaxAble*/ {

	static final Logger LOG = LoggerFactory.getLogger(SnaxAdapter.class);
	
	interface SnaxAble {
		void toSnax(SimpleContentHandler sch) throws SAXException;
	}
	
	/**
	 * a flat (single-element name and attribute)
	 * and recursive (child-list) approach to serialization
	 */
	interface SnaxElement {
		String name();
		Map<String, String> atts();
		Collection<SnaxAble> kids();
	}
	
	final SnaxElement E;
	SnaxAdapter(SnaxElement e) {
		E = e;
	}
	
//	@Override
	public void toSnax(SimpleContentHandler sch) throws SAXException {
		sch.startElement(E.name(), new SimpleAttributes().addAttributes(E.atts()));
			for (SnaxAble kid : E.kids())
				kid.toSnax(sch);
		sch.endElement(E.name());
	}
}

//class SnaxElementImpl implements SnaxElement {
//	final String NAME;
//	final Map<String, String> ATTS;
//	final Collection<SnaxAble> KIDS;
//	SnaxElementImpl(String name, Map<String, String> atts, Collection<SnaxAble> kids) {
//		NAME = name;
//		ATTS = atts;
//		KIDS = kids;
//	}
//	@Override
//	public String name() {
//		return NAME;
//	}
//	@Override
//	public Map<String, String> atts() {
//		return ATTS;
//	}
//	@Override
//	public Collection<SnaxAble> kids() {
//		return KIDS;
//	}
//}
//
//class SnaxSource extends SAXSource {
//	SnaxSource(SnaxAble x) {
//		super(new SnaxReader(), new SnaxInput(x));
//	}
//	SnaxSource(SnaxAble x, XMLReader r) {
//		super(new SnaxReader(r), new SnaxInput(x));
//	}
//}
//
//class SnaxInput extends InputSource {
//	static final Logger LOG = LoggerFactory.getLogger(SnaxInput.class);
//	
//	final SnaxAble X;
//	SnaxInput(SnaxAble x) {
//		X = x;
//	}
//	
//	/**
//	 * @throws UnsupportedOperationException always
//	 * @deprecated
//	 */
//	@Override
//	@Deprecated
//	public InputStream getByteStream() throws UnsupportedOperationException {
//		RuntimeException e = new UnsupportedOperationException();
//		LOG.error("FATAL", e);
//		throw e;
//	}
//	/**
//	 * @throws UnsupportedOperationException always
//	 * @deprecated
//	 */
//	@Override
//	@Deprecated
//	public Reader getCharacterStream() throws UnsupportedOperationException {
//		RuntimeException e = new UnsupportedOperationException();
//		LOG.error("FATAL", e);
//		throw e;
//	}
//	/**
//	 * @throws UnsupportedOperationException always
//	 * @deprecated
//	 */
//	@Override
//	@Deprecated
//	public void setByteStream(InputStream is) throws UnsupportedOperationException {
//		RuntimeException e = new UnsupportedOperationException();
//		LOG.error("FATAL", e);
//		throw e;
//	}
//	/**
//	 * @throws UnsupportedOperationException always
//	 * @deprecated
//	 */
//	@Override
//	@Deprecated
//	public void setCharacterStream(Reader r) throws UnsupportedOperationException {
//		RuntimeException e = new UnsupportedOperationException();
//		LOG.error("FATAL", e);
//		throw e;
//	}
//}
//
//class SnaxReader extends XMLFilterImpl {
//	static final Logger LOG = LoggerFactory.getLogger(SnaxReader.class);
//	
//	static XMLReader createXMLReader() {
//		try {
//			return XMLReaderFactory.createXMLReader();
//		} catch (SAXException e) {
//			throw new Error(e);
//		}
//	}
//	
//	SnaxReader() {
//		this(createXMLReader());
//	}
//	
//	SnaxReader(XMLReader parent) {
//		super(parent);
//		setErrorHandler(new LogErrorHandler(SnaxReader.class));
//	}
//	
//	@Override
//	public void parse(InputSource is) throws SAXException, IOException {
//		LOG.trace("parse({})", is);
//		
//		if (is instanceof SnaxInput) {
//			SimpleContentHandler sch = new SimpleContentHandler(getContentHandler());
//			sch.startDocument();
//			((SnaxInput) is).X.toSnax(sch);
//			sch.endDocument();
//		} else
//			super.parse(is);
//	}
//}
