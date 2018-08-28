package com.fr.swift.jdbc.parser;

import com.fr.general.jsqlparser.schema.Column;
import com.fr.general.jsqlparser.statement.Commit;
import com.fr.general.jsqlparser.statement.SetStatement;
import com.fr.general.jsqlparser.statement.StatementVisitor;
import com.fr.general.jsqlparser.statement.Statements;
import com.fr.general.jsqlparser.statement.alter.Alter;
import com.fr.general.jsqlparser.statement.create.index.CreateIndex;
import com.fr.general.jsqlparser.statement.create.table.CreateTable;
import com.fr.general.jsqlparser.statement.create.view.AlterView;
import com.fr.general.jsqlparser.statement.create.view.CreateView;
import com.fr.general.jsqlparser.statement.delete.Delete;
import com.fr.general.jsqlparser.statement.drop.Drop;
import com.fr.general.jsqlparser.statement.execute.Execute;
import com.fr.general.jsqlparser.statement.insert.Insert;
import com.fr.general.jsqlparser.statement.merge.Merge;
import com.fr.general.jsqlparser.statement.replace.Replace;
import com.fr.general.jsqlparser.statement.select.Select;
import com.fr.general.jsqlparser.statement.truncate.Truncate;
import com.fr.general.jsqlparser.statement.update.Update;
import com.fr.general.jsqlparser.statement.upsert.Upsert;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.jdbc.bean.InsertBean;
import com.fr.swift.jdbc.exception.ColumnNotMatchException;
import com.fr.swift.jdbc.exception.SwiftJDBCNotSupportedException;
import com.fr.swift.jdbc.parser.insert.InsertBeanParser;
import com.fr.swift.jdbc.parser.insert.RowListVisitor;
import com.fr.swift.jdbc.rpc.RpcCaller;
import com.fr.swift.query.info.bean.query.AbstractSingleTableQueryInfoBean;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.util.Crasher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by pony on 2018/8/17.
 */
public class QueryBeanVisitor implements StatementVisitor, QueryBeanParser, InsertBeanParser {
    private QueryBean queryBean;
    private InsertBean insertBean;
    private RpcCaller caller;
    private SwiftDatabase schema;

    public QueryBeanVisitor(SwiftDatabase schema, RpcCaller caller) {
        this.caller = caller;
        this.schema = schema;
    }

    @Override
    public void visit(Commit commit) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(Delete delete) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(Update update) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(Insert insert) {
        insertBean = new InsertBean();
        insertBean.setTableName(insert.getTable().getName());
        List<Column> columns = insert.getColumns();
        if (null != columns && !columns.isEmpty()) {
            List<String> columnNames = new ArrayList<String>();
            for (Column column : columns) {
                columnNames.add(column.getColumnName());
            }
            insertBean.setColumnNames(columnNames);
        } else {
            insertBean.setColumnNames(Collections.<String>emptyList());
        }
        if (insert.getSelect() == null) {
            RowListVisitor visitor = new RowListVisitor();
            insert.getItemsList().accept(visitor);
            insertBean.setDatas(visitor.get());
        } else {
            visit(insert.getSelect());
            if (queryBean != null && queryBean instanceof AbstractSingleTableQueryInfoBean) {
                int size = ((AbstractSingleTableQueryInfoBean) queryBean).getDimensionBeans().size();
                int insertColumnSize = insertBean.getColumnNames().size();
                if (insertColumnSize != size) {
                    if (insertColumnSize == 0) {
                        insertBean.setQueryJson(queryBean.toString());
                    } else {
                        throw new ColumnNotMatchException(insertColumnSize, size);
                    }
                } else {
                    insertBean.setQueryJson(queryBean.toString());
                }
            }
        }

    }

    @Override
    public void visit(Replace replace) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(Drop drop) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(Truncate truncate) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(CreateIndex createIndex) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(CreateTable createTable) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(CreateView createView) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(AlterView alterView) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(Alter alter) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(Statements statements) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(Execute execute) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(SetStatement setStatement) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(Merge merge) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(Select select) {
        SelectQueryBeanVisitor visitor = new SelectQueryBeanVisitor(schema, caller);
        select.getSelectBody().accept(visitor);
        this.queryBean = visitor.getQueryBean();
    }

    @Override
    public void visit(Upsert upsert) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public QueryBean getQueryBean() {
        return queryBean;
    }

    @Override
    public InsertBean getInsertBean() {
        return insertBean;
    }
}
