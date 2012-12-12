/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/logging/ConfigListener.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.logging;

import java.util.Properties;

/**
 * Implement this interface if you want to do any post processing after the logging framework has
 * been configured. this would be useful when the configuration file is monitored and you want to
 * react to changes in the logging parameters.
 * 
 * @author Iulian Vlasov
 * @since TurboPrv
 */
public interface ConfigListener {

	/**
	 * If the active log configuration file is a properties file, the method is invoked with the
	 * Properties object as it was loaded and enhanced by the Log4J framework.
	 * <p>
	 * If the active log configuration file is an XML file, the Properties object is
	 * <code>null</code> since all Log4J advanced elements can be specified in the file itself -
	 * such as asynchronous logging.
	 * 
	 * @param p
	 */
	void postConfig(Properties p);

}