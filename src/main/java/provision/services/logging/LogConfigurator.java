/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/logging/LogConfigurator.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.logging;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.apache.log4j.helpers.FileWatchdog;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.xml.Log4jEntityResolver;
import org.apache.log4j.xml.SAXErrorHandler;

import provision.util.JMXUtil;
import provision.util.ResourceUtil;
import provision.util.StringUtil;

/**
 * This class encapsulates the logging configuration details. It provides the mechanism to react to
 * configuration changes triggered by the monitor thread that watches for changes in the
 * configuration file.
 * <p>
 * It supports configuration files in XML as well as properties format.
 * <p>
 * The "Off" switch flag is only detected when the configuration file is in properties format. If
 * the flag is set to <code<true</code>, an async appender is injected at the root level. Code that
 * belongs to an "appender" must not be misplaced within the application code and executed prior to
 * calling the logging framework.
 * 
 * This implementation ensures a small Perforce footprint by bundling multiple package private
 * classes related to configuration.
 * 
 * @author Iulian Vlasov
 * @since TurboPrv
 */
public class LogConfigurator {

	/**
	 * System property that enables internal Log4J debug
	 */
	private final static String LOG_DEBUG_FLAG = "log4j.debug";

	/**
	 * System property for specifying a different log configuration file.
	 */
	private final static String LOG_CONFIG_FILE_PROP = "prv.log.configFile";

	/**
	 * This is the default logging configuration file.
	 */
	private final static String DEFAULT_LOG_CONFIG_FILE = "prvlog4j.xml";

	/**
	 * System property for specifying a different watch delay for the log configuration file.
	 */
	private final static String LOG_WATCHDOG_DELAY_PROP = "prv.log.watchDelay";

	/**
	 * Default delay in milliseconds for monitoring changes in the log configuration file.
	 */
	private final static long DEFAULT_WATCHDOG_DELAY = 60000;

	private static ObjectName[] managedBeans;

	private static LogFileWatchDog watcher;

	/**
	 * Performs Log4J configuration using system properties for customizing the config file and
	 * watch delay.
	 */
	static void configure() {
		boolean oldDebugFlag = setBooleanProperty(LOG_DEBUG_FLAG, true);
		boolean oldResourceFlag = setBooleanProperty(ResourceUtil.APP_VERBOSE_LOGGING, true);

		String configFile = getStringProperty(LOG_CONFIG_FILE_PROP, DEFAULT_LOG_CONFIG_FILE);
		long watchDelay = getLongProperty(LOG_WATCHDOG_DELAY_PROP, DEFAULT_WATCHDOG_DELAY);

		configure(configFile, watchDelay);

		setBooleanProperty(LOG_DEBUG_FLAG, oldDebugFlag);
		setBooleanProperty(ResourceUtil.APP_VERBOSE_LOGGING, oldResourceFlag);
		
		if (!oldDebugFlag) {
			LogLog.setInternalDebugging(false);
		}
	}

	/**
	 * Performs custom Log4J initialization. The custom configuration file is monitored for changes.
	 * Logging configuration file can be available in the classloader context or it can be located
	 * in the current directory from the JVM's standpoint.
	 * 
	 * @param args - Acceptable arguments are:<br>
	 *        - String name of the resource file; it can be in XML or properties format<br>
	 *        - long delay value for the watchdog thread that monitors the file for changes.
	 * @return
	 */
	static void configure(Object... args) {
		if (TurboRepositorySelector.addRepository(true)) {
			String resource = null;
			Long watchDelay = null;

			for (int i = 0; i < args.length; i++) {
				if (args[i] == null) continue;

				if (String.class.isAssignableFrom(args[i].getClass())) {
					resource = (String) args[i];
				}
				else if (Long.class.isAssignableFrom(args[i].getClass())) {
					watchDelay = (Long) args[i];
				}
			}

			URL url = getConfigurationResource(resource);
			if ((url == null) && (resource != null)) {
				// our custom config file is missing, try the standard ones
				url = getConfigurationResource();
			}

			if (url != null) {
				configure(url, watchDelay);
			}
			else {
				new LogConfigListener(resource).postConfig(null);
			}

			managedBeans = registerMBeans();
		}
	}

