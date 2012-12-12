/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/util/v1.5/src/main/java/provision/services/logging/DefaultServerNameConverter.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/04/02 13:17:52 $ $Change: 5132706 $
 */
package provision.services.logging;

import java.lang.management.ManagementFactory;


import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * PID converter, using JMX Runtime information
 * 
 */
public class DefaultPIDConverter extends PatternConverter {

	/**
	 * PID
	 */
	
	private String PID = null;
	
	/**
	 * Retrieve the local host name.
	 */
	DefaultPIDConverter() {
		String mxName = ManagementFactory.getRuntimeMXBean().getName();
		PID = mxName.substring(0, mxName.indexOf("@"));
	}

	/**
	 * 
	 * 
	 * @param event - not used
	 * @return
	 */
	@Override
	protected String convert(LoggingEvent event) {
		return PID;
	}
}