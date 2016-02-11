package org.fwb.xml.sax.snax;

import java.io.StringWriter;

import org.fwb.xml.sax.SaxUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import test.fwb.xml.sax.snax.TestSnax;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

public class TestOther {
	private static final Logger LOG = LoggerFactory.getLogger(TestOther.class);
	
	@Test
	@SuppressWarnings("deprecation")
	public void testSmartDeprecatedEndAll() throws Exception {
		// a rare bird, this would actually be a good case for fancy mockito (to mock ContentHandler).
		// that said, plain XML testing is good "enough" so i stick to what's familiar.
		
		StringWriter sw = new StringWriter();
		SmartContentHandler sch = new SmartContentHandler(SaxUtil.createContentHandler(sw));
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
		assertXMLEqual(TestSnax.XML1, xml);
	}
}
