/*
 * (C) 2008 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/logging/TurboMBean.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.logging;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenMBeanAttributeInfo;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import javax.management.openmbean.OpenMBeanConstructorInfo;
import javax.management.openmbean.OpenMBeanConstructorInfoSupport;
import javax.management.openmbean.OpenMBeanInfoSupport;
import javax.management.openmbean.OpenMBeanOperationInfo;
import javax.management.openmbean.OpenMBeanOperationInfoSupport;
import javax.management.openmbean.OpenMBeanParameterInfo;
import javax.management.openmbean.OpenMBeanParameterInfoSupport;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import provision.util.StringUtil;


/**
 * This is an Open MBean that exposes attributes and operations for managing the behavior of the
 * logging subsystem.
 * <p>
 * This MBean exposes the following attributes:
 * <ul>
 * <li>Loggers - complex tabular read-only representation of the logging hierarchy</li>
 * <li>Root Logger Level - simple R/W attribute for setting the severity threshold of the root logger</li>
 * <li>Turbo Details - internal details of the turbo appender</li>
 * <li>Turbo Total Messages - runtime counter of the messages being logged</li>
 * </ul>
 * This MBean exposes the following operations:
 * <ul>
 * <li>Set Severity Threshold - applicable to any logger, not just root</li>
 * <li>Set Output File - changes the destination of the file appender</li>
 * <li>Set Conversion Pattern - changes the conversion pattern of the layout</li>
 * </ul>
 * 
 * @author Iulian Vlasov
 * @since TurboPrv
 */
public class TurboMBean implements DynamicMBean {

	private static final String LOGGERS_JMXATTR = "Loggers";
	private static final String ROOT_LEVEL_JMXATTR = "Root Logger Level";
	private static final String TURBO_TOTAL_JMXATTR = "Turbo Total Messages";
	private static final String TURBO_QUEUE_JMXATTR = "Turbo Queue Size";
	private static final String LEVEL_JMXOP = "Set Severity Threshold";
	private static final String OUTPUT_FILE_JMXOP = "Set Output File";
	private static final String CONV_PATTERN_JMXOP = "Set Conversion Pattern";
	private static final String APPENDERS_JMXITEM = "Appenders";

	/* Open type index */
	private static String[] indexNames = { "Name" };

	/* Property item and list open type */
	private static String[] propItemNames = { "Name", "Value" };
	private static OpenType[] propItemTypes = { SimpleType.STRING, SimpleType.STRING };
	private static CompositeType propType;
	private static TabularType propListType;

	/* Appender item and list open type */
	private static String[] appItemNames = { "Name", "Class", "Layout", "Pattern", "Properties" };
	private static OpenType[] appItemTypes = { SimpleType.STRING, SimpleType.STRING, SimpleType.STRING,
			SimpleType.STRING, propListType };
	private static CompositeType appType;
	private static TabularType appListType;

	/* Logger item and list open type */
	private static String[] logItemNames = { "Name", "Class", "Level", APPENDERS_JMXITEM };
	private static OpenType[] logItemTypes = { SimpleType.STRING, SimpleType.STRING, SimpleType.STRING, appListType };
	private static CompositeType logType;
	private static TabularType logListType;

	private Map<String, Method> opMap;
	private Map<String, Method> attrMap;

	// Open type initialization
	static {
		try {
			propType = new CompositeType("property", "Property Info", propItemNames, propItemNames, propItemTypes);
			propListType = new TabularType("properties", "Property List", propType, indexNames);

			appItemTypes[4] = propListType;
			appType = new CompositeType("appender", "Appender Info", appItemNames, appItemNames, appItemTypes);
			appListType = new TabularType("appenders", "Appender List", appType, indexNames);

			logItemTypes[3] = appListType;
			logType = new CompositeType("logger", "Logger Info", logItemNames, logItemNames, logItemTypes);
			logListType = new TabularType("All Loggers", "Logger List", logType, indexNames);
		}
		catch (OpenDataException e) {
			// should not happen
			throw new RuntimeException(StringUtil.getStackTrace(e));
		}
	}

	private TabularData loggers;
	private MBeanInfo ombInfo;

	// private String rootLevel;
	// private String turboDetails;

