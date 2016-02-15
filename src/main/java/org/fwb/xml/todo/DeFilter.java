package org.fwb.xml.todo;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * this class presents an un-filtered view of a given XMLFilter's parent. 
 * the primary purpose of this class is to present a dynamic view of an XMLReader's "application layer" (its 4 handlers)
 * which performs some filtration before passing events to its handlers
 * wrapping such an XMLFilter with this class results in an XMLFilter which passes events *directly* to those 4 handlers
 * 
 * @deprecated TODO what's even the point?
 * I'm sure for some reason I needed a layer other than XMLReader (i.e. DTDHandler or ErrorHandler, etc.),
 * but I'm not exactly understanding how this resolves that, but implementing them all as no-ops? sigh.
 * for now I prefer a consuemr simply call {@link XMLFilter#getParent()}.
 * 
 * at any rate it needs testing.
 */
class DeFilter extends XMLFilterImpl {
	static final Logger LOG = LoggerFactory.getLogger(DeFilter.class);
	
	final XMLReader CHILD;
	public DeFilter(XMLReader child) {
		CHILD = child;
	}
	
	public void parse(InputSource input) throws IOException, SAXException {
		LOG.trace("parse({})", input);
		
		setContentHandler(getContentHandler());
		setDTDHandler(getDTDHandler());
		setEntityResolver(getEntityResolver());
		setErrorHandler(getErrorHandler());
		
		super.parse(input);	// IOException, SAXException
	}
	
	@Override
	public ContentHandler getContentHandler() {
		ContentHandler retVal = CHILD.getContentHandler();
		LOG.trace("getContentHandler(): {}", retVal);
		return retVal;
	}
	@Override
	public DTDHandler getDTDHandler() {
		DTDHandler retVal = CHILD.getDTDHandler();
		LOG.trace("getDTDHandler(): {}", retVal);
		return retVal;
	}
	@Override
	public EntityResolver getEntityResolver() {
		EntityResolver retVal = CHILD.getEntityResolver();
		LOG.trace("getEntityResolver(): {}", retVal);
		return retVal;
	}
	@Override
	public ErrorHandler getErrorHandler() {
		ErrorHandler retVal = CHILD.getErrorHandler();
		LOG.trace("getErrorHandler(): {}", retVal);
		return retVal;
	}
}
