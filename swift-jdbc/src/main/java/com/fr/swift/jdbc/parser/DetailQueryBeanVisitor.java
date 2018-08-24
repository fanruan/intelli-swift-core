package com.fr.swift.jdbc.parser;

import com.fr.general.jsqlparser.statement.select.AllColumns;
import com.fr.general.jsqlparser.statement.select.AllTableColumns;
import com.fr.general.jsqlparser.statement.select.SelectExpressionItem;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.jdbc.exception.SwiftJDBCNotSupportedException;
import com.fr.swift.jdbc.exception.SwiftJDBCTableAbsentException;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.element.GroupBean;
import com.fr.swift.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.query.info.bean.type.DimensionType;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.Crasher;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2018/8/17.
 */
public class DetailQueryBeanVisitor extends AbstractQueryBeanVisitor{
    private DetailQueryInfoBean queryBean;

    public DetailQueryBeanVisitor(DetailQueryInfoBean queryBean) {
        super(queryBean);
        this.queryBean = queryBean;
    }

    @Override
    public void visit(AllColumns allColumns) {
        Table table = SwiftDatabase.getInstance().getTable(new SourceKey(queryBean.getTableName()));
        if (table == null){
            Crasher.crash(new SwiftJDBCTableAbsentException(queryBean.getTableName()));
        }
        try {
            SwiftMetaData metaData = table.getMeta();
            for (int i = 0; i < metaData.getColumnCount(); i++){
                addColumn(metaData.getColumnName(i + 1));
            }
        } catch (SQLException e) {
            Crasher.crash(e);
        }
    }

    @Override
    public void visit(AllTableColumns allTableColumns) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(SelectExpressionItem selectExpressionItem) {
        selectExpressionItem.getExpression().accept(this);
    }

    protected void addColumn(String columnName){
        columnName = QuoteUtils.trimQuote(columnName);
        List<String> columnNames = queryBean.getColumns();
        if (columnNames == null){
            columnNames = new ArrayList<String>();
            queryBean.setColumns(columnNames);
        }
        List<DimensionBean> dimensions = queryBean.getDimensionBeans();
        if (dimensions == null){
            dimensions = new ArrayList<DimensionBean>();
            queryBean.setDimensionBeans(dimensions);
        }
        columnNames.add(columnName);
        DimensionBean bean = new DimensionBean();
        bean.setColumn(columnName);
        bean.setDimensionType(DimensionType.DETAIL);
        GroupBean groupBean = new GroupBean();
        groupBean.setType(GroupType.NONE);
        bean.setGroupBean(groupBean);
        dimensions.add(bean);
    }

}
