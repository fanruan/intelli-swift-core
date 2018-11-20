package com.fr.swift.jdbc.checker;

import com.fr.swift.jdbc.exception.Exceptions;
import com.fr.swift.jdbc.info.SqlInfo;
import com.fr.swift.jdbc.sql.SwiftPreparedStatement;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * 检查语法顺便对sql进行预解析
 *
 * @author yee
 * @date 2018/11/16
 */
public interface GrammarChecker {
    SqlInfo check(String sql) throws SQLException;

    SqlInfo check(String sql, List paramValues) throws SQLException;

    GrammarChecker INSTANCE = new GrammarChecker() {
        @Override
        public SqlInfo check(String sql) {
            return null;
        }

        @Override
        public SqlInfo check(String sql, List paramValues) throws SQLException {
            return check(getRealSql(sql, paramValues));
        }

        private String getRealSql(String sql, List values) throws SQLException {
            if (values.contains(SwiftPreparedStatement.NullValue.INSTANCE)) {
                throw Exceptions.sql(String.format("Parameter index %d must be set.", values.indexOf(SwiftPreparedStatement.NullValue.INSTANCE) + 1));
            }
            for (final Object value : values) {
                String valueStr = null;
                if (value instanceof String) {
                    valueStr = "'" + value + "'";
                } else if (value instanceof Date) {
                    valueStr = String.valueOf(((Date) value).getTime());
                } else {
                    valueStr = value.toString();
                }
                sql = sql.replaceFirst(SwiftPreparedStatement.VALUE_POS_PATTERN.pattern(), valueStr);
            }
            return sql;
        }
    };
}
