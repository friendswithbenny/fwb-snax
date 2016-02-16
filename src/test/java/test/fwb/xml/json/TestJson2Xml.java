package test.fwb.xml.json;

import org.fwb.xml.json.Json2Xml;
import org.json.JSONArray;
import org.json.XML;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class TestJson2Xml {
	static final Logger LOG = LoggerFactory.getLogger(TestJson2Xml.class);
	
	static final String
		JSON1 = "{\"foo\":13,\"bar\":\"7\",\"bam\":{1:2,3:4}}",
		JSON2 = "[\"foo\",13,\"bar\",\"7\",\"bam\",[1,2,3,4]]",
		XML1 = "<bam><3>4</3><1>2</1></bam><foo>13</foo><bar>7</bar>",
		XML2 = "<json>foo</json><json>13</json><json>bar</json><json>7</json><json>bam</json><json>1</json><json>2</json><json>3</json><json>4</json>";
	
	@Test
	public void testJson2Xml() {
		assertEquals("<json>" + XML1 + "</json>", Json2Xml.json2xml(JSON1));
		assertEquals(XML2, Json2Xml.json2xml(JSON2));
		assertEquals(XML1, Json2Xml.json2xmlNoRoot(JSON1));
	}
	
	@Test
	public void testSanityChecks() {
		// XML.toString takes already-parsed json input
		// ex. this is interpreted as a json-string input
		assertEquals(
				"\"{&quot;foo&quot;:13,&quot;bar&quot;:&quot;7&quot;,&quot;bam&quot;:{1:2,3:4}}\"",
				XML.toString(JSON1));
		
		assertEquals("\"123\"", XML.toString("123"));
		// is it strange that i find it strange that number literals (or any literal, really) be quoted?
		assertEquals("\"123\"", XML.toString(123));
		
		// 'array' is the default element tag-name for arrays
		// n.b. neither literal quoted here
		// n.b. sadly, nested arrays are flattened
		assertEquals("<array>1</array><array>2</array><array>3</array>",
				XML.toString(new JSONArray("[1,\"2\", [3]]")));
	}
}
