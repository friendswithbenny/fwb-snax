package org.fwb.xml.trax;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * utility methods for java's Transformation API for XML.
 */
public class TraxUtil {
	static final Logger LOG = LoggerFactory.getLogger(TraxUtil.class);
	
	/** @deprecated static utilities only */
	@Deprecated
	private TraxUtil() { }
	
	static final SAXTransformerFactory STF = newSaxTransformerFactory();
	
	/** @deprecated TODO test */
	static final Templates XSL_CHILDELEMENT_SUBSEQUENCE = newTemplatesUnchecked(
			new StreamSource(TraxUtil.class.getResource("childelement-subsequence.xsl").toExternalForm()));
	
	public static final SAXTransformerFactory newSaxTransformerFactory() {
		return (SAXTransformerFactory) SAXTransformerFactory.newInstance();
	}
	
	/**
	 * @param cls class which shares a path with the XSL resource
	 * @param xsl XSL resource filename
	 */
	public static Templates newTemplates(Class<?> cls, String xsl) {
		return newTemplatesUnchecked(
				new StreamSource(
						cls.getResource(xsl).toExternalForm()));
	}
	
	static Templates newTemplatesUnchecked(Source s) {
		try {
			return STF.newTemplates(s);
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException(e);
		}
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
