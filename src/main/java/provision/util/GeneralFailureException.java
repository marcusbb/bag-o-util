/*
 * (C) 2001 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 */

package provision.util;

/**
 * This exception is thrown for all problems that occur in the EJB Container code
 * of the provision application. This class defines a few typical error types
 * to help the developer find the error cause.
 * @version 1.0.1
 */
import provision.services.logging.Logger;


public class GeneralFailureException extends RuntimeException implements java.io.Serializable {
	private static final String CALLER = GeneralFailureException.class.getName();
    private String code;

    public GeneralFailureException(String code) {
		  super();
		  this.code = code;
    }

    public GeneralFailureException(String code, String msg) {
		super(msg);
        this.code = code;
    }

	public String getCode() {
		return code;
    }

    public GeneralFailureException(Exception e) {
            super(CommonExceptionConstants.getCode(e));
            Logger.debug(CALLER, "GeneralFailureException", e.getMessage());            
    }

   public GeneralFailureException(String msg, Exception e) {
            super(CommonExceptionConstants.getCode(e)+": "+msg);
            this.code = CommonExceptionConstants.getCode(e);
            Logger.debug(CALLER, "GeneralFailureException", e.getMessage());
    }
}
