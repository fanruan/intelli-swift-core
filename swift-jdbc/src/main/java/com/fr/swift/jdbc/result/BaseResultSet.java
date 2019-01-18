package com.fr.swift.jdbc.result;

import com.fr.swift.jdbc.exception.Exceptions;
import com.fr.swift.source.Row;

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
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

/**
 * @author yee
 * @date 2018-12-19
 */
public abstract class BaseResultSet implements ResultSet {

    protected Row current;

    @Override
    public boolean wasNull() {
        return false;
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        if (null == current) {
            throw Exceptions.sql("Illegal operation on empty result set.");
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
            throw Exceptions.sql("Illegal operation on empty result set.");
        }
        Object ob = current.getValue(columnIndex - 1);
        if (ob instanceof Number) {
            return (Number) ob;
        }
        return ob == null ? new Integer(0) : new BigDecimal(ob.toString());
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
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
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
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
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public Date getDate(String columnLabel) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public Time getTime(String columnLabel) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public void clearWarnings() {

    }

    @Override
    public String getCursorName() throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public Object getObject(int columnIndex) {
        return current.getValue(columnIndex - 1);
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public Reader getCharacterStream(String columnLabel) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public boolean isBeforeFirst() {
        return false;
    }

    @Override
    public boolean isAfterLast() {
        return false;
    }

    @Override
    public boolean isFirst() {
        return false;
    }

    @Override
    public boolean isLast() {
        return false;
    }

    @Override
    public void beforeFirst() {

    }

    @Override
    public void afterLast() {

    }

    @Override
    public boolean first() {
        return false;
    }

    @Override
    public boolean last() {
        return false;
    }

    @Override
    public int getRow() {
        return 0;
    }

    @Override
    public boolean absolute(int row) {
        return false;
    }

    @Override
    public boolean relative(int rows) {
        return false;
    }

    @Override
    public boolean previous() {
        return false;
    }

    @Override
    public int getFetchDirection() {
        return 0;
    }

    @Override
    public void setFetchDirection(int direction) {

    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public void setFetchSize(int rows) {

    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getConcurrency() {
        return 0;
    }

    @Override
    public boolean rowUpdated() {
        return false;
    }

    @Override
    public boolean rowInserted() {
        return false;
    }

    @Override
    public boolean rowDeleted() {
        return false;
    }

    @Override
    public void updateNull(int columnIndex) {

    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) {

    }

    @Override
    public void updateByte(int columnIndex, byte x) {

    }

    @Override
    public void updateShort(int columnIndex, short x) {

    }

    @Override
    public void updateInt(int columnIndex, int x) {

    }

    @Override
    public void updateLong(int columnIndex, long x) {

    }

    @Override
    public void updateFloat(int columnIndex, float x) {

    }

    @Override
    public void updateDouble(int columnIndex, double x) {

    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) {

    }

    @Override
    public void updateString(int columnIndex, String x) {

    }

    @Override
    public void updateBytes(int columnIndex, byte[] x) {

    }

    @Override
    public void updateDate(int columnIndex, Date x) {

    }

    @Override
    public void updateTime(int columnIndex, Time x) {

    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) {

    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, int length) {

    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, int length) {

    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, int length) {

    }

    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) {

    }

    @Override
    public void updateObject(int columnIndex, Object x) {

    }

    @Override
    public void updateNull(String columnLabel) {

    }

    @Override
    public void updateBoolean(String columnLabel, boolean x) {

    }

    @Override
    public void updateByte(String columnLabel, byte x) {

    }

    @Override
    public void updateShort(String columnLabel, short x) {

    }

    @Override
    public void updateInt(String columnLabel, int x) {

    }

    @Override
    public void updateLong(String columnLabel, long x) {

    }

    @Override
    public void updateFloat(String columnLabel, float x) {

    }

    @Override
    public void updateDouble(String columnLabel, double x) {

    }

    @Override
    public void updateBigDecimal(String columnLabel, BigDecimal x) {

    }

    @Override
    public void updateString(String columnLabel, String x) {

    }

    @Override
    public void updateBytes(String columnLabel, byte[] x) {

    }

    @Override
    public void updateDate(String columnLabel, Date x) {

    }

    @Override
    public void updateTime(String columnLabel, Time x) {

    }

    @Override
    public void updateTimestamp(String columnLabel, Timestamp x) {

    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, int length) {

    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, int length) {

    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, int length) {

    }

    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) {

    }

    @Override
    public void updateObject(String columnLabel, Object x) {

    }

    @Override
    public void insertRow() {

    }

    @Override
    public void updateRow() {

    }

    @Override
    public void deleteRow() {

    }

    @Override
    public void refreshRow() {

    }

    @Override
    public void cancelRowUpdates() {

    }

    @Override
    public void moveToInsertRow() {

    }

    @Override
    public void moveToCurrentRow() {

    }

    @Override
    public Statement getStatement() throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public Ref getRef(int columnIndex) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public Blob getBlob(int columnIndex) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public Clob getClob(int columnIndex) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public Array getArray(int columnIndex) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public Ref getRef(String columnLabel) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public Blob getBlob(String columnLabel) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public Clob getClob(String columnLabel) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public Array getArray(String columnLabel) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public URL getURL(int columnIndex) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public URL getURL(String columnLabel) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public void updateRef(int columnIndex, Ref x) {

    }

    @Override
    public void updateRef(String columnLabel, Ref x) {

    }

    @Override
    public void updateBlob(int columnIndex, Blob x) {

    }

    @Override
    public void updateBlob(String columnLabel, Blob x) {

    }

    @Override
    public void updateClob(int columnIndex, Clob x) {

    }

    @Override
    public void updateClob(String columnLabel, Clob x) {

    }

    @Override
    public void updateArray(int columnIndex, Array x) {

    }

    @Override
    public void updateArray(String columnLabel, Array x) {

    }

    @Override
    public RowId getRowId(int columnIndex) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) {

    }

    @Override
    public void updateRowId(String columnLabel, RowId x) {

    }

    @Override
    public int getHoldability() {
        return 0;
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public void updateNString(int columnIndex, String nString) {

    }

    @Override
    public void updateNString(String columnLabel, String nString) {

    }

    @Override
    public void updateNClob(int columnIndex, NClob nClob) {

    }

    @Override
    public void updateNClob(String columnLabel, NClob nClob) {

    }

    @Override
    public NClob getNClob(int columnIndex) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public NClob getNClob(String columnLabel) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) {

    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) {

    }

    @Override
    public String getNString(int columnIndex) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public String getNString(String columnLabel) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x, long length) {

    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader, long length) {

    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, long length) {

    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, long length) {

    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, long length) {

    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, long length) {

    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, long length) {

    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, long length) {

    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length) {

    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream, long length) {

    }

    @Override
    public void updateClob(int columnIndex, Reader reader, long length) {

    }

    @Override
    public void updateClob(String columnLabel, Reader reader, long length) {

    }

    @Override
    public void updateNClob(int columnIndex, Reader reader, long length) {

    }

    @Override
    public void updateNClob(String columnLabel, Reader reader, long length) {

    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x) {

    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader) {

    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x) {

    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x) {

    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x) {

    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x) {

    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x) {

    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader) {

    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream) {

    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream) {

    }

    @Override
    public void updateClob(int columnIndex, Reader reader) {

    }

    @Override
    public void updateClob(String columnLabel, Reader reader) {

    }

    @Override
    public void updateNClob(int columnIndex, Reader reader) {

    }

    @Override
    public void updateNClob(String columnLabel, Reader reader) {

    }

    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw Exceptions.sql(new UnsupportedOperationException());
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        return false;
    }
}
