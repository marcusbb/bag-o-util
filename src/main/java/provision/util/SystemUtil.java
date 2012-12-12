package provision.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


import provision.services.logging.LogConfigurator;

/**
 * Created by IntelliJ IDEA. User: jchin Date: 11-Aug-2004 Time: 3:47:10 PM To change this template
 * use File | Settings | File Templates.
 */
public class SystemUtil {
	static final String CALLER = "SystemUtil";
	private static final String DEFAULT_TSFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";


	public static final String padZerosInFront(String hexStr, int finalSize) {
		String r = null;
		if (hexStr == null || hexStr.length() >= finalSize) {
			return hexStr;
		}
		r = hexStr;
		for (int i = hexStr.length(); i < finalSize; i++) {
			r = "0" + r;
		}
		return r;
	}

	public static String getContextName() {
		return LogConfigurator.getContextName();
	}
	
	public static void addToken(StringBuilder sb, String name, Object value) {
		sb.append( StringUtil.padRight(name, 14)).append(": ").append(value).append(StringUtil.NEWLINE);
	}

	public static String printApplicationName(String applicationName, String applicationVersion, boolean on) {
		
		StringBuilder sb = new StringBuilder(on ? "+":"-").append(applicationName);
		
		if (applicationVersion != null) {
			sb.append(' ').append(applicationVersion);;
		}
		
		String text = BigString.get(sb.toString());
		SystemUtil.trace(text);
		
		return text;
	}

	public static void trace(Object o) {
		System.out.println(o);
	}

	/**
	 * @param f
	 * @param format
	 * @return
	 */
	public static String now() {
		return now(DEFAULT_TSFORMAT);
	}

	public static String now(String format) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(cal.getTime());
	}
	
	/**
	 * @param count
	 * @return
	 */
	public static <K,V> Map<K,V> createMap(int count) {
		return createMap(count, false);
	}

	/**
	 * @param count
	 * @param sorted
	 * @return
	 */
	public static <K,V> Map<K,V> createMap(int count, boolean sorted) {
		Map<K,V> map = new HashMap<K,V>((int)(count / 0.75 + 1));
		return (sorted ? new TreeMap<K,V>(map) : map);
	}

	/**
	 * Checks for empty objects. An empty is considered empty if it is null or it has no content.
	 * 
	 * @param value o Object
	 * @return true if the object is empty.
	 */
	@SuppressWarnings("unchecked")
	public static boolean isEmpty(Object o) {
		if (o == null) return true;

		if (o instanceof String) {
			return (((String) o).trim().length() == 0);
		}
		else if (o instanceof Object[]) {
			return (((Object[]) o).length == 0);
		}
		else if (o instanceof List) {
			return (((List) o).size() == 0);
		}
		else if (o instanceof Map) {
			return (((Map) o).isEmpty());
		}
		else if (o instanceof StringBuilder) {
			return (((StringBuilder) o).length() == 0);
		}
		else return false;
	}
	
	/**
	 * @param element
	 * @param list
	 * @return
	 */
	public static <T> List<T> addElement(T element, List<T> list) {
		boolean exists = false;

		if (list == null) {
			list = new ArrayList<T>(2);
		}
		else {
			exists = list.contains(element);
		}

		if (!exists) list.add(element);
		return list;
	}

	/**
	 * @param element
	 * @param list
	 */
	public static <T> void removeElement(T element, List<T> list) {
		if (list != null) list.remove(element);
	}

	/**
	 * Utility method for returning the name of the local Weblogic server
	 * 
	 * @return String The server name, or "Server Name Unavailable"
	 */
	public static String getLocalServerName() {
		return getLocalServerName("weblogic.Name");
	}

	public static String getLocalServerName( String propertyName) {
		String serverName = System.getProperty(propertyName);
		if (serverName == null || serverName.length() == 0) {
			serverName = "Server Name Unavailable";
		}
		return serverName;
	}

}