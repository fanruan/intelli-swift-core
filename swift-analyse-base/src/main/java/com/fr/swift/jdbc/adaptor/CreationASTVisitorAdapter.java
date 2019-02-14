package com.fr.swift.jdbc.adaptor;

import com.fr.swift.jdbc.adaptor.bean.ColumnBean;
import com.fr.swift.jdbc.adaptor.bean.CreationBean;
import com.fr.swift.jdbc.druid.sql.ast.SQLDataType;
import com.fr.swift.jdbc.druid.sql.ast.SQLName;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLIdentifierExpr;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLColumnDefinition;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLCreateTableStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLTableElement;
import com.fr.swift.jdbc.druid.sql.visitor.SQLASTVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyon on 2018/12/10.
 */
class CreationASTVisitorAdapter extends SQLASTVisitorAdapter implements CreationBeanParser {

    private CreationBean creationBean;

    @Override
    public boolean visit(SQLCreateTableStatement x) {
        String[] table = SwiftSQLUtils.getTableName(x.getTableSource());
        creationBean = new CreationBean();
        creationBean.setTableName(table[0]);
        creationBean.setSchema(table[1]);
        List<SQLTableElement> elements = x.getTableElementList();
        List<ColumnBean> columnBeans = new ArrayList<ColumnBean>();
        for (SQLTableElement element : elements) {
            if (element instanceof SQLColumnDefinition) {
                ColumnBean columnBean = new ColumnBean();
                SQLName name = ((SQLColumnDefinition) element).getName();
                if (name instanceof SQLIdentifierExpr) {
                    columnBean.setColumnName(((SQLIdentifierExpr) name).getName());
                }
                SQLDataType dataType = ((SQLColumnDefinition) element).getDataType();
                columnBean.setColumnType(SwiftSQLUtils.getDataType(dataType.getName()));
                columnBeans.add(columnBean);
            }
        }
        creationBean.setFields(columnBeans);
        // TODO: 2018/12/11 partition by 要写parser太麻烦了
        return false;
    }

    @Override
    public CreationBean getCreationBean() {
        return creationBean;
    }
}
