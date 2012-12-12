/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id$
 * $Author$ $DateTime$ $Change$
 */
package provision.util;

/**
 * This is a utility class for obtaining EJB references.
 * @author Chris S.; Mike C.
 * @version 4.0
 */

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.sql.DataSource;

import oracle.jdbc.xa.OracleXAException;
import provision.services.logging.Logger;

/** @noinspection ThrowInsideCatchBlockWhichIgnoresCaughtException*/
final public class EJBUtil
{
   // cache EJBHome references in HasMap
   private static Map homes = new HashMap();
   private static Map prvHomes = new HashMap();
   private static Map ejb30Cache = new HashMap();
	
   private static InitialContext iniCtx = null;
   private static String caller = EJBUtil.class.getName();

    /**
     * because of junit 4.3.1, caching may cause error sometimes
     * call this method to refresh cache
     */
    public static synchronized void refresh() {
        homes = new HashMap();
        prvHomes = new HashMap();
        ejb30Cache = new HashMap();
        iniCtx = null;
    }
    
    public static Object getEjb(String className) throws PSException{
    	getInitialContext();
    	try {
    		return iniCtx.lookup(className);
    	} catch ( NamingException e )
         {
            throw new PSException( CommonExceptionConstants.GENERAL_NAMING_CODE,
                                   "Exception obtaining reference to "+className
                                   +": "+e.getMessage());
         }
    }

   /**
    * This generic method will attempt to look up an EJBHome class.
    * <BR>
    * It will return a PSException if:
    * <UL>
    *    <li> The InitialContext could not be obtained (E_103)
    *    <li> There was an error looking up the home class (E_103)
    *    <Li> There were no errors, but a reference was not obtained (E_113)
    * </UL>
    *
    * @param homeClass
    * @return
    * @throws PSException
    */
   public static EJBHome getLocalHome( Class homeClass ) throws PSException
   {
      EJBHome home = (EJBHome) homes.get( homeClass );
      if ( home == null )
      {
         getInitialContext();
         Object objref = null;
         try
         {
            objref = iniCtx.lookup( homeClass.getName() );
         } catch ( NamingException e )
         {
            throw new PSException( CommonExceptionConstants.GENERAL_NAMING_CODE,
                                   "Exception obtaining reference to "+homeClass.getName()
                                   +": "+e.getMessage());
         }
         if ( objref == null )
         {
            throw new PSException( CommonExceptionConstants.GENERAL_LOOKUP_FAILURE_CODE,
                                   "Unable to obtain requested reference to "+homeClass.getName());
         }
         else
         {
            home = (EJBHome) PortableRemoteObject.narrow( objref, homeClass );
            synchronized(homes){
            	homes.put( homeClass, home );
            }
         }
      }
      return home;
   }

    public static EJBLocalHome getEJBLocalHome( Class localHomeClass ) throws PSException
    {
        return getEJBLocalHome(localHomeClass, localHomeClass.getName());
    }

   public static EJBLocalHome getEJBLocalHome( Class localHomeClass, String jndiName ) throws PSException
  {
      EJBLocalHome localHome = (EJBLocalHome) homes.get( localHomeClass );
      if ( localHome == null )
      {

         try {
             Object objref = null;
             if ( iniCtx == null )
                iniCtx = new InitialContext();

             objref = iniCtx.lookup(jndiName);
             localHome = (EJBLocalHome) objref;
             synchronized(homes){
            	 homes.put( localHomeClass, localHome );
             }  
             if ( objref == null )
             {
                throw new PSException("GENERAL_LOOKUP_FAILURE_CODE",
                                   "Unable to obtain requested reference to "+jndiName);
             }

         } catch (NamingException e) {

             throw new PSException(CommonExceptionConstants.GENERAL_NAMING_CODE,
                                   "Unable to lookup Local Home interface for "
                                   +jndiName+" [Naming Exception], probably because"
                                    +" the local interface is not in the same deployment as the caller: "+e.getMessage());
         }

      }

      return localHome;
   }


   public static Object getEJB30Bean(Class remoteInterface, String jndiName ) throws PSException
   {
		if (ejb30Cache.containsKey(remoteInterface.getName())) {
			return ejb30Cache.get(remoteInterface.getName());
		}
	   
	   Object bean = null;
	   try {
		    getInitialContext();
			Object obj = iniCtx.lookup(jndiName);
			bean = PortableRemoteObject
					.narrow(obj, remoteInterface);
		} catch (NamingException ne) {
			throw new PSException (CommonExceptionConstants.GENERAL_NAMING_CODE, "Unable to obtain requested reference to " + jndiName);
		}		

		if (bean == null){
			throw new PSException( CommonExceptionConstants.GENERAL_LOOKUP_FAILURE_CODE,
                    "Unable to obtain requested reference to " + jndiName);
		}else {	           
			synchronized(ejb30Cache){
				if (!ejb30Cache.containsKey(remoteInterface.getName())) {
					ejb30Cache.put(remoteInterface.getName(), bean);
				}	
			}	
		}	

		return bean;
   }   
   

