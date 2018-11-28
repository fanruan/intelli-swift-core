/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fr.swift.jdbc.druid.sql;

import com.fr.swift.jdbc.druid.DruidRuntimeException;
import com.fr.swift.jdbc.druid.sql.ast.SQLLimit;
import com.fr.swift.jdbc.druid.sql.ast.SQLSetQuantifier;
import com.fr.swift.jdbc.druid.sql.ast.SQLStatement;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLAggregateExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLAggregateOption;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLAllColumnExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLIntegerExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLNumericLiteralExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLVariantRefExpr;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLSelect;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLSelectItem;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLSelectQuery;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLSelectStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLUnionQuery;

import java.util.List;

public class PagerUtils {

    public static String count(String sql, String dbType) {
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);

        if (stmtList.size() != 1) {
            throw new IllegalArgumentException("sql not support count : " + sql);
        }

        SQLStatement stmt = stmtList.get(0);

        if (!(stmt instanceof SQLSelectStatement)) {
            throw new IllegalArgumentException("sql not support count : " + sql);
        }

        SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;
        return count(selectStmt.getSelect(), dbType);
    }

    public static String limit(String sql, String dbType, int offset, int count) {
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);

        if (stmtList.size() != 1) {
            throw new IllegalArgumentException("sql not support count : " + sql);
        }

        SQLStatement stmt = stmtList.get(0);

        if (!(stmt instanceof SQLSelectStatement)) {
            throw new IllegalArgumentException("sql not support count : " + sql);
        }

        SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;

        return limit(selectStmt.getSelect(), dbType, offset, count);
    }

    public static String limit(SQLSelect select, String dbType, int offset, int count) {
        limit(select, dbType, offset, count, false);

        return SQLUtils.toSQLString(select, dbType);
    }

    public static boolean limit(SQLSelect select, String dbType, int offset, int count, boolean check) {
        SQLSelectQuery query = select.getQuery();
        return limitQueryBlock(select, dbType, offset, count, check);
    }

    private static boolean limitQueryBlock(SQLSelect select, String dbType, int offset, int count, boolean check) {
        SQLSelectQuery query = select.getQuery();
        if (query instanceof SQLUnionQuery) {
            SQLUnionQuery union = (SQLUnionQuery) query;
            return limitUnion(union, dbType, offset, count, check);
        }

        SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) query;
        throw new UnsupportedOperationException();
    }

    private static boolean limitUnion(SQLUnionQuery queryBlock, String dbType, int offset, int count, boolean check) {
        SQLLimit limit = queryBlock.getLimit();
        if (limit != null) {
            if (offset > 0) {
                limit.setOffset(new SQLIntegerExpr(offset));
            }

            if (check && limit.getRowCount() instanceof SQLNumericLiteralExpr) {
                int rowCount = ((SQLNumericLiteralExpr) limit.getRowCount()).getNumber().intValue();
                if (rowCount <= count && offset <= 0) {
                    return false;
                }
            } else if (check && limit.getRowCount() instanceof SQLVariantRefExpr) {
                return false;
            }

            limit.setRowCount(new SQLIntegerExpr(count));
        }

        if (limit == null) {
            limit = new SQLLimit();
            if (offset > 0) {
                limit.setOffset(new SQLIntegerExpr(offset));
            }
            limit.setRowCount(new SQLIntegerExpr(count));
            queryBlock.setLimit(limit);
        }

        return true;
    }

    private static String count(SQLSelect select, String dbType) {
        if (select.getOrderBy() != null) {
            select.setOrderBy(null);
        }

        SQLSelectQuery query = select.getQuery();
        clearOrderBy(query);

        if (query instanceof SQLSelectQueryBlock) {
            SQLSelectItem countItem = createCountItem(dbType);

            SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) query;
            List<SQLSelectItem> selectList = queryBlock.getSelectList();

            if (queryBlock.getGroupBy() != null
                    && queryBlock.getGroupBy().getItems().size() > 0) {
                return createCountUseSubQuery(select, dbType);
            }

            int option = queryBlock.getDistionOption();
            if (option == SQLSetQuantifier.DISTINCT
                    && selectList.size() >= 1) {
                SQLAggregateExpr countExpr = new SQLAggregateExpr("COUNT", SQLAggregateOption.DISTINCT);
                for (int i = 0; i < selectList.size(); ++i) {
                    countExpr.addArgument(selectList.get(i).getExpr());
                }
                selectList.clear();
                queryBlock.setDistionOption(0);
                queryBlock.addSelectItem(countExpr);
            } else {
                selectList.clear();
                selectList.add(countItem);
            }
            return SQLUtils.toSQLString(select, dbType);
        } else if (query instanceof SQLUnionQuery) {
            return createCountUseSubQuery(select, dbType);
        }

        throw new IllegalStateException();
    }

    private static String createCountUseSubQuery(SQLSelect select, String dbType) {
        SQLSelectQueryBlock countSelectQuery = createQueryBlock(dbType);

        SQLSelectItem countItem = createCountItem(dbType);
        countSelectQuery.getSelectList().add(countItem);

        SQLSubqueryTableSource fromSubquery = new SQLSubqueryTableSource(select);
        fromSubquery.setAlias("ALIAS_COUNT");
        countSelectQuery.setFrom(fromSubquery);

        SQLSelect countSelect = new SQLSelect(countSelectQuery);
        SQLSelectStatement countStmt = new SQLSelectStatement(countSelect, dbType);

        return SQLUtils.toSQLString(countStmt, dbType);
    }

    private static SQLSelectQueryBlock createQueryBlock(String dbType) {
        return new SQLSelectQueryBlock();
    }

    private static SQLSelectItem createCountItem(String dbType) {
        SQLAggregateExpr countExpr = new SQLAggregateExpr("COUNT");

        countExpr.addArgument(new SQLAllColumnExpr());

        SQLSelectItem countItem = new SQLSelectItem(countExpr);
        return countItem;
    }

    private static void clearOrderBy(SQLSelectQuery query) {
        if (query instanceof SQLSelectQueryBlock) {
            SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) query;
            if (queryBlock.getOrderBy() != null) {
                queryBlock.setOrderBy(null);
            }
            return;
        }

        if (query instanceof SQLUnionQuery) {
            SQLUnionQuery union = (SQLUnionQuery) query;
            if (union.getOrderBy() != null) {
                union.setOrderBy(null);
            }
            clearOrderBy(union.getLeft());
            clearOrderBy(union.getRight());
        }
    }

    /**
     * @param sql
     * @param dbType
     * @return if not exists limit, return -1;
     */
    public static int getLimit(String sql, String dbType) {
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);

        if (stmtList.size() != 1) {
            return -1;
        }

        SQLStatement stmt = stmtList.get(0);

        if (stmt instanceof SQLSelectStatement) {
            SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;
            SQLSelectQuery query = selectStmt.getSelect().getQuery();
            if (query instanceof SQLSelectQueryBlock) {
                return -1;
            }
        }

        return -1;
    }

    public static boolean hasUnorderedLimit(String sql, String dbType) {
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        throw new DruidRuntimeException("not supported. dbType : " + dbType);
    }
}
