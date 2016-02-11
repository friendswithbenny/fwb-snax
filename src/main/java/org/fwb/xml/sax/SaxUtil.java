package org.fwb.xml.sax;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;

import org.fwb.xml.trax.TraxUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

import static org.fwb.xml.trax.TraxUtil.result;

/**
 * utility methods for creating plain/regular (non-SNAX) SAX ContentHandlers (non-Simple, non-Smart).
 */
public class SaxUtil {
	/** @deprecated static utilities only */
	@Deprecated
	private SaxUtil() { }
	
	static final Logger LOG = LoggerFactory.getLogger(SaxUtil.class);
	
	static final SAXParserFactory SPF = SAXParserFactory.newInstance();
	static SAXParser newSAXParser() {
		try {
			return SPF.newSAXParser();
		} catch (ParserConfigurationException e) {
			throw new Error("never happens");
		} catch (SAXException e) {
			throw new Error("never happens");
		}
	}
	public static XMLReader newXMLReader() {
		SAXParser sp = newSAXParser();
		try {
			return sp.getXMLReader();
		} catch (SAXException e) {
			throw new Error("never happens");
		}
	}
	
	public static final String
		/** default data-type for XML values */
		CDATA = "CDATA",
		/** XMLReader property to set LexicalHandler */
		PROP_LEXICAL_HANDLER = "http://xml.org/sax/properties/lexical-handler";
	
	public static void setContentHandlerMaybeLexicalHandler(XMLReader r, ContentHandler ch) {
		r.setContentHandler(ch);
		if (ch instanceof LexicalHandler)
			try {
				r.setProperty(PROP_LEXICAL_HANDLER, ch);
			} catch (SAXNotRecognizedException e) {
				LOG.warn(
						String.format("LexicalHandler (%s) ignored. unsupported by (%s)", ch, r),
						e);
			} catch (SAXNotSupportedException e) {
				LOG.warn(
						String.format("LexicalHandler (%s) ignored. unsupported by (%s)", ch, r),
						e);
			}
	}
	
	static final SAXTransformerFactory STF = TraxUtil.newSaxTransformerFactory();
	/**
	 * creates a ContentHandler whose events are sent to the given Result.
	 * 
	 * this is essentially a bridge from TrAX to SAX.
	 * 
	 * @param r the Result to which to send all events on the returned ContentHandler
	 * @return an instance of TransformerHandler (i.e. NOT SimpleContentHandler)
	 */
	public static ContentHandler createContentHandler(Result r) {
		TransformerHandler retVal;
		try {
			retVal = STF.newTransformerHandler();
		} catch (TransformerConfigurationException e) {
			// identity transformer should never fail
			throw new Error("never happens", e);
		}
		retVal.setResult(r);
		return retVal;
	}
	
	public static ContentHandler createContentHandler(File f) {
		return createContentHandler(result(f));
	}
	/** @see #createContentHandler(String, OutputStream) */
	public static ContentHandler createContentHandler(OutputStream os) {
		return createContentHandler(result(os));
	}
	/** @see #createContentHandler(String, Writer) */
	public static ContentHandler createContentHandler(Writer w) {
		return createContentHandler(result(w));
	}
	
	public static ContentHandler createContentHandler(String systemID) {
		return createContentHandler(result(systemID));
	}
	public static ContentHandler createContentHandler(URI systemID) {
		return createContentHandler(systemID.toString());
	}
	public static ContentHandler createContentHandler(URL systemID) {
		return createContentHandler(systemID.toExternalForm());
	}
	
	public static ContentHandler createContentHandler(String systemID, OutputStream os) {
		return createContentHandler(result(systemID, os));
	}
	public static ContentHandler createContentHandler(String systemID, Writer w) {
		return createContentHandler(result(systemID, w));
	}
	
	/** OCD: high-performance immutable+empty attributes singleton */
	public static final Attributes EMPTY_ATTS =
//			new AttributesImpl();	// alternative free approach
			new Attributes() {
				@Override
				public int getLength() {
					return 0;
				}
				@Override
				public String getURI(int index) {
					return null;
				}
				@Override
				public String getLocalName(int index) {
					return null;
				}
				@Override
				public String getQName(int index) {
					return null;
				}
				@Override
				public String getType(int index) {
					return null;
				}
				@Override
				public String getValue(int index) {
					return null;
				}
				@Override
				public int getIndex(String uri, String localName) {
					return -1;
				}
				@Override
				public int getIndex(String qName) {
					return -1;
				}
				@Override
				public String getType(String uri, String localName) {
					return null;
				}
				@Override
				public String getType(String qName) {
					return null;
				}
				@Override
				public String getValue(String uri, String localName) {
					return null;
				}
				@Override
				public String getValue(String qName) {
					return null;
				}
			};
	
//	/**
//	 * An alternative approach; XMLSerializer implements ContentHandler too.
//	 */
//	public static XMLSerializer createXmlSerializer(OutputStream os) {
//		XMLSerializer ser = new XMLSerializer(os, null);
//		
//		// historically, this call was required to initialize the serializer
//		ser.asContentHandler();		// IOException
//		
//		return ser;
//	}
	
//	public static ContentHandler createContentHandler(Object o) {
//		if (null == o)
//			throw new IllegalArgumentException("can't create ContentHandler for null");
//		
//		if (o instanceof Result)
//			return createContentHandler((Result) o);
//		if (o instanceof File)
//			return createContentHandler((File) o);
//		if (o instanceof Writer)
//			return createContentHandler((Writer) o);
//		if (o instanceof OutputStream)
//			return createContentHandler((OutputStream) o);
//		if (o instanceof String)
//			return createContentHandler((String) o);
//		if (o instanceof URI)
//			return createContentHandler((URI) o);
//		if (o instanceof URL)
//			return createContentHandler((URL) o);
//		
//		throw new IllegalArgumentException(String.format(
//				"can't create ContentHandler for %s (%s) ", o.getClass(), o));
//	}
}
