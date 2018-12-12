package com.fr.swift.jdbc.adaptor;

import com.fr.swift.jdbc.adaptor.bean.InsertionBean;
import com.fr.swift.jdbc.druid.sql.ast.SQLExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLIdentifierExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLNumericLiteralExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLTextLiteralExpr;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLExprTableSource;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLInsertStatement;
import com.fr.swift.jdbc.druid.sql.visitor.SQLASTVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyon on 2018/12/10.
 */
class InsertionASTVisitorAdapter extends SQLASTVisitorAdapter implements InsertionBeanParser {

    private InsertionBean insertionBean;

    @Override
    public boolean visit(SQLInsertStatement x) {
        if (x == null) {
            return false;
        }
        insertionBean = new InsertionBean();
        SQLExprTableSource table = x.getTableSource();
        String[] tableName = SwiftSQLUtils.getTableName(table);
        insertionBean.setTableName(tableName[0]);
        insertionBean.setSchema(tableName[1]);
        List<SQLExpr> columns = x.getColumns();
        List<String> fields = new ArrayList<String>();
        for (SQLExpr expr : columns) {
            if (expr instanceof SQLIdentifierExpr) {
                fields.add(((SQLIdentifierExpr) expr).getName());
            }
        }
        insertionBean.setFields(fields);
        List<List> rows = new ArrayList<List>();
        List<SQLInsertStatement.ValuesClause> valuesClauses = x.getValuesList();
        for (SQLInsertStatement.ValuesClause valuesClause : valuesClauses) {
            List<SQLExpr> exprList = valuesClause.getValues();
            List row = new ArrayList();
            for (SQLExpr expr : exprList) {
                if (expr instanceof SQLTextLiteralExpr) {
                    row.add(((SQLTextLiteralExpr) expr).getText());
                } else if (expr instanceof SQLNumericLiteralExpr) {
                    row.add(((SQLNumericLiteralExpr) expr).getNumber());
                }
            }
            rows.add(row);
        }
        insertionBean.setRows(rows);
        return false;
    }

    @Override
    public InsertionBean getInsertionBean() {
        return insertionBean;
    }
}
