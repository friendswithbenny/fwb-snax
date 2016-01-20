package org.fwb.xml.sax.snax;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.fwb.xml.sax.snax.SnaxAble.SnaxAdapter;
import org.fwb.xml.sax.snax.SnaxAble.SnaxSource;
import org.fwb.xml.trax.LogErrorListener;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

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
		t.transform(
				new SnaxSource(new SnaxAdapter(NAME, ATTS, Arrays.<SnaxAble>asList(
						new SnaxAdapter(CHILD1NAME, CHILD1ATTS, Collections.<SnaxAble>emptySet()),
						CHILD2))),
				new StreamResult(sw));
		
		LOG.debug(EXPECTED);
		LOG.debug(sw.toString());
		Assert.assertEquals(EXPECTED, sw.toString());
	}
}
