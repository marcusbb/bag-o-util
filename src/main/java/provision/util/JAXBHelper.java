/**
 * (C) 2012 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * @author Tran Huynh
 */
package provision.util;

import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

import provision.services.logging.Logger;

import com.sun.jersey.api.json.JSONJAXBContext;
import com.sun.jersey.api.json.JSONMarshaller;
import com.sun.jersey.api.json.JSONUnmarshaller;

/**
 * Helper class for marshaling and un-marshaling data to and from XML and JSON.
 * 
 * It uses Jersey's library underneath to perform the marshaling and un-marshaling.
 * 
 * @author trhuynh
 *
 */
public class JAXBHelper{
	
	private final static String CALLER = JAXBHelper.class.getSimpleName();
	
	public <T> T unmarshal(JSONJAXBContext context, String mediaType, InputStream in, Class<T> expectedClass) throws JAXBException, SAXException{
		if((context == null) || (mediaType == null) || ("".equals(mediaType)) || (in == null) || (expectedClass == null)){
			Logger.error(CALLER, "unmarshal_InitError", 
					     "Some required initialization property is missing",
					     "JAXB_CONTEXT_MISSING", (context == null),
					     "MEDIA_TYPE", mediaType,
					     "INPUT_STREAM_MISSING", (in == null),
					     "EXPECT_CLASS_MISSING", (expectedClass == null));
			throw new JAXBException(CALLER + "_unmarshal_InitError");
		}

		final T result;
		if (StringUtils.startsWithIgnoreCase(mediaType, MediaType.APPLICATION_XML)) {
		  result = unmarshalFromXml(context, in, expectedClass);
		} 
		else if (StringUtils.startsWithIgnoreCase(mediaType, MediaType.APPLICATION_JSON)) {
		  result = unmarshalFromJSON(context, in, expectedClass);
		} 
		else {
		  throw new JAXBException(CALLER + "_unmarshal_InitError: Unsupported content-type: " + mediaType);
		}
		return result;
	}
	
	public <T>T unmarshalFromJSON(JSONJAXBContext context, InputStream in, Class<T> expectedClass) throws JAXBException{
		if((context == null) || (in == null) || (expectedClass == null)){
			Logger.error(CALLER, "unmarshalFromJSON_InitError", 
					     "Some required initialization property is missing",
					     "JAXB_CONTEXT_MISSING", (context == null),
					     "INPUT_STREAM_MISSING", (in == null),
					     "EXPECT_CLASS_MISSING", (expectedClass == null));
			throw new JAXBException(CALLER + "_unmarshalFromJSON_InitError");
		}
		
		JSONUnmarshaller jsonUnmarshaller = context.createJSONUnmarshaller();
		return jsonUnmarshaller.unmarshalFromJSON(in, expectedClass);
	}

	public <T>T unmarshalFromXml(JSONJAXBContext context, InputStream in, Class<T> expectedClass) throws JAXBException, SAXException{
		if((context == null) || (in == null) || (expectedClass == null)){
			Logger.error(CALLER, "unmarshalFromXml_InitError", 
					     "Some required initialization property is missing",
					     "JAXB_CONTEXT_MISSING", (context == null),
					     "INPUT_STREAM_MISSING", (in == null),
					     "EXPECT_CLASS_MISSING", (expectedClass == null));
			throw new JAXBException(CALLER + "_unmarshalFromXml_InitError");
		}		
		Unmarshaller xmlUnmarshaller = context.createUnmarshaller();
		return expectedClass.cast(xmlUnmarshaller.unmarshal(in));
	}
	
	public void marshal(JSONJAXBContext context, String mediaType, Object jaxbElement, OutputStream out) throws JAXBException{
		if((context == null) || (mediaType == null) || ("".equals(mediaType)) || (jaxbElement == null) || (out == null)){
			Logger.error(CALLER, "marshal_InitError", 
					     "Some required initialization property is missing",
					     "JAXB_CONTEXT_MISSING", (context == null),
					     "MEDIA_TYPE", mediaType,
					     "INPUT_DATA_MISSING", (jaxbElement == null),
					     "OUTPUT_STREAM_MISSING", (out == null));
			throw new JAXBException(CALLER + "_marshal_InitError");
		}
		
		if(MediaType.APPLICATION_XML.equals(mediaType)){
		     marshalToXml(context, jaxbElement, out);
		}
		else{
		     marshalToJSON(context, jaxbElement, out);
		}
	}
	
	public void marshalToJSON(JSONJAXBContext context, Object jaxbElement, OutputStream out) throws JAXBException{
		if((context == null) || (jaxbElement == null) || (out == null)){
			Logger.error(CALLER, "marshalToJSON_InitError", 
					     "Some required initialization property is missing",
					     "JAXB_CONTEXT_MISSING", (context == null),
					     "INPUT_DATA_MISSING", (jaxbElement == null),
					     "OUTPUT_STREAM_MISSING", (out == null));
			throw new JAXBException(CALLER + "_marshalToJSON_InitError");
		}
		
		JSONMarshaller jsonMarshaller = context.createJSONMarshaller();
	    jsonMarshaller.marshallToJSON(jaxbElement, out);
	}
	
	
	public void marshalToXml(JSONJAXBContext context, Object jaxbElement, OutputStream out) throws JAXBException{
		if((context == null) || (jaxbElement == null) || (out == null)){
			Logger.error(CALLER, "marshalToXml_InitError", 
					     "Some required initialization property is missing",
					     "JAXB_CONTEXT_MISSING", (context == null),
					     "INPUT_DATA_MISSING", (jaxbElement == null),
					     "OUTPUT_STREAM_MISSING", (out == null));
			throw new JAXBException(CALLER + "_marshalToXml_InitError");
		}
		
		Marshaller xmlMarshaller = context.createMarshaller();
		xmlMarshaller.marshal(jaxbElement, out);
	}

}
