/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/logging/ConverterFactory.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.logging;

import org.apache.log4j.helpers.PatternConverter;

/**
 * A factory of converters creates and initializes {@link PatternConverter} associated with the
 * specified conversion characters.
 * <p>
 * The factory is configured via Log4J configuration files.
 * 
 * @author Iulian Vlasov
 * @since TurboPrv
 */
public interface ConverterFactory {

	/**
	 * Return the PatternConverter handling the conversion or null if no converter is configured for
	 * the specified conversion character.
	 * 
	 * @param conversionChar
	 * @return
	 */
	PatternConverter getConverter(char conversionChar);
}
