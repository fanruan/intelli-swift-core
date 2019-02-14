package com.fr.swift.result;

import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.Closable;

import java.sql.SQLException;

/**
 * @author pony
 * @date 2017/12/5
 * 为以后适配ResultSet做准备
 */
public interface SwiftResultSet extends Closable {

    int getFetchSize();

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
    boolean hasNext() throws SQLException;

    /**
     * get row
     * @return row
     * @throws SQLException 异常
     */
    Row getNextRow() throws SQLException;

    /**
     * 关闭
     *
     * @throws SQLException 异常
     */
    @Override
    void close() throws SQLException;
}