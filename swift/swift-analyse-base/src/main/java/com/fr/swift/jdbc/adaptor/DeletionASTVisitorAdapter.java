package com.fr.swift.jdbc.adaptor;

import com.fr.swift.jdbc.adaptor.bean.DeletionBean;
import com.fr.swift.jdbc.adaptor.bean.DropBean;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLDeleteStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLDropTableStatement;
import com.fr.swift.jdbc.druid.sql.visitor.SQLASTVisitorAdapter;

/**
 * Created by lyon on 2018/12/10.
 */
class DeletionASTVisitorAdapter extends SQLASTVisitorAdapter implements DeletionBeanParser, DropBeanParser {

    private DeletionBean deletionBean;
    private DropBean dropBean;

    @Override
    public boolean visit(SQLDeleteStatement x) {
        String[] table = SwiftSQLUtils.getTableName(x.getExprTableSource());
        deletionBean = new DeletionBean();
        deletionBean.setTableName(table[0]);
        deletionBean.setSchema(table[1]);
        if (x.getWhere() != null) {
            WhereASTVisitorAdapter visitor = new WhereASTVisitorAdapter();
            x.getWhere().accept(visitor);
            deletionBean.setFilter(visitor.getFilterInfoBean());
        }
        return false;
    }

    @Override
    public boolean visit(SQLDropTableStatement x) {
        String[] table = SwiftSQLUtils.getTableName(x.getTableSources().get(0));
        dropBean = new DropBean();
        dropBean.setTableName(table[0]);
        dropBean.setSchema(table[1]);
        return false;
    }

    @Override
    public DeletionBean getDeletionBean() {
        return deletionBean;
    }

    @Override
    public DropBean getDropBean() {
        return dropBean;
    }
}
