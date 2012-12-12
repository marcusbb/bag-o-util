/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/logging/DefaultProcessNameConverter.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.logging;

import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Converts the custom conversion character {@link PSPatternLayout#PROCESS_CONVERSION} in order to
 * add the process name to the formatted message. The process name is not the real name of the java
 * process but rather the name of the deployable artifact (application) that contains the code
 * logging the event.
 * 
 * @author Iulian Vlasov
 * @since TurboPrv
 */
public class DefaultProcessNameConverter extends PatternConverter {

	/**
	 * @param event - used to get the local MDC
	 * @return The thread name
	 */
	@Override
	protected String convert(LoggingEvent event) {
		String processName = "Weblogic";

		Object thname = event.getMDC(Logger.CALLER_CLASS);
		if (thname == null) {
			String thrname = Thread.currentThread().getName();
			if (thrname.startsWith("MailReceiver")) {
				processName = "MailReceiver";
			}
		}
		else {
			if (thname instanceof Class) {
				processName = LogConfigurator.getContextName(thname);
			}
			else {
				processName = thname.toString();
			}
		}

		return processName;
	}
}