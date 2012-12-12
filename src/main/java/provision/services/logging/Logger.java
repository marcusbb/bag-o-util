/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/logging/Logger.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.logging;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.MDC;

import provision.services.propagation.PropagationContext;
import provision.services.propagation.PropagationContextFactory;

/**
 * This is an utility class rather than a Logger but the name is kept for backward compatibility
 * since it is referenced throughout the entire application.
 * <p>
 * It provides static utility methods that correspond to each severity level: {@link #fatal FATAL},
 * {@link #error ERROR}, {@link #warn WARN}, {@link #info INFO}, {@link #debug DEBUG},
 * {@link #trace TRACE}. These methods delegate the logging to the Jakarta's Log4J framework,
 * passing along a {@link LoggingMessage} object containing all or some of these elements:
 * <ul>
 * <li>Event identifier string</li>
 * <li>Event message string</li>
 * <li>Event parameters object (usually a Map) describing the context in which the logging event
 * occurs</li>
 * </ul>
 * <p>
 * This implementation forces the application to log asynchronously unless the system property
 * <code>app.logging.noasync</code> is set.
 * <p>
 * Callers can invoke the logging methods passing any number and types of arguments, in any order.
 * These are the rules for processing the arguments:
 * <ul>
 * <li>the first String argument is considered the event identifier</li>
 * <li>the second String argument is considered the event message</li>
 * <li>the last Map argument is considered the map containing the event parameters (tokens). If you
 * care about their order, you should pass an instance of {@link PSTokenDictionary}.</li>
 * <li>the 3rd, 5th, 7th (... and so on) argument is considered a parameter key and the next
 * argument is the parameter value</li>
 * <li>the last Throwable argument is considered the event exception, if any</li>
 * <li>nulls are ignored</li>
 * </ul>
 * For logging an error messsage with event id, message, exception and pre-built map of parameters:<br>
 * <code>Logger.error(getClass().getName(), "event ID", "event Msg", exception, parms);</code> <br>
 * or<br>
 * <code>Logger.error(getClass().getName(), exception, parms, "event ID", "event Msg");</code>
 * <p>
 * For logging an error messsage with event id, message, and inline parameters:<br>
 * <code>Logger.error(getClass().getName(), "event ID", "event Msg", parmKey, parmValue);</code>
 * <p>
 * You can log formatted messages, providing that you conform to the {@link java.text.MessageFormat}
 * rules and you provide the objects required to format the event message. In this case you may not
 * need the event id and/or event parameters.<br>
 * <code>Logger.info(getClass().getName(), "This is {0} formatted string with {1} args", "my", "two");</code>
 * <p>
 * This implementation is not concerned with text massaging and formatting, leaving this task at the
 * discretion of more suitable components of the Log4J framework, which are the pattern converters.
 * 
 * @author Iulian Vlasov
 * @since TurboPrv
 */
public class Logger {

	/**
	 * MDC key for storing the thread of the caller issuing the logging request. This may be
	 * different than the thread outputting the log entries, if the logging mode is asynchronous.
	 */
	public static final String CALLER_THREAD = "CALLER_THREAD";

	/**
	 * MDC key for storing the time of the logging request.
	 */
	public static final String CALLER_TIMESTAMP = "CALLER_TIMESTAMP";

	/**
	 * MDC key for storing the location information of the caller issuing the logging request.
	 */
	public static final String CALLER_LOCATION = "CALLER_LOCATION";

	/**
	 * MDC key for storing the logger used by the caller issuing the logging request. This may be
	 * different than the actual logger used in the running thread if the "Off" switch intercepts
	 * the request.
	 */
	public static final String CALLER_LOGGER = "CALLER_LOGGER";

	public static final String CALLER_CLASS = "CALLER_CLASS";
	
	private static final Pattern formatPattern = Pattern.compile("(\\{\\d+\\})");
	private static final int STACK_BP = 2;

	/**
	 * A concurrent map to make non-blocking logger lookup.
	 */
	private static final Map<String,org.apache.log4j.Logger> loggerMap = new ConcurrentHashMap<String, org.apache.log4j.Logger>();
	/*
	 * Configure Log4J repository.
	 */
	static {
		LogConfigurator.configure();
	}

