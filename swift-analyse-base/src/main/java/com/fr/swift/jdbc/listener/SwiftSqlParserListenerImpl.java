package com.fr.swift.jdbc.listener;

import com.fr.swift.jdbc.adaptor.bean.ColumnBean;
import com.fr.swift.jdbc.adaptor.bean.CreationBean;
import com.fr.swift.jdbc.adaptor.bean.DeletionBean;
import com.fr.swift.jdbc.adaptor.bean.DropBean;
import com.fr.swift.jdbc.adaptor.bean.TruncateBean;
import com.fr.swift.jdbc.antlr4.SwiftSqlParser;
import com.fr.swift.jdbc.antlr4.SwiftSqlParserBaseListener;
import com.fr.swift.jdbc.listener.handler.SwiftSqlBeanHandler;
import com.fr.swift.jdbc.visitor.DataTypeVisitor;
import com.fr.swift.jdbc.visitor.WhereVisitor;
import com.fr.swift.util.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2019-07-18
 */
public class SwiftSqlParserListenerImpl extends SwiftSqlParserBaseListener {
    private SelectListener selectListener = new SelectListener();
    private InsertListener insertListener = new InsertListener();
    private SwiftSqlBeanHandler handler;

    public SwiftSqlParserListenerImpl(SwiftSqlBeanHandler handler) {
        this.handler = handler;
    }

    @Override
    public void enterOrderBy(SwiftSqlParser.OrderByContext ctx) {
        selectListener.enterOrderBy(ctx);
    }

    @Override
    public void exitOrderBy(SwiftSqlParser.OrderByContext ctx) {
        selectListener.exitOrderBy(ctx);
    }

    @Override
    public void enterSelect(SwiftSqlParser.SelectContext ctx) {
        selectListener.enterSelect(ctx);
    }

    @Override
    public void exitSelect(SwiftSqlParser.SelectContext ctx) {
        handler.handle(selectListener.getSelectionBean());
    }

    @Override
    public void enterInsert(SwiftSqlParser.InsertContext ctx) {
        insertListener.enterInsert(ctx);
    }

    @Override
    public void exitInsert(SwiftSqlParser.InsertContext ctx) {
        handler.handle(insertListener.getInsertionBean());
    }

    @Override
    public void enterCreateTable(SwiftSqlParser.CreateTableContext ctx) {
        CreationBean creationBean = new CreationBean();
        String tableName = ctx.name().getText();
        creationBean.setTableName(tableName);
        SwiftSqlParser.ColumnDefinitionsContext definition = ctx.columnDefinitions();
        List<SwiftSqlParser.NameContext> columnsName = definition.name();
        ArrayList<ColumnBean> fields = new ArrayList<>();
        for (int i = 0; i < columnsName.size(); i++) {
            SwiftSqlParser.ColumnDefinitionContext d = definition.columnDefinition(i);
            String columnName = columnsName.get(i).getText();
            ColumnBean column = new ColumnBean();
            column.setColumnName(columnName);
            SwiftSqlParser.DataTypeContext dataType = d.dataType();
            column.setColumnType(dataType.accept(new DataTypeVisitor()));
            fields.add(column);
        }
        creationBean.setFields(fields);
        handler.handle(creationBean);
    }

    @Override
    public void enterDropTable(SwiftSqlParser.DropTableContext ctx) {
        String tableName = ctx.name().getText();
        DropBean dropBean = new DropBean();
        dropBean.setTableName(tableName);
        handler.handle(dropBean);
    }

    @Override
    public void enterDelete(SwiftSqlParser.DeleteContext ctx) {
        String tableName = ctx.name().getText();
        DeletionBean deletionBean = new DeletionBean();
        deletionBean.setTableName(tableName);
        if (null != ctx.where) {
            deletionBean.setFilter(ctx.where.accept(new WhereVisitor()));
        }
        handler.handle(deletionBean);
    }

    @Override
    public void enterTruncate(SwiftSqlParser.TruncateContext ctx) {
        String tableName = ctx.name().getText();
        TruncateBean truncateBean = new TruncateBean(Strings.EMPTY, tableName);
        handler.handle(truncateBean);
    }

}
