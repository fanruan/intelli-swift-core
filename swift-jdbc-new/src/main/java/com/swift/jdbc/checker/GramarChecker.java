package com.swift.jdbc.checker;

import com.swift.jdbc.info.SqlInfo;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/11/16
 */
public interface GramarChecker {
    SqlInfo check(String sql) throws SQLException;
}
