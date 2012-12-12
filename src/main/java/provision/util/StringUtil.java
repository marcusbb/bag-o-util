/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/util/StringUtil.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class that deals with strings and produces string representations of various object
 * types.
 * 
 * @author Iulian Vlasov
 * @since TurboPrv
 */
public class StringUtil {

	/**
	 * Use for inserting new lines in formatted string representations. This is an application-wide
	 * constant.
	 */
	public static final String NEWLINE = System.getProperty("line.separator");

	/**
	 * Enhanced toString representation of complex objects such as arrays and throwables.
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
	 * @param ctx
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
			if (cause != t) sb.append(NEWLINE).append(fill(' ', level << 1)).append("Caused by: ");

			sb.append(cause.getClass().getName()).append(": ").append(cause.getMessage());

			if (stackTraceSize != -1) {
				StackTraceElement[] ste = cause.getStackTrace();
				int size = Math.min(stackTraceSize, ste.length);
				for (int i = 0; i < size; i++) {
					sb.append(NEWLINE).append(fill(' ', (level + 1) << 1)).append("- ");
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
     * Create a string by repeating the source string the specified number of
     * times.
     * 
     * @param source - source string to be repeated
     * @param count - the number of times to repeat the source string
     * @return String
     */
    public static String fill(char fchar, int count) {
        StringBuilder sb = new StringBuilder(count);

        for (int i = 0; i < count; i++) {
            sb.insert(i, fchar);
        }

        return sb.toString();
    }

