package com.fr.swift.jdbc.result;

import com.fr.swift.jdbc.MetadataWrapper;
import com.fr.swift.jdbc.exception.SwiftJDBCColumnAbsentException;
import com.fr.swift.jdbc.exception.SwiftJDBCNotSupportedException;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.Util;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pony on 2018/8/17.
 */
public class ResultSetWrapper implements ResultSet {
    private SwiftResultSet resultSet;
    private Row current;
    private Map<String, Integer> label2Index;

    public ResultSetWrapper(SwiftResultSet resultSet) {
        Util.requireNonNull(resultSet);
        this.resultSet = resultSet;
        try {
            initLabel2IndexMap();
        } catch (SQLException e) {
            Crasher.crash(new SwiftJDBCNotSupportedException());
        }
    }

    public ResultSetWrapper(SwiftResultSet resultSet, Map<String, Integer> label2Index) {
        Util.requireNonNull(resultSet);
        this.resultSet = resultSet;
        this.label2Index = label2Index;
    }

    private void initLabel2IndexMap() throws SQLException {
        label2Index = new HashMap<String, Integer>();
        SwiftMetaData metaData = resultSet.getMetaData();
        if (null != metaData) {
            List<String> fieldNames = metaData.getFieldNames();
            for (String fieldName : fieldNames) {
                label2Index.put(fieldName, metaData.getColumnIndex(fieldName));
            }
        }
    }

    @Override
    public boolean next() throws SQLException {
        if (resultSet.hasNext()) {
            current = resultSet.getNextRow();
            return true;
        }
        return false;
    }

    @Override
    public void close() throws SQLException {
        resultSet.close();
    }

    @Override
    public boolean wasNull() throws SQLException {
        return false;
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        if (null == current) {
            throw new SQLException("Illegal operation on empty result set.");
        }
        Object ob = current.getValue(columnIndex - 1);
        return ob == null ? null : String.valueOf(ob);
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        return Boolean.valueOf(getString(columnIndex));
    }

    @Override
    public byte getByte(int columnIndex) throws SQLException {
        return getNumber(columnIndex).byteValue();
    }

