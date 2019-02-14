package com.fr.swift.jdbc.adaptor;

import com.fr.swift.SwiftContext;
import com.fr.swift.api.server.exception.ApiCrasher;
import com.fr.swift.api.server.response.error.ServerErrorCode;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.oper.impl.ConfigWhereImpl;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.jdbc.adaptor.bean.InsertionBean;
import com.fr.swift.jdbc.druid.sql.ast.SQLExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLIdentifierExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLNumericLiteralExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLTextLiteralExpr;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLExprTableSource;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLInsertStatement;
import com.fr.swift.jdbc.druid.sql.visitor.SQLASTVisitorAdapter;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.resultset.importing.file.impl.BaseFileLineParser;
import com.fr.swift.util.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyon on 2018/12/10.
 */
class InsertionASTVisitorAdapter extends SQLASTVisitorAdapter implements InsertionBeanParser {

    private InsertionBean insertionBean;
    private String defaultDatabase;

    public InsertionASTVisitorAdapter(String defaultDatabase) {
        this.defaultDatabase = defaultDatabase;
    }

    @Override
    public boolean visit(SQLInsertStatement x) {
        if (x == null) {
            return false;
        }
        insertionBean = new InsertionBean();
        SQLExprTableSource table = x.getTableSource();
        String[] tableName = SwiftSQLUtils.getTableName(table);
        insertionBean.setTableName(tableName[0]);
        String database = Strings.isEmpty(tableName[1]) ? defaultDatabase : tableName[1];
        insertionBean.setSchema(database);
        SwiftMetaData metaData = null;
        try {
            List<SwiftMetaData> metaDataList = SwiftContext.get().getBean(SwiftMetaDataService.class).find(
                    ConfigWhereImpl.eq(SwiftConfigConstants.MetaDataConfig.COLUMN_TABLE_NAME, tableName[0]),
                    ConfigWhereImpl.eq(SwiftConfigConstants.MetaDataConfig.COLUMN_SWIFT_SCHEMA, SwiftDatabase.fromKey(database))
            );
            if (metaDataList.isEmpty()) {
                ApiCrasher.crash(ServerErrorCode.SERVER_UNKNOWN_ERROR, String.format("Table %s is not exists", tableName[0]));
            }
            for (SwiftMetaData swiftMetaData : metaDataList) {
                if (swiftMetaData.getSwiftDatabase().equals(SwiftDatabase.fromKey(database))) {
                    metaData = swiftMetaData;
                    break;
                }
            }
            if (null == metaData) {
                ApiCrasher.crash(ServerErrorCode.SERVER_UNKNOWN_ERROR, String.format("Table %s is not exists", tableName[0]));
            }
        } catch (Exception e) {
            ApiCrasher.crash(ServerErrorCode.SERVER_UNKNOWN_ERROR, e);
        }
        List<SQLExpr> columns = x.getColumns();
        List<String> fields = new ArrayList<String>();
        for (SQLExpr expr : columns) {
            if (expr instanceof SQLIdentifierExpr) {
                fields.add(((SQLIdentifierExpr) expr).getName());
            }
        }
        if (fields.isEmpty()) {
            fields = metaData.getFieldNames();
        }
        insertionBean.setFields(fields);
        List<Row> rows = new ArrayList<Row>();
        List<SQLInsertStatement.ValuesClause> valuesClauses = x.getValuesList();
        for (SQLInsertStatement.ValuesClause valuesClause : valuesClauses) {
            List<SQLExpr> exprList = valuesClause.getValues();
            List row = new ArrayList();
            try {
                for (int i = 0; i < exprList.size(); i++) {
                    SQLExpr expr = exprList.get(i);
                    String fieldName = fields.get(i);
                    SwiftMetaDataColumn column = metaData.getColumn(fieldName);
                    ColumnTypeConstants.ClassType type = ColumnTypeUtils.getClassType(column);
                    if (expr instanceof SQLTextLiteralExpr) {
                        String col = ((SQLTextLiteralExpr) expr).getText();
                        BaseFileLineParser.addRowDataFromString(row, col, type);
                    } else if (expr instanceof SQLNumericLiteralExpr) {
                        Number number = ((SQLNumericLiteralExpr) expr).getNumber();
                        switch (type) {
                            case DOUBLE:
                                row.add(number.doubleValue());
                                break;
                            case INTEGER:
                            case LONG:
                            case DATE:
                                row.add(number.longValue());
                                break;
                            default:
                                row.add(number.toString().trim());
                                break;
                        }
                    }

                }
            } catch (Exception e) {
                ApiCrasher.crash(ServerErrorCode.SERVER_UNKNOWN_ERROR, e);
            }
            rows.add(new ListBasedRow(row));
        }
        insertionBean.setRows(rows);
        return false;
    }

    @Override
    public InsertionBean getInsertionBean() {
        return insertionBean;
    }
}
