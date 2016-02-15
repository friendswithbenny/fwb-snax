package org.fwb.xml.todo;

import java.io.IOException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

//import org.apache.log4j.Logger;

/**
 * when added to an XMLFilter chain, this will resolve entities according to the provided EntityResolver
 * for any coordinates which resolve to null, the request is passed down the chain
 * 
 * @deprecated TODO
 * some thought needs to go into whether this "trickling" (passing down the chain) is conceptually correct
 * perhaps the constructor should take a boolean "trickle" parameter,
 * or maybe it's well-defined which behavior should occur, one way or the other
 */
class EntityResolverFilter extends XMLFilterImpl {
//	private static final Logger logger = Logger.getLogger(EntityResolverFilter.class);
	public final EntityResolver ER;
	public EntityResolverFilter(EntityResolver er) {
		ER = er;
	}
	
	@Override
	public final InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
		// stage 1, resolve
		InputSource retVal = ER.resolveEntity(publicId, systemId);	// IOException, SAXException
//		logger.debug(this + ".ER(" + ER + ").resolveEntity(" + publicId + ", " + systemId + "): " + retVal);
		
		// stage 2, if null, trickle
		if (retVal == null) {
			retVal = super.resolveEntity(publicId, systemId);
//			logger.debug(this + ".super.resolveEntity(" + publicId + ", " + systemId + "): " + retVal);
		}
		
		return retVal;
	}
}