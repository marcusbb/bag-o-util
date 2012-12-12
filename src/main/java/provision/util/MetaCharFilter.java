package provision.util;

public class MetaCharFilter {
	private static final String DEFAULT_SUBS = "_";
	
	public static String filter(String userInput, String substitude) {
		if (userInput == null) {
			return "";
		}
		
		String filtered = userInput.replaceAll("([^A-Za-z0-9@.' _-]+)", substitude);
		return filtered;
	}
	
	public static String filter(String userInput) {
		return filter(userInput, DEFAULT_SUBS);
	}
	
	/**
	 * replaces &, < , > with &amp;,&lt; and &gt;
	 * @param value
	 * @return
	 */
	public static String escapeXmlChars(String value) {
		String temp = value.replaceAll("\\&", "\\&amp;");
		temp = temp.replaceAll("<", "\\&lt;");
		temp = temp.replaceAll(">", "\\&gt;");
		return temp;
	}
}
