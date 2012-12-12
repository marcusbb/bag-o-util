package provision.util.sql;

import java.sql.*;
import java.util.*;

import java.sql.Types;

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

public class DBCommand extends DBConnectionManager {

    private ResultSet res = null;
    private Statement stmt= null;
    private PreparedStatement pstmt;
    private CallableStatement cstmt = null;

    private Connection conn;

    private Vector vParameters;
    private StringBuffer sqlStatement;

    private static String caller = DBCommand.class.getName();

    //Static Variables
    public final static int TEXT = 1;
    public final static int STORED_PROC = 1;
    public final static int STORED_FUNC = 2;
    public final static int RETURN_TYPE_STRING    = Types.VARCHAR;
    public final static int RETURN_TYPE_INTEGER   = Types.INTEGER;

    //public variables;
    public int CommandType;
    public String CommandText;

    public DBCommand() {
        vParameters = new Vector();
    }

    /** This constructor is used by clients that need database access via DAO objects
     * from clients outside container (ie not beans).
     */
    public DBCommand(String containerURL) {
        super(containerURL);
        vParameters = new Vector();
    }

    /** This do a select on the table specified
     * @param sqlStmt an String
     * @return ResultSet
     */

    public ResultSet executeSQLSelect(String sqlStmt, String dsn) throws SQLException {
        if (conn == null || conn.isClosed())
            conn = getConnectionPool(dsn);
        closeStatement(stmt);
        stmt = conn.createStatement();
        res  = stmt.executeQuery(sqlStmt);
        return res;
    }

    /** This do a select on the table specified. Only to be used on JDBC 2.0 and above drivers.
     * @param sqlStmt an String
     * @return ResultSet
     */

    public ResultSet executeSQLSelect(String sqlStmt, String dsn, int resultSetType) throws SQLException {
        if (conn == null || conn.isClosed())
            conn = getConnectionPool(dsn);

        closeStatement(stmt);
        stmt = conn.createStatement(resultSetType, ResultSet.CONCUR_READ_ONLY);
        res  = stmt.executeQuery(sqlStmt);
        return res;
    }

    /** This method will do a select on the table, specified with a parameter ?...?.?
     * example: SQL 'select * from accounts where login_id = ?'
     * @param sqlStmt an String
     * @return ResultSet
     */

    public ResultSet executeSQLSelectWithParameter(String sqlStmt, String dsn) throws SQLException {
        //check if parameters is empty or null
        //since we init vParameters in the constructor then we do not test for nullability
        //instead we have to check of there are elements on the vector.

        if (vParameters.size() == 0) {
            Logger.warn(caller, LogEvent.DBCOMMAND_EXECUTE_SQL_SELECT_WITH_PARAM, "No parameters defined for select. Throwing SQLException.");
            throw new SQLException("SQL Exception no parameters defined.");
        }

        closeStatement(pstmt);
        setSQLStatementParameters(sqlStmt, dsn, false);
        res = pstmt.executeQuery();
        return res;
    }

    public boolean executeSQLUpdate(String sqlStmt, String dsn) {

        boolean methodRetValue = true;

        try{
            if ((conn == null) || (conn.isClosed())){
                conn = getConnectionPool(dsn);
            }
            closeStatement(stmt);
            stmt = conn.createStatement();
            stmt.executeUpdate(sqlStmt);
        }
        catch (SQLException e) {
            PSTokenDictionary logTokens = new PSTokenDictionary();
            logTokens.put("DSN", dsn);
            Logger.error(caller, LogEvent.DBCOMMAND_EXECUTE_SQL_UPDATE, logTokens, e.getMessage(), null);
            Logger.debug(caller, "Sql Statement: " + sqlStmt, e);
            methodRetValue = false;
        }
        finally{
            closeStatement(stmt);
            stmt = null;
        }

        return methodRetValue;
    }

    public int executeSQLUpdateWithRowCount(String sqlStmt, String dsn) throws SQLException
    {

        try {
            if (conn == null || conn.isClosed())
                conn = getConnectionPool(dsn);
            closeStatement(stmt);
            stmt = conn.createStatement();
            return stmt.executeUpdate(sqlStmt);
        } catch (SQLException e) {
            PSTokenDictionary logTokens = new PSTokenDictionary();
            logTokens.put("DSN", dsn);
            Logger.error(caller, LogEvent.DBCOMMAND_EXECUTE_SQL_UPDATE, logTokens, e.getMessage(), null);
            Logger.debug(caller, "Sql Statement: " + sqlStmt, e);
            throw e;
        }
        finally
        {
            closeStatement(stmt);
            stmt = null;
        }
    }

    public boolean executeSQLUpdateWithParameter(String sqlStmt, String dsn) throws SQLException {

        if (vParameters.size() == 0) {
            Logger.warn(caller, LogEvent.DBCOMMAND_EXECUTE_SQL_UPDATE_WITH_PARAM, "No parameters defined for update. Throwing SQLException.");
            throw new SQLException("SQL Exception no parameters defined.");
        }

        closeStatement(pstmt);
        try
        {
            setSQLStatementParameters(sqlStmt, dsn, true);
            pstmt.executeUpdate();
        }
        finally
        {
            closeStatement(pstmt);
            pstmt = null;
        }

        return true;
    }

    /**
     * This will initilize all the parameter
     * @param sqlStmt as String
     */
    private void setSQLStatementParameters(String sqlStmt, String dsn, boolean upd) throws SQLException {

        int size;
        int index = 0;

        try {
            if (conn == null || conn.isClosed())
                conn = getConnectionPool(dsn);
            if(upd)
                pstmt = conn.prepareStatement(sqlStmt);
            else
                pstmt = conn.prepareStatement(sqlStmt, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

            size = vParameters.size();
            for (int i = 0; i < size; i++) {
                SQLParameter param = (SQLParameter)vParameters.elementAt(i);
                ++index;
                switch (param.getParamType()) {
                    case Types.INTEGER:
                        pstmt.setInt(index, param.getIntParamValue());
                        break;
                    case Types.VARCHAR:
                        pstmt.setString(index, param.getStringParamValue());
                        break;
                    case Types.FLOAT:
                        pstmt.setFloat(index, param.getFloatParamValue());
                        break;
                    case Types.DOUBLE:
                        pstmt.setDouble(index,param.getDoubleParamValue());
                        break;
                    case Types.BIGINT:
                        pstmt.setLong(index,param.getLongParamValue());
                        break;
                    case Types.TIMESTAMP:
                        Timestamp timestamp = (param.getLongParamValue() > 0) ? new Timestamp(param.getLongParamValue()) : null;
                        pstmt.setTimestamp(index, timestamp);
                        break;
                    case Types.DATE:
                        //pstmt.setDate(index, param.ge);
                    	java.sql.Date date = (param.getLongParamValue() > 0) ? new java.sql.Date(param.getLongParamValue()) : null;
                    	pstmt.setDate(index, date);
                        break;
                }
            }

        } catch (SQLException e) {
            PSTokenDictionary logTokens = new PSTokenDictionary();
            logTokens.put("DSN", dsn);
            Logger.error(caller, LogEvent.DBCOMMAND_SET_SQL_STATEMENT_PARAMETERS, logTokens, e.getMessage(), null);
            Logger.debug(caller, "Sql Statement: " + sqlStmt, e);
            closeStatement(pstmt);
            pstmt = null;
            throw e;
        }
   }

    /**
     * This method executes a stored procedure, timeout must be determined by caller and passed in.
     * @param spName - stored procedure name
     * @param dsn - data source
     * @param timeout - timeout in seconds for stored procedure
     * @return result of storedprocedure
     * @throws SQLException -
     */
    public CallableStatement executeStoredProcedure(String spName, String dsn, int timeout) throws SQLException {

        CommandText = spName;
        CommandType = STORED_PROC;
        setStatementStart();
        setStoredProcedureStatement();
        setStatementEnd();
        Logger.debug(caller, "executeStoredProcedure parameterCount=" + vParameters.size() + "; sqlStatement=" + sqlStatement);

        try {
            //now check if there are any input parameters then properly set all the parameter
            if (conn == null || conn.isClosed()){
                conn = getConnectionPool(dsn);
            }
            closeStatement(cstmt);
            cstmt = conn.prepareCall(new String(sqlStatement));
            cstmt.setQueryTimeout(timeout);
            cstmt = setStoredProcedureParameters(cstmt,0);
            cstmt.execute();
        } catch (SQLException e) {
            PSTokenDictionary logTokens = new PSTokenDictionary();
            logTokens.put("STORED_PROCEDURE", spName);
            logTokens.put("DSN", dsn);
            Logger.error(caller, LogEvent.DBCOMMAND_EXECUTE_STORED_PROC, logTokens, "SQLException: " + e.getMessage(), null);
            closeStatement(cstmt);
            cstmt = null;
            throw e;
        }
        return cstmt;
    }

    /**
     * This method executes a function, timeout must be determined by caller and passed in.
     * @param functionName - function name
     * @param sqlType - returned parameter type
     * @param dsn - datasource
     * @param timeout in seconds for function
     * @return - result of function
     * @throws SQLException -
     */
    public Object executeStoredFunction(StringBuffer functionName, int sqlType, String dsn, int timeout ) throws SQLException {

        Object retObj;
        CommandText = functionName.toString();
        CommandType = STORED_FUNC;
        setStatementStart();
        setStoredProcedureStatement();
        setStatementEnd();

        try {

            //now check if there are parameters then properly set all the parameter
            if (conn == null || conn.isClosed())
                conn = getConnectionPool(dsn);

            closeStatement(cstmt);

            cstmt = conn.prepareCall(new String(sqlStatement));
            cstmt.setQueryTimeout(timeout);
            cstmt.registerOutParameter(1, sqlType);
            cstmt = setStoredProcedureParameters(cstmt);
            cstmt.execute();
            retObj = cstmt.getObject(1);
            if (retObj != null)
                Logger.debug(caller, "executeStoredFunction: The class of " + retObj + " is " + retObj.getClass().getName());

        } catch (SQLException e) {
            PSTokenDictionary logTokens = new PSTokenDictionary();
            logTokens.put("STORED_FUNCTION", functionName.toString());
            logTokens.put("DSN", dsn);
            Logger.error(caller, LogEvent.DBCOMMAND_EXECUTE_STORED_FUNC_WITH_RETURN_OBJECT, logTokens, e.getMessage(),null);
            throw new SQLException(e.getMessage(), e.getSQLState());
        }
        finally
        {
            closeStatement(cstmt);
            cstmt = null;
        }


        return retObj;
    }



    public void close(){
        cleanup();
    }

    private void cleanup() {
//        //close ResultSet Object
//        closeResultSet(res);
//        //close PreparedStatement Object
//        closeStatement(pstmt);
//
//        //close Statement Object
//        closeStatement(stmt);
//
//        //close CallableStatement Object
//        closeStatement(cstmt);

        //finally return connection to the pool
        closeConnection(conn);
        
        this.stmt = null;
        this.pstmt = null;
        this.cstmt = null;
        clearParameters();
    }

    private static void closeResultSet(ResultSet resultSet){
        if(resultSet == null){
            return;
        }

        try{
            resultSet.close();
        }
        catch(Exception e){
            Logger.warn(caller, LogEvent.DBCOMMAND_RELEASE_DB_RESOURCES, null, e.getMessage(), null);            
        }
    }

    private static void closeConnection(Connection conn){
        if(conn == null){
            return;
        }

        try{
            conn.close();
        }
        catch(Exception e){
            Logger.warn(caller, LogEvent.DBCOMMAND_RELEASE_DB_RESOURCES, null, e.getMessage(), null);
        }
    }

    private static void closeStatement(Statement statement){
        try
        {
            if (statement != null) //&& !statement.
            {
                statement.close();
            }
        }
        catch (SQLException se)
        {
            Logger.warn(caller, LogEvent.DBCOMMAND_RELEASE_DB_RESOURCES, null, se.getMessage(), null);
        }
    }

    public void clearParameters() {
        if (vParameters != null)
            vParameters.removeAllElements();
    }

    public void addParameter(int paramType, String paramValue) {
        vParameters.addElement(new SQLParameter(paramType, paramValue));
    }

    public void addParameter(int paramType, int paramValue) {
        vParameters.addElement(new SQLParameter(paramType, paramValue));
    }

    public void addParameter(int paramType, double paramValue) {
        vParameters.addElement(new SQLParameter(paramType, paramValue));
    }

    public void addParameter(int paramType, float paramValue) {
        vParameters.addElement(new SQLParameter(paramType, paramValue));
    }
    public void addParameter(int paramType, long paramValue) {
        vParameters.addElement(new SQLParameter(paramType, paramValue));
    }

    public void addOutputParameter(int paramType, String paramValue) {
        vParameters.addElement(new SQLParameter(paramType, paramValue, SQLParameter.OUT));
    }

    public void addOutputParameter(int paramType, int paramValue) {
        vParameters.addElement(new SQLParameter(paramType, paramValue, SQLParameter.OUT));
    }

    public void addOutputParameter(int paramType, double paramValue) {
        vParameters.addElement(new SQLParameter(paramType, paramValue, SQLParameter.OUT));
    }

    public void addOutputParameter(int paramType, float paramValue) {
        vParameters.addElement(new SQLParameter(paramType, paramValue, SQLParameter.OUT));
    }

    public void addInputOutputParameter(int paramType, String paramValue) {
        vParameters.addElement(new SQLParameter(paramType, paramValue, SQLParameter.IN_OUT));
    }

    public void addInputOutputParameter(int paramType, int paramValue) {
        vParameters.addElement(new SQLParameter(paramType, paramValue, SQLParameter.IN_OUT));
    }

    public void addInputOutputParameter(int paramType, double paramValue) {
        vParameters.addElement(new SQLParameter(paramType, paramValue, SQLParameter.IN_OUT));
    }

    public void addInputOutputParameter(int paramType, float paramValue) {
        vParameters.addElement(new SQLParameter(paramType, paramValue, SQLParameter.IN_OUT));
    }

    public void addParameter(SQLParameter param) {
        vParameters.addElement(param);
    }

    private void setStatementStart(){
        if (CommandType == STORED_FUNC)   //in Oracle stored function required a return value
            sqlStatement = new StringBuffer ("{? = call " + CommandText + " (");
        else  //Stored procedure does not required to return a value
            sqlStatement = new StringBuffer ("{call " + CommandText + " (");

    }

    private void setStatementEnd() {
        sqlStatement.append (")}");
    }

    //this build the sql statement for the stored procedure call
    private void setStoredProcedureStatement() {
        int size = vParameters.size();
        for (int i = 1; i <= size; i++) {
            sqlStatement.append("?");
            if (size != i)
                sqlStatement.append(", ");
        }

    }

    private CallableStatement setStoredProcedureParameters(CallableStatement cstmt) throws SQLException {
        // default is set to one output parameter, if 0 or more then one use the other method signature
        return setStoredProcedureParameters(cstmt, 1);
    }


    private CallableStatement setStoredProcedureParameters(CallableStatement cstmt, int outParameterCount) throws SQLException {

        int index = outParameterCount;  //Start input parameter count after Output parameters
        int size = vParameters.size();

        for (int i = 0; i < size; i++) {
            SQLParameter param = (SQLParameter)vParameters.elementAt(i);
            index++;
            if(param.getInOutType() == SQLParameter.IN || param.getInOutType() == SQLParameter.IN_OUT){
                switch (param.getParamType()) {
                    case Types.INTEGER:
                        cstmt.setInt(index, param.getIntParamValue());
                        break;
                    case Types.BIGINT:
                        cstmt.setLong(index, param.getLongParamValue());
                        break;
                    case Types.VARCHAR:
                        cstmt.setString(index, param.getStringParamValue());
                        break;
                    case Types.FLOAT:
                        cstmt.setFloat(index, param.getFloatParamValue());
                        break;
                    case Types.DOUBLE:
                        cstmt.setDouble(index, param.getDoubleParamValue());
                        break;
                    case Types.DATE:
                        //cstmt.setString(index, param.getStringParamValue());
                        break;
                }
            }
            if(param.getInOutType() != SQLParameter.IN){
                cstmt.registerOutParameter(index, param.getParamType());
            }
        }
        return cstmt;
    }

    /*
      This will get the next id based from the sequence name created from db
    */
    public Integer getSequenceNumber(String sequenceName, String dsn) throws SQLException {

        Integer newId = null;
        Statement stm  = null;
        ResultSet result = null;

        String queryStr = "SELECT " + sequenceName + ".nextval FROM DUAL";
        try {
            if (conn == null || conn.isClosed()){
                conn = getConnectionPool(dsn);
            }

            stm = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            result = stm.executeQuery(queryStr);
            if (result.next()) {
                newId = new Integer(result.getInt("nextval"));
            }
        } catch (SQLException e) {		// ORA-02289; sequence does not exist
            // No Sequence!? Generate a new one, but initalize with the max Id determined:
            if(conn != null && stm != null){
                Logger.debug(caller, "getSequenceNumber: SQLException: " + e.getMessage() + " | Creating Sequence");
                createSequence(sequenceName, conn);

                // try to get again the nextval
                result = stm.executeQuery(queryStr);
                if (result.next()) {
                    newId = new Integer(result.getInt("nextval"));
                }
            }
        } finally {
            closeResultSet(result);
            closeStatement(stm);
            closeConnection(conn);

        }
        return newId;

    } //end getSequenceNumber

    /**
     * Muhammad Ali
     * As part of SCR1058989 - the length of TRXHDR is being changed from 9 to 14. For this specific change, this method is written
     * so that the other could would continue to call old sequence method getting Integer in return and just for TRXHDR
     * we would call this one.
     * @param sequenceName
     * @param dsn
     * @return
     * @throws SQLException
     */
    public Long getSequenceNumberLong(String sequenceName, String dsn) throws SQLException {

        Long newId = null;
        Statement stm  = null;
        ResultSet result = null;

        String queryStr = "SELECT " + sequenceName + ".nextval FROM (SELECT RANK() OVER (ORDER BY lang_id) AS rk FROM Languages) WHERE rk =1";
        try {
            if (conn == null || conn.isClosed()){
                conn = getConnectionPool(dsn);
            }

            stm = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            result = stm.executeQuery(queryStr);
            if (result.next()) {
                newId = new Long(result.getLong("nextval"));
            }
        } catch (SQLException e) {		// ORA-02289; sequence does not exist
            // No Sequence!? Generate a new one, but initalize with the max Id determined:
            if(conn != null && stm != null){
                Logger.debug(caller, "getSequenceNumber: SQLException: " + e.getMessage() + " | Creating Sequence");
                createSequence(sequenceName, conn);

                // try to get again the nextval
                result = stm.executeQuery(queryStr);
                if (result.next()) {
                    newId = new Long(result.getLong("nextval"));
                }
            }
        } finally {
            closeResultSet(result);
            closeStatement(stm);
            closeConnection(conn);

        }
        return newId;

    } //end getSequenceNumber
    
    
    /*
      This will get the next id based from the sequence name created from db
      This method differs from getSequenceNumber() in that (1) it returns a Long instead of an Integer; and (2) it does not close the db connection
    */
    public Long getLongSequenceNumber(String sequenceName, String dsn) throws SQLException {

        Long newId = null;
        Statement stm  = null;
        ResultSet result = null;

        String queryStr = "SELECT " + sequenceName + ".nextval FROM DUAL";
        try {
            if (conn == null || conn.isClosed()){
                conn = getConnectionPool(dsn);
            }

            stm = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            result = stm.executeQuery(queryStr);
            if (result.next()) {
                newId = new Long(result.getLong("nextval"));
            }
        } catch (SQLException e) {		// ORA-02289; sequence does not exist
            // No Sequence!? Generate a new one, but initalize with the max Id determined:
            if(conn != null && stm != null){
                Logger.debug(caller, "getSequenceNumber: SQLException: " + e.getMessage() + " | Creating Sequence");
                createSequence(sequenceName, conn);

                // try to get again the nextval
                result = stm.executeQuery(queryStr);
                if (result.next()) {
                    newId = new Long(result.getInt("nextval"));
                }
            }
        }
        finally{
            closeResultSet(result);
            closeStatement(stm);
        }
        return newId;

    } //end getSequenceNumber

/*
      This will get the next id based from the sequence name created from db
    */
    public Long getSequenceNumber(String sequenceName, String dsn, Connection con) throws SQLException {

        Long newId = null;
        Statement stm  = null;
        ResultSet result = null;

        String queryStr = "SELECT " + sequenceName + ".nextval FROM (SELECT RANK() OVER (ORDER BY lang_id) AS rk FROM Languages) WHERE rk =1";
        try {


            stm = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            result = stm.executeQuery(queryStr);
            if (result.next()) {
                newId = new Long(result.getLong("nextval"));
            }
        } catch (SQLException e) {		// ORA-02289; sequence does not exist
            // No Sequence!? Generate a new one, but initalize with the max Id determined:
            if(stm != null){
                Logger.debug(caller, "getSequenceNumber: SQLException: " + e.getMessage() + " | Creating Sequence");
                createSequence(sequenceName, con);

                // try to get again the nextval
                result = stm.executeQuery(queryStr);
                if (result.next()) {
                    newId = new Long(result.getLong("nextval"));
                }
            }
        } finally {
            closeResultSet(result);
            closeStatement(stm);
        }
        return newId;

    } //end getSequenceNumber


    //this sequence generator is specific to BBID since it will be returning a primitive long data type
    //it can also be use by other component by passing in the sequence name
    public Long getNextKey(String sequenceName, String dsn) throws SQLException{

        Long newKey = null;
        Statement stm  = null;
        ResultSet result = null;

        String queryStr = "SELECT " + sequenceName + ".nextval FROM (SELECT RANK() OVER (ORDER BY lang_id) AS rk FROM Languages) WHERE rk =1";
        try {
            if (conn == null || conn.isClosed())
                conn = getConnectionPool(dsn);

            stm = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            result = stm.executeQuery(queryStr);
            if (result.next()) {
                newKey = new Long(result.getLong("nextval"));
            }
        } catch (SQLException e) {		// ORA-02289; sequence does not exist
            if(conn != null && stm != null){
                // No Sequence!? Generate a new one, but initalize with the max Id determined:
                Logger.debug(caller, "getSequenceNumber: SQLException: " + e.getMessage() + " | Creating Sequence");
                createSequence(sequenceName, conn);

                // try to get again the nextval
                result = stm.executeQuery(queryStr);
                if (result.next()) {
                    newKey = new Long(result.getLong("nextval"));
                }
            }

        } finally {
            closeResultSet(result);
            closeStatement(stm);
            closeConnection(conn);
        }
        return newKey;
    }

    //this will format a given a array string into SQL format
    //example -- select * from table where column  IN (1,2,3,4)
    public static String fomatStringIntoInClause(String[] str) {

        String outputStr = "";
        for (int i=0; i < str.length; i++) {
            outputStr = outputStr.concat(",").concat(str[i]);
        }

        //Clean up -- remove leading comma
        outputStr = (outputStr.startsWith(",")) ? outputStr.substring(1) : outputStr;

        //add close and open paranthesis
        if (outputStr.length() > 0)
            outputStr = "(".concat(outputStr).concat(")");

        return outputStr;

    } //fomatStringIntoInClause
    private String getMaxSqlSelectForSequence(String sequenceName){
        String seqSql = null;

        if(sequenceName.equals(SequenceNumbers.BBID_KEY)) seqSql = "select max(bbid_key) from BBIDS";
        else if(sequenceName.equals(SequenceNumbers.SEQPROTRXHDR)) seqSql = "select max(trx_hdr) from ProTrxHeader";
        else if(sequenceName.equals(SequenceNumbers.PARTNER_ID)) seqSql = "select max(partner_id) from Partners";
        else if(sequenceName.equals(SequenceNumbers.SEQVAR_ID)) seqSql = "select max(var_id) from VARs";
        else if(sequenceName.equals(SequenceNumbers.SR_ID)) seqSql = "select max(sr_id) from Sr_Contents";
        else if(sequenceName.equals(SequenceNumbers.PROREQUESTOR_MAP_ID)) seqSql = "select max(map_id) from Prorequestor_map";
        else if(sequenceName.equals(SequenceNumbers.CPS_RETRY_ID)) seqSql = "select max(retry_id) from CPSConnector_Retries";
        else if(sequenceName.equals(SequenceNumbers.SEQ_FAULTY_REG_LOG_ID)) seqSql = "select max(log_id) from Faulty_Registration_Log";

        return seqSql;
    }

    // assuming caller will close the connection
    private void createSequence(String sequenceName, Connection connection) throws SQLException{

        if(connection == null || connection.isClosed()){
            throw new SQLException("Connection Not Initialized");
        }

        String seqSql = getMaxSqlSelectForSequence(sequenceName);
        int iniseq = 1;
        Statement stm = null;
        ResultSet rs = null;
        try{
            connection.clearWarnings();
            stm = connection.createStatement();
            if(stm.execute(seqSql))
            {
                rs = stm.getResultSet();
                if (rs.next()){
                    iniseq = rs.getInt(1) + 1;
                }
              }
        }
        finally{
            closeResultSet(rs);
            closeStatement(stm);
        }

        try{
            Logger.debug(caller, "createSequence: Generating new "+ sequenceName + " starting from " + iniseq);

            seqSql = "declare pragma autonomous_transaction; begin execute immediate 'CREATE SEQUENCE " + sequenceName + " INCREMENT BY 1 START WITH " + iniseq + " MAXVALUE 1.0E28 MINVALUE 1 CACHE 20 NOCYCLE  ORDER'; end;";

            stm = connection.createStatement();
            stm.executeUpdate(seqSql);

        }
        finally {
            closeStatement(stm);
        }
    }

    /**
     * Retrieve the current timestamp from the database rather than
     * from the current application server to avoid time conflicting
     * @return
     */
    public Timestamp getTimestamp(String dsn){
        Timestamp curTimestamp = null;
        PSTokenDictionary logToken = new PSTokenDictionary();
        PreparedStatement psmt = null;
        ResultSet result = null;
        try{
            final String query = "SELECT sysdate FROM dual";
            logToken.put("QUERY", query);
            if (conn == null || conn.isClosed()){
                conn = getConnectionPool(dsn);
            }
            psmt = conn.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            result = psmt.executeQuery();
            if(result.next()){
                curTimestamp = result.getTimestamp("sysdate");
                Logger.debug(caller, "getTimestamp: " + curTimestamp);
            }
            else{
                Logger.error(caller, "getTimestamp", "Unable to retrieve the timestamp from the database, system is using node specific timestamp");
                curTimestamp = new Timestamp(new java.util.Date().getTime());
            }
        }
        catch(SQLException sqlException){
            logToken.put("EXCEPTION", sqlException);
            Logger.error(caller, "getTimestamp", "Unable to retrieve the timestamp from the database, system is using node specific timestamp");
            curTimestamp = new Timestamp(new java.util.Date().getTime());
        }
        finally{
            closeResultSet(result);
            closeStatement(psmt);
            closeConnection(conn);
        }

        return curTimestamp;
    }


}
