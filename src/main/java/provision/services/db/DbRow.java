/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/db/DbRow.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.db;

/**
 * Generic interface to a row in a result set.
 * 
 * @author Iulian Vlasov
 */
public interface DbRow<T> {

	/**
	 * @param colNum
	 * @return
	 */
	public Object getColumn(int colNum);

	/**
	 * @param colName
	 * @return
	 */
	public Object getColumn(String colName);

	/**
	 * @return
	 */
	public int getRowNumber();

	/**
	 * @return
	 */
	public DbRowSet<T> getRowSet();

}