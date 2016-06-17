package org.fwb.xml.sax.snax;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

/**
 * this utility simplifies (implementation of) the {@link XMLReader} interface
 * for (a vast majority of) cases where a character stream is required.
 * 
 * it is typically the parser's responsibility to:
 * 1. obtain the stream if it is not included in the {@link InputSource}
 * 2. robustly release said resource, if obtained in #1.
 * 
 * rather than perform these duties with a surprising amount of boilerplate,
 * implementors of {@link XMLReader#parse} may instead:
 * 1. implement the more consistent contract of {@link SimpleXmlParser#parse(Reader, String)}
 * 2. delegate to {@link #parse(SimpleXmlParser, InputSource)} to provide stream management.
 * 
 * TODO tests!
 */
public class SimpleXmlParsing {
	static final Logger LOG = LoggerFactory.getLogger(SimpleXmlParsing.class);
	
	/**
	 * a simple expression of character-stream parsing logic
	 * @param <T> a return value of the implementor's choice
	 */
	public interface SimpleXmlParser<T> {
		/**
		 * a common basic signature for implementors of of character-stream parsing logic.
		 * this method should expect to be called within a managed context,
		 * and need NOT close the given Reader when finished nor in the event of an error.
		 * 
		 * @param sch not null; do not call {@link ContentHandler#startDocument()} nor {@link ContentHandler#endDocument()}
		 * @param r not null; do not close
		 * @param systemId possibly null
		 * @return a value of the implementor's choice
		 */
		T parse(SimpleContentHandler sch, Reader r, String systemId) throws IOException, SAXException;
	}
	
	public static class ParseManager {
		public static final ParseManager INSTANCE = new ParseManager();
		
		/**
		 * accessible to sub-classes only, for over-ride.
		 * @see #INSTANCE
		 */
		protected ParseManager() {}
		
		/**
		 * calls the {@link SimpleXmlParser#parse(Reader, String)} method,
		 * if necessary obtaining a character stream (via {@link #openStream(String)}) and closing it.
		 * 
		 * this method is state-less and thread-safe
		 * 
		 * @param input not null
		 * @param sch not null
		 */
		public <T> T parseManaged(SimpleXmlParser<T> sxr, SimpleContentHandler sch, InputSource input) throws IOException, SAXException {
			Preconditions.checkNotNull(input, "InputSource mustn't be null");
			Preconditions.checkNotNull(sch, "SimpleContentHandler mustn't be null");
			
			boolean newStream = false;
			
			Reader r = input.getCharacterStream();
			String url = input.getSystemId();
			String encoding = input.getEncoding();
			if (null == r) {
				InputStream is = input.getByteStream();
				if (null == is) {
					Preconditions.checkNotNull(url,
							"Reader, InputStream, and SystemID mustn't ALL be null: %s",
							input);
					is = openStream(url);
					newStream = true;
				}
				
				try {
					r = getReaderFromStream(is,
							null == encoding ? null : Charset.forName(encoding));
				} finally {
					if (null == r && newStream) // exception was thrown, so silence #close
						closeQuietly(is,
								"silenced close-error after failed to create InputStreamReader(InputSource(%s, %s))",
								url, is);
				}
			}
			
			try {
				sch.startDocument();
				T retVal = sxr.parse(sch, r, input.getSystemId());
				sch.endDocument();
				
				if (newStream) {
					newStream = false;
					r.close(); // IOE
				}
				return retVal;
			} finally {
				if (newStream) // exception was thrown by #parse, so silence #close
					closeQuietly(r,
							"silenced close-error after failed to parse(InputSource(%s, %s))",
							url, r);
			}
		}
		
