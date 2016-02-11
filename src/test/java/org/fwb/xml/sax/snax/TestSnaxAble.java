package org.fwb.xml.sax.snax;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.fwb.xml.sax.snax.SnaxAble.SnaxElement.ElementSnaxAble;
import org.fwb.xml.sax.snax.SnaxAble.SnaxElement.SnaxElementImpl;
import org.fwb.xml.sax.snax.SnaxAble.SnaxSource;
import org.fwb.xml.trax.LogErrorListener;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * TODO confirm this approach and publicize necessary components in {@link SnaxAble},
 * then move this test to the "test." package.
 */
public class TestSnaxAble {
	static final Logger LOG = LoggerFactory.getLogger(TestSnaxAble.class);
	
	static final String NAME= "theElementName";
	static final Map<String,String> ATTS = Collections.unmodifiableMap(
			new LinkedHashMap<String, String>() {
				/** default */
				private static final long serialVersionUID = 1L;
				{
					put("firstAtt", "thirteen");
					put("secondAttribution", "some value");
				}
			});
	static final String CHILD1NAME = "firstborn";
	static final Map<String,String> CHILD1ATTS = Collections.unmodifiableMap(
			new LinkedHashMap<String, String>() {
				/** eclipse-generated */
				private static final long serialVersionUID = 8896217818244417977L;
				{
					put("nationality", "egyptian");
					put("gender", "masculine");
				}
			});
	static final SnaxAble CHILD2 = new SnaxAble() {
		@Override
		public void toSnax(SimpleContentHandler sch) throws SAXException {
			LOG.debug("CHILD2.toSnax({} ({}))", sch, sch.delegate());
			sch.startElement("theSecondChild", new SimpleAttributes().addAttributes(
					new LinkedHashMap<String, String>() {
						/** eclipse-generated */
						private static final long serialVersionUID = 8896217818244417977L;
						{
							put("hair", "blonde");
							put("type", "snazzy snax able");
						}
					}));
				
				sch.characters("some char broil too");
			sch.endElement("theSecondChild");
		}
	};
	
	static final SnaxSource SRC = new SnaxSource(
			new ElementSnaxAble(new SnaxElementImpl(NAME, ATTS, Arrays.<SnaxAble>asList(
					new ElementSnaxAble(new SnaxElementImpl(
							CHILD1NAME, CHILD1ATTS, Collections.<SnaxAble>emptySet()
					)),
					CHILD2
			)))
	);
	
	static final String EXPECTED = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><theElementName firstAtt=\"thirteen\" secondAttribution=\"some value\">"
			+ System.lineSeparator() + "<firstborn nationality=\"egyptian\" gender=\"masculine\"/>"
			+ System.lineSeparator() + "<theSecondChild hair=\"blonde\" type=\"snazzy snax able\">some char broil too</theSecondChild>"
			+ System.lineSeparator() + "</theElementName>"
			+ System.lineSeparator();
	
	@Test
	public void test() throws Exception {
		TransformerFactory tf = TransformerFactory.newInstance();
		tf.setErrorListener(new LogErrorListener(LoggerFactory.getLogger("org.fwb.xml.sax.snax.TestSnaxAble.FACTORY")));
		Transformer t = tf.newTransformer();
		t.setErrorListener(new LogErrorListener(LoggerFactory.getLogger("org.fwb.xml.sax.snax.TestSnaxAble.TRANSFORMER")));
		t.setOutputProperty(OutputKeys.INDENT, "yes");
		
		// strange suggestion. doesn't work
		// (http://stackoverflow.com/questions/28543214/pretty-print-xml-in-java-like-eclipse-does)
//		t.setOutputProperty(OutputKeys.STANDALONE, "yes");
		
		StringWriter sw = new StringWriter();
		t.transform(SRC, new StreamResult(sw));
		
		LOG.debug(EXPECTED);
		LOG.debug(sw.toString());
		Assert.assertEquals(EXPECTED, sw.toString());
	}
	
	/* testing JAXB compatibility below */
	
	static final JAXBContext JC;
	static {
		try {
			JC = JAXBContext.newInstance(JaxbThing.class);
		} catch (JAXBException e) {
			throw new Error("never happens", e);
		}
	}
	static Marshaller marshaller() {
		try {
			Marshaller retVal = JC.createMarshaller();
			retVal.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			retVal.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			return retVal;
		} catch (JAXBException e) {
			throw new Error("never happens", e);
		}
	}
	/**
	 * with great sadness, I decide for now JAXB is not in fact compatible.
	 * 
	 * this post suggests directly using JAXB unmarshaller to build a JAXBElement,
	 * but I find that response unsatisfactory.
	 * (http://blog.bdoughan.com/2013/03/jaxb-and-javautilmap.html)
	 * (from http://stackoverflow.com/questions/26232916/jaxb-adapter-which-return-xml-node-or-raw-xml-text-instead-object)
	 * 
	 * TODO research marshaller and unmarshaller more. this may merit a customization at that level.
	 */
	@Test
	public void test2JaxbCompatible() throws Exception {
		StringWriter sw = new StringWriter();
		marshaller().marshal(new JaxbThing(), sw);
		LOG.debug(sw.toString());
		
		// ridiculous at first, but I guess @XmlValue means unparsed content or something,
		// which in this case appeared binary and was converted to hex?
		String hilarious = "<bennytesty>PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48dGhlRWxlbWVudE5hbWUgZmlyc3RBdHQ9InRoaXJ0ZWVuIiBzZWNvbmRBdHRyaWJ1dGlvbj0ic29tZSB2YWx1ZSI+PGZpcnN0Ym9ybiBuYXRpb25hbGl0eT0iZWd5cHRpYW4iIGdlbmRlcj0ibWFzY3VsaW5lIi8+PHRoZVNlY29uZENoaWxkIGhhaXI9ImJsb25kZSIgdHlwZT0ic25henp5IHNuYXggYWJsZSI+c29tZSBjaGFyIGJyb2lsIHRvbzwvdGhlU2Vjb25kQ2hpbGQ+PC90aGVFbGVtZW50TmFtZT4=</bennytesty>";
		Assert.assertEquals(hilarious, sw.toString());
	}
	
	@XmlRootElement(name="bennytesty")
	public static class JaxbThing {
		@XmlValue
		Source xxx = SRC;
		
		public String toString() {
			return "but no, really, look. i'm some other type of object!";
		}
	}
}
