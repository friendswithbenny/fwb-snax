package org.fwb.xml.sax;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * to log errors from SAX XMLReaders
 * @see org.fwb.xml.trax.LogErrorListener
 */
public final class LogErrorHandler implements ErrorHandler {
	final Logger LOG;
	
	public LogErrorHandler() {
		this(LogErrorHandler.class);
	}
	public LogErrorHandler(Class<?> cls) {
		this(LoggerFactory.getLogger(cls));
	}
	public LogErrorHandler(Logger logger) {
		LOG = logger;
	}
	
	@Override
	public final void warning(SAXParseException e) {
		LOG.warn(e.toString(), e);
	}
	@Override
	public final void error(SAXParseException e) throws SAXException {
		LOG.error(e.toString(), e);
		throw e;
	}
	@Override
	public final void fatalError(SAXParseException e) throws SAXException {
		LOG.error("FATAL: " + e, e);
		throw e;
	}
}
