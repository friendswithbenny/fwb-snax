package org.fwb.xml.jaxp.xsd;

import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

/**
 * source: https://www.tutorialspoint.com/xsd/xsd_validation.htm
 */
public class SchemaUtil {
	/**
	 * returns normally if validation succeeds
	 * @throws SAXException if validation fails
	 * @throws RuntimeException (wrapping) if other errors occur
	 */
	public static void main(String[] args) throws SAXException {
		if (args.length != 2) {
			System.out.println("Usage : XSDValidator <file-name.xsd> <file-name.xml>");
		} else {
			validateXMLSchema(args[0], args[1]);
		}
	}
	
	/**
	 * @throws RuntimeException wrapping IOException from {@link Validator#validate(Source)}
	 * @throws SAXException from {@link Validator#validate(Source)}
	 */
	public static void validateXMLSchema(String xsdURI, String xmlURI) throws SAXException {
		Schema schema = parseSchema(xsdURI);
		Validator validator = schema.newValidator();
		try {
			validator.validate(new StreamSource(xmlURI));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	static SchemaFactory newFactory() {
		return SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	}
	/** @throws RuntimeException wrapping SAXException from SchemaFactory#newSchema */
	public static Schema parseSchema(String xsdURI) {
		SchemaFactory factory = newFactory();
		try {
			return factory.newSchema(new StreamSource(xsdURI));
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}
	}
	// XXX TODO consider the newSchema(Source[]) method as well?
}