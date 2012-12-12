/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/logging/PSPatternLayout.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.logging;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.helpers.PatternParser;

import provision.util.ReflectionUtil;

/**
 * This class defines a customized pattern layout for logging events.
 * <p>
 * It takes advantage of dependency injection mechanism provided by Log4J to activate its own
 * framework for manipulating custom settings such as:
 * 
 * <pre>
 * - a customizable pattern parser
 * - an arbitrary list of conversion characters and their custom pattern converters
 * - an arbitrary list of properties for configuring the custom pattern converters
 * </pre>
 * <p>
 * Here is a properties configuration sample that uses this layout for the specified appender:
 * 
 * <pre>
 * log4j.appender.stdout.layout=provision.common.logging.PSPatternLayout
 * log4j.appender.stdout.layout.conversionPattern=[%d] %D %S %-5p {%P} %#: [%-17c{5} %-30t] %m%n
 * log4j.appender.stdout.layout.parser=provision.common.logging.PSPatternParser
 * log4j.appender.stdout.layout.patternConverters=m:provision.common.logging.DefaultMesssageConverter
 * log4j.appender.stdout.layout.converterProperties=m:propName1=propValue1,propName2=propValue2 P:name=value
 * </pre>
 * 
 * @author Iulian Vlasov
 * @since TurboPrv
 */
public class PSPatternLayout extends PatternLayout implements ConverterFactory {

	static final char TIMESTAMP_CONVERSION = 'D';
	static final char THREAD_CONVERSION = 'T';
	static final char PROCESS_CONVERSION = 'P';
	static final char SERVER_CONVERSION = 'S';
	static final char SEQUENCE_CONVERSION = '#';
	static final char MESSAGE_CONVERSION = 'm';
	static final char CALLER_CONVERSION = 'J';
	static final char PID_CONVERSION = 'e';
	
	private static final String LOG4J_PROPERTY_SEPARATOR = " ";
	private static final String CONVERSION_SEPARATOR = ":";
	private static final String CONVERTER_PROPERTY_SEPARATOR = ",";
	private static final String CONVERTER_VALUE_SEPARATOR = "=";

	private boolean useDefaults = true;
	private String conversionPattern;
	private Class<? extends PatternParser> parserClass;
	private Map<Character, String> convClassMap;
	private Map<Character, String[][]> convPropsMap;
	private PatternParser parser;
	private Map<Character, PatternConverter> converterMap;
	private boolean ignoreThrowables;
	private boolean activated;

	/**
	 * This property decides whether to use default settings for the pattern parser and the pattern
	 * converters, if they are not set set in the configuration file. By default, it is set to
	 * <code>true</code>.
	 * 
	 * @param b
	 */
	public void setUseDefaults(boolean b) {
		this.useDefaults = b;
	}

	/**
	 * Overrides super due to its horrible Log4J implementation - even though PatternLayout is an
	 * OptionHandler, its implementation doesn't allow sync between properties which remain ambigous
	 * until all are set.
	 * <p>
	 * This implementation saves the pattern and rely on the Log4J framework to activate the
	 * options.
	 * <p>
	 * After the options are activated once, if this property gets set once more, perhaps as a
	 * result of an MBean operation, the options get activated again, this time manually. As a
	 * result, it provides support for changes in the conversion pattern which introduce new
	 * conversion characters or converter implementations, defined by setting the
	 * {@link #setPatternConverters(String) patternConverters} property.
	 * 
	 * @param s - the conversion pattern
	 */
	@Override
	public void setConversionPattern(String s) {
		this.conversionPattern = s;
		if (activated) {
			activateOptions();
		}
	}

	/**
	 * This property specifies pattern parser class name. If this property is not set and
	 * {@link #setUseDefaults(boolean) useDefaults} is set to true, the default
	 * {@link PSatternParser} is used.
	 * 
	 * @param className
	 */
	public void setPatternParser(String className) {
		try {
			this.parserClass = ReflectionUtil.newClass(className, PatternParser.class);
		}
		catch (Exception e) {
			LogLog.error(MessageFormat.format("Invalid pattern parser class: {0}!", className), e);
		}
	}

