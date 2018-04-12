package com.fr.swift.source;

import java.sql.SQLException;

/**
 * @author pony
 * @date 2017/12/5
 * 为以后适配ResultSet做准备
 */
public interface SwiftResultSet {
    /**
     * meta
     *
     * @return meta
     * @throws SQLException 异常
     */
    SwiftMetaData getMetaData() throws SQLException;

    /**
     * if has next
     *
     * @return 是否有下一个
     * @throws SQLException 异常
     */
    boolean next() throws SQLException;

    /**
     * get row
     * @return row
     * @throws SQLException 异常
     */
    Row getRowData() throws SQLException;

    /**
     * 关闭
     *
     * @throws SQLException 异常
     */
    void close() throws SQLException;
}