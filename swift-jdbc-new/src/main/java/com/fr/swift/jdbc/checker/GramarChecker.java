package com.fr.swift.jdbc.checker;

import com.fr.swift.jdbc.info.SqlInfo;

import java.sql.SQLException;
import java.util.List;

/**
 * @author yee
 * @date 2018/11/16
 */
public interface GramarChecker {
    SqlInfo check(String sql) throws SQLException;

    GramarChecker INSTANCE = new GramarChecker() {
        @Override
        public SqlInfo check(String sql) {
            return null;
        }

        @Override
        public SqlInfo check(String sql, List paramValues) {
            return null;
        }
    };

    SqlInfo check(String sql, List paramValues) throws SQLException;
}