	/**
	 * Configures the logging framework using the provided URL of the config file. The file is
	 * monitored for changes according with the specified delay in milliseconds. A default listener
	 * will be notified when the (re)configuration has finished in order to do any
	 * post-configuration activities.
	 * 
	 * @param url
	 * @param watchDelay
	 */
	static void configure(URL url, Long watchDelay) {
		String filename = url.getFile();
		File f = new File(filename);
		ConfigListener listener = new LogConfigListener(f, watchDelay);
		configure(filename, watchDelay, listener, isXML(f));
	}

	/**
	 * Configures the logging framework using the provided config filename. The file is monitored
	 * for changes according with the specified delay in milliseconds. The provided listener will be
	 * notified when the (re)configuration has finished in order to do any post-configuration
	 * activities.
	 * 
	 * @param filename
	 * @param watchDelay
	 * @param listener
	 * @param isXml
	 */
	static void configure(String filename, Long watchDelay, ConfigListener listener, boolean isXml) {
		if (watchDelay == null) {
			if (isXml) {
				new XMLConfigurator(listener).doConfigure(filename, LogManager.getLoggerRepository());
			}
			else {
				new PropertyConfigurator(listener).doConfigure(filename, LogManager.getLoggerRepository());
			}
		}
		else {
			if (watcher != null) {
				watcher.cancel();
			}
			watcher = new LogFileWatchDog(filename, watchDelay, listener, isXml);
			watcher.start();
		}
	}

	/**
	 * Returns an array of ObjectName for each registered MBean.
	 * 
	 * @return
	 */
	private static ObjectName[] registerMBeans() {
		if (getStringProperty("weblogic.Name", null) == null) {
			return new ObjectName[] {};
		}

		MBeanServer server = null;
		try {
			server = JMXUtil.getServer();
		}
		catch (Throwable t) {
			// don't let anything spill, since this is an optional feature
			Logger.warnx(t, "Failed to retrieve an instance of the mbean server");
			return new ObjectName[] {};
		}

		String agentName = "TurboPrv";
		String mbeanType = "TurboLogging";
		String mbeanName = getContextName();
		Object mbeanObject = new TurboMBean();

		Map<String, String> attrs = new PSTokenDictionary<String, String>();
		attrs.put("type", mbeanType);
		attrs.put("name", mbeanName);

		ObjectName beanName = null;
		try {
			beanName = JMXUtil.registerMBean(server, agentName, attrs, mbeanObject);
			Logger.infox("Successfully registered MBean: " + beanName);
		}
		catch (Throwable t) {
			if (t instanceof InstanceAlreadyExistsException) {
				Logger.warnx(t, "MBean already registered with the server: " + attrs);
			}
			else {
				Logger.warnx(t, "Failed to register MBean: " + attrs);
				return new ObjectName[] {};
			}
		}

		return new ObjectName[] { beanName };
	}

	/**
	 * Return the URL of the default log configuration file.
	 * 
	 * @return
	 */
	public static URL getConfigurationResource() {
		return getConfigurationResource(null);
	}

	/**
	 * Return the URL of the specified resource or the default log configuration file if the
	 * resource is null.
	 * 
	 * @param resource
	 * @return
	 */
	public static URL getConfigurationResource(String resource) {
		URL url = null;

		if (resource != null) {
			url = ResourceUtil.getResourceAsURL(resource);
		}
		else {
			url = ResourceUtil.getResourceAsURL("log4j.xml");
			if (url == null) {
				url = ResourceUtil.getResourceAsURL("log4j.properties");
			}
		}

		return url;
	}

	/**
	 * Use this method to decide programatically whether Log4J was initialized properly or not.
	 * Returns true if there are any appenders defined at the root logger. If the return value is
	 * false, Log4J was not initialized and no logging will occur.
	 * 
	 * @return
	 */
	public static boolean isRepositoryConfigured() {
		return LogManager.getRootLogger().getAllAppenders().hasMoreElements();
	}

	public static org.apache.log4j.Logger getOffSwitchLogger() {
		return LogConfigListener.OFF_SWITCH_LOGGER;
	}

	public static ObjectName[] getManagedBeans() {
		return managedBeans;
	}

	public static boolean useVolatileEvents() {
		return LogConfigListener.USE_VOLATILE_EVENTS;
	}

