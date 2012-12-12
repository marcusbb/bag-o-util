/*
 * (C) 2010 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/util/BaseUtil.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.util;

import static provision.util.StringUtil.NEWLINE;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Provides a set of general utilities.
 * 
 * @author Iulian Vlasov
 * @since BA-Plus
*/
public class BaseUtil {

    /**
     * Checks for empty objects. An object is considered empty if it is null or
     * it has no content.
     * 
     * @param o
     * @return
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
        else
            return false;
    }

    /**
     * Enhanced toString representation of complex objects such as arrays and
     * throwables.
     * 
     * @param obj
     * @return
     */
    public static String toString(Object obj) {
        if (obj instanceof Throwable) {
            return toString((Throwable) obj, Integer.MAX_VALUE);
        }
        else if (obj instanceof Object[]) {
            Object[] arr = (Object[]) obj;
            StringBuilder sb = new StringBuilder();
            sb.append("Array[").append(arr.length).append("]");
            for (int i = 0; i < arr.length; i++) {
                sb.append(' ').append(arr[i]);
            }
            return sb.toString();
        }
        else if (obj != null) {
            return obj.toString();
        }

        return null;
    }

    /**
     * Prints a formatted message containing all chained exception messages.
     * 
     * @param t
     * @param stackTraceSize
     * @return
     */
    public static String toString(Throwable t, int stackTraceSize) {
        if (stackTraceSize == -1) {
            return getStackTrace(t);
        }

        StringBuilder sb = new StringBuilder();

        int level = -1;
        Throwable cause = t;
        while (cause != null) {
            level++;
            if (cause != t) sb.append(NEWLINE).append(StringUtil.fill(' ', level << 1)).append("Caused by: ");

            sb.append(cause.getClass().getName()).append(": ").append(cause.getMessage());

            if (stackTraceSize != -1) {
                StackTraceElement[] ste = cause.getStackTrace();
                int size = Math.min(stackTraceSize, ste.length);
                for (int i = 0; i < size; i++) {
                    sb.append(NEWLINE).append(StringUtil.fill(' ', (level + 1) << 1)).append("- ");
                    sb.append(ste[i].getClassName()).append(':');
                    sb.append(ste[i].getMethodName()).append(':');
                    sb.append(ste[i].getLineNumber());
                    sb.append('(').append(ste[i].getFileName()).append(')');
                }
            }

            if (cause != cause.getCause()) {
                cause = cause.getCause();
            }
            else {
                cause = null;
            }
        }

        return sb.toString();
    }

    /**
     * @param t
     * @param stSize
     * @return
     */
    public static StringBuilder toNiceString(Throwable t, int stSize) {
        if (stSize == -1) {
            stSize = Integer.MAX_VALUE;
        }

        StringBuilder sb = new StringBuilder();
        int level = -1;
        
        processThr(t, sb, stSize, level + 1);
        sb.insert(0, NEWLINE);
        return sb;
    }
    
    private static int processThr(Throwable t, StringBuilder sb, int stMaxSize, int level) {
        Throwable cause = t.getCause();
        int size = level;
        if ((cause != null) && (cause != t)) {
            size = processThr(cause, sb, stMaxSize, level + 1);
        }
        
        if (size > level) {
            sb.append(NEWLINE).append(StringUtil.fill(' ', (size - level) << 1)).append("Wrapped in: ");
        }
        
        sb.append(t.getClass().getName());
        String msg = t.getMessage();
        if (msg != null) sb.append(": ").append(msg);

        if (stMaxSize != -1) {
            StackTraceElement[] ste = t.getStackTrace();
            int stSize = Math.min(stMaxSize, ste.length);
            for (int i = 0; i < stSize; i++) {
                sb.append(NEWLINE).append(StringUtil.fill(' ', ((size - level + 1) << 1) - 1)).append("- ");
                sb.append(ste[i].getClassName()).append(':');
                sb.append(ste[i].getMethodName()).append(':');
                sb.append(ste[i].getLineNumber());
                sb.append('(').append(ste[i].getFileName()).append(')');
            }
        }
        
        return size;
    }
    
    /**
     * Returns a stack trace as a string.
     * 
     * @param t
     * @return
     */
    public static String getStackTrace(Throwable t) {
        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);

        t.printStackTrace(writer);
        return out.toString();
    }

    /**
     * @param <T>
     * @param c
     * @return
     */
    public static <T> Class<T> getCallerClass(Class<T> c) {
        StackTraceElement[] st = new Throwable().getStackTrace();
        StackTraceElement ste = st[st.length > 2 ? 2 : 1];
        String callerName = ste.getClassName();
        
        try {
            return ReflectionUtil.newClass(callerName, c);
        }
        catch (ReflectionException re) {
            return null;
        }
    }

    /**
     * @param text
     */
    public static void printBig(String text) {
        String bigtext = BigString.get(text);
        System.out.println(bigtext);
    }

    public static Object toBigString(String text) {
        String big = BigString.get(text, 2, 11, null);
        return big;
    }

    /**
     * @param <K>
     * @param <V>
     * @param count
     * @return
     */
    public static <K, V> Map<K, V> createMap(int count) {
        return createMap(count, false);
    }

    /**
     * @param <K>
     * @param <V>
     * @param count
     * @param sorted
     * @return
     */
    public static <K, V> Map<K, V> createMap(int count, boolean sorted) {
        Map<K, V> map = new HashMap<K, V>((int) (count / 0.75 + 1));
        return (sorted ? new TreeMap<K, V>(map) : map);
    }

    public static Map<String, Object> toMap(Properties p) {
        if (p == null) return null;
        
        Map<String, Object> map = new HashMap<String, Object>();
        Enumeration<?> e = p.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            map.put(key, p.getProperty(key));
        }
        
        return map;
    }

    public static boolean isRootCause(Throwable t, Class<?> xcp) {
        Throwable cause = t;
        while (cause != null) {
            if (cause.getClass().isAssignableFrom(xcp)) {
                return true;
            }

            if (cause != cause.getCause()) {
                cause = cause.getCause();
            }
            else {
                cause = null;
            }
        }
        
        return false;
    }

    public static String getStringProperty(String key, String defValue) {
        String value = null;
        try {
            value = System.getProperty(key, defValue);
        }
        catch (SecurityException sowhat) {
            value = defValue;
        }

        return value;
    }

    public static long getLongProperty(String key, long defValue) {
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
 
    public static boolean setBooleanProperty(String key, boolean newValue) {
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
    
    public static boolean isStandalone() {
        return (getStringProperty("weblogic.Name", null) == null);
    }
    
}