	/**
	 * Constructs this MBean and populates its attributes with Log4J config data.
	 */
	public TurboMBean() {
		this.opMap = new HashMap<String, Method>(3);
		bindMethod(LEVEL_JMXOP, "setLevel", opMap);
		bindMethod(OUTPUT_FILE_JMXOP, "setOutputFile", opMap);
		bindMethod(CONV_PATTERN_JMXOP, "setConversionPattern", opMap);

		this.attrMap = new HashMap<String, Method>(3);
		bindMethod(LOGGERS_JMXATTR, "getLoggers", attrMap);
		bindMethod(ROOT_LEVEL_JMXATTR, "getRootLevel", attrMap);
		bindMethod(TURBO_TOTAL_JMXATTR, "getTurboTotalMessages", attrMap);
		bindMethod(TURBO_QUEUE_JMXATTR, "getTurboQueueSize", attrMap);

		buildMBeanInfo();

		// build the tabular logger data
		this.loggers = new TabularDataSupport(logListType);
		List<Logger> list = LoggingHelper.getAllLoggers();
		for (Logger logger : list) {
			CompositeData loggerData;
			try {
				loggerData = getLoggerCompositeData(logger);
				loggers.put(loggerData);
			}
			catch (OpenDataException e) {
				e.printStackTrace();
			}
		}

		// this.rootLevel = LoggingHelper.getRootLevel();
	}

	private void bindMethod(String jmxName, String javaName, Map<String, Method> map) {
		Method[] methods = getClass().getMethods();
		for (Method method : methods) {
			if (method.getName().equals(javaName)) {
				map.put(jmxName, method);
				return;
			}
		}
	}

	private void buildMBeanInfo() {
		this.ombInfo = new OpenMBeanInfoSupport(getClass().getName(), "Turbo MBean", getAttributesInfo(),
				getConstructorsInfo(), getOperationsInfo(), getNotificationsInfo());
	}

	private OpenMBeanAttributeInfo[] getAttributesInfo() {
		OpenMBeanAttributeInfo[] attrs = new OpenMBeanAttributeInfo[attrMap.size()];
		if (attrs.length > 0) {
			int index = 0;

			if (attrMap.containsKey(LOGGERS_JMXATTR)) {
				attrs[index++] = new OpenMBeanAttributeInfoSupport(LOGGERS_JMXATTR, "List of available Loggers",
						logListType, true, false, false);
			}

			if (attrMap.containsKey(ROOT_LEVEL_JMXATTR)) {
				attrs[index++] = new OpenMBeanAttributeInfoSupport(ROOT_LEVEL_JMXATTR,
						"Threshold Level for the Root Logger", SimpleType.STRING, true, true, false);
			}

			if (attrMap.containsKey(TURBO_TOTAL_JMXATTR)) {
				attrs[index++] = new OpenMBeanAttributeInfoSupport(TURBO_TOTAL_JMXATTR,
						"Total messages processed by the turbo-logging framework", SimpleType.LONG, true, false, false);
			}

			if (attrMap.containsKey(TURBO_QUEUE_JMXATTR)) {
				attrs[index++] = new OpenMBeanAttributeInfoSupport(TURBO_QUEUE_JMXATTR,
						"Current number of pending log events to be processed by the turbo-logging framework", SimpleType.INTEGER, true, false, false);
			}
		}

		return attrs;
	}

	private OpenMBeanConstructorInfo[] getConstructorsInfo() {
		return new OpenMBeanConstructorInfo[] { new OpenMBeanConstructorInfoSupport("LogOpenMBean",
				"Constructs a LogOpenMBean instance containing all loggers.", new OpenMBeanParameterInfo[0]) };
	}