	/**
	 * Our default config file extension - "lcf" - doesn't tell much about what type of content it
	 * has. We need to support XML format as well as properties.
	 * 
	 * @param f
	 * @return
	 */
	private static boolean isXML(File f) {
		String name = f.getName();
		boolean isXML = name.endsWith(".xml");
		if (!isXML && !name.endsWith(".properties")) {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(true);
			try {
				DocumentBuilder docBuilder = dbf.newDocumentBuilder();
				docBuilder.setErrorHandler(new SAXErrorHandler());
				docBuilder.setEntityResolver(new Log4jEntityResolver());
				docBuilder.parse(f);
				isXML = true;
			}
			catch (Throwable ignored) {
				// most likely invalid XML file
			}
		}

		return isXML;
	}

	private static boolean setBooleanProperty(String key, boolean newValue) {
		boolean oldValue = Boolean.getBoolean(key);
		if (oldValue ^ newValue) {
			try {
				System.setProperty(key, String.valueOf(newValue));
			}
			catch (SecurityException nobigdeal) {
			}
		}

		return oldValue;
	}

	static String getStringProperty(String key, String defValue) {
		String value = null;
		try {
			value = System.getProperty(key, defValue);
		}
		catch (SecurityException sowhat) {
			value = defValue;
		}

		return value;
	}

	static long getLongProperty(String key, long defValue) {
		String sValue = getStringProperty(key, null);
		long value = defValue;

		if (sValue != null) {
			try {
				value = Long.parseLong(sValue);
			}
			catch (NumberFormatException ignored) {
			}
		}

		return value;
	}

	public static String getContextName() {
		return getContextName(LogConfigurator.class);
	}

	public static String getContextName(Object o) {
		if (o == null) {
			o = LogConfigurator.class;
		}

		Class<?> c = (o instanceof Class ? (Class<?>) o : o.getClass());
		ClassLoader cl = c.getClassLoader();
		String clname = cl.toString();
		String token = "annotation: ";
		int ix = clname.lastIndexOf(token);
		if (ix != -1) {
			clname = clname.substring(ix + token.length());
			if (clname.endsWith("@")) {
				clname = clname.substring(0, clname.length() - 1);
			}
		}

		return clname;
	}

	public static void shutdown() {
		if (TurboRepositorySelector.removeRepository()) {
			if (watcher != null) {
				watcher.cancel();
				watcher = null;
			}
		}
	}
}

/**
 * Common watcher for XML and properties log config files.
 * 
 * @author Iulian Vlasov
 */
class LogFileWatchDog extends FileWatchdog {

	private ConfigListener listener;
	private boolean isXml;
	private boolean hasListener;
	private volatile boolean cancelled;

	LogFileWatchDog(String filename, long watchDelay, ConfigListener listener, boolean isXml) {
		super(filename);
		this.setName("TURBO-LogFileWatchDog");
		this.setDelay(watchDelay);
		this.listener = listener;
		this.isXml = isXml;
		this.hasListener = true;
		doOnChange();
	}

	public void cancel() {
		this.cancelled = true;
	}

	/**
	 * Call the appropriate configurator to reconfigure Log4J.
	 */
	public void doOnChange() {
		if (hasListener) {
			LogLog.setInternalDebugging(true);
			if (isXml) {
				new XMLConfigurator(listener).doConfigure(filename, LogManager.getLoggerRepository());
			}
			else {
				new PropertyConfigurator(listener).doConfigure(filename, LogManager.getLoggerRepository());
			}
			LogLog.setInternalDebugging(false);
		}
	}

	public void run() {
		while (!cancelled) {
			try {
				Thread.sleep(delay);
			}
			catch (InterruptedException ignored) {
			}

			checkAndConfigure();
		}
	}
}

/**
 * This configurator is used when the config file is in XML format.
 * 
 * @author Iulian Vlasov
 */
class XMLConfigurator extends DOMConfigurator {

	private ConfigListener listener;

	public XMLConfigurator(ConfigListener listener) {
		this.listener = listener;
	}

	@Override
	public void doConfigure(final String filename, LoggerRepository repository) {
		super.doConfigure(filename, repository);

		if (listener != null) {
			listener.postConfig(null);
		}
	}
}

/**
 * This configurator is used when the config file is in 'properties' format.
 * 
 * @author Iulian Vlasov
 */
class PropertyConfigurator extends org.apache.log4j.PropertyConfigurator {

	private ConfigListener listener;

	public PropertyConfigurator(ConfigListener listener) {
		this.listener = listener;
	}

	@Override
	public void doConfigure(Properties properties, LoggerRepository hierarchy) {
		super.doConfigure(properties, hierarchy);

		if (listener != null) {
			listener.postConfig(properties);
		}
	}
}

