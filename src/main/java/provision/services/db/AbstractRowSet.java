/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/db/AbstractRowSet.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.db;

import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import provision.util.StringUtil;
import provision.util.SystemUtil;

/**
 * Generic class holding result sets. If headings are enabled, it stores information about the
 * column headings, width and alignment for display purposes.
 * 
 * @author Iulian Vlasov
 * @param <T>
 */
@SuppressWarnings("serial")
public abstract class AbstractRowSet<T> implements DbRowSet<T> {

	protected transient AbstractRowSetHandler<T> rsh;
	protected String sql;
	protected int colCount;
	protected String[] titles;
	protected ArrayList<T> rows;
	protected boolean caseSensitive;
	protected Map<String, Integer> ixNames;
	protected int[] displayWidth;
	protected boolean[] displayAlignment;
	protected StringBuilder warnings;

	/**
	 * @param rsh
	 * @param sql
	 */
	public AbstractRowSet(AbstractRowSetHandler<T> rsh) {
		this.rsh = rsh;
		this.sql = rsh.sql;
		this.titles = rsh.titles;
		this.colCount = (titles == null ? 0 : titles.length);

		if (rsh.useColumnNames) {
			setIndexedNames();
		}
		
		if (rsh.useHeadings) {
			this.displayWidth = rsh.width;
			this.displayAlignment = rsh.rightAligned;
		}

		this.rows = new ArrayList<T>();
	}

	/**
	 * @see provision.services.db.DbRowSet#getColumnCount()
	 */
	public int getColumnCount() {
		return this.colCount;
	}

	/**
	 * @see provision.services.db.DbRowSet#getTitles()
	 */
	public String[] getTitles() {
		return titles;
	}

	/**
	 * @see provision.services.db.DbRowSet#getColumnIndex(java.lang.String)
	 */
	public int getColumnIndex(String colName) {
		if (this.ixNames == null) {
			// it makes sense to enable here column lookup by name
			setIndexedNames();
		}
	
		Integer colIndex = (caseSensitive ? ixNames.get(colName) : ixNames.get(colName.toLowerCase()));
		if (colIndex == null) {
			throw new IndexOutOfBoundsException("Invalid column name: " + colName);
		}
	
		return colIndex.intValue();
	}

	/**
	 * @see provision.services.db.DbRowSet#getRows()
	 */
	public List<T> getRows() {
		return rows;
	}

	/**
	 * @see provision.services.db.DbRowSet#getRow(int)
	 */
	public T getRow(int rowNum) {
		return rows.get(rowNum);
	}

	/**
	 * @see provision.services.db.DbRowSet#iterator()
	 */
	public Iterator<DbRow<T>> iterator() {
		return new RowIt();
	}

	/**
	 * @see provision.services.db.DbRowSet#getDbRow(int)
	 */
	public DbRow<T> getDbRow(int rowNum) {
		return new RowIt(rowNum);
	}

	/**
	 * @see provision.services.db.DbRowSet#getColumn(int, int)
	 */
	public abstract Object getColumn(int rowNum, int colNum);

	/**
	 * @see provision.services.db.DbRowSet#getColumn(int, java.lang.String)
	 */
	public Object getColumn(int rowNum, String colName) {
		int colNum = getColumnIndex(colName);
		return getColumn(rowNum, colNum);
	}

	/**
	 * @see provision.services.db.DbResults#getSQL()
	 */
	public String getSQL() {
		return sql;
	}

	/**
	 * @see provision.services.db.DbResults#getRowCount()
	 */
	public int getRowCount() {
		return (rows == null ? 0 : rows.size());
	}

	/**
	 * @return
	 */
	public String getWarnings() {
		return (warnings == null ? null : warnings.toString());
	}

