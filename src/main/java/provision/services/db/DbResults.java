/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/db/DbResults.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.db;

import java.io.Serializable;
import java.sql.SQLWarning;

/**
 * Generic class holding results of an SQL command. For queries these are result sets, for updates
 * it's just the update count and for complex stored procedures/scripts there may be multiple result
 * sets and/or update counts.
 * 
 * @author Iulian Vlasov
 */
public interface DbResults extends Serializable {

	/**
	 * Get the SQL statement that generated the results.
	 */
	public String getSQL();

	/**
	 * Return the number of rows affected by the SQL command.
	 * 
	 * @return
	 */
	public int getRowCount();

	/**
	 * @return
	 */
	public String getWarnings();

	/**
	 * @param warnings
	 */
	public void setWarnings(SQLWarning warnings);

}