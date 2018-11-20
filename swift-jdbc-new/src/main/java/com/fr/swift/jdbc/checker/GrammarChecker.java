package com.fr.swift.jdbc.checker;

import com.fr.swift.jdbc.info.SqlInfo;

import java.sql.SQLException;
import java.util.List;

/**
 * 检查语法顺便对sql进行预解析
 * @author yee
 * @date 2018/11/16
 */
public interface GrammarChecker {
    SqlInfo check(String sql) throws SQLException;

    GrammarChecker INSTANCE = new GrammarChecker() {
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
