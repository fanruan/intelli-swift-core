package com.fr.swift.jdbc.parser;

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
import com.fr.swift.jdbc.exception.SwiftJDBCNotSupportedException;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.util.Crasher;

/**
 * Created by pony on 2018/8/17.
 */
public class QueryBeanVisitor implements StatementVisitor,QueryBeanParser {
    private QueryBean queryBean;
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
        Crasher.crash(new SwiftJDBCNotSupportedException());
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
        SelectQueryBeanVisitor visitor = new SelectQueryBeanVisitor();
        select.getSelectBody().accept(visitor);
        this.queryBean = visitor.getQueryBean();
    }

    @Override
    public void visit(Upsert upsert) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    public QueryBean getQueryBean() {
        return queryBean;
    }
}
