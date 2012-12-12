/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/logging/LoggingHelper.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.logging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.LoggerRepository;

import provision.services.logging.TurboAppender.TurboLoggingServer;
import provision.util.StringUtil;

/**
 * Utility class that provides information on logging repository, loggers and appenders. It also
 * allows clients to programatically change the repository configuration such as:
 * 
 * <ul>
 * <li>(re)setting asynchronous logging for specific loggers</li>
 * <li>add/remove appenders for specific loggers</li>
 * <li>change settings for specific appenders</li>
 * </ul>
 * 
 * @author Iulian Vlasov
 * @since TurboPrv
 */
public class LoggingHelper {

	/**
	 * Convenience reference to the root logger
	 */
	public static Logger rootLogger = LogManager.getRootLogger();

	/**
	 * Convenience reference to the repository
	 */
	public static LoggerRepository repo = LogManager.getLoggerRepository();

	/**
	 * Default suffix for the dynamic async appender name
	 */
	static final String DEFAULT_ASYNC_SUFFIX = "-Turbo";

	/**
	 * Provides a detailed description of the Log4j repository.
	 * 
	 * @return StringBuilder useful for appending to other printable information.
	 */
	public static StringBuilder getRepositoryDetails() {
		StringBuilder sb = new StringBuilder(StringUtil.underline("All Loggers - Repository: " + LogConfigurator.getContextName(), '-'));

		appendValue(sb, "Master Threshold", repo.getThreshold());
		appendLine(sb, getLoggerDetails(rootLogger));

		List<Logger> loggers = getLoggers();
		for (Logger logger : loggers) {
			appendLine(sb, getLoggerDetails(logger));
		}

		return sb;
	}

	/**
	 * @param names
	 * @return
	 */
	public static StringBuilder getLoggersDetails(String[] names) {
		
		StringBuilder sb = new StringBuilder(StringUtil.underline("Selected Loggers - Repository: " + LogConfigurator.getContextName(), '-'));

		sb.append(getLoggerDetails(rootLogger));
		
		List<Logger> loggers = getLoggers();
        for (Logger logger : loggers) {
            if (logger.getAdditivity()) continue;
            
            String name = logger.getName();
            
            if (name.startsWith("org.") || name.startsWith("net.") || name.startsWith("apache")) continue;
            
            appendLine(sb, getLoggerDetails(logger));
        }

		return sb;
	}
	
	/**
	 * Provides a detailed description of a Log4j logger.
	 * 
	 * @param logger
	 * @return StringBuilder useful for appending to other loggable information.
	 */
	public static StringBuilder getLoggerDetails(Logger logger) {
		StringBuilder sb = new StringBuilder("Logger");
		appendValue(sb, "Name", logger.getName());

		Category parent = logger.getParent();
		if (parent != null) {
			appendValue(sb, "Parent", parent.getName());
		}

		appendValue(sb, "Level", logger.getEffectiveLevel());
		appendValue(sb, "Additivity", logger.getAdditivity());
		appendValue(sb, "Class", logger.getClass().getName());

		appendLine(sb, getAppendersDetails(logger, ""));

		return sb;
	}

	/**
	 * Provides a detailed description of a Log4j appender.
	 * 
	 * @param appender
	 * @param indent - use to nicely print formatted output for chained appenders
	 * @return StringBuilder useful for appending to other loggable information.
	 */
	public static StringBuilder getAppenderDetails(Appender appender, String indent) {
		StringBuilder sb = new StringBuilder(indent).append(" - Appender");
		appendValue(sb, "Name", appender.getName());
		appendValue(sb, "Class", appender.getClass().getName());

		if (appender instanceof AppenderAttachable) {
			AppenderAttachable attachable = (AppenderAttachable) appender;
			if (attachable instanceof TurboAppender) {
				appendValue(sb, "Queue Capacity", ((TurboAppender) attachable).getBufferSize());
			}

			appendLine(sb, getAppendersDetails(attachable, indent + "  "));
		}
		else if (appender instanceof FileAppender) {
			FileAppender fileAppender = (FileAppender) appender;
			appendValue(sb, "File", fileAppender.getFile());

			if (fileAppender instanceof RollingFileAppender) {
				RollingFileAppender rollAppender = (RollingFileAppender) fileAppender;
				appendValue(sb, "MaxFileSize", rollAppender.getMaximumFileSize());
				appendValue(sb, "MaxBackupIndex", rollAppender.getMaxBackupIndex());
			}
		}

        if (appender.requiresLayout()) {
            Layout layout = appender.getLayout();
            if (layout != null) {
                appendValue(sb, "Layout", layout.getClass().getName());

                if (layout instanceof PatternLayout) {
                    appendValue(sb, "Pattern", ((PatternLayout) layout).getConversionPattern());
                }
                appendValue(sb, "Content", layout.getContentType());
                appendValue(sb, "IgnoresThrowable", layout.ignoresThrowable());
            }
        }

		return sb;
	}