/**
 * This implementation of the ConfigListener dynamically enables async logging at the root logger if
 * it wasn't set already either programatically or in the config file.
 * 
 * @author Iulian Vlasov
 */
class LogConfigListener implements ConfigListener {

	static org.apache.log4j.Logger OFF_SWITCH_LOGGER = null;
	static boolean USE_VOLATILE_EVENTS = true;

	private static final String TURBO_FLAG = "Turbo";
	private static final String OFF_SWITCH = "OffSwitch";
	private static final String NO_VOLATILE_EVENTS = "NoVolatileEvents";

	private File cfgFile;
	private Long watchDelay;
	private boolean wasInitialized;
	private String resource;

	LogConfigListener(File f, Long delay) {
		this.cfgFile = f;
		this.watchDelay = delay;
		this.wasInitialized = LogConfigurator.isRepositoryConfigured();
	}

	/**
	 * This constructor is used only when the resource could not be resolved to a URL.
	 * 
	 * @param resource
	 */
	LogConfigListener(String resource) {
		this.resource = resource;
		this.wasInitialized = LogConfigurator.isRepositoryConfigured();
	}

	/**
	 * If the active log configuration file is a properties file, this implementation looks for a
	 * property named {@link #TURBO_FLAG}. If the case-insensitive value is either <code>true</code>
	 * , <code>on</code> or <code>yes</code>, the root logger will log asynchronously.
	 * 
	 * @param p
	 * @see provision.common.logging.ConfigListener#postConfig(java.util.Properties)
	 */
	public void postConfig(Properties p) {
		org.apache.log4j.Logger offLogger = LogManager.exists(OFF_SWITCH);
		if ((offLogger != null) && !LoggingHelper.hasAppenders(offLogger)) {
			offLogger = null;
		}

		OFF_SWITCH_LOGGER = offLogger;

		org.apache.log4j.Logger volatileLogger = LogManager.exists(NO_VOLATILE_EVENTS);
		USE_VOLATILE_EVENTS = ((volatileLogger == null) || !LoggingHelper.hasAppenders(volatileLogger));

		Appender rootAppender = LoggingHelper.getFirstAppender(LoggingHelper.rootLogger);
		boolean isTurbo = false;

		if (p == null) {
			// xml config - nothing to do but retrieve the Turbo flag
			isTurbo = (rootAppender instanceof TurboAppender);
		}
		else {
			// properties config - set the Turbo mode
			String turboMode = p.getProperty(TURBO_FLAG, "true");
			isTurbo = Boolean.valueOf(turboMode);
			if (!isTurbo && ("yes".equalsIgnoreCase(turboMode) || "on".equalsIgnoreCase(turboMode))) {
				isTurbo = true;
			}

			if (isTurbo) {
				LoggingHelper.setAsyncLogging(LoggingHelper.rootLogger);
			}
		}

		StringBuilder sb = new StringBuilder();
		StringUtil.appendLine(sb, StringUtil.underline("Log4J Configuration Details:", '-'));

		if (cfgFile == null) {
			sb.append("- Could not configure hierarchy using " + (resource == null ? "default Log4J files" : resource));

			if (wasInitialized) {
				URL stdurl = LogConfigurator.getConfigurationResource();
				StringUtil.appendLine(sb, "- Log4J has been previously initialized");
				if (stdurl != null) {
					sb.append(" from: " + stdurl);
				}
			}
		}
		else {
			sb.append("- Hierarchy has been configured from file: " + cfgFile);

			String fileTime = ResourceUtil.getFormattedFileTime(cfgFile, "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			StringUtil.appendLine(sb, "- Configuration file has been last modified at: " + fileTime);
			StringUtil.appendLine(sb, "- Configuration changes are monitored every " + watchDelay / 1000 + " seconds");
		}

		StringUtil.appendLine(sb, "- Turbo Mode: " + isTurbo);
		StringUtil.appendLine(sb, LoggingHelper.getLoggersDetails(new String[] { OFF_SWITCH }));

		if (isTurbo) {
			StringUtil.appendLine(sb, LoggingHelper.getTurboServer());
		}

		if ((wasInitialized || (rootAppender != null)) && LoggingHelper.rootLogger.isInfoEnabled()) {
			LoggingHelper.rootLogger.info(sb);
		}

		System.out.println(sb);
	}
}