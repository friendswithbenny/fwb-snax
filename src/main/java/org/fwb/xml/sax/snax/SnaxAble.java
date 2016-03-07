package org.fwb.xml.sax.snax;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;

import javax.xml.transform.sax.SAXSource;

import org.fwb.xml.sax.LogErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

/**
 * an interface for XML serialization, and some adapters.
 * 
 * this unfortunately only supports primitive (type-free, namespace-free) XML for the time being.
 * TODO allow types other than String, including QName?
 */
public interface SnaxAble {
	static final Logger LOG = LoggerFactory.getLogger(SnaxAble.class);
	
	void toSnax(SimpleContentHandler sch) throws SAXException;
	
	class SnaxSource extends SAXSource {
		SnaxSource(SnaxAble x) {
			super(new SnaxReader(), new SnaxInput(x));
		}
		SnaxSource(SnaxAble x, XMLReader r) {
			super(new SnaxReader(r), new SnaxInput(x));
		}
	}
	
	/**
	 * a utility interface, this allows an implementation to represent itself as
	 * a flat, single-element name and attribute-set, along with recursive child-list.
	 * 
	 * @see SnaxAdapter
	 */
	interface SnaxElement {
		String name();
		Map<String, String> atts();
		Iterable<SnaxAble> kids();
		
		/** immutable data implementation */
		class SnaxElementImpl implements SnaxElement {
			final String NAME;
			final Map<String, String> ATTS;
			final Iterable<SnaxAble> KIDS;
			public SnaxElementImpl(String name, Map<String, String> atts, Iterable<SnaxAble> kids) {
				NAME = name;
				ATTS = atts;
				KIDS = kids; // TODO ImmutableList.copyOf?
			}
			
			@Override
			public String name() {
				return NAME;
			}
			@Override
			public Map<String, String> atts() {
				return ATTS;
			}
			@Override
			public Iterable<SnaxAble> kids() {
				return KIDS;
			}
		}
		
		/** an adapter, mapping from SnaxElement to SnaxAble */
		class ElementSnaxAble implements SnaxAble {
			public static final Function<SnaxElement, SnaxAble> ELEMENT_SNAXABLE =
					new Function<SnaxElement, SnaxAble>() {
						@Override
						public SnaxAble apply(SnaxElement input) {
							return new ElementSnaxAble(input);
						}
					};
			
			final SnaxElement E;
			public ElementSnaxAble(SnaxElement e) {
				E = e;
			}
			
			@Override
			public void toSnax(SimpleContentHandler sch) throws SAXException {
				sch.startElement(E.name(), new SimpleAttributes().addAttributes(E.atts()));
					for (SnaxAble kid : E.kids())
						kid.toSnax(sch);
				sch.endElement(E.name());
			}
		}
	}
	
	class SnaxText implements SnaxAble {
		final Object DELEGATE;
		/**
		 * @param delegate the object whose #toString will be these text contents.
		 *  never null, and #toString mustn't return null either (but may return empty-string).
		 * @throws IllegalArgumentException if {@code delegate} is {@code null}
		 */
		public SnaxText(Object delegate) {
			DELEGATE = delegate;
			Preconditions.checkArgument(null != DELEGATE,
					"delegate mustn't be null");
		}
		@Override
		public void toSnax(SimpleContentHandler sch) throws SAXException {
			String s = DELEGATE.toString();
			Preconditions.checkArgument(null != s,
					"delegate toString mustn't return null");
			sch.characters(toString());
		}
	}
}

class SnaxInput extends InputSource {
	static final Logger LOG = LoggerFactory.getLogger(SnaxInput.class);
	
	final SnaxAble X;
	SnaxInput(SnaxAble x) {
		X = x;
	}
	
	/**
	 * @throws UnsupportedOperationException always
	 * @deprecated
	 */
	@Override
	@Deprecated
	public InputStream getByteStream() throws UnsupportedOperationException {
		RuntimeException e = new UnsupportedOperationException();
		LOG.error("FATAL", e);
		throw e;
	}
	/**
	 * @throws UnsupportedOperationException always
	 * @deprecated
	 */
	@Override
	@Deprecated
	public Reader getCharacterStream() throws UnsupportedOperationException {
		RuntimeException e = new UnsupportedOperationException();
		LOG.error("FATAL", e);
		throw e;
	}
	/**
	 * @throws UnsupportedOperationException always
	 * @deprecated
	 */
	@Override
	@Deprecated
	public void setByteStream(InputStream is) throws UnsupportedOperationException {
		RuntimeException e = new UnsupportedOperationException();
		LOG.error("FATAL", e);
		throw e;
	}
	/**
	 * @throws UnsupportedOperationException always
	 * @deprecated
	 */
	@Override
	@Deprecated
	public void setCharacterStream(Reader r) throws UnsupportedOperationException {
		RuntimeException e = new UnsupportedOperationException();
		LOG.error("FATAL", e);
		throw e;
	}
}

class SnaxReader extends XMLFilterImpl {
	static final Logger LOG = LoggerFactory.getLogger(SnaxReader.class);
	
	static XMLReader createXMLReader() {
		try {
			return XMLReaderFactory.createXMLReader();
		} catch (SAXException e) {
			throw new Error(e);
		}
	}
	
	SnaxReader() {
		this(createXMLReader());
	}
	
	SnaxReader(XMLReader parent) {
		super(parent);
		setErrorHandler(new LogErrorHandler(SnaxReader.class));
	}
	
	@Override
	public void parse(InputSource is) throws SAXException, IOException {
		LOG.trace("parse({})", is);
		
		if (is instanceof SnaxInput) {
			SimpleContentHandler sch = new SimpleContentHandler(getContentHandler());
			sch.startDocument();
			((SnaxInput) is).X.toSnax(sch);
			sch.endDocument();
		} else
			super.parse(is);
	}
}