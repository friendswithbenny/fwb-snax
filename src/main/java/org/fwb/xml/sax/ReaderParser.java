package org.fwb.xml.sax;

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
 * this extension to the {@link XMLReader} interface defines
 * what I claim is the most logical method for any text-parser to implement.
 * 
 * in this API, the {@link Reader} is guaranteed to be resolved,
 * and it's intended to be invoked inside wrapper architecture/framework
 * which externally provides resource-management ({@link Closeable#close()}).
 * 
 * @deprecated TODO remove this, it's information-only,
 * a decent implementation of a bad-choice for the API.
 * I'll replace this with a simple wrapped parse(InputStream) method,
 * performing both these purposes (ensuring Reader, and managing resources accordingly)
 */
public interface ReaderParser {
	static final Logger LOG = LoggerFactory.getLogger(ReaderParser.class);
	
	/**
	 * @param r not null; do not close
	 * @param systemId possibly null
	 */
	void parse(Reader r, String systemId);
	
	class ReadAndParse {
		static void parse(InputSource input, ReaderParser rp) throws IOException {
			Preconditions.checkNotNull(input, "InputSource mustn't be null");
			Preconditions.checkNotNull(rp, "ReaderParser mustn't be null");
			
			boolean newStream = false;
			
			Reader r = input.getCharacterStream();
			String url = input.getSystemId();
			if (null == r) {
				InputStream is = input.getByteStream();
				if (null == is) {
					Preconditions.checkNotNull(url,
							"Reader, InputStream, and SystemID mustn't ALL be null");
					is = new URL(url).openStream(); // IOE
					newStream = true;
				}
				
				try {
					r = new InputStreamReader(is, Charset.defaultCharset());
				} finally {
					if (null == r && newStream) // exception was thrown, so silence.
						closeQuietly(is,
								String.format("silenced close-error after failed to create InputStreamReader(InputSource(%s, %s))", url, is));
				}
			}
			
			try {
				rp.parse(r, input.getSystemId());
				if (newStream) {
					newStream = false;
					r.close(); // IOE
				}
			} finally {
				if (newStream) // exception was thrown, so silence
					closeQuietly(r,
							String.format("silenced close-error after failed to parse(InputSource(%s, %s))", url, r));
			}
		}
		
		/**
		 * opens a stream, then converts it to an InputStreamReader,
		 * closing the former (quietly) if the latter fails.
		 */
		static Reader openReader(URL url) throws IOException {
			Reader retVal = null;
			InputStream is = url.openStream(); try {
				retVal = new InputStreamReader(is, Charset.defaultCharset());
			} finally {
				if (null == retVal) // exception was thrown, so silence.
					closeQuietly(is,
							String.format("silenced close-error after failed to create InputStreamReader(%s)", url));
			}
			return retVal;
		}
		
		static void closeQuietly(Closeable c, String errorMessage) {
			try {
				c.close();
			} catch (Throwable t) {
				LOG.error(errorMessage, t);
			}
		}
	}
}
