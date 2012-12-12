/*
 * (C) 2006 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 */
package provision.util.exception;

import provision.util.CommonExceptionConstants;
import provision.util.PSException;


/**
 * @author Piotr Madej
 *
 * Exception class to be thrown when a concurrent update error is detected
 */
public class ConcurrentUpdateException extends PSException {


    /**
     * constructor
     * @param msg - debug message
     */
    public ConcurrentUpdateException (String msg) {
        super(CommonExceptionConstants.CONCURRENT_UPDATE_ERROR_CODE, msg);
    }

    /**
     * constructor
     * @param code - error code
     * @param msg - debug message
     */
    public ConcurrentUpdateException (String code, String msg) {
        super(code, msg);
    }

    /**
     * default constructor
     */
    public ConcurrentUpdateException () {
        super(CommonExceptionConstants.CONCURRENT_UPDATE_ERROR_CODE);
    }
}
