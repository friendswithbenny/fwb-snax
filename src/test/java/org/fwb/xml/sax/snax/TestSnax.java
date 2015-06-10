package org.fwb.xml.sax.snax;

import java.io.StringWriter;

import org.fwb.xml.sax.ContentHandlers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.custommonkey.xmlunit.XMLAssert.*;

public class TestSnax {
	private static final Logger LOG = LoggerFactory.getLogger(TestSnax.class);
	
	static final String
		XML1 = "<thing1 bar='jar' foo='bar'><thing2>thang</thing2><thing3 /></thing1>";
	
	/**
	 * Sadly, this test fails.
	 * It will pass if xmlunit ever fixes the bug,
	 * but that's unlikely as it would be backwards-incompatible.
	 * xmlunit's "similar" neglects child-order,
	 * which is clearly a bug as child-order is semantically meaningful in XML.
	 * "similar" is still preferred here, to ignore certain lexical details e.g. prefixes.
	 */
//	@Test
	public void testXmlUnitBugFixed() throws Exception {
		assertXMLNotEqual("<thing1 bar='jar' foo='bar'><thing3 /><thing2>thang</thing2></thing1>", XML1);
	}
	
	@Test
	public void testX() throws Exception {
		// a rare bird, this would actually be a good case for fancy mockito (to mock ContentHandler).
		// that said, plain XML testing is good "enough" so i stick to what's familiar.
		
		StringWriter sw = new StringWriter();
		SmartContentHandler sch = new SmartContentHandler(ContentHandlers.createContentHandler(sw));
		sch.startDocument();
			sch.startElement("thing1");
				sch.addAttribute("foo", "bar");
				sch.addAttribute("bar", "jar");
				sch.startElement("thing2");
					sch.characters("thang");
				sch.endElement();
				sch.emptyElement("thing3");
		sch.endAll();
		String xml = sw.toString();
		
		LOG.debug("got xml {}", xml);
		assertXMLEqual(XML1, xml);
	}
}