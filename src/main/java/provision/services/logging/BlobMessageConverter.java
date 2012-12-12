/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/logging/BlobMessageConverter.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.logging;

import java.util.Map;

import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import provision.util.StringUtil;

/**
 * Converts the standard conversion character {@link PSPatternLayout#MESSAGE_CONVERSION}. This
 * custom message converter obfuscates a LoggingEvent into a blob of characters, by replacing
 * end-of-line characters with a custom separator and 'wrapping' lines to a specified length.
 * <p>
 * To use this converter with the custom {@link PSPatternLayout}, specify this property in the Log4J
 * configuration file:
 * 
 * <pre>
 * log4j.appender.YourAppender.layout.patternConverters=m:provision.common.logging.BlobMessageConverter
 * </pre>
 * <p>
 * To configure this converter, specify this property in the Log4J configuration file:
 * 
 * <pre>
 * log4j.appender.YourAppender.layout.converterProperties=m:eolReplacement=|&circ;|;,maxLineLength=255
 * </pre>
 * 
 * @author Iulian Vlasov
 * @since TurboPrv
 */
public class BlobMessageConverter extends PatternConverter {

	static final String DEFAULT_EOL_REPLACEMENT = "|^|";
	static final String NO_EOL_REPLACEMENT = "\n";
	static final String EVENT_ID_TOKEN = "EVENT";
	static final String EVENT_MSG_TOKEN = "EVENT_MSG";
	static final String EXCEPTION_TOKEN = "EVENT_EXCEPTION";
	static final String TEXT_DELIM = "\"";
	static final String NULL_STR = "null";
	
	static final char VALUE_SEP = '=';
	static final char PAIR_SEP = ';';
	static final char PAIR_SEP_REPLACEMENT_CHAR = '!';
	static final char SPACE = ' ';

	static final int DEFAULT_LINE_LENGTH = 255;
	static final int LOG_HEADER_LENGTH = 110;

	private String eolReplacement = DEFAULT_EOL_REPLACEMENT;
	private int maxLineLength = DEFAULT_LINE_LENGTH;

	/**
	 * @param s
	 */
	public void setEolReplacement(String s) {
		this.eolReplacement = (StringUtil.isEmpty(s) ? NO_EOL_REPLACEMENT : s);
	}

	/**
	 * @param i
	 */
	public void setMaxLineLength(int i) {
		this.maxLineLength = i;
	}

	/**
	 * @param event
	 * @return
	 */
	@Override
	protected String convert(LoggingEvent event) {
		String renderedMsg = null;

		Object message = event.getMessage();
		if (TurboLogMessage.isTurboMessage(message)) {
			ThrowableInformation thrInfo = event.getThrowableInformation();
			Throwable t = (thrInfo == null ? null : thrInfo.getThrowable());

			renderedMsg = renderBlobMessage(message, t);
		}
		else {
			renderedMsg = event.getRenderedMessage();
		}

		return renderedMsg;
	}

	/* -------- */
	/* BLOB API */
	/* -------- */
	/**
	 * Composes the message to be inserted with log4j from the supplied eventid and tokens.
	 */
	@SuppressWarnings("unchecked")
	private String renderBlobMessage(Object logMsg, Throwable t) {
		String eventId = TurboLogMessage.getId(logMsg);
		String eventMsg = TurboLogMessage.getMessage(logMsg);
		Map tokens = TurboLogMessage.getParameters(logMsg);

		String renderedMsg = eventId;

		if ((t == null) && (tokens == null)) {
			renderedMsg = composeMsg(eventId, eventMsg);
		}
		else if ((t == null) && (eventMsg == null)) {
			renderedMsg = composeMsg(eventId, tokens);
		}
		else {
			renderedMsg = composeMsg(eventId, tokens, eventMsg, t);
		}

		return renderedMsg;
	}