	/**
	 * Returns a list of all loggers in the repository, excluding the root logger.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Logger> getLoggers() {
		Enumeration<Logger> loggers = repo.getCurrentLoggers();

		List<Logger> l = null;

		if ((loggers != null) && loggers.hasMoreElements()) {
			l = Collections.list(loggers);
		}
		else {
			l = Collections.EMPTY_LIST;
		}

		return l;
	}

	/**
	 * Returns a list of all loggers in the repository, including the root logger as the first
	 * element.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Logger> getAllLoggers() {
		List<Logger> loggers = getLoggers();

		List list = new ArrayList(loggers);
		list.add(0, rootLogger);

		return list;
	}

	/**
	 * Returns an array of all logger names that start with the specified prefix, with the first
	 * element being the root logger. The clients are likely MBeans interested in application
	 * related loggers which can be grouped by packaging/naming conventions.
	 * 
	 * @param prefix
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String[] getLoggersNames(String prefix) {
		if (StringUtil.isEmpty(prefix)) {
			return getAllLoggersNames();
		}

		List<String> list = new ArrayList<String>();
		list.add(rootLogger.getName());

		Enumeration<Logger> loggers = repo.getCurrentLoggers();
		if (loggers != null) {
			while (loggers.hasMoreElements()) {
				String name = loggers.nextElement().getName();
				if (name.startsWith(prefix)) {
					list.add(name);
				}
			}
		}

		String[] names = new String[list.size()];
		list.toArray(names);

		return names;
	}

	/**
	 * Returns an array of all logger names defined in the repository at the time of the call.
	 * 
	 * @param prefix
	 * @return
	 */
	public static String[] getAllLoggersNames() {
		List<Logger> loggers = getLoggers();

		String[] names = new String[loggers.size() + 1];

		int i = 0;
		names[i++] = rootLogger.getName();

		for (Logger logger : loggers) {
			names[i++] = logger.getName();
		}

		return names;
	}

	/**
	 * Returns a list of appenders attached to the specified attachable. The list contains only the
	 * first level appenders and does not go recursively into the sub-appenders.
	 * 
	 * @param attachable
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Appender> getAppenders(AppenderAttachable attachable) {
		List<Appender> list = null;

		Enumeration<Appender> appenders = attachable.getAllAppenders();
		if ((appenders != null) && appenders.hasMoreElements()) {
			list = Collections.list(appenders);
		}
		else {
			list = Collections.EMPTY_LIST;
		}

		return list;
	}

	/**
	 * Returns a list of appenders attached to the specified attachable. The list contains all the
	 * appenders including those defined recursively under appenders which are themselves
	 * attachable. The clients are likely MBeans whose operations involve changing appenders
	 * configuration.
	 * 
	 * @param attachable
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Appender> getAllAppenders(AppenderAttachable attachable) {
		List<Appender> appenders = getAppenders(attachable);
		List all = null;

		for (Appender appender : appenders) {
			if (appender instanceof AppenderAttachable) {
				List subAppenders = getAllAppenders((AppenderAttachable) appender);
				if (all == null) {
					all = new ArrayList(appenders);
				}

				all.addAll(subAppenders);
			}
		}

		return (all == null ? appenders : all);
	}

	/**
	 * Returns an array of names of appenders attached to the specified attachable. The list
	 * contains only the first level appenders and does not go recursively into the sub-appenders.
	 * 
	 * @param attachable
	 * @return
	 */
	public static String[] getAppenderNames(AppenderAttachable attachable) {
		List<Appender> appenders = getAppenders(attachable);

		String[] names = new String[appenders.size()];
		int i = 0;
		for (Appender appender : appenders) {
			names[i++] = appender.getName();
		}

		return names;
	}

	/**
	 * Tries to return the attached appender by name. If the specified name is not found, it checks
	 * the first appender to see if it's attachable itself and then it goes recursively until a
	 * sub-appender with that name exists or no more sub-appenders are defined, in which case it
	 * returns null.
	 * 
	 * If no name is specified, it returns the first non-attachable appender found.
	 * 
	 * @param attachable
	 * @param appenderName
	 * @return
	 */
	public static Appender getAppender(AppenderAttachable attachable, String appenderName) {
		Appender appender = null;

		if (StringUtil.isEmpty(appenderName)) {
			appender = getFirstOutputAppender(attachable);
		}
		else {
			appender = attachable.getAppender(appenderName);
			if (appender == null) {
				appender = getFirstAppender(attachable);
				if (appender instanceof AppenderAttachable) {
					appender = getAppender((AppenderAttachable) appender, appenderName);
				}
				else appender = null;
			}
		}

		return appender;
	}