	/**
	 * Use this property to define new conversion characters and their respective converter classes
	 * without changing the parser or the layout class.
	 * <p>
	 * The is a multi-value property specified as a space-delimited list of tokens in the form:
	 * <code>conversion-character:pattern-converter-classname</code>
	 * <p>
	 * The implementation does a lazy instantiation of the converters, in case some conversion
	 * characters are not actually part of the conversion pattern.
	 * 
	 * @param s
	 */
	public void setPatternConverters(String s) {
		String[] tokens = s.split(LOG4J_PROPERTY_SEPARATOR);
		if ((tokens.length > 0) && (convClassMap == null)) {
			this.convClassMap = new HashMap<Character, String>(tokens.length);
		}

		for (int i = 0; i < tokens.length; i++) {
			String[] kv = tokens[i].split(CONVERSION_SEPARATOR);
			char conversionChar = kv[0].trim().charAt(0);
			String className = kv[1].trim();
			convClassMap.put(conversionChar, className);

			if (getMapEntry(converterMap, conversionChar) != null) {
				converterMap.remove(conversionChar);
			}
		}
	}

	/**
	 * Use this property to define the properties for the custom pattern converters.
	 * <p>
	 * The is a multi-value property specified as a space-delimited list of tokens in the form:
	 * <code>conversion-character:prop-name=prop-value,prop-name=prop-value,...</code>
	 * <p>
	 * The implementation does a lazy configuration of the converters, in case some conversion
	 * characters are not actually part of the conversion pattern.
	 * 
	 * @param s
	 */
	public void setConverterProperties(String s) {
		String[] tokens = s.split(LOG4J_PROPERTY_SEPARATOR);
		if ((tokens.length > 0) && (convPropsMap == null)) {
			this.convPropsMap = new HashMap<Character, String[][]>(tokens.length);
		}

		for (int i = 0; i < tokens.length; i++) {
			String[] kv = tokens[i].split(CONVERSION_SEPARATOR);
			char conversionChar = kv[0].trim().charAt(0);

			String[] propskv = kv[1].trim().split(CONVERTER_PROPERTY_SEPARATOR);
			String[][] props = new String[propskv.length][];
			for (int j = 0; j < propskv.length; j++) {
				props[j] = propskv[j].split(CONVERTER_VALUE_SEPARATOR);
			}
			convPropsMap.put(conversionChar, props);
		}
	}

	/**
	 * This layout ignores throwable only if there is no custom message converter.
	 * 
	 * @return True if no custom message converter was configured, false otherwise.
	 */
	@Override
	public boolean ignoresThrowable() {
		return ignoreThrowables;
	}

	/**
	 * This method returns the custom pattern parser instantiated based on configuration settings
	 * for this layout. If the layout is misconfigured, it delegates to the superclass to return a
	 * default parser.
	 * 
	 * @param pattern - the conversion pattern for the parser
	 * @return The pattern parser
	 */
	@Override
	protected PatternParser createPatternParser(String pattern) {
		if (parser == null) {
			return super.createPatternParser(pattern);
		}

		if (activated) {
			try {
				this.parser = (PatternParser) ReflectionUtil.newObject(parserClass,
						(pattern == null ? DEFAULT_CONVERSION_PATTERN : pattern));

				if (parser instanceof CustomConverterSupport) {
					((CustomConverterSupport) parser).setConverterFactory(this);
				}
			}
			catch (Exception e) {
				// should not happen if we validated the class in the setter
				LogLog.error(MessageFormat.format("Unable to create the pattern parser: {0}!", parserClass.getName()),
						e);
			}
		}

		return parser;
	}

