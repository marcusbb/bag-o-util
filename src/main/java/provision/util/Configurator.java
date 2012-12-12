/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/util/Configurator.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.util;

import java.util.Properties;

/**
 * This class provides a mechanism for retrieving application configuration data. For now it loads
 * configuration properties from a boot resource file. If the resource file doesn't exist or the
 * properties are not defined, it will use defaults defined with system properties for easy mockup
 * tests.
 * <p>
 * In future the implementation will make use of "system parameters" in read-only mode.
 * <p>
 * If properties are defined as system properties, they must be prefixed by {@link #PREFIX} to
 * create an application namespace.
 * 
 * @author Iulian Vlasov
 */
public class Configurator {

	/**
	 * Prefix for all properties used by the configuration provider.
	 */
	public static final String PREFIX = "prv.";

	public static final String CLASS_SUFFIX  = ".class";
	public static final String ARGS_SUFFIX   = ".args";
	public static final String ARGSEP_SUFFIX = ".argsep";

	/**
	 * Property for the resource holding all boot/config properties.
	 */
	static final String BOOT_RESOURCE_PROPERTY = PREFIX + "boot";

	/**
	 * Default resource holding all boot/config properties.
	 */
	static final String DEFAULT_BOOT_RESOURCE = "boot.properties";

	/**
	 * Configuration properties loaded from the "boot" resource file. If no such resource exists,
	 * this object is empty but not null so clients will avoid getting NPE.
	 */
	private static Properties bootProps;

	static {
		String bootResource = StringUtil.getStringProperty(BOOT_RESOURCE_PROPERTY, DEFAULT_BOOT_RESOURCE);
		bootProps = ResourceUtil.getResourceAsProperties(bootResource);
		if (bootProps == null) {
			bootProps = new Properties();
		}
	}

	/**
	 * Return all configuration properties.
	 * 
	 * @return
	 */
	public static Properties getProperties() {
		return bootProps;
	}

	/**
	 * Return a configuration property as a String or null if the property is undefined.
	 * 
	 * @param name
	 * @return
	 */
	public static String getProperty(String name) {
		return getProperty(name, null);
	}

	/**
	 * Return a configuration property as a String or a default value if the property is undefined.
	 * 
	 * @param name
	 * @param defValue
	 * @return
	 */
	public static String getProperty(String name, String defValue) {
		String property = bootProps.getProperty(name);
		if (property == null) {
			property = getSystemProperty(PREFIX + name, defValue);
		}

		return property;
	}

	/**
	 * Return a configuration property as a Class type, as long as it is of the type given by the
	 * target argument.
	 * 
	 * @param name - property that points to the fully qualified class name
	 * @param target - target type
	 * @return
	 */
	public static <C> Class<? extends C> getClassProperty(String name, Class<C> target) {
		String fqcn = getProperty(name);
		if (SystemUtil.isEmpty(fqcn)) {
			fqcn = getProperty(name + CLASS_SUFFIX);
			if (SystemUtil.isEmpty(fqcn)) {
				return null;
			}
		}
		
		Class<C> c = ReflectionUtil.newClass(fqcn, target);
		return c;
	}

	/**
	 * @param <T>
	 * @param name
	 * @param target
	 * @return
	 */
	public static <T> T getObjectProperty(String name, Class<T> target) {
		return getObjectProperty(name, target, (Object[]) null);
	}

	/**
	 * @param <T>
	 * @param name
	 * @param target
	 * @param args
	 * @return
	 */
	public static <T> T getObjectProperty(String name, Class<T> target, Object... args) {
		Class<? extends T> c = getClassProperty(name, target);
		if (c == null) {
			return null;
		}

		T o = null;
		
		if (args != null) {
			o = ReflectionUtil.newDynamicObject(c, args);
		}
		else {
			// try getting arguments
			String sArgs = getProperty(name + ARGS_SUFFIX);
			if (SystemUtil.isEmpty(sArgs)) {
				// couldn't get the specific args, so try with the properties object itself or no-args
				o = ReflectionUtil.newDynamicObject(c, bootProps);
			}
			else {
				String argSep = getProperty(name + ARGSEP_SUFFIX);
				if (SystemUtil.isEmpty(argSep)) {
					// couldn't get the args separator, so try String or no-args constructor
					o = ReflectionUtil.newDynamicObject(c, sArgs);
				}
				else {
					// try String[] or no-args constructor
					Object[] cArgs = sArgs.split(argSep);
					o = ReflectionUtil.newDynamicObject(c, cArgs);
				}
			}
			
		}

		return o;
	}

	/**
	 * Return a configuration property as a long numeric value or a default value if the property is
	 * undefined.
	 * 
	 * @param name
	 * @param defValue
	 * @return
	 */
	public static long getLongProperty(String name, long defValue) {
		String sValue = getProperty(name);
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

	/**
	 * Return a configuration property as a boolean value.
	 * 
	 * @param name
	 * @return
	 */
	public static boolean getBooleanProperty(String name) {
		String sValue = getProperty(name);
		return Boolean.parseBoolean(sValue);
	}

	/**
	 * Return the system property given by the name argument or a default value if the property is
	 * undefined. It handles security exceptions and it returns null if the property cannot be read.
	 * 
	 * @param name
	 * @param defValue
	 * @return
	 */
	public static String getSystemProperty(String name, String defValue) {
		String value = null;
		try {
			value = System.getProperty(name, defValue);
		}
		catch (SecurityException sowhat) {
			value = defValue;
		}

		return value;
	}

}