package com.fr.swift.jdbc.adaptor;

import com.fr.swift.jdbc.adaptor.bean.CreationBean;
import com.fr.swift.jdbc.adaptor.bean.DeletionBean;
import com.fr.swift.jdbc.adaptor.bean.DropBean;
import com.fr.swift.jdbc.adaptor.bean.InsertionBean;
import com.fr.swift.jdbc.adaptor.bean.SelectionBean;
import com.fr.swift.jdbc.adaptor.bean.TruncateBean;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLCreateTableStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLDeleteStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLDropTableStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLExprTableSource;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLInsertStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLTruncateStatement;
import com.fr.swift.jdbc.druid.sql.visitor.SQLASTVisitorAdapter;

import java.util.List;

/**
 * Created by lyon on 2018/12/10.
 */
public class SwiftASTVisitorAdapter extends SQLASTVisitorAdapter implements SelectionBeanParser, CreationBeanParser,
        InsertionBeanParser, DeletionBeanParser, DropBeanParser, TruncateBeanParser {

    private SwiftSQLType sqlType;
    private SelectionBean queryInfoBean;
    private CreationBean creationBean;
    private InsertionBean insertionBean;
    private DeletionBean deletionBean;
    private DropBean dropBean;
    private TruncateBean truncateBean;
    private String defaultDatabase;

    public SwiftASTVisitorAdapter(String database) {
        this.defaultDatabase = database;
    }

    @Override
    public boolean visit(SQLSelectQueryBlock x) {
        sqlType = SwiftSQLType.SELECT;
        QueryASTVisitorAdapter visitor = new QueryASTVisitorAdapter();
        visitor.visit(x);
        queryInfoBean = visitor.getSelectionBean();
        return false;
    }

    @Override
    public boolean visit(SQLCreateTableStatement x) {
        sqlType = SwiftSQLType.CREATE;
        CreationASTVisitorAdapter visitor = new CreationASTVisitorAdapter();
        visitor.visit(x);
        creationBean = visitor.getCreationBean();
        return false;
    }

    @Override
    public boolean visit(SQLInsertStatement x) {
        sqlType = SwiftSQLType.INSERT;
        InsertionASTVisitorAdapter visitor = new InsertionASTVisitorAdapter(defaultDatabase);
        visitor.visit(x);
        insertionBean = visitor.getInsertionBean();
        return false;
    }

    @Override
    public boolean visit(SQLDropTableStatement x) {
        sqlType = SwiftSQLType.DROP;
        DeletionASTVisitorAdapter visitor = new DeletionASTVisitorAdapter();
        visitor.visit(x);
        dropBean = visitor.getDropBean();
        return false;
    }

    @Override
    public boolean visit(SQLDeleteStatement x) {
        sqlType = SwiftSQLType.DELETE;
        DeletionASTVisitorAdapter visitor = new DeletionASTVisitorAdapter();
        visitor.visit(x);
        deletionBean = visitor.getDeletionBean();
        return false;
    }

    @Override
    public boolean visit(SQLTruncateStatement x) {
        sqlType = SwiftSQLType.TRUNCATE;
        List<SQLExprTableSource> sources = x.getTableSources();
        String[] tableNames = SwiftSQLUtils.getTableName(sources.get(0));
        truncateBean = new TruncateBean(tableNames[1], tableNames[0]);

        return false;
    }

    @Override
    public SelectionBean getSelectionBean() {
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

    public SwiftSQLType getSqlType() {
        return sqlType;
    }

    @Override
    public TruncateBean getTruncateBean() {
        return truncateBean;
    }
}