	/**
	 * Returns a map of appender properties. Currently only works with Console, File and attacheable
	 * appenders. Can be improved to use introspection.
	 * 
	 * The clients are likely MBeans wanting to display this information as an attribute.
	 * 
	 * @param appender
	 * @return
	 */
	public static Map<String, String> getAppenderProperties(Appender appender) {
		Map<String, String> props = new TreeMap<String, String>();

		if (appender instanceof ConsoleAppender) {
			ConsoleAppender capp = (ConsoleAppender) appender;
			props.put("Encoding", capp.getEncoding());
			props.put("Follow", Boolean.toString(capp.getFollow()));
			props.put("Immediate Flush", Boolean.toString(capp.getImmediateFlush()));
			props.put("Target", capp.getTarget());
		}
		else if (appender instanceof FileAppender) {
			FileAppender fapp = (FileAppender) appender;
			props.put("Append", Boolean.toString(fapp.getAppend()));
			props.put("Buffered IO", Boolean.toString(fapp.getBufferedIO()));
			props.put("File", fapp.getFile());
			props.put("Buffer Size", Integer.toString(fapp.getBufferSize()));

			if (appender instanceof RollingFileAppender) {
				RollingFileAppender rfapp = (RollingFileAppender) appender;
				props.put("Max Backup Index", Integer.toString(rfapp.getMaxBackupIndex()));
				props.put("Max file Size", Long.toString(rfapp.getMaximumFileSize()));
			}
		}
		else if (appender instanceof AppenderAttachable) {
			String[] s = getAppenderNames((AppenderAttachable) appender);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < s.length; i++) {
				if (sb.length() > 0) {
					sb.append(',');
				}
				sb.append(s[i]);
			}
			props.put("Chained Appenders", sb.toString());

			if (appender instanceof TurboAppender) {
				TurboAppender turboAppender = (TurboAppender) appender;
				props.put("Buffer Size", Integer.toString(turboAppender.getBufferSize()));
				props.put("Location Info", Boolean.toString(turboAppender.getLocationInfo()));
			}
		}

