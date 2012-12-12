/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/db/AbstractRowSetHandler.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import provision.util.ResourceUtil;
import provision.util.StringUtil;

/**
 * This is an abstract implementation for a DbRowSetHandler. Data caching is implementation specific
 * so subclasses must implement:
 * <p>
 * <li>protected abstract Object createRows() throws Exception;</li>
 * <li>protected abstract Object createRow(int rowNum) throws Exception;</li>
 * <li>protected abstract void postRetrieveColumn(Object row, Object column, int rowNum, int colNum)
 * throws Exception;</li>
 * <li>protected abstract void postRetrieveRow(Object rows, Object row, int rowNum) throws
 * Exception;</li>
 * <li>protected abstract DbRowSet createRowSet(Object rows, String sql) throws Exception;</li>
 * <li>protected abstract void retrieveEnd(Object rows) throws Exception;</li>
 * 
 * @author Iulian Vlasov
 */
public abstract class AbstractRowSetHandler<T> implements DbRowSetHandler<T> {

	private static final char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	//protected DatabaseMetaData meta;
	protected int maxRows = Integer.MAX_VALUE;
	protected int maxChars = Integer.MAX_VALUE;
	protected boolean useHeadings;
	protected boolean ignoreSpaces = true;
	protected boolean useColumnNames;
	protected int rowSize;

	protected String sql;
	protected ResultSet rs;
	protected ResultSetMetaData rsMeta;
	protected int colCount;
	protected String[] titles;
	protected int[] type;
	protected int[] width;
	protected boolean[] rightAligned;
	protected int firstRow = 1;
	protected boolean lastPage = true;
	protected boolean caseSensitive;

	/**
	 * @see provision.services.db.DbRowSetHandler#retrieve(java.sql.ResultSet,
	 *      java.lang.String)
	 */
	public DbRowSet<T> retrieve(ResultSet resultSet, String sql) throws Exception {
		this.rsMeta = null;
		this.rs = resultSet;

		int rowNum = 1 - this.firstRow;
		this.lastPage = true;

		try {
			boolean hasNext = rs.next();

			// Had to move it here or else get an SQLException
			this.sql = sql;
			this.rsMeta = rs.getMetaData();
			this.colCount = rsMeta.getColumnCount();

			retrieveStart();
			DbRowSet<T> rowSet = createRowSet();
			
			while (hasNext) {
				if (++rowNum >= 1) {
					if (rowNum <= maxRows) {
						T row = retrieveRow(rowNum);
						postRetrieveRow(rowSet, row, rowNum);
					}
					else {
						this.lastPage = false;
						break;
					}
				}
				else {
					// just skip rows
				}
				
				hasNext = rs.next();
			}

			postRetrieve(rowSet);
			return rowSet;
		}
		catch (SQLException e) {
			throw new Exception("Error processing row: " + rowNum, e);
		}
	}

	/**
	 * @see provision.services.db.DbRowSetHandler#isFirstPage()
	 */
	public boolean isFirstPage() {
		return (firstRow == 1);
	}

	/**
	 * @see provision.services.db.DbRowSetHandler#isLastPage()
	 */
	public boolean isLastPage() {
		return lastPage;
	}

	/**
	 * @see provision.services.db.DbRowSetHandler#setUseColumnNames(boolean)
	 */
	public void setUseColumnNames(boolean flag) {
		this.useColumnNames = flag;
	}

	/**
	 * @see provision.services.db.DbRowSetHandler#setIgnoreSpaces(boolean)
	 */
	public void setIgnoreSpaces(boolean flag) {
		this.ignoreSpaces = flag;
	}

	/**
	 * @see provision.services.db.DbRowSetHandler#setUseHeadings(boolean)
	 */
	public void setUseHeadings(boolean flag) {
		this.useHeadings = flag;
	}

	/**
	 * @see provision.services.db.DbRowSetHandler#setMaxRows(int)
	 */
	public void setMaxRows(int value) {
		this.maxRows = value;
	}

	/**
	 * @see provision.services.db.DbRowSetHandler#setMaxChars(int)
	 */
	public void setMaxChars(int value) {
		this.maxChars = value;
	}

	/**
	 * @see provision.services.db.DbRowSetHandler#setFirstRow(int)
	 */
	public void setFirstRow(int value) {
		this.firstRow = value;
	}

	/**
	 * @see provision.services.db.DbRowSetHandler#setMetaData(java.sql.DatabaseMetaData)
	 */
//	public void setMetaData(DatabaseMetaData value) {
//		this.meta = value;
//	}

	/**
	 * This method is invoked only once, before any rows are processed. It caches the column titles
	 * and SQL types. If headings are enabled, it also caches the display width and alignment based
	 * on the column's datatype.
	 * 
	 * @throws Exception
	 */
	protected void retrieveStart() throws Exception {
		this.titles = new String[colCount];
		this.type = new int[colCount];
		this.rowSize = 0;

		int i = 0;
		try {
			if (useHeadings) {
				this.width = new int[colCount];
				this.rightAligned = new boolean[colCount];

				for (; i < colCount; i++) {
					// titles[i] = rsMeta.getColumnName(i + 1);
					titles[i] = rsMeta.getColumnLabel(i + 1);
					type[i] = rsMeta.getColumnType(i + 1);

					int colSize = rsMeta.getColumnDisplaySize(i + 1);
					rowSize += colSize;

					if (titles[i] != null) {
						// this may be wider than the column size
						width[i] = titles[i].length();
					}
					else {
						width[i] = colSize;
					}

					rightAligned[i] = isNumericType(type[i]);
				}

			}
			else {
				for (; i < colCount; i++) {
					// titles[i] = rsMeta.getColumnName(i + 1);
					titles[i] = rsMeta.getColumnLabel(i + 1);
					type[i] = rsMeta.getColumnType(i + 1);

					int colSize = rsMeta.getColumnDisplaySize(i + 1);
					rowSize += colSize;
				}
			}
		}
		catch (SQLException e) {
			throw new Exception("Error processing col: " + (i + 1), e);
		}
	}

