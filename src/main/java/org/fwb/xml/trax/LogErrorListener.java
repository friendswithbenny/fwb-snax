package org.fwb.xml.trax;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transformers handle Exceptions by calling ErrorListener methods of t.getErrorListener();
 * If no ErrorListener is supplied, Transformers act implementation-dependently.
 * The good ones should log nicely, and throw any error/fatalError.
 * 
 * example:
 * 	Transformer t = XMLUtility.TF.newTransformer(new StreamSource(xsltFile));
 * 	t.setErrorListener(new DefaultErrorListener(logger));	// PUNCHLINE
 * 	t.transform(new StreamSource(inFile), new StreamResult(outFile));
 * 
 * @see org.fwb.xml.sax.LogErrorHandler
 */
public class LogErrorListener implements ErrorListener {
	final Logger LOG;
	
	public LogErrorListener() {
		this(LogErrorListener.class);
	}
	public LogErrorListener(Class<?> cls) {
		this(LoggerFactory.getLogger(cls));
	}
	public LogErrorListener(Logger logger) {
		this.LOG = logger;
		
		LOG.debug("initialized: " + this);
	}
	
	@Override
	public void warning(TransformerException e) {
		LOG.warn(e.toString(), e);
	}
	@Override
	public void error(TransformerException e) throws TransformerException {
		LOG.error(e.toString(), e);
		throw e;
	}
	@Override
	public void fatalError(TransformerException e) throws TransformerException {
		LOG.error("FATAL: " + e, e);
		throw e;
	}
}
