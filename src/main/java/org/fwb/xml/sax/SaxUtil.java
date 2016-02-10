package org.fwb.xml.sax;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;
import java.net.URL;

import javax.xml.transform.Result;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;

import org.fwb.xml.trax.TraxUtil;
import org.xml.sax.ContentHandler;

import static org.fwb.xml.trax.TraxUtil.result;

/**
 * utility methods for creating plain/regular (non-SNAX) SAX ContentHandlers (non-Simple, non-Smart).
 */
public class SaxUtil {
	/** @deprecated static utilities only */
	@Deprecated
	private SaxUtil() { }
	
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
