/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/db/ref/BeanRowSetHandler.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.db.ref;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import provision.services.db.AbstractRowSet;
import provision.services.db.AbstractRowSetHandler;
import provision.util.ReflectionUtil;
import provision.util.SystemUtil;
import provision.util.beans.BeanManager;

/**
 * This is the default SQL handler implementation used by DbManager.
 * 
 * @author Iulian Vlasov
 */
public class BeanRowSetHandler<T> extends AbstractRowSetHandler<T> {

	protected Class<T> beanType;
	private BTreeNode btreeRoot;

	/**
	 * @param beanType
	 * @throws Exception
	 */
	public BeanRowSetHandler(Class<T> beanType) {
		this.beanType = beanType;
		this.useColumnNames = true;
	}

	/**
	 * @throws Exception 
	 * @see provision.services.db.AbstractRowSetHandler#createRowSet()
	 */
	protected BeanRowSet createRowSet() throws Exception {
		BeanRowSet rowSet = new BeanRowSet(this);
		
		this.btreeRoot = null;

		Map<String, PropertyDescriptor> props = BeanManager.getProperties(beanType);
		for (PropertyDescriptor pd : props.values()) {
			if (pd.getWriteMethod() == null) continue;

			String propName = pd.getName();
			try {
				int cx = 0;
				try {
					cx = rowSet.getColumnIndex(propName);
				}
				catch (IndexOutOfBoundsException e) {
					// try lowercase
					cx = rowSet.getColumnIndex(propName.toLowerCase());
				}

				BTreeNode node = new BTreeNode(pd, cx);
				if (btreeRoot == null) {
					btreeRoot = node;
				}
				else {
					btreeRoot.addNode(node);
				}
			}
			catch (IndexOutOfBoundsException e) {
				// property not in result set
				continue;
			}
		}

		if (btreeRoot == null) {
			throw new Exception("Bean is not writeable: " + beanType);
		}
		
		return rowSet;
	}

	/**
	 * @see provision.services.db.AbstractRowSetHandler#createRow(int)
	 */
	protected T createRow(int rowNum) throws Exception {
		return ReflectionUtil.newObject(beanType);
	}

	/**
	 * @see provision.services.db.AbstractRowSetHandler#retrieveRow(int)
	 */
	@Override
	protected T retrieveRow(int rowNum) throws Exception {
		try {
			T row = createRow(rowNum);
			btreeRoot.populate(row, rs);

			return row;
		}
		catch (Exception e) {
			throw new Exception("@Row:" + rowNum, e);
		}
	}

	/**
	 * @see provision.services.db.AbstractRowSetHandler#postRetrieveColumn(java.lang.Object,
	 *      java.lang.Object, int, int)
	 */
	protected void postRetrieveColumn(T row, Object column, int rowNum, int colNum) {
	}

	/**
	 * A DbRowSet with each row represented as an instance of type T.
	 */
	@SuppressWarnings("serial")
	private class BeanRowSet extends AbstractRowSet<T> {

		/**
		 * @param rsh
		 */
		public BeanRowSet(BeanRowSetHandler<T> rsh) {
			super(rsh);
		}

		/**
		 * @see provision.services.db.AbstractRowSet#getColumn(int, int)
		 */
		public Object getColumn(int rowNum, int colNum) {
			String colName = BeanManager.getAsProperty(titles[colNum]);
			return getColumn(rowNum, colName);
		}

		/**
		 * @see provision.services.db.AbstractRowSet#getColumn(int, java.lang.String)
		 */
		@Override
		public Object getColumn(int rowNum, String colName) {
			T bean = getRow(rowNum);
			try {
				return BeanManager.getProperty(bean, colName);
			}
			catch (Exception e) {
				return null;
			}
		}

		/**
		 * @see provision.services.db.AbstractRowSet#setIndexedNames()
		 */
		@Override
		protected void setIndexedNames() {
			if (ixNames == null) {
				this.ixNames = SystemUtil.createMap(colCount);

				int index = 0;
				for (String title : titles) {
					int ix = title.indexOf('_');
					if (ix == -1) {
						ixNames.put(caseSensitive ? title : title.toLowerCase(), index++);
					}
					else {
						String colName = BeanManager.getAsProperty(title);
						ixNames.put(caseSensitive ? colName : colName.toLowerCase(), index++);
					}
				}
			}
		}

		/**
		 * @see provision.services.db.AbstractRowSet#writeRow(java.lang.StringBuffer, int)
		 */
		@Override
		protected void writeRow(StringBuilder sb, int rowNum) {
			sb.append(BeanManager.beanToString(getRow(rowNum)));
		}

	}

	/**
	 * B-Tree
	 */
	private class BTreeNode {
		public BTreeNode left;
		public BTreeNode right;
		public PropertyDescriptor pd;
		public int colIndex;
		private int rowNum;

		public BTreeNode(PropertyDescriptor pd, int cx) {
			this.pd = pd;
			this.colIndex = cx; // + 1;
		}

		public void addNode(BTreeNode node) {
			int nix = node.colIndex;
			int rix = this.colIndex;

			if (nix < rix) {
				if (left == null) {
					left = node;
				}
				else {
					left.addNode(node);
				}
			}
			else if (nix > rix) {
				if (right == null) {
					right = node;
				}
				else {
					right.addNode(node);
				}
			}
		}

		public void populate(Object bean, ResultSet rs) throws Exception {
			if (left != null) {
				left.populate(bean, rs);
			}

			try {
				Object o = retrieveColumn(colIndex);

				if ((o != null) || !pd.getPropertyType().isPrimitive()) {
					BeanManager.setProperty(bean, pd, o);
				}
			}
			catch (SQLException e) {
				throw new Exception("Error reading column: @" + rowNum + "/" + (colIndex + 1), e);
			}

			if (right != null) {
				right.populate(bean, rs);
			}
		}

	}

}