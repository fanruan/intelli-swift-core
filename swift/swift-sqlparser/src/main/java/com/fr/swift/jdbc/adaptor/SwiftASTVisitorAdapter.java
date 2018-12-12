package com.fr.swift.jdbc.adaptor;

import com.fr.swift.jdbc.adaptor.bean.CreationBean;
import com.fr.swift.jdbc.adaptor.bean.DeletionBean;
import com.fr.swift.jdbc.adaptor.bean.DropBean;
import com.fr.swift.jdbc.adaptor.bean.InsertionBean;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLCreateTableStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLDropTableSpaceStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLDropTableStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLInsertStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.fr.swift.jdbc.druid.sql.visitor.SQLASTVisitorAdapter;
import com.fr.swift.query.info.bean.query.QueryInfoBean;

/**
 * Created by lyon on 2018/12/10.
 */
public class SwiftASTVisitorAdapter extends SQLASTVisitorAdapter implements QueryInfoBeanParser, CreationBeanParser,
        InsertionBeanParser, DeletionBeanParser, DropBeanParser {

    private QueryInfoBean queryInfoBean;
    private CreationBean creationBean;
    private InsertionBean insertionBean;
    private DeletionBean deletionBean;
    private DropBean dropBean;

    @Override
    public boolean visit(SQLSelectQueryBlock x) {
        QueryASTVisitorAdapter visitor = new QueryASTVisitorAdapter();
        visitor.visit(x);
        queryInfoBean = visitor.getQueryInfoBean();
        return false;
    }

    @Override
    public boolean visit(SQLCreateTableStatement x) {
        CreationASTVisitorAdapter visitor = new CreationASTVisitorAdapter();
        visitor.visit(x);
        creationBean = visitor.getCreationBean();
        return false;
    }

    @Override
    public boolean visit(SQLInsertStatement x) {
        InsertionASTVisitorAdapter visitor = new InsertionASTVisitorAdapter();
        visitor.visit(x);
        insertionBean = visitor.getInsertionBean();
        return false;
    }

    @Override
    public boolean visit(SQLDropTableStatement x) {
        DeletionASTVisitorAdapter visitor = new DeletionASTVisitorAdapter();
        visitor.visit(x);
        deletionBean = visitor.getDeletionBean();
        return false;
    }

    @Override
    public boolean visit(SQLDropTableSpaceStatement x) {
        DeletionASTVisitorAdapter visitor = new DeletionASTVisitorAdapter();
        visitor.visit(x);
        dropBean = visitor.getDropBean();
        return false;
    }

    @Override
    public QueryInfoBean getQueryInfoBean() {
        return queryInfoBean;
    }

    @Override
    public CreationBean getCreationBean() {
        return creationBean;
    }

    @Override
    public DeletionBean getDeletionBean() {
        return deletionBean;
    }

    @Override
    public DropBean getDropBean() {
        return dropBean;
    }

    @Override
    public InsertionBean getInsertionBean() {
        return insertionBean;
    }
}
