package provision.util;/*
 * (C) 2001 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 */

import java.rmi.RemoteException;
import javax.naming.NamingException;
import javax.ejb.CreateException;
import javax.ejb.RemoveException;
import javax.ejb.EJBException;
import java.sql.SQLException;
import javax.jms.JMSException;
import javax.ejb.FinderException;
import java.io.*;
import java.net.*;

public class CommonExceptionConstants {
	/* GeneralFailureExceptions */
	public static final String GENERAL_CODE = "E_100";

	public static final String GENERAL_REMOTE_CODE = "E_101";
	public static final String GENERAL_REMOTE_MSG = "EJB Remote Exception";

	public static final String GENERAL_CREATE_CODE = "GENERAL_CREATE_CODE";
	public static final String GENERAL_CREATE_MSG = "EJB CreateException";

	public static final String GENERAL_NAMING_CODE = "E_103";
	public static final String GENERAL_NAMING_MSG = "JNDI NamingException";

	public static final String GENERAL_SQL_CODE = "E_104";
	public static final String GENERAL_SQL_MSG = "DB SQLException";

	public static final String GENERAL_JMS_CODE = "E_105";
	public static final String GENERAL_JMS_MSG = "JMSException";

	public static final String GENERAL_FINDER_CODE = "E_106";
	public static final String GENERAL_FINDER_MSG = "EJB FinderException";

	public static final String GENERAL_IO_CODE = "E_107";
	public static final String GENERAL_IO_MSG = "IOException";

	public static final String GENERAL_CONNECT_CODE = "E_108";
	public static final String GENERAL_CONNECT_MSG = "ConnectException";

	public static final String GENERAL_NO_ROUTE_TO_HOST_CODE = "E_109";
	public static final String GENERAL_NO_ROUTE_TO_HOST_MSG = "NoRouteToHostException";

	public static final String GENERAL_UNKNOWN_HOST_CODE = "E_110";
	public static final String GENERAL_UNKNOWN_HOST_MSG = "UnknownHostException";

	public static final String GENERAL_SOCKET_CODE = "E_111";
	public static final String GENERAL_SOCKET_MSG = "SocketException";

	public static final String GENERAL_REMOVE_CODE = "E_112";
	public static final String GENERAL_REMOVE_MSG = "EJB RemoveException";

	public static final String GENERAL_LOOKUP_FAILURE_CODE = "E_113";
	public static final String GENERAL_LOOKUP_FAILURE_MSG = "JNDI Lookup Failure";

	public static final String GENERAL_DATACONNECTION_FAILURE_CODE = "E_114";
	public static final String GENERAL_DATACONECTION_FAILURE_MSG = "Database Connection Lookup Failure";

    public static final String CONCURRENT_UPDATE_ERROR_CODE = "I_327";
    public static final String CONCURRENT_UPDATE_ERROR_MSG = "Concurrent update exception";

	public static String getCode(Exception e) {
		String code = GENERAL_CODE;
		if (e instanceof NamingException) {
			code = GENERAL_NAMING_CODE;
		} else if (e instanceof SQLException) {
			code = GENERAL_SQL_CODE;
		} else if (e instanceof CreateException) {
			code = GENERAL_CREATE_CODE;
		} else if (e instanceof RemoteException) {
			code = GENERAL_REMOTE_CODE;
		} else if (e instanceof JMSException) {
			code = GENERAL_JMS_CODE;
		} else if (e instanceof FinderException) {
			code = GENERAL_FINDER_CODE;
		} else if (e instanceof IOException) {
			code = GENERAL_IO_CODE;
		} else if (e instanceof ConnectException) {
			code = GENERAL_CONNECT_CODE;
		} else if (e instanceof NoRouteToHostException) {
			code = GENERAL_NO_ROUTE_TO_HOST_CODE;
		} else if (e instanceof UnknownHostException) {
			code = GENERAL_UNKNOWN_HOST_CODE;
		} else if (e instanceof SocketException) {
			code = GENERAL_SOCKET_CODE;
		} else if (e instanceof RemoveException) {
			code = GENERAL_REMOVE_CODE;
		} else if (e instanceof PSException) {
			code = ((PSException) e).getCode();
		}
		return code;
	}

}