		/**
		 * may be over-ridden by sub-classes to specify URLConnection details
		 * @param systemId not null
		 */
		protected InputStream openStream(String systemId) throws IOException {
			Preconditions.checkNotNull(systemId, "systemId mustn't be null");
			
			return new URL(systemId).openStream();
		}
		/**
		 * may be over-ridden by sub-classes to specify e.g. a specific charset
		 * 
		 * @param is not null
		 * @param charset may be null
		 * 
		 * TODO this method should be updated to implement "guessing" rather than use system default charset
		 * @see https://www.w3.org/TR/REC-xml/#sec-guessing
		 */
		protected Reader getReaderFromStream(InputStream is, Charset charset) {
			Preconditions.checkNotNull(is, "inputstream mustn't be null");
			
			return new InputStreamReader(is, MoreObjects.firstNonNull(
					charset,
					Charset.defaultCharset()));
		}
	}
	
	public static class SimpleXMLReader implements XMLReader {
		final ParseManager SXP;
		final SimpleXmlParser<?> SXR;
		/** @see ParseManager#INSTANCE */
		public SimpleXMLReader(SimpleXmlParser<?> sxr) {
			this(ParseManager.INSTANCE, sxr);
		}
		public SimpleXMLReader(ParseManager sxp, SimpleXmlParser<?> sxr) {
			SXP = sxp;
			SXR = sxr;
		}
		
		@Override
		public void parse(InputSource input) throws IOException, SAXException {
			SXP.parseManaged(SXR, getContentHandler(), input);
		}
		@Override
		public void parse(String systemId) throws IOException, SAXException {
			parse(new InputSource(systemId));
		}
		
		private SimpleContentHandler sch;
		@Override
		public void setContentHandler(ContentHandler handler) {
			sch = SimpleContentHandler.of(handler);
		}
		@Override
		public SimpleContentHandler getContentHandler() {
			return sch;
		}
		
		/* * TODO * */
		@Override
		public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
			throw new UnsupportedOperationException("TODO"); // TODO
		}
		@Override
		public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
			throw new UnsupportedOperationException("TODO"); // TODO
		}
		@Override
		public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
			throw new UnsupportedOperationException("TODO"); // TODO
		}
		@Override
		public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
			throw new UnsupportedOperationException("TODO"); // TODO
		}
		@Override
		public void setEntityResolver(EntityResolver resolver) {
			throw new UnsupportedOperationException("TODO"); // TODO
		}
		@Override
		public EntityResolver getEntityResolver() {
			throw new UnsupportedOperationException("TODO"); // TODO
		}
		@Override
		public void setDTDHandler(DTDHandler handler) {
			throw new UnsupportedOperationException("TODO"); // TODO
		}
		@Override
		public DTDHandler getDTDHandler() {
			throw new UnsupportedOperationException("TODO"); // TODO
		}
		@Override
		public void setErrorHandler(ErrorHandler handler) {
			throw new UnsupportedOperationException("TODO"); // TODO
		}
		@Override
		public ErrorHandler getErrorHandler() {
			throw new UnsupportedOperationException("TODO"); // TODO
		}
	}
	
	/**
	 * opens a stream, then converts it to an InputStreamReader,
	 * closing the former (quietly) if the latter fails.
	 * 
	 * @param charset null to use default charset
	 * 
	 * @deprecated not used
	 * TODO move this to fwb-ciao
	 */
	static Reader openReader(URL url, Charset charset) throws IOException {
		Preconditions.checkNotNull(url, "url mustn't be null");
		if (null == charset)
			charset = Charset.defaultCharset();
		
		Reader retVal = null;
		InputStream is = url.openStream(); try {
			retVal = new InputStreamReader(is, charset);
		} finally {
			if (null == retVal) // exception was thrown, so silence #close
				closeQuietly(is,
						"silenced close-error after failed to create InputStreamReader(%s)",
						url);
		}
		return retVal;
	}
	
	/**
	 * TODO move this to fwb-ciao
	 */
	static void closeQuietly(Closeable c) {
		closeQuietly(c, "silenced close-error");
	}
	/**
	 * TODO move this to fwb-ciao
	 * 
	 * @param logError a message to log (error-level) if closing fails
	 */
	static void closeQuietly(Closeable c, String logError, Object... logErrorArgs) {
		try {
			c.close();
		} catch (Throwable t) {
			LOG.error(
					String.format(logError, logErrorArgs),
					t);
		}
	}
}