	private OpenMBeanOperationInfo[] getOperationsInfo() {
		OpenMBeanOperationInfo[] ops = new OpenMBeanOperationInfo[opMap.size()];
		if (ops.length > 0) {
			int index = 0;

			OpenMBeanParameterInfo loggerParm = new OpenMBeanParameterInfoSupport("Logger Name",
					"Must be empty for the root logger", SimpleType.STRING);
			OpenMBeanParameterInfo appenderParm = new OpenMBeanParameterInfoSupport("Appender Name",
					"Name of appender chained to the specified logger", SimpleType.STRING);

			if (opMap.containsKey(LEVEL_JMXOP)) {
				OpenMBeanParameterInfo levelParm = new OpenMBeanParameterInfoSupport("Sevetity Level",
						"Valid values (case insensitive): FATAL, ERROR, WARN, INFO, DEBUG, TRACE", SimpleType.STRING);
				ops[index++] = new OpenMBeanOperationInfoSupport(LEVEL_JMXOP, "set level",
						new OpenMBeanParameterInfo[] { loggerParm, levelParm }, SimpleType.STRING,
						MBeanOperationInfo.ACTION);
			}

			if (opMap.containsKey(OUTPUT_FILE_JMXOP)) {
				OpenMBeanParameterInfo fileParm = new OpenMBeanParameterInfoSupport("File Name",
						"Output filename for the specified file appender", SimpleType.STRING);
				ops[index++] = new OpenMBeanOperationInfoSupport(OUTPUT_FILE_JMXOP, "set output file",
						new OpenMBeanParameterInfo[] { loggerParm, appenderParm, fileParm }, SimpleType.STRING,
						MBeanOperationInfo.ACTION);
			}

			if (opMap.containsKey(CONV_PATTERN_JMXOP)) {
				OpenMBeanParameterInfo patternParm = new OpenMBeanParameterInfoSupport("Conversion Pattern",
						"Conversion pattern for the pattern layout of the specfied appender", SimpleType.STRING);
				ops[index++] = new OpenMBeanOperationInfoSupport(CONV_PATTERN_JMXOP, "set the conversion pattern",
						new OpenMBeanParameterInfo[] { loggerParm, appenderParm, patternParm }, SimpleType.STRING,
						MBeanOperationInfo.ACTION);
			}
		}

		return ops;
	}

	private MBeanNotificationInfo[] getNotificationsInfo() {
		return new MBeanNotificationInfo[0];
	}

	/**
	 * Build the composite data for the specified logger.
	 * 
	 * @param logger
	 * @return
	 * @throws OpenDataException
	 */
	private CompositeData getLoggerCompositeData(Logger logger) throws OpenDataException {
		String name = logger.getName();
		String clasa = logger.getClass().getName();
		String level = logger.getEffectiveLevel().toString();

		TabularDataSupport appenders = new TabularDataSupport(appListType);

		List<Appender> list = LoggingHelper.getAllAppenders(logger);
		for (Appender appender : list) {
			CompositeData appenderData;
			try {
				appenderData = getAppenderCompositeData(appender);
				appenders.put(appenderData);
			}
			catch (OpenDataException e) {
				e.printStackTrace();
			}
		}

		Object[] itemValues = { name, clasa, level, appenders };
		CompositeData loggerData = new CompositeDataSupport(logType, logItemNames, itemValues);

		return loggerData;
	}

	/**
	 * Build the composite data for the specified appender.
	 * 
	 * @param appender
	 * @return
	 * @throws OpenDataException
	 */
	private CompositeData getAppenderCompositeData(Appender appender) throws OpenDataException {
		String name = appender.getName();
		String clasa = appender.getClass().getName();
		String layoutClass = null;
		String pattern = null;

		if (appender.requiresLayout()) {
			Layout layout = appender.getLayout();
			if (layout != null) {
				layoutClass = layout.getClass().getName();

				if (layout instanceof PatternLayout) {
					pattern = ((PatternLayout) layout).getConversionPattern();
				}
			}
		}

		TabularDataSupport propsData = new TabularDataSupport(propListType);

		Map<String, String> props = LoggingHelper.getAppenderProperties(appender);
		for (Iterator<String> it = props.keySet().iterator(); it.hasNext();) {
			String key = it.next();
			Object[] itemValues = { key, props.get(key) };
			CompositeData propData = new CompositeDataSupport(propType, propItemNames, itemValues);
			propsData.put(propData);
		}

		Object[] itemValues = { name, clasa, layoutClass, pattern, propsData };
		CompositeData appData = new CompositeDataSupport(appType, appItemNames, itemValues);

		return appData;
	}