	@SuppressWarnings("unchecked")
	private String composeMsg(String eventId, Map tokens) {

		StringBuffer b = new StringBuffer();
		b.append(EVENT_ID_TOKEN);
		b.append(VALUE_SEP);
		b.append(eventId);
		b.append(PAIR_SEP);
		b.append(SPACE);
		int len = LOG_HEADER_LENGTH + b.length(); // initial value

		if (!StringUtil.isEmpty(tokens)) {
			// name=value;
			for (Object key : tokens.keySet()) {
				Object value = tokens.get(key);
				String t = (key != null ? key.toString() : "") + VALUE_SEP + checkValueFormat(value != null ? value.toString() : NULL_STR) + PAIR_SEP + SPACE;

				// Checks to see if a newline '\n' should be inserted
				if (len + t.length() > maxLineLength) {
					// Starts a new line
					b.append(eolReplacement);
					len = 0;
				}
				b.append(t);
				len += t.length();
			}

			b.setLength(b.length() - 2);
		}
		return removeEOLCharacter(b.toString());
	}

	/**
	 * Composes the log4j message from eventId and raw text.
	 */
	private String composeMsg(String eventId, String eventMsg) {
		StringBuffer b = new StringBuffer();
		b.append(EVENT_ID_TOKEN);
		b.append(VALUE_SEP);
		b.append(eventId);
		
		if (eventMsg != null) {
			b.append(PAIR_SEP);
			b.append(SPACE);
			b.append(EVENT_MSG_TOKEN);
			b.append(VALUE_SEP);
			b.append(checkValueFormat(eventMsg));
		}

		return removeEOLCharacter(b.toString());
	}

	/**
	 * Composes the log4j message from eventId, tokens (if any), eventMsg (if any) and throwable (if
	 * any).
	 */
	@SuppressWarnings("unchecked")
	private String composeMsg(String eventId, Map tokens, String eventMsg, Throwable t) {

		String s = composeMsg(eventId, tokens);
		StringBuffer b = new StringBuffer(s);

		if (eventMsg != null) {
			String em = EVENT_MSG_TOKEN + VALUE_SEP + checkValueFormat(eventMsg);
			append(b, em);
		}

		if (t != null) {
			StackTraceElement[] stackTrace = t.getStackTrace();
			String stackT = "";
			if (stackTrace != null && stackTrace.length > 0) {
				for (int i = 0; i < stackTrace.length; i++) {
					stackT += stackTrace[i] + eolReplacement;
				}
			}

			String tm = EXCEPTION_TOKEN + VALUE_SEP + checkValueFormat(stackT);
			append(b, tm);
		}

		return removeEOLCharacter(b.toString());
	}

	/**
	 * Appends the given String to the StringBuffer, but first insert a newline "\n" in the
	 * appropriate place to ensure each line is less than LINE_WRAP_LENGTH long. The String s will
	 * not be broken up into multiple lines even if it is longer than LINE_WRAP_LENGTH.
	 */
	private void append(StringBuffer b, String s) {
		String bstr = b.toString();
		int lastNewLine = bstr.lastIndexOf(eolReplacement);
		if (lastNewLine == -1) {
			if (LOG_HEADER_LENGTH + bstr.length() + s.length() > maxLineLength) {
				b.append("" + PAIR_SEP + eolReplacement + s);
				return;
			}
		}
		else {
			if (bstr.length() - lastNewLine + s.length() > maxLineLength) {
				b.append("" + PAIR_SEP + eolReplacement + s);
				return;
			}
		}
		b.append("" + PAIR_SEP + SPACE + s);
	}

	/**
	 * Escapes text if required.
	 */
	private String checkValueFormat(String in) {

		if (in.indexOf(PAIR_SEP) != -1) in = in.replace(PAIR_SEP, PAIR_SEP_REPLACEMENT_CHAR);
		if (in.indexOf(" ") != -1) {
			in = TEXT_DELIM + in + TEXT_DELIM;
		}
		return removeEOLCharacter(in);

	}

	private String removeEOLCharacter(String in) {
		// if string is null or empty then return, nothing we can do
		if ((in == null) || in.equals("")) {
			return in;
		}
		// if the EOL Separator set in the System Parameter is \n, then return
		// because we do not need to do anything
		if ("\n".equals(eolReplacement)) {
			return in;
		}
		return in.replaceAll("\n", eolReplacement).replaceAll("\r", eolReplacement).replaceAll(
				System.getProperty("line.separator"), eolReplacement);
	}

}
