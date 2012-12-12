/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/logging/TurboLogMessage.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.logging;

import java.io.Serializable;
import java.util.Map;

/**
 * This object gets logged by the {@link Logger} and allows the framework to pass structured objects
 * rather than just strings, to the pattern converters.
 * <p>
 * It contains the following elements (all are optional but at least one should be not null):
 * <ul>
 * <li>Event identifier string</li>
 * <li>Event message string</li>
 * <li>Event parameters object (usually a Map) describing the context in which the logging event
 * occurs</li>
 * </ul>
 * 
 * @author Iulian Vlasov
 * @since TurboPrv
 */
public class TurboLogMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String id;
	private String message;
	private Object parameters;

	/**
	 * @param id
	 * @param message
	 * @param tokens
	 */
	public TurboLogMessage(String id, String message, Object parameters) {
		setId(id);
		setMessage(message);
		setParameters(parameters);
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id - the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message - the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the tokens
	 */
	public Object getParameters() {
		return parameters;
	}

	/**
	 * @param tokens - the tokens to set
	 */
	public void setParameters(Object parameters) {
		this.parameters = parameters;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		return sb.append(id).append(message).append(parameters).toString();
	}

	public static Object[] create(String eventId, String eventMsg, Map<?,?> eventParms) {
		return new Object[] {eventId, eventMsg, eventParms};
	}

	public static boolean isTurboMessage(Object message) {
		return ((message instanceof Object[]) && (((Object[]) message).length == 3));
	}

	public static String getId(Object message) {
		return (String) ((Object[]) message)[0];
	}
	
	public static String getMessage(Object message) {
		return (String) ((Object[]) message)[1];
	}

	public static Map<?,?> getParameters(Object message) {
		return (Map<?,?>) ((Object[]) message)[2];
	}
}
