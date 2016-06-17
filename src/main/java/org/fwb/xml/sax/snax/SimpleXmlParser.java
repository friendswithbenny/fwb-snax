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
import org.xml.sax.InputSource;

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
 * 1. implement the more consistent contract of {@link SimpleXmlReader#parse(Reader, String)}
 * 2. delegate to {@link #parse(SimpleXmlReader, InputSource)} to provide stream management.-
 */
public class SimpleXmlParser {
	static final Logger LOG = LoggerFactory.getLogger(SimpleXmlParser.class);
	
	/**
	 * a simple expression of character-stream parsing logic
	 * @param <T> a return value of the implementor's choice
	 */
	public interface SimpleXmlReader<T> {
		/**
		 * a common basic signature for implementors of of character-stream parsing logic.
		 * this method should expect to be called within a managed context,
		 * and need NOT close the given Reader when finished nor in the event of an error.
		 * 
		 * @param r not null; do not close
		 * @param systemId possibly null
		 * @return a value of the implementor's choice
		 */
		T parse(Reader r, String systemId);
	}
	
	final Charset CHARSET;
	public SimpleXmlParser() {
		this(Charset.defaultCharset());
	}
	public SimpleXmlParser(Charset charset) {
		CHARSET = Preconditions.checkNotNull(charset, "charset mustn't be null");
	}
	
	/**
	 * calls the {@link SimpleXmlReader#parse(Reader, String)} method,
	 * if necessary obtaining a character stream (via {@link #openStream(String)}) and closing it.
	 */
	public <T> T parseManaged(SimpleXmlReader<T> rp, InputSource input) throws IOException {
		Preconditions.checkNotNull(rp, "ReaderParser mustn't be null");
		Preconditions.checkNotNull(input, "InputSource mustn't be null");
		
		boolean newStream = false;
		
		Reader r = input.getCharacterStream();
		String url = input.getSystemId();
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
				r = new InputStreamReader(is, CHARSET);
			} finally {
				if (null == r && newStream) // exception was thrown, so silence #close
					closeQuietly(is,
							"silenced close-error after failed to create InputStreamReader(InputSource(%s, %s))",
							url, is);
			}
		}
		
		try {
			T retVal = rp.parse(r, input.getSystemId());
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
	 */
	protected InputStream openStream(String systemId) throws IOException {
		return new URL(systemId).openStream();
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