   /**
    * This utility method will look up an InitialContext, throwing a PSException if it fails.
    * @throws PSException
    */
   public static InitialContext getInitialContext()
           throws PSException
   {
      if ( iniCtx == null )
      {
         try
         {
            iniCtx = new InitialContext();
         } catch ( NamingException e )
         {
            throw new PSException( CommonExceptionConstants.GENERAL_NAMING_CODE, "Unable to lookup Initial Context: "+e.getMessage());
         }
      }
      
      return iniCtx;
   }

   /**
    * A convenience method which closes a context reference, handling the Exception.
    *
    * @param ctx
    * @throws PSException
    */
   private static void closeContext( Context ctx )
           throws PSException
   {
      if ( ctx != null )
      {
         try
         {
            ctx.close();
         } catch ( NamingException e )
         {
            throw new PSException( CommonExceptionConstants.GENERAL_NAMING_CODE, "Unable to close Initial Context: " + e.getMessage() );
         }
      }
   }
     
   public static Connection getDBConnection( String dsn )
   {
      Connection conn = null;
      try
      {
         if ( iniCtx == null ) try
         {
            getInitialContext();
         } catch ( PSException e )
         {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
         }
         DataSource ds = (DataSource) iniCtx.lookup( dsn );
         try
         {
            conn = ds.getConnection();
         } catch ( SQLException ce )
         {
            Logger.error( caller, "getDBConnection() DSN=" + dsn, "SQLException: " + ce.getMessage() );
            if ((ce.getCause() != null) && (ce.getCause() instanceof OracleXAException))
            {
               OracleXAException xaException = (OracleXAException)(ce.getCause());
               Logger.error( caller, "getDBConnection() DSN=" + dsn, "OracleXAException: " + getOracleXAExceptionDetails(xaException));
            }
            throw new GeneralFailureException( "Failure to obtain DB Connection",ce );
         }catch ( NullPointerException npe )
         {
            throw new GeneralFailureException( "Datasource lookup returned null",npe );
         }
         return conn;
      } catch ( NamingException ne )
      {
         Logger.error( caller, "getDBConnection() DSN=" + dsn, "NamingException: " + ne.getMessage() );
         throw new GeneralFailureException( ne );
      }
   }

   public static String getOracleXAExceptionDetails(OracleXAException xaException)
   {
       StringBuffer details = new StringBuffer();
       details.append("XAError = ").append(xaException.getXAError());
       details.append("; XAErrorMessage = ").append(OracleXAException.getXAErrorMessage(xaException.getXAError()));
       details.append("; OracleError = ").append(xaException.getOracleError());
       details.append("; OracleSQLError = ").append(xaException.getOracleSQLError());
       return details.toString();
   }

   public static Connection getDBConnectionWithURL( String dsn, String containerURL )
   {
      Connection conn = null;
      try
      {
         Context c = getContextFromURL( containerURL );
         DataSource ds = (DataSource) c.lookup( dsn );
         try
         {
            conn = ds.getConnection();
         } catch ( SQLException se )
         {
            throw new GeneralFailureException( se );
         }
         return conn;
      } catch ( NamingException ne )
      {
         throw new GeneralFailureException( ne );
      }
   }

   private static Context getContextFromURL( String containerURL )
           throws NamingException
   {
      Properties p = new Properties();
      p.put( Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory" );
      p.put( Context.PROVIDER_URL, containerURL );
      Context c = new InitialContext( p );
      return c;
   }

   public static String getEnvProperty( String prop )
   {
      try
      {
         if ( iniCtx == null )
            try
            {
               getInitialContext();
            } catch ( PSException e )
            {
               e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
         Context env = (Context) iniCtx.lookup( "java:comp/env" );
         return (String) env.lookup( prop );
      } catch ( NamingException ne )
      {
         throw new GeneralFailureException( ne );
      }
   }


   public static String formatSQLString( String s )
   {
      String newStr = "";
      for ( int i = 0; i < s.length(); i++ )
      {
         if ( s.charAt( i ) == '\'' )
         {
            newStr = newStr + "''";
         } else
         {
            newStr = newStr + s.charAt( i );
         }
      }
      return newStr;
   }

   private static class HomeKey
   {
      public Class homeClass;
      public int prvInstance;

      public HomeKey()
      {
      }

      public HomeKey( Class homeClass, int prvInstance )
      {
         this.homeClass = homeClass;
         this.prvInstance = prvInstance;
      }

      public boolean equals( Object o )
      {
         if ( o instanceof HomeKey )
         {
            HomeKey hk = (HomeKey) o;
            return homeClass.equals( hk.homeClass ) && prvInstance == hk.prvInstance;
         }
         return false;
      }
   }
   
	public static Object getObjectFromJNDI(String jndiName) throws PSException {

		Object objRef = null;
		
		try {
			getInitialContext();
			objRef = iniCtx.lookup(jndiName);

		} 
		
		catch ( NamingException e )
        {
           throw new PSException( CommonExceptionConstants.GENERAL_NAMING_CODE,
                                  "Exception obtaining reference : "+jndiName
                                  +": "+e.getMessage());
        }
        if ( objRef == null )
        {
           throw new PSException( CommonExceptionConstants.GENERAL_LOOKUP_FAILURE_CODE,
                                  "Unable to obtain requested reference: "+jndiName);
        }

		return objRef;

	}
	
}