    @Override
    public short getShort(int columnIndex) throws SQLException {
        return getNumber(columnIndex).shortValue();
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        return getNumber(columnIndex).intValue();
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
        return getNumber(columnIndex).longValue();
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {
        return getNumber(columnIndex).floatValue();
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException {
        return getNumber(columnIndex).doubleValue();
    }

    private Number getNumber(int columnIndex) throws SQLException {
        if (null == current) {
            throw new SQLException("Illegal operation on empty result set.");
        }
        Object ob = current.getValue(columnIndex - 1);
        if (ob instanceof Number) {
            return (Number) ob;
        }
        return ob == null ? 0 : new BigDecimal(ob.toString());
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public Date getDate(int columnIndex) throws SQLException {
        return new Date(getNumber(columnIndex).longValue());
    }

    @Override
    public Time getTime(int columnIndex) throws SQLException {
        return new Time(getNumber(columnIndex).longValue());
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        return new Timestamp(getNumber(columnIndex).longValue());
    }

    @Override
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public String getString(String columnLabel) throws SQLException {
        return getString(findColumn(columnLabel));
    }

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        return getBoolean(findColumn(columnLabel));
    }

    @Override
    public byte getByte(String columnLabel) throws SQLException {
        return getByte(findColumn(columnLabel));
    }

    @Override
    public short getShort(String columnLabel) throws SQLException {
        return getShort(findColumn(columnLabel));
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {
        return getInt(findColumn(columnLabel));
    }

    @Override
    public long getLong(String columnLabel) throws SQLException {
        return getLong(findColumn(columnLabel));
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException {
        return getFloat(findColumn(columnLabel));
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException {
        return getDouble(findColumn(columnLabel));
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public Date getDate(String columnLabel) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public Time getTime(String columnLabel) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void clearWarnings() throws SQLException {

    }

    @Override
    public String getCursorName() throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return new MetadataWrapper(resultSet.getMetaData());
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        return current.getValue(columnIndex - 1);
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public int findColumn(String columnLabel) throws SQLException {
        if (label2Index == null || !label2Index.containsKey(columnLabel)) {
            return Crasher.crash(new SwiftJDBCColumnAbsentException(columnLabel));
        }
        return label2Index.get(columnLabel);
    }

    @Override
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public Reader getCharacterStream(String columnLabel) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        return false;
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        return false;
    }

    @Override
    public boolean isFirst() throws SQLException {
        return false;
    }

    @Override
    public boolean isLast() throws SQLException {
        return false;
    }

    @Override
    public void beforeFirst() throws SQLException {

    }

    @Override
    public void afterLast() throws SQLException {

    }

    @Override
    public boolean first() throws SQLException {
        return false;
    }

    @Override
    public boolean last() throws SQLException {
        return false;
    }

    @Override
    public int getRow() throws SQLException {
        return 0;
    }

    @Override
    public boolean absolute(int row) throws SQLException {
        return false;
    }

    @Override
    public boolean relative(int rows) throws SQLException {
        return false;
    }

    @Override
    public boolean previous() throws SQLException {
        return false;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {

    }

    @Override
    public int getFetchDirection() throws SQLException {
        return 0;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {

    }

    @Override
    public int getFetchSize() throws SQLException {
        return 0;
    }

    @Override
    public int getType() throws SQLException {
        return 0;
    }

    @Override
    public int getConcurrency() throws SQLException {
        return 0;
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        return false;
    }

    @Override
    public boolean rowInserted() throws SQLException {
        return false;
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        return false;
    }

    @Override
    public void updateNull(int columnIndex) throws SQLException {

    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {

    }

    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException {

    }

    @Override
    public void updateShort(int columnIndex, short x) throws SQLException {

    }

    @Override
    public void updateInt(int columnIndex, int x) throws SQLException {

    }

    @Override
    public void updateLong(int columnIndex, long x) throws SQLException {

    }

    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException {

    }

    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException {

    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {

    }

    @Override
    public void updateString(int columnIndex, String x) throws SQLException {

    }

    @Override
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {

    }

    @Override
    public void updateDate(int columnIndex, Date x) throws SQLException {

    }

    @Override
    public void updateTime(int columnIndex, Time x) throws SQLException {

    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {

    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {

    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {

    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {

    }

    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {

    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {

    }

    @Override
    public void updateNull(String columnLabel) throws SQLException {

    }

    @Override
    public void updateBoolean(String columnLabel, boolean x) throws SQLException {

    }

    @Override
    public void updateByte(String columnLabel, byte x) throws SQLException {

    }

    @Override
    public void updateShort(String columnLabel, short x) throws SQLException {

    }

    @Override
    public void updateInt(String columnLabel, int x) throws SQLException {

    }

    @Override
    public void updateLong(String columnLabel, long x) throws SQLException {

    }

    @Override
    public void updateFloat(String columnLabel, float x) throws SQLException {

    }

    @Override
    public void updateDouble(String columnLabel, double x) throws SQLException {

    }

    @Override
    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {

    }

    @Override
    public void updateString(String columnLabel, String x) throws SQLException {

    }

    @Override
    public void updateBytes(String columnLabel, byte[] x) throws SQLException {

    }

    @Override
    public void updateDate(String columnLabel, Date x) throws SQLException {

    }

    @Override
    public void updateTime(String columnLabel, Time x) throws SQLException {

    }

    @Override
    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {

    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {

    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {

    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {

    }

    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {

    }

    @Override
    public void updateObject(String columnLabel, Object x) throws SQLException {

    }

    @Override
    public void insertRow() throws SQLException {

    }

    @Override
    public void updateRow() throws SQLException {

    }

    @Override
    public void deleteRow() throws SQLException {

    }

    @Override
    public void refreshRow() throws SQLException {

    }

    @Override
    public void cancelRowUpdates() throws SQLException {

    }

    @Override
    public void moveToInsertRow() throws SQLException {

    }

    @Override
    public void moveToCurrentRow() throws SQLException {

    }

    @Override
    public Statement getStatement() throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public Ref getRef(int columnIndex) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public Blob getBlob(int columnIndex) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public Clob getClob(int columnIndex) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public Array getArray(int columnIndex) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public Ref getRef(String columnLabel) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public Blob getBlob(String columnLabel) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public Clob getClob(String columnLabel) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public Array getArray(String columnLabel) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public URL getURL(int columnIndex) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public URL getURL(String columnLabel) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void updateRef(int columnIndex, Ref x) throws SQLException {

    }

    @Override
    public void updateRef(String columnLabel, Ref x) throws SQLException {

    }

    @Override
    public void updateBlob(int columnIndex, Blob x) throws SQLException {

    }

    @Override
    public void updateBlob(String columnLabel, Blob x) throws SQLException {

    }

    @Override
    public void updateClob(int columnIndex, Clob x) throws SQLException {

    }

    @Override
    public void updateClob(String columnLabel, Clob x) throws SQLException {

    }

    @Override
    public void updateArray(int columnIndex, Array x) throws SQLException {

    }

    @Override
    public void updateArray(String columnLabel, Array x) throws SQLException {

    }

    @Override
    public RowId getRowId(int columnIndex) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {

    }

    @Override
    public void updateRowId(String columnLabel, RowId x) throws SQLException {

    }

    @Override
    public int getHoldability() throws SQLException {
        return 0;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return false;
    }

    @Override
    public void updateNString(int columnIndex, String nString) throws SQLException {

    }

    @Override
    public void updateNString(String columnLabel, String nString) throws SQLException {

    }

    @Override
    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {

    }

    @Override
    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {

    }

    @Override
    public NClob getNClob(int columnIndex) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public NClob getNClob(String columnLabel) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {

    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {

    }

    @Override
    public String getNString(int columnIndex) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public String getNString(String columnLabel) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {

    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {

    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {

    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {

    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {

    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {

    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {

    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {

    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {

    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {

    }

    @Override
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {

    }

    @Override
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {

    }

    @Override
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {

    }

    @Override
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {

    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {

    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {

    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {

    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {

    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {

    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {

    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {

    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {

    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {

    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {

    }

    @Override
    public void updateClob(int columnIndex, Reader reader) throws SQLException {

    }

    @Override
    public void updateClob(String columnLabel, Reader reader) throws SQLException {

    }

    @Override
    public void updateNClob(int columnIndex, Reader reader) throws SQLException {

    }

    @Override
    public void updateNClob(String columnLabel, Reader reader) throws SQLException {

    }

    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