	/**
	 * Overrides super to activate changes in the conversion pattern, pattern converter classes and
	 * their properties.
	 */
	@Override
	public void activateOptions() {
		if (parserClass == null) {
			if (!useDefaults) return;

			parserClass = PSPatternParser.class;
		}

		if ((parser == null) || !parserClass.isAssignableFrom(parser.getClass())) {
			try {
				this.parser = (PatternParser) ReflectionUtil.newObject(parserClass,
						(conversionPattern == null ? DEFAULT_CONVERSION_PATTERN : conversionPattern));

				if (parser instanceof CustomConverterSupport) {
					((CustomConverterSupport) parser).setConverterFactory(this);
				}
			}
			catch (Exception e) {
				// should not happen if we validated the class in the setter
				LogLog.error(MessageFormat.format("Unable to create the pattern parser: {0}!", parserClass.getName()),
						e);
			}
		}

		// Only now it is time to parse the pattern. Again, horrible Log4J
		// implementation that forces us to call the super
		// setter which indirectly allows us to return our custom parser.
		super.setConversionPattern(conversionPattern);

		this.ignoreThrowables = (getMapEntry(converterMap, MESSAGE_CONVERSION) == null);
		this.activated = true;
	}

	/**
	 * This implementation of the ConverterFactory uses the converters defined by
	 * {@link #setPatternConverters(String)}. In addition, it provides default converters for the
	 * following conversion characters:
	 * <ul>
	 * <li>SEQUENCE_CONVERSION - {@link DefaultSequenceConverter}</li>
	 * <li>SERVER_CONVERSION - {@link DefaultServerNameConverter}</li>
	 * <li>PROCESS_CONVERSION - {@link DefaultProcessNameConverter}</li>
	 * <li>THREAD_CONVERSION - {@link DefaultThreadNameConverter}</li>
	 * <li>TIMESTAMP_CONVERSION - {@link DefaultTimestampConverter}</li>
	 * <li>MESSAGE_CONVERSION - {@link BlobMessageConverter}</li>
	 * </ul>
	 * 
	 * @param conversionChar
	 * @return
	 * @see provision.common.logging.ConverterFactory#getConverter(char)
	 */
	public PatternConverter getConverter(char conversionChar) {
		PatternConverter converter = (PatternConverter) getMapEntry(converterMap, conversionChar);
		if (converter == null) {
			String className = (String) getMapEntry(convClassMap, conversionChar);
			if (className != null) {
				try {
					Class<PatternConverter> converterClass = ReflectionUtil.newClass(className, PatternConverter.class);
					converter = (PatternConverter) ReflectionUtil.newObject(converterClass);
				}
				catch (Exception e) {
					LogLog.error(MessageFormat.format(
							"Unable to handle pattern conversion [{0}] with converter class [{1}]!", conversionChar,
							className), e);
				}
			}

			if ((converter == null) && useDefaults) {
				switch (conversionChar) {
				case SEQUENCE_CONVERSION:
					converter = new DefaultSequenceConverter();
					break;
				case SERVER_CONVERSION:
					converter = new DefaultServerNameConverter();
					break;
				case PROCESS_CONVERSION:
					converter = new DefaultProcessNameConverter();
					break;
				case THREAD_CONVERSION:
					converter = new DefaultThreadNameConverter();
					break;
				case TIMESTAMP_CONVERSION:
					converter = new DefaultTimestampConverter();
					break;
				case MESSAGE_CONVERSION:
					converter = new BlobMessageConverter();
					break;
				case CALLER_CONVERSION:
					converter = new DefaultCallerConverter();
					break;
				case PID_CONVERSION:
					converter = new DefaultPIDConverter();
					break;
				default:
					return null;
				}
			}
		}

		if (converter != null) {
			// cache the instances, in case the factory is called again
			if (converterMap == null) {
				this.converterMap = new HashMap<Character, PatternConverter>();
			}
			converterMap.put(conversionChar, converter);

			// only now it is time to inject converter properties
			String[][] props = (String[][]) getMapEntry(convPropsMap, conversionChar);
			ReflectionUtil.setProperties(converter, props);
		}

		return converter;
	}

	private Object getMapEntry(Map<Character, ?> map, Character key) {
		return (map == null ? null : map.get(key));
	}
}
