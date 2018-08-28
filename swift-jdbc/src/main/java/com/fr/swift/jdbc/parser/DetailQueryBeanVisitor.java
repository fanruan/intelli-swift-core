package com.fr.swift.jdbc.parser;

import com.fr.general.jsqlparser.statement.select.AllColumns;
import com.fr.general.jsqlparser.statement.select.AllTableColumns;
import com.fr.general.jsqlparser.statement.select.SelectExpressionItem;
import com.fr.swift.jdbc.exception.SwiftJDBCNotSupportedException;
import com.fr.swift.jdbc.exception.SwiftJDBCTableAbsentException;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.element.GroupBean;
import com.fr.swift.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.query.info.bean.type.DimensionType;
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
    private SwiftMetaDataGetter metaDataGetter;

    public DetailQueryBeanVisitor(DetailQueryInfoBean queryBean, SwiftMetaDataGetter getter) {
        super(queryBean);
        this.queryBean = queryBean;
        this.metaDataGetter = getter;
    }

    @Override
    public void visit(AllColumns allColumns) {
        SwiftMetaData metaData = metaDataGetter.get();
        if (metaData == null) {
            Crasher.crash(new SwiftJDBCTableAbsentException(queryBean.getTableName()));
        }
        queryBean.setTableName(metaData.getId());
        try {
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
