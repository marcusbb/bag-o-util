package provision.util;

import java.util.Map.Entry;
import java.util.Properties;
import junit.framework.Assert;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import provision.util.PropertiesUtil;

public class PropertiesUtilTest {
	@Test
	public void testToPropertiess(){
		String pairDelim = "=*-";
		String entryDelim = ";*&";
		Properties expected = createProperties("key1", "val1", 
												"key2", "val2", 
												"key3", "val3");
		StringBuffer propsStr = new StringBuffer().append("key1").append(pairDelim).append("val1")
												  .append(entryDelim).append("key2").append(pairDelim).append("val2")
												  .append(entryDelim).append("key3").append(pairDelim).append("val3");
		
		Properties actual = PropertiesUtil.toProperties(propsStr.toString(), pairDelim, entryDelim);
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testToPropertiess_PropertiesStrNull(){
		String pairDelim = "=*-";
		String entryDelim = ";*&";
		
		Properties actual = PropertiesUtil.toProperties(null, pairDelim, entryDelim);
		Assert.assertNotNull(actual);
		Assert.assertTrue(actual.isEmpty());
	}

	@Test
	public void testToPropertiess_NameValueDelimNull(){
		String pairDelim = null;
		String entryDelim = ";*&";
		StringBuffer propsStr = new StringBuffer().append("key1").append(entryDelim).append("val1");;
		
		Properties actual = PropertiesUtil.toProperties(propsStr.toString(), pairDelim, entryDelim);
		Assert.assertNotNull(actual);
		Assert.assertTrue(actual.isEmpty());
	}

	@Test
	public void testToPropertiess_PropertyPairDelimiterNull(){
		String pairDelim = "=*-";
		String entryDelim = null;
		StringBuffer propsStr =  new StringBuffer().append("key1").append(pairDelim).append("val1");
		
		Properties actual = PropertiesUtil.toProperties(propsStr.toString(), pairDelim, entryDelim);
		Assert.assertNotNull(actual);
		Assert.assertTrue(actual.isEmpty());
	}

	@Test
	public void testToPropertiess_PairDelimMissingForOneEntry(){
		String pairDelim = "=*-";
		String entryDelim = ";*&";
		
		Properties expected = createProperties("key1", "val1",  
												"key3", "val3");
		StringBuffer propsStr = new StringBuffer().append("key1").append(pairDelim).append("val1")
												  .append(entryDelim).append("key2")
												  .append(entryDelim).append("key3").append(pairDelim).append("val3");
		
		Properties actual = PropertiesUtil.toProperties(propsStr.toString(), pairDelim, entryDelim);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testToPropertiess_NameBlankForOneEntry(){
		String pairDelim = "=*-";
		String entryDelim = ";*&";
		Properties expected = createProperties("key1", "val1", 
				"key3", "val3");
		
		StringBuffer propsStr = new StringBuffer().append("key1").append(pairDelim).append("val1")
				  .append(entryDelim).append(pairDelim).append("val2")
				  .append(entryDelim).append("key3").append(pairDelim).append("val3");
		
		Properties actual = PropertiesUtil.toProperties(propsStr.toString(), pairDelim, entryDelim);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testToPropertiess_ValueBlankForOneEntry(){
		String pairDelim = "=*-";
		String entryDelim = ";*&";
		Properties expected = createProperties("key1", "val1", 
				"key3", "val3");
		
		StringBuffer propsStr = new StringBuffer().append("key1").append(pairDelim).append("val1")
				  .append(entryDelim).append("key2").append(pairDelim)
				  .append(entryDelim).append("key3").append(pairDelim).append("val3");
		
		Properties actual = PropertiesUtil.toProperties(propsStr.toString(), pairDelim, entryDelim);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testToPropertiess_ExtraPairDelimBeforeNextEntryDelim(){
		String pairDelim = "=*-";
		String entryDelim = ";*&";
		Properties expected = createProperties("key1", "val1".concat(pairDelim).concat("morestuff"), 
											    "key2", "val2", 
											    "key3", "val3");
		StringBuffer propsStr = new StringBuffer().append("key1").append(pairDelim).append("val1").append(pairDelim).append("morestuff")
												  .append(entryDelim).append("key2").append(pairDelim).append("val2")
												  .append(entryDelim).append("key3").append(pairDelim).append("val3");
		
		Properties actual = PropertiesUtil.toProperties(propsStr.toString(), pairDelim, entryDelim);
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testToPropertiess_EntryDelimAtEnd(){
		String pairDelim = "=*-";
		String entryDelim = ";*&";
		Properties expected = createProperties("key1", "val1", 
				"key2", "val2", 
				"key3", "val3");

		StringBuffer propsStr = new StringBuffer().append("key1").append(pairDelim).append("val1")
				  .append(entryDelim).append("key2").append(pairDelim).append("val2")
				  .append(entryDelim)
				  .append(entryDelim).append("key3").append(pairDelim).append("val3")
				  .append(entryDelim);

		Properties actual = PropertiesUtil.toProperties(propsStr.toString(), pairDelim, entryDelim);
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testToString(){
		String pairDelim = "=*-";
		String entryDelim = ";*&";
		Properties props = createProperties("key1", "val1", 
												"key2", "val2", 
												"key3", "val3");		
		String actual = PropertiesUtil.toString((Properties)props.clone(), pairDelim, entryDelim);
		assertPropString(props, pairDelim, entryDelim, actual);
	
	}
	
	@Test
	public void testToString_PairDelimNull(){
		String pairDelim = null;
		String entryDelim = ";*&";
		Properties props = createProperties("key1", "val1", 
												"key2", "val2", 
												"key3", "val3");		
		String actual = PropertiesUtil.toString((Properties)props.clone(), pairDelim, entryDelim);
		Assert.assertNull(actual);
	
	}

	@Test
	public void testToString_EntryDelimNull(){
		String pairDelim = "=*-";
		String entryDelim = null;
		Properties props = createProperties("key1", "val1", 
												"key2", "val2", 
												"key3", "val3");		
		String actual = PropertiesUtil.toString((Properties)props.clone(), pairDelim, entryDelim);
		Assert.assertNull(actual);
	
	}

	@Test
	public void testToString_PropertiesNull(){
		String pairDelim = "=*-";
		String entryDelim = ";*&";

		String actual = PropertiesUtil.toString(null, pairDelim, entryDelim);
		Assert.assertNull(actual);
	
	}

	@Test
	public void testToString_PropertiesEmpty(){
		String pairDelim = "=*-";
		String entryDelim = ";*&";
		Properties props = new Properties();		
		String actual = PropertiesUtil.toString(props, pairDelim, entryDelim);
		Assert.assertNull(actual);
	
	}

	public void assertPropString(Properties props, String expectedPairDelim, String expectedEntryDelim, String actual){		
		
		Assert.assertNotNull(actual);		
		
		int expectedEntryDelimCount = props.size()-1;
		int expectedPairDelimCount = props.size();
		
		//can't predict order of entries in string so just check it is segmented properly, has all the prop keys and values, and has no extra delims hanging around.		
		Assert.assertEquals(expectedEntryDelimCount, StringUtils.countMatches(actual, expectedEntryDelim));
		Assert.assertEquals(expectedPairDelimCount, StringUtils.countMatches(actual, expectedPairDelim));
		
		//split the string by entry delimiter to see if the delimiters are placed properly. Should be same size as expected properties		
		Assert.assertEquals(props.size(), StringUtils.splitByWholeSeparator(actual, expectedEntryDelim).length);
					
		int expectedStrLen = expectedEntryDelim.length()*expectedEntryDelimCount;
		
		for(Entry<Object, Object> expectedProperty : props.entrySet()){
			String expectedNameValString = ((String)expectedProperty.getKey()).concat(expectedPairDelim).concat((String)expectedProperty.getValue()); 
			expectedStrLen += expectedNameValString.length();
			
			Assert.assertTrue(StringUtils.contains(actual, expectedNameValString));						
		}
		
		Assert.assertEquals(expectedStrLen, actual.length());
	}
	
	/**
	 * 
	 * @param props each name string should be followed by value string
	 * @return
	 */
	public static Properties createProperties(String... nameValues) {
		Properties props = new Properties();
		String name = null;		
		for (int i = 0; i < nameValues.length; i++) {
			if(i % 2 == 0){
				name = nameValues[i].trim();
			}else{
				props.setProperty(name, nameValues[i].trim());
			}
		}
		return props;
	}
}
