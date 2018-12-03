package com.fr.swift.jdbc.checker;

import com.fr.swift.jdbc.info.SqlRequestInfo;

import java.sql.SQLException;
import java.util.List;

/**
 * 检查语法顺便对sql进行预解析
 *
 * @author yee
 * @date 2018/11/16
 */
public interface GrammarChecker {
    SqlRequestInfo check(String sql) throws SQLException;

    SqlRequestInfo check(String sql, List paramValues) throws SQLException;
}
