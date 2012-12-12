/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/logging/LoggingMessage.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.logging;

/**
 * The LoggingMessage class contains all the fields required
 * for sending a logging message object to the MessageLoggerBean
 * logging message listener
 *
 * @author		Snezana Visnjic-Obucina
 * @version		
 * @see     
 * @since   
 */
public class LoggingMessage implements java.io.Serializable {

	private int loggingLevel;
    private String caller;
    private String message;
    private Throwable exception;
	private Object origThread;
	private Object origTimeStamp;

    public LoggingMessage(int loggingLevel, String caller, String message) {
        this.loggingLevel = loggingLevel;
        this.caller = caller;
        this.message = message;
    }

    public LoggingMessage(int loggingLevel, String caller, String message, Throwable exception) {
        this(loggingLevel, caller, message);
        this.exception = exception;
    }

    public int getLoggingLevel() { return loggingLevel; }

    public String getCaller() { return caller; }

    public String getMessage() { return message; }

    public Throwable getException() { return exception; }

	public Object getOrigThread() { return origThread; }

	public void setOrigThread(Object ti) { 
		origThread = ti; 
	}

	public Object getOrigTimeStamp() { return origTimeStamp; }

	public void setOrigTimeStamp(Object ts) { 
		origTimeStamp = ts; 
	}

}