	/**
	 * @param value
	 */
	public void setWarnings(SQLWarning warn) {
		if (warn != null) {
			if (warnings != null) {
				warnings.setLength(0);
			}
			else {
				warnings = new StringBuilder();
			}

			do {
				warnings.append(warn.getMessage()).append(StringUtil.NEWLINE);
				warn = warn.getNextWarning();
			} while (warn != null);
		}
		else {
			if (warnings != null) warnings = null;
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
	
		if (this.displayWidth == null) {
			sb.append(sql).append(StringUtil.NEWLINE);
		}
	
		writeHeadings(sb);
	
		int rowCount = getRowCount();
		for (int rowNum = 0; rowNum < rowCount; rowNum++) {
			writeRow(sb, rowNum);
		}
	
		return sb.toString();
	}

	/**
	 * 
	 */
	protected void setIndexedNames() {
		if (ixNames == null) {
			this.ixNames = SystemUtil.createMap(colCount);

			if (caseSensitive) {
				for (int i = 0; i < colCount; i++) {
					ixNames.put(titles[i], i);
				}
			}
			else {
				for (int i = 0; i < colCount; i++) {
					ixNames.put(titles[i].toLowerCase(), i);
				}
			}
		}
	}

	/**
	 * @param sb
	 */
	protected void writeHeadings(StringBuilder sb) {
		if (displayWidth == null) {
			for (int i = 0; i < colCount; i++) {
				String title = titles[i];
				sb.append(title);
	
				if (i < colCount - 1) sb.append(",");
			}
			sb.append(StringUtil.NEWLINE);
	
		}
		else {
			int rowCount = getRowCount();
			sb.append("Row Count: ").append(rowCount).append(StringUtil.NEWLINE);
	
			StringBuilder sbHead = new StringBuilder();
			StringBuilder sbLine = new StringBuilder();
	
			for (int i = 0; i < colCount; i++) {
				String title = titles[i];
				int headLen = title.length();
	
				int index = i;
	
				if (displayWidth[index] > headLen) {
					sbHead.append(StringUtil.padRight(title, displayWidth[index] + 1));
					sbLine.append(StringUtil.padRight("", displayWidth[index], '-') + " ");
				}
				else {
					sbHead.append(title + " ");
					sbLine.append(StringUtil.padRight("", headLen, '-') + " ");
				}
			}
	
			sb.append(sbHead).append(StringUtil.NEWLINE);
			sb.append(sbLine).append(StringUtil.NEWLINE);
		}
	}

	/**
	 * @param sb
	 * @param rowNum
	 */
	protected void writeRow(StringBuilder sb, int rowNum) {
		if (displayWidth == null) {
			for (int i = 0; i < colCount; i++) {
				sb.append(getColumn(rowNum, i));
				if (i < colCount - 1) sb.append(",");
			}
		}
		else {
			for (int i = 0; i < colCount; i++) {
				Object data = getColumn(rowNum, i);
				String value = (data == null ? "" : data.toString());
				int dataLen = value.length();

				if (displayWidth[i] > dataLen) {
					if (displayAlignment[i]) {
						sb.append(StringUtil.padLeft(value, displayWidth[i]));
					}
					else {
						sb.append(StringUtil.padRight(value, displayWidth[i]));
					}
				}
				else {
					sb.append(value);
				}
				
				sb.append(' ');
			}
		}

		sb.append(StringUtil.NEWLINE);
	}

	/**
	 *
	 */
	private class RowIt implements Iterator<DbRow<T>>, DbRow<T> {
		private int index = -1;

		RowIt() {}

		RowIt(int rowNum) {
			this.index = rowNum;
		}

		public DbRow<T> next() {
			index++;
			return this;
		}

		public boolean hasNext() {
			return (index + 1 < rows.size());
		}

		public void remove() {}

		/**
		 * @see provision.services.db.DbRow#getColumn(int)
		 */
		public Object getColumn(int colNum) {
			return AbstractRowSet.this.getColumn(index, colNum);
		}

		/**
		 * @see provision.services.db.DbRow#getColumn(java.lang.String)
		 */
		public Object getColumn(String colName) {
			return AbstractRowSet.this.getColumn(index, colName);
		}

		/**
		 * @see provision.services.db.DbRow#getRowNumber()
		 */
		public int getRowNumber() {
			return index;
		}

		/**
		 * @see provision.services.db.DbRow#getRowSet()
		 */
		public DbRowSet<T> getRowSet() {
			return AbstractRowSet.this;
		}

	}

}