	/* --------------------------- */
	/* DynamicMBean Implementation */
	/* --------------------------- */
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.management.DynamicMBean#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String attr) throws AttributeNotFoundException, ReflectionException {
		Method m = attrMap.get(attr);
		if (m == null) {
			throw new AttributeNotFoundException("Invalid attribute: " + attr);
		}

		try {
			return m.invoke(this, (Object[]) null);
		}
		catch (Exception e) {
			throw new ReflectionException(e, MessageFormat.format(
					"Unable to retrieve attribute [{0}]. Caught exception {1} - Message: {2}", attr, e.getClass()
							.getName(), e.getMessage()));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.management.DynamicMBean#getAttributes(java.lang.String[])
	 */
	public AttributeList getAttributes(String[] attrNames) {
		if (attrNames == null) {
			throw new RuntimeOperationsException(new IllegalArgumentException(
					"The argument attributeNames array must not be null"), "Unable to call getAttributes");
		}

		AttributeList resultList = new AttributeList();
		for (String attrName : attrNames) {
			try {
				resultList.add(new Attribute(attrName, getAttribute(attrName)));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		return (resultList);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.management.DynamicMBean#getMBeanInfo()
	 */
	public MBeanInfo getMBeanInfo() {
		return ombInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.management.DynamicMBean#invoke(java.lang.String, java.lang.Object[],
	 * java.lang.String[])
	 */
	public Object invoke(String opName, Object[] parms, String[] signature) throws MBeanException, ReflectionException {
		Method m = opMap.get(opName);
		if (m == null) {
			throw new ReflectionException(new NoSuchMethodException(opName), "Invalid operation: " + opName);
		}

		Class<?>[] methodParms = m.getParameterTypes();
		if (methodParms.length != parms.length) {
			throw new RuntimeOperationsException(new IllegalArgumentException(MessageFormat.format(
					"Unable to invoke operation [{0}]. Invalid number of arguments - expected [{1}] but got [{2}]!",
					opName, methodParms.length, parms.length)));
		}

		for (int i = 0; i < methodParms.length; i++) {
			if (parms[i] != null) {
				if (!methodParms[i].isAssignableFrom(parms[i].getClass())) {
					throw new RuntimeOperationsException(
							new IllegalArgumentException(
									MessageFormat
											.format(
													"Unable to invoke operation [{0}]. Invalid type for argument [{1}] - expected [{2}] but got [{3}]!",
													opName, i + 1, methodParms[i].getName(), parms[i].getClass()
															.getName())));
				}
			}
		}

		try {
			return m.invoke(this, parms);
		}
		catch (Exception e) {
			throw new MBeanException(e, MessageFormat.format(
					"Unable to invoke operation [{0}]. Caught exception {1} - Message: {2}", opName, e.getClass()
							.getName(), e.getMessage()));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.management.DynamicMBean#setAttribute(javax.management.Attribute)
	 */
	public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException,
			MBeanException, ReflectionException {

		if (ROOT_LEVEL_JMXATTR.equals(attribute.getName())) {
			String slevel = (String) attribute.getValue();
			try {
				setLevel(null, slevel);
				// this.rootLevel = LoggingHelper.rootLogger.getLevel().toString();
			}
			catch (OpenDataException e) {
				throw new MBeanException(e, MessageFormat.format(
						"Caught exception {0} while setting attribute {2} with value {3}. Message: {1}", e.getClass()
								.getName(), e.getMessage(), attribute.getName(), attribute.getValue()));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.management.DynamicMBean#setAttributes(javax.management.AttributeList )
	 */
	
	public AttributeList setAttributes(AttributeList attributes) {
		for (int i = 0; i < attributes.size(); i++) {
			Attribute attr = (Attribute)attributes.get(i);
			try {
				setAttribute(attr);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		return attributes;
	}

	/* ----------- */
	/* JMX getters */
	/* ----------- */
	/**
	 * JMX attribute getter.
	 * 
	 * @return
	 */
	public TabularData getLoggers() {
		return (TabularData) ((TabularDataSupport) loggers).clone();
	}

	/**
	 * @return the level
	 */
	public String getRootLevel() {
		return LoggingHelper.getRootLevel();
	}

	/**
	 * @return
	 */
	public int getTurboQueueSize() {
		return LoggingHelper.getTurboQueueSize();
	}

	/**
	 * @return
	 */
	public long getTurboTotalMessages() {
		return LoggingHelper.getTurboTotalMessages();
	}
	
	/**
	 * @param loggerName
	 * @param levelName
	 * @return
	 * @throws OpenDataException
	 */
	public Object setLevel(String loggerName, String levelName) throws OpenDataException {
		Logger logger = (StringUtil.isEmpty(loggerName) ? LogManager.getRootLogger() : LogManager.getLogger(loggerName));

		Level level = Level.toLevel(levelName, logger.getLevel());
		if (level != null) {
			logger.setLevel(level);
			refresh(logger, null);
		}

		StringBuilder sb = new StringBuilder("Changed: [Logger]=").append(logger.getName()).append(" [Level]=").append(
				level);
		return sb.toString();
	}

	/**
	 * @param loggerName
	 * @param appenderName
	 * @param fileName
	 * @return
	 * @throws OpenDataException
	 */
	public Object setOutputFile(String loggerName, String appenderName, String fileName) throws OpenDataException {
		Logger logger = (StringUtil.isEmpty(loggerName) ? LogManager.getRootLogger() : LogManager.getLogger(loggerName));
		Appender appender = LoggingHelper.getAppender(logger, appenderName);
		if (!(appender instanceof FileAppender)) {
			throw new IllegalArgumentException(MessageFormat.format("No FileAppender named: {0} for logger: {1}",
					appenderName, logger.getName()));
		}

		FileAppender fileAppender = (FileAppender) appender;
		fileAppender.setFile(fileName);
		fileAppender.activateOptions();

		refresh(logger, appender);

		StringBuilder sb = new StringBuilder("Changed: [Logger]=").append(logger.getName()).append(" [Appender]=")
				.append(appender.getName()).append(" [Output File]=").append(fileName);
		return sb.toString();
	}

	/* ----------- */
	/* JMX setters */
	/* ----------- */
	/**
	 * @param loggerName
	 * @param appenderName
	 * @param pattern
	 * @return
	 * @throws OpenDataException
	 */
	public Object setConversionPattern(String loggerName, String appenderName, String pattern) throws OpenDataException {
		Logger logger = (StringUtil.isEmpty(loggerName) ? LogManager.getRootLogger() : LogManager.getLogger(loggerName));
		Appender appender = LoggingHelper.getAppender(logger, appenderName);
		if (appender == null) {
			throw new IllegalArgumentException(MessageFormat.format("No Appender named: {0} for logger: {1}",
					appenderName, logger.getName()));
		}

		Layout layout = appender.getLayout();
		if (!(layout instanceof PatternLayout)) {
			throw new IllegalArgumentException(MessageFormat.format(
					"No PatternLayout defined for appender {0} of logger: {1}", appenderName, logger.getName()));
		}

		((PatternLayout) layout).setConversionPattern(pattern);
		refresh(logger, appender);

		StringBuilder sb = new StringBuilder("Changed: [Logger]=").append(logger.getName()).append(" [Appender]=")
				.append(appender.getName()).append(" [Conversion Pattern]=").append(pattern);
		return sb.toString();
	}

	/**
	 * @param logger
	 * @param appender
	 * @throws OpenDataException
	 */
	private void refresh(Logger logger, Appender appender) throws OpenDataException {
		String[] logIndex = new String[] { logger.getName() };
		CompositeData loggerData = (CompositeData) loggers.get(logIndex);
		if ((loggerData != null) && (appender != null)) {
			TabularData appenders = (TabularData) loggerData.get(APPENDERS_JMXITEM);

			String[] appIndex = new String[] { appender.getName() };
			CompositeData appenderData = (CompositeData) appenders.get(appIndex);
			if (appenderData != null) {
				appenders.remove(appIndex);
			}

			appenderData = getAppenderCompositeData(appender);
			appenders.put(appenderData);
		}
		else {
			if (loggerData != null) {
				loggers.remove(logIndex);
			}

			loggerData = getLoggerCompositeData(logger);
			loggers.put(loggerData);
		}
	}

}