	/**
	 * Log an entry with FATAL level.
	 * 
	 * @param loggerName
	 * @param args
	 * @see {@link Logger#log(Level, String, Object...)}
	 */
	public static void fatal(String loggerName, Object... args) {
		log(Level.FATAL, loggerName, args);
	}

	/**
	 * eXtended implementation for logging a FATAL event. It dynamically infers the caller issuing
	 * the logging request and the appropriate logger based on the class/package.
	 * 
	 * @param args
	 */
	public static void fatalx(Object... args) {
		log(Level.FATAL, getLoggerName(), args);
	}

	/**
	 * Log an entry with ERROR level.
	 * 
	 * @param loggerName
	 * @param args
	 * @see {@link Logger#log(Level, String, Object...)}
	 */
	public static void error(String loggerName, Object... args) {
		log(Level.ERROR, loggerName, args);
	}

	/**
	 * eXtended implementation for logging a ERROR event. It dynamically infers the caller issuing
	 * the logging request and the appropriate logger based on the class/package.
	 * 
	 * @param args
	 */
	public static void errorx(Object... args) {
		log(Level.ERROR, getLoggerName(), args);
	}

	/**
	 * Log an entry with WARN level.
	 * 
	 * @param loggerName
	 * @param args
	 * @see {@link Logger#log(Level, String, Object...)}
	 */
	public static void warn(String loggerName, Object... args) {
		log(Level.WARN, loggerName, args);
	}

	/**
	 * eXtended implementation for logging a WARN event. It dynamically infers the caller issuing
	 * the logging request and the appropriate logger based on the class/package.
	 * 
	 * @param args
	 */
	public static void warnx(Object... args) {
		log(Level.WARN, getLoggerName(), args);
	}

	/**
	 * Log an entry with INFO level.
	 * 
	 * @param loggerName
	 * @param args
	 * @see {@link Logger#log(Level, String, Object...)}
	 */
	public static void info(String loggerName, Object... args) {
		log(Level.INFO, loggerName, args);
	}

	/**
	 * eXtended implementation for logging a INFO event. It dynamically infers the caller issuing
	 * the logging request and the appropriate logger based on the class/package.
	 * 
	 * @param args
	 */
	public static void infox(Object... args) {
		log(Level.INFO, getLoggerName(), args);
	}

	/**
	 * Log an entry with DEBUG level.
	 * 
	 * @param loggerName
	 * @param args
	 * @see Logger#log(Level, String, Object...)
	 */
	public static void debug(String loggerName, Object... args) {
		log(Level.DEBUG, loggerName, args);
	}

	/**
	 * eXtended implementation for logging a DEBUG event. It dynamically infers the caller issuing
	 * the logging request and the appropriate logger based on the class/package.
	 * 
	 * @param args
	 */
	public static void debugx(Object... args) {
		log(Level.DEBUG, getLoggerName(), args);
	}

	/**
	 * Log an entry with TRACE level.
	 * 
	 * @param loggerName
	 * @param args
	 * @see {@link Logger#log(Level, String, Object...)}
	 */
	public static void trace(String loggerName, Object... args) {
		log(Level.TRACE, loggerName, args);
	}

	/**
	 * eXtended implementation for logging a TRACE event. It dynamically infers the caller issuing
	 * the logging request and the appropriate logger based on the class/package.
	 * 
	 * @param args
	 */
	public static void tracex(Object... args) {
		log(Level.TRACE, getLoggerName(), args);
	}

	/**
	 * Retrieves a logger making sure our custom configuration takes place.
	 * 
	 * @param name
	 * @return
	 */
	public static org.apache.log4j.Logger getLogger(String name) {
		org.apache.log4j.Logger logger =  loggerMap.get(name);
		if (logger == null ) {
			logger =  org.apache.log4j.Logger.getLogger(name);
			loggerMap.put(name, logger);
		}
		
		return logger;
	}

