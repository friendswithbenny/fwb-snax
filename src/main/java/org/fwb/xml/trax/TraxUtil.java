package org.fwb.xml.trax;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;
import java.net.URL;

import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;

/**
 * utility methods for java's Transformation API for XML.
 */
public class TraxUtil {
	/** @deprecated static utilities only */
	@Deprecated
	private TraxUtil() { }
	
	public static final SAXTransformerFactory newSaxTransformerFactory() {
		return (SAXTransformerFactory) SAXTransformerFactory.newInstance();
	}
	
	public static StreamResult result(File f) {
		return new StreamResult(f);
	}
	public static StreamResult result(OutputStream os) {
		return new StreamResult(os);
	}
	public static StreamResult result(Writer w) {
		return new StreamResult(w);
	}
	public static StreamResult result(String systemID) {
		return new StreamResult(systemID);
	}
	public static StreamResult result(URI systemID) {
		return result(systemID.toString());
	}
	public static StreamResult result(URL systemID) {
		return result(systemID.toExternalForm());
	}
	public static StreamResult result(String systemID, OutputStream os) {
		StreamResult r = new StreamResult(os);
		r.setSystemId(systemID);
		return r;
	}
	public static StreamResult result(String systemID, Writer w) {
		StreamResult r = new StreamResult(w);
		r.setSystemId(systemID);
		return r;
	}
}
