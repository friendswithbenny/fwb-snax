package test.fwb.xml.jaxp.xsd;

import javax.xml.validation.Schema;

import org.fwb.xml.jaxp.xsd.SchemaUtil;

// TODO convert to a proper (junit) test?
public class TestSchema {
	static final String
		XBRL_URI = "https://www.xbrl.org/2003/xbrl-instance-2003-12-31.xsd",
		AAOI_URI = "file:///C:/Users/benni/Documents/work.2018-07-02/0001558370-18-001271-xbrl/aaoi-20171231.xsd",
		DEI_URI_RAW = "http://xbrl.sec.gov/dei/2014/dei-2014-01-31.xsd",
		DEI_URI_HTTPS = "https://xbrl.sec.gov/dei/2014/dei-2014-01-31.xsd",
		TEST_URI = TestSchema.class.getResource("xbrl-instance-test.xsd").toExternalForm();
	
	
	public static void main(String[] args) throws Exception {
		Schema schema = SchemaUtil.parseSchema(TEST_URI);
		System.out.println(schema);
	}
}
