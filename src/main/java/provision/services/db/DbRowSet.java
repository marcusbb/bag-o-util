/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/db/DbRowSet.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.db;

import java.util.*;

/**
 * Generic interface to a result set.
 * 
 * @author Iulian Vlasov
 */
public interface DbRowSet<T> extends DbResults {

	/**
	 * Returns the number of columns in the result set.
	 * 
	 * @return Column count.
	 */
	public int getColumnCount();

	/**
	 * Return an array of column headings/titles.
	 * 
	 * @return
	 */
	public String[] getTitles();

	/**
	 * @param colName
	 * @return
	 */
	public int getColumnIndex(String colName);

	/**
	 * @return
	 */
	public List<T> getRows();

	/**
	 * @param rowNum
	 * @return
	 */
	public T getRow(int rowNum);

	/**
	 * @return
	 */
	public Iterator<DbRow<T>> iterator();

	/**
	 * @param rowNum
	 * @return
	 */
	public DbRow<T> getDbRow(int rowNum);

	/**
	 * @param rowNum
	 * @param colNum
	 * @return
	 */
	public Object getColumn(int rowNum, int colNum);

	/**
	 * @param rowNum
	 * @param colName
	 * @return
	 */
	public Object getColumn(int rowNum, String colName);

}