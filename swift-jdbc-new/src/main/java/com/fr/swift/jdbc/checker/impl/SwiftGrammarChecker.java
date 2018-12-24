package com.fr.swift.jdbc.checker.impl;

import com.fr.swift.jdbc.checker.GrammarChecker;
import com.fr.swift.jdbc.druid.sql.SQLUtils;
import com.fr.swift.jdbc.druid.sql.ast.SQLStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLSelectStatement;
import com.fr.swift.jdbc.exception.Exceptions;
import com.fr.swift.jdbc.info.SqlRequestInfo;
import com.fr.swift.jdbc.sql.SwiftPreparedStatement;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

/**
 * @author yee
 * @date 2018-12-03
 */
public class SwiftGrammarChecker implements GrammarChecker {
    @Override
    public SqlRequestInfo check(String sql, Object... paramValues) throws SQLException {
        Matcher matcher = SwiftPreparedStatement.VALUE_POS_PATTERN.matcher(sql);
        int paramCount = 0;
        while (matcher.find()) {
            paramCount++;
        }
        if (paramCount > 0) {
            sql = getRealSql(sql, Arrays.asList(paramValues), paramCount);
        }
        try {
            List<SQLStatement> list = SQLUtils.parseStatements(sql, null);
            if (list.get(0) instanceof SQLSelectStatement) {
                return new SqlRequestInfo(sql, true);
            } else {
                return new SqlRequestInfo(sql, false);
            }
        } catch (Exception e) {
            throw Exceptions.sqlIncorrect(sql, e);
        }
    }

    private String getRealSql(String sql, List values, int size) throws SQLException {
        if (values.size() != size) {
            throw Exceptions.sql(String.format("Expect parameter count is %d but get %d", size, values.size()));
        }
        if (values.contains(SwiftPreparedStatement.NullValue.INSTANCE)) {
            throw Exceptions.sql(String.format("Parameter index %d must be set.", values.indexOf(SwiftPreparedStatement.NullValue.INSTANCE) + 1));
        }
        String tmp = sql.trim();
        for (final Object value : values) {
            String valueStr = null;
            if (value instanceof String) {
                valueStr = "'" + value + "'";
            } else if (value instanceof Date) {
                valueStr = String.valueOf(((Date) value).getTime());
            } else {
                valueStr = value.toString();
            }
            tmp = tmp.replaceFirst(SwiftPreparedStatement.VALUE_POS_PATTERN.pattern(), valueStr);
        }
        return tmp;
    }
}
