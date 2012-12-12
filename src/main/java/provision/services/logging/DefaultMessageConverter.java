/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/logging/DefaultMessageConverter.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.logging;

import java.util.Map;

import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import provision.util.StringUtil;

/**
 * Converts the standard conversion character {@link PSPatternLayout#MESSAGE_CONVERSION}. This is
 * the default message pattern converter used by the {@link PSPatternParser} when no other message
 * converter is specified in the Log4J configuration file. In addition to the log message, it also
 * converts the Throwable object, if any, into a string.
 * <p>
 * To configure this converter, set the appender layout property <code>converterProperties</code>.
 * 
 * <pre>
 * log4j.appender.stdout.layout.converterProperties=m:...
 * </pre>
 * 
 * If the event message is of type {@link LoggingMessage}, it outputs at most 3 tokens, as follows:<br>
 * {ID:event-id}{MSG:event-message}{PARMS:key1=value;key2=value2;...;}
 * 
 * @author Iulian Vlasov
 * @since TurboPrv
 */
public class DefaultMessageConverter extends PatternConverter {

	static final String DEFAULT_TOKEN_PREFIX = "{";
	static final String DEFAULT_TOKEN_SUFFIX = "}";
	static final String DEFAULT_TOKEN_SEPARATOR = ":";
	static final String DEFAULT_EVENT_ID_TOKEN = "ID";
	static final String DEFAULT_EVENT_MSG_TOKEN = "MSG";
	static final String DEFAULT_EVENT_PARMS_TOKEN = "PARMS";
	static final String DEFAULT_PARM_NAME_SEPARATOR = ";";
	static final String DEFAULT_PARM_VALUE_SEPARATOR = "=";

	private String tokenPrefix = DEFAULT_TOKEN_PREFIX;
	private String tokenSuffix = DEFAULT_TOKEN_SUFFIX;
	private String tokenSeparator = DEFAULT_TOKEN_SEPARATOR;
	private String eventIdToken = DEFAULT_EVENT_ID_TOKEN;
	private String eventMsgToken = DEFAULT_EVENT_MSG_TOKEN;
	private String eventParmsToken = DEFAULT_EVENT_PARMS_TOKEN;
	private String parmNameSeparator = DEFAULT_PARM_NAME_SEPARATOR;
	private String parmValueSeparator = DEFAULT_PARM_VALUE_SEPARATOR;
	private int stackTraceSize = -1;

	/*************************************
	 * Converter configurable properties *
	 *************************************/
	public void setTokenPrefix(String s) {
		this.tokenPrefix = s;
	}

	public void setTokenSuffix(String tokenSuffix) {
		this.tokenSuffix = tokenSuffix;
	}

	public void setTokenSeparator(String tokenSeparator) {
		this.tokenSeparator = tokenSeparator;
	}

	public void setEventIdToken(String eventIdToken) {
		this.eventIdToken = eventIdToken;
	}

	public void setEventMsgToken(String eventMsgToken) {
		this.eventMsgToken = eventMsgToken;
	}

	public void setEventParmToken(String eventParmToken) {
		this.eventParmsToken = eventParmToken;
	}

	public void setParmNameSeparator(String parmNameSeparator) {
		this.parmNameSeparator = parmNameSeparator;
	}

	public void setParmValueSeparator(String parmValueSeparator) {
		this.parmValueSeparator = parmValueSeparator;
	}

	public void setStackTraceSize(int i) {
		this.stackTraceSize = i;
	}

	/**
	 * Use the event message object and throwable information to render the message as a plain
	 * string.
	 * 
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

			renderedMsg = renderMessage(message, t);
		}
		else {
			renderedMsg = event.getRenderedMessage();
		}

		return renderedMsg;
	}

	/**
	 * @param logMsg
	 * @param t
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String renderMessage(Object logMsg, Throwable t) {
		String eventId = TurboLogMessage.getId(logMsg);
		String eventMsg = TurboLogMessage.getMessage(logMsg);
		Object parameters = TurboLogMessage.getParameters(logMsg);

		StringBuilder sb = new StringBuilder();

		// event id
		if (!StringUtil.isEmpty(eventId)) {
			sb.append(tokenPrefix).append(eventIdToken).append(tokenSeparator).append(eventId).append(tokenSuffix);
		}

		// event parameters
		Map eventCtx = null;
		if (parameters instanceof Map) {
			eventCtx = (Map) parameters;
		}

		if (eventCtx != null) {
			sb.append(tokenPrefix).append(eventParmsToken).append(tokenSeparator);

			for (Object key : eventCtx.keySet()) {
				Object value = eventCtx.get(key);
				sb.append(key).append(parmValueSeparator).append(value).append(parmNameSeparator);
			}
			sb.append(tokenSuffix);
		}

		// add actual message
		if (!StringUtil.isEmpty(eventMsg)) {
			sb.append(tokenPrefix).append(eventMsgToken).append(tokenSeparator).append(eventMsg).append(tokenSuffix);
		}

		// add throwable text
		if (t != null) {
			sb.append(StringUtil.NEWLINE);
			sb.append(StringUtil.toString(t, stackTraceSize));
		}

		return sb.toString();
	}
}