/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/logging/DefaultServerNameConverter.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.logging;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Converts the custom conversion character {@link PSPatternLayout#SERVER_CONVERSION} in order to
 * add the server host name to the formatted message.
 * 
 * @author Iulian Vlasov
 * @since TurboPrv
 */
public class DefaultServerNameConverter extends PatternConverter {

	/**
	 * The server InetAddress name.
	 */
	private String hostName = null;

	/**
	 * Retrieve the local host name.
	 */
	DefaultServerNameConverter() {
		super();

		try {
			InetAddress i = InetAddress.getLocalHost();
			hostName = i.getHostName();
		}
		catch (UnknownHostException uhe) {
			hostName = "NA: Localhost";
		}
	}

	/**
	 * Returns the server's host name.
	 * 
	 * @param event - not used
	 * @return
	 */
	@Override
	protected String convert(LoggingEvent event) {
		return hostName;
	}
}