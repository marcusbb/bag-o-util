/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/logging/CustomConverterSupport.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.logging;

/**
 * Pattern parsers implementing this interface will use a {@link ConverterFactory} to obtain pattern
 * converters associated with conversion characters defined within a conversion pattern string.
 * 
 * @author Iulian Vlasov
 * @since TurboPrv
 */
public interface CustomConverterSupport {

	/**
	 * Inject the factory that creates pattern converters.
	 * 
	 * @param cf
	 */
	void setConverterFactory(ConverterFactory cf);
}