		return props;
	}

	/**
	 * Returns the first attached appender.
	 * 
	 * @param attachable
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Appender getFirstAppender(AppenderAttachable attachable) {
		Enumeration<Appender> appenders = attachable.getAllAppenders();
		return (((appenders == null) || !appenders.hasMoreElements()) ? null : appenders.nextElement());
	}

	/**
	 * @param attachable
	 * @param target
	 * @return
	 */
	public static Appender getFirstAppender(AppenderAttachable attachable, Class<?> target) {
		Appender appender = getFirstAppender(attachable);
		if (appender != null) {
			if (target.isAssignableFrom(appender.getClass())) {
				return appender;
			}
		}

		return null;
	}

	/**
	 * @param attachable
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean hasAppenders(AppenderAttachable attachable) {
		Enumeration<Appender> appenders = attachable.getAllAppenders();
		return ((appenders != null) && appenders.hasMoreElements());
	}

	/**
	 * Returns the first non-attacheable appender. It may return an attacheable appender when maybe
	 * due to misconfiguration, the attacheable appender has no output appenders attached to it.
	 * 
	 * @param attachable
	 * @return
	 */
	public static Appender getFirstOutputAppender(AppenderAttachable attachable) {
		Appender appender = getFirstAppender(attachable);
		if (appender instanceof AppenderAttachable) {
			Appender first = getFirstOutputAppender((AppenderAttachable) appender);
			return (first == null ? appender : first);

		}
		else return appender;
	}

	/**
	 * Removes the first attached appender.
	 * 
	 * @param attachable
	 * @return The appender that was dettached.
	 */
	public static Appender removeFirstAppender(AppenderAttachable attachable) {
		Appender first = getFirstAppender(attachable);
		if (first != null) {
			attachable.removeAppender(first);
		}

		return first;
	}

	/**
	 * Replaces the first attached appender with another appender.
	 * 
	 * @param attachable
	 * @param newAppender
	 * @return The appender that was replaced/dettached.
	 */
	public static Appender replaceFirstAppender(AppenderAttachable attachable, Appender newAppender) {
		Appender oldAppender = removeFirstAppender(attachable);
		attachable.addAppender(newAppender);
		return oldAppender;
	}

	/**
	 * Sets asynchronous logging for the root logger with a default buffer size of
	 * {@link #DEFAULT_ASYNC_BUFFER DEFAULT_ASYNC_BUFFER}.
	 * 
	 * @param logger
	 * @return The AppenderAttachable of the specified logger.
	 */
	public static AppenderAttachable setAsyncLogging(Logger logger) {
		return setAsyncLogging(logger, "TurboAppender", TurboAppender.DEFAULT_BUFFER_SIZE, false);
	}

	/**
	 * Sets asynchronous logging for the specified logger. A new async appender is dynamically
	 * atached to the specified logger, with the specified name, buffer size and location info flag.
	 * All appenders previously attached to the logger, are re-attached to the newly created async
	 * appender. The implementation checks if async logging is already enabled for this logger.
	 * 
	 * @param logger - if null, the root logger is affected
	 * @param name - if null, the {@link #DEFAULT_ASYNC_SUFFIX} is appended to the logger name
	 * @param bufferSize
	 * @param locationInfo
	 * @return
	 */
	public static AppenderAttachable setAsyncLogging(Logger logger, String name, int bufferSize, boolean locationInfo) {
		TurboAppender turboAppender = null;

		if (logger == null) {
			logger = rootLogger;
		}

		List<Appender> appenders = getAppenders(logger);
		if (appenders.size() > 0) {
			// Check if already async
			boolean isAsync = false;

			if ((appenders.size() == 1) && (appenders.get(0) instanceof TurboAppender)) {
				turboAppender = (TurboAppender) appenders.get(0);
				isAsync = true;
			}
			else {
				turboAppender = new TurboAppender();
				logger.addAppender(turboAppender);
			}

			turboAppender.setName(name == null ? logger.getName() + DEFAULT_ASYNC_SUFFIX : name);
			turboAppender.setBufferSize(bufferSize);
			turboAppender.setLocationInfo(locationInfo);
			turboAppender.activateOptions();

			if (!isAsync) {
				for (Appender appender : appenders) {
					turboAppender.addAppender(appender);
					logger.removeAppender(appender);
				}
			}
		}

		return turboAppender;
	}

	/**
	 * Removes asynchronous logging for the specified logger. All appenders attached to the async
	 * one will be re-atached to the logger.
	 * 
	 * @param logger
	 */
	public static void resetAsyncLogging(Logger logger) {
		if (logger == null) {
			logger = rootLogger;
		}

		List<Appender> appenders = getAppenders(logger);

		// Check if already async
		if ((appenders.size() == 1) && (appenders.get(0) instanceof TurboAppender)) {
			TurboAppender turboAppender = (TurboAppender) appenders.get(0);
			logger.removeAppender(turboAppender);

			appenders = getAppenders(turboAppender);

			for (Appender appender : appenders) {
				turboAppender.removeAppender(appender);
				logger.addAppender(appender);
			}

			turboAppender.close();
		}
	}

	/**
	 * Used to return information on chained appenders.
	 * 
	 * @param attachable
	 * @param indent
	 * @return
	 */
	private static StringBuilder getAppendersDetails(AppenderAttachable attachable, String indent) {
		StringBuilder sb = null;

		List<Appender> appenders = getAppenders(attachable);
		if (appenders.size() > 0) {
			sb = new StringBuilder();

			for (Appender appender : appenders) {
				if (sb.length() > 0) {
					sb.append(StringUtil.NEWLINE);
				}
				sb.append(getAppenderDetails(appender, indent));
			}
		}

		return sb;
	}

	/**
	 * Used to return tokenized (key=value) printable information.
	 * 
	 * @param sb
	 * @param key
	 * @param value
	 * @return
	 */
	static StringBuilder appendValue(StringBuilder sb, String key, Object value) {
		return StringUtil.appendValue(sb, key, value);
	}

	static StringBuilder appendLine(StringBuilder sb, Object value) {
		return StringUtil.appendLine(sb, value);
	}

	public static String getRootLevel() {
		return rootLogger.getLevel().toString();
	}

	public static TurboAppender getTurboAppender() {
		return (TurboAppender) getFirstAppender(rootLogger, TurboAppender.class);
	}

	public static TurboLoggingServer getTurboServer() {
		TurboAppender turboAppender = getTurboAppender();
		return (turboAppender == null ? null : turboAppender.getServer());
	}

	public static int getTurboQueueSize() {
		TurboLoggingServer server = getTurboServer();
		return (server == null ? 0 : server.getQueueSize());
	}

	public static long getTurboTotalMessages() {
		TurboLoggingServer server = getTurboServer();
		return (server == null ? 0 : server.getCompletedTaskCount());
	}

}