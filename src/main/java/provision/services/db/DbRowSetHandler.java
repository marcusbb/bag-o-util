/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/db/DbRowSetHandler.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.db;

import java.sql.ResultSet;

/**
 * Implementations of this interface are responsible for creating DbRowSet object out of an JDBC
 * ResultSet object.
 * 
 * @author Iulian Vlasov
 */
public interface DbRowSetHandler<T> {

	/**
	 * Produce a DbRowSet from the data in the specified result set.
	 * 
	 * @param rs
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public DbRowSet<T> retrieve(ResultSet rs, String sql) throws Exception;

    /**
	 * @return
	 */
	public boolean isFirstPage();

	/**
     * @return
     */
    public boolean isLastPage();

    /**
	 * Enable or disable data retrieval based on column names rather than column numbers.
	 * 
	 * @param flag
	 */
	public void setUseColumnNames(boolean flag);

	/**
	 * @param flag
	 */
	public void setIgnoreSpaces(boolean flag);

	/**
	 * Enable or disable column headings in result sets. Column titles are usefull in interactive
	 * mode and for reporting/logging.
	 * 
	 * @param flag If true, result sets will have column headings.
	 */
	public void setUseHeadings(boolean flag);

	/**
	 * Set the maximum number of rows to be processed. If the results contain more rows, they are
	 * ignored.
	 * 
	 * @param value
	 */
	public void setMaxRows(int value);

	/**
	 * Specifies the maximum number of characters that will be processed for columns holding long
	 * character strings. The data will be truncated based on this value.
	 * 
	 * @param value
	 */
	public void setMaxChars(int value);

	/**
	 * Set the first row in the result set to be processed. It enables the row set handler to
	 * implement pagination for large result sets.
	 * 
	 * @param value
	 */
	public void setFirstRow(int value);

	/**
	 * @param value
	 */
	//public void setMetaData(DatabaseMetaData value);

}