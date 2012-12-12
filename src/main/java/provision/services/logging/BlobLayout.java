/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/logging/BlobLayout.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.logging;

import java.util.Map;

import org.apache.log4j.SimpleLayout;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import provision.util.StringUtil;

/**
 * 
 * @author Iulian Vlasov
 * @since TurboPrv
 */
public class BlobLayout extends SimpleLayout implements LoggerConstants {

	private static final String NO_SEPARATOR = "\n";

	private String eolSeparator = DEFAULT_EOL_DELIM;

	public BlobLayout() {
	}

	public void setEolSeparator(String s) {
		this.eolSeparator = (StringUtil.isEmpty(s) ? NO_SEPARATOR : s);
	}

	/**
	 * @param event
	 * @return
	 * @see org.apache.log4j.SimpleLayout#format(org.apache.log4j.spi.LoggingEvent)
	 */
	@Override
	public String format(LoggingEvent event) {
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

	@SuppressWarnings("unchecked")
	private String renderBlobMessage(Object logMsg, Throwable t) {
		String eventId = TurboLogMessage.getId(logMsg);
		String eventMsg = TurboLogMessage.getMessage(logMsg);
		Map tokens = TurboLogMessage.getParameters(logMsg);

		String renderedMsg = eventId;

		if (t == null) {
			if (tokens == null) {
				renderedMsg = composeMsg(eventId, eventMsg);
			}
			else {
				renderedMsg = composeMsg(eventId, tokens);
			}
		}
		else {
			renderedMsg = composeMsg(eventId, tokens, eventMsg, t);
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
	private String composeMsg(String eventId, Map tokens) {

		StringBuffer b = new StringBuffer();
		b.append(EVENT_TOKEN);
		b.append(VALUE_SEP);
		b.append(eventId);
		b.append(PAIR_SEP);
		b.append(SPACE);
		int len = LOG_HEADER_LENGTH + b.length(); // initial value

		if (tokens != null && tokens.size() > 0) {
			for (Object token : tokens.keySet()) {
				if (token != null) {
					String tokenString = token.toString();
					Object value = tokens.get(tokenString);
					String valueString = "";
					if (value != null) valueString = value.toString();

					// name=value;
					String t = tokenString + VALUE_SEP + checkValueFormat(valueString) + PAIR_SEP + SPACE;

					// Checks to see if a newline '\n' should be inserted
					if (len + t.length() > LINE_WRAP_LENGTH) {
						// Starts a new line
						b.append(eolSeparator);
						len = 0;
					}
					b.append(t);
					len += t.length();
				}
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
		b.append(EVENT_TOKEN);
		b.append(VALUE_SEP);
		b.append(eventId);
		b.append(PAIR_SEP);
		b.append(SPACE);
		b.append(MSG_TOKEN);
		b.append(VALUE_SEP);
		if (eventMsg != null) b.append(checkValueFormat(eventMsg));
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
			String em = MSG_TOKEN + VALUE_SEP + checkValueFormat(eventMsg);
			append(b, em);
		}

		if (t != null) {
			StackTraceElement[] stackTrace = t.getStackTrace();
			String stackT = "";
			if (stackTrace != null && stackTrace.length > 0) {
				for (int i = 0; i < stackTrace.length; i++) {
					stackT += stackTrace[i] + eolSeparator;
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
		int lastNewLine = bstr.lastIndexOf(eolSeparator);
		if (lastNewLine == -1) {
			if (LOG_HEADER_LENGTH + bstr.length() + s.length() > LINE_WRAP_LENGTH) {
				b.append("" + PAIR_SEP + eolSeparator + s);
				return;
			}
		}
		else {
			if (bstr.length() - lastNewLine + s.length() > LINE_WRAP_LENGTH) {
				b.append("" + PAIR_SEP + eolSeparator + s);
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
		if ("\n".equals(eolSeparator)) {
			return in;
		}
		return in.replaceAll("\n", eolSeparator).replaceAll("\r", eolSeparator).replaceAll(
				System.getProperty("line.separator"), eolSeparator);
	}

}
