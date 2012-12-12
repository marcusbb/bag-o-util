/*
 * (C) 2010 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/util/BaseEvent.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.util;

/**
 * An event is described by:
 * <p>
 * <ul>
 * <li>an name/identifier - used by engineering through logging</li>
 * <li>a code - used by end-users through GUI
 * <li>a message used by development through logging.
 * </ul>
 * 
 * @author Iulian Vlasov
 * @since BA-Plus
 */
public interface BaseEvent {

    String getId();

    String getCode();

    String getMessage();

}