	/**
	 * This is the meat of this class. Details such as how and what gets logged are delegated to the
	 * logging framework, particularly to the appropriate formatters and appenders.
	 * 
	 * @param level
	 * @param loggerName
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	static void log(Level level, String loggerName, Object... args) {
		org.apache.log4j.Logger logger = getLogger(loggerName);
		if (!logger.isEnabledFor(level)) return;

		MDC.put(CALLER_TIMESTAMP, System.currentTimeMillis());
		MDC.put(CALLER_THREAD, Thread.currentThread().getName());
		MDC.put(CALLER_CLASS, Logger.class);
		PropagationContext pCtx = PropagationContextFactory.get();
		if (pCtx != null && pCtx.isInitialized()) {
			Iterator<String> keys = pCtx.keys();
			while(keys.hasNext()) {
				String key = keys.next();
				MDC.put(key, pCtx.getStringCtx(key));
			}
		}
		org.apache.log4j.Logger offLogger = LogConfigurator.getOffSwitchLogger();
		if (offLogger != null) {
			logger = offLogger;
			MDC.put(CALLER_LOGGER, loggerName);
		}

		String eventId = null;
		String eventMsg = null;
		Map eventParms = null;
		Throwable t = null;
		Object printable = null;

		for (int i = 0; i < args.length; i++) {
			if (args[i] == null) continue;

			if (Throwable.class.isAssignableFrom(args[i].getClass())) {
				t = (Throwable) args[i];
			}
			else if (Map.class.isAssignableFrom(args[i].getClass())) {
				Map callerMap = (Map) args[i];
				// Check whether cloning is required before sharing it with other threads.
				boolean makeSafeClone = (LogConfigurator.useVolatileEvents() && (callerMap instanceof HashMap));
				eventParms = (makeSafeClone ? (Map) ((HashMap) callerMap).clone() : callerMap);
			}
			else if (String.class.isAssignableFrom(args[i].getClass())) {
				if (eventMsg == null) {
					int parmCount = getParameterCount((String) args[i]);
					if ((parmCount > 0) && (i + parmCount < args.length)) {
						Object[] dest = new Object[parmCount];
						System.arraycopy(args, i + 1, dest, 0, parmCount);
						eventMsg = MessageFormat.format((String) args[i], dest);

						i = i + parmCount;
					}
					else if (eventId == null) {
						eventId = (String) args[i];
					}
					else {
						eventMsg = (String) args[i];
					}
				}
				else {
					// build event parameters map
					String key = (String) args[i];
					if (i < args.length - 1) {
						Object parm = args[++i];
						if (eventParms == null) {
							eventParms = new PSTokenDictionary((int) ((args.length - i + 1) / 0.75));
						}

						eventParms.put(key, parm);
					}
				}
			}
			else {
				printable = args[i];
			}
		}

		Object logObject = null;
		if ((eventId == null) && (eventMsg == null) && (eventParms == null)) {
			logObject = printable;
		}
		else {
			logObject = TurboLogMessage.create(eventId, eventMsg, eventParms);
		}
		
		logger.log(level, logObject, t);
	}

	/**
	 * Called by the eXtended logging API methods, stores the caller info in the MDC and returns the
	 * classname of the caller issuing the logging request.
	 * 
	 * @return
	 */
	private static String getLoggerName() {
		StackTraceElement[] st = new Throwable().getStackTrace();
		if (st.length < STACK_BP) {
			// @paranoia: should never happen
			return "";
		}
		else {
			StackTraceElement ste = st[STACK_BP];
			String loggerName = ste.getClassName();
			StringBuilder sb = new StringBuilder(loggerName).append(':').append(ste.getMethodName()).append(':')
					.append(ste.getLineNumber());
			MDC.put(CALLER_LOCATION, sb.toString());
			return loggerName;
		}
	}

	/**
	 * Used to detect if the event message is formatted and has to be combined with the event
	 * parameters.
	 * 
	 * @param input
	 * @return
	 */
	private static int getParameterCount(String input) {
		Matcher m = formatPattern.matcher(input);
		int count = 0;
		while (m.find()) {
			count++;
		}

		return count;
	}

	public static void loadClass() {
	}
}
