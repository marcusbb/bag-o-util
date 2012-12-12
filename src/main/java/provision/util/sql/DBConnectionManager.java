package provision.util.sql;

import javax.sql.DataSource;
import java.sql.Connection;
import javax.naming.InitialContext;
import javax.naming.Context;
import javax.naming.NamingException;
import java.sql.SQLException;
import javax.ejb.EJBException;
import java.util.*;
import oracle.jdbc.pool.OracleDataSource;

import provision.util.EJBUtil;
import provision.services.logging.Logger;
import provision.services.logging.LogEvent;
import provision.services.logging.PSTokenDictionary;
/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class DBConnectionManager {


  private final static String DB_URL    = "jdbc:oracle:thin:@ballantines:1521:ps1";
  private final static String DB_ID     = "amanuel";
  private final static String DB_PASSWD = "password";
  private final static String DB_DRIVER = "oracle.jdbc.driver.OracleDriver";
  private String _containerURL = null;

  private static String caller = DBConnectionManager.class.getName();


  public DBConnectionManager() {
  }

  /** This constructor is used by clients that need database access via DAO objects
   * from clients outside container (ie not beans).
   */
  public DBConnectionManager(String containerURL) {
      _containerURL = containerURL;
  }

  /** this connection is just use for testing
   *  do not use it
   */
  public Connection getTestConnectionPool() throws SQLException {

      InitialContext initCtx = null;

       // Put connection properties in to a hashtable. for remote
       Hashtable ht = new Hashtable();
       ht.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
       ht.put(Context.PROVIDER_URL,"t3://localhost:7001");


	try {
          initCtx = new InitialContext(ht);
          //initCtx = new InitialContext();
	  //DataSource ds = (javax.sql.DataSource)initCtx.lookup("ora817pool");
          DataSource ds = (javax.sql.DataSource)initCtx.lookup("athomedbpool");

          //DataSource ds = (javax.sql.DataSource)initCtx.lookup("athomedbpool");
	  return ds.getConnection();
	} catch(NamingException ne) {
		Logger.debug(caller, "Unable to get connection", ne);
	    //throw new EJBException(ne);
        return null;
	} finally {
	   try {
	      if(initCtx != null) initCtx.close();
	   } catch(NamingException ne) {
		   Logger.debug(caller, "Unable to close the context", ne);
	       throw new EJBException(ne);
	   }
	}

  }


  /**
   * Use this method to get the connection this method support multiple datasources
   *
   */

  public Connection getConnectionPool(String dsn) {

    // Following use of url will allow java clients to narrow the
    // dsn properly using url of the container.
    if(_containerURL == null) 
        return EJBUtil.getDBConnection(dsn);
    else
        return EJBUtil.getDBConnectionWithURL(dsn, _containerURL);

  }

  public synchronized Connection getJDBCConnection() throws SQLException {

      Connection conn = null;

	try {
          loadJDBCDriver();
          OracleDataSource ods = new OracleDataSource();
          ods.setUser(DB_ID);
          ods.setPassword(DB_PASSWD);
          ods.setURL(DB_URL);
          conn = ods.getConnection();
	} catch(SQLException e) {
	    PSTokenDictionary logTokens = new PSTokenDictionary();
		logTokens.put("DB_URL", DB_URL);
	    logTokens.put("USER_ID", DB_ID);
		logTokens.put("DB_DRIVER", DB_DRIVER);
	    Logger.error(caller, LogEvent.DBCONNECTION_CONNECT_TO_DB, logTokens, e.getMessage(), null);
	} catch (ClassNotFoundException e ) {
	    PSTokenDictionary logTokens = new PSTokenDictionary();
		logTokens.put("DB_URL", DB_URL);
	    logTokens.put("USER_ID", DB_ID);
		logTokens.put("DB_DRIVER", DB_DRIVER);
	    Logger.error(caller, LogEvent.DBCONNECTION_CONNECT_TO_DB, logTokens, e.getMessage(), null);
    }

       return conn;

  }

  private void loadJDBCDriver() throws ClassNotFoundException {

     Class.forName(DB_DRIVER);

  }


}