	/**
	 * @return
	 * @throws Exception
	 */
	protected abstract DbRowSet<T> createRowSet() throws Exception;

	/**
	 * @param rowNum
	 * @return
	 * @throws Exception
	 */
	protected abstract T createRow(int rowNum) throws Exception;

	/**
	 * This method is invoked for each row in the result set. It builds a row of data whose type is
	 * specified in subclasses. Exceptions are catched and logged. The width of each column is
	 * computed based on the widest text in the column and its heading.
	 * 
	 * @return Object the row holding column data.
	 */
	protected T retrieveRow(int rowNum) throws Exception {
		T row = createRow(rowNum);
		Object column = null;

		int colNum = 0;
		try {
			if (useHeadings) {
				for (; colNum < colCount; colNum++) {
					column = retrieveColumn(colNum);

					// displaying tabular results
					if ((column != null) && !(column instanceof File)) {
						String data = column.toString();

						int dataLen = data.length();
						if (dataLen > maxChars) {
							data = data.substring(0, maxChars);
							dataLen = maxChars;
						}

						width[colNum] = Math.max(width[colNum], dataLen);
						column = data;
					}

					postRetrieveColumn(row, column, rowNum, colNum);
				}
			}
			else {
				for (; colNum < colCount; colNum++) {
					column = retrieveColumn(colNum);

					postRetrieveColumn(row, column, rowNum, colNum);
				}
			}

			return row;
		}
		catch (IOException e) {
			throw new Exception("Error processing row: " + (rowNum + firstRow - 1) + " col: " + (colNum + 1), e);
		}
		catch (SQLException e) {
			throw new Exception("Error processing row: " + (rowNum + firstRow - 1) + " col: " + (colNum + 1), e);
		}
	}

	/**
	 * This method is invoked in order to retrieve each column in a row.
	 */
	protected Object retrieveColumn(int colIndex) throws SQLException, IOException {
		colIndex += 1;
		Object colData = null;

		try {
			int ctype = rsMeta.getColumnType(colIndex);
			if (ctype == Types.CLOB) {
				colData = readCLOBColumn(colIndex);
			}
			else if (ctype == Types.BINARY || ctype == Types.VARBINARY || ctype == Types.LONGVARBINARY) {
				colData = readBinaryColumn(colIndex);
			}
			else if (ctype == Types.DATE || ctype == Types.TIME || ctype == Types.TIMESTAMP) {
				colData = rs.getTimestamp(colIndex);
			}
			else {
				colData = readObjectColumn(colIndex);

				// patch for stupid bridge
				if (colData == null) {
					colData = rs.getString(colIndex);
				}
			}
		}
		catch (SQLException e) {
			// patch for stupid bridge
			if ((e.getSQLState() != null) || (e.getErrorCode() != 0)) throw e;
		}

		if ((colData instanceof String) && ignoreSpaces) {
			colData = ((String) colData).trim();
		}

		return colData;
	}

	/**
	 * @param row
	 * @param column
	 * @param rowNum
	 * @param colNum
	 * @throws Exception
	 */
	protected abstract void postRetrieveColumn(T row, Object column, int rowNum, int colNum) throws Exception;

	/**
	 * @param row
	 * @param rowNum
	 */
	protected void postRetrieveRow(DbRowSet<T> rowSet, T row, int rowNum) {
		rowSet.getRows().add(row);
	}

	/**
	 * @param rowSet
	 */
	protected void postRetrieve(DbRowSet<T> rowSet) {
		((ArrayList<T>) rowSet.getRows()).trimToSize();
	}

	/**
	 * @param sqlType
	 * @return
	 */
	protected boolean isNumericType(int sqlType) {
		switch (sqlType) {
		case Types.BIGINT:
		case Types.BIT:
		case Types.DECIMAL:
		case Types.DOUBLE:
		case Types.FLOAT:
		case Types.INTEGER:
		case Types.NUMERIC:
		case Types.REAL:
		case Types.SMALLINT:
		case Types.TINYINT:
			return true;

		default:
			return false;
		}
	}

	/**
	 * Default implementation reads each column as a Java object.
	 * 
	 * @param colIndex
	 * @return
	 * @throws SQLException
	 */
	protected Object readObjectColumn(int colIndex) throws SQLException {
		return rs.getObject(colIndex);
	}

	/**
	 * Reads blob/clob columns as a Java string.
	 * 
	 * @param colIndex
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	protected String readCLOBColumn(int colIndex) throws SQLException, IOException {
		InputStream is = rs.getAsciiStream(colIndex);
		if (is == null) return null;

		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String line = null;
		StringBuffer sb = new StringBuffer();
		long totalBytes = 0;

		while (((line = br.readLine()) != null) && (totalBytes < this.maxChars)) {
			sb.append(line).append(StringUtil.NEWLINE);
			totalBytes += line.length();
		}

		return sb.toString();
	}

	/**
	 * Reads binary columns as Java objects
	 * 
	 * @param colIndex
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	protected Object readBinaryColumn(int colIndex) throws SQLException, IOException {
		InputStream is = rs.getBinaryStream(colIndex);
		if (is == null) return null;

		byte[] bytes = ResourceUtil.getResourceAsBytes(is, maxChars);
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];
			int bint = (b < 0 ? (int) b + 0x100 : b);

			sb.append(HEX[bint / 16]);
			sb.append(HEX[bint % 16]);
		}

		return sb.toString();
	}

}