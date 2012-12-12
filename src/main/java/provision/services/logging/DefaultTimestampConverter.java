/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/logging/DefaultTimestampConverter.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.logging;

import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Converts the custom conversion character {@link PSPatternLayout#TIMESTAMP_CONVERSION} in order to
 * add the original call timestamp to the formatted message.
 * 
 * @author Iulian Vlasov
 * @since TurboPrv
 */
public class DefaultTimestampConverter extends PatternConverter {

	/**
	 * 
	 * @param event - used to get the local MDC
	 * @return The original time stamp.
	 */
	@Override
	protected String convert(LoggingEvent event) {
		Object ts = event.getMDC(Logger.CALLER_TIMESTAMP);
		if (ts == null) {
			ts = System.currentTimeMillis();
		}

		String timestamp = ts.toString();
		return timestamp;
	}
}