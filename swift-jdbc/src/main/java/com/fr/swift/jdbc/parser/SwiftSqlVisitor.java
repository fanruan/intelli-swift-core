package com.fr.swift.jdbc.parser;

import com.fr.general.jsqlparser.schema.Column;
import com.fr.general.jsqlparser.schema.Table;
import com.fr.general.jsqlparser.statement.Commit;
import com.fr.general.jsqlparser.statement.SetStatement;
import com.fr.general.jsqlparser.statement.StatementVisitor;
import com.fr.general.jsqlparser.statement.Statements;
import com.fr.general.jsqlparser.statement.alter.Alter;
import com.fr.general.jsqlparser.statement.create.index.CreateIndex;
import com.fr.general.jsqlparser.statement.create.table.ColumnDefinition;
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
import com.fr.swift.jdbc.bean.CreateTableBean;
import com.fr.swift.jdbc.bean.InsertBean;
import com.fr.swift.jdbc.exception.ColumnNotMatchException;
import com.fr.swift.jdbc.exception.SwiftJDBCNotSupportedException;
import com.fr.swift.jdbc.invoke.SqlInvoker;
import com.fr.swift.jdbc.invoke.impl.CreateTableInvokerImpl;
import com.fr.swift.jdbc.invoke.impl.InsertInvokerImpl;
import com.fr.swift.jdbc.invoke.impl.SelectInvokerImpl;
import com.fr.swift.jdbc.parser.insert.RowListVisitor;
import com.fr.swift.jdbc.proxy.invoke.JdbcCaller;
import com.fr.swift.jdbc.type.JdbcType;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.info.bean.query.AbstractSingleTableQueryInfoBean;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.util.Crasher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author pony
 * @date 2018/8/17
 */
public class SwiftSqlVisitor implements StatementVisitor, SqlInvokeGetter {
    private QueryBean queryBean;
    private InsertBean insertBean;
    private JdbcCaller caller;
    private SwiftDatabase schema;
    private SqlInvoker invoke;

    public SwiftSqlVisitor(SwiftDatabase schema, JdbcCaller caller) {
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
        insertBean.setTableName(QuoteUtils.trimQuote(insert.getTable().getName()));
        List<Column> columns = insert.getColumns();
        if (null != columns && !columns.isEmpty()) {
            List<String> columnNames = new ArrayList<String>();
            for (Column column : columns) {
                columnNames.add(QuoteUtils.trimQuote(column.getColumnName()));
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
        insertBean.setSchema(schema);
        invoke = new InsertInvokerImpl(insertBean, (JdbcCaller.MaintenanceJdbcCaller) caller);
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
        Table table = createTable.getTable();
        String tableName = table.getName();
        List<com.fr.swift.api.rpc.bean.Column> columns = new ArrayList<com.fr.swift.api.rpc.bean.Column>();
        List<ColumnDefinition> columnDefinitions = createTable.getColumnDefinitions();
        for (ColumnDefinition columnDefinition : columnDefinitions) {
            JdbcType type = null;
            String dataType = columnDefinition.getColDataType().getDataType();
            try {
                type = JdbcType.getType(dataType);
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(String.format("Cannot find type %s use VARCHAR. Error Message: ", dataType, e.getMessage()));
                type = JdbcType.VARCHAR;
            }
            columns.add(new com.fr.swift.api.rpc.bean.Column(columnDefinition.getColumnName(), type.getType()));
        }

        CreateTableBean bean = new CreateTableBean();
        bean.setDatabase(schema);
        bean.setTableName(tableName);
        bean.setColumns(columns);
        invoke = new CreateTableInvokerImpl(bean, (JdbcCaller.MaintenanceJdbcCaller) caller);
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
        invoke = new SelectInvokerImpl(queryBean, schema, (JdbcCaller.SelectJdbcCaller) caller);
    }

    @Override
    public void visit(Upsert upsert) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public SqlInvoker get() {
        return invoke;
    }
}
