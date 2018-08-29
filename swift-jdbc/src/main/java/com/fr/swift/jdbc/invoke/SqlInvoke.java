package com.fr.swift.jdbc.invoke;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/8/29
 */
public interface SqlInvoke<Result> {
    Result invoke() throws SQLException;

    Type getType();

    enum Type {
        QUERY, INSERT, UPDATE, DELETE, CREATE_TABLE
    }
}
