package org.fwb.xml.sax.snax;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * a small and abandoned set of tests exploring the JAXB "JaxbElement" api.
 * in particular, i use the API directly rather than via annotated beans.
 * 
 * I attempted this because I'd be happy to interface with the JAXB standard,
 * but the bean-annotation approach is appropriate for specific schema developers,
 * not more general SPI architecture which is a better use of Java.
 * 
 * the experiment failed.
 * I find the direct JAXBElement api unsatisfactory to create the dynamic/arbitrary structures i require.
 * it is fair enough, as JAXB is (nobly) intended for static schema facilities as well.
 * one day hopefully I can revisit JAXB more seriously.
 */
public class TestJaxbAble {
	static final Logger LOG = LoggerFactory.getLogger(TestJaxbAble.class);
	
	static final JAXBContext JC;
	static {
		try {
			JC = JAXBContext.newInstance(Object.class);
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
	
	static <T> JAXBElement<T> newJAXBElement(String qname, Class<T> declaredType, T value) {
		return new JAXBElement<T>(new QName(qname), declaredType, value);
	}
	
	@Test
	public void test1() throws JAXBException {
		JAXBElement<String> e = new JAXBElement<String>(
				new QName("foo"),
				String.class,
				"bar");
		
		StringWriter sw = new StringWriter();
		marshaller().marshal(e, sw);
		LOG.debug(sw.toString());
		Assert.assertEquals("<foo>bar</foo>", sw.toString());
	}
	
	@Test
	public void test2() throws JAXBException {
		@SuppressWarnings("unchecked")
		Class<JAXBElement<?>> cheat = (Class<JAXBElement<?>>) (Class<?>) JAXBElement.class;
		JAXBElement<?> e = new JAXBElement<JAXBElement<?>>(
				new QName("foo"),
				cheat,
				new JAXBElement<String>(
						new QName("bar"),
						String.class,
						"benny"));
		
		StringWriter sw = new StringWriter();
		marshaller().marshal(e, sw);
		LOG.debug(sw.toString());
		Assert.assertEquals("<foo>\n    <bar>benny</bar>\n</foo>", sw.toString());
	}
	
	@Test
	public void test3() throws JAXBException {
		JAXBElement<?> e = new JAXBElement<Object>(
				new QName("foo"),
				Object.class,
				new JAXBElement<String>(
						new QName("bar"),
						String.class,
						"benny"));
		
		StringWriter sw = new StringWriter();
		
		try {
			marshaller().marshal(e, sw);
			Assert.fail();
		} catch (JAXBException x) {
			// sad, sad pass
			/*
			 * e.g. 
			 * javax.xml.bind.MarshalException
			 * - with linked exception:
			 * [com.sun.istack.internal.SAXException2:
			 * Instance of "javax.xml.bind.JAXBElement" is substituting "java.lang.Object",
			 * but "javax.xml.bind.JAXBElement" is bound to an anonymous type.]
			 */
		}
//		LOG.debug(sw.toString());
//		Assert.assertEquals("<foo>\n    <bar>benny</bar>\n</foo>", sw.toString());
	}
	
	@Test
	public void test4() throws JAXBException {
		// can't be done:
//		JAXBElement<String>[] kids = new JAXBElement<String>[] {new JAXBElement<String>(null, null, null)};
		
		JAXBElement<?>[] kids = new JAXBElement<?>[] {
				newJAXBElement("bar1", String.class, "benny1"),
				newJAXBElement("bar2", String.class, "benny2"),};
		
		@SuppressWarnings("unchecked")
		JAXBElement<?> e = new JAXBElement<JAXBElement<?>[]>(
				new QName("foo"),
				(Class<JAXBElement<?>[]>) (Class<?>) JAXBElement[].class,
				kids);
		
		StringWriter sw = new StringWriter();
		
		try {
			marshaller().marshal(e, sw);
			Assert.fail();
		} catch (JAXBException x) {
			// sad, even sadder pass
			/*
			 * javax.xml.bind.MarshalException
			 * - with linked exception:
			 * [com.sun.istack.internal.SAXException2:
			 *   [Ljavax.xml.bind.JAXBElement; is not known to this context
			 *   javax.xml.bind.JAXBException:
			 *   [Ljavax.xml.bind.JAXBElement; is not known to this context]
			 */
		}
//		LOG.debug(sw.toString());
//		Assert.assertEquals("<foo>\n    <bar>benny</bar>\n</foo>", sw.toString());
	}
	
	// TODO at least attempt to see if attributes can be assigned!?
	// sadly, i can't even bring myself to care.
//	@Test
//	public void test5() throws JAXBException {
//		
//	}
}
