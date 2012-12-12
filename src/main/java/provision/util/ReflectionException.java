/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/util/ReflectionException.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.util;

/**
 * @author Iulian Vlasov
 * @since PRV-5.0.3
 */
@SuppressWarnings("serial")
public class ReflectionException extends BaseException {

    /**
     * @param evt
     * @param args
     */
    public ReflectionException(BaseEvent evt, Object... args) {
        super(evt, args);
    }

    /**
     * @param evt
     * @param t
     * @param args
     */
    public ReflectionException(BaseEvent evt, Throwable t, Object... args) {
        super(evt, t, args);
    }

}