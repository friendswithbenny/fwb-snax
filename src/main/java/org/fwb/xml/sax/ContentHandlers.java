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
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.ContentHandler;

/** utility methods for creating plain/regular (non-SNAX) SAX ContentHandlers (non-Simple, non-Smart) */
public class ContentHandlers {
	static final SAXTransformerFactory STF = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
	
	/**
	 * creates a ContentHandler whose events are sent to the given Result
	 * @param r the Result to which to send all events on the returned ContentHandler
	 * @return an instance of TransformerHandler (i.e. NOT SimpleContentHandler)
	 */
	public static ContentHandler createContentHandler(Result r) {
		TransformerHandler retVal;
		try {
			retVal = STF.newTransformerHandler();
		} catch (TransformerConfigurationException e) {
			throw new Error("never happens", e);
		}
		retVal.setResult(r);
		return retVal;
	}
	
	public static ContentHandler createContentHandler(File f) {
		return createContentHandler(new StreamResult(f));
	}
	/**
	 * @deprecated
	 * 	prefer versions which specify a systemID
	 * @see #createContentHandler(String, OutputStream)
	 */
	public static ContentHandler createContentHandler(OutputStream os) {
		return createContentHandler(new StreamResult(os));
	}
	/**
	 * @deprecated
	 * 	"Normally, a stream should be used rather than a reader[SIC],
	 * 	so that the transformer may use instructions contained
	 * 	in the transformation instructions[SIC] to control the encoding."
	 */
	public static ContentHandler createContentHandler(Writer w) {
		return createContentHandler(new StreamResult(w));
	}
	
	public static ContentHandler createContentHandler(String systemID) {
		return createContentHandler(new StreamResult(systemID));
	}
	public static ContentHandler createContentHandler(URI systemID) {
		return createContentHandler(systemID.toString());
	}
	public static ContentHandler createContentHandler(URL systemID) {
		return createContentHandler(systemID.toExternalForm());
	}
	
	public static ContentHandler createContentHandler(Object o) {
		if (o == null)
			throw new IllegalArgumentException("can't create ContentHandler for null");
		
		if (o instanceof Result)
			return createContentHandler((Result) o);
		if (o instanceof File)
			return createContentHandler((File) o);
		if (o instanceof Writer)
			return createContentHandler((Writer) o);
		if (o instanceof OutputStream)
			return createContentHandler((OutputStream) o);
		if (o instanceof String)
			return createContentHandler((String) o);
		if (o instanceof URI)
			return createContentHandler((URI) o);
		if (o instanceof URL)
			return createContentHandler((URL) o);
		
		throw new IllegalArgumentException("can't create ContentHandler for: " + o);
	}
	
	public static ContentHandler createContentHandler(String systemID, OutputStream os) {
		Result r = new StreamResult(os);
		r.setSystemId(systemID);
		return createContentHandler(r);
	}
}
