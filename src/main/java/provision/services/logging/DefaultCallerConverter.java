/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/logging/DefaultCallerConverter.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.logging;

import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Converts the custom conversion character {@link PSPatternLayout#CALLER_CONVERSION} in order to
 * add the caller information to the formatted message.
 * 
 * @author Iulian Vlasov
 * @since TurboPrv
 */
public class DefaultCallerConverter extends PatternConverter {

	/**
	 * Returns caller info as [class-name]:[method-name]:[line-number]
	 * 
	 * @param event - used to get the local MDC
	 * @return The caller information.
	 */
	@Override
	protected String convert(LoggingEvent event) {
		String callerInfo = (String) event.getMDC(Logger.CALLER_LOCATION);
		if (callerInfo == null) {
			LocationInfo li = event.getLocationInformation();
			callerInfo = new StringBuilder(li.getClassName()).append(':').append(li.getMethodName()).append(':')
					.append(li.getLineNumber()).toString();
		}

		return callerInfo;
	}
}