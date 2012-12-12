/**
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id$
 * $DateTime$
 * $Author$
 * $Change$
 */


package provision.util.sql;

import provision.services.logging.Logger;
import provision.util.PSException;

import java.sql.*;

public class DBUtil{

    private final static String CALLER = "DBUtil";

    /**
     * Close all JDBC resource provided and throw the PSException upon receiving SQLException.
     * This method is written for backward compatibility with previous code to ensure that those methods
     * which requires PSException to be thrown upon receiving SQLException when closing DB resource gets
     * PSException thrown.
     */
    public static void closeResourceAndThrowExceptionOnError(ResultSet rs, Statement statement, Connection conn, String caller) throws PSException{
        try{
            closeResourceAndThrowExceptionOnError(rs, caller);
        }
        finally{
            try{
                closeResourceAndThrowExceptionOnError(statement, caller);
            }
            finally{
                closeResourceAndThrowExceptionOnError(conn, caller);
            }
        }
    }

    public static void closeResource(ResultSet resultSet, Statement statement, Connection connection, String caller){
        closeResource(resultSet, caller);
        closeResource(statement, caller);
        closeResource(connection, caller);
    }


    /**
     * differ from the other one because it is throwing PSException,
     * only used for those method that needs PSException to be thrown
     * upon SQLException
     */
    public static void closeResourceAndThrowExceptionOnError(Object objResource, String caller) throws PSException{
        try{
            close(objResource, caller);
        }
        catch(SQLException se){
            throw new PSException(caller + "_SQLException_while_closing_db_resource", se.getErrorCode() + "_" + se.getMessage());
        }
    }
    
    /**
     * this method is used for those that only need SQLException to be logged if thrown
     */
    public static void closeResource(Object resource, String caller){
        try{
            close(resource, caller);
        }
        catch(SQLException e){
             // no longer needed to handle this since it's already been logged in close()
        }
    }

    private static String close(Object objResource, String caller) throws SQLException{
        String resourceType = "";

        if(objResource == null){
            return resourceType;
        }

        if(caller == null){
            caller = "";
        }

        try{
            if(objResource instanceof ResultSet){
                resourceType = "ResultSet";
                ResultSet resultSet = (ResultSet)objResource;
                resultSet.close();
            }
            else if(objResource instanceof Statement){
                resourceType = "Statement";
                Statement statement = (Statement)objResource;
                statement.close();
            }
            else if(objResource instanceof Connection){
                resourceType = "Connection";
                Connection connection = (Connection)objResource;
                connection.close();
            }
            else{
                Logger.warn(CALLER, "close", "Unrecognize JDBC resource type: " + objResource.getClass().getName());
            }
        }
        catch(SQLException se){
            Logger.error(CALLER, "close", caller + "_SQLException_while_closing_" + resourceType, se);
            // throw it back in case caller wants to convert it into another kind of exception as seen in current code
            throw se;
        }

        return resourceType;
    }
}
