package org.fwb.xml.sax.jaxb;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * an interface for XML serialization, and some adapters.
 * 
 * this unfortunately only supports primitive (type-free, namespace-free) XML for the time being.
 * TODO allow types other than String, including QName?
 * 
 * @deprecated tried and failed. see TestJaxbAble for JAXBElement's shortcomings.
 */
@Deprecated
interface JaxbAble {
	static final Logger LOG = LoggerFactory.getLogger(JaxbAble.class);
	
	SnaxElement toElement();
	
	/**
	 * a flat (single-element name and attribute)
	 * and recursive (child-list) approach to serialization
	 */
	interface SnaxElement {
		String name();
		Map<String, String> atts();
		Collection<SnaxElement> kids();
	}
	
//	class SnaxAdapter implements JaxbAble {
//		final SnaxElement E;
//		SnaxAdapter(SnaxElement e) {
//			E = e;
//		}
//		
//		SnaxAdapter(
//				final String name, final Map<String, String> atts,
//				final Collection<JaxbAble> kids) {
//			this(new SnaxElement() {
//				@Override
//				public String name() {
//					return name;
//				}
//				@Override
//				public Map<String, String> atts() {
//					return atts;
//				}
//				@Override
//				public Collection<JaxbAble> kids() {
//					return kids;
//				}
//			});
//		}
//		
//		@Override
//		public void toSnax(SimpleContentHandler sch) throws SAXException {
//			sch.startElement(E.name(), new SimpleAttributes().addAttributes(E.atts()));
//				for (JaxbAble kid : E.kids())
//					kid.toSnax(sch);
//			sch.endElement(E.name());
//		}
//	}
}