	/**
	 * Underline the specified string using the specified character.
	 * 
	 * @param source
	 * @param uchar
	 * @return The "underlined" string as a StringBuilder.
	 */
	public static StringBuilder underline(String source, char uchar) {
		int srcLen = source.length();
		StringBuilder sb = new StringBuilder(source).append(NEWLINE).append(fill(uchar, srcLen))
				.append(NEWLINE);
		return sb;
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
	
	public static boolean isEquals(String source, String target){
		return (source == target) || ((source != null) && (target != null) && (source.equals(target)));
	}

	/**
	 * Used to return tokenized (key=value) printable information.
	 * 
	 * @param sb
	 * @param key
	 * @param value
	 * @return
	 */
	public static StringBuilder appendValue(StringBuilder sb, String key, Object value) {
		return sb.append(" [").append(key).append("]=").append(value);
	}

	public static StringBuilder appendLine(StringBuilder sb, Object value) {
		return (value == null ? sb : sb.append(NEWLINE).append(value));
	}

	/**
	 * Pad the source string with trailing spaces to the specified length.
	 * 
	 * @param source Source string to be padded
	 * @param length The final length of the string
	 * @return String
	 */
	public static String padRight(String source, int length) {
		return padRight(source, length, ' ');
	}

	/**
	 * Pad the source string with trailing characters to the specified length.
	 * 
	 * @param source String source string to be padded
	 * @param length The final length of the string
	 * @param padChar The character used to pad the source string
	 * @return String
	 */
	public static String padRight(String source, int length, char padChar) {
		if (source == null) source = "";

		if (length > source.length()) {
			char[] chars = new char[length];

			source.getChars(0, source.length(), chars, 0);

			for (int i = source.length(); i < length; i++) {
				chars[i] = padChar;
			}

			return new String(chars);
		}
		else {
			return source;
		}
	}

	/**
	 * Pad the source string with trailing spaces to the specified length.
	 * 
	 * @param source Source string to be padded
	 * @param length The final length of the string
	 * @return String
	 */
	public static String padLeft(String source, int length) {
		return padLeft(source, length, ' ');
	}

	/**
	 * Pad the source string with trailing characters to the specified length.
	 * 
	 * @param source String source string to be padded
	 * @param length The final length of the string
	 * @param padChar The character used to pad the source string
	 * @return String
	 */
	public static String padLeft(String source, int length, char padChar) {
		if (source == null) source = "";

		int diff = length - source.length();
		if (diff > 0) {
			char[] chars = new char[length];

			source.getChars(0, source.length(), chars, diff);

			for (int i = 0; i < diff; i++) {
				chars[i] = padChar;
			}

			return new String(chars);
		}
		else {
			return source;
		}
	}
	
    public static boolean isEmpty(String source){
        return (source == null) || (source.equals(""));
    }

    /**
     * Finds out if a string contains a value. White spaces before and
     * after the string will be trimmed. Empty string or null value is 
     * considered no value.  
     * 
     * Note that this is different from isEmpty()
     * in the evaluation of a string containing only whitespaces:
     * 
     * isEmpty(" ") returns false
     * hasValue(" ") returns false
     *  
     * @param source
     * @return true if the string contains a value; false if the string
     *         is null or empty.
     */
    public static boolean hasValue(String source) {
    	if(source == null || "".equals(source.trim())) {
    		return false;
    	} else {
    		return true;
    	}
    }
    
    /* Check if a String has text. More specifically, returns <code>true</code> if the string not
     * <code>null<code>, it's <code>length is > 0</code>, and it has at least one non-whitespace character.
     * <p>
     * 
     * <pre>
     * StringUtil.hasText(null) = false
     * StringUtil.hasText("") = false
     * StringUtil.hasText(" ") = false
     * StringUtil.hasText("12345") = true
     * StringUtil.hasText(" 12345 ") = true
     * </pre>
     * 
     * @param str the String to check, may be null
     * @return <code>true</code> if the String is not null, length > 0, and not whitespace only
     */
    public static boolean hasText(String str) {
        return str != null && str.trim().length() > 0;
    }
    
	/**
	 * Returns null if the given string is null or it only has whitespaces, otherwise returns the string. See {{@link #hasText(String)}
	 * 
	 * @param str - the string to be checked.
	 * @return the string if is not null and has at least not white space character, null otherwise
	 */
	public static String nullIfNoText(String str) {
		return hasText(str) ? str : null;
	}

	/**
	 * Returns null if the given string is null or empty, otherwise returns the string. See {{@link #isEmpty(String)}
	 * 
	 * @param str - the string to be checked.
	 * @return the string if is not null and not empty string, null othewise
	 */
	public static String nullIfEmpty(String str) {
		return !isEmpty(str) ? str : null;
	}
    
    public static boolean matches(Pattern pattern, String input){
        if((pattern == null) || (input == null)){
            return false;
        }

        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
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
	
	public static boolean isStringExistsInArray(String target, String[] arrSource){
		boolean exists = false;
		
		if(StringUtil.isEmpty(target) || StringUtil.isEmpty(arrSource)){
			return exists;
		}
		
		for(String curSource : arrSource){
			if (target.equals(curSource)){
				exists = true;
				break;
			}
		}
		
		return exists;
	}

	public static void main(String[] args) {
		String s = " ";
		try {
			System.out.println("hasValue():" + StringUtil.hasValue(s));
			System.out.println("isEmpty():" + StringUtil.isEmpty(s));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    public static String justify(String src, int length, char padChar) {
        String filled = fill(padChar, length);
        char[] chars = new char[length];
        filled.getChars(0, length, chars, 0);
        int slen = src.length();
        int pad = (length - slen) >> 1;
        src.getChars(0, src.length(), chars, pad);
        return new String(chars);
    }

    public static StringBuilder box(String text, char border, int hpad, int vpad, String justText) {
        Scanner scan = new Scanner(text).useDelimiter(NEWLINE);
        List<String> lines = new ArrayList<String>();
        int textWidth = 0;
        int windex = -1;
        while (scan.hasNext()) {
            String line = scan.next();
            textWidth = Math.max(textWidth, line.length());
            lines.add(line);
            
            if (line.equals(justText)) {
                windex = lines.size() - 1;
            }
        }

        if (windex != -1) {
            String line = justify(lines.get(windex), textWidth, '-');
            lines.set(windex, line);
        }

        int boxWidth = textWidth + (hpad << 1) + 2;
        String longSide = fill(border, boxWidth);
        String padLine = fill(' ', boxWidth - 2);
        String hPadding = fill(' ', hpad);

        StringBuilder sb = new StringBuilder(longSide).append(NEWLINE);
        for (int v = 0; v < vpad; v++) {
            sb.append(border).append(padLine).append(border).append(NEWLINE);
        }
        for (String line : lines) {
            sb.append(border).append(hPadding).append(padRight(line, textWidth)).append(hPadding).append(
                    border).append(NEWLINE);
        }
        for (int v = 0; v < vpad; v++) {
            sb.append(border).append(padLine).append(border).append(NEWLINE);
        }
        sb.append(longSide);
        
        return sb;
    }
	
    public static String replace(String source, Properties properties) {
		HashMap<String, String> map = new HashMap<String, String>(properties.size());
		Iterator<Object> iter = properties.keySet().iterator();
		while(iter.hasNext()) {
			String key = (String)iter.next();
			map.put(key, properties.getProperty(key));
		}
		return replace(source,map);
	}
	/**
	 * Replaces ${__} with a map according to what is provided.
	 * 
	 * @param source
	 * @param replacements
	 * @return
	 */
	public static String replace(String source,Map<String,String> replacements) {

		if (replacements == null )
			return source;
		if (replacements.size() ==0)
			return source;
		
		
		char []ch = source.toCharArray();
		StringBuilder formatted = new StringBuilder();
		boolean skip = false;
		
		for (int i=0;i<ch.length;i++) {
			
				
			if (i>1 && ch[i-1] == '$' && ch[i] == '{') {
				//formatted.delete(i-1, i);
				StringBuilder rep = new StringBuilder();
				while(ch[++i] != '}') {
					rep.append(ch[i]);
				}
				String replace = replacements.get(rep.toString());
				if (replace == null)
					replace = "${" + rep.toString() + "}" ;
				formatted.append(replace);
				
				
			} else if (ch[i] == '$') {
				skip = true;
			}else {
				formatted.append(ch[i]);
			}
			
		}
		return formatted.toString();
		
	}
	
	//
	//may end up deprecating or refactoring ${} replace util
	//method below
	//
	public static final String SYS_PROP_START = "${";
	public static final int START_LEN = SYS_PROP_START.length();
	public static final String SYS_PROP_END = "}";
	public static final int END_LEN = SYS_PROP_START.length();
	
	public static String replaceVariables(String value){
		if(value == null){
			return null;
		}
				
		StringBuffer buf = new StringBuffer(value);
		
		int from = 0;
		int varStart = buf.indexOf(SYS_PROP_START, from);	
		int varEnd = buf.indexOf(SYS_PROP_END, from);
		
		while(varStart >= 0 && varEnd >= 0){
			
			String systemProp = buf.substring(varStart + START_LEN, varEnd);
			String sysPropValue = System.getProperty(systemProp, "");
			System.out.println(sysPropValue);
			
			int delta = varStart + (varEnd+1 - varStart);
			buf.replace(varStart, delta, sysPropValue);
			
			from = varStart + sysPropValue.length();
			
			varStart = buf.indexOf(SYS_PROP_START, from);		
			varEnd = buf.indexOf(SYS_PROP_END, from);			
		}		
		
		return buf.toString();
	}		
}
