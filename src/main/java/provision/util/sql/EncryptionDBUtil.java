/*
 * (C) 2011 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id$
 * $DateTime$ $Author$ $Change$
 */
package provision.util.sql;

import java.sql.SQLException;
import java.sql.Types;

import provision.services.logging.Logger;
import provision.util.CommonExceptionConstants;
import provision.util.PSException;

/**
 * @author tramanathan
 *
 */
public class EncryptionDBUtil {
    private static final String CALLER = EncryptionDBUtil.class.getName();
    private static final String EN_DE_CRYPTION_PACKAGE_NAME = "prv_encryption_pkg";
    private static final int timeout = 5; 

    /**
     * @param encryptedMsg
     * @param key
     * @param dataSource
     * @return
     * @throws PSException
     */
    public static String decryptMsg(String encryptedMsg, String key, String dataSource) throws PSException {
    	
        DBCommand dbcmd   = new DBCommand();
		String decryptedMsg = null;
		
        try {
            dbcmd.addParameter(Types.VARCHAR, encryptedMsg);
            dbcmd.addParameter(Types.VARCHAR, key);
            decryptedMsg = (String)dbcmd.executeStoredFunction(new StringBuffer(EN_DE_CRYPTION_PACKAGE_NAME+".decrypt_msg"), Types.VARCHAR, dataSource, timeout);
        }
        catch (SQLException sqe){
            Logger.error(CALLER, "decryptMsgWithKeyInSysparm", null, sqe.getMessage(), sqe);
            sqe.printStackTrace();
            throw new PSException( CommonExceptionConstants.GENERAL_SQL_CODE);
        }
        finally {
            dbcmd.close();
        }
        return decryptedMsg;
    }
	
	
    /**
     * @param encryptedMsg
     * @param sysParm
     * @param dataSource
     * @return
     * @throws PSException
     */
    public static String decryptMsgWithKeyInSysparm(String encryptedMsg, String sysParm, String dataSource) throws PSException {    	
		String decryptedMsg = null;
        DBCommand dbcmd   = new DBCommand();
		
        try {
            dbcmd.addParameter(Types.VARCHAR, encryptedMsg);
            dbcmd.addParameter(Types.VARCHAR, sysParm);
            decryptedMsg = (String)dbcmd.executeStoredFunction(new StringBuffer(EN_DE_CRYPTION_PACKAGE_NAME+".decrypt_msg_with_sysparm_key"), Types.VARCHAR, dataSource, timeout);
        }
        catch (SQLException sqe){
            Logger.error(CALLER, "decryptMsgWithKeyInSysparm", null, sqe.getMessage(), sqe);
            sqe.printStackTrace();
            throw new PSException( CommonExceptionConstants.GENERAL_SQL_CODE);
        }
        finally {
            dbcmd.close();
        }
        return decryptedMsg;
    }    
    
    

}
