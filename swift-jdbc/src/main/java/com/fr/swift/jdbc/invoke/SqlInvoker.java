package com.fr.swift.jdbc.invoke;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/8/29
 */
public interface SqlInvoker<Result> {
    /**
     * 执行SQL
     *
     * @return
     * @throws SQLException
     */
    Result invoke() throws SQLException;

    /**
     * 获取SQL类型
     * @return
     */
    Type getType();

    enum Type {
        QUERY, INSERT, UPDATE, DELETE, CREATE_TABLE
    }
}
