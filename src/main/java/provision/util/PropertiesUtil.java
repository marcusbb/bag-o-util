package provision.util;

import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

public class PropertiesUtil {

	/**
	 * 
	 * @param propertiesStr - the string to build into properties
	 * @param nameValueDelimiter - 	the delimiter is used as whole to split the name value pair, only the first delimiter found will be split, the
	 * 								any remaining nameValueDelimiters before reaching the next propertyPairDelimiter will be part of the property value. 
	 * 		  e.g buildProperties("key1=*=val1;key2=*=val2", "=*=", ";"); 	[[key1, val1], [key2, val2]] 
	 * @param newPropertyDelimiter - the delimiter is used to identify each new property set.
	 * 		  e.g buildProperties("key1=*=val1;key2=*=val2", "=*=", ";"); 	[[key1, val1], [key2, val2]]
	 * @return
	 */
	public static Properties toProperties(String propertiesStr, String nameValueDelimiter, String newPropertyDelimiter){
		Properties props = new Properties();
		if(StringUtils.isNotBlank(propertiesStr) && StringUtils.isNotBlank(nameValueDelimiter) && StringUtils.isNotBlank(newPropertyDelimiter)){
			String[] propertySets = StringUtils.splitByWholeSeparator(propertiesStr, newPropertyDelimiter);
			if(ArrayUtils.isNotEmpty(propertySets)){
				for(String propertySet : propertySets){
					String[] nameValuePair = StringUtils.splitByWholeSeparator(propertySet, nameValueDelimiter, 2);
					if(ArrayUtils.isNotEmpty(nameValuePair) && nameValuePair.length==2 && 
							StringUtils.isNotBlank(nameValuePair[0]) && StringUtils.isNotBlank(nameValuePair[1])){
						props.setProperty(nameValuePair[0].trim(), nameValuePair[1].trim()); 
					}
				}
				
			}			
		}
		
		return props;
	}
	
	public static String toString(Properties props, String nameValueDelimiter, String newPropertyDelimiter){
		if(props == null || props.isEmpty() || StringUtils.isBlank(nameValueDelimiter) || StringUtils.isBlank(newPropertyDelimiter)){
			return null;
		}
		
		StringBuffer buf = new StringBuffer();
		Set<Entry<Object, Object>> entries = props.entrySet();
		
		for(Entry<Object, Object> entry: entries){
			buf.append(entry.getKey()).append(nameValueDelimiter).append(entry.getValue()).append(newPropertyDelimiter);
		}
		
		return StringUtils.removeEnd(buf.toString(), newPropertyDelimiter);
	}
}
