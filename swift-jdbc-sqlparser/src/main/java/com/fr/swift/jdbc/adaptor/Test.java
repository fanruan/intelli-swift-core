package com.fr.swift.jdbc.adaptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fr.swift.jdbc.druid.sql.SQLUtils;
import com.fr.swift.jdbc.druid.sql.ast.SQLStatement;
import com.fr.swift.jdbc.druid.util.JdbcConstants;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.query.info.bean.query.QueryInfoBean;

import java.util.List;

/**
 * Created by lyon on 2018/12/7.
 */
public class Test {

    public static void main(String[] args) throws JsonProcessingException {
        String sql = "select a, aa, HLL_DISTINCT(b) as bb from table_a where a > 233 and aa <= 999 group by a asc, aa asc order by bb asc";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.SWIFT);

        QueryASTVisitorAdapter visitor = new QueryASTVisitorAdapter();
        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }
        QueryInfoBean bean = visitor.getQueryInfoBean();
        System.out.println(QueryBeanFactory.queryBean2String(bean));
    }
}
