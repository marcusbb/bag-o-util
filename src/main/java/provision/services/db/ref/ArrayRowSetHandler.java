/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/db/ref/ArrayRowSetHandler.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.db.ref;

import provision.services.db.AbstractRowSet;
import provision.services.db.AbstractRowSetHandler;

/**
 * This handler crates row sets that have each row represented as array of Objects.
 * 
 * @author Iulian Vlasov
 */
public class ArrayRowSetHandler extends AbstractRowSetHandler<Object[]> {

	/**
	 * @see provision.services.db.AbstractRowSetHandler#createRowSet()
	 */
	protected ArrayRowSet createRowSet() {
		return new ArrayRowSet(this);
	}

	/**
	 * @see provision.services.db.AbstractRowSetHandler#createRow(int)
	 */
	protected Object[] createRow(int rowNum) throws Exception {
		return new Object[colCount];
	}

	/**
	 * @see provision.services.db.AbstractRowSetHandler#postRetrieveColumn(java.lang.Object, java.lang.Object, int, int)
	 */
	protected void postRetrieveColumn(Object[] row, Object column, int rowNum, int colNum) {
		row[colNum] = column;
	}

	/**
	 * Implementation of a DbRowSet, where each row holds data as an array of objects.
	 */
	@SuppressWarnings("serial")
	private class ArrayRowSet extends AbstractRowSet<Object[]> {

		/**
		 * @param rsh
		 */
		public ArrayRowSet(ArrayRowSetHandler rsh) {
			super(rsh);
		}

		/**
		 * @see provision.services.db.AbstractRowSet#getColumn(int, int)
		 */
		public Object getColumn(int rowNum, int colNum) {
			return getRow(rowNum)[colNum];
		}

	